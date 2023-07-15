package com.snapgames.core.entity;

import com.snapgames.core.Application;
import com.snapgames.core.math.Vector2D;
import com.snapgames.core.math.physic.World;

import java.awt.*;

/**
 * A {@link Perturbation} is a rectangle area into the {@link World#playArea}
 * where Entity will be influenced by
 * some physic changes.
 * <p>
 * It can be an attraction factor applied to any entity in this perturbation
 * area, or a new force added to the {@link Entity},
 * this both thing are applied at computation time into the
 * {@link Application#updateEntity(Entity, double)} processing method.
 */
public class Perturbation extends Entity<Perturbation> {
    private double attraction;

    public Perturbation(String n, double x, double y, int w, int h) {
        super(n, x, y, w, h);
    }

    @Override
    public void draw(Graphics2D g) {
        // nothing to draw, perturbation is only a virtual element applying its effect
        // to other entities.
    }

    public Perturbation setAttraction(double attraction) {
        this.attraction = attraction;
        return this;
    }

    public Perturbation setForce(Vector2D f) {
        this.forces.add(f);
        return this;
    }

}
