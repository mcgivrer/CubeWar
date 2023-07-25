package com.snapgames.demo.scenes;

import com.snapgames.core.Application;
import com.snapgames.core.entity.TextObject;
import com.snapgames.core.graphics.Renderer;
import com.snapgames.core.input.InputHandler;
import com.snapgames.core.math.physic.PhysicType;
import com.snapgames.core.scene.AbstractScene;
import com.snapgames.core.system.GSystemManager;
import com.snapgames.core.utils.config.Configuration;
import com.snapgames.demo.input.PlayerInput;
import com.snapgames.demo.input.TitleInput;

import java.awt.*;

public class TitleScene extends AbstractScene {
    TitleInput ti;

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


        TextObject title = new TextObject("title")
                .setPosition(
                        configuration.bufferResolution.getWidth() * 0.50,
                        configuration.bufferResolution.getHeight() * 0.30)
                .setPhysicType(PhysicType.STATIC)
                .setTextAlign(TextObject.ALIGN_CENTER)
                .setShadowColor(new Color(0.2f, 0.2f, 0.2f, 0.6f))
                .setBorderColor(Color.BLACK)
                .setFont(g2d.getFont().deriveFont(12.0f))
                .setColor(Color.WHITE)
                .setShadowWidth(3)
                .setBorderWidth(2)
                .setI18nKeyCode("app.title.welcome")
                .setPriority(20)
                .setDebug(2)
                .setMaterial(null);

        addEntity(title);

        TextObject copyRMessage = new TextObject("copyright")
                .setPosition(
                        configuration.bufferResolution.getWidth() * 0.50,
                        configuration.bufferResolution.getHeight() * 0.95)
                .setPhysicType(PhysicType.STATIC)
                .setTextAlign(TextObject.ALIGN_CENTER)
                .setShadowColor(new Color(0.2f, 0.2f, 0.2f, 0.6f))
                .setBorderColor(Color.BLACK)
                .setFont(g2d.getFont().deriveFont(8.0f))
                .setColor(Color.WHITE)
                .setShadowWidth(3)
                .setBorderWidth(2)
                .setI18nKeyCode("app.title.copyright")
                .setPriority(20)
                .setStickToCameraView(true)
                .setDebug(2)
                .setMaterial(null);

        addEntity(copyRMessage);

        ((InputHandler) GSystemManager.find(InputHandler.class))
                .add(ti);
    }


    @Override
    public void dispose() {
        super.dispose();
        ((InputHandler) GSystemManager.find(InputHandler.class)).remove(ti);
    }
}
