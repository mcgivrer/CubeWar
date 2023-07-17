package com.snapgames.core.test.scenes;

import com.snapgames.core.Application;
import com.snapgames.core.entity.Camera;
import com.snapgames.core.entity.GameObject;
import com.snapgames.core.scene.AbstractScene;

import java.awt.*;

public class TestScene extends AbstractScene {
    @Override
    public String getName() {
        return "TestScene";
    }

    @Override
    public void create(Application app) {
        addEntity(new GameObject("player")
                .setPosition(160, 100).setSize(16, 16)
                .setColor(Color.RED)
        );
        addCamera(
                new Camera("came01", 320, 200)
                        .setTarget(getEntity("player"))
                        .setTween(0.02));
    }
}
