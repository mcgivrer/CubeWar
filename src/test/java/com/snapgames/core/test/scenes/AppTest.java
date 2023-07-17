package com.snapgames.core.test.scenes;

import com.snapgames.core.Application;

public class AppTest extends Application {
    @Override
    public void createScenes() {
        getSceneManager().add(new TestScene());
    }
}
