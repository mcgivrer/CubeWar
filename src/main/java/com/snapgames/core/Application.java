package com.snapgames.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.snapgames.core.entity.Entity;
import com.snapgames.core.graphics.Renderer;
import com.snapgames.core.input.InputHandler;
import com.snapgames.core.loop.GameLoop;
import com.snapgames.core.loop.StandardGameLoop;
import com.snapgames.core.math.physic.CollisionDetection;
import com.snapgames.core.math.physic.PhysicEngine;
import com.snapgames.core.math.physic.PhysicType;
import com.snapgames.core.math.physic.SpacePartition;
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
    public static int FPS = 60;
    public static int UPS = 120;

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
    private GameLoop gameLoop;

    /**
     * Create the {@link Application}.
     * <p>
     * The default message file (from i18n/messages.properties) is loaded.
     */
    protected Application() {
    }

    public void run(String[] args) {
        initialize(args);
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
    private void initialize(String[] args) {
        List<String> lArgs = Arrays.asList(args);
        configuration = new Configuration(pathToConfigFile, lArgs);
        testMode = configuration.testMode;
        exit = configuration.requestExit;
        pathToConfigFile = configuration.pathToConfigFile;
    }

    /**
     * Initialize all internal services before anything else.
     */
    private void initializeService() {
        GSystemManager.get();
        gameLoop = new StandardGameLoop(configuration);
        GSystemManager.add(I18n.get());
        GSystemManager.add(new PhysicEngine(this));
        GSystemManager.add(new SpacePartition(this));
        GSystemManager.add(new CollisionDetection(this));
        GSystemManager.add(new Renderer(this));
        GSystemManager.add(new InputHandler(this));
        GSystemManager.add(new SceneManager(this));

        GSystemManager.initialize(configuration);
    }

    /**
     * The main {@link Application} Loop where every thing is processed and/or
     * displayed from.
     */
    private void loop() {
        gameLoop.loop(this);
    }

    public void input(Scene scene) {
        InputHandler inputHandler = GSystemManager.find(InputHandler.class);
        inputHandler.input();
    }

    public void draw(Scene scene, Map<String, Object> stats) {
        PhysicEngine physicEngine = GSystemManager.find(PhysicEngine.class);
        Renderer renderer = GSystemManager.find(Renderer.class);
        renderer.draw(physicEngine.getWorld(), scene, stats);
    }

    public void update(Scene scene, long elapsed, Map<String, Object> stats) {
        PhysicEngine physicEngine = GSystemManager.find(PhysicEngine.class);
        CollisionDetection cd = GSystemManager.find(CollisionDetection.class);
        SpacePartition spacePartition = GSystemManager.find(SpacePartition.class);

        GSystemManager.update(scene, elapsed * 0.00000002, stats);

        physicEngine.update(scene, elapsed * 0.00000002, stats);
        spacePartition.update(scene, elapsed * 0.00000002, stats);
        cd.update(scene, elapsed * 0.00000002, stats);

        cd.reset();
    }


    /**
     * Create the scene with all the required {@link Entity}'s to be displayed and
     * managed by this {@link Application} scene.
     */
    protected abstract void createScenes();

    public void input(InputHandler ih, Scene scene) {
        ih.input();
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

    public boolean isTestMode() {
        return testMode;
    }
}
