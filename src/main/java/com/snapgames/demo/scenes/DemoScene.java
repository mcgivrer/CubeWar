package com.snapgames.demo.scenes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.Optional;

import com.snapgames.core.Application;
import com.snapgames.core.entity.Camera;
import com.snapgames.core.entity.Entity;
import com.snapgames.core.entity.GameObject;
import com.snapgames.core.entity.GameObjectType;
import com.snapgames.core.entity.TextObject;
import com.snapgames.core.graphics.Renderer;
import com.snapgames.core.input.InputHandler;
import com.snapgames.core.math.Vector2D;
import com.snapgames.core.math.physic.Material;
import com.snapgames.core.math.physic.PhysicEngine;
import com.snapgames.core.math.physic.PhysicType;
import com.snapgames.core.math.physic.World;
import com.snapgames.core.math.physic.entity.Perturbation;
import com.snapgames.core.scene.AbstractScene;
import com.snapgames.core.scene.Scene;
import com.snapgames.core.system.GSystemManager;
import com.snapgames.core.utils.config.Configuration;
import com.snapgames.core.utils.particles.ParticleSystemBuilder;
import com.snapgames.demo.input.CameraInput;
import com.snapgames.demo.input.DemoInput;
import com.snapgames.demo.input.PlayerInput;
import com.snapgames.demo.particles.BallParticleBehavior;
import com.snapgames.demo.particles.RainParticleBehavior;

