package com.snapgames.demo.input;

import com.snapgames.core.input.InputHandler;
import com.snapgames.core.input.InputInterface;
import com.snapgames.core.scene.SceneManager;
import com.snapgames.core.system.GSystemManager;

import java.awt.event.KeyEvent;

public class TitleInput implements InputInterface {

    @Override
    public void onKeyReleased(InputHandler ih, KeyEvent key) {
        switch (key.getKeyCode()) {
            case KeyEvent.VK_SPACE, KeyEvent.VK_ENTER -> {
                ((SceneManager) GSystemManager.find(SceneManager.class)).activate("demo");
            }
            default -> {
                // nothing to do there !
            }
        }

    }
}
