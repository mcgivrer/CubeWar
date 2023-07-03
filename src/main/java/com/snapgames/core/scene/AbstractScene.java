package com.snapgames.core.scene;

import com.snapgames.core.entity.Camera;
import com.snapgames.core.entity.Entity;
import com.snapgames.core.entity.GameObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractScene implements Scene {


    private Camera activeCamera;
    private final Map<String, Entity<? extends Entity<?>>> entities = new HashMap<>();


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

    public void dispose() {

    }

    public Collection<Entity<?>> getEntities() {
        return entities.values();
    }

    @Override
    public Camera getActiveCamera() {
        return this.activeCamera;
    }

    public Entity<?> getEntity(String entityName) {
        return entities.get(entityName);
    }

    public void clearScene() {
        entities.clear();
    }
}
