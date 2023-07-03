package com.snapgames.core.math.physic;

import com.snapgames.core.entity.Entity;

/**
 * The {@link Material} object contains all the physic attribute for a material.
 * <p>
 * It contains
 * <ul>
 * <li>a <code>name</code> to just debug purpose and understand the attribute's
 * values for this {@link Material},</li>
 * <li>a <code>density</code> value (double type),</li>
 * <li>an <code>elasticity</code> factor (0.0 to 1.0),</li>
 * <li>a <code>roughness</code> factor (0.0 to 1.0).</li>
 * </ul>
 * <p>
 * It will be used :
 * <ul>
 * <li>to be applied to any {@link Entity}</li>
 * <li>used by the physic computation done at
 * {@link PhysicEngine#updateEntity(Entity, double)} processing.</li>
 * </ul>
 */
public class Material {
    public static final Material DEFAULT = new Material("default", 0.0, 1.0, 1.0);
    public static final Material RUBBER = new Material("rubber", 0.68, 0.7, 0.67);
    public static final Material SUPER_BALL = new Material("super_ball", 0.98, 0.7, 0.998);
    public static final Material WOOD = new Material("wood", 0.20, 0.65, 0.50);
    public static final Material STEEL = new Material("steel", 0.10, 1.2, 0.12);
    public static final Material AIR = new Material("air", 0.0, 0.05, 0.9999);
    public static final Material WATER = new Material("water", 0.80, 0.997, 1.0);

    private String name;
    private double density;
    private double elasticity;
    private double roughness;

    /**
     * Craete a new {@link Material} with a name, a density, an elasticity
     * (bounciness), and
     * a roughness to compute friction.
     *
     * @param n the name for that {@link Material}.
     * @param e the elasticity or bounciness for this {@link Material}
     * @param d the density for this {@link Material}.
     * @param r the roughness or friction for this {@link Material}
     */
    public Material(String n, double e, double d, double r) {
        this.name = n;
        this.elasticity = e;
        this.density = d;
        this.roughness = r;
    }

    public Material setName(String name) {
        this.name = name;
        return this;
    }

    public Material setDensity(double density) {
        this.density = density;
        return this;
    }

    public Material setElasticity(double elasticity) {
        this.elasticity = elasticity;
        return this;
    }

    public Material setRoughness(double roughness) {
        this.roughness = roughness;
        return this;
    }

    public String getName() {
        return name;
    }

    public double getDensity() {
        return density;
    }

    public double getElasticity() {
        return elasticity;
    }

    public double getRoughness() {
        return roughness;
    }


    public String toString() {
        return "{n:'" + name + "',d:" + density + ",e:" + elasticity + ",r:" + roughness + "}";
    }

}
