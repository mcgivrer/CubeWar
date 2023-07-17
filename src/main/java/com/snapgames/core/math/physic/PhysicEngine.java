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

    public void initialize(Configuration config) {
        this.maxEntityAcc = config.maxEntityAcc;
        this.maxEntitySpeed = config.maxEntitySpeed;
        this.timeScaleFactor = config.timeScaleFactor;
    }

    public void update(Scene scene, double elapsed, Map<String, Object> stats) {
        Camera camera = scene.getActiveCamera();
        Collection<Entity<?>> entities = scene.getEntities();
        double time = (elapsed * timeScaleFactor);
        cumulatedTime += elapsed;
        if (!application.isExiting()) {

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
                            });
            if (Optional.ofNullable(camera).isPresent()) {
                camera.update(time);
            }
            scene.update(application, time);
            long renderedEntities = entities.stream()
                    .filter(Entity::isActive)
                    .filter(e -> camera.inViewport(e) || e.stickToCamera).count();
            stats.put("3_rendered", renderedEntities);
        }

    }

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

        // apply Behaviors
        if (entity.behaviors.size() > 0) {
            entity.behaviors.forEach(b -> b.update(entity, elapsed));
        }

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

    public void setWorld(World world) {
        this.world = world;
    }

    public World getWorld() {
        return this.world;
    }

    public void dispose() {

    }
}
