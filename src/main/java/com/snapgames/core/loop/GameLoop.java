package com.snapgames.core.loop;

import com.snapgames.core.Application;
import com.snapgames.core.input.InputHandler;
import com.snapgames.core.scene.Scene;

import java.util.Map;

/**
 * The {@link GameLoop} interface let us implement the game loop management.
 * it must call the following methods :
 * <ul>
 *     <li>{@link GameLoop#input(Application, Scene)},</li>
 *     <li>the {@link Application#update},</li>
 *     <li>{@link GameLoop#update(Application, Scene, long, Map)},</li>
 *     <li>{@link Application#draw(Scene, Map)}</li>
 * </ul>
 *
 * @author Frédéric Delorme
 * @since 1.0.5
 */
public interface GameLoop {

    /**
     * The main loop implementation calling the input, update dans draw GameLoop interface methods..
     *
     * @param app the parent {@link Application}.
     */
    void loop(Application app);

    /**
     * Manage the {@link Application} input events for the {@link Scene}.
     *
     * @param app   the parent {@link Application}.
     * @param scene the {@link Scene} to be processed.
     */
    void input(Application app, Scene scene);

    /**
     * Call te draw operation for the {@link Scene} of the parent {@link Application}.
     *
     * @param app   the parent {@link Application}.
     * @param scene the {@link Scene} to be processed.
     * @param stats some stats that can be feed through other GameLoop operation or from the loop itself.
     */
    void draw(Application app, Scene scene, Map<String, Object> stats);

    /**
     * Manage the update operation for the {@link Scene} of the {@link Application}.
     *
     * @param app     the parent {@link Application}.
     * @param scene   the {@link Scene} to be processed.
     * @param elapsed the elapsed time since previous call, computed into the main loop method.
     * @param stats   some stats that can be feed through other GameLoop operation or from the loop itself.
     */
    void update(Application app, Scene scene, long elapsed, Map<String, Object> stats);
}
