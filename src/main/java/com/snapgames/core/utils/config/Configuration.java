package com.snapgames.core.utils.config;

import com.snapgames.core.Application;
import com.snapgames.core.math.Vector2D;
import com.snapgames.core.math.physic.World;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link Configuration} will load property values into some configuration
 * attributes.
 * <p>
 * Those values can be overloaded from CLI arguments.
 * <p>
 * The path of the loaded properties file is set into the constructor, but can
 * be overridden by the specific argument <code>configPath</code>. If this argument is
 * present, it will reload the configuration accordingly.
 *
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public class Configuration extends ConcurrentHashMap<String, Object> {

    // the properties file handler.
    private final Properties props = new Properties();

    // proposed configuration values.

    /**
     * Timescale factor to speed-up or slow down the game loop.
     */
    public double timeScaleFactor;
    /**
     * Activate the test mode for unit test only.
     */
    public boolean testMode;
    /**
     * path to the configuration properties file.
     */
    public String pathToConfigFile;
    /**
     * Internal name for this application.
     */
    public String name;
    /**
     * Debug mode activation.
     */
    public boolean debug;
    /**
     * If debug mode is activated, set the current required debug level.
     */
    public int debugLevel;

    /**
     * {@link com.snapgames.core.graphics.Renderer}:Window size to display the application.
     */
    public Dimension winSize;
    /**
     * {@link com.snapgames.core.graphics.Renderer}: the screen resolution for a rendering process.
     */
    public Dimension bufferResolution;
    /**
     * {@link com.snapgames.core.math.physic.PhysicEngine}: Maximum speed for any {@link com.snapgames.core.entity.Entity}.
     */
    public double maxEntitySpeed;
    /**
     * {@link com.snapgames.core.math.physic.PhysicEngine}: Maximum acceleration for any {@link com.snapgames.core.entity.Entity}.
     */
    public double maxEntityAcc;
    /**
     * {@link com.snapgames.core.math.physic.PhysicEngine}: {@link World} instance defining world constraints and environment for this {@link Application} at physic processing time.
     */
    public World world;
    /**
     * {@link com.snapgames.core.math.physic.PhysicEngine}: Define if Entities must be constrained by {@link World#playArea}.
     */
    public boolean physicConstrained;
    /**
     * {@link com.snapgames.core.math.physic.SpacePartition}: define the maximum number of {@link com.snapgames.core.entity.Entity} in a tree node.
     */
    public int maxEntitiesInSpace;
    /**
     * {@link com.snapgames.core.math.physic.SpacePartition}: define the maximum depth level in the tree.
     */
    public int maxLevelsInSpace;

    /**
     * targeted frame-per-second rate
     */
    public int fps;
    /**
     * targeted update-per-second rate.
     */
    public int ups;
    /**
     * Define an {@link com.snapgames.core.entity.Entity}'s name-based filtering for a debug display process.
     */
    public String debugFilter;
    /**
     * internal {@link Application} flag to decide exit request.
     */
    public boolean requestExit;

    /**
     * the default {@link com.snapgames.core.scene.Scene} name to be activated at {@link Application} start.
     */
    public String defaultScene;

    /**
     * Initialize and load the configuration with the dedicated properties file,
     * and enhance file's values with possible CLI arguments.
     *
     * @param pathCfgFile the path to the property configuration file to be
     *                    loaded.
     * @param lArgs       the list of command line argument to be parsed to overload
     *                    values from file properties.
     */
    public Configuration(String pathCfgFile, List<String> lArgs) {
        this.pathToConfigFile = pathCfgFile;
        parseArgs(lArgs);
        try {
            InputStream inConfigStream = Application.class.getClassLoader().getResourceAsStream(pathToConfigFile);
            if (inConfigStream == null) {
                inConfigStream = Application.class.getResourceAsStream(pathToConfigFile);
                System.out.println(">> <!> load configuration from class path.");
            } else {
                System.out.println(">> <!> load configuration from class loader path.");
            }
            if (inConfigStream != null) {
                props.load(inConfigStream);
                parseConfig(props);
            } else {
                System.err.printf(">> <?> Unable to read configuration from '%s'.%n", pathToConfigFile);
                System.exit(-1);
            }
        } catch (IOException e) {
            System.err.printf(">> <?> unable to read configuration file: %s%n", e.getMessage());
        }

        parseArgs(lArgs);
        System.out.printf(">> Configuration loaded%n");
    }

    /**
     * Parse the property configuration file to extract config values from.
     *
     * @param config {@link Properties} instance to be parsed.
     */
    private void parseConfig(Properties config) {

        // --- Configuration information ---

        // test mode (true = on)
        testMode = getParsedBoolean(config, "app.test.mode", "false");

        // debug mode (true = on)
        debug = getParsedBoolean(config, "app.debug", "false");
        // debug level (0-5 where 0=off and 5 max debug info)
        debugLevel = getParsedInt(config, "app.debug.level", "0");
        // debug filter: filtering entity on its name
        debugFilter = config.getProperty("app.debug.filter", "none");
        // exit flag to let test only ONE loop execution.
        requestExit = getParsedBoolean(config, "app.exit", "false");

        // Window size
        winSize = getParsedDimension(config, "app.window.size", "640x400");
        // resolution
        bufferResolution = getParsedDimension(config, "app.render.resolution", "320x200");

        // Timescale factor adaptation for Physic computation.
        timeScaleFactor = getParsedDouble(config, "app.physic.time.scale.factor", "1.0");
        // apply constraints on Speed and Acceleration.
        physicConstrained = getParsedBoolean(config, "app.physic.constrained", "false");
        // Maximum speed for Entity.
        maxEntitySpeed = getParsedDouble(config, "app.physic.speed.max", "16.0");
        // Maximum Acceleration for Entity.
        maxEntityAcc = Double.parseDouble(config.getProperty("app.physic.acceleration.max", "4.0"));
        // set the Default World for physic engine computation.
        world = getParsedWorld(config, "app.physic.world", "world(default,0.981,(1024x1024))");

        name = config.getProperty("app.window.name", "Default name Application");

        fps = getParsedInt(config, "app.render.fps", "60");
        ups = getParsedInt(config, "app.physic.ups", "120");

        maxEntitiesInSpace = getParsedInt(config, "app.physic.space.max.entities", "10");

        maxLevelsInSpace = getParsedInt(config, "app.physic.space.max.levels", "5");
    }

    /**
     * Retrieve the key boolean value from the config. if nof exists, return the
     * default value.
     *
     * @param config       the Properties instance to be parsed in.
     * @param key          the key for the required boolean value.
     * @param defaultValue the default boolean value for the key entry if it not
     *                     exists in.
     * @return boolean value.
     */
    private static boolean getParsedBoolean(Properties config, String key, String defaultValue) {
        System.out.printf(">> <!> Configuration attribute %s loaded to %s value.%n", key,
            config.getProperty(key, defaultValue));
        return Boolean.parseBoolean(config.getProperty(key, defaultValue));
    }

    /**
     * Retrieve the key Integer value from the config. if nof exists, return the
     * default value.
     *
     * @param config       the Properties instance to be parsed in.
     * @param key          the key for the required Integer value.
     * @param defaultValue the default Integer value for the key entry if it not
     *                     exists in.
     * @return Integer value.
     */
    private static int getParsedInt(Properties config, String key, String defaultValue) {
        System.out.printf(">> <!> Configuration attribute %s loaded to %s value.%n", key,
            config.getProperty(key, defaultValue));
        return Integer.parseInt(config.getProperty(key, defaultValue));
    }

    /**
     * Retrieve the key Double value from the config. if nof exists, return the
     * default value.
     *
     * @param config       the Properties instance to be parsed in.
     * @param key          the key for the required Double value.
     * @param defaultValue the default Double value for the key entry if it not
     *                     exists in.
     * @return Integer value.
     */
    private static double getParsedDouble(Properties config, String key, String defaultValue) {
        System.out.printf(">> <!> Configuration attribute %s loaded to %s value.%n", key,
            config.getProperty(key, defaultValue));
        return Double.parseDouble(config.getProperty(key, defaultValue));
    }

    /**
     * Retrieve the key Rectangle2D value from the config. if nof exists, return the
     * default value.
     *
     * @param config       the Properties instance to be parsed in.
     * @param key          the key for the required Rectangle2D value.
     * @param defaultValue the default Rectangle2D value for the key entry if it not
     *                     exists in.
     * @return Rectangle2D value.
     */
    private Rectangle2D getParsedRectangle2D(Properties config, String key, String defaultValue) {
        System.out.printf(">> <!> Configuration attribute %s loaded to %s value.%n", key,
            config.getProperty(key, defaultValue));

        String[] paArgs = config.getProperty(key, defaultValue).split("x");
        return new Rectangle2D.Double(
            0, 0,
            Integer.parseInt(paArgs[0]),
            Integer.parseInt(paArgs[1]));
    }

    /**
     * Retrieve the key Dimension value from the config.
     * If nof exists, return the
     * default value.
     *
     * @param config       the Properties instance to be parsed in.
     * @param key          the key for the required Dimension value.
     * @param defaultValue the default Dimension value for the key entry if it not
     *                     exists in.
     * @return Dimension value.
     */
    private Dimension getParsedDimension(Properties config, String key, String defaultValue) {
        System.out.printf(">> <!> Configuration attribute %s loaded to %s value.%n", key,
            config.getProperty(key, defaultValue));

        String[] winSizeArgs = config.getProperty(key, defaultValue).split("x");
        return new Dimension(
            Integer.parseInt(winSizeArgs[0]),
            Integer.parseInt(winSizeArgs[1]));
    }

    /**
     * Retrieve the key World object from the config. if not exists, return the
     * default value
     * (value format is <code>"world([name],[gravity],([width]x[height]))"</code>).
     *
     * @param config       the Properties instance to be parsed in.
     * @param key          the key for the required World value.
     * @param defaultValue the default World value for the key entry if it not
     *                     exists in.
     * @return World value.
     */
    private World getParsedWorld(Properties config, String key, String defaultValue) {
        System.out.printf(">> <!> Configuration attribute %s loaded to %s value.%n", key,
            config.getProperty(key, defaultValue));

        String value = config.getProperty(key, defaultValue);
        String[] wArgs = value.substring("world(".length(), value.length() - ")".length()).split(",");
        double g = Double.parseDouble(wArgs[1]);
        String[] paArgs = wArgs[2].substring("(".length(), wArgs[2].length() - ")".length()).split("x");
        Rectangle2D pa = new Rectangle2D.Double(
            0, 0,
            Integer.parseInt(paArgs[0]),
            Integer.parseInt(paArgs[0]));
        return new World(wArgs[0]).setGravity(new Vector2D(0, g)).setPlayArea(pa);
    }

    /**
     * Parse the argument's list to try and decode possible new parameters and
     * configuration to be applied
     * on the {@link Application} before initialization.
     *
     * @param lArgs the list of arguments coming from the Java Command line.
     */
    private void parseArgs(List<String> lArgs) {
        lArgs.forEach(s -> {
            System.out.printf("- process arg: '%s'%n", s);
            String[] arg = s.split("=");
            switch (arg[0]) {
                // do not execute loop, just perform one and exit (used for test purpose only).
                case "x", "exit" -> {
                    requestExit = Boolean.parseBoolean(arg[1]);
                    System.out.printf(">> <!> argument 'exit' set to %s%n", arg[1]);
                }
                case "testMode" -> {
                    testMode = Boolean.parseBoolean(arg[1]);
                    System.out.printf(">> <!> argument 'Test Mode' set to %s: test mode %s.%n", arg[1],
                        testMode ? "activated" : "NOT activated");
                }
                // define debug level for this application run.
                case "d", "debugLevel" -> {
                    debugLevel = Integer.parseInt(arg[1]);
                    this.debug = true;
                    System.out.printf(">> <!> argument 'Debug Level' set to %s: debug mode activated.%n", arg[1]);
                }
                case "f", "fps" -> {
                    fps = Integer.parseInt(arg[1]);
                    System.out.printf(">> <!> argument 'FPS' set to %s Frame-Per-Second.%n", arg[1]);
                }
                case "u", "ups" -> {
                    ups = Integer.parseInt(arg[1]);
                    System.out.printf(">> <!> argument 'UPS' set to %s Update-Per-Second.%n", arg[1]);
                }

                // define an alternate file configuration path to feed the Application.
                // used mainly for automated test requirement.
                case "c", "configPath" -> {
                    this.pathToConfigFile = arg[1];
                    System.out.printf(">> <!> argument 'configuration file path' set to %s%n", arg[1]);
                }
                case "ds", "defaultScene" -> {
                    this.defaultScene = arg[1];
                    System.out.printf(">> <!> argument 'default scene' set to %s%n", arg[1]);
                }
                default -> {
                    System.err.printf(">> <?> unknown argument: %s in %s%n", arg[0], s);
                }
            }
        });
    }
}
