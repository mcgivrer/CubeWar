package com.snapgames.demo.scene;

import com.snapgames.demo.Application;
import com.snapgames.demo.behavior.Behavior;
import com.snapgames.demo.behavior.ParticleBehavior;
import com.snapgames.demo.entity.Camera;
import com.snapgames.demo.entity.Entity;
import com.snapgames.demo.entity.GameObject;
import com.snapgames.demo.entity.TextObject;
import com.snapgames.demo.input.InputHandler;
import com.snapgames.demo.math.physic.Material;
import com.snapgames.demo.math.physic.Vector2D;
import com.snapgames.demo.math.physic.World;
import com.snapgames.demo.utils.config.Configuration;
import com.snapgames.demo.utils.particles.ParticleSystemBuilder;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

public class DemoScene extends AbstractScene implements Scene {
    @Override
    public String getName() {
        return "demo";
    }

    @Override
    public void create(Application app) {
        Configuration configuration = app.getConfiguration();
        Graphics2D g2d = (Graphics2D) app.getRenderer().getBufferGraphics();
        World world = app.getPhysicEngine().getWorld();

        TextObject score = new TextObject("score", configuration.bufferResolution.getWidth() * 0.98, 32)
                .setShadowColor(new Color(0.2f, 0.2f, 0.2f, 0.6f))
                .setPhysicType(Entity.STATIC)
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

        TextObject heart = new TextObject("heart", 10, configuration.bufferResolution.getHeight() * 0.90)
                .setPhysicType(Entity.STATIC)
                .setShadowColor(new Color(0.2f, 0.2f, 0.2f, 0.6f))
                .setBorderColor(Color.BLACK)
                .setFont(g2d.getFont().deriveFont(16.0f))
                .setColor(Color.RED)
                .setShadowWidth(3)
                .setBorderWidth(2)
                .setText("\u2764")
                .setPriority(20)
                .setStickToCameraView(true)
                .setMaterial(null);

        addEntity(heart);

        TextObject life = new TextObject("life", 20, configuration.bufferResolution.getHeight() * 0.90)
                .setPhysicType(Entity.STATIC)
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

        TextObject welcomeMessage = new TextObject("message",
                configuration.bufferResolution.getWidth() * 0.50,
                configuration.bufferResolution.getHeight() * 0.70)
                .setPhysicType(Entity.STATIC)
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
                .setDuration(5000)
                .setMaterial(null);

        addEntity(welcomeMessage);

        TextObject pauseObj = new TextObject("pause",
                configuration.bufferResolution.getWidth() * 0.50,
                configuration.bufferResolution.getHeight() * 0.50)
                .setPhysicType(Entity.STATIC)
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
                .addBehavior(new Behavior<TextObject>() {
                    @Override
                    public void update(Entity<?> e, double elapsed) {
                        e.setActive(app.isPause());
                    }
                });

        addEntity(pauseObj);

        GameObject player = new GameObject("player",
                (int) ((configuration.bufferResolution.getWidth() - 16) * 0.5),
                (int) ((configuration.bufferResolution.getHeight() - 16) * 0.5),
                16, 16)
                .setPhysicType(Entity.DYNAMIC)
                .setPriority(10)
                .setMass(60.0)
                .setMaterial(Material.RUBBER)
                .setAttribute("speedStep", 0.25)
                .setAttribute("jumpFactor", 24.601)
                .setAttribute("speedRotStep", 0.001)
                .setDebug(2);
        addEntity(player);

        addEntity(
                ParticleSystemBuilder.createParticleSystem(world, "drop", 1000,
                        new ParticleBehavior<GameObject>() {
                            @Override
                            public GameObject create(World parentWorld, double elapsed, String particleNamePrefix, Entity<?> e) {
                                GameObject drop = new GameObject(
                                        String.format(particleNamePrefix + "_%d", GameObject.index),
                                        (int) (Math.random() * parentWorld.getPlayArea().getWidth()),
                                        (int) (Math.random() * parentWorld.getPlayArea().getHeight() * 0.1),
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
                                        .setSpeed(0.0, Math.random() * 0.0003)
                                        .addBehavior(this);
                                return drop;
                            }

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
                                int nbP = (int) parent.getAttribute("nbParticles", 0);
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
        cam.setTween(0.05);
        addCamera(cam);

    }

    @Override
    public void input(Application app, InputHandler ih) {
        Entity<?> player = getEntity("player");
        boolean moving = false;
        // player moves
        double step = (double) player.getAttribute("speedStep", 0.05);
        double jumpFactor = (double) player.getAttribute("jumpFactor", 10.0);
        double rotStep = (double) player.getAttribute("speedRotStep", 0.01);

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

    }

    @Override
    public void draw(Application app, Graphics2D g, Map<String, Object> stats) {

    }

    @Override
    public void setWorld(World world) {

    }


}
