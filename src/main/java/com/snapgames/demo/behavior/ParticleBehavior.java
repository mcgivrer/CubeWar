package com.snapgames.demo.behavior;

import com.snapgames.demo.Application;
import com.snapgames.demo.entity.Entity;
import com.snapgames.demo.math.physic.World;

/**
 * Add a specific {@link ParticleBehavior#create(World, double, String, Entity)}
 * extending the existing {@link Behavior}
 * and will be applied to {@link com.snapgames.demo.entity.GameObject} entity.
 * <p>
 * The new <code>create</code> phase for this {@link com.snapgames.demo.entity.GameObject}
 * will be modified
 * with the implementation of this behavior interface, and allow to create a new
 * particle by the
 * {@link Application#createParticleSystem(World, String, int, ParticleBehavior)},
 * while the already defined
 * {@link Behavior#update(Entity, double)} will be used to update the created
 * particles like any other
 * {@link com.snapgames.demo.entity.GameObject}.
 *
 * @param <GameObject> the Entity to be modified.
 */
public interface ParticleBehavior<GameObject> extends Behavior<GameObject> {
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
    GameObject create(World w, double elapsed, String particleNamePrefix, Entity<?> parent);
}
