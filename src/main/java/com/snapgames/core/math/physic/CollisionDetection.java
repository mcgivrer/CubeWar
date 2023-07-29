package com.snapgames.core.math.physic;

import com.snapgames.core.Application;
import com.snapgames.core.behavior.CollisionResponseBehavior;
import com.snapgames.core.entity.Entity;
import com.snapgames.core.scene.Scene;
import com.snapgames.core.scene.SceneManager;
import com.snapgames.core.system.GSystem;
import com.snapgames.core.system.GSystemManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CollisionDetection implements GSystem {

    private ArrayList<CollisionEvent> collisions;

    public CollisionDetection(Application app) {

    }

    @Override
    public Class<? extends GSystem> getSystemName() {
        return CollisionDetection.class;
    }

    @Override
    public void initialize(Application app) {
        collisions = new ArrayList<>();
    }


    public void update(Scene scene, double elapsed, Map<String, Object> datastats) {

        scene.getEntities().stream().filter(e1 -> e1.isActive() && e1.physicType.equals(PhysicType.DYNAMIC))
            .forEach(e1 -> {
                scene.getEntities().stream()
                    .filter(e2 -> e2.isActive() && e2.physicType.equals(PhysicType.DYNAMIC)
                        && !e1.equals(e2))
                    .forEach(e2 -> {
                        if (e1.intersects(e2)) {
                            collide(e2, e1);
                        }
                    });
            });
        datastats.put("5_col", getCount());
    }

    private void collide(Entity<?> e2, Entity<?> e1) {
        CollisionEvent ce = new CollisionEvent(e1, e2);
        System.out.printf(">> <?> collision between %s and %s%n", e2.getName(), e1.getName());
        collisions.add(ce);
        collisionResponse(ce);
    }

    private void collisionResponse(CollisionEvent ce) {
        ce.getEntity1().behaviors.stream()
            .filter(b -> b.getClass().isAssignableFrom(CollisionResponseBehavior.class))
            .forEach(
                crb -> ((CollisionResponseBehavior) crb).response(ce));
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

    }
}
