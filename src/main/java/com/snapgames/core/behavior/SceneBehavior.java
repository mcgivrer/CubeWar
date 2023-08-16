package com.snapgames.core.behavior;

import com.snapgames.core.graphics.Renderer;
import com.snapgames.core.input.InputHandler;
import com.snapgames.core.scene.Scene;

public interface SceneBehavior {
    void create(Scene scene);

    void input(Scene scene, InputHandler ih);

    void update(Scene scene, double elapsed);

    void draw(Scene scene, Renderer r);

}
