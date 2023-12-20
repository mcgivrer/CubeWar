package com.snapgames.demo.behaviors;

import com.snapgames.core.behavior.CollisionResponseBehavior;
import com.snapgames.core.entity.Entity;
import com.snapgames.core.entity.GameObject;
import com.snapgames.core.math.Vector2D;
import com.snapgames.core.math.physic.CollisionEvent;

public class GameObjectCollisionResponse implements CollisionResponseBehavior {
    @Override
    public void update(Entity<?> e, double elapsed) {

    }

    @Override
    public void response(CollisionEvent ce) {
        Vector2D c1 = new Vector2D(ce.getEntity1().getCenterX(), ce.getEntity1().getCenterY());
        Vector2D s1 = new Vector2D(ce.getEntity1().getWidth(), ce.getEntity1().getHeight());
        Vector2D c2 = new Vector2D(ce.getEntity2().getCenterX(), ce.getEntity2().getCenterY());
        Vector2D s2 = new Vector2D(ce.getEntity2().getWidth(), ce.getEntity2().getHeight());
        Vector2D dd = c1.substract(c2);
        if (ce.getEntity1().getVelocity().x > ce.getEntity1().getVelocity().y) {
            if (dd.x < s1.x + s2.x) {
                ce.getEntity2().setPosition(c1.x + s1.x, c2.y);
            }
        } else {
            if (dd.y < s1.y + s2.y) {
                ce.getEntity2().setPosition(c2.x, c1.y + s1.y);
            }
        }
    }

    @Override
    public boolean filter(CollisionEvent ce) {
        return true;
    }
}
