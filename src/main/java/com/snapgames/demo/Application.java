package com.snapgames.demo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * Main {@link Application} class for project Test002
 * <p>
 * Some basic minimalist good practice round Java development, without external
 * library, using only the JDK (20).
 * Only added the JUnit library to execute unit tests.
 * <p>
 * This {@link Application} class will manage a bunch of {@link Entity} or
 * {@link TextEntity} and a {@link Camera} to display
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
public class Application extends JPanel implements KeyListener {

    /**
     * Classe interne représentant une entité dans le jeu.
     *
     * <p>
     * Chaque entité possède un nom, une position, une taille, une vitesse, une
     * durée de vie, des attributs et des propriétés graphiques.
     * </p>
     */
    public class Entity<T extends Entity<?>> extends Rectangle2D.Double {
        final static int NONE = 0;
        final static int STATIC = 1;
        final static int DYNAMIC = 2;
        final static int TYPE_POINT = 1;
        final static int TYPE_LINE = 2;
        final static int TYPE_RECTANGLE = 3;
        final static int TYPE_ELLIPSE = 4;
        String name;
        double rotation;
        double dx;
        double dy;
        double drotation;
        double mass;
        private boolean active = true;

        int duration = -1;
        int life;

        Color color = Color.WHITE;
        Color fillColor = Color.RED;
        int priority = 1;

        Map<String, Object> attributes = new HashMap<>();
        List<Behavior<T>> behaviors = new ArrayList<>();
        public int physicType;
        public Material material;
        public int type;
        public int layer;
        public boolean constrainedToPlayArea = true;
        public boolean stickToCamera;
        public int contact;

        /**
         * Constructeur de l'entité.
         *
         * @param n le nom de l'entité
         * @param x la position en x de l'entité
         * @param y la position en y de l'entité
         * @param w la largeur de l'entité
         * @param h la hauteur de l'entité
         */
        public Entity(String n, double x, double y, int w, int h) {
            this.name = n;
            this.x = x;
            this.y = y;
            this.width = w;
            this.height = h;
            this.active = true;
            this.type = TYPE_RECTANGLE;
            this.stickToCamera = false;
            this.material = Material.DEFAULT;
        }

        /**
         * Met à jour l'entité.
         *
         * @param elapsed le temps écoulé depuis la dernière mise à jour
         */
        public void update(int elapsed) {
            if (life + elapsed > duration && duration != -1) {
                active = false;
            } else {
                life += elapsed;
            }
        }

        /**
         * Définit si l'entité est active ou non.
         *
         * @param active true pour activer l'entité, false pour la désactiver
         */
        public T setActive(boolean active) {
            this.active = active;
            if (duration != -1) {
                life = duration;
            }
            return (T) this;
        }

        /**
         * Vérifie si l'entité est active.
         *
         * @return true si l'entité est active, false sinon
         */
        public boolean isActive() {
            return this.active;
        }

        /**
         * Dessine l'entité.
         *
         * @param g le contexte graphique sur lequel dessiner
         */
        public void draw(Graphics2D g) {
            switch (type) {
                case TYPE_POINT -> {
                    if (color != null) {
                        g.setColor(color);
                        g.drawRect((int) x, (int) y, 1, 1);
                    }
                }
                case TYPE_LINE -> {
                    if (color != null) {
                        g.setColor(color);
                        g.drawLine((int) x, (int) y, width, height);
                    }
                }
                case TYPE_RECTANGLE -> {
                    if (fillColor != null) {
                        g.setColor(fillColor);
                        g.fill(this);
                    }
                    if (color != null) {
                        g.setColor(color);
                        g.draw(this);
                    }
                }
                case TYPE_ELLIPSE -> {
                    if (fillColor != null) {
                        g.setColor(fillColor);
                        g.fillOval((int) x, (int) y, (int) width, (int) height);
                    }
                    if (color != null) {
                        g.setColor(color);
                        g.drawOval((int) x, (int) y, (int) width, (int) height);
                    }
                }
                default -> {
                    System.err.printf("Unknown Entity type %d%n", type);
                }
            }
        }

        public T addBehavior(Behavior<T> b) {
            behaviors.add(b);
            return (T) this;
        }

