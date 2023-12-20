package com.snapgames.core.test.scenes;

import com.snapgames.core.Application;
import com.snapgames.core.entity.Camera;
import com.snapgames.core.entity.GameObject;
import com.snapgames.core.math.physic.Material;
import com.snapgames.core.scene.AbstractScene;

import java.awt.*;

public abstract class AbstractTestScene extends AbstractScene {
    protected String name;

    public AbstractTestScene(String sceneName) {
        this.name = sceneName;
    }

    @Override
    public String getName() {
        return name;
    }


}
