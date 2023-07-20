package com.snapgames.core.test;

import com.snapgames.core.Application;
import com.snapgames.core.test.scenes.TestScene;
import com.snapgames.core.utils.config.Configuration;

public class AppTest extends Application {
    @Override
    public void createScenes() {
        getSceneManager().add(new TestScene());
    }

    public void setConfiguration(Configuration config) {
        this.configuration = config;
    }
}
