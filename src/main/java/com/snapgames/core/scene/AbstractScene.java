package com.snapgames.core.scene;

import com.snapgames.core.Application;
import com.snapgames.core.behavior.SceneBehavior;
import com.snapgames.core.entity.Camera;
import com.snapgames.core.entity.Entity;
import com.snapgames.core.entity.GameObject;
import com.snapgames.core.graphics.Renderer;
import com.snapgames.core.input.InputHandler;
import com.snapgames.core.math.physic.SpacePartition;
import com.snapgames.core.math.physic.World;
import com.snapgames.core.system.GSystemManager;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
    private final Map<String, Entity<? extends Entity<?>>> entities = new ConcurrentHashMap<>();
    /**
     * The list of behaviors for this scene.
     */
    private Collection<SceneBehavior> behaviors = new ArrayList<>();

    @Override
    public void addEntity(Entity<?> e) {
        entities.put(e.name, e);
        e.child.forEach(c -> entities.put(c.name, c));
    }

    public void addBehavior(SceneBehavior sb) {
        this.behaviors.add(sb);
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
    public Entity<? extends Entity> getEntity(String entityName) {
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
        SpacePartition sp = GSystemManager.find(SpacePartition.class);
        Renderer r = GSystemManager.find(Renderer.class);
        if (app.isDebugAtLeast(2)) {
            r.moveFromCameraPoV(g, getActiveCamera(), -1);
            sp.draw(r, g, this);
            r.moveFromCameraPoV(g, getActiveCamera(), 1);
        }
    }

    @Override
    public void setWorld(World world) {
        // default implementation
    }


    @Override
    public Collection<SceneBehavior> getBehaviors() {
        return behaviors;
    }
}
