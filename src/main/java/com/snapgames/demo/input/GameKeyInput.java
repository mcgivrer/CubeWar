package com.snapgames.demo.input;

import com.snapgames.core.Application;
import com.snapgames.core.graphics.Renderer;
import com.snapgames.core.input.InputHandler;
import com.snapgames.core.input.InputInterface;
import com.snapgames.core.math.physic.PhysicEngine;
import com.snapgames.core.math.physic.World;
import com.snapgames.core.scene.Scene;
import com.snapgames.core.scene.SceneManager;
import com.snapgames.core.system.GSystemManager;
import com.snapgames.core.utils.config.Configuration;
import com.snapgames.core.utils.i18n.I18n;

import java.awt.event.KeyEvent;

public class GameKeyInput implements InputInterface {

    @Override
    public void onKeyReleased(InputHandler ih, KeyEvent e) {
        Application application = ih.getApplication();
        Configuration configuration = application.getConfiguration();
        PhysicEngine physicEngine = GSystemManager.find(PhysicEngine.class);
        Scene scn = ((SceneManager) GSystemManager.find(SceneManager.class)).getCurrent();

        switch (e.getKeyCode()) {
            // Request exiting game.
            case KeyEvent.VK_ESCAPE, KeyEvent.VK_Q -> {
                application.requestExit();
            }
            // Change debug level
            case KeyEvent.VK_D -> {
                if (ih.ctrlKey) {
                    configuration.debug = !configuration.debug;
                } else {
                    configuration.debugLevel = configuration.debugLevel + 1 <= 5 ? configuration.debugLevel + 1 : 0;
                }
            }
            // Reverse gravity
            case KeyEvent.VK_G -> {
                World world = physicEngine.getWorld();
                world.setGravity(world.getGravity().negate());
            }
            case KeyEvent.VK_Z -> {
                if (ih.ctrlKey) {
                    scn.clearScene();
                    scn.create(application);
                }
            }
            case KeyEvent.VK_F3 -> {
                Renderer rdr = GSystemManager.find(Renderer.class);
                rdr.takeScreenShot();
            }
            case KeyEvent.VK_L -> {
                application.getI18n().roll();
                Renderer renderer = GSystemManager.find(Renderer.class);
                renderer.getWindow().setTitle(I18n.getMessage("app.window.name"));
            }
            default -> {
                // nothing to do !
            }
        }
    }
}