/**
 * A {@link Scene} implementing a demonstration of capabilities for this
 * framework.
 * <p>
 * The scene implements a main <code>player</code> {@link GameObject}, a
 * particle systems thanks
 * to the {@link ParticleSystemBuilder}, and a bunch of TextObject to display
 * <code>score</code>, <code>life</code>
 * and some <code>message</code> and <code>pause</code> texts.
 *
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public class DemoScene extends AbstractScene {

    PlayerInput playerInput;
    CameraInput cameraInput;
    DemoInput demoInput;

    public DemoScene() {
        playerInput = new PlayerInput();
        cameraInput = new CameraInput();
        demoInput = new DemoInput();
    }

    @Override
    public String getName() {
        return "demo";
    }

    @Override
    public void create(Application app) {
        Configuration configuration = app.getConfiguration();
        Graphics2D g2d = ((Renderer) GSystemManager.find(Renderer.class)).getBufferGraphics();
        PhysicEngine pe = ((PhysicEngine) GSystemManager.find(PhysicEngine.class));
        pe.setWorld(configuration.world);
        pe.setMaxAcceleration(configuration.maxEntityAcc);
        pe.setMaxVelocity(configuration.maxEntitySpeed);
        World world = pe.getWorld();

        ((InputHandler) GSystemManager.find(InputHandler.class))
            .add(demoInput)
            .add(playerInput)
            .add(cameraInput);
        world.add(
            new Perturbation(
                "wind",
                0, 0,
                world.getPlayArea().getWidth(), world.getPlayArea().getHeight(),
                3, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19)
                .setForce(new Vector2D(0.03, 0.00))
                .setColor(new Color(0.0f, 0.0f, 0.0f, 0.0f))
                .setFillColor(new Color(0.1f, 0.6f, 0.3f, 0.1f)));
        world.add(
            new Perturbation(
                "water",
                0, world.getPlayArea().getHeight() * 0.85,
                world.getPlayArea().getWidth(), world.getPlayArea().getHeight() * 0.15)
                .setForce(new Vector2D(0.0, -1.0))
                .setColor(new Color(0.5f, 0.4f, 0.9f, 0.6f))
                .setFillColor(new Color(0.3f, 0.2f, 0.8f, 0.5f)));
        world.add(
            new Perturbation(
                "magnet_1",
                0, world.getPlayArea().getHeight() * 0.1,
                world.getPlayArea().getWidth() * 0.1, world.getPlayArea().getHeight() * 0.9)
                .setForce(new Vector2D(0.05, 0.0))
                .setColor(new Color(0.0f, 0.0f, 0.0f, 0.0f))
                .setFillColor(new Color(0.1f, 0.7f, 0.2f, 0.1f)));
        world.add(
            new Perturbation(
                "magnet_2",
                world.getPlayArea().getWidth() * 0.90, world.getPlayArea().getHeight() * 0.1,
                world.getPlayArea().getWidth() * 0.1, world.getPlayArea().getHeight() * 0.9)
                .setForce(new Vector2D(-0.05, 0.0))
                .setColor(new Color(0.0f, 0.0f, 0.0f, 0.0f))
                .setFillColor(new Color(0.1f, 0.7f, 0.2f, 0.1f)));
        world.add(
            new Perturbation(
                "magnet_3",
                world.getPlayArea().getWidth() * 0.1, 0,
                world.getPlayArea().getWidth() * 0.8, world.getPlayArea().getHeight() * 0.1)
                .setForce(new Vector2D(0.00, -1.3))
                .setColor(new Color(0.0f, 0.0f, 0.0f, 0.0f))
                .setFillColor(new Color(0.9f, 0.7f, 0.1f, 0.1f)));

        TextObject score = new TextObject("score")
            .setPosition(
                configuration.bufferResolution.getWidth() * 0.98, 32)
            .setShadowColor(new Color(0.2f, 0.2f, 0.2f, 0.6f))
            .setPhysicType(PhysicType.NONE)
            .setBorderColor(Color.BLACK)
            .setFont(g2d.getFont().deriveFont(20.0f))
            .setColor(Color.WHITE)
            .setShadowWidth(3)
            .setBorderWidth(2)
            .setText("%05d")
            .setValue(0)
            .setPriority(20)
            .setLayer(1)
            .setTextAlign(TextObject.ALIGN_RIGHT)
            .setStickToCameraView(true)
            .setMaterial(null)
            .setDebug(3);

        addEntity(score);

        TextObject heart = new TextObject("heart")
            .setPosition(10, configuration.bufferResolution.getHeight() * 0.90)
            .setPhysicType(PhysicType.NONE)
            .setShadowColor(new Color(0.2f, 0.2f, 0.2f, 0.6f))
            .setBorderColor(Color.BLACK)
            .setFont(g2d.getFont().deriveFont(16.0f))
            .setColor(Color.RED)
            .setShadowWidth(3)
            .setBorderWidth(2)
            .setText("❤")
            .setPriority(20)
            .setLayer(1)
            .setStickToCameraView(true)
            .setMaterial(null);

        addEntity(heart);

        TextObject life = new TextObject("life")
            .setPosition(20, configuration.bufferResolution.getHeight() * 0.90)
            .setPhysicType(PhysicType.NONE)
            .setShadowColor(new Color(0.2f, 0.2f, 0.2f, 0.6f))
            .setBorderColor(Color.BLACK)
            .setFont(g2d.getFont().deriveFont(12.0f))
            .setColor(Color.WHITE)
            .setShadowWidth(3)
            .setBorderWidth(1)
            .setText("%d")
            .setValue(3)
            .setPriority(21)
            .setLayer(1)
            .setStickToCameraView(true)
            .setDebug(2)
            .setMaterial(null);

        addEntity(life);

        TextObject welcomeMessage = new TextObject("message")
            .setPosition(
                configuration.bufferResolution.getWidth() * 0.50,
                configuration.bufferResolution.getHeight() * 0.70)
            .setPhysicType(PhysicType.NONE)
            .setTextAlign(TextObject.ALIGN_CENTER)
            .setShadowColor(new Color(0.2f, 0.2f, 0.2f, 0.6f))
            .setBorderColor(Color.BLACK)
            .setFont(g2d.getFont().deriveFont(12.0f))
            .setColor(Color.WHITE)
            .setShadowWidth(3)
            .setBorderWidth(2)
            .setI18nKeyCode("app.title.welcome")
            .setPriority(20)
            .setLayer(1)
            .setStickToCameraView(true)
            .setDuration(5000)
            .setDebug(2)
            .setMaterial(null);

        addEntity(welcomeMessage);

        TextObject pauseObj = new TextObject("pause")
            .setPosition(
                configuration.bufferResolution.getWidth() * 0.50,
                configuration.bufferResolution.getHeight() * 0.40)
            .setPhysicType(PhysicType.NONE)
            .setTextAlign(TextObject.ALIGN_CENTER)
            .setShadowColor(new Color(0.2f, 0.2f, 0.2f, 0.6f))
            .setBorderColor(Color.BLACK)
            .setFont(g2d.getFont().deriveFont(24.0f))
            .setColor(Color.WHITE)
            .setShadowWidth(3)
            .setBorderWidth(2)
            .setI18nKeyCode("app.pause.message")
            .setPriority(20)
            .setLayer(1)
            .setStickToCameraView(true)
            .setMaterial(null)
            .setActive(false);

        addEntity(pauseObj);

        GameObject player = new GameObject("player")
            .setPosition(
                configuration.world.getPlayArea().getWidth() * 0.50,
                configuration.world.getPlayArea().getHeight() * 0.50)
            .setSize(16, 16)
            .setType(GameObjectType.TYPE_RECTANGLE)
            .setPhysicType(PhysicType.DYNAMIC)
            .setPriority(10)
            .setLayer(80)
            .setColor(Color.WHITE)
            .setFillColor(Color.GREEN)
            .setMass(60.0)
            .setMaterial(new Material("playerMat", 0.80, 1.0, 0.99))
            .setAttribute("speedStep", 0.1)
            .setAttribute("jumpFactor", 99.601)
            .setAttribute("speedRotStep", 0.001)
            .setDebug(2);
        addEntity(player);

        TextObject helpPanel = new TextObject("helpPanel")
            .setPosition(
                configuration.bufferResolution.getWidth() * 0.50,
                configuration.bufferResolution.getHeight() * 0.50)
            .setPhysicType(PhysicType.STATIC)
            .setTextAlign(TextObject.ALIGN_CENTER)
            .setColor(Color.LIGHT_GRAY)
            .setShadowColor(new Color(0.4f, 0.4f, 0.4f, 0.8f))
            .setBorderColor(Color.BLACK)
            .setFont(g2d.getFont().deriveFont(8.5f))
            .setShadowWidth(3)
            .setBorderWidth(2)
            .setI18nKeyCode("app.demo.help")
            .setPriority(30)
            .setLayer(2)
            .setStickToCameraView(true)
            .setDuration(7000)
            .setDebug(2)
            .setMaterial(null)
            .setActive(false);
        addEntity(helpPanel);
        // create some red ball particle system
        addEntity(
            ParticleSystemBuilder.createParticleSystem(world, "ball", 50, 1,
                new BallParticleBehavior(200.0, 2.0)));

        // add rain drops particle system.
        addEntity(
            ParticleSystemBuilder.createParticleSystem(world, "raindrop", 1000, 50,
                new RainParticleBehavior(0.003)));

        Camera cam = new Camera("cam01", configuration.bufferResolution.width, configuration.bufferResolution.height);
        cam.setTarget(player);
        cam.setTween(0.5);
        addCamera(cam);

    }

    @Override
    public void input(Application app, InputHandler ih) {
        Entity<?> player = getEntity("player");
        if (Optional.ofNullable(player).isPresent()) {
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

    @Override
    public void dispose() {
        super.dispose();
        ((InputHandler) GSystemManager.find(InputHandler.class))
            .remove(playerInput)
            .remove(cameraInput)
            .remove(demoInput);
    }

}
