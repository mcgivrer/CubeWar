package com.snapgames.core.entity;

import com.snapgames.core.math.physic.World;

import java.util.List;

/**
 * La {@link Camera} qui permet de suivre une entité {@link Entity}.
 * <p>
 * The {@link Camera} object intends to track another {@link Entity} across the
 * play area (now defined in the {@link World#playArea} object).
 * <p>
 * So to use it, just add it to the scene, and set the {@link Camera#target}'s
 * {@link Entity} and
 * a {@link Camera#tween} factor to set the {@link Camera} velocity onthe
 * tracking.
 */
public class Camera extends Entity<Camera> {

    private Entity target;
    private double tween;

    public Camera(String n, int vpWidth, int vpHeight) {
        super(n, 0, 0, vpWidth, vpHeight);
        setMaterial(null);
        setMass(0.0);
    }

    public void setTarget(Entity t) {
        this.target = t;
    }

    public void setTween(double t) {
        this.tween = t;
    }

    @Override
    public void update(double elapsed) {
        this.rotation += dRotation;
        this.x += (target.x - ((this.width - target.width) * 0.5) - this.x) * tween * elapsed;
        this.y += (target.y - ((this.height - target.height) * 0.5) - this.y) * tween * elapsed;
        super.update(elapsed);
    }

    @Override
    public List<String> getDebugInfo() {
        List<String> infos = super.getDebugInfo();
        infos.add(String.format("3_target:%s", target.name));
        infos.add(String.format("4_vp:%fx%f", width, height));
        return infos;
    }

    public boolean inViewport(Entity<? extends Entity<?>> e) {
        return getBounds2D().contains(e.getBounds2D());
    }
}
