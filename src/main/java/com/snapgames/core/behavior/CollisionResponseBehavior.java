package com.snapgames.core.behavior;

import com.snapgames.core.entity.Entity;
import com.snapgames.core.math.physic.CollisionEvent;

/**
 * The {@link CollisionResponseBehavior} proposes an interface to implement the collision
 * response into an enhanced  {@link Behavior}, raised by the {@link com.snapgames.core.math.physic.CollisionDetection} system.
 * <p>
 * the {@link CollisionResponseBehavior#response(CollisionEvent)} interface will process
 * the given {@link CollisionEvent}.
 */
public interface CollisionResponseBehavior extends Behavior<Entity<?>> {

    /**
     * Interface to implement the possible response when a {@link CollisionEvent} is raised
     * by the {@link com.snapgames.core.math.physic.CollisionDetection} system.
     *
     * @param ce the {@link CollisionEvent} raised by 2  entities colliding.
     */
    void response(CollisionEvent ce);
}
