package com.snapgames.demo.math.physic;

import com.snapgames.demo.Application;
import com.snapgames.demo.utils.config.Configuration;
import com.snapgames.demo.entity.Camera;
import com.snapgames.demo.entity.Entity;
import com.snapgames.demo.scene.Scene;

import java.util.*;

/**
 * The PhysicEngince service will process mathematical moves to all Scene entities.
 */
public class PhysicEngine {

    public transient World world;

    private final Application application;
    private double maxEntityAcc;
    private double maxEntitySpeed;

    public PhysicEngine(Application app) {
        this.application = app;
        setWorld(app.getConfiguration().world);
    }

    public void initialize(Configuration config) {
        this.maxEntityAcc = config.maxEntityAcc;
        this.maxEntitySpeed = config.maxEntitySpeed;
    }

    public void update(Scene scene, double elapsed, Map<String, Object> stats) {
        Camera camera = scene.getActiveCamera();
        Collection<Entity<?>> entities = scene.getEntities();
        double time = (elapsed * 0.000001);

        entities.stream().filter(Entity::isActive)
                .sorted(Comparator.comparingInt(a -> a.physicType))
                .forEach(
                        e -> {
                            updateEntity(e, time);
                        });
        if (Optional.ofNullable(camera).isPresent()) {
            camera.update(time);
        }
        long renderedEntities = entities.stream()
                .filter(Entity::isActive)
                .filter(e -> camera.inViewport(e) || e.physicType == Entity.NONE).count();
        stats.put("5_rend", renderedEntities);
        stats.put("5_time", time);
    }

    private void updateEntity(Entity<?> entity, double elapsed) {
        Vector2D gravity = Vector2D.ZERO();
        // apply gravity
        if (entity.physicType != Entity.NONE || !entity.stickToCamera) {
            gravity = world.getGravity();
        }
        entity.forces.add(gravity);
        // compute acceleration
        entity.acceleration = entity.acceleration.addAll(entity.getForces());
        entity.acceleration = entity.acceleration.multiply(
                entity.mass * (entity.getMaterial() != null ? entity.getMaterial().getDensity() : 1.0));
        entity.acceleration.maximize((double) entity.getAttribute("maxAccelY", maxEntityAcc))
                .thresholdToZero(0.01);

        // compute velocity
        double roughness = 1.0;
        if (entity.contact > 0) {
            roughness = entity.getMaterial().getRoughness();
        } else {
            roughness = world.getMaterial().getRoughness();
        }
        entity.vel = entity.vel.add(entity.acceleration.multiply(elapsed * elapsed * 0.5)).multiply(roughness);
        entity.vel.maximize(
                        (double) entity.getAttribute("maxVelX", maxEntitySpeed),
                        (double) entity.getAttribute("maxVelY", maxEntitySpeed))
                .thresholdToZero(0.8);

        // compute position
        entity.pos = entity.pos.add(entity.vel.multiply(elapsed));
        entity.getChild().forEach(c -> updateEntity(c, elapsed));
        entity.forces.clear();

        if (entity.behaviors.size() > 0) {
            entity.behaviors.forEach(b -> b.update(entity, elapsed));
        }

        entity.x = entity.pos.x;
        entity.y = entity.pos.y;
        entity.setContact(0);
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
