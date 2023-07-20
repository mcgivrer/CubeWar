package com.snapgames.core.behavior;

import com.snapgames.core.Application;
import com.snapgames.core.entity.Entity;
import com.snapgames.core.math.physic.World;

/**
 * Add a specific {@link ParticleBehavior#create(World, double, String, Entity)}
 * extending the existing {@link Behavior}
 * and will be applied to {@link com.snapgames.core.entity.GameObject} entity.
 * <p>
 * The new <code>create</code> phase for this {@link com.snapgames.core.entity.GameObject}
 * will be modified
 * with the implementation of this behavior interface, and allow to
 * <ul><li>create a new
 * particle by the
 * {@link com.snapgames.core.utils.particles.ParticleSystemBuilder#createParticleSystem(World, String, int, ParticleBehavior)},
 * </li>
 * <li>while the already defined
 * {@link Behavior#update(Entity, double)} will be used to update the created
 * particles like any other
 * {@link com.snapgames.core.entity.GameObject}.
 * </li></ul>
 *
 * @param <T> the Entity to be modified.
 */
public interface ParticleBehavior<T> extends Behavior<T> {
    /**
     * Implement the <code>create</code> phase for the particle using a prefix name,
     * the {@link World} object as
     * context and the parent {@link Entity}
     *
     * @param w                  the world context object defining the environment
     *                           where this new particle will evolve.
     * @param elapsed            The elapsed time (in millisecond) since previous call.
     * @param particleNamePrefix the prefix name for this new particle. it will be
     *                           completed by the internal {@link Entity#index}
     *                           value.
     * @param parent             the parent {@link Entity} hosting this particle.
     * @return the newly created {@link ParticleBehavior} implementation.
     */
    T create(World w, double elapsed, String particleNamePrefix, Entity<?> parent);
}
