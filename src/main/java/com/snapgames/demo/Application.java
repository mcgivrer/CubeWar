package com.snapgames.demo;

import com.snapgames.demo.behavior.ParticleBehavior;
import com.snapgames.demo.entity.Camera;
import com.snapgames.demo.entity.Entity;
import com.snapgames.demo.entity.GameObject;
import com.snapgames.demo.entity.TextObject;
import com.snapgames.demo.graphics.Renderer;
import com.snapgames.demo.input.InputHandler;
import com.snapgames.demo.math.physic.PhysicEngine;
import com.snapgames.demo.math.physic.Vector2D;
import com.snapgames.demo.math.physic.World;
import com.snapgames.demo.scene.DemoScene;
import com.snapgames.demo.scene.Scene;
import com.snapgames.demo.scene.SceneManager;
import com.snapgames.demo.utils.config.Configuration;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.*;

/**
 * Main {@link Application} class for project Test002
 * <p>
 * Some basic minimalist good practice round Java development, without external
 * library, using only the JDK (20).
 * Only added the JUnit library to execute unit tests.
 * <p>
 * This {@link Application} class will manage a bunch of {@link Entity} or
 * {@link TextObject} and a {@link Camera} to display
 * amazing things on the rendering buffer before displaying it on the
 * {@link JFrame} window.
 * <p>
 * It also maintains some basic physic math about moves for the
 * {@link Entity#active}.
 * <p>
 * {@link Entity} can be from 2 physic nature:
 * <ul>
 * <li><code>{@link Entity#STATIC}</code>, stick to the display screen,</li>
 * <li><code>{@link Entity#DYNAMIC}</code>, move according to the first Newton's
 * law on movement.</li>
 * </ul>
 * <p>
 * And this {@link Entity} can be :
 * <ul>
 * <li><code>{@link Entity#TYPE_POINT}</code> to be drawn as a simple 2D
 * point,</li>
 * <li><code>{@link Entity#TYPE_LINE}</code> to be drawn as a 2D line from (x,y)
 * to (width,height),</li>
 * <li><code>{@link Entity#TYPE_RECTANGLE}</code> to be drawn as a 2D rectangle
 * at (x,y) of size (width,height),</li>
 * <li><code>{@link Entity#TYPE_ELLIPSE}</code> to be drawn as a 2D ellipse at
 * (x,y) with (r1=width and r2=height).</li>
 * </ul>
 *
 * @author Frédéric
 * @since 1.0.0
 */
public class Application {


    private static int FPS = 120;
    private static int UPS = 60;
    private static double PIXEL_METER_RATIO = 12.0;

    private ResourceBundle messages;


    public boolean exit = false;
    public boolean pause = false;

    /**
     * Configuration variables
     */
    protected String pathToConfigFile = "/config.properties";


    public String title = "no-title";
    public String version = "0.0.0";


    private Configuration configuration;
    private SceneManager scnMgr;
    private PhysicEngine physicEngine;
    private Renderer renderer;
    private InputHandler inputHandler;


    /**
     * Create the {@link Application}.
     * <p>
     * The default message file (from i18n/messages.properties) is loaded.
     */
    public Application() {
        messages = ResourceBundle.getBundle("i18n/messages");
    }

    public void run(String[] args) {
        init(args);
        initializeService();

        // --- Translated information ---
        // Application name.
        title = Optional.of(messages.getString("app.window.name")).orElse("-Test002-");
        // Version of the application.
        version = Optional.of(messages.getString("app.version")).orElse("-1.0.0-");

        renderer.createWindow(inputHandler);
        createScenes();
        Scene scene = scnMgr.getCurrent();
        long staticEntities = scene.getEntities().stream().filter(e -> e.physicType == Entity.STATIC).count();
        long dynamicEntities = scene.getEntities().stream().filter(e -> e.physicType == Entity.DYNAMIC).count();
        long nonePhysicEntities = scene.getEntities().stream().filter(e -> e.physicType == Entity.NONE).count();
        System.out.printf(
                ">> Scene created with %d static entities, %d dynamic entities and %d with physic disabled entities and %d camera%n",
                staticEntities, dynamicEntities, nonePhysicEntities, scene.getActiveCamera() != null ? 1 : 0);
        loop();
        dispose();
    }

