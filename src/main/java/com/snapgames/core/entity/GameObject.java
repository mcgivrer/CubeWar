package com.snapgames.core.entity;

/**
 * The {@link GameObject} is the basic Object element to be displayed on screen.
 * It can be a POINT, a LINE,a RECTANGLE, an ELLIPSE or an IMAGE.
 */
public class GameObject extends Entity<GameObject> {

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
}
