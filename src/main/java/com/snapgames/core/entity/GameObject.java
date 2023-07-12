package com.snapgames.core.entity;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Optional;

/**
 * The {@link GameObject} is the basic Object element to be displayed on screen.
 * It can be a POINT, a LINE,a RECTANGLE, an ELLIPSE or an IMAGE.
 */
public class GameObject extends Entity<GameObject> {

    public final static int TYPE_POINT = 1;
    public final static int TYPE_LINE = 2;
    public final static int TYPE_RECTANGLE = 3;
    public final static int TYPE_ELLIPSE = 4;
    public final static int TYPE_IMAGE = 5;

    public int type = TYPE_RECTANGLE;
    private BufferedImage image;

    /**
     * Create a new GameObject.
     *
     * @param n le nom de l'entité
     * @param x la position en x de l'entité
     * @param y la position en y de l'entité
     * @param w la largeur de l'entité
     * @param h la hauteur de l'entité
     */
    public GameObject(String n, double x, double y, int w, int h) {
        super(n, x, y, w, h);
    }

    /**
     * Create a new GameObject.
     *
     * @param n le nom de l'entité
     */
    public GameObject(String n) {
        super(n);
    }


    @Override
    public void draw(Graphics2D g) {
        switch (type) {
            case TYPE_POINT -> {
                if (color != null) {
                    g.setColor(color);
                    g.drawRect((int) pos.x, (int) pos.y, 1, 1);
                }
            }
            case TYPE_LINE -> {
                if (color != null) {
                    g.setColor(color);
                    g.drawLine((int) pos.x, (int) pos.y, (int) oldPos.x, (int) oldPos.y);
                }
            }
            case TYPE_RECTANGLE -> {
                if (fillColor != null) {
                    g.setColor(fillColor);
                    g.fill(this);
                }
                if (color != null) {
                    g.setColor(color);
                    g.draw(this);
                }
            }
            case TYPE_ELLIPSE -> {
                if (fillColor != null) {
                    g.setColor(fillColor);
                    g.fillOval((int) pos.x, (int) pos.y, (int) width, (int) height);
                }
                if (color != null) {
                    g.setColor(color);
                    g.drawOval((int) pos.x, (int) pos.y, (int) width, (int) height);
                }
            }
            case TYPE_IMAGE -> {
                if (Optional.ofNullable(image).isPresent()) {
                    if (vel.x > 0) {
                        g.drawImage(image, (int) pos.x, (int) pos.y, null);
                    } else {
                        g.drawImage(image, (int) (pos.x - width), (int) pos.y, (int) -width, (int) height, null);
                    }
                }
            }
            default -> System.err.printf("Unknown Entity type %d%n", type);
        }
    }

    public GameObject setType(int t) {
        this.type = t;
        return this;
    }

    public GameObject setImage(BufferedImage img) {
        this.image = img;
        return this;
    }
}
