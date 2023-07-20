package com.snapgames.core.math.physic;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

import com.snapgames.core.Application;
import com.snapgames.core.entity.Camera;
import com.snapgames.core.entity.Entity;
import com.snapgames.core.scene.Scene;
import com.snapgames.core.utils.config.Configuration;

/**
 * The {@link PhysicEngine} service will process mathematical moves to any Scene {@link Entity}.
 * <p>
 * Mainly some adapted Newton's laws are used to compute motions.
 * Any {@link Entity} is under the first and second Newton's laws.
 * <p>
 * Some local changes can be added to th {@link World} through a {@link com.snapgames.core.math.physic.entity.Perturbation}
 * object, bringing some force or attraction factor to be applied on a specific.
 * <p>
 * Some computation constrains on speed, acceleration and time scla factor can be defined into the configuration file:
 * <pre>
 * app.physic.constrained=true
 * app.physic.ups=120
 * app.physic.speed.max=128.0
 * app.physic.acceleration.max=48.0
 * app.physic.world=world(amazing,0.981,(1024x1024))
 * </pre>
 * <p>
 * where:
 *
 * <ul>
 *     <li><code>app.physic.constrained</code> defines if {@link Entity} is constrained to World play area,</li>
 *     <li><code>app.physic.ups</code> defines loop engine update frequency (updates-per-second),</li>
 *     <li><code>app.physic.speed.max</code> set the maximum speed for any {@link Entity} processed by the {@link PhysicEngine},</li>
 *     <li><code>app.physic.acceleration.max</code> set the maximum acceleration for any {@link Entity} processed by the {@link PhysicEngine},</li>
 *     <li><code>app.physic.world</code> defines the World object with a name, the gravity(only vertical) and the rectangle play area.</li>
 * </ul>
 *
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public class PhysicEngine {

    public transient World world;

    private final Application application;
    private double maxEntityAcc;
    private double maxEntitySpeed;
    private double timeScaleFactor = 1.00;
    private static long cumulatedTime;

    public PhysicEngine(Application app) {
        this.application = app;
        setWorld(app.getConfiguration().world);
        initialize(app.getConfiguration());
    }

    /**
     * Initialize the service according to the {@link Configuration} entries.
     *
     * @param config the {@link Configuration} object corresponding to the loaded properties file.
     */
    public void initialize(Configuration config) {
        this.maxEntityAcc = config.maxEntityAcc;
        this.maxEntitySpeed = config.maxEntitySpeed;
        this.timeScaleFactor = config.timeScaleFactor;
    }

    /**
     * Process any {@link Entity} in the {@link Scene} with the elapsed time.
     *
     * @param scene   the Scene containing the list of {@link Entity} to be processed.
     * @param elapsed the elapsed time since previous call
     * @param stats   the statistics map to be enhanced or used into the service, or to expose new statistics to other services.
     */
    public void update(Scene scene, double elapsed, Map<String, Object> stats) {
        Camera camera = scene.getActiveCamera();
        Collection<Entity<?>> entities = scene.getEntities();
        double time = (elapsed * timeScaleFactor);
        cumulatedTime += elapsed;

        // if world contains any Perturbation n, apply to all concerned entities.
        world.getPerturbations().stream().forEach(p ->
                entities.stream().filter(e -> e.isActive() && p.isEntityConstrained(e))
                        .forEach(e -> {
                            e.addForces(p.getForces());
                        })
        );

        entities.stream()
                .filter(Entity::isActive)
                .sorted(Comparator.comparingInt(a -> a.physicType.ordinal()))
                .forEach(
                        e -> {
                            if (e.physicType != PhysicType.STATIC && !e.stickToCamera) {
                                updateEntity(e, time);
                            }
                            e.update(time * 100);
                            // apply Behaviors
                            if (e.behaviors.size() > 0) {
                                e.behaviors.forEach(b -> b.update(e, elapsed));
                            }
                        });
        if (Optional.ofNullable(camera).isPresent()) {
            camera.update(time);
        }
        scene.update(application, time);
        long renderedEntities = entities.stream()
                .filter(Entity::isActive)
                .filter(e -> (camera != null && camera.inViewport(e)) || e.stickToCamera).count();
        stats.put("3_rendered", renderedEntities);

    }

    /**
     * Process the {@link Entity} with the basic 2 first Newton's laws.
     *
     * @param entity  the concerned {@link Entity}
     * @param elapsed the elapsed time since previous call.
     */
    private void updateEntity(Entity<?> entity, double elapsed) {
        // save previous entity position.
        entity.setOldPosition(entity.pos);
        // apply gravity
        if (!entity.physicType.equals(PhysicType.NONE) || !entity.stickToCamera) {
            entity.forces.add(world.getGravity());
        }
        // compute acceleration
        entity.setAcceleration(entity.acceleration.addAll(entity.getForces()));
        entity.setAcceleration(entity.acceleration.multiply(
                entity.mass * (entity.getMaterial() != null ? entity.getMaterial().getDensity() : 1.0)));
        if (application.getConfiguration().physicConstrained) {
            entity.acceleration = entity.acceleration.maximize(entity.getAttribute("maxAccelY", this.maxEntityAcc));
        }
        // compute velocity
        double roughness = 1.0;
        if (entity.contact > 0) {
            roughness = entity.getMaterial().getRoughness();
        } else {
            roughness = world.getMaterial().getRoughness();
        }
        entity.setSpeed(entity.vel.add(entity.acceleration.multiply(elapsed * elapsed * 0.5)).multiply(roughness));
        if (application.getConfiguration().physicConstrained) {
            entity.vel = entity.vel.maximize(entity.getAttribute("maxVelX", this.maxEntitySpeed));
        }

        // compute position
        entity.pos = entity.pos.add(entity.vel.multiply(elapsed));

        // update child entities
        entity.getChild().forEach(c -> updateEntity(c, elapsed));
        entity.forces.clear();

        // set natural BoundingBox coordinates
        entity.x = entity.pos.x;
        entity.y = entity.pos.y;

        // reset contact value
        entity.setContact(0);
        // test Entity against play area limits.
        constrainPlayArea(entity);
    }

    /**
     * Apply play area constrains to the concerned {@link Entity}.
     *
     * @param entity the {@link Entity} to be keep inside the play area.
     */
    private void constrainPlayArea(Entity<? extends Entity<?>> entity) {

        if (!entity.constrainedToPlayArea)
            return;
        if (entity.x < 0) {
            entity.setPosition(0, entity.pos.y);

            entity.setSpeed(entity.vel.x * -entity.getMaterial().getElasticity(), entity.vel.y);
            entity.contact += 1;
        }
        if (entity.x + entity.width > world.getPlayArea().getWidth()) {
            entity.setPosition(world.getPlayArea().getWidth() - entity.width, entity.pos.y);
            entity.setSpeed(entity.vel.x * -entity.getMaterial().getElasticity(), entity.vel.y);
            entity.contact += 2;
        }
        if (entity.y < 0) {
            entity.setPosition(entity.pos.x, 0);

            entity.setSpeed(entity.vel.x, entity.vel.y * -entity.getMaterial().getElasticity());
            entity.contact += 4;
        }
        if (entity.y + entity.height > world.getPlayArea().getHeight()) {
            entity.setPosition(entity.pos.x, world.getPlayArea().getHeight() - entity.height);

            entity.setSpeed(entity.vel.x, entity.vel.y * -entity.getMaterial().getElasticity());
            entity.contact += 8;
        }
        entity.x = entity.pos.x;
        entity.y = entity.pos.y;
    }

    /**
     * Define {@link World} object to be used during {@link Entity} processing.
     *
     * @param world the new {@link World} object instance to be used.
     */
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * retrieve the current World object instance used for {@link Entity} processing.
     *
     * @return the World instance used into {@link Entity} processing.
     */
    public World getWorld() {
        return this.world;
    }

    /**
     * Release possible captured resource.
     */
    public void dispose() {

    }
}