        /**
         * Ajoute un attribut à l'entité.
         *
         * @param attrName  la clé de l'attribut
         * @param attrValue la valeur de l'attribut
         */
        public T setAttribute(String attrName, Object attrValue) {
            attributes.put(attrName, attrValue);
            return (T) this;
        }

        /**
         * Récupère la valeur de l'attribut spécifié.
         *
         * @param attrName la clé de l'attribut
         * @return la valeur de l'attribut, ou null si l'attribut n'existe pas
         */
        public <Y> Y getAttribute(String attrName, Y defaultValue) {
            return (Y) attributes.getOrDefault(attrName, defaultValue);
        }

        public T setSpeed(double dx, double dy) {
            this.dx = dx;
            this.dy = dy;
            return (T) this;
        }

        public T setPosition(double x, double y) {
            this.x = x;
            this.y = y;
            return (T) this;
        }

        public T setSize(int w, int h) {
            this.width = w;
            this.height = h;
            return (T) this;
        }

        public T setRotation(double r) {
            this.rotation = r;
            return (T) this;
        }

        public T setRotationSpeed(double dr) {
            this.drotation = dr;
            return (T) this;
        }

        public T setMass(double m) {
            this.mass = m;
            return (T) this;
        }

        public T setMaterial(Material mat) {
            this.material = mat;
            return (T) this;
        }

        public Rectangle2D getBounds2D() {
            return new Rectangle2D.Double(x, y, width, height);
        }

        public String toString() {
            return String.format("%s:pos[%.02f,%.02f]", name, x, y);
        }

        public T setStickToCamera(boolean b) {
            this.stickToCamera = b;
            return (T) this;
        }

        public T setPhysicType(int t) {
            assert (t == NONE || t == STATIC || t == DYNAMIC);
            this.physicType = t;
            return (T) this;
        }

        public T setColor(Color c) {
            this.color = c;
            return (T) this;
        }

        public T setFillColor(Color c) {
            this.fillColor = c;
            return (T) this;
        }

        public T setPriority(int t) {
            this.priority = t;
            return (T) this;
        }

        public T setContact(int c) {
            this.contact = c;
            return (T) this;
        }

        public T setConstrainedToPlayArea(boolean ctpa) {
            this.constrainedToPlayArea = ctpa;
            return (T) this;
        }

        public T setLayer(int l) {
            this.layer = l;
            return (T) this;
        }
    }

    public class GameObject extends Entity<GameObject> {

        /**
         * Create a new GameObject.
         *
         * @param n le nom de l'entité
         * @param x la position en x de l'entité
         * @param y la position en y de l'entité
         * @param w la largeur de l'entité
         * @param h la hauteur de l'entité
         */
        public GameObject(String n, double x, double y, int w, int h) {
            super(n, x, y, w, h);
        }
    }

    /**
     * La {@link Camera} qui permet de suivre une entité {@link Entity}.
     * <p>
     * The {@link Camera} object intends to track another {@link Entity} across the
     * play area (now defined in the {@link World#playArea} object).
     * <p>
     * So to use it, just add it to the scene, and set the {@link Camera#target}'s
     * {@link Entity} and
     * a {@link Camera#tween} factor to set the {@link Camera} velocity onthe
     * tracking.
     */
    public class Camera extends Entity<Camera> {

        private Entity target;
        private double tween;

        public Camera(String n, int vpWidth, int vpHeight) {
            super(n, 0, 0, vpWidth, vpHeight);
        }

        public void setTarget(Entity t) {
            this.target = t;
        }

        public void setTween(double t) {
            this.tween = t;
        }

        public void update(int elapsed) {
            this.rotation += drotation;
            this.x += (target.x - ((this.width - target.width) * 0.5) - this.x) * tween * elapsed;
            this.y += (target.y - ((this.height - target.height) * 0.5) - this.y) * tween * elapsed;
        }
    }

    public class TextEntity extends Entity<TextEntity> {
        public static final int ALIGN_LEFT = 1;
        public static final int ALIGN_CENTER = 2;
        public static final int ALIGN_RIGHT = 4;
        String text;
        Font font;
        Object value;
        int shadowWidth;
        Color shadowColor;
        int borderWidth;
        Color borderColor;
        int textAlign = ALIGN_LEFT;

        public TextEntity(String n, double x, double y) {
            super(n, x, y, 0, 0);
        }

