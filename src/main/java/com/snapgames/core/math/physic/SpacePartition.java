package com.snapgames.core.math.physic;

import com.snapgames.core.Application;
import com.snapgames.core.entity.Entity;
import com.snapgames.core.graphics.Renderer;
import com.snapgames.core.scene.Scene;
import com.snapgames.core.system.GSystem;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * A space partitioning system to dispatch Scene's {@link Entity} list into some {@link SpacePartition}, to accelerate
 * any neighbour operation like collision detection.
 * <p>
 * This partitioning system is used from {@link CollisionDetection} system.
 * During collision detection, {@link CollisionEvent} are created and processed by
 * {@link com.snapgames.core.behavior.CollisionResponseBehavior} at {@link Entity} level.
 * <p>
 * At each {@link PhysicEngine}  update cycle, the inactive entities are removed from scene.
 *
 * @author Frédéric Delorme
 * @see CollisionDetection
 * @see PhysicEngine
 * @since 1.0.4
 */
public class SpacePartition extends Rectangle2D.Double implements GSystem {
    private int maxObjectsPerNode = 10;
    private int maxTreeLevels = 5;

    private SpacePartition root;

    private int level;
    private List<Entity<?>> objects;
    private SpacePartition[] nodes;

    /**
     * Create a new {@link SpacePartition} with a depth level and its defined rectangle area.
     *
     * @param pLevel  the  depth level for this {@link SpacePartition}
     * @param pBounds the Rectangle area covered by this {@link SpacePartition}.
     */
    public SpacePartition(int pLevel, Rectangle pBounds) {
        level = pLevel;
        objects = new ArrayList<>();
        setRect(pBounds);
        nodes = new SpacePartition[4];
    }

    /**
     * Initialize the {@link SpacePartition} {@link GSystem}'s implementation according to the defined configuration.
     * <p>
     * The configuration file will provide 2 parameters:
     *     <ul>
     *         <li><code>app.physic.space.max.entities</code> is the maximum number of entities that a SpacePartition node can contain,</li>
     *         <li><code>app.physic.space.max.levels</code> is the max Depth level the tree hierarchy can contain.</li>
     *     </ul>
     * </p>
     *
     * @param app the parent {@link Application} instance.
     */
    public SpacePartition(Application app) {
        this(0, app.getConfiguration().world.getPlayArea().getBounds());
        this.maxObjectsPerNode = app.getConfiguration().maxEntitiesInSpace;
        this.maxTreeLevels = app.getConfiguration().maxLevelsInSpace;
    }

    /**
     * Clears the {@link SpacePartition} nodes.
     */
    public void clear() {
        objects.clear();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    /**
     * Split the current SpacePartition into 4 sub spaces.
     */
    private void split() {
        int subWidth = (int) (getWidth() / 2);
        int subHeight = (int) (getHeight() / 2);
        int x = (int) getX();
        int y = (int) getY();
        nodes[0] = new SpacePartition(level + 1, new Rectangle(x + subWidth, y, subWidth, subHeight));
        nodes[1] = new SpacePartition(level + 1, new Rectangle(x, y, subWidth, subHeight));
        nodes[2] = new SpacePartition(level + 1, new Rectangle(x, y + subHeight, subWidth, subHeight));
        nodes[3] = new SpacePartition(level + 1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight));
    }

    /**
     * Determine which {@link SpacePartition} node the {@link Entity} belongs to.
     *
     * @param pRect the {@link Entity} to search in the {@link SpacePartition}'s tree.
     * @return the depth level of the {@link Entity}; -1 means object cannot completely fit
     * within a child node and is part of the parent node
     */
    private int getIndex(Entity<?> pRect) {
        int index = -1;
        double verticalMidpoint = getX() + (getWidth() / 2);
        double horizontalMidpoint = getY() + (getHeight() / 2);
        // Object can completely fit within the top quadrants
        boolean topQuadrant = (pRect.getY() < horizontalMidpoint && pRect.getY() + pRect.getHeight() < horizontalMidpoint);
        // Object can completely fit within the bottom quadrants
        boolean bottomQuadrant = (pRect.getY() > horizontalMidpoint);
        // Object can completely fit within the left quadrants
        if (pRect.getX() < verticalMidpoint && pRect.getX() + pRect.getWidth() < verticalMidpoint) {
            if (topQuadrant) {
                index = 1;
            } else if (bottomQuadrant) {
                index = 2;
            }
        }
        // Object can completely fit within the right quadrants
        else if (pRect.getX() > verticalMidpoint) {
            if (topQuadrant) {
                index = 0;
            } else if (bottomQuadrant) {
                index = 3;
            }
        }
        return index;
    }