    void dispose() {
        physicEngine.dispose();
        renderer.dispose();
        scnMgr.dispose();
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
    }

    /**
     * Initialize all internal services before anything else.
     */
    private void initializeService() {
        this.physicEngine = new PhysicEngine(this);
        this.renderer = new Renderer(this);
        this.inputHandler = new InputHandler(this);
        this.scnMgr = new SceneManager(this);
    }

    /**
     * The main {@link Application} Loop where every thing is processed and/or
     * displayed from.
     */
    private void loop() {
        scnMgr.getCurrent().create(this);
        long start = System.nanoTime();
        long previous = start;
        long elapsedTime = 0;
        int fps = 0;
        int realFPS = 0;
        int realUPS = 0;
        int frames = 0;
        int updates = 0;
        int wait = 0;
        Map<String, Object> datastats = new HashMap<>();
        Scene scene = scnMgr.getCurrent();
        datastats.put("9_cam", scene.getActiveCamera());
        do {
            start = System.nanoTime();
            long elapsed = start - previous;

            input(inputHandler, scene);

            physicEngine.update(scnMgr.getCurrent(), elapsed, datastats);
            updates++;

            fps += (elapsed * 0.0000001);
            if (fps < (1000 / FPS) && !pause) {
                renderer.draw(physicEngine.getWorld(), scnMgr.getCurrent(), datastats);
                frames++;
            } else {
                fps = 0;
            }
            previous = start;
            elapsedTime += (elapsed * 0.000001);
            if (elapsedTime > 1000) {
                realFPS = frames;
                realUPS = updates;
                datastats.put("0_dbg", configuration.debug ? "ON" : "off");
                if (configuration.debug) {
                    datastats.put("0_dbgLvl", configuration.debugLevel);
                }
                datastats.put("1_FPS", realFPS);
                datastats.put("2_UPS", realUPS);
                datastats.put("3_nbObj", scnMgr.getCurrent().getEntities().size());
                datastats.put("4_wait", wait);
                elapsedTime = 0;
                frames = 0;
                updates = 0;
            }
            wait = (int) ((1000.0 / UPS) - elapsed * 0.000001);
            try {
                Thread.sleep((wait > 1 ? wait : 1));
            } catch (InterruptedException e) {
                Thread.interrupted();
                System.err.printf("Error while waiting for next update/frame %s%n",
                        Arrays.toString(e.getStackTrace()));
            }
        } while (!exit);
    }

    /**
     * Create the scene with all the required {@link Entity}'s to be displayed and
     * managed by this {@link Application} scene.
     */
    protected void createScenes() {
        scnMgr.add(new DemoScene());
    }



    protected void input(InputHandler ih, Scene scene) {
        scene.input(this, ih);
    }

    public void dispose(Scene scene) {
        scene.clearScene();
        renderer.dispose();
        System.out.printf(">> End of application %s%n", title);
    }

    public void requestExit() {
        exit = true;
    }

    public boolean isPause() {
        return this.pause;
    }

    public void setPause(boolean p) {
        this.pause = p;
    }


    /**
     * The entrypoint for our Application to be initialized and executed.
     *
     * @param argc the list of arguments from the Java command line.
     */
    public static void main(String[] argc) {
        Application app = new Application();
        app.run(argc);
    }

    public PhysicEngine getPhysicEngine() {
        return physicEngine;
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

    public ResourceBundle getMessages() {
        return messages;
    }

    public Renderer getRenderer() {
        return this.renderer;
    }

    public SceneManager getSceneManager() {
        return this.scnMgr;
    }
}
