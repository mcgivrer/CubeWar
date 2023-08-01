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
 * {@link Configuration} will load properties values into some configuration
 * attributes.
 * 
 * Those values can be overloaded from CLI arguments.
 * 
 * The path of the loaded properties file is set into the constructor, but can
 * be overriden
 * by the specific argument <code>configPath</code>. If thios argument is
 * present, it will reload
 * the configuration accordingly.
 * 
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public class Configuration extends ConcurrentHashMap<String, Object> {

    // the properties file handler.
    private Properties props = new Properties();

    // proposed configuration values.
    public boolean physicConstrained;
    public double timeScaleFactor;
    public boolean testMode;
    public String pathToConfigFile;

    public String name;
    public boolean debug;
    public int debugLevel;

    public Dimension winSize;
    public Dimension bufferResolution;
    public double maxEntitySpeed;
    public double maxEntityAcc;
    public World world;

    public int fps;
    public int ups;
    public String debugFilter;
    public boolean requestExit;

    /**
     * Iniitialize and load the configruation with the dedicated properties file,
     * and enhance file's values with possible CLI arguments.
     * 
     * @param app         the parent application
     * @param pathCfgFile the path to the properties configuration file to be
     *                    loaded.
     * @param lArgs       the list of command line argument to be parsed to overload
     *                    values from file properties.
     */
    public Configuration(Application app, String pathCfgFile, List<String> lArgs) {
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
     * Parse the properties configuration file to extract config values from.
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
        winSize = getDimension(config, "app.window.size", "640x400");
        // resolution
        bufferResolution = getDimension(config, "app.render.resolution", "320x200");

        // Time scale factor adaptation for Physic computation.
        timeScaleFactor = getParsedDouble(config, "app.physic.time.scale.factor", "1.0");
        // apply constraints on Speed and Acceleration.
        physicConstrained = getParsedBoolean(config, "app.physic.constrained", "false");
        // Maximum speed for Entity.
        maxEntitySpeed = getParsedDouble(config, "app.physic.speed.max", "16.0");
        // Maximum Acceleration for Entity.
        maxEntityAcc = Double.parseDouble(config.getProperty("app.physic.acceleration.max", "4.0"));
        // set Default World for physic engine computation.
        world = getWorld(config, "app.physic.world", "world(default,0.981,(1024x1024))");

        name = config.getProperty("app.name", "Default name Application");

        fps = getParsedInt(config, "app.render.fps", "60");
        ups = getParsedInt(config, "app.physic.ups", "120");

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
    private Rectangle2D getRectangle2D(Properties config, String key, String defaultValue) {
        System.out.printf(">> <!> Configuration attribute %s loaded to %s value.%n", key,
                config.getProperty(key, defaultValue));

        String[] paArgs = config.getProperty(key, defaultValue).split("x");
        return new Rectangle2D.Double(
                0, 0,
                Integer.parseInt(paArgs[0]),
                Integer.parseInt(paArgs[1]));
    }

    /**
     * Retrieve the key Dimension value from the config. if nof exists, return the
     * default value.
     *
     * @param config       the Properties instance to be parsed in.
     * @param key          the key for the required Dimension value.
     * @param defaultValue the default Dimension value for the key entry if it not
     *                     exists in.
     * @return Dimension value.
     */
    private Dimension getDimension(Properties config, String key, String defaultValue) {
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
    private World getWorld(Properties config, String key, String defaultValue) {
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
                default -> {
                    System.err.printf(">> <?> unknown argument: %s in %s%n", arg[0], s);
                }
            }
        });
    }
}
