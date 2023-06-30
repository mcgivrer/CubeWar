package com.snapgames.demo.graphics;

import com.snapgames.demo.Application;
import com.snapgames.demo.entity.Camera;
import com.snapgames.demo.entity.Entity;
import com.snapgames.demo.input.InputHandler;
import com.snapgames.demo.math.physic.World;
import com.snapgames.demo.scene.Scene;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.snapgames.demo.utils.StringUtils.prepareStatsString;

/**
 * The {@link Renderer} service will draw all entities from the {@link Scene}.
 *
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public class Renderer extends JPanel {


    private final Application application;
    /**
     * Graphics components
     */
    private JFrame frame;
    private BufferedImage buffer;

    public Renderer(Application app) {
        this.application = app;
    }

    /**
     * Create tthe {@link Application}'s window, according to the defined
     * configuration attributes.
     *
     * @return a new created JFrame, window of the {@link Application}.
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
     * Draw all {@link Application} the Entityies on window.
     *
     * @param stats a set of metadata to be displayed on screen as debug
     *              purpose. (only if Application#debug >0)
     */
    public void draw(World world, Scene scene, Map<String, Object> stats) {

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
        moveFromCameraPoV(g, scene.getActiveCamera(), -1);
        drawGrid(g, world.getPlayArea());
        g.setColor(Color.BLUE);
        g.draw(world.getPlayArea());
        moveFromCameraPoV(g, scene.getActiveCamera(), 1);

        // draw entities
        moveFromCameraPoV(g, scene.getActiveCamera(), -1);
        drawAllEntities(g, scene, false);
        moveFromCameraPoV(g, scene.getActiveCamera(), 1);

        // draw all entities that are stick to Camera viewport.
        drawAllEntities(g, scene, true);

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
        // switch to next availbale drawing buffer
        frame.getBufferStrategy().show();
    }

    private void drawAllEntities(Graphics2D g, Scene scene, boolean stickToCamera) {
        scene.getEntities().stream()
                .filter(e -> e.isActive() && e.stickToCamera == stickToCamera)
                .filter(e -> scene.getActiveCamera().inViewport(e) || e.physicType == Entity.STATIC)
                .sorted(Comparator.comparingInt(a -> a.getPriority()))
                .forEach(
                        e -> {
                            g.rotate(-e.rotation,
                                    e.pos.x + e.width * 0.5,
                                    e.pos.y + e.height * 0.5);
                            e.draw(g);
                            g.rotate(e.rotation,
                                    e.pos.x + e.width * 0.5,
                                    e.pos.y + e.height * 0.5);
                            drawEntityDebugInfo(g, scene, e);
                        });
    }

    private void drawEntityDebugInfo(Graphics2D g, Scene scene, Entity<? extends Entity<?>> e) {
        if (application.getConfiguration().debugLevel > 0 && application.getConfiguration().debugLevel >= e.debug) {
            List<String> infos = e.getDebugInfo();
            int l = 0;
            float fontSize = 9f;
            g.setFont(g.getFont().deriveFont(fontSize));

            int maxWidth = infos.stream().mapToInt(s -> g.getFontMetrics().stringWidth(s)).max().orElse(0);
            int offsetX = (int) (e.pos.x + maxWidth > (
                    (e.stickToCamera ? 0 : scene.getActiveCamera().x) + scene.getActiveCamera().width) ? -(maxWidth + 4.0)
                    : 4.0);
            int offsetY = (int) (e.pos.y + (fontSize * infos.size()) >
                    ((e.stickToCamera ? 0 : scene.getActiveCamera().y) + scene.getActiveCamera().height)
                    ? -(9.0 + (fontSize * infos.size()))
                    : -9.0);
            g.setColor(Color.ORANGE);
            for (String info : infos) {
                String levelStr = info.substring(0, info.indexOf("_"));
                int level = Integer.parseInt(levelStr);
                if (level <= application.getConfiguration().debugLevel) {
                    g.drawString(info.substring(info.indexOf("_") + 1, info.length()),
                            (int) (e.pos.x + e.getWidth() + offsetX),
                            (int) (e.pos.y + offsetY + (l * fontSize)));
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

    public Graphics2D getBufferGraphics() {
        return (Graphics2D) buffer.getGraphics();
    }

    public void dispose() {
        if (Optional.ofNullable(frame).isPresent()) {
            frame.dispose();
        }

    }
}
