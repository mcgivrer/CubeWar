package com.snapgames.demo.input;

import com.snapgames.demo.Application;
import com.snapgames.demo.utils.config.Configuration;
import com.snapgames.demo.math.physic.PhysicEngine;
import com.snapgames.demo.math.physic.World;
import com.snapgames.demo.scene.SceneManager;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {

    private final Application application;


    /**
     * Key listener components
     */
    private boolean keys[] = new boolean[1024];
    public boolean ctrlKey;
    public boolean shiftKey;
    public boolean altKey;

    public InputHandler(Application app) {
        this.application = app;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (application.getConfiguration().debugLevel > 3) {
            System.out.printf(">> <!> key typed: %s%n", e.getKeyChar());
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
        checkMetaKeys(e);
    }

    private void checkMetaKeys(KeyEvent e) {
        ctrlKey = e.isControlDown();
        shiftKey = e.isShiftDown();
        altKey = e.isAltDown();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Configuration configuration = application.getConfiguration();
        PhysicEngine physicEngine = application.getPhysicEngine();
        SceneManager scnMgr = application.getSceneManager();
        keys[e.getKeyCode()] = false;
        switch (e.getKeyCode()) {
            // Request exiting game.
            case KeyEvent.VK_ESCAPE -> {
                application.requestExit();
            }
            // Change debug level
            case KeyEvent.VK_D -> {
                configuration.debugLevel = configuration.debugLevel + 1 <= 5 ? configuration.debugLevel + 1 : 0;
            }
            // Reverse gravity
            case KeyEvent.VK_G -> {
                World world = physicEngine.getWorld();
                world.setGravity(world.getGravity().negate());
            }
            case KeyEvent.VK_Z -> {
                if (ctrlKey) {
                    scnMgr.getCurrent().clearScene();
                    scnMgr.getCurrent().create(application);
                }
            }
            case KeyEvent.VK_P, KeyEvent.VK_PAUSE -> {
                application.setPause(!application.isPause());
            }
            default -> {
                // nothing to do !
            }
        }
    }

    public boolean isKeyPressed(int keyCode) {
        return keys[keyCode];
    }
}