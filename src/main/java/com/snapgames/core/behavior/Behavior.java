package com.snapgames.core.behavior;

import com.snapgames.core.entity.Entity;

/**
 * Add a specific {@link Behavior#update(Entity, double)} to a
 * {@link com.snapgames.core.entity.GameObject} entity.
 * <p>
 * The update phase for this {@link com.snapgames.core.entity.GameObject} will be modified
 * with the implementation of this behavior interface.
 *
 * @param <GameObject> the Entity to be modified.
 */
public interface Behavior<T extends Entity<?>> {
    /**
     * Implement the <code>update</code> the e Entity according to the elapsed time.
     *
     * @param e       the Entity to be updated.
     * @param elapsed the elapsed time since previous call.
     */
    void update(Entity<?> e, double elapsed);
}
