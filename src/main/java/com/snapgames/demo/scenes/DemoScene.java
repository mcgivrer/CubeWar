package com.snapgames.demo.scenes;

import com.snapgames.core.Application;
import com.snapgames.core.behavior.ParticleBehavior;
import com.snapgames.core.entity.*;
import com.snapgames.core.input.InputHandler;
import com.snapgames.core.math.physic.Material;
import com.snapgames.core.math.physic.PhysicType;
import com.snapgames.core.math.physic.Vector2D;
import com.snapgames.core.math.physic.World;
import com.snapgames.core.scene.AbstractScene;
import com.snapgames.core.scene.Scene;
import com.snapgames.core.utils.config.Configuration;
import com.snapgames.core.utils.particles.ParticleSystemBuilder;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.Map;

/**
 * A {@link Scene} implementing a demonstration of capabilities for this
 * framework.
 *
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public class DemoScene extends AbstractScene {
    @Override
    public String getName() {
        return "demo";
    }

    @Override
    public void create(Application app) {
        Configuration configuration = app.getConfiguration();
        Graphics2D g2d = app.getRenderer().getBufferGraphics();
        World world = app.getPhysicEngine().getWorld();

        TextObject score = new TextObject("score")
                .setPosition(
                        configuration.bufferResolution.getWidth() * 0.98, 32)
                .setShadowColor(new Color(0.2f, 0.2f, 0.2f, 0.6f))
                .setPhysicType(PhysicType.STATIC)
                .setBorderColor(Color.BLACK)
                .setFont(g2d.getFont().deriveFont(20.0f))
                .setColor(Color.WHITE)
                .setShadowWidth(3)
                .setBorderWidth(2)
                .setText("%05d")
                .setValue(0)
                .setPriority(20)
                .setTextAlign(TextObject.ALIGN_RIGHT)
                .setStickToCameraView(true)
                .setMaterial(null)
                .setDebug(3);

        addEntity(score);

        TextObject heart = new TextObject("heart")
                .setPosition(10, configuration.bufferResolution.getHeight() * 0.90)
                .setPhysicType(PhysicType.STATIC)
                .setShadowColor(new Color(0.2f, 0.2f, 0.2f, 0.6f))
                .setBorderColor(Color.BLACK)
                .setFont(g2d.getFont().deriveFont(16.0f))
                .setColor(Color.RED)
                .setShadowWidth(3)
                .setBorderWidth(2)
                .setText("❤")
                .setPriority(20)
                .setStickToCameraView(true)
                .setMaterial(null);

        addEntity(heart);

        TextObject life = new TextObject("life")
                .setPosition(20, configuration.bufferResolution.getHeight() * 0.90)
                .setPhysicType(PhysicType.STATIC)
                .setShadowColor(new Color(0.2f, 0.2f, 0.2f, 0.6f))
                .setBorderColor(Color.BLACK)
                .setFont(g2d.getFont().deriveFont(12.0f))
                .setColor(Color.WHITE)
                .setShadowWidth(3)
                .setBorderWidth(1)
                .setText("%d")
                .setValue(3)
                .setPriority(21)
                .setStickToCameraView(true)
                .setDebug(2)
                .setMaterial(null);

        addEntity(life);

        TextObject welcomeMessage = new TextObject("message")
                .setPosition(
                        configuration.bufferResolution.getWidth() * 0.50,
                        configuration.bufferResolution.getHeight() * 0.70)
                .setPhysicType(PhysicType.STATIC)
                .setTextAlign(TextObject.ALIGN_CENTER)
                .setShadowColor(new Color(0.2f, 0.2f, 0.2f, 0.6f))
                .setBorderColor(Color.BLACK)
                .setFont(g2d.getFont().deriveFont(12.0f))
                .setColor(Color.WHITE)
                .setShadowWidth(3)
                .setBorderWidth(2)
                .setText(app.getMessages().getString("app.title.welcome"))
                .setPriority(20)
                .setStickToCameraView(true)
                .setDuration(5)
                .setDebug(2)
                .setMaterial(null);

        addEntity(welcomeMessage);

        TextObject pauseObj = new TextObject("pause")
                .setPosition(
                        configuration.bufferResolution.getWidth() * 0.50,
                        configuration.bufferResolution.getHeight() * 0.50)
                .setPhysicType(PhysicType.STATIC)
                .setTextAlign(TextObject.ALIGN_CENTER)
                .setShadowColor(new Color(0.2f, 0.2f, 0.2f, 0.6f))
                .setBorderColor(Color.BLACK)
                .setFont(g2d.getFont().deriveFont(12.0f))
                .setColor(Color.WHITE)
                .setShadowWidth(3)
                .setBorderWidth(2)
                .setText(app.getMessages().getString("app.pause.message"))
                .setPriority(20)
                .setStickToCameraView(true)
                .setMaterial(null)
                .setActive(false)
                .addBehavior((e, elapsed) -> e.setActive(app.isPause()));

        addEntity(pauseObj);

        GameObject player = new GameObject("player")
                .setPosition(
                        configuration.world.getPlayArea().getWidth() * 0.50,
                        configuration.world.getPlayArea().getHeight() * 0.50)
                .setSize(16, 16)
                .setType(GameObject.TYPE_RECTANGLE)
                .setPhysicType(PhysicType.DYNAMIC)
                .setPriority(10)
                .setColor(Color.WHITE)
                .setFillColor(Color.GREEN)
                .setMass(60.0)
                .setMaterial(Material.SUPER_BALL)
                .setAttribute("speedStep", 0.01)
                .setAttribute("jumpFactor", 99.601)
                .setAttribute("speedRotStep", 0.001)
                .setDebug(2);
        addEntity(player);

        addEntity(
                ParticleSystemBuilder.createParticleSystem(world, "drop", 1000,
                        new ParticleBehavior<>() {
                            @Override
                            public GameObject create(World parentWorld, double elapsed, String particleNamePrefix,
                                                     Entity<?> e) {

                                return new GameObject(
                                        particleNamePrefix + "_" + GameObject.index)
                                        .setPosition(
                                                Math.random() * parentWorld.getPlayArea().getWidth(),
                                                Math.random() * parentWorld.getPlayArea().getHeight() * 0.1)
                                        .setSize(1, 1)
                                        .setPriority(1)
                                        .setType(GameObject.TYPE_LINE)
                                        .setConstrainedToPlayArea(false)
                                        // set depth to the rain drop.
                                        .setLayer((int) (Math.random() * 9) + 1)
                                        .setPhysicType(PhysicType.DYNAMIC)
                                        .setColor(Color.YELLOW)
                                        .setMaterial(Material.WATER)
                                        .setMass(1.0)
                                        .setParent(e)
                                        .addBehavior(this)
                                        .addForce(new Vector2D(0.0, Math.random() * 0.0003));
                            }

                            /**
                             * Update the Entity e according to the elapsed time since previous call.
                             *
                             * @param e       the Entity to be updated
                             * @param elapsed the elapsed time since previous call.
                             */
                            @Override
                            public void update(Entity<?> e, double elapsed) {
                                e.setColor(new Color((0.1f), (0.3f), (e.layer * 0.1f), 0.8f));
                                if (!world.getPlayArea().getBounds2D().contains(new Point2D.Double(e.x, e.y))) {
                                    e.setPosition(world.getPlayArea().getWidth() * Math.random(),
                                            Math.random() * world.getPlayArea().getHeight() * 0.1);
                                    e.setOldPosition(e.x, e.y);

                                }
                                GameObject parent = (GameObject) e.parent;
                                double time = parent.getAttribute("particleTime", 0.0);
                                double particleTimeCycle = parent.getAttribute("particleTimeCycle", 9800.0);
                                double particleFreq = parent.getAttribute("particleFreq", 0.005);
                                time += elapsed;
                                int nbP = parent.getAttribute("nbParticles", 0);
                                if (parent.getChild().size() < nbP && time > particleTimeCycle) {
                                    for (int i = 0; i < nbP * particleFreq; i++) {
                                        GameObject particle = this.create(world, 0, parent.name, parent);
                                        parent.addChild(particle);
                                        addEntity(particle);
                                    }
                                    time = 0;
                                }
                                parent.setAttribute("particleTime", time);
                            }
                        }));

        Camera cam = new Camera("cam01", configuration.bufferResolution.width, configuration.bufferResolution.height);
        cam.setTarget(player);
        cam.setTween(0.5);
        addCamera(cam);

    }

    @Override
    public void input(Application app, InputHandler ih) {
        Entity<?> player = getEntity("player");
        boolean moving = false;
        // player moves
        double step = player.getAttribute("speedStep", 0.0005);
        double jumpFactor = player.getAttribute("jumpFactor", 10.0);
        double rotStep = player.getAttribute("speedRotStep", 0.01);

        if (ih.ctrlKey)
            step = step * 4.0;
        if (ih.shiftKey)
            step = step * 2.0;

        // player rotation
        if (ih.altKey) {
            if (ih.isKeyPressed(KeyEvent.VK_UP)) {
                player.setRotationSpeed(-rotStep);
            }
            if (ih.isKeyPressed(KeyEvent.VK_DOWN)) {
                player.setRotationSpeed(+rotStep);
            }
            if (ih.isKeyPressed(KeyEvent.VK_DELETE)) {
                player.setRotationSpeed(0.0);
                player.setRotation(0.0);
            }
        } else {
            if (ih.isKeyPressed(KeyEvent.VK_UP)) {
                player.addForce(new Vector2D(0.0, -step * jumpFactor));
                moving = true;
            }
            if (ih.isKeyPressed(KeyEvent.VK_DOWN)) {
                player.addForce(new Vector2D(0.0, step));
                moving = true;
            }
        }

        if (ih.isKeyPressed(KeyEvent.VK_LEFT)) {
            player.addForce(new Vector2D(-step, 0.0));
            moving = true;
        }
        if (ih.isKeyPressed(KeyEvent.VK_RIGHT)) {
            player.addForce(new Vector2D(step, 0.0));
            moving = true;
        }

        // camera rotation
        if (ih.isKeyPressed(KeyEvent.VK_PAGE_UP)) {
            getActiveCamera().setRotationSpeed(0.001);
        }
        if (ih.isKeyPressed(KeyEvent.VK_PAGE_DOWN)) {
            getActiveCamera().setRotationSpeed(-0.001);
        }
        if (ih.isKeyPressed(KeyEvent.VK_DELETE)) {
            getActiveCamera().setRotationSpeed(0.0);
            getActiveCamera().setRotation(0.0);
        }
        if (!moving) {
            player.vel = player.vel.multiply(player.getMaterial().getRoughness());
        }
    }

    @Override
    public void update(Application app, double elapsed) {
        // nothing for this demo.
    }

    @Override
    public void draw(Application app, Graphics2D g, Map<String, Object> stats) {
        // nothing for this demo.
    }

    @Override
    public void setWorld(World world) {
        world.add(
                new Perturbation("wind",
                        0, 0,
                        (int) world.getPlayArea().getWidth(),
                        (int) world.getPlayArea().getHeight())
                        .setSpeed(new Vector2D(0.5, 0.5)));
    }

}
