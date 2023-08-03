package com.snapgames.core.graphics;

import static com.snapgames.core.utils.StringUtils.prepareStatsString;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.snapgames.core.Application;
import com.snapgames.core.entity.Camera;
import com.snapgames.core.entity.Entity;
import com.snapgames.core.graphics.plugins.GameObjectRendererPlugin;
import com.snapgames.core.graphics.plugins.PerturbationRendererPlugin;
import com.snapgames.core.graphics.plugins.RendererPlugin;
import com.snapgames.core.graphics.plugins.TextObjectRendererPlugin;
import com.snapgames.core.input.InputHandler;
import com.snapgames.core.math.physic.World;
import com.snapgames.core.math.physic.entity.Perturbation;
import com.snapgames.core.scene.Scene;
import com.snapgames.core.system.GSystem;

/**
 * The {@link Renderer} service will draw all entities from the {@link Scene}.
 *
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public class Renderer extends JPanel implements GSystem {

    private final Application application;
    /**
     * Graphics components
     */
    private JFrame frame;
    private BufferedImage buffer;
    private boolean drawing = true;
    private static int sc_index;

    private Map<Class<?>, RendererPlugin<? extends Entity>> plugins = new HashMap<>();

    public Renderer(Application app) {
        this.application = app;

    }

    private void addPlugin(RendererPlugin<?> rendererPlugin) {
        this.plugins.put(rendererPlugin.getEntityClass(), rendererPlugin);
    }

    /**
     * Create the {@link Application}'s window, according to the defined
     * configuration attributes.
     */
    public void createWindow(InputHandler ih) {
        frame = new JFrame(application.title);
        setPreferredSize(application.getConfiguration().winSize);
        setMinimumSize(application.getConfiguration().winSize);
        setSize(application.getConfiguration().winSize);
        frame.setContentPane(this);
        frame.setLayout(new GridLayout());
        frame.enableInputMethods(true);
        frame.setFocusTraversalKeysEnabled(false);
        frame.setIgnoreRepaint(true);
        frame.requestFocus();
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                application.requestExit();
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.createBufferStrategy(2);
        frame.addKeyListener(ih);
        clearWindow(frame);

        buffer = new BufferedImage(
            application.getConfiguration().bufferResolution.width,
            application.getConfiguration().bufferResolution.height,
            BufferedImage.TYPE_INT_ARGB);
    }

    private void clearWindow(JFrame frame) {
        frame.setBackground(Color.BLACK);
        frame.getGraphics().fillRect(0, 0, frame.getWidth(), frame.getHeight());
    }

    /**
     * Draw all {@link Application} the Entities on window.
     *
     * @param stats a set of metadata to be displayed on screen as debug
     *              purpose. (only if Application#debug >0)
     */
    public void draw(World world, Scene scene, Map<String, Object> stats) {
        if (drawing) {
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
            Camera cam = scene.getActiveCamera();
            moveFromCameraPoV(g, cam, -1);
            drawGrid(g, world.getPlayArea());
            g.setColor(Color.BLUE);
            g.draw(world.getPlayArea());
            moveFromCameraPoV(g, cam, 1);

            // draw everything to be drawn
            drawEntities(g, scene,
                // join all Entity in Scene and all Perturbation in World.
                Stream.concat(scene.getEntities().stream(), world.getPerturbations().stream())
                    .filter(e -> e.isEnabled()
                        // object in the camera viewport and not stick to camera
                        && (((cam != null
                        && cam.inViewport(e)
                        && !e.stickToCamera)
                        // object stick to camera
                        || (e.stickToCamera)
                        // object is a Perturbation instance
                        || e.getClass().isAssignableFrom(Perturbation.class))
                        // Scene has no camera !
                        || cam == null))
                    .collect(Collectors.toList()));
            scene.draw(application, g, stats);
            g.dispose();

            // copy to JFrame
            Graphics2D gScreen = (Graphics2D) frame.getBufferStrategy().getDrawGraphics();
            gScreen.drawImage(
                buffer, 0, 0, frame.getWidth(), frame.getHeight(),
                0, 0, buffer.getWidth(), buffer.getHeight(),
                null);
            if (application.getConfiguration().debug && application.getConfiguration().debugLevel > 0) {
                gScreen.setColor(Color.ORANGE);
                gScreen.drawString(
                    prepareStatsString(stats, "[ ", " | ", " ]"),
                    20, frame.getHeight() - 20);
            }
            gScreen.dispose();
            // switch to next available drawing buffer
            frame.getBufferStrategy().show();
        }
    }

    private void drawEntities(Graphics2D g, Scene scene, List<Entity> list) {
        list.stream().filter(e -> e.isEnabled())
            .sorted(Comparator.comparingInt((Entity a) -> a.getLayer() * 1000 + a.getPriority()).reversed())
            .forEach(
                e -> {

                    if (!e.stickToCamera) moveFromCameraPoV(g, scene.getActiveCamera(), -1);
                    RendererPlugin rp = plugins.get(e.getClass());
                    g.rotate(-e.rotation,
                        e.pos.x + e.width * 0.5,
                        e.pos.y + e.height * 0.5);
                    rp.draw(this, g, e);
                    e.setDrawnBy(rp.getClass());
                    g.rotate(e.rotation,
                        e.pos.x + e.width * 0.5,
                        e.pos.y + e.height * 0.5);
                    rp.drawDebugInfo(application, scene, this, g, e);

                    if (application.isDebugAtLeast(5)) {
                        System.out.printf(">> <d> draw entity %s with %s%n", e.getName(),
                            rp.getClass().getSimpleName());
                    }

                    if (!e.stickToCamera) moveFromCameraPoV(g, scene.getActiveCamera(), 1);
                });
    }

    public void moveFromCameraPoV(Graphics2D g, Camera camera, double direction) {
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

    public Graphics2D getBufferGraphics() {
        return (Graphics2D) buffer.getGraphics();
    }

    public void dispose() {
        if (Optional.ofNullable(frame).isPresent()) {
            frame.dispose();
        }

    }

    public void takeScreenShot() {
        this.drawing = false;
        if (Optional.ofNullable(buffer).isPresent()) {
            // TODO implement buffer image save to file.
        }
        this.drawing = true;
    }

    @Override
    public Class<? extends GSystem> getSystemName() {
        return Renderer.class;
    }

    @Override
    public void initialize(Application app) {
        addPlugin(new GameObjectRendererPlugin());
        addPlugin(new TextObjectRendererPlugin());
        addPlugin(new PerturbationRendererPlugin());
    }

    public JFrame getWindow() {
        return frame;
    }
}