        @Override
        public void draw(Graphics2D g) {
            if (Optional.ofNullable(font).isPresent()) {
                g.setFont(font);
            }
            FontMetrics fm = g.getFontMetrics();
            String textValue = text;
            if (text.contains("%") && Optional.ofNullable(value).isPresent()) {
                textValue = String.format(text, value);
            }
            this.width = fm.stringWidth(textValue);
            this.height = fm.getHeight();
            double offsetX = 0;
            switch (textAlign) {
                case ALIGN_LEFT -> {
                    offsetX = 0;
                }
                case ALIGN_CENTER -> {
                    offsetX = (int) (-this.width * 0.5);

                }
                case ALIGN_RIGHT -> {
                    offsetX = -this.width;
                }
                default -> {
                    offsetX = 0;
                    System.err.printf(">> <?> unknown textAlignt %d value for %s%n", textAlign, name);
                }
            }
            if (shadowWidth > 0 && Optional.ofNullable(shadowColor).isPresent()) {
                drawShadowText(g, textValue, x + offsetX, y);
            }
            if (borderWidth > 0 && Optional.ofNullable(borderColor).isPresent()) {
                drawBorderText(g, textValue, x + offsetX, y);
            }
            g.setColor(color);
            g.drawString(textValue, (int) (x + offsetX), (int) y);
        }

        private void drawShadowText(Graphics2D g, String textValue, double x, double y) {
            g.setColor(shadowColor);
            for (int i = 0; i < shadowWidth; i++) {
                g.drawString(textValue, (int) x + i, (int) y + i);
            }
        }

        private void drawBorderText(Graphics2D g, String textValue, double x, double y) {
            g.setColor(borderColor);
            for (int i = -borderWidth; i < borderWidth; i++) {
                for (int j = -borderWidth; j < borderWidth; j++) {
                    g.drawString(textValue, (int) x + i, (int) y + j);
                }
            }
        }

        public TextEntity setShadowColor(Color sc) {
            this.shadowColor = sc;
            return this;
        }

        public TextEntity setShadowWidth(int sw) {
            this.shadowWidth = sw;
            return this;
        }

        public TextEntity setBorderColor(Color bc) {
            this.borderColor = bc;
            return this;
        }

        public TextEntity setBorderWidth(int bw) {
            this.borderWidth = bw;
            return this;
        }

        public TextEntity setFont(Font f) {
            this.font = f;
            return this;
        }

        public TextEntity setText(String t) {
            this.text = t;
            return this;
        }

        public TextEntity setValue(Object v) {
            this.value = v;
            return this;
        }

        public TextEntity setTextAlign(int ta) {
            assert (ta == ALIGN_LEFT || ta == ALIGN_CENTER || ta == ALIGN_RIGHT);
            this.textAlign = ta;
            return this;
        }

    }

    public class Perturbation extends Entity<Perturbation> {
        public Perturbation(String n, double x, double y, int w, int h) {
            super(n, x, y, w, h);
        }

        @Override
        public void draw(Graphics2D g) {
            // nothing to draw, perturbation is only a virtual element applying its effect to other entities.
        }
    }

    public static class World {
        String name = "defaultWorld";
        Rectangle2D playArea;
        double gravity;
        Material material = Material.AIR;

        List<Perturbation> perturbations = new ArrayList<>();

        public World(String name) {
            this.name = name;
        }

        public World setGravity(double g) {
            this.gravity = g;
            return this;
        }

        public World setPlayArea(Rectangle2D pa) {
            this.playArea = pa;
            return this;
        }

        public World addPertubator(Perturbation p) {
            this.perturbations.add(p);
            return this;
        }

        @Override
        public String toString() {
            return "World{" +
                    "name='" + name + '\'' +
                    ", playArea=(" + playArea.getWidth() + "x" + playArea.getHeight() + ")" +
                    ", gravity=" + gravity +
                    '}';
        }
    }

    public static class Material {
        public static final Material DEFAULT = new Material("defualt", 0.0, 1.0, 1.0);
        public static final Material RUBBER = new Material("rubber", 0.68, 0.7, 0.67);
        public static final Material SUPER_BALL = new Material("superball", 0.98, 0.7, 0.998);
        public static final Material WOOD = new Material("wood", 0.20, 0.65, 0.50);
        public static final Material STEEL = new Material("steel", 0.10, 1.2, 0.12);
        public static final Material AIR = new Material("air", 0.0, 0.05, 0.9999);
        public static final Material WATER = new Material("water", 0.0, 0.90, 0.80);
        String name;
        double density;
        double elasticity;
        double roughness;

