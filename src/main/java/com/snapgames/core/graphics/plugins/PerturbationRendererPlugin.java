package com.snapgames.core.graphics.plugins;

import com.snapgames.core.Application;
import com.snapgames.core.graphics.Renderer;
import com.snapgames.core.math.physic.entity.Perturbation;
import com.snapgames.core.scene.Scene;

import java.awt.*;
import java.util.Optional;

public class PerturbationRendererPlugin implements RendererPlugin<Perturbation> {
    @Override
    public Class<?> getEntityClass() {
        return Perturbation.class;
    }

    @Override
    public void draw(Renderer r, Graphics2D g, Perturbation entity) {

    }

    @Override
    public void drawDebugInfo(Application application, Scene scene, Renderer r, Graphics2D g, Perturbation e) {
        RendererPlugin.super.drawDebugInfo(application, scene, r, g, e);
        g.setColor(e.getFillColor());
        g.fill(e);
        if (Optional.ofNullable(e.getColor()).isPresent()) {
            g.setColor(e.getColor());
            g.draw(e);
        }
    }
}
