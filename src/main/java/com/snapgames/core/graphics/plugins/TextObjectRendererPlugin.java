package com.snapgames.core.graphics.plugins;

import com.snapgames.core.entity.Entity;
import com.snapgames.core.entity.TextObject;
import com.snapgames.core.graphics.Renderer;

import java.awt.*;
import java.util.Optional;

import static com.snapgames.core.entity.TextObject.*;

public class TextObjectRendererPlugin implements RendererPlugin<TextObject> {
    @Override
    public Class<?> getEntityClass() {
        return TextObject.class;
    }

    @Override
    public void draw(Renderer r, Graphics2D g, TextObject entity) {
        if (Optional.ofNullable(entity.getFont()).isPresent()) {
            g.setFont(entity.getFont());
        }
        FontMetrics fm = g.getFontMetrics();
        String textValue = entity.getText();
        if (entity.getText().contains("%") && Optional.ofNullable(entity.getValue()).isPresent()) {
            textValue = String.format(entity.getText(), entity.getValue());
        }
        entity.width = fm.stringWidth(textValue);
        entity.height = fm.getHeight();
        double offsetX = 0;
        switch (entity.getTextAlign()) {
            case ALIGN_LEFT -> {
                offsetX = 0;
            }
            case ALIGN_CENTER -> {
                offsetX = (int) (-entity.width * 0.5);

            }
            case ALIGN_RIGHT -> {
                offsetX = -entity.width;
            }
            default -> {
                offsetX = 0;
                System.err.printf(">> <?> unknown textAlign %d value for %s%n", entity.getTextAlign(), entity.getName());
            }
        }
        if (entity.getShadowWidth() > 0 && Optional.ofNullable(entity.getShadowColor()).isPresent()) {
            drawShadowText(g, entity, textValue, entity.pos.x + offsetX, entity.pos.y);
        }
        if (entity.getBorderWidth() > 0 && Optional.ofNullable(entity.getBorderWidth()).isPresent()) {
            drawBorderText(g, entity, textValue, entity.pos.x + offsetX, entity.pos.y);
        }
        g.setColor(entity.getColor());
        g.drawString(textValue, (int) (entity.pos.x + offsetX), (int) entity.pos.y);
    }

    private void drawShadowText(Graphics2D g, TextObject entity, String textValue, double x, double y) {
        g.setColor(entity.getShadowColor());
        for (int i = 0; i < entity.getShadowWidth(); i++) {
            g.drawString(textValue, (int) x + i, (int) y + i);
        }
    }

    private void drawBorderText(Graphics2D g, TextObject entity, String textValue, double x, double y) {
        g.setColor(entity.getBorderColor());
        for (int i = -entity.getBorderWidth(); i < entity.getBorderWidth(); i++) {
            for (int j = -entity.getBorderWidth(); j < entity.getBorderWidth(); j++) {
                g.drawString(textValue, (int) x + i, (int) y + j);
            }
        }
    }
}
