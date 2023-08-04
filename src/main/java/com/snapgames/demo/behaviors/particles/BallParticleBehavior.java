package com.snapgames.demo.behaviors.particles;

import com.snapgames.core.behavior.ParticleBehavior;
import com.snapgames.core.entity.Entity;
import com.snapgames.core.entity.GameObject;
import com.snapgames.core.entity.GameObjectType;
import com.snapgames.core.math.Vector2D;
import com.snapgames.core.math.physic.Material;
import com.snapgames.core.math.physic.PhysicType;
import com.snapgames.core.math.physic.World;
import com.snapgames.core.utils.particles.ParticleSystemBuilder;

import java.awt.Color;

/**
 * The Ball Particle animation behavior to be applied on any Ball particle
 * system.to be used with the {@link ParticleSystemBuilder}
 *
 * @author Frédéric Delorme
 * @since 1.0.3
 */
public class BallParticleBehavior implements ParticleBehavior<GameObject> {

    private int internalTime = 0;
    private final double ballForce;
    private final double appFreq;

    /**
     * Create a new animation behavior for a Particle System created with the
     * {@link ParticleSystemBuilder}.
     *
     * @param forceToBeAppliedOnBall the force to be applied to all balls on a
     *                               defined frequency
     * @param applyingFrequency      the frequency to apply the defined force to
     *                               any ball in the particle system.
     */
    public BallParticleBehavior(double forceToBeAppliedOnBall, double applyingFrequency) {
        ballForce = forceToBeAppliedOnBall;
        appFreq = applyingFrequency;
    }

    @Override
    public GameObject create(World parentWorld, double elapsed, String particleNamePrefix,
                             GameObject parent) {

        return new GameObject(
            particleNamePrefix + "_" + GameObject.index)
            .setPosition(
                Math.random() * parentWorld.getPlayArea().getWidth(),
                Math.random() * parentWorld.getPlayArea().getHeight() * 0.1)
            .setSize(8, 8)
            .setPriority(1)
            .setType(GameObjectType.TYPE_RECTANGLE)
            .setConstrainedToPlayArea(true)
            .setLayer(3)
            .setPhysicType(PhysicType.DYNAMIC)
            .setColor(Color.RED.darker().darker())
            .setFillColor(Color.RED)
            .setMaterial(Material.RUBBER)
            .setMass(30.0 * Math.random() + 20.0)
            .setParent(parent)
            .addBehavior(this)
            .setAttribute("energy", Math.random() * 20.0)
            .addForce(
                new Vector2D(
                    -0.15 + Math.random() * 0.30,
                    -0.15 + Math.random() * 0.30));
    }

    /**
     * Update the Entity e according to the elapsed time since previous call.
     *
     * @param e       the Entity to be updated
     * @param elapsed the elapsed time since previous call.
     */
    @Override
    public void update(Entity<?> e, double elapsed) {
        internalTime += (int) elapsed;
        if (internalTime > appFreq) {
            internalTime = 0;
            e.addForce(
                new Vector2D(
                    -(ballForce) + Math.random() * ballForce * 2.0,
                    -(ballForce) + Math.random() * ballForce * 2.0));
        }
    }
}
