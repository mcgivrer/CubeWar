package com.snapgames.core.test.scenes;

import com.snapgames.core.Application;
import com.snapgames.core.entity.Camera;
import com.snapgames.core.entity.GameObject;
import com.snapgames.core.math.physic.Material;
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
                .setPosition(100, 100)
                .setSize(16, 16)
                .setMass(100)
                .setMaterial(new Material("testMaterial",1.0,1.0,1.0))
                .setColor(Color.RED)
        );
        addCamera(
                new Camera("came01", 320, 200)
                        .setTarget(getEntity("player"))
                        .setTween(0.02));
    }
}
