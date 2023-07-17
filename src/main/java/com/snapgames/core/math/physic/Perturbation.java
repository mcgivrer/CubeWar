package com.snapgames.core.math.physic;

import com.snapgames.core.entity.Entity;

import java.awt.*;

/**
 * A {@link Perturbation} is a rectangle area into the {@link World#playArea}
 * where Entity will be influenced by
 * some physic changes.
 * <p>
 * It can be an attraction factor applied to any entity in this perturbation
 * area, or a new force added to the {@link Entity},
 * this both thing are applied at computation time into the
 * {@link PhysicEngine#updateEntity(Entity, double)} processing method.
 */
public class Perturbation extends Entity<Perturbation> {
    private double attraction;

    public Perturbation(String n, double x, double y, double w, double h) {
        super(n, x, y, w, h);
    }

    @Override
    public void draw(Graphics2D g) {
        // nothing to draw, perturbation is only a virtual element applying its effect
        // to other entities.
    }

    @Override
    public void drawDebug(Graphics2D g) {
        g.setColor(new Color(0.1f, 0.5f, 1.0f, 0.4f));
        g.fill(this);
    }

    /**
     * Define Attraction level for this {@link Perturbation}, relatively applied from its center.
     *
     * @param attraction a double factor from 0.0 to 1.0
     * @return updated {@link Perturbation} object.
     */
    public Perturbation setAttraction(double attraction) {
        this.attraction = attraction;
        return this;
    }

    /**
     * Define a force applied to any {@link Entity} contained by this {@link Perturbation}
     *
     * @param f the Vector2D force to be applied to contained {@link Entity}.
     * @return updated {@link Perturbation} object.
     */
    public Perturbation setForce(Vector2D f) {
        this.forces.add(f);
        return this;
    }

    /**
     * Test if the e {@link Entity} is contained by the {@link Perturbation}.
     *
     * @param e
     * @return true if {@link Perturbation} contains the e {@link Entity}.
     */
    public boolean isEntityConstrained(Entity e) {
        return this.contains(e) || this.intersects(e);
    }

}
