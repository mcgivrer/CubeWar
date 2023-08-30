package com.snapgames.core.loop;

import com.snapgames.core.Application;
import com.snapgames.core.graphics.Renderer;
import com.snapgames.core.input.InputHandler;
import com.snapgames.core.math.physic.CollisionDetection;
import com.snapgames.core.math.physic.PhysicEngine;
import com.snapgames.core.math.physic.PhysicType;
import com.snapgames.core.math.physic.SpacePartition;
import com.snapgames.core.scene.Scene;
import com.snapgames.core.scene.SceneManager;
import com.snapgames.core.system.GSystemManager;
import com.snapgames.core.utils.StringUtils;
import com.snapgames.core.utils.config.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StandardGameLoop implements GameLoop {

    private final int fps;
    private final int ups;

    public StandardGameLoop(Configuration config) {

        // retrieve Frame-Per-Second
        this.fps = config.fps;
        // retrieve Update-Per-Second
        this.ups = config.ups;
    }

    @Override
    public void loop(Application app) {

        PhysicEngine physicEngine = GSystemManager.find(PhysicEngine.class);
        CollisionDetection cd = GSystemManager.find(CollisionDetection.class);
        SpacePartition spacePartition = GSystemManager.find(SpacePartition.class);
        InputHandler inputHandler = GSystemManager.find(InputHandler.class);
        Renderer renderer = GSystemManager.find(Renderer.class);
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

            app.input(inputHandler, scene);
            if (!app.isPaused()) {
                if (upsTime > (1000.0 / this.ups)) {
                    physicEngine.update(scene, elapsed * 0.00000002, stats);
                    spacePartition.update(scene, elapsed * 0.00000002);
                    cd.update(scene, elapsed * 0.00000002, stats);
                    cd.reset();
                    updates++;
                    upsTime = 0;
                }

                upsTime += (elapsed * 0.00001);
                cumulatedGameTime += elapsed * 0.000001;
            }
            if (fpsTime > (1000.0 / this.fps)) {
                renderer.draw(physicEngine.getWorld(), scene, stats);
                frames++;
                fpsTime = 0;
            }

            fpsTime += (elapsed * 0.00001);
            previous = start;
            elapsedTime += (elapsed * 0.000001);
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
            Thread.sleep((wait > 1 ? wait : 1));
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
