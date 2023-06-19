package com.snapgames.demo;

import java.io.IOException;
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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * main class for project Test002
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
    public class Entity {
        final static int STATIC = 1;
        final static int DYNAMIC = 2;
        final static int TYPE_POINT = 1;
        final static int TYPE_LINE = 2;
        final static int TYPE_RECTANGULE = 3;
        final static int TYPE_ELLIPSE = 4;
        String name;
        double x, y;
        int width, height;
        double dx, dy;
        private boolean active = true;

        int duration = -1;
        int life;

        Color color = Color.WHITE;
        Color fillColor = Color.RED;
        int priority = 1;

        Map<String, Object> attributes = new HashMap<>();
        public int physicType;
        public int type;
        public int layer;
        public boolean constrainedToPlayArea = true;

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
            this.type = TYPE_RECTANGULE;
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
        public void setActive(boolean active) {
            this.active = active;
            if (duration != -1) {
                life = duration;
            }
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
                case TYPE_RECTANGULE -> {
                    if (fillColor != null) {
                        g.setColor(fillColor);
                        g.fillRect((int) x, (int) y, width, height);
                    }
                    if (color != null) {
                        g.setColor(color);
                        g.drawRect((int) x, (int) y, width, height);
                    }
                }
                case TYPE_ELLIPSE -> {
                    if (fillColor != null) {
                        g.setColor(fillColor);
                        g.fillRect((int) x, (int) y, width, height);
                    }
                    if (color != null) {
                        g.setColor(color);
                        g.drawRect((int) x, (int) y, width, height);
                    }
                }
                default -> {
                    System.err.printf("Unknown Entity type %d%n", type);
                }
            }
        }

        /**
         * Ajoute un attribut à l'entité.
         * 
         * @param key   la clé de l'attribut
         * @param value la valeur de l'attribut
         */
        public <T> void setAttribute(String attrName, T attrValue) {
            attributes.put(attrName, attrValue);
        }

        /**
         * Récupère la valeur de l'attribut spécifié.
         * 
         * @param key la clé de l'attribut
         * @return la valeur de l'attribut, ou null si l'attribut n'existe pas
         */
        public <T> T getAttribute(String attrName, T defaultValue) {
            return (T) attributes.getOrDefault(attrName, defaultValue);
        }

        public void setSpeed(double dx, double dy) {
            this.dx = dx;
            this.dy = dy;
        }

        public Rectangle2D getBounds2D() {
            return new Rectangle2D.Double(x, y, width, height);
        }

        public String toString() {
            return String.format("%s:pos[%.02f,%.02f]", name, x, y);
        }
    }

    /**
     * La {@link Camera} qui permet de suivre une entité {@link Entity}.
     */
    public class Camera extends Entity {

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
            this.x += (target.x - ((this.width - target.width) * 0.5) - this.x) * tween * elapsed;
            this.y += (target.y - ((this.height - target.height) * 0.5) - this.y) * tween * elapsed;
        }
    }

    public class TextEntity extends Entity {
        String text;
        Font font;
        Object value;
        int shadowWidth;
        Color shadowColor;
        int borderWidth;
        Color borderColor;

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
            if (shadowWidth > 0 && Optional.ofNullable(shadowColor).isPresent()) {
                drawShadowText(g, textValue, x, y);
            }
            if (borderWidth > 0 && Optional.ofNullable(borderColor).isPresent()) {
                drawBorderText(g, textValue, x, y);
            }
            g.setColor(color);
            g.drawString(textValue, (int) x, (int) y);
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

    }

    private int FPS = 60;
    private int UPS = 120;

    private ResourceBundle messages;
    private Properties config = new Properties();
    private boolean exit = false;
    private int debug = 0;
    private boolean pause = false;
    private Dimension winSize;
    private Dimension bufferResolution;
    private double maxEntitySpeed;

    private String title = "no-title";
    private String version = "0.0.0";

    private JFrame frame;
    private BufferedImage buffer;

    private Rectangle2D playArea;
    private Camera camera;

    private boolean keys[] = new boolean[1024];

    private Map<String, Entity> entities = new HashMap<>();
    private boolean ctrlKey;
    private boolean shiftKey;
    private boolean altKey;
    private boolean metaKey;

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
        try {
            config.load(this.getClass().getResourceAsStream("/config.properties"));
            parseConfig(config);
        } catch (IOException e) {
            System.err.println(String.format("unable to read configuration file: %s", e.getMessage()));
        }

        parseArgs(lArgs);
        System.out.printf("Initialization application %s (%s)%n",
                title,
                version);
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

    private void parseConfig(Properties config) {
        // debug level (0-5 where 0=off and 5 max debug info)
        debug = Integer.parseInt(config.getProperty("app.debug", "0"));
        // exit flag to let test only ONE loop execution.
        exit = Boolean.parseBoolean(config.getProperty("app.exit"));
        // Window size
        // file deepcode ignore CallOnNull: <please specify a reason of ignoring this>
        String[] winSizeArgs = config.getProperty("app.window.size", "640x400").split("x");
        winSize = new Dimension(
                Integer.parseInt(winSizeArgs[0]),
                Integer.parseInt(winSizeArgs[1]));
        // resolution
        String[] resoArgs = config.getProperty("app.render.resolution", "320x200").split("x");
        bufferResolution = new Dimension(
                Integer.parseInt(resoArgs[0]),
                Integer.parseInt(resoArgs[1]));
        // play area
        String[] paArgs = config.getProperty("app.physic.playarea", "1000x1000").split("x");
        playArea = new Rectangle2D.Double(
                0, 0,
                Integer.parseInt(paArgs[0]),
                Integer.parseInt(paArgs[1]));
        // Maximum speed for Entity.
        maxEntitySpeed = Double.parseDouble(config.getProperty("app.physic.speed.max", "16.0"));

        // --- Translated information ---
        // Applicaiton name.
        title = Optional.of(messages.getString("app.name")).orElse("-Test002-");
        // version of the application.
        version = Optional.of(messages.getString("app.version")).orElse("-1.0.0-");

    }

    private void parseArgs(List<String> lArgs) {
        lArgs.forEach(s -> {
            System.out.println(String.format("- arg: %s", s));
            String[] arg = s.split("=");
            switch (arg[0]) {
                case "x", "exit" -> {
                    exit = Boolean.parseBoolean(arg[1]);
                    System.out.println(String.format("argument exit set to %s", arg[1]));
                }
                case "t", "title" -> {
                    title = arg[1];
                    System.out.println(String.format("argument title set to %s", arg[1]));
                }
                default -> {
                    System.err.println(String.format("unknown argument: %s in %s", arg[0], s));
                }
            }
        });
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

    private void create() {
        TextEntity score = new TextEntity("score", bufferResolution.getWidth() * 0.80, 32);
        score.shadowColor = new Color(0.2f, 0.2f, 0.2f, 0.6f);
        score.borderColor = Color.BLACK;
        score.font = getFont().deriveFont(20.0f);
        score.color = Color.WHITE;
        score.shadowWidth = 3;
        score.borderWidth = 2;
        score.text = "%05d";
        score.value = 0;
        score.priority = 20;
        score.physicType = Entity.STATIC;

        addEntity(score);

        TextEntity life = new TextEntity("life", 10, bufferResolution.getHeight() * 0.90);
        life.shadowColor = new Color(0.2f, 0.2f, 0.2f, 0.6f);
        life.borderColor = Color.BLACK;
        life.font = getFont().deriveFont(14.0f);
        life.color = Color.WHITE;
        life.shadowWidth = 3;
        life.borderWidth = 2;
        life.text = "%02d";
        life.value = 3;
        life.priority = 20;
        life.physicType = Entity.STATIC;

        addEntity(life);

        Entity player = new Entity("player",
                (int) ((bufferResolution.getWidth() - 16) * 0.5),
                (int) ((bufferResolution.getHeight() - 16) * 0.5),
                16, 16);
        player.priority = 10;
        player.setAttribute("speedStep", 1);
        addEntity(player);

        addEntities(createStarfield("star_%d", 2000));

        camera = new Camera("cam01", bufferResolution.width, bufferResolution.height);
        camera.setTarget(player);
        camera.setTween(0.02);
    }

    private List<Entity> createStarfield(String namePrefix, int nbStars) {
        List<Entity> stars = new ArrayList<>();
        for (int i = 0; i < nbStars; i++) {
            double x = Math.random() * (playArea.getWidth() * 3);
            double y = Math.random() * (playArea.getHeight() * 3);
            Entity star = new Entity(
                    String.format(namePrefix, i),
                    x - playArea.getWidth(),
                    y - playArea.getHeight(),
                    1, 1);
            star.priority = 1;
            star.constrainedToPlayArea = false;
            star.layer = (int) (Math.random() * 5) + 1;
            star.physicType = Entity.DYNAMIC;
            star.color = Color.YELLOW;
            star.setSpeed(0.2, 0.2);
            stars.add(star);
        }
        return stars;
    }

    private void input() {
        Entity player = entities.get("player");
        int step = player.getAttribute("speedStep", 2);
        if (ctrlKey)
            step = step * 4;
        if (shiftKey)
            step = step * 2;

        if (keys[KeyEvent.VK_UP]) {
            player.dy = -step;
        }
        if (keys[KeyEvent.VK_DOWN]) {
            player.dy = step;

        }
        if (keys[KeyEvent.VK_LEFT]) {
            player.dx = -step;

        }
        if (keys[KeyEvent.VK_RIGHT]) {
            player.dx = step;
        }
        player.dx *= 0.98;
        player.dy *= 0.98;
        entities.values().stream()
                .filter(e -> e.name.startsWith("star_"))
                .forEach(e -> {
                    e.setSpeed(-player.dx * (e.layer * 0.2), -player.dy * (e.layer * 0.2));
                    e.color = new Color((e.layer * 0.2f), (e.layer * 0.2f), (e.layer * 0.2f));
                });
    }

    private void update(long elapsed, Map<String, Object> datastats) {
        int time = (int) (elapsed * 0.0000001);
        entities.values().stream().filter(e -> e.isActive()).forEach(
                e -> {
                    updateEntity(e, time);
                    constrainToViewport(e);
                });
        if (Optional.ofNullable(camera).isPresent()) {
            camera.update(time);
        }
        long renderedEntities = entities.values().stream()
                .filter(e -> e.isActive())
                .filter(e -> inViewport(camera, e) || e.physicType == Entity.STATIC)
                .sorted((a, b) -> a.priority > b.priority ? 1 : -1).count();
        datastats.put("5_rend", renderedEntities);
    }

    private void updateEntity(Entity e, int elapsed) {
        constrainToPhysic(e);
        e.x += e.dx * elapsed;
        e.y += e.dy * elapsed;
        e.update(elapsed);
    }

    private void constrainToPhysic(Entity e) {
        // maximize speed.
        if (Math.abs(e.dx) > maxEntitySpeed) {
            e.dx = Math.signum(e.dx) * maxEntitySpeed;
        }
        if (Math.abs(e.dy) > maxEntitySpeed) {
            e.dy = Math.signum(e.dy) * maxEntitySpeed;
        }
    }

    protected void constrainToViewport(Entity e) {
        if (!e.constrainedToPlayArea)
            return;
        if (e.x < 0) {
            e.x = 0;
        }
        if (e.x + e.width > playArea.getWidth()) {
            e.x = playArea.getWidth() - e.width;
        }
        if (e.y < 0) {
            e.y = 0;
        }
        if (e.y + e.height > playArea.getHeight()) {
            e.y = playArea.getHeight() - e.height;
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
        moveFromCameraPoV(g, -1);
        drawGrid(g, playArea);
        g.setColor(Color.BLUE);
        g.draw(playArea);
        moveFromCameraPoV(g, 1);

        // draw entities
        entities.values().stream()
                .filter(e -> e.isActive())
                .filter(e -> inViewport(camera, e) || e.physicType == Entity.STATIC)
                .sorted((a, b) -> a.priority > b.priority ? 1 : -1)
                .forEach(
                        e -> {
                            if (e.physicType != Entity.STATIC) {
                                moveFromCameraPoV(g, -1);
                            }
                            e.draw(g);
                            if (e.physicType != Entity.STATIC) {
                                moveFromCameraPoV(g, 1);
                            }
                        });
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

    private void moveFromCameraPoV(Graphics2D g, double direction) {
        if (camera != null) {
            g.translate(direction * camera.x, direction * camera.y);
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

    private boolean inViewport(Camera cam, Entity e) {
        return cam.getBounds2D().contains(e.getBounds2D());
    }

    private void dispose() {
        entities.clear();
        frame.dispose();
        System.out.printf("End of application %s%n", title);
    }

    private Entity addEntity(Entity e) {
        return entities.put(e.name, e);
    }

    private void addEntities(List<Entity> listEntities) {
        for (Entity e : listEntities) {
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
     *         {@link java.util.Map.Entry}.
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
