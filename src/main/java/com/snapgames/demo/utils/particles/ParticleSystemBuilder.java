package com.snapgames.demo.utils.particles;

import com.snapgames.demo.behavior.ParticleBehavior;
import com.snapgames.demo.entity.GameObject;
import com.snapgames.demo.math.physic.World;

public class ParticleSystemBuilder {
    /**
     * Create a new Particle System with a parent GameObject and a certain number of
     * child according to the nbParticles parameter.
     * <p>
     * These particles are {@link GameObject} with a specific
     * {@link ParticleBehavior} applied on to have a
     * common processing for all those particles belonging to the same parent
     * {@link GameObject}.
     * <p>
     * the {@link GameObject#parent} will have all those particles declared as its
     * own {@link GameObject#child}.
     *
     * @param parentWorld        the world where all those particles will evolve.
     * @param particleNamePrefix the prefix name for all those particles.
     * @param nbParticles        the number of particle to be created.
     * @param b                  the common {@link ParticleBehavior} to be applied
     *                           to all those particles.
     * @return a new parent {@link GameObject} containing a bunch of
     * {@link GameObject} particle child with
     * the same {@link ParticleBehavior}.
     */
    public static GameObject createParticleSystem(
            World parentWorld,
            String particleNamePrefix,
            int nbParticles,
            ParticleBehavior<GameObject> b) {

        GameObject parentParticle = new GameObject(particleNamePrefix + "'s", 0, 0, 0, 0);
        parentParticle.setAttribute("nbParticles", nbParticles);
        for (int i = 0; i < nbParticles / 100; i++) {
            GameObject particle = b.create(parentWorld, 0, particleNamePrefix, parentParticle);
            parentParticle.addChild(particle);
        }
        return parentParticle;
    }
}
