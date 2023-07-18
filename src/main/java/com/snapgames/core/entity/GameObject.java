package com.snapgames.core.entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Optional;

/**
 * The {@link GameObject} is the basic Object element to be displayed on screen.
 * It can be a POINT, a LINE,a RECTANGLE, an ELLIPSE or an IMAGE.
 * * <p>
 * * And this {@link com.snapgames.core.entity.GameObject} can be :
 * * <ul>
 * * <li><code>{@link GameObjectType#TYPE_POINT}</code> to be drawn as a simple 2D
 * * point,</li>
 * * <li><code>{@link GameObjectType#TYPE_LINE}</code> to be drawn as a 2D line from (x,y)
 * * to (width,height),</li>
 * * <li><code>{@link GameObjectType#TYPE_RECTANGLE}</code> to be drawn as a 2D rectangle
 * * at (x,y) of size (width,height),</li>
 * * <li><code>{@link GameObjectType#TYPE_ELLIPSE}</code> to be drawn as a 2D ellipse at
 * * (x,y) with (r1=width and r2=height).</li>
 * * </ul>
 */
public class GameObject extends Entity<GameObject> {


    public GameObjectType type = GameObjectType.TYPE_RECTANGLE;
    private BufferedImage image;

    /**
     * Create a new GameObject.
     *
     * @param n name of the {@link Entity}
     * @param x horizontal position of the {@link Entity}
     * @param y vertical position of the {@link Entity}
     * @param w width of the {@link Entity}
     * @param h height of the {@link Entity}
     */
    public GameObject(String n, double x, double y, int w, int h) {
        super(n, x, y, w, h);
    }

    /**
     * Create a new GameObject.
     *
     * @param n name of the {@link Entity}
     */
    public GameObject(String n) {
        super(n);
    }


    @Override
    public void draw(Graphics2D g) {

    }

    public GameObject setType(GameObjectType t) {
        this.type = t;
        return this;
    }

    public GameObject setImage(BufferedImage img) {
        this.image = img;
        return this;
    }

    public BufferedImage getImage() {
        return image;
    }
}
