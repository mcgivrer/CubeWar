package com.snapgames.core.scene;

import com.snapgames.core.Application;
import com.snapgames.core.entity.Camera;
import com.snapgames.core.entity.Entity;
import com.snapgames.core.entity.GameObject;
import com.snapgames.core.input.InputHandler;
import com.snapgames.core.math.physic.World;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@link AbstractScene} delivers the default implementation and all supportive operation to
 * manage {@link Scene}.
 *
 * @author Frédéric Delorme
 * @wince 1.0.0
 */
public abstract class AbstractScene implements Scene {

    /**
     * the internal current active {@link Camera} if it exists.
     */
    private Camera activeCamera;
    /**
     * the map of entities maintained for this Scene implementation.
     */
    private final Map<String, Entity<? extends Entity<?>>> entities = new HashMap<>();

    @Override
    public void addEntity(Entity<?> e) {
        entities.put(e.name, e);
        e.child.forEach(c -> entities.put(c.name, c));
    }


    public void addEntities(List<GameObject> listEntities) {
        for (Entity<? extends Entity<?>> e : listEntities) {
            addEntity(e);
        }
    }

    public void addCamera(Camera cam) {
        this.activeCamera = cam;
    }

    @Override
    public void dispose() {
        entities.clear();
    }

    @Override
    public Collection<Entity<?>> getEntities() {
        return entities.values();
    }

    @Override
    public Camera getActiveCamera() {
        return this.activeCamera;
    }

    @Override
    public Entity<?> getEntity(String entityName) {
        return entities.get(entityName);
    }

    public void clearScene() {
        entities.clear();
    }

    @Override
    public void input(Application app, InputHandler ih) {
        // default implementation
    }

    @Override
    public void update(Application app, double elapsed) {
        // default implementation
    }

    @Override
    public void draw(Application app, Graphics2D g, Map<String, Object> stats) {
        // default implementation
    }

    @Override
    public void setWorld(World world) {
        // default implementation
    }
}