    /**
     * Insert the {@link Entity} into the {@link SpacePartition} tree. If the node
     * exceeds the capacity, it will split and add all
     * objects to their corresponding nodes.
     *
     * @param pRect the {@link Entity} to insert into the tree.
     */
    public void insert(Entity<?> pRect) {
        if (nodes[0] != null) {
            int index = getIndex(pRect);
            if (index != -1) {
                nodes[index].insert(pRect);
                return;
            }
        }
        objects.add(pRect);
        if (objects.size() > maxObjectsPerNode && level < maxTreeLevels) {
            if (nodes[0] == null) {
                split();
            }
            int i = 0;
            while (i < objects.size()) {
                int index = getIndex(objects.get(i));
                if (index != -1) {
                    nodes[index].insert(objects.remove(i));
                } else {
                    i++;
                }
            }
        }
    }

    /**
     * Find the {@link Entity} into the {@link SpacePartition} tree and return the list of neighbour's entities.
     *
     * @param e the entity to find.
     * @return a list of neighbour's entities.
     */
    public List<Entity<?>> find(Entity<?> e) {
        List<Entity<?>> list = new ArrayList<>();
        return find(list, e);
    }


    /*
     * Return all objects that could collide with the given object
     */
    private List find(List returnObjects, Entity<?> pRect) {
        int index = getIndex(pRect);
        if (index != -1 && nodes[0] != null) {
            nodes[index].find(returnObjects, pRect);
        }
        returnObjects.addAll(objects);
        return returnObjects;
    }

    /**
     * Dispatch all the {@link Scene} {@link Entity}'s into the {@link SpacePartition} tree.
     *
     * @param scene   the Scene to be processed.
     * @param elapsed the elapsed time since previous call (not used here).
     */
    public void update(Scene scene, double elapsed) {
        this.clear();
        scene.getEntities().forEach(e -> this.insert(e));
    }


    @Override
    public Class<? extends GSystem> getSystemName() {
        return SpacePartition.class;
    }

    @Override
    public void initialize(Application app) {
        this.root = this;
    }

    @Override
    public void dispose() {

    }

    /**
     * Draw all {@link SpacePartition} nodes with a following color code:
     * <ul>
     *     <li><code>RED</code> the node is full,</li>
     *     <li><code>ORANGE</code> the node has entities but is not full,</li>
     *     <li><code>GREEN</code> the node is empty.</li>
     * </ul>
     *
     * @param r     the {@link Renderer} instance
     * @param g     the {@link Graphics2D} API instance
     * @param scene the {@link Scene} to be processed.
     */
    public void draw(Renderer r, Graphics2D g, Scene scene) {
        g.setFont(g.getFont().deriveFont(8.5f));
        SpacePartition sp = this;
        if (objects.isEmpty()) {
            g.setColor(Color.GREEN);
        } else if (objects.size() < maxObjectsPerNode) {
            g.setColor(Color.ORANGE);
        } else {
            g.setColor(Color.RED);
        }
        g.draw(this);
        g.setColor(Color.ORANGE);
        g.drawString("s:" + this.objects.size(), (int) (this.x + 4), (int) (this.y + 10));
        if (this.nodes != null) {
            for (SpacePartition node : nodes) {
                if (node != null) {
                    node.draw(r, g, scene);
                }
            }
        }
    }
}
