package com.snapgames.core.math.physic;

import com.snapgames.core.Application;
import com.snapgames.core.behavior.CollisionResponseBehavior;
import com.snapgames.core.entity.Entity;
import com.snapgames.core.scene.Scene;
import com.snapgames.core.system.GSystem;
import com.snapgames.core.system.GSystemManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Manage {@link CollisionDetection} between Scene {@link Entity}'s.
 * <p>
 * On any collision detected, a {@link CollisionEvent} is generated, and then a {@link CollisionResponseBehavior}
 * is called.
 *
 * @author Frédéric Delorme
 * @since 1.0.4
 */
public class CollisionDetection implements GSystem {

    /**
     * The parent applicaiont.
     */
    Application application;

    /**
     * A list of {@link CollisionEvent}.
     */
    private ArrayList<CollisionEvent> collisions;

    /**
     * Space partitioning system to manage collision faster.
     */
    private SpacePartition spacePartition;

    /**
     * Create the CollisionDetection system.
     *
     * @param app tha parent {@link Application} instance.
     */
    public CollisionDetection(Application app) {
        this.application = app;
    }

    /**
     * Return the {@link GSystem} name.
     *
     * @return the Class name fo this {@link GSystem}.
     */
    @Override
    public Class<? extends GSystem> getSystemName() {
        return CollisionDetection.class;
    }

    /**
     * Initialize the {@link GSystem}.
     *
     * @param app parent {@link Application}.
     */
    @Override
    public void initialize(Application app) {
        collisions = new ArrayList<>();
    }


    /**
     * Process all the Scene entities to detect and process collision.
     *
     * @param scene    The {@link Scene} to be processed
     * @param elapsed  the elapsed time since previous call.
     * @param metadata a map of metadata, mainly used for debug purpose.
     */
    public void update(Scene scene, double elapsed, Map<String, Object> metadata) {
        spacePartition = GSystemManager.find(SpacePartition.class);
        // TODO use the space partition instance in the parsing
        scene.getEntities().stream().filter(e1 -> e1.isEnabled() && e1.physicType.equals(PhysicType.DYNAMIC))
            .forEach(e1 -> {
                List<Entity<?>> neighbours = spacePartition.find(e1);
                if (neighbours != null && neighbours.size() > 0) {
                    neighbours.stream()
                        .filter(e2 -> e2.isEnabled() && e2.physicType.equals(PhysicType.DYNAMIC)
                            && !e1.equals(e2))
                        .forEach(e2 -> {
                            if (e1.intersects(e2)) {
                                collide(e2, e1);
                            }
                        });
                }
            });
        metadata.put("5_colliders", getCount());
    }

    /**
     * Create the CollisionEvent fo this detected collision.
     *
     * @param e2 the targeted entity by the collision
     * @param e1 the source of the collision.
     */
    private void collide(Entity<?> e1, Entity<?> e2) {
        CollisionEvent ce = new CollisionEvent(e1, e2);
        if (application.isDebugAtLeast(6) && (e1.getName().equals("player") || e2.getName().equals("player"))) {
            System.out.printf(">> <?> collision between %s and %s%n", e1.getName(), e2.getName());
        }
        collisions.add(ce);
        collisionResponse(ce);
    }

    /**
     * Call the response {@link CollisionResponseBehavior}'s {@link CollisionEvent}
     *
     * @param ce the {@link CollisionEvent} to be processed.
     */
    private void collisionResponse(CollisionEvent ce) {
        ce.getEntity1().behaviors
            .forEach(
                crb -> {
                    try {
                        if (((CollisionResponseBehavior) crb).filter(ce)) {
                            ((CollisionResponseBehavior) crb).response(ce);
                        }
                    } catch (Exception e) {
                        // nothing
                    }
                });
    }

    public List<CollisionEvent> getCollisions() {
        return this.collisions;
    }

    public int getCount() {
        return collisions.size();
    }

    public void reset() {
        collisions.clear();
    }

    @Override
    public void dispose() {
        collisions.clear();
    }
}
