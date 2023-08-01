package com.snapgames.demo.behaviors;

import com.snapgames.core.behavior.Behavior;
import com.snapgames.core.entity.Entity;
import com.snapgames.core.entity.GameObject;
import com.snapgames.core.math.Vector2D;

/**
 * An Enemy behavior design to track a target with a specific acceleration factor.
 *
 * @author Frédéric Delorme
 * @since 1.0.3
 */
public class EnemyBehavior implements Behavior<GameObject> {

    private final GameObject target;
    private final double speedFactor;

    /**
     * Create a new {@link EnemyBehavior} targeting the target entity with an acceleration equals to the distance
     * between the objects applied with a factor of the distance between these.
     *
     * @param target      the targeted object
     * @param speedFactor the speedFactor to be applied on distance.
     */
    public EnemyBehavior(GameObject target, double speedFactor) {
        this.target = target;
        this.speedFactor = speedFactor;
    }

    @Override
    public void update(Entity<?> e, double elapsed) {
        Vector2D distance = target.getPosition().substract(e.getPosition());
        e.addForce(distance.multiply(speedFactor));
    }
}
