package com.snapgames.core.math.physic;

import com.snapgames.core.entity.Entity;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class SpacePartition extends Rectangle2D.Double {
    private static SpacePartition root;
    private SpacePartition[] childSpace = new SpacePartition[4];

    private List<Entity<?>> entities = new ArrayList<>();
    private int maxEntities;

    public SpacePartition getRoot() {
        return root;
    }

    public SpacePartition setMaxEntities(int maxEntities) {
        this.maxEntities = maxEntities;
        root = this;
        return this;
    }

    public SpacePartition(double x, double y, double w, double h) {
        this.setRect(x, y, w, h);
    }

    public SpacePartition(Rectangle2D space) {
        if (space != null) {
            childSpace[0] = new SpacePartition(
                space.getX(), space.getY(),
                space.getWidth() * 0.5, space.getHeight() * 0.5);
            childSpace[1] = new SpacePartition(
                space.getX() + space.getWidth() * 0.5, space.getY(),
                space.getWidth() * 0.5, space.getHeight() * 0.5);
            childSpace[2] = new SpacePartition(
                space.getX(), space.getY() + space.getHeight() * 0.5,
                space.getWidth() * 0.5, space.getHeight() * 0.5);
            childSpace[3] = new SpacePartition(
                space.getX() + space.getWidth() * 0.5, space.getY() + space.getHeight() * 0.5,
                space.getWidth() * 0.5, space.getHeight() * 0.5);
        }

    }

    public void add(Entity<?> e) {
        for (int i = 0; i < 4; i++) {
            if (childSpace[i].contains(e) || childSpace[i].intersects(e)) {
                childSpace[i].add(e);
                if (childSpace[i].entities.size() > maxEntities) {
                    childSpace[i] = new SpacePartition(childSpace[i]);
                    childSpace[i].add(e);
                } else {
                    childSpace[i].entities.add(e);
                    e.setAttribute("childSpace", childSpace[i]);
                }
            }
        }
    }

    public List<Entity<?>> find(Entity<?> e) {
        SpacePartition sp = (SpacePartition) e.getAttribute("childSpace", null);
        return sp != null ? sp.getEntities() : null;
    }

    private List<Entity<?>> getEntities() {
        return entities;
    }
}