        /**
         * Craete a new {@link Material} with a name, a density, an elasticity
         * (bounciness), and
         * a roughness to compute friction.
         *
         * @param n the name for that {@link Material}.
         * @param e the elasticity or bounciness for this {@link Material}
         * @param d the density for this {@link Material}.
         * @param r the roughness or friction for this {@link Material}
         */
        public Material(String n, double e, double d, double r) {
            this.name = n;
            this.elasticity = e;
            this.density = d;
            this.roughness = r;
        }
    }

    /**
     * Add a specific {@link Behavior#update(Entity, int)} to a {@link Application.GameObject} entity.
     * <p>
     * The update phase for this {@link Application.GameObject} will be modified with the implementation of this behavior interface.
     *
     * @param <GameObject> the Entity to be modified.
     */
    public interface Behavior<GameObject> {
        void update(Entity<?> e, int elapsed);
    }

    private static int FPS = 60;
    private static int UPS = 120;
    private static double PIXEL_METER_RATIO = 12.0;

    private ResourceBundle messages;
    private Properties config = new Properties();

    /**
     * Configuration variables
     */
    protected String pathToConfigFile = "/config.properties";
    protected boolean exit = false;
    protected int debug = 0;
    protected boolean pause = false;
    protected Dimension winSize;
    protected Dimension bufferResolution;
    protected double maxEntitySpeed;

    private String title = "no-title";
    private String version = "0.0.0";

    /**
     * Graphics components
     */
    private JFrame frame;
    private BufferedImage buffer;

    private Camera camera;

    public World world;

    /**
     * Key listener components
     */
    private boolean keys[] = new boolean[1024];

