package com.snapgames.core.input;

import com.snapgames.core.Application;
import com.snapgames.core.utils.config.Configuration;
import com.snapgames.core.math.physic.PhysicEngine;
import com.snapgames.core.math.physic.World;
import com.snapgames.core.scene.SceneManager;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InputHandler implements KeyListener {

    private final Application application;


    /**
     * Key listener components
     */
    private boolean keys[] = new boolean[1024];
    public boolean ctrlKey;
    public boolean shiftKey;
    public boolean altKey;

    private List<InputInterface> inputInterfaceList = new CopyOnWriteArrayList<>();

    public InputHandler(Application app) {
        this.application = app;
    }

    public InputHandler add(InputInterface ii) {
        inputInterfaceList.add(ii);
        return this;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (application.getConfiguration().debugLevel > 3) {
            System.out.printf(">> <!> key typed: %s%n", e.getKeyChar());
        }
        inputInterfaceList.forEach(ii -> {
            ii.onKeyTyped(this, e);
        });
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
        checkMetaKeys(e);
        inputInterfaceList.forEach(ii -> {
            ii.onKeyPressed(this, e);
        });
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

        inputInterfaceList.forEach(ii -> {
            ii.onKeyReleased(this, e);
        });
    }

    public boolean isKeyPressed(int keyCode) {
        return keys[keyCode];
    }

    public Application getApplication() {
        return application;
    }
}
