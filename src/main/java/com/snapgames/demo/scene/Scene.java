package com.snapgames.demo.scene;

import com.snapgames.demo.Application;
import com.snapgames.demo.entity.Camera;
import com.snapgames.demo.entity.Entity;
import com.snapgames.demo.input.InputHandler;
import com.snapgames.demo.math.physic.World;

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