    private Map<String, Entity<? extends Entity<?>>> entities = new HashMap<>();
    private boolean ctrlKey;
    private boolean shiftKey;
    private boolean altKey;
    private boolean metaKey;

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
        frame = createWindow();
        create();
        loop();
        dispose();
    }

    private void init(String[] args) {
        List<String> lArgs = Arrays.asList(args);
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
                config.load(inConfigStream);
                parseConfig(config);
            } else {
                System.err.printf(">> <?> Unable to read configuration from '%s'.%n", pathToConfigFile);
                System.exit(-1);
            }
        } catch (IOException e) {
            System.err.printf(">> <?> unable to read configuration file: %s%n", e.getMessage());
        }

        parseArgs(lArgs);
        System.out.printf(">> Initialization application %s (%s)%n",
                title,
                version);
    }

    /**
     * Parse the properties configuration file to extract config values from.
     *
     * @param config {@link Properties} instance to be parsed.
     */
    private void parseConfig(Properties config) {

        // --- Configuration information ---

        // debug level (0-5 where 0=off and 5 max debug info)
        debug = getParsedInt(config, "app.debug", "0");
        // exit flag to let test only ONE loop execution.
        exit = getParsedBoolean(config, "app.exit", "false");
        // Window size
        winSize = getDimension(config, "app.window.size", "640x400");
        // resolution
        bufferResolution = getDimension(config, "app.render.resolution", "320x200");
        // Maximum speed for Entity.
        maxEntitySpeed = Double.parseDouble(config.getProperty("app.physic.speed.max", "16.0"));

        world = getWorld(config, "app.physic.world", "world(default,0.981,(1024x1024))");

        // --- Translated information ---
        // Application name.
        title = Optional.of(messages.getString("app.name")).orElse("-Test002-");
        // Version of the application.
        version = Optional.of(messages.getString("app.version")).orElse("-1.0.0-");

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
        return Integer.parseInt(config.getProperty(key, defaultValue));
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
        String value = config.getProperty(key, defaultValue);
        String[] wArgs = value.substring("world(".length(), value.length() - ")".length()).split(",");
        double g = Double.parseDouble(wArgs[1]);
        String[] paArgs = wArgs[2].substring("(".length(), wArgs[2].length() - ")".length()).split("x");
        Rectangle2D pa = new Rectangle2D.Double(
                0, 0,
                Integer.parseInt(paArgs[0]),
                Integer.parseInt(paArgs[0]));
        return new World(wArgs[0]).setGravity(g).setPlayArea(pa);
    }

    private void parseArgs(List<String> lArgs) {
        lArgs.forEach(s -> {
            System.out.printf("- process arg: '%s'%n", s);
            String[] arg = s.split("=");
            switch (arg[0]) {
                case "x", "exit" -> {
                    exit = Boolean.parseBoolean(arg[1]);
                    System.out.printf(">> <!> argument 'exit' set to %s%n", arg[1]);
                }
                case "t", "title" -> {
                    title = arg[1];
                    System.out.printf(">> <!> argument 'title' set to %s%n", arg[1]);
                }
                case "cp", "configPath" -> {
                    pathToConfigFile = arg[1];
                    System.out.printf(">> <!> argument 'configuration file path' set to %s%n", arg[1]);
                }
                default -> {
                    System.err.printf(">> <?> unknown argument: %s in %s%n", arg[0], s);
                }
            }
        });
    }

    private JFrame createWindow() {
        JFrame frame = new JFrame(title);
        setPreferredSize(winSize);
        setMinimumSize(winSize);
        setSize(winSize);
        frame.setContentPane(this);
        frame.setLayout(new GridLayout());
        frame.enableInputMethods(true);
        frame.setFocusTraversalKeysEnabled(false);
        frame.setIgnoreRepaint(true);
        frame.requestFocus();
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                requestExit();
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.createBufferStrategy(2);
        frame.addKeyListener(this);
        clearWindow(frame);

        buffer = new BufferedImage(bufferResolution.width, bufferResolution.height, BufferedImage.TYPE_INT_ARGB);
        return frame;
    }

    private void clearWindow(JFrame frame) {
        frame.setBackground(Color.BLACK);
        frame.getGraphics().fillRect(0, 0, frame.getWidth(), frame.getHeight());
    }

    private void loop() {
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
        datastats.put("9_cam", camera);
        do {
            start = System.nanoTime();
            long elapsed = start - previous;

            input();

            update(elapsed, datastats);
            updates++;

            fps += (elapsed * 0.000001);
            if (fps < (1000 / FPS) && !pause) {
                draw(datastats);
                frames++;
            } else {
                fps = 0;
            }
            previous = start;
            elapsedTime += (elapsed * 0.000001);
            if (elapsedTime > 1000) {
                realFPS = frames;
                realUPS = updates;
                datastats.put("0_debug", debug);
                datastats.put("1_FPS", realFPS);
                datastats.put("2_UPS", realUPS);
                datastats.put("3_nbObj", entities.size());
                datastats.put("4_wait", wait);
                elapsedTime = 0;
                frames = 0;
                updates = 0;
            }
            wait = (int) ((1000.0 / UPS) - elapsed * 0.0000001);
            try {
                Thread.sleep((wait > 1 ? wait : 1));
            } catch (InterruptedException e) {
                Thread.interrupted();
                System.err.printf("Error while waiting for next update/frame %s%n",
                        Arrays.toString(e.getStackTrace()));
            }
        } while (!exit);
    }

    protected void create() {
        TextEntity score = new TextEntity("score", bufferResolution.getWidth() * 0.98, 32);
        score.setShadowColor(new Color(0.2f, 0.2f, 0.2f, 0.6f))
                .setPhysicType(Entity.STATIC)
                .setBorderColor(Color.BLACK)
                .setFont(getFont().deriveFont(20.0f))
                .setColor(Color.WHITE)
                .setShadowWidth(3)
                .setBorderWidth(2)
                .setText("%05d")
                .setValue(0)
                .setPriority(20)
                .setTextAlign(TextEntity.ALIGN_RIGHT)
                .setStickToCamera(true)
                .setMaterial(Material.RUBBER);

        addEntity(score);

        TextEntity heart = new TextEntity("heart", 10, bufferResolution.getHeight() * 0.90)
                .setPhysicType(Entity.STATIC)
                .setShadowColor(new Color(0.2f, 0.2f, 0.2f, 0.6f))
                .setBorderColor(Color.BLACK)
                .setFont(getFont().deriveFont(16.0f))
                .setColor(Color.RED)
                .setShadowWidth(3)
                .setBorderWidth(2)
                .setText("\u2764")
                .setPriority(20)
                .setStickToCamera(true);

        addEntity(heart);

        TextEntity life = new TextEntity("life", 20, bufferResolution.getHeight() * 0.90)
                .setPhysicType(Entity.STATIC)
                .setShadowColor(new Color(0.2f, 0.2f, 0.2f, 0.6f))
                .setBorderColor(Color.BLACK)
                .setFont(getFont().deriveFont(12.0f))
                .setColor(Color.WHITE)
                .setShadowWidth(3)
                .setBorderWidth(2)
                .setText("%d")
                .setValue(3)
                .setPriority(21)
                .setStickToCamera(true);

        addEntity(life);

        GameObject player = new GameObject("player",
                (int) ((bufferResolution.getWidth() - 16) * 0.5),
                (int) ((bufferResolution.getHeight() - 16) * 0.5),
                16, 16)
                .setPhysicType(Entity.DYNAMIC)
                .setPriority(10)
                .setMass(60.0)
                .setMaterial(Material.RUBBER)
                .setAttribute("speedStep", 3.0)
                .setAttribute("speedRotStep", 0.01);
        addEntity(player);

        addEntities(createDrops(world, "drop_%d", 2000));

        camera = new Camera("cam01", bufferResolution.width, bufferResolution.height);
        camera.setTarget(player);
        camera.setTween(0.2);
    }

    private List<GameObject> createDrops(World world, String namePrefix, int nbStars) {
        List<GameObject> drops = new ArrayList<>();
        for (int i = 0; i < nbStars; i++) {
            double x = Math.random() * (world.playArea.getWidth());
            double y = Math.random() * (world.playArea.getHeight());
            GameObject drop = new GameObject(
                    String.format(namePrefix, i),
                    x,
                    y,
                    1, 1)
                    .setPriority(1)
                    .setConstrainedToPlayArea(false)
                    .setLayer((int) (Math.random() * 9) + 1)
                    .setPhysicType(Entity.DYNAMIC)
                    .setColor(Color.YELLOW)
                    .setMaterial(Material.AIR)
                    .setMass(110.0)
                    .setSpeed(0.0, Math.random() * 0.4)
                    .addBehavior(new Behavior<GameObject>() {
                        @Override
                        public void update(Entity<?> e, int elapsed) {
                            e.setColor(new Color((e.layer * 0.1f), (e.layer * 0.1f), (e.layer * 0.1f)));
                            if (!world.playArea.getBounds2D().contains(new Point2D.Double(e.x, e.y))) {
                                e.setPosition(world.playArea.getWidth() * Math.random(),
                                        Math.random() * world.playArea.getHeight() * 0.1);
                            }
                        }
                    });
            drops.add(drop);
        }
        return drops;
    }

    protected void input() {
        Entity player = entities.get("player");
        boolean moving = false;
        // player moves
        double step = (double) player.getAttribute("speedStep", 2.0);
        double rotStep = (double) player.getAttribute("speedRotStep", 0.01);
        if (ctrlKey)
            step = step * 4.0;
        if (shiftKey)
            step = step * 2.0;

        // player rotation
        if (altKey) {
            if (keys[KeyEvent.VK_UP]) {
                player.setRotationSpeed(-rotStep);
            }
            if (keys[KeyEvent.VK_DOWN]) {
                player.setRotationSpeed(+rotStep);
            }
            if (keys[KeyEvent.VK_DELETE]) {
                player.setRotationSpeed(0.0);
                player.setRotation(0.0);
            }
        } else {
            if (keys[KeyEvent.VK_UP]) {
                player.setSpeed(player.dx, -step);
                moving = true;
            }
            if (keys[KeyEvent.VK_DOWN]) {
                player.setSpeed(player.dx, step);
                moving = true;
            }
        }

        if (keys[KeyEvent.VK_LEFT]) {
            player.setSpeed(-step, player.dy);
            moving = true;
        }
        if (keys[KeyEvent.VK_RIGHT]) {
            player.setSpeed(step, player.dy);
            moving = true;
        }

        // camera rotation
        if (keys[KeyEvent.VK_PAGE_UP]) {
            camera.setRotationSpeed(0.001);
        }
        if (keys[KeyEvent.VK_PAGE_DOWN]) {
            camera.setRotationSpeed(-0.001);
        }
        if (keys[KeyEvent.VK_DELETE]) {
            camera.setRotationSpeed(0.0);
            camera.setRotation(0.0);
        }
        if (!moving) {
            player.setSpeed(
                    player.dx * player.material.roughness,
                    player.dy * player.material.roughness);

        }
    }

    private void update(long elapsed, Map<String, Object> datastats) {
        int time = (int) (elapsed * 0.0000001);
        entities.values().stream().filter(e -> e.isActive()).sorted((a, b) -> a.physicType < b.physicType ? 1 : -1)
                .forEach(
                        e -> {
                            updateEntity(e, time);
                            e.setContact(0);
                            constrainPlayArea(e);
                        });
        if (Optional.ofNullable(camera).isPresent()) {
            camera.update(time);
        }
        long renderedEntities = entities.values().stream()
                .filter(e -> e.isActive())
                .filter(e -> inViewport(camera, e) || e.physicType == Entity.NONE)
                .sorted((a, b) -> a.priority > b.priority ? 1 : -1).count();
        datastats.put("5_rend", renderedEntities);
    }

    private void updateEntity(Entity<?> e, int elapsed) {

        double gravity = (e.stickToCamera || e.physicType == Entity.NONE ? 0.0 : world.gravity);

        e.rotation += e.drotation * elapsed;
        if (e.contact > 0) {
            e.dx *= e.material.roughness;
            e.dy *= e.material.roughness;
            e.drotation *= e.material.roughness;
        } else {
            e.dx *= world.material.roughness;
            e.dy *= world.material.roughness;
            e.drotation *= world.material.roughness;

        }
        e.y += (e.dy + gravity * (e.mass != 0.0 ? e.mass * e.material.density : 1.0)) * elapsed;
        e.x += e.dx * elapsed;

        e.update(elapsed);
        if (e.behaviors.size() > 0) {
            e.behaviors.forEach(b -> b.update(e, elapsed));
        }
        constrainToPhysic(e);
    }

    private void constrainToPhysic(Entity<? extends Entity<?>> e) {
        // maximize speed.
        if (Math.abs(e.dx) > maxEntitySpeed) {
            e.dx = Math.signum(e.dx) * maxEntitySpeed;
        }
        if (Math.abs(e.dy) > maxEntitySpeed) {
            e.dy = Math.signum(e.dy) * maxEntitySpeed;
        }
    }

    protected void constrainPlayArea(Entity<? extends Entity<?>> e) {

        if (!e.constrainedToPlayArea)
            return;
        if (e.x < 0) {
            e.x = 0;

            e.dx *= -e.material.elasticity;
            e.contact += 1;
        }
        if (e.x + e.width > world.playArea.getWidth()) {
            e.x = world.playArea.getWidth() - e.width;

            e.dx *= -e.material.elasticity;
            e.contact += 2;
        }
        if (e.y < 0) {
            e.y = 0;

            e.dy *= -e.material.elasticity;
            e.contact += 4;
        }
        if (e.y + e.height > world.playArea.getHeight()) {
            e.y = world.playArea.getHeight() - e.height;

            e.dy *= -e.material.elasticity;
            e.contact += 8;
        }
    }

    /**
     * Draw all {@link Application} the Entityies on window.
     *
     * @param datastats a set of metadata to be displayed on screen as debug
     *                  purpose. (only if Application#debug >0)
     */
    private void draw(Map<String, Object> datastats) {

        // prepare rendering buffer
        Graphics2D g = buffer.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // clear buffer
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());

        // draw playArea
        moveFromCameraPoV(g, camera, -1);
        drawGrid(g, world.playArea);
        g.setColor(Color.BLUE);
        g.draw(world.playArea);
        moveFromCameraPoV(g, camera, 1);

        // draw entities
        moveFromCameraPoV(g, camera, -1);
        drawAllEntities(g, false);
        moveFromCameraPoV(g, camera, 1);

        // draw all entities that are stick to Camera viewport.
        drawAllEntities(g, true);

        g.dispose();

        // copy to JFrame
        Graphics2D gScreen = (Graphics2D) frame.getBufferStrategy().getDrawGraphics();
        gScreen.drawImage(
                buffer, 0, 0, frame.getWidth(), frame.getHeight(),
                0, 0, buffer.getWidth(), buffer.getHeight(),
                null);
        if (debug > 0) {
            gScreen.setColor(Color.ORANGE);
            gScreen.drawString(
                    prepareStatsString(datastats, "[ ", " | ", " ]"),
                    20, frame.getHeight() - 20);
        }
        gScreen.dispose();
        // switch to next availbale drawing buffer
        frame.getBufferStrategy().show();
    }

    private void drawAllEntities(Graphics2D g, boolean stickToCamera) {
        entities.values().stream()
                .filter(e -> e.isActive() && e.stickToCamera == stickToCamera)
                .filter(e -> inViewport(camera, e) || e.physicType == Entity.STATIC)
                .sorted((a, b) -> a.priority > b.priority ? 1 : -1)
                .forEach(
                        e -> {
                            g.rotate(-e.rotation,
                                    e.x + e.width * 0.5,
                                    e.y + e.height * 0.5);
                            e.draw(g);

                            g.rotate(e.rotation,
                                    e.x + e.width * 0.5,
                                    e.y + e.height * 0.5);
                        });
    }

    private void moveFromCameraPoV(Graphics2D g, Camera camera, double direction) {
        if (camera != null) {
            AffineTransform af = AffineTransform.getRotateInstance(
                    direction * camera.rotation,
                    camera.width * 0.5,
                    camera.height * 0.5);
            af.translate(camera.x * direction, camera.y * direction);
            // A Zoom factor can be : af.scale(1.0 / zoom, 1.0 / zoom);
            g.transform(af);
        }
    }

    private void drawGrid(Graphics2D g, Rectangle2D playArea) {
        g.setColor(Color.DARK_GRAY);
        for (int x = 0; x < playArea.getWidth(); x += 32) {
            g.drawRect(x, 0, 32, (int) playArea.getHeight());
        }
        for (int y = 0; y < playArea.getWidth(); y += 32) {
            g.drawRect(0, y, (int) playArea.getWidth(), 32);
        }
    }

    private boolean inViewport(Camera cam, Entity<? extends Entity<?>> e) {
        return cam.getBounds2D().contains(e.getBounds2D());
    }

    public void dispose() {
        entities.clear();
        if (Optional.ofNullable(frame).isPresent()) {
            frame.dispose();
        }
        System.out.printf(">> End of application %s%n", title);
    }

    private Entity<? extends Entity<?>> addEntity(Entity<? extends Entity<?>> e) {
        return entities.put(e.name, e);
    }

    private void addEntities(List<GameObject> listEntities) {
        for (Entity<? extends Entity<?>> e : listEntities) {
            addEntity(e);
        }
    }

    protected void requestExit() {
        exit = true;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
        checkMetaKeys(e);
    }

    private void checkMetaKeys(KeyEvent e) {
        ctrlKey = e.isControlDown();
        shiftKey = e.isShiftDown();
        altKey = e.isAltDown();
        metaKey = e.isMetaDown();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE -> {
                requestExit();
            }
            default -> {
                // nothing to do !
            }
        }
    }

    /**
     * Create a String from all the {@link java.util.Map.Entry} of a {@link Map}.
     * <p>
     * the String is composed on the format "[ entry1:value1 | entry2:value2 ]"
     * where, in e the map :
     *
     * <pre>
     * Maps.of("1_entry1","value1","2_entry2","value2",...);
     * </pre>
     * <p>
     * this will sort the Entry on the `[9]` from the `[9]_[keyname]` key name.
     *
     * @param attributes the {@link Map} of value to be displayed.
     * @param start      the character to start the string with.
     * @param end        the character to end the string with.
     * @param delimiter  the character to seperate each entry.
     * @return a concatenated {@link String} based on the {@link Map}
     * {@link java.util.Map.Entry}.
     */
    public static String prepareStatsString(Map<String, Object> attributes, String start, String delimiter,
                                            String end) {
        return start + attributes.entrySet().stream().sorted(Map.Entry.comparingByKey()).map(entry -> {
            String value = "";
            switch (entry.getValue().getClass().getSimpleName()) {
                case "Double", "double", "Float", "float" -> {
                    value = String.format("%04.2f", entry.getValue());
                }
                case "Integer", "int" -> {
                    value = String.format("%5d", entry.getValue());
                }
                default -> {
                    value = entry.getValue().toString();
                }
            }
            return entry.getKey().substring(((String) entry.getKey().toString()).indexOf('_') + 1)
                    + ":"
                    + value;
        }).collect(Collectors.joining(delimiter)) + end;
    }

    public static void main(String[] argc) {
        Application app = new Application();
        app.run(argc);
    }

}
