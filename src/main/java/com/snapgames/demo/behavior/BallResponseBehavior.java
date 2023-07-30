package com.snapgames.demo.behavior;

import com.snapgames.core.behavior.Behavior;
import com.snapgames.core.behavior.CollisionResponseBehavior;
import com.snapgames.core.entity.Entity;
import com.snapgames.core.entity.GameObject;
import com.snapgames.core.math.physic.CollisionEvent;

/**
 * Ball collision response implementation (see {@link CollisionResponseBehavior}).
 * <p>
 * When a ball collides another object, test if other object is the "player".
 * if yes, reduce its own energy, and the player's energy.
 *
 * @author Frédéric Delorme
 * @since 1.0.3
 */
public class BallResponseBehavior implements CollisionResponseBehavior {

    @Override
    public void response(CollisionEvent ce) {
        if (ce.getEntity2().getName().equals("player")) {
            System.out.printf(">> <d> Ball %s hit player and reduce its energy by 10.0%n", ce.getEntity1().getName());
            double energy = ce.getEntity1().getAttribute("energy", 100.0);
            energy -= 10.0;
            ce.getEntity1().setAttribute("energy", energy);
            ce.getEntity1().setSpeed(ce.getEntity1().getVelocity().multiply(-1 * ce.getEntity1().getMaterial().getDensity()));
            if (energy <= 0.0) {
                ce.getEntity1().setActive(false);
                System.out.printf(">> <d> Ball %s has been deactivated%n", ce.getEntity1().getName());
            }
        }
    }

    /**
     * Nothing to do on the update operation (from {@link Behavior}).
     *
     * @see Behavior#update(Entity, double)
     */
    @Override
    public void update(Entity e, double elapsed) {

    }
}
