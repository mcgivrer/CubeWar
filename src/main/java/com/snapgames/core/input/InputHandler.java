package com.snapgames.core.input;

import com.snapgames.core.Application;
import com.snapgames.core.system.GSystem;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InputHandler implements KeyListener, GSystem {

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
        keys[e.getKeyCode()] = false;

        inputInterfaceList.forEach(ii -> {
            ii.onKeyReleased(this, e);
        });
    }

    public void input() {
        inputInterfaceList.forEach(ii -> {
            ii.input(this);
        });
    }

    public boolean isKeyPressed(int keyCode) {
        return keys[keyCode];
    }

    public Application getApplication() {
        return application;
    }

    @Override
    public Class<? extends GSystem> getSystemName() {
        return InputHandler.class;
    }

    @Override
    public void initialize(Application app) {
        // nothing specific to perform for this service initialization.
    }

    @Override
    public void dispose() {
        // nothing specific to perform for this service release.
    }

    public InputHandler remove(InputInterface ti) {
        inputInterfaceList.remove(ti);
        return this;
    }
}
