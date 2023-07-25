package com.snapgames.core.graphics.plugins;

import com.snapgames.core.Application;
import com.snapgames.core.entity.Camera;
import com.snapgames.core.entity.Entity;
import com.snapgames.core.graphics.Renderer;
import com.snapgames.core.scene.Scene;

import java.awt.*;
import java.util.List;
import java.util.Optional;

/**
 * The {@link RendererPlugin} interface define the required methods to implement the draw operations
 * for any {@link Entity} to be drawn on screen.
 *
 * @param <T> the {@link Entity} inherited class to be processed by the RendererPlugin implementation.
 * @author Frédéric Delorme
 * @see RendererPlugin
 * @since 1.0.0
 */
public interface RendererPlugin<T extends Entity<?>> {

    /**
     * Retrieve the child inherited {@link Entity} class
     *
     * @return the class of the {@link Entity} type processed by this plugin implementation.
     */
    Class<?> getEntityClass();

    /**
     * Draw the T {@link Entity} according to the corresponding type
     *
     * @param r      the {@link Renderer} service instance
     * @param g      the Graphics2D API instance to use to render this entity.
     * @param entity the instance of the Entity T to be drawn.
     */
    void draw(Renderer r, Graphics2D g, T entity);

    /**
     * Draw all info from this entity to the display debug mode.
     *
     * @param application the parent {@link Application}
     * @param scene       the parent {@link Scene} for this entity
     * @param r           the {@link Renderer} instance
     * @param g           the {@link Graphics2D} API instance
     * @param e           the {@link Entity} to draw debug info for.
     */
    default void drawDebugInfo(Application application, Scene scene, Renderer r, Graphics2D g, T e) {
        if (application.getConfiguration().debug
                && application.getConfiguration().debugLevel > 0
                && application.getConfiguration().debugLevel >= e.debug
                && application.getConfiguration().debugFilter.contains(e.getName())) {
            List<String> info = e.getDebugInfo();
            int l = 0;
            float fontSize = 9f;
            g.setFont(g.getFont().deriveFont(fontSize));
            Camera activeCamera = scene.getActiveCamera();
            int maxWidth = info.stream().mapToInt(s -> g.getFontMetrics().stringWidth(s)).max().orElse(0);
            int offsetX = 4;
            int offsetY = 0;
            if (Optional.ofNullable(activeCamera).isPresent()) {
                offsetX = (int) (e.pos.x + maxWidth > (
                        (e.stickToCamera ? 0 : scene.getActiveCamera().x) + scene.getActiveCamera().width) ? -(maxWidth + 4.0)
                        : 4.0);
                long nbLines = info.stream().filter(i -> Integer.parseInt((i.contains("_") ? i.substring(0, i.indexOf("_")) : "0")) <= application.getConfiguration().debugLevel).count();
                offsetY = (int) (e.pos.y + (fontSize * nbLines) >
                        ((e.stickToCamera ? 0 : scene.getActiveCamera().y) + scene.getActiveCamera().height)
                        ? -(9.0 + (fontSize * nbLines))
                        : 0);
            }
            g.setColor(Color.ORANGE);
            for (String item : info) {
                if (!item.equals("")) {
                    String levelStr = item.contains("_") ? item.substring(0, item.indexOf("_")) : "0";
                    int level = Integer.parseInt(levelStr);
                    if (level <= application.getConfiguration().debugLevel) {
                        g.drawString(item.substring(info.indexOf("_") + 1),
                                (int) (e.pos.x + e.getWidth() + offsetX),
                                (int) (e.pos.y + offsetY + (l * fontSize)));
                        l++;
                    }
                }
            }
        }
    }
}
