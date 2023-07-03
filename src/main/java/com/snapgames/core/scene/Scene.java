package com.snapgames.core.scene;

import com.snapgames.core.Application;
import com.snapgames.core.entity.Camera;
import com.snapgames.core.entity.Entity;
import com.snapgames.core.input.InputHandler;
import com.snapgames.core.math.physic.World;

import java.awt.*;
import java.util.Collection;
import java.util.Map;

public interface Scene {
    String getName();

    void clearScene();

    void create(Application app);

    void input(Application app, InputHandler ih);

    void update(Application app, double elapsed);

    void draw(Application app, Graphics2D g, Map<String, Object> stats);

    void dispose();

    void addEntity(Entity<?> entity);

    void setWorld(World world);

    Camera getActiveCamera();

    Collection<Entity<?>> getEntities();

    Entity<?> getEntity(String entityName);
}
