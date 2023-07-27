package com.snapgames.core.graphics.plugins;

import com.snapgames.core.entity.GameObject;
import com.snapgames.core.graphics.Renderer;

import java.awt.*;
import java.util.Optional;

/**
 * The {@link GameObjectRendererPlugin} implements the draw operation for the {@link GameObject}.
 *
 * @author Frédéric Delorme
 * @see RendererPlugin
 * @since 1.0.0
 */
public class GameObjectRendererPlugin implements RendererPlugin<GameObject> {
    @Override
    public Class<?> getEntityClass() {
        return GameObject.class;
    }

    @Override
    public void draw(Renderer r, Graphics2D g, GameObject entity) {
        switch (entity.type) {
            case TYPE_POINT -> {
                if (entity.getColor() != null) {
                    g.setColor(entity.getColor());
                    g.drawRect((int) entity.pos.x, (int) entity.pos.y, 1, 1);
                }
            }
            case TYPE_LINE -> {
                if (entity.getColor() != null) {
                    g.setColor(entity.getColor());
                    g.drawLine((int) entity.pos.x, (int) entity.pos.y, (int) entity.oldPos.x, (int) entity.oldPos.y);
                }
            }
            case TYPE_RECTANGLE -> {
                if (entity.getFillColor() != null) {
                    g.setColor(entity.getFillColor());
                    g.fill(entity);
                }
                if (entity.getColor() != null) {
                    g.setColor(entity.getColor());
                    g.draw(entity);
                }
            }
            case TYPE_ELLIPSE -> {
                if (entity.getFillColor() != null) {
                    g.setColor(entity.getFillColor());
                    g.fillOval((int) entity.pos.x, (int) entity.pos.y, (int) entity.width, (int) entity.height);
                }
                if (entity.getColor() != null) {
                    g.setColor(entity.getColor());
                    g.drawOval((int) entity.pos.x, (int) entity.pos.y, (int) entity.width, (int) entity.height);
                }
            }
            case TYPE_IMAGE -> {
                if (Optional.ofNullable(entity.getImage()).isPresent()) {
                    if (entity.vel.x > 0) {
                        g.drawImage(entity.getImage(), (int) entity.pos.x, (int) entity.pos.y, null);
                    } else {
                        g.drawImage(entity.getImage(),
                                (int) (entity.pos.x - entity.width),
                                (int) entity.pos.y,
                                (int) -entity.width,
                                (int) entity.height,
                                null);
                    }
                }
            }
            default -> System.err.printf("Unknown Entity type %d%n", entity.type);
        }
    }
}
