package com.snapgames.core.math.physic.entity;

import com.snapgames.core.entity.Entity;
import com.snapgames.core.math.Vector2D;
import com.snapgames.core.math.physic.PhysicEngine;
import com.snapgames.core.math.physic.World;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Perturbation} is a rectangle area into the {@link World#playArea}
 * where Entity will be influenced by
 * some physic changes.
 * <p>
 * It can be an attraction factor applied to any entity in this perturbation
 * area, or a new force added to the {@link Entity},
 * this both thing are applied at computation time into the
 * {@link PhysicEngine#updateEntity(Entity, double)} processing method.
 *
 * @author Frédéric Delorme
 * @since 1.0.1
 */
public class Perturbation extends Entity<Perturbation> {
    private double attraction;
    private final List<Integer> impactedlayers = new ArrayList<>();

    /**
     * Create a {@link Perturbation} with a name on defined rectangular area (x,y,w,h)
     *
     * @param name name of the perturbation area
     * @param x    horizontal coordinate
     * @param y    vertical coordinate
     * @param w    width of the rectangular perturbation area
     * @param h    height of the rectangular perturbation area
     */
    public Perturbation(String name, double x, double y, double w, double h) {
        super(name, x, y, w, h);
    }

    /**
     * Create a {@link Perturbation} with a name on defined rectangular area (x,y,w,h)
     * but active only on a list of define impacted layers.
     *
     * @param name           name of the perturbation area
     * @param x              horizontal coordinate
     * @param y              vertical coordinate
     * @param w              width of the rectangular perturbation area
     * @param h              height of the rectangular perturbation area
     * @param impactedLayers list of layer nbumber impacted by this new {@link Perturbation}.
     */
    public Perturbation(String name, double x, double y, double w, double h, int... impactedLayers) {
        super(name, x, y, w, h);
        this.impactedlayers.addAll(impactedlayers);
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
        // check if entity intersect or is contained by the Perturbation
        // AND check Entity on its own layer matches the perturbation layer, if defined.
        return (this.contains(e) || this.intersects(e))
                && (getLayers().size() > 0 ? getLayers().stream().anyMatch(l -> l == e.getLayer()) : getLayers().size() == 0 ? true : false);
    }

    /**
     * Add dynamically the id number of the new impacted layer.
     *
     * @param layerId the unique id of the new impacted layer
     * @return updated Perturbation.
     */
    public Perturbation addLayer(int layerId) {
        impactedlayers.add(layerId);
        return this;
    }

    public List<Integer> getLayers() {
        return impactedlayers;
    }
}
