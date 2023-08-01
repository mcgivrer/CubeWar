package com.snapgames.demo.scenes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import com.snapgames.core.Application;
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
import com.snapgames.core.system.GSystemManager;
import com.snapgames.core.utils.config.Configuration;
import com.snapgames.core.utils.particles.ParticleSystemBuilder;
import com.snapgames.demo.behaviors.EnemyBehavior;
import com.snapgames.demo.input.TitleInput;
import com.snapgames.demo.behaviors.particles.RainParticleBehavior;

public class TitleScene extends AbstractScene {

    TitleInput ti;
    double internalSceneTime = 0;

    public TitleScene() {
        ti = new TitleInput();
    }

    @Override
    public String getName() {
        return "title";
    }

    @Override
    public void create(Application app) {
        Configuration configuration = app.getConfiguration();
        Graphics2D g2d = ((Renderer) GSystemManager.find(Renderer.class)).getBufferGraphics();
        PhysicEngine pe = GSystemManager.find(PhysicEngine.class);
        pe.setMaxAcceleration(100.0).setMaxVelocity(200.0);


        pe.setWorld(
                new World("title")
                        .setGravity(new Vector2D(0, 0.1))
                        .setPlayArea(new Rectangle2D.Double(0, 0, 400, 240)));

        pe.getWorld().add(
                new Perturbation(
                        "wind",
                        0, 0,
                        pe.getWorld().getPlayArea().getWidth(), pe.getWorld().getPlayArea().getHeight(),
                        10, 11, 12, 13, 14, 15, 16, 17, 18, 19)
                        .setForce(new Vector2D(0.1, 0.00))
                        .setFillColor(new Color(0.1f, 0.6f, 0.3f, 0.5f))
                        .setLayer(5)
        );


        // add rain drops particle system.
        addEntity(
                ParticleSystemBuilder.createParticleSystem(pe.getWorld(), "raindrop", 1000, 10,
                        new RainParticleBehavior(0.03)));

        GameObject player = new GameObject("player")
                .setPosition(
                        pe.getWorld().getPlayArea().getWidth() * 0.50,
                        pe.getWorld().getPlayArea().getHeight() * 0.50)
                .setSize(16, 16)
                .setType(GameObjectType.TYPE_RECTANGLE)
                .setPhysicType(PhysicType.DYNAMIC)
                .setPriority(10)
                .setLayer(2)
                .setColor(Color.GREEN)
                .setFillColor(Color.GREEN)
                .setMass(60.0)
                .setMaterial(new Material("playerMat", 0.80, 1.0, 0.99))
                .setAttribute("speedStep", 0.1)
                .setAttribute("jumpFactor", 99.601)
                .setAttribute("speedRotStep", 0.001)
                .setDebug(2);
        addEntity(player);

        GameObject ennemy_1 = new GameObject("ennemy_1")
            .setPosition(
                pe.getWorld().getPlayArea().getWidth() * 0.10,
                pe.getWorld().getPlayArea().getHeight() * 0.90)
            .setSize(8, 8)
            .setType(GameObjectType.TYPE_RECTANGLE)
            .setPhysicType(PhysicType.DYNAMIC)
            .setPriority(10)
            .setLayer(2)
            .setColor(Color.RED)
            .setFillColor(Color.RED)
            .setMass(30.0)
            .setMaterial(new Material("ennemyMat", 0.80, 1.0, 0.87))
            .setAttribute("speedStep", 0.1)
            .setAttribute("jumpFactor", 99.601)
            .setAttribute("speedRotStep", 0.001)
            .setDebug(3)
            .addBehavior(new EnemyBehavior(player,0.006));
        addEntity(ennemy_1);
        GameObject ennemy_2 = new GameObject("ennemy_2")
            .setPosition(
                pe.getWorld().getPlayArea().getWidth() * 0.90,
                pe.getWorld().getPlayArea().getHeight() * 0.90)
            .setSize(8, 8)
            .setType(GameObjectType.TYPE_RECTANGLE)
            .setPhysicType(PhysicType.DYNAMIC)
            .setPriority(10)
            .setLayer(2)
            .setColor(Color.RED)
            .setFillColor(Color.RED)
            .setMass(30.0)
            .setMaterial(new Material("ennemyMat", 0.80, 1.0, 0.87))
            .setAttribute("speedStep", 0.1)
            .setAttribute("jumpFactor", 99.601)
            .setAttribute("speedRotStep", 0.001)
            .setDebug(3)
            .addBehavior(new EnemyBehavior(player,0.004));
        addEntity(ennemy_2);

        TextObject title = new TextObject("title")
                .setPosition(
                        configuration.bufferResolution.getWidth() * 0.50,
                        configuration.bufferResolution.getHeight() * 0.30)
                .setPhysicType(PhysicType.STATIC)
                .setTextAlign(TextObject.ALIGN_CENTER)
                .setShadowColor(new Color(0.2f, 0.2f, 0.2f, 0.6f))
                .setBorderColor(Color.BLACK)
                .setFont(g2d.getFont().deriveFont(28.0f))
                .setColor(Color.WHITE)
                .setShadowWidth(3)
                .setBorderWidth(2)
                .setI18nKeyCode("app.title.main")
                .setPriority(20)
                .setLayer(1)
                .setDebug(2)
                .setMaterial(null);

        addEntity(title);

        TextObject startMessage = new TextObject("startMessage")
                .setPosition(
                        configuration.bufferResolution.getWidth() * 0.50,
                        configuration.bufferResolution.getHeight() * 0.70)
                .setPhysicType(PhysicType.STATIC)
                .setTextAlign(TextObject.ALIGN_CENTER)
                .setShadowColor(new Color(0.2f, 0.2f, 0.2f, 0.6f))
                .setBorderColor(Color.BLACK)
                .setFont(g2d.getFont().deriveFont(9.0f))
                .setColor(Color.WHITE)
                .setShadowWidth(3)
                .setBorderWidth(2)
                .setI18nKeyCode("app.title.start")
                .setPriority(20)
                .setLayer(1)
                .setDebug(2)
                .setMaterial(null);

        addEntity(startMessage);

        TextObject copyRMessage = new TextObject("copyright")
                .setPosition(
                        configuration.bufferResolution.getWidth() * 0.50,
                        configuration.bufferResolution.getHeight() * 0.95)
                .setPhysicType(PhysicType.STATIC)
                .setTextAlign(TextObject.ALIGN_CENTER)
                .setShadowColor(new Color(0.2f, 0.2f, 0.2f, 0.6f))
                .setBorderColor(Color.BLACK)
                .setFont(g2d.getFont().deriveFont(9.0f))
                .setColor(Color.WHITE)
                .setShadowWidth(3)
                .setBorderWidth(2)
                .setI18nKeyCode("app.title.copyright")
                .setPriority(20)
                .setLayer(1)
                .setStickToCameraView(true)
                .setDebug(2)
                .setMaterial(null);

        addEntity(copyRMessage);

        ((InputHandler) GSystemManager.find(InputHandler.class))
                .add(ti);
    }

    @Override
    public void update(Application app, double elapsed) {
        double factor = 40.0;
        GameObject player = (GameObject) getEntity("player");
        internalSceneTime += elapsed;
        if (internalSceneTime > 0.5) {
            player.addForce(new Vector2D(-(factor * 0.5) + Math.random() * factor, -(factor * 1.5) + Math.random() * factor * 3));
            internalSceneTime = 0;
        }

    }

    @Override
    public void dispose() {
        super.dispose();
        ((InputHandler) GSystemManager.find(InputHandler.class)).remove(ti);
    }

}
