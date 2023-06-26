package com.snapgames.demo;

import java.awt.*;
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
public class Application extends JPanel implements KeyListener {

    /**
     * Classe interne représentant une entité dans le jeu.
     *
     * <p>
     * Chaque entité possède un nom, une position, une taille, une vitesse, une
     * durée de vie, des attributs et des propriétés graphiques.
     * </p>
     */
    public static class Entity<T extends Entity<?>> extends Rectangle2D.Double {
        final static int NONE = 0;
        final static int STATIC = 1;
        final static int DYNAMIC = 2;
        final static int TYPE_POINT = 1;
        final static int TYPE_LINE = 2;
        final static int TYPE_RECTANGLE = 3;
        final static int TYPE_ELLIPSE = 4;

        final static int TYPE_IMAGE = 5;
        static int index = 0;
        protected List<Point2D> forces = new ArrayList<>();
        int id = ++index;
        String name = "entity_" + id;
        double rotation;

        double oldX;
        double oldY;
        double dx;
        double dy;
        double dRotation;
        double mass;
        private boolean active;

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
        public boolean constrainedToPlayArea;
        public boolean stickToCamera;
        public int contact;
        public int debug = 5;
        private Entity<?> parent;
        private List<Entity<?>> child = new ArrayList<>();
        private BufferedImage image;

        /**
         * Constructeur de l'entité.
         *
         * @param n le nom de l'entité
         * @param x la position en x de l'entité
         * @param y la position en y de l'entité
         * @param w la largeur de l'entité
         * @param h la hauteur de l'entité
         */
        public Entity(String n, double x, double y, double w, double h) {
            this.name = n;
            this.x = x;
            this.y = y;
            this.width = w;
            this.height = h;
            this.active = true;
            this.type = TYPE_RECTANGLE;
            this.stickToCamera = false;
            this.constrainedToPlayArea = true;
            this.material = Material.DEFAULT;
        }

