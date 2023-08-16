package com.snapgames.demo.behaviors.scene;

import com.snapgames.core.behavior.SceneBehavior;
import com.snapgames.core.entity.TextObject;
import com.snapgames.core.graphics.Renderer;
import com.snapgames.core.input.InputHandler;
import com.snapgames.core.scene.Scene;

public class ScoreDisplayBehavior implements SceneBehavior {
    @Override
    public void create(Scene scene) {

    }

    @Override
    public void input(Scene scene, InputHandler ih) {

    }

    @Override
    public void update(Scene scene, double elapsed) {
        if (scene.getEntity("player") != null) {
            int score = scene.getEntity("player").getAttribute("score", 0);
            TextObject scoreTextObj = ((TextObject) scene.getEntity("score")).setValue(score);
        }
    }

    @Override
    public void draw(Scene scene, Renderer r) {

    }
}
