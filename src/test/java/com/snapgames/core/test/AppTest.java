package com.snapgames.core.test;

import com.snapgames.core.Application;
import com.snapgames.core.scene.Scene;
import com.snapgames.core.scene.SceneManager;
import com.snapgames.core.system.GSystemManager;
import com.snapgames.core.test.scenes.TestScene;
import com.snapgames.core.utils.config.Configuration;

public class AppTest extends Application {
    @Override
    public void createScenes() {
        ((SceneManager) GSystemManager.find(SceneManager.class)).add(new TestScene());
    }

    public void setConfiguration(Configuration config) {
        this.configuration = config;
    }
}