        /**
         * Met à jour l'entité.
         *
         * @param elapsed le temps écoulé depuis la dernière mise à jour
         */
        public void update(double elapsed) {

            life += elapsed;
            if (life > duration && duration != -1) {
                active = false;
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
            child.forEach(c -> c.setActive(active));
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

        public T setDebug(int d) {
            this.debug = d;
            return (T) this;
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
                        g.drawLine((int) x, (int) y, (int) oldX, (int) oldY);
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
                case TYPE_IMAGE -> {
                    if (Optional.ofNullable(image).isPresent()) {
                        if (dx > 0) {
                            g.drawImage(image, (int) x, (int) y, null);
                        } else {
                            g.drawImage(image, (int) (x - width), (int) y, (int) -width, (int) height, null);
                        }
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
         * Add an attribute to this Entity.
         *
         * @param attrName  Attribute's name
         * @param attrValue Attribute's value
         */
        public T setAttribute(String attrName, Object attrValue) {
            attributes.put(attrName, attrValue);
            return (T) this;
        }

        /**
         * Retrieve the specified attribute value.
         *
         * @param attrName     Attribute's name
         * @param defaultValue the default value if it does not exist.
         * @return Attribute's value or the default value if it was not existing.
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
            this.dRotation = dr;
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

        public T setType(int t) {
            this.type = t;
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

        public List<String> getDebugInfo() {
            List<String> infos = new ArrayList<>();
            infos.add(String.format("1_#%d", id));
            infos.add(String.format("1_name:%s", name));
            infos.add(String.format("2_pos:(%.02f,%.02f)", x, y));
            infos.add(String.format("2_size:(%.02f,%.02f)", width, height));
            infos.add(String.format("3_vel:(%.02f,%.02f)", dx, dy));
            if (mass != 0.0) infos.add(String.format("3_mass:%.02f kg", mass));
            if (material != null) infos.add(String.format("4_mat:%s", material));
            return infos;
        }

        public T setParent(Entity<?> e) {
            this.parent = e;
            return (T) this;
        }

        public T addChild(Entity<?> c) {
            this.child.add(c);
            return (T) this;
        }

        public T setDuration(int d) {
            this.duration = d;
            return (T) this;
        }
    }

    /**
     * The {@link GameObject} is the basic Object element to be displayed on screen.
     * It can be a POINT, a LINE,a RECTANGLE, an ELLIPSE or an IMAGE.
     */
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
            setMaterial(null);
            setMass(0.0);
        }

        public void setTarget(Entity t) {
            this.target = t;
        }

        public void setTween(double t) {
            this.tween = t;
        }

        public void update(int elapsed) {
            this.rotation += dRotation;
            this.x += (target.x - ((this.width - target.width) * 0.5) - this.x) * tween * elapsed;
            this.y += (target.y - ((this.height - target.height) * 0.5) - this.y) * tween * elapsed;
        }

        @Override
        public List<String> getDebugInfo() {
            List<String> infos = super.getDebugInfo();
            infos.add(String.format("3_target:%s", target.name));
            infos.add(String.format("4_vp:%fx%f", width, height));
            return infos;
        }
    }

    /**
     * The {@link TextObject} is an extended {@link Entity} to support Text drawing.
     * <p>
     * the <code>text</code> can be a simple string, or a formatted String with the provided <code>value</code>.
     * The text will be then compatible with the {@link String#format(String, Object...)} formatting rules.
     * <p>
     * It also supports
     * <ul>
     *     <li>a graphical <code>textAlign</code> attribute than can be one of the {@link TextObject#ALIGN_LEFT},
     *     {@link TextObject#ALIGN_CENTER} or {@link TextObject#ALIGN_RIGHT} values,</li>
     *     <li> a <code>font</code> to define font family and size,</li>
     *     <li>a <code>shadowColor</code> and <code>shadowWidth</code> to define a text shadow,</li>
     *     <li>a <code>borderColor</code> and a <code>borderWidth</code> to define a outlined border on the text.</li>
     * </ul>
     */
    public static class TextObject extends Entity<TextObject> {
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

        public TextObject(String n, double x, double y) {
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
                    System.err.printf(">> <?> unknown textAlign %d value for %s%n", textAlign, name);
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

        public TextObject setShadowColor(Color sc) {
            this.shadowColor = sc;
            return this;
        }

        public TextObject setShadowWidth(int sw) {
            this.shadowWidth = sw;
            return this;
        }

        public TextObject setBorderColor(Color bc) {
            this.borderColor = bc;
            return this;
        }

        public TextObject setBorderWidth(int bw) {
            this.borderWidth = bw;
            return this;
        }

        public TextObject setFont(Font f) {
            this.font = f;
            return this;
        }

        public TextObject setText(String t) {
            this.text = t;
            return this;
        }

        public TextObject setValue(Object v) {
            this.value = v;
            return this;
        }

        public TextObject setTextAlign(int ta) {
            assert (ta == ALIGN_LEFT || ta == ALIGN_CENTER || ta == ALIGN_RIGHT);
            this.textAlign = ta;
            return this;
        }

        @Override
        public List<String> getDebugInfo() {
            List<String> infos = super.getDebugInfo();
            infos.add(String.format("3_text:%s", text));
            infos.add(String.format("3_val:%s", value != null ? value.toString() : "null"));
            return infos;
        }
    }

    /**
     * A {@link Perturbation} is a rectangle area into the {@link World#playArea} where Entity will be influenced by
     * some physic changes.
     * <p>
     * It can be an attraction factor applied to any entity in this perturbation area, or a new force added to the {@link Entity},
     * this both thing are applied at computation time into the {@link Application#updateEntity(Entity, double)} processing method.
     */
    public static class Perturbation extends Entity<Perturbation> {
        private double attraction;

        public Perturbation(String n, double x, double y, int w, int h) {
            super(n, x, y, w, h);
        }

        @Override
        public void draw(Graphics2D g) {
            // nothing to draw, perturbation is only a virtual element applying its effect
            // to other entities.
        }

        public Perturbation setAttraction(double attraction) {
            this.attraction = attraction;
            return this;
        }

        public Perturbation setForce(Point2D f) {
            this.forces.add(f);
            return this;
        }

    }

    /**
     * The {@link World} defines the context and environment where all {@link Application}'s {@link Entity} will evolve.
     * <p>
     * A <code>name</code>, a <code>playArea</code> and a <code>gravity</code> are the first mandatory things.
     * the <code>perturbations</code> list will be implemented and used to influence
     * {@link Entity} in certain places into the world play area.
     */
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

    /**
     * The {@link Material} object contains all the physic attribute for a material.
     * <p>
     * It contains
     * <ul>
     *     <li>a <code>name</code> to just debug purpose and understand the attribute's values for this {@link Material},</li>
     *     <li>a <code>density</code> value (double type),</li>
     *     <li>an <code>elasticity</code> factor (0.0 to 1.0),</li>
     *     <li>a <code>roughness</code> factor (0.0 to 1.0).</li>
     * </ul>
     * <p>It will be used :
     * <ul>
     *     <li>to be applied to any {@link Entity}</li>
     * <li>used by the physic computation done at {@link Application#updateEntity(Entity, double)} processing.</li>
     * </ul>
     */
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

        public String toString() {
            return "{n:" + name + "," + ",d:" + density + ",e:" + elasticity + ",r:" + roughness + "}";
        }
    }

    /**
     * Add a specific {@link Behavior#update(Entity, double)} to a
     * {@link Application.GameObject} entity.
     * <p>
     * The update phase for this {@link Application.GameObject} will be modified
     * with the implementation of this behavior interface.
     *
     * @param <GameObject> the Entity to be modified.
     */
    public interface Behavior<GameObject> {
        /**
         * Implement the <code>update</code> the e Entity according to the elapsed time.
         *
         * @param e       the Entity to be updated.
         * @param elapsed the elapsed time since previous call.
         */
        void update(Entity<?> e, double elapsed);
    }

    /**
     * Add a specific {@link ParticleBehavior#create(World, String, Entity)} extending the existing {@link Behavior}
     * and will be applied to  {@link Application.GameObject} entity.
     * <p>
     * The new <code>create</code> phase for this {@link Application.GameObject} will be modified
     * with the implementation of this behavior interface, and allow to create a new particle by the
     * {@link Application#createParticleSystem(World, String, int, ParticleBehavior)}, while the already defined
     * {@link Behavior#update(Entity, double)} will be used to update the created particles like any other
     * {@link Application.GameObject}.
     *
     * @param <GameObject> the Entity to be modified.
     */
    public interface ParticleBehavior<GameObject> extends Behavior<GameObject> {
        /**
         * Implement the <code>create</code> phase for the particle using a prefix name, the {@link World} object as
         * context and the parent {@link Entity}
         *
         * @param w                  the world context object defining the environment where this new particle will evolve.
         * @param particleNamePrefix the prefix name for this new particle. it will be completed by the internal {@link Entity#index} value.
         * @param parent             the parent {@link Entity} hosting this particle.
         * @return
         */
        GameObject create(World w, String particleNamePrefix, Entity<?> parent);
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
    private String name;
    private int debugLevel;

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
        long staticEntities = entities.values().stream().filter(e -> e.physicType == Entity.STATIC).count();
        long dynamicEntities = entities.values().stream().filter(e -> e.physicType == Entity.DYNAMIC).count();
        long nonePhysicEntities = entities.values().stream().filter(e -> e.physicType == Entity.NONE).count();
        System.out.printf(
                ">> Scene created with %d static entities, %d dynamic entities and %d with physic disabled entities and %d camera%n",
                staticEntities, dynamicEntities, nonePhysicEntities, camera != null ? 1 : 0);
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

        name = config.getProperty("app.name", "Default name Application");

        // --- Translated information ---
        // Application name.
        title = Optional.of(messages.getString("app.window.name")).orElse("-Test002-");
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
                case "d", "debug" -> {
                    debugLevel = Integer.parseInt(arg[1]);
                    System.out.printf(">> <!> argument 'debug' set to %s%n", arg[1]);
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
        TextObject score = new TextObject("score", bufferResolution.getWidth() * 0.98, 32)
                .setShadowColor(new Color(0.2f, 0.2f, 0.2f, 0.6f))
                .setPhysicType(Entity.STATIC)
                .setBorderColor(Color.BLACK)
                .setFont(getFont().deriveFont(20.0f))
                .setColor(Color.WHITE)
                .setShadowWidth(3)
                .setBorderWidth(2)
                .setText("%05d")
                .setValue(0)
                .setPriority(20)
                .setTextAlign(TextObject.ALIGN_RIGHT)
                .setStickToCamera(true)
                .setMaterial(null)
                .setDebug(3);

        addEntity(score);

        TextObject heart = new TextObject("heart", 10, bufferResolution.getHeight() * 0.90)
                .setPhysicType(Entity.STATIC)
                .setShadowColor(new Color(0.2f, 0.2f, 0.2f, 0.6f))
                .setBorderColor(Color.BLACK)
                .setFont(getFont().deriveFont(16.0f))
                .setColor(Color.RED)
                .setShadowWidth(3)
                .setBorderWidth(2)
                .setText("\u2764")
                .setPriority(20)
                .setStickToCamera(true)
                .setMaterial(null);

        addEntity(heart);

        TextObject welcomeMessage = new TextObject("message",
                bufferResolution.getWidth() * 0.50,
                bufferResolution.getHeight() * 0.70)
                .setPhysicType(Entity.STATIC)
                .setTextAlign(TextObject.ALIGN_CENTER)
                .setShadowColor(new Color(0.2f, 0.2f, 0.2f, 0.6f))
                .setBorderColor(Color.BLACK)
                .setFont(getFont().deriveFont(12.0f))
                .setColor(Color.WHITE)
                .setShadowWidth(3)
                .setBorderWidth(2)
                .setText(messages.getString("app.title.welcome"))
                .setPriority(20)
                .setStickToCamera(true)
                .setDuration(5000)
                .setMaterial(null);

        addEntity(welcomeMessage);

        TextObject life = new TextObject("life", 20, bufferResolution.getHeight() * 0.90)
                .setPhysicType(Entity.STATIC)
                .setShadowColor(new Color(0.2f, 0.2f, 0.2f, 0.6f))
                .setBorderColor(Color.BLACK)
                .setFont(getFont().deriveFont(12.0f))
                .setColor(Color.WHITE)
                .setShadowWidth(3)
                .setBorderWidth(1)
                .setText("%d")
                .setValue(3)
                .setPriority(21)
                .setStickToCamera(true)
                .setDebug(2)
                .setMaterial(null);

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
                .setAttribute("speedRotStep", 0.01)
                .setDebug(2);
        addEntity(player);

        addEntity(
                createParticleSystem(world, "drop", 1000,
                        new ParticleBehavior<GameObject>() {
                            @Override
                            public GameObject create(World parentWorld, String particleNamePrefix, Entity<?> e) {
                                double x = (int) (Math.random() * parentWorld.playArea.getWidth());
                                double y = (int) (Math.random() * parentWorld.playArea.getHeight() * 0.1);
                                GameObject drop = new GameObject(
                                        String.format(particleNamePrefix + "_%d", GameObject.index),
                                        x,
                                        y,
                                        1, 1)
                                        .setPriority(1)
                                        .setType(Entity.TYPE_LINE)
                                        .setConstrainedToPlayArea(false)
                                        .setLayer((int) (Math.random() * 9) + 1)
                                        .setPhysicType(Entity.DYNAMIC)
                                        .setColor(Color.YELLOW)
                                        .setMaterial(Material.AIR)
                                        .setMass(110.0)
                                        .setParent(e)
                                        .setSpeed(0.0, Math.random() * 40.0 * parentWorld.gravity)
                                        .addBehavior(this);
                                return drop;
                            }

                            @Override
                            public void update(Entity<?> e, double elapsed) {
                                e.setColor(new Color((e.layer * 0.1f), (e.layer * 0.1f), (e.layer * 0.1f)));
                                if (!world.playArea.getBounds2D().contains(new Point2D.Double(e.x, e.y))) {
                                    e.setPosition(world.playArea.getWidth() * Math.random(),
                                            Math.random() * world.playArea.getHeight() * 0.1);
                                }
                            }
                        }));

        camera = new Camera("cam01", bufferResolution.width, bufferResolution.height);
        camera.setTarget(player);
        camera.setTween(0.05);
    }

    private GameObject createParticleSystem(
            World parentWorld,
            String particleNamePrefix,
            int nbParticles,
            ParticleBehavior<GameObject> b) {

        GameObject parentParticle = new GameObject(particleNamePrefix + "'s", 0, 0, 0, 0);
        List<GameObject> drops = new ArrayList<>();
        for (int i = 0; i < nbParticles; i++) {
            GameObject particle = b.create(world, particleNamePrefix, parentParticle);
            parentParticle.addChild(particle);
        }
        return parentParticle;
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

    private void updateEntity(Entity<?> e, double elapsed) {
        e.oldX = e.x;
        e.oldY = e.y;
        double gravity = (e.stickToCamera || e.physicType == Entity.NONE ? 0.0 : world.gravity);

        e.rotation += e.dRotation * elapsed;
        if (e.contact > 0) {
            e.dx *= e.material.roughness;
            e.dy *= e.material.roughness;
            e.dRotation *= e.material.roughness;
        } else {
            e.dx *= world.material.roughness;
            e.dy *= world.material.roughness;
            e.dRotation *= world.material.roughness;

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
                            drawEntityDebugInfo(g, e);
                        });
    }

    private void drawEntityDebugInfo(Graphics2D g, Entity<? extends Entity<?>> e) {
        if (debugLevel > 0 && debugLevel >= e.debug) {
            List<String> infos = e.getDebugInfo();
            int l = 0;
            float fontSize = 9f;
            g.setFont(g.getFont().deriveFont(fontSize));

            int maxWidth = infos.stream().mapToInt(s -> g.getFontMetrics().stringWidth(s)).max().orElse(0);
            int offsetX = (int) (e.x + maxWidth > ((e.stickToCamera ? 0 : camera.x) + camera.width) ? -(maxWidth + 4.0) : 4.0);
            int offsetY = (int) (e.y + (fontSize * infos.size()) > ((e.stickToCamera ? 0 : camera.y) + camera.height)
                    ? -(9.0 + (fontSize * infos.size()))
                    : -9.0);
            g.setColor(Color.ORANGE);
            for (String info : infos) {
                String levelStr = info.substring(0, info.indexOf("_"));
                int level = Integer.parseInt(levelStr);
                if (level <= debugLevel) {
                    g.drawString(info.substring(info.indexOf("_") + 1, info.length()),
                            (int) (e.getX() + e.getWidth() + offsetX),
                            (int) (e.getY() + offsetY + (l * fontSize)));
                    l++;
                }

            }
        }
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
        e.child.forEach(c -> entities.put(c.name, c));
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
        if (debugLevel > 3) {
            System.out.printf(">> <!> key typed: %s%n", e.getKeyChar());
        }
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
            // Request exiting game.
            case KeyEvent.VK_ESCAPE -> {
                requestExit();
            }
            // Change debug level
            case KeyEvent.VK_D -> {
                debugLevel = debugLevel + 1 <= 5 ? debugLevel + 1 : 0;
            }
            // Reverse gravity
            case KeyEvent.VK_G -> {
                world.gravity = -world.gravity;
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
