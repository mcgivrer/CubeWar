package com.snapgames.demo.particles;

import com.snapgames.core.behavior.ParticleBehavior;
import com.snapgames.core.entity.Entity;
import com.snapgames.core.entity.GameObject;
import com.snapgames.core.entity.GameObjectType;
import com.snapgames.core.math.Vector2D;
import com.snapgames.core.math.physic.Material;
import com.snapgames.core.math.physic.PhysicEngine;
import com.snapgames.core.math.physic.PhysicType;
import com.snapgames.core.math.physic.World;
import com.snapgames.core.scene.Scene;
import com.snapgames.core.scene.SceneManager;
import com.snapgames.core.system.GSystemManager;

import java.awt.*;
import java.awt.geom.Point2D;

public class RainParticleBehavior implements ParticleBehavior<GameObject> {
    public GameObject create(World parentWorld, double elapsed, String particleNamePrefix,
                             Entity<?> parent) {

        return new GameObject(
                particleNamePrefix + "_" + GameObject.index)
                .setPosition(
                        Math.random() * parentWorld.getPlayArea().getWidth(),
                        Math.random() * parentWorld.getPlayArea().getHeight() * 0.1)
                .setSize(1, 1)
                .setPriority(1)
                .setType(GameObjectType.TYPE_LINE)
                .setConstrainedToPlayArea(false)
                // set depth to the rain drop.
                .setLayer((int) (Math.random() * 9) + 1)
                .setPhysicType(PhysicType.DYNAMIC)
                .setColor(Color.YELLOW)
                .setMaterial(Material.WATER)
                .setMass(1.0)
                .setParent(parent)
                .addBehavior(this)
                .addForce(new Vector2D(0.0, Math.random() * 0.0003 * parentWorld.getGravity().y));
    }

    /**
     * Update the Entity e according to the elapsed time since previous call.
     *
     * @param e       the Entity to be updated
     * @param elapsed the elapsed time since previous call.
     */
    public void update(Entity<?> e, double elapsed) {
        Scene scene = ((SceneManager) GSystemManager.find(SceneManager.class)).getCurrent();
        World parentWorld = ((PhysicEngine) GSystemManager.find(PhysicEngine.class)).getWorld();

        int layer = e.getLayer();
        e.setColor(new Color((layer * 0.1f), (layer * 0.1f), (layer * 0.1f), (layer * 0.1f)));
        if (!parentWorld.getPlayArea().getBounds2D().contains(new Point2D.Double(e.x, e.y))) {
            e.setPosition(parentWorld.getPlayArea().getWidth() * Math.random(),
                    Math.random() * parentWorld.getPlayArea().getHeight() * 0.1);
            e.setOldPosition(e.x, e.y);

        }
        GameObject parent = (GameObject) e.parent;
        double time = parent.getAttribute("particleTime", 0.0);
        double particleTimeCycle = parent.getAttribute("particleTimeCycle", 980.0);
        double particleFreq = parent.getAttribute("particleFreq", 0.005);
        time += elapsed;
        int nbP = parent.getAttribute("nbParticles", 0);
        if (parent.getChild().size() < nbP && time > particleTimeCycle) {
            for (int i = 0; i < nbP * particleFreq; i++) {
                GameObject particle = this.create(parentWorld, 0, parent.name, parent);
                parent.addChild(particle);
                scene.addEntity(particle);
            }
            time = 0;
        }
        parent.setAttribute("particleTime", time);
    }
}
