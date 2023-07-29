package com.snapgames.demo;

import com.snapgames.core.behavior.Behavior;
import com.snapgames.core.behavior.CollisionResponseBehavior;
import com.snapgames.core.entity.Entity;
import com.snapgames.core.entity.GameObject;
import com.snapgames.core.math.physic.CollisionEvent;

public class BallResponseBehavior implements CollisionResponseBehavior {

    @Override
    public void response(CollisionEvent ce) {
        if (ce.getEntity2().getName().equals("player")) {
            double energy = ce.getEntity1().getAttribute("energy", 100.0);
            energy -= 10.0;
            ce.getEntity1().setAttribute("energy", energy);
            if (energy <= 0.0) {
                ce.getEntity1().setActive(false);
            }
        }
    }

    @Override
    public void update(Entity<?> e, double elapsed) {

    }
}
