package com.snapgames.demo.input;

import com.snapgames.core.Application;
import com.snapgames.core.entity.Entity;
import com.snapgames.core.input.InputHandler;
import com.snapgames.core.input.InputInterface;
import com.snapgames.core.math.Vector2D;
import com.snapgames.core.scene.Scene;
import com.snapgames.core.scene.SceneManager;
import com.snapgames.core.system.GSystemManager;

import java.awt.event.KeyEvent;

public class PlayerInput implements InputInterface {
    @Override
    public void input(InputHandler ih) {
        Application app = ih.getApplication();
        Scene scene = ((SceneManager) GSystemManager.find(SceneManager.class)).getCurrent();
        Entity<?> player = scene.getEntity("player");
        boolean moving = false;
        // player moves
        double step = player.getAttribute("speedStep", 0.0005);
        double jumpFactor = player.getAttribute("jumpFactor", 10.0);
        double rotStep = player.getAttribute("speedRotStep", 0.01);

        if (ih.ctrlKey)
            step = step * 4.0;
        if (ih.shiftKey)
            step = step * 2.0;

        // player rotation
        if (ih.altKey) {
            if (ih.isKeyPressed(KeyEvent.VK_UP)) {
                player.setRotationSpeed(-rotStep);
            }
            if (ih.isKeyPressed(KeyEvent.VK_DOWN)) {
                player.setRotationSpeed(+rotStep);
            }
            if (ih.isKeyPressed(KeyEvent.VK_DELETE)) {
                player.setRotationSpeed(0.0);
                player.setRotation(0.0);
            }
        } else {
            if (ih.isKeyPressed(KeyEvent.VK_UP)) {
                player.addForce(new Vector2D(0.0, -step * jumpFactor));
                moving = true;
            }
            if (ih.isKeyPressed(KeyEvent.VK_DOWN)) {
                player.addForce(new Vector2D(0.0, step));
                moving = true;
            }
        }

        if (ih.isKeyPressed(KeyEvent.VK_LEFT)) {
            player.addForce(new Vector2D(-step, 0.0));
            moving = true;
        }
        if (ih.isKeyPressed(KeyEvent.VK_RIGHT)) {
            player.addForce(new Vector2D(step, 0.0));
            moving = true;
        }

        if (!moving) {
            player.vel = player.vel.multiply(player.getMaterial().getRoughness());
        }
    }


}
