package com.snapgames.core.math.physic;

import com.snapgames.core.Application;
import com.snapgames.core.entity.Entity;
import com.snapgames.core.entity.Perturbation;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@link World} defines the context and environment where all
 * {@link Application}'s {@link Entity} will evolve.
 * <p>
 * A <code>name</code>, a <code>playArea</code> and a <code>gravity</code> are
 * the first mandatory things.
 * the <code>perturbations</code> list will be implemented and used to influence
 * {@link Entity} in certain places into the world play area.
 */
public class World {
    private String name = "defaultWorld";
    private Rectangle2D playArea;
    private Vector2D gravity;
    private Material material = Material.AIR;

    public List<Perturbation> perturbations = new ArrayList<>();

    public World(String name) {
        this.name = name;
    }

    public World setGravity(Vector2D g) {
        this.gravity = g;
        return this;
    }

    public World setPlayArea(Rectangle2D pa) {
        this.playArea = pa;
        return this;
    }

    public World add(Perturbation p) {
        this.perturbations.add(p);
        return this;
    }

    public Vector2D getGravity() {
        return gravity;
    }

    public Material getMaterial() {
        return this.material;
    }

    public Rectangle2D getPlayArea() {
        return this.playArea;
    }

    public List<Perturbation> getPerturbations() {
        return this.perturbations;
    }

    @Override
    public String toString() {
        return "World{" +
                "name='" + name + '\'' +
                ", playArea=(" + playArea.getWidth() + "x" + playArea.getHeight() + ")" +
                ", gravity=" + gravity +
                '}';
    }
}
