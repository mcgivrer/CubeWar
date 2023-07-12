package com.snapgames.core.entity;

import java.awt.*;
import java.util.List;
import java.util.Optional;

/**
 * The {@link TextObject} is an extended {@link Entity} to support Text drawing.
 * <p>
 * the <code>text</code> can be a simple string, or a formatted String with the
 * provided <code>value</code>.
 * The text will be then compatible with the
 * {@link String#format(String, Object...)} formatting rules.
 * <p>
 * It also supports
 * <ul>
 * <li>a graphical <code>textAlign</code> attribute than can be one of the
 * {@link TextObject#ALIGN_LEFT},
 * {@link TextObject#ALIGN_CENTER} or {@link TextObject#ALIGN_RIGHT}
 * values,</li>
 * <li>a <code>font</code> to define font family and size,</li>
 * <li>a <code>shadowColor</code> and <code>shadowWidth</code> to define a text
 * shadow,</li>
 * <li>a <code>borderColor</code> and a <code>borderWidth</code> to define a
 * outlined border on the text.</li>
 * </ul>
 */
public class TextObject extends Entity<TextObject> {
    public static final int ALIGN_LEFT = 1;
    public static final int ALIGN_CENTER = 2;
    public static final int ALIGN_RIGHT = 4;
    String text;
    Font font;
    Object value;
    int shadowWidth;
    Color shadowColor;
    int borderWidth;
    Color borderColor;
    int textAlign = ALIGN_LEFT;

    /**
     * Create a new {@link TextObject} at (x,y) with name n.
     * 
     * @param n name of the new {@link TextObject}
     * @param x horizontal position of the new {@link TextObject}
     * @param y vertical position of the new {@link TextObject}
     */
    public TextObject(String n, double x, double y) {
        super(n, x, y, 0, 0);
    }

    /**
     * Create a new {@link TextObject} at (x,y) with name n.
     * 
     * @param n name of the new {@link TextObject}
     */
    public TextObject(String n) {
        super(n);
    }

    @Override
    public void draw(Graphics2D g) {
        if (Optional.ofNullable(font).isPresent()) {
            g.setFont(font);
        }
        FontMetrics fm = g.getFontMetrics();
        String textValue = text;
        if (text.contains("%") && Optional.ofNullable(value).isPresent()) {
            textValue = String.format(text, value);
        }
        this.width = fm.stringWidth(textValue);
        this.height = fm.getHeight();
        double offsetX = 0;
        switch (textAlign) {
            case ALIGN_LEFT -> {
                offsetX = 0;
            }
            case ALIGN_CENTER -> {
                offsetX = (int) (-this.width * 0.5);

            }
            case ALIGN_RIGHT -> {
                offsetX = -this.width;
            }
            default -> {
                offsetX = 0;
                System.err.printf(">> <?> unknown textAlign %d value for %s%n", textAlign, name);
            }
        }
        if (shadowWidth > 0 && Optional.ofNullable(shadowColor).isPresent()) {
            drawShadowText(g, textValue, pos.x + offsetX, pos.y);
        }
        if (borderWidth > 0 && Optional.ofNullable(borderColor).isPresent()) {
            drawBorderText(g, textValue, pos.x + offsetX, pos.y);
        }
        g.setColor(color);
        g.drawString(textValue, (int) (pos.x + offsetX), (int) pos.y);
    }

    private void drawShadowText(Graphics2D g, String textValue, double x, double y) {
        g.setColor(shadowColor);
        for (int i = 0; i < shadowWidth; i++) {
            g.drawString(textValue, (int) x + i, (int) y + i);
        }
    }

    private void drawBorderText(Graphics2D g, String textValue, double x, double y) {
        g.setColor(borderColor);
        for (int i = -borderWidth; i < borderWidth; i++) {
            for (int j = -borderWidth; j < borderWidth; j++) {
                g.drawString(textValue, (int) x + i, (int) y + j);
            }
        }
    }

    public TextObject setShadowColor(Color sc) {
        this.shadowColor = sc;
        return this;
    }

    public TextObject setShadowWidth(int sw) {
        this.shadowWidth = sw;
        return this;
    }

    public TextObject setBorderColor(Color bc) {
        this.borderColor = bc;
        return this;
    }

    public TextObject setBorderWidth(int bw) {
        this.borderWidth = bw;
        return this;
    }

    public TextObject setFont(Font f) {
        this.font = f;
        return this;
    }

    public TextObject setText(String t) {
        this.text = t;
        return this;
    }

    public TextObject setValue(Object v) {
        this.value = v;
        return this;
    }

    public TextObject setTextAlign(int ta) {
        assert (ta == ALIGN_LEFT || ta == ALIGN_CENTER || ta == ALIGN_RIGHT);
        this.textAlign = ta;
        return this;
    }

    @Override
    public List<String> getDebugInfo() {
        List<String> infos = super.getDebugInfo();
        infos.add(String.format("3_text:%s", text));
        infos.add(String.format("3_val:%s", value != null ? value.toString() : "null"));
        return infos;
    }
}
