package com.snapgames.demo.input;

import java.awt.event.KeyEvent;
import java.util.Optional;

import com.snapgames.core.Application;
import com.snapgames.core.entity.TextObject;
import com.snapgames.core.input.InputHandler;
import com.snapgames.core.input.InputInterface;
import com.snapgames.core.scene.SceneManager;
import com.snapgames.core.system.GSystemManager;

public class DemoInput implements InputInterface {

    @Override
    public void onKeyReleased(InputHandler ih, KeyEvent key) {
        Application application = ih.getApplication();
        switch (key.getKeyCode()) {
            case KeyEvent.VK_H, KeyEvent.VK_EXCLAMATION_MARK -> {
                TextObject helpPanel = (TextObject) ((SceneManager) GSystemManager.find(SceneManager.class))
                        .getCurrent()
                        .getEntity("helpPanel");
                if (Optional.ofNullable(helpPanel).isPresent()) {
                    helpPanel.setDuration(7000)
                            .setActive(!helpPanel.active);
                }
            }
            case KeyEvent.VK_P, KeyEvent.VK_PAUSE -> {
                application.setPause(!application.isPaused());
                TextObject pause = (TextObject) ((SceneManager) GSystemManager.find(SceneManager.class))
                        .getCurrent()
                        .getEntity("pause");
                pause.setActive(application.isPaused());
            }
            default -> {
                // nothing to do here !
            }
        }
    }
}
