package com.snapgames.core.entity;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import com.snapgames.core.utils.i18n.I18n;

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
    String i18nKeyCode;

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

    @Override
    public void update(double elapsed) {
        super.update(elapsed);
        if (i18nKeyCode != null) {
            this.setText(I18n.getMessage(i18nKeyCode));
        }
    }

    public Font getFont() {
        return font;
    }

    public String getText() {
        return text;
    }

    public Object getValue() {
        return value;
    }

    public int getTextAlign() {
        return textAlign;
    }

    public int getShadowWidth() {
        return shadowWidth;
    }

    public Color getShadowColor() {
        return shadowColor;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public TextObject setI18nKeyCode(String i18nKeyCode) {
        this.i18nKeyCode = i18nKeyCode;
        return this;
    }
}
