package com.snapgames.core.utils.particles;

import com.snapgames.core.behavior.Behavior;
import com.snapgames.core.behavior.ParticleBehavior;
import com.snapgames.core.entity.GameObject;
import com.snapgames.core.math.physic.World;

import java.util.Arrays;
import java.util.Optional;

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
     * @param createBehavior     the common {@link ParticleBehavior} to be applied
     *                           to all those particles.
     * @param particleBehaviors  a lit of {@link Behavior} to be added to the created particle.
     * @return a new parent {@link GameObject} containing a bunch of
     * {@link GameObject} particle child with
     * the same {@link ParticleBehavior}.
     */
    @SafeVarargs
    public static GameObject createParticleSystem(
        World parentWorld,
        String particleNamePrefix,
        int nbParticles,
        int threshold,
        ParticleBehavior<GameObject> createBehavior,
        Behavior<GameObject>... particleBehaviors) {

        GameObject parentParticle = new GameObject(particleNamePrefix + "'s", 0, 0, 0, 0);
        parentParticle.setAttribute("nbParticles", nbParticles);
        for (int i = 0; i < nbParticles / threshold; i++) {
            GameObject particle = createBehavior.create(parentWorld, 0, particleNamePrefix, parentParticle);
            if (Optional.ofNullable(particleBehaviors).isPresent()) {
                Arrays.stream(particleBehaviors).toList().forEach(particle::addBehavior);
            }
            parentParticle.addChild(particle);
        }
        return parentParticle;
    }
}
