package com.snapgames.demo.input;

import com.snapgames.core.Application;
import com.snapgames.core.input.InputHandler;
import com.snapgames.core.input.InputInterface;
import com.snapgames.core.scene.Scene;
import com.snapgames.core.scene.SceneManager;
import com.snapgames.core.system.GSystemManager;

import java.awt.event.KeyEvent;

public class CameraInput implements InputInterface {
    @Override
    public void input(InputHandler ih) {
        Application app = ih.getApplication();
        Scene scene = ((SceneManager) GSystemManager.find(SceneManager.class)).getCurrent();
        // camera rotation
        if (ih.isKeyPressed(KeyEvent.VK_PAGE_UP)) {
            scene.getActiveCamera().setRotationSpeed(0.001);
        }
        if (ih.isKeyPressed(KeyEvent.VK_PAGE_DOWN)) {
            scene.getActiveCamera().setRotationSpeed(-0.001);
        }
        if (ih.isKeyPressed(KeyEvent.VK_DELETE)) {
            scene.getActiveCamera().setRotationSpeed(0.0);
            scene.getActiveCamera().setRotation(0.0);
        }
    }

    @Override
    public void onKeyPressed(InputHandler ih, KeyEvent key) {

    }

    @Override
    public void onKeyReleased(InputHandler ih, KeyEvent key) {

    }

    @Override
    public void onKeyTyped(InputHandler ih, KeyEvent key) {

    }
}
