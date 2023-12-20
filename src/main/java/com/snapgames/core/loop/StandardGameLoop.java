package com.snapgames.core.loop;

import com.snapgames.core.Application;
import com.snapgames.core.math.physic.PhysicType;
import com.snapgames.core.scene.Scene;
import com.snapgames.core.scene.SceneManager;
import com.snapgames.core.system.GSystemManager;
import com.snapgames.core.utils.StringUtils;
import com.snapgames.core.utils.config.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * The Standard implementation of the GameLoop interface to satisfy.
 *
 * @author Frédéric Delorme
 * @see GameLoop
 * @since 1.0.5
 */
public class StandardGameLoop implements GameLoop {

    private final int fps;
    private final int ups;

    /**
     * Initialize the standard game loop.
     *
     * @param config the Application configuration to get values from.
     */
    public StandardGameLoop(Configuration config) {

        // retrieve Frame-Per-Second
        this.fps = config.fps;
        // retrieve Update-Per-Second
        this.ups = config.ups;
    }

    @Override
    public void loop(Application app) {

        Scene scene = ((SceneManager) GSystemManager.find(SceneManager.class)).getCurrent();

        System.out.printf(
            ">> <!> Activate Scene '%s'(%s).%n",
            scene.getName(), scene.getClass().getName());


        scene.create(app);
        traceStats(scene);

        System.out.printf(
            ">> <!> Application now loops on Scene '%s'%n", scene.getName());
        long start = System.nanoTime();
        long previous = start;
        long elapsedTime = 0;
        int fpsTime = 0;
        int realFPS = 0;
        int realUPS = 0;
        int frames = 0;
        int updates = 0;
        int wait = 0;
        long cumulatedGameTime = 0;
        long upsTime = 0;
        Map<String, Object> stats = new HashMap<>();
        do {
            scene = ((SceneManager) GSystemManager.find(SceneManager.class)).getCurrent();
            start = System.nanoTime();
            long elapsed = start - previous;

            double time = elapsed * 0.00001;

            input(app, scene);
            if (!app.isPaused()) {
                if (upsTime > (1000.0 / this.ups)) {
                    update(app, scene, elapsed, stats);
                    updates++;
                    upsTime = 0;
                }

                upsTime += time;
                cumulatedGameTime += time;
            }
            if (fpsTime > (1000.0 / this.fps)) {
                draw(app, scene, stats);
                frames++;
                fpsTime = 0;
            }

            fpsTime += time;
            previous = start;
            elapsedTime += time;
            if (elapsedTime > 1000) {
                realFPS = frames;
                realUPS = updates;
                traceStatsCycle(app, scene, realFPS, realUPS, stats);
                elapsedTime = 0;
                frames = 0;
                updates = 0;
            }
            waitNextCycle(app, elapsed);

            stats.put("5_internal", StringUtils.formatDuration(cumulatedGameTime));
        } while (!(app.isExiting() || app.isTestMode()));
    }

    public void input(Application app, Scene scene) {
        app.input(scene);
    }

    public void draw(Application app, Scene scene, Map<String, Object> stats) {
        app.draw(scene, stats);
    }

    public void update(Application app, Scene scene, long elapsed, Map<String, Object> stats) {
        app.update(scene, elapsed, stats);
    }

    private void traceStatsCycle(Application app, Scene scene, int realFPS, int realUPS, Map<String, Object> datastats) {
        datastats.put("0_dbg", app.getConfiguration().debug ? "ON" : "off");
        if (app.getConfiguration().debug) {
            datastats.put("0_dbgLvl", app.getConfiguration().debugLevel);
        }
        datastats.put("1_FPS", realFPS);
        datastats.put("2_UPS", realUPS);
        datastats.put("3_nbObj", scene.getEntities().size());
        datastats.put("4_pause", app.isPaused() ? "on" : "off");
    }

    private void waitNextCycle(Application app, long elapsed) {
        int wait;
        wait = (int) ((1000.0 / this.ups) - elapsed * 0.000001);
        try {
            Thread.sleep((Math.max(wait, 1)));
        } catch (InterruptedException e) {
            Thread.interrupted();
            System.err.printf("Error while waiting for next update/frame %s%n",
                Arrays.toString(e.getStackTrace()));
        }
    }

    private void traceStats(Scene scene) {
        long staticEntities = scene.getEntities().stream()
            .filter(e -> e.physicType.equals(PhysicType.STATIC))
            .count();
        long dynamicEntities = scene.getEntities().stream()
            .filter(e -> e.physicType.equals(PhysicType.DYNAMIC))
            .count();
        long nonePhysicEntities = scene.getEntities().stream()
            .filter(e -> e.physicType.equals(PhysicType.NONE))
            .count();
        System.out.printf(
            ">> <!> Scene '%s' created with %d static entities, %d dynamic entities and %d with physic disabled entities and %d camera%n",
            scene.getName(),
            staticEntities,
            dynamicEntities,
            nonePhysicEntities,
            scene.getActiveCamera() != null ? 1 : 0);
    }
}
