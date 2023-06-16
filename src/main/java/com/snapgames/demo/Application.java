package com.snapgames.demo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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

    public class Entity {
        String name;
        int x, y;
        int width, height;
        int dx, dy;
        private boolean active = true;

        int duration = -1;
        int life;

        Color color = Color.WHITE;
        Color fillColor = Color.RED;
        int priority = 1;

        Map<String, Object> attributes = new HashMap<>();

        public Entity(String n, int x, int y, int w, int h) {
            this.name = n;
            this.x = x;
            this.y = y;
            this.width = w;
            this.height = h;
            this.active = true;
        }

        public boolean isActive() {
            return this.active;
        }

        public void update(int elapsed) {
            x += dx * elapsed;
            y += dy * elapsed;
            if (life + elapsed > duration && duration != -1) {
                active = false;
            } else {
                life += elapsed;
            }
        }

        public void setActive(boolean active) {
            this.active = active;
            if (duration != -1) {
                life = duration;
            }
        }

        public void draw(Graphics2D g) {
            if (fillColor != null) {
                g.setColor(fillColor);
                g.fillRect(x, y, width, height);
            }
            if (color != null) {
                g.setColor(color);
                g.drawRect(x, y, width, height);
            }
        }

        public <T> void setAttribute(String attrName, T attrValue) {
            attributes.put(attrName, attrValue);
        }

        public <T> T getAttribute(String attrName, T defaultValue) {
            return (T) attributes.getOrDefault(attrName, defaultValue);
        }
    }

    private int FPS = 60;
    private int UPS = 120;

    private ResourceBundle messages;
    private Properties config = new Properties();
    private boolean exit = false;
    private boolean pause = false;
    private Dimension winSize;
    private Dimension bufferResolution;

    private String title = "no-title";
    private String version = "0.0.0";

    private JFrame frame;
    private BufferedImage buffer;

    private boolean keys[] = new boolean[1024];

    private Map<String, Entity> entities = new HashMap<>();

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
        frame.createBufferStrategy(3);
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
        exit = Boolean.parseBoolean(config.getProperty("app.exit"));
        title = messages.getString("app.name");
        version = messages.getString("app.version");
        String[] winSizeArgs = config.getProperty("app.window.size").split("x");
        winSize = new Dimension(
                Integer.parseInt(winSizeArgs[0]),
                Integer.parseInt(winSizeArgs[1]));
        String[] resoArgs = config.getProperty("app.render.resolution").split("x");
        bufferResolution = new Dimension(
                Integer.parseInt(resoArgs[0]),
                Integer.parseInt(resoArgs[1]));
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
        long frames = 0;
        long elapsedTime = 0;
        int fps = 0, ups = 0;
        int realFPS = 0, realUPS = 0;
        Map<String, Object> datastats = new HashMap<>();
        do {
            start = System.nanoTime();
            long elapsed = start - previous;

            input();
            ups += 1;
            if (ups < UPS && !pause) {
                update(elapsed);
            } else {
                ups = 0;
            }
            fps += 1;
            if (fps < FPS && !pause) {
                draw(datastats);
                frames++;
            } else {
                fps = 0;
            }
            previous = start;
            elapsedTime += elapsed;
            if (elapsedTime > 1000000000) {
                realFPS = fps;
                realUPS = ups;
                fps = 0;
                ups = 0;
                datastats.put("FPS", realFPS);
                datastats.put("UPS", realUPS);
            }
            try {
                Thread.sleep(1000 / UPS);
            } catch (InterruptedException e) {
                System.err.printf("Error while waiting for next update/frame %s%n",
                        e.getStackTrace().toString());
            }
        } while (!exit);
    }

    private void create() {
        Entity player = new Entity("player",
                (int) ((bufferResolution.getWidth() - 16) * 0.5),
                (int) ((bufferResolution.getHeight() - 16) * 0.5),
                16, 16);
        player.priority = 10;
        player.setAttribute("speedStep", 1);
        addEntity(player);
        addEntities(createStarfield(400));
    }

    private List<Entity> createStarfield(int nbStars) {
        List<Entity> stars = new ArrayList<>();
        for (int i = 0; i < nbStars; i++) {
            Entity star = new Entity("start_" + i,
                    (int) (Math.random() * bufferResolution.getWidth()),
                    (int) (Math.random() * bufferResolution.getHeight()),
                    1, 1);
            star.priority = 1;
            star.color = Color.YELLOW;
            stars.add(star);
        }
        return stars;
    }

    private void input() {
        Entity player = entities.get("player");
        int step = player.getAttribute("speedStep", 2);
        player.dx = 0;
        player.dy = 0;
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
    }

    private void update(long elapsed) {
        int time = (int) (elapsed * 0.0000001);
        entities.values().stream().filter(e -> e.isActive()).forEach(
                e -> {
                    e.update(time);
                    constrainToViewport(e);
                });
    }

    protected void constrainToViewport(Entity e) {
        if (e.x < 0) {
            e.x = 0;
        }
        if (e.x + e.width > buffer.getWidth()) {
            e.x = buffer.getWidth() - e.width;
        }
        if (e.y < 0) {
            e.y = 0;
        }
        if (e.y + e.height > buffer.getHeight()) {
            e.y = buffer.getHeight() - e.height;
        }
    }

    private void draw(Map<String, Object> datastats) {

        // prepare rendering buffer
        Graphics2D g = buffer.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        // clear buffer
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());

        // draw entities
        entities.values().stream().filter(e -> e.isActive()).sorted((a, b) -> a.priority > b.priority ? 1 : -1)
                .forEach(
                        e -> e.draw(g));
        g.dispose();

        // copy to JFrame
        Graphics2D gScreen = (Graphics2D) frame.getBufferStrategy().getDrawGraphics();
        gScreen.drawImage(
                buffer, 0, 0, frame.getWidth(), frame.getHeight(),
                0, 0, buffer.getWidth(), buffer.getHeight(),
                null);
        gScreen.setColor(Color.ORANGE);
        gScreen.drawString("fps:%d | ups:%d", 20, frame.getHeight() - 20);
        gScreen.dispose();

        frame.getBufferStrategy().show();
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

    public static void main(String[] argc) {
        Application app = new Application();
        app.run(argc);
    }

}
