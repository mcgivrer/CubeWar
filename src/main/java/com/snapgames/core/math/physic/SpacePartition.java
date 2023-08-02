package com.snapgames.core.math.physic;

import com.snapgames.core.Application;
import com.snapgames.core.entity.Entity;
import com.snapgames.core.scene.Scene;
import com.snapgames.core.system.GSystem;

import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SpacePartition extends Rectangle2D.Double implements GSystem {
    private static SpacePartition root;
    private static Map<Entity<?>, SpacePartition> mapping = new ConcurrentHashMap<>();
    private static int maxEntities = 10;
    private SpacePartition[] childSpace = new SpacePartition[4];

    private List<Entity<?>> entities = new ArrayList<>();

    public SpacePartition getRoot() {
        return root;
    }

    public SpacePartition setMaxEntities(int maxEntities) {
        SpacePartition.maxEntities = maxEntities;
        root = this;
        return this;
    }

    public SpacePartition(Application app) {
        // nothing to do right now.
    }

    public SpacePartition(double x, double y, double w, double h) {
        this.setRect(x, y, w, h);
        if (Optional.ofNullable(root).isEmpty()) {
            this.root = this;
        }
    }

    public SpacePartition(Rectangle2D space) {
        initializeSubSpace(space);

    }

    private void initializeSubSpace(Rectangle2D space) {
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
        // if entity exists in the tree, remove it from exiting branch
        if (mapping.containsKey(e)) {
            mapping.get(e).getEntities().remove(e);
        }
        // then try to add it to the corresponding tree branch.
        if (getEntities().size() < maxEntities) {
            if (!this.getEntities().contains(e)) {
                this.getEntities().add(e);
                mapping.put(e, this);
            }
        } else {
            moveToSubSpace(this);
            insertIntoSubSpace(this, e);
        }
    }

    private void moveToSubSpace(SpacePartition spacePartition) {
        spacePartition.getEntities().forEach(e -> insertIntoSubSpace(spacePartition, e));
    }

    private void insertIntoSubSpace(SpacePartition p, Entity<?> e) {
        for (int i = 0; i < 4; i++) {
            if ((p.childSpace[i].contains(e) || p.childSpace[i].intersects(e))) {
                if (p.childSpace[i].entities.size() > maxEntities) {
                    p.childSpace[i] = new SpacePartition(p.childSpace[i]);
                    p.childSpace[i].add(e);
                } else {
                    p.childSpace[i].entities.add(e);
                    mapping.put(e, p.childSpace[i]);
                    e.setAttribute("childSpace", p.childSpace[i]);
                    return;
                }
            }
        }
    }

    /**
     * Search for {@link SpacePartition} instance containing this {@link Entity}.
     *
     * @param e the Entity to be found
     * @return the SpacePartition instance containing the {@link Entity} looking at.
     */
    public List<Entity<?>> find(Entity<?> e) {
        if (!mapping.containsKey(e)) {
            add(e);
        }
        if (mapping.get(e) == null || mapping.get(e).getEntities() == null) {
            return new ArrayList<>();
        }
        return mapping.get(e).getEntities();
    }

    /**
     * Parse all the {@link SpacePartition} child structure to find the {@link Entity}, and then return the list
     * of entities contained by the corresponding {@link SpacePartition}.
     *
     * @param spacePartition the tre space partition branch to search from.
     * @param e              the entity to be found
     * @return the list of {@link Entity} from the contained {@link SpacePartition}.
     */
    @Deprecated
    private List<Entity<?>> find(SpacePartition spacePartition, Entity<?> e) {
        Entity<?> found = spacePartition.getEntities().stream().filter(i -> i.getName().equals(e.name)).findFirst().orElse(null);
        if (Optional.ofNullable(found).isEmpty()) {
            for (int i = 0; i < childSpace.length; i++) {
                return find(childSpace[i], e);
            }
        }
        return spacePartition.getEntities();
    }

    /**
     * Return the list of {@link Entity} of this {@link SpacePartition}.
     *
     * @return
     */
    private List<Entity<?>> getEntities() {
        return entities;
    }

    /**
     * Clear everything ion the {@link SpacePartition} tree.
     */
    public void clear() {
        clear(root);
    }

    /**
     * Clear everything in a specific  {@link SpacePartition}.
     */

    private void clear(SpacePartition sp) {
        if (sp != null) {
            sp.getEntities().clear();
            for (int i = 0; i < sp.childSpace.length; i++) {
                if (sp != null) {
                    sp.clear(sp.childSpace[i]);
                }
            }
        }
    }


    @Override
    public Class<? extends GSystem> getSystemName() {
        return SpacePartition.class;
    }

    @Override
    public void initialize(Application app) {
        this.root = this;
        setRect(app.getConfiguration().world.getPlayArea());
        setMaxEntities(app.getConfiguration().maxEntitiesInSpace);
        initializeSubSpace(root);
    }

    public void update(Scene scn, double elapsed) {
        scn.getEntities().forEach(e -> add(e));
    }

    @Override
    public void dispose() {
        clear();
    }
}
