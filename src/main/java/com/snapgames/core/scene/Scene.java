package com.snapgames.core.scene;

import com.snapgames.core.Application;
import com.snapgames.core.entity.Camera;
import com.snapgames.core.entity.Entity;
import com.snapgames.core.input.InputHandler;
import com.snapgames.core.math.physic.World;

import java.awt.*;
import java.util.Collection;
import java.util.Map;

/**
 * A {@link Scene} is a state of the game.
 * <p>
 * The title screen, the main game menu at start, the map display state are all different Scene for the {@link Application}.
 * Globally maintained by the {@link SceneManager}, respecting to the following lifecycle:
 * <ul>
 *     <li><code>create</code> to create all the required entity for this scene,</li>
 *     <li><code>input</code> to manage input for this scene (key, mouse, anything input data,</li>
 *     <li><code>update</code> update all the scene state data to maintain gameplay,</li>
 *     <li><code>draw</code> upon the default framework capabilities, here is the opportunity to add to the
 *     default drawing process,</li>
 *     <li><code>dispose</code> release all the loaded resources for this state.</li>
 * </ul>
 *
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public interface Scene {
    /**
     * return the Name for this {@link Scene}. It must be unique across all scene loaded in to the Application instance..
     *
     * @return the String of the name for this {@link Scene}.
     */
    String getName();

    /**
     * Create any Entity to participate in the {@link Scene} gameplay.
     *
     * @param app the parent {@link Application} instance.
     */
    void create(Application app);

    /**
     * Manage the specific input for this {@link Scene}.
     *
     * @param app the parent {@link Application} instance.
     * @param ih  the {@link InputHandler} instance to get input states and interact with.
     */
    void input(Application app, InputHandler ih);

    /**
     * Update all entities into this Scene for global gameplay support.
     *
     * @param app     the parent {@link Application} instance.
     * @param elapsed the elapsed time since previous call.
     */
    void update(Application app, double elapsed);

    /**
     * If default framework processing does not meet your own requirement,
     * you can extend the drawing process by your own code here.
     *
     * @param app   the parent {@link Application} instance.
     * @param g     the default Graphics2D instance to interact with the drawing API.
     * @param stats map of stats to collect or add new internal KPI.
     */
    void draw(Application app, Graphics2D g, Map<String, Object> stats);

    /**
     * Release all loaded resources for this scene. you can implement specific Processing before
     * exiting the scene.
     */
    void dispose();

    /**
     * Add an {@link Entity} to the {@link Scene}.
     *
     * @param entity the {@link Entity} instance to be added to the scene management.
     */
    void addEntity(Entity<?> entity);

    /**
     * If default {@link World} instance must be adapted to match your gameplay requirement.
     *
     * @param world the default World to be updated to match your specific needs.
     */
    void setWorld(World world);

    /**
     * Retrieve the current active {@link Camera}. The default implementation will deliver the current {@link Camera}.
     *
     * @return the instance of the current {@link Camera}.
     */
    Camera getActiveCamera();

    /**
     * Retrieve all entities acting in this {@link Scene} instance.
     *
     * @return
     */
    Collection<Entity<?>> getEntities();

    /**
     * retrieve an {@link Entity} instance on its name.
     *
     * @param entityName the name fo the {@link Entity} to retrieve.
     * @return the name corresponding {@link Entity} instance
     */
    Entity<?> getEntity(String entityName);

    /**
     * Clear all entities from the scene.
     */
    void clearScene();
}
