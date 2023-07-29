package com.snapgames.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.snapgames.core.entity.Entity;
import com.snapgames.core.graphics.Renderer;
import com.snapgames.core.input.InputHandler;
import com.snapgames.core.math.physic.CollisionDetection;
import com.snapgames.core.math.physic.PhysicEngine;
import com.snapgames.core.math.physic.PhysicType;
import com.snapgames.core.scene.Scene;
import com.snapgames.core.scene.SceneManager;
import com.snapgames.core.system.GSystemManager;
import com.snapgames.core.utils.StringUtils;
import com.snapgames.core.utils.config.Configuration;
import com.snapgames.core.utils.i18n.I18n;

/**
 * Main {@link Application} class for project <code>TestJavaApp</code>.
 * <p>
 * Some basic minimalist good practice round Java development, without external
 * library, using only the JDK (20).
 * Only added the JUnit library to execute unit tests.
 * <p>
 * This application class must be jnhérited by your own implementation.
 * <p>
 * You will only need to instantiate your own class and call the
 * {@link Application#run(String[])} method to start your app.
 *
 * <pre>
 * public class MyApp extends Application {
 *     public static void main(String[] args) {
 *         MyApp app = new MyApp();
 *         app.run(args);
 *     }
 * }
 * </pre>
 *
 * @author Frédéric
 * @since 1.0.0
 */
public abstract class Application {
    private static int FPS = 60;
    private static int UPS = 120;

    public boolean exit = false;
    public boolean pause = false;

    /**
     * Configuration variables
     */
    public String pathToConfigFile = "/config.properties";

    public String title = "no-title";
    public String version = "0.0.0";

    protected Configuration configuration;
    protected SceneManager scnMgr;
    public boolean testMode;

    /**
     * Create the {@link Application}.
     * <p>
     * The default message file (from i18n/messages.properties) is loaded.
     */
    protected Application() {
    }

    public void run(String[] args) {
        init(args);
        initializeService();
        // --- Translated information ---
        // Application name.
        title = Optional.of(I18n.getMessage("app.window.name")).orElse("-Test002-");
        // Version of the application.
        version = Optional.of(I18n.getMessage("app.version")).orElse("-1.0.0-");

        ((Renderer) GSystemManager.find(Renderer.class))
            .createWindow(((InputHandler) GSystemManager.find(InputHandler.class)));
        createScenes();
        loop();
        if (!testMode) {
            dispose();
        }
        System.out.println(">> <!> Scene Ended.");
        System.out.printf(">> <!> Application %s exiting.%n", title);
    }

    /**
     * Initialize the {@link Application} by loading the configuration from
     * the possible argument file path (see <code>-config</code> argument),
     * or on the default configuration file <code>./config.properties</code>.
     *
     * @param args the list of arguments from the java CLI.
     */
    private void init(String[] args) {
        List<String> lArgs = Arrays.asList(args);
        configuration = new Configuration(this, pathToConfigFile, lArgs);
        testMode = configuration.testMode;
        exit = configuration.requestExit;
        pathToConfigFile = configuration.pathToConfigFile;
    }

    /**
     * Initialize all internal services before anything else.
     */
    private void initializeService() {
        GSystemManager.get();

        GSystemManager.add(I18n.get());
        GSystemManager.add(new PhysicEngine(this));
        GSystemManager.add(new CollisionDetection(this));
        GSystemManager.add(new Renderer(this));
        GSystemManager.add(new InputHandler(this));
        GSystemManager.add(new SceneManager(this));

        GSystemManager.initialize(this);
    }

    /**
     * The main {@link Application} Loop where every thing is processed and/or
     * displayed from.
     */
    private void loop() {

        PhysicEngine physicEngine = ((PhysicEngine) GSystemManager.find(PhysicEngine.class));
        CollisionDetection cd = ((CollisionDetection) GSystemManager.find(CollisionDetection.class));
        InputHandler inputHandler = ((InputHandler) GSystemManager.find(InputHandler.class));
        Renderer renderer = ((Renderer) GSystemManager.find(Renderer.class));
        Scene scene = ((SceneManager) GSystemManager.find(SceneManager.class)).getCurrent();

        System.out.printf(
            ">> <!> Activate Scene '%s'(%s).%n",
            scene.getName(), scene.getClass().getName());

        // retrieve Frame-Per-Second
        FPS = configuration.fps;
        // retrieve Update-Per-Second
        UPS = configuration.ups;

        scene.create(this);
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
        Map<String, Object> datastats = new HashMap<>();
        do {
            scene = ((SceneManager) GSystemManager.find(SceneManager.class)).getCurrent();
            start = System.nanoTime();
            long elapsed = start - previous;

            input(inputHandler, scene);
            if (!pause) {
                if (upsTime > (1000.0 / UPS)) {
                    cd.update(scene, elapsed * 0.00000002, datastats);
                    physicEngine.update(scene, elapsed * 0.00000002, datastats);
                    cd.reset();
                    updates++;
                    upsTime = 0;
                }

                upsTime += (elapsed * 0.00001);
                cumulatedGameTime += elapsed * 0.000001;
            }
            if (fpsTime > (1000.0 / FPS)) {
                renderer.draw(physicEngine.getWorld(), scene, datastats);
                frames++;
                fpsTime = 0;
            }

            fpsTime += (elapsed * 0.00001);
            previous = start;
            elapsedTime += (elapsed * 0.000001);
            if (elapsedTime > 1000) {
                realFPS = frames;
                realUPS = updates;
                traceStatsCycle(scene, realFPS, realUPS, datastats);

                elapsedTime = 0;
                frames = 0;
                updates = 0;
            }
            waitNextCycle(elapsed);

            datastats.put("5_internal", StringUtils.formatDuration(cumulatedGameTime));
        } while (!(exit || testMode));
    }

    private void traceStatsCycle(Scene scene, int realFPS, int realUPS, Map<String, Object> datastats) {
        datastats.put("0_dbg", configuration.debug ? "ON" : "off");
        if (configuration.debug) {
            datastats.put("0_dbgLvl", configuration.debugLevel);
        }
        datastats.put("1_FPS", realFPS);
        datastats.put("2_UPS", realUPS);
        datastats.put("3_nbObj", scene.getEntities().size());
        datastats.put("4_pause", this.pause ? "on" : "off");
    }

    private void waitNextCycle(long elapsed) {
        int wait;
        wait = (int) ((1000.0 / UPS) - elapsed * 0.000001);
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

    /**
     * Create the scene with all the required {@link Entity}'s to be displayed and
     * managed by this {@link Application} scene.
     */
    protected abstract void createScenes();

    protected void input(InputHandler ih, Scene scene) {
        scene.input(this, ih);
    }

    public void dispose() {
        GSystemManager.dispose();
    }

    public void requestExit() {
        exit = true;
    }

    public boolean isPaused() {
        return this.pause;
    }

    public void setPause(boolean p) {
        this.pause = p;
    }

    public void setExit(boolean x) {
        this.exit = x;
    }

    public void setTitle(String t) {
        this.title = t;
    }

    public void setPathToConfigFile(String ptcf) {
        this.pathToConfigFile = ptcf;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public boolean isDebugAtLeast(int dl) {
        return configuration.debugLevel >= dl;
    }

    public boolean isExiting() {
        return exit;
    }

    public String getDebugFilter() {
        return configuration.debugFilter;
    }

    public I18n getI18n() {
        return I18n.get();
    }
}
