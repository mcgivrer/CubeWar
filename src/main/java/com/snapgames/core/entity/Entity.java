package com.snapgames.core.entity;

import com.snapgames.core.behavior.Behavior;
import com.snapgames.core.math.physic.Material;
import com.snapgames.core.math.physic.Vector2D;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Classe interne représentant une entité dans le jeu.
 *
 * <p>
 * Chaque entité possède un nom, une position, une taille, une vitesse, une
 * durée de vie, des attributs et des propriétés graphiques.
 * </p>
 */
public class Entity<T extends Entity<?>> extends Rectangle2D.Double {

    public final static int NONE = 0;
    public final static int STATIC = 1;
    public final static int DYNAMIC = 2;

    public final static int TYPE_POINT = 1;
    public final static int TYPE_LINE = 2;
    public final static int TYPE_RECTANGLE = 3;
    public final static int TYPE_ELLIPSE = 4;
    public final static int TYPE_IMAGE = 5;

    public static int index = 0;
    protected int id = ++index;
    public String name;
    public double rotation;

    public Vector2D oldPos = Vector2D.ZERO();
    public Vector2D pos = Vector2D.ZERO();
    public Vector2D vel = Vector2D.ZERO();
    public Vector2D acceleration = Vector2D.ZERO();

    public List<Vector2D> forces = new ArrayList<>();
    public double dRotation;
    public double mass;
    public boolean active;

    int duration = -1;
    int life;

    Color color = Color.WHITE;
    Color fillColor = Color.RED;
    int priority = 1;

    Map<String, Object> attributes = new HashMap<>();
    public List<Behavior<T>> behaviors = new ArrayList<>();
    public int physicType;
    public Material material;
    public int type;
    public int layer;
    public boolean constrainedToPlayArea;
    public boolean stickToCamera;
    public int contact;
    public int debug = 5;
    public Entity<?> parent;
    public List<Entity<?>> child = new CopyOnWriteArrayList<>();
    private BufferedImage image;

    /**
     * Entity's constructor with a name, a position (x,y) and a size (w,h).
     *
     * @param n Name for this new {@link Entity}
     * @param x X axis position
     * @param y Y axis position
     * @param w Entity's width
     * @param h Entity's height.
     */
    public Entity(String n, double x, double y, double w, double h) {
        this.name = n;
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.active = true;
        this.type = TYPE_RECTANGLE;
        this.stickToCamera = false;
        this.constrainedToPlayArea = true;
        this.material = Material.DEFAULT;
        this.mass = 1.0;
    }

    /**
     * Update the internal Entity's attribute for time management.
     *
     * @param elapsed Elapsed time since previous call.
     */
    public void update(double elapsed) {

        life += elapsed;
        if (life > duration && duration != -1) {
            active = false;
        }

    }

    /**
     * Define the {@link Entity} activity state.
     *
     * @param active true to activate this {@link Entity}.
     */
    public T setActive(boolean active) {
        this.active = active;
        if (duration != -1) {
            life = duration;
        }
        child.forEach(c -> c.setActive(active));
        return (T) this;
    }

    /**
     * Check activity status of theis {@link Entity}.
     *
     * @return true if {@link Entity} is active, elsewhere false.
     */
    public boolean isActive() {
        return this.active;
    }

    public T setDebug(int d) {
        this.debug = d;
        return (T) this;
    }

    /**
     * Dessine l'entité.
     *
     * @param g le contexte graphique sur lequel dessiner
     */
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
            default -> {
                System.err.printf("Unknown Entity type %d%n", type);
            }
        }
    }

    public T addBehavior(Behavior<T> b) {
        behaviors.add(b);
        return (T) this;
    }

    /**
     * Add an attribute to this Entity.
     *
     * @param attrName  Attribute's name
     * @param attrValue Attribute's value
     */
    public T setAttribute(String attrName, Object attrValue) {
        attributes.put(attrName, attrValue);
        return (T) this;
    }

    /**
     * Retrieve the specified attribute value.
     *
     * @param attrName     Attribute's name
     * @param defaultValue the default value if it does not exist.
     * @return Attribute's value or the default value if it was not existing.
     */
    public <Y> Y getAttribute(String attrName, Y defaultValue) {
        return (Y) attributes.getOrDefault(attrName, defaultValue);
    }

    public T setSpeed(double dx, double dy) {
        this.vel = new Vector2D(dx, dy);
        return (T) this;
    }

    public T setSpeed(Vector2D d) {
        this.vel = d;
        return (T) this;
    }

    public T setPosition(double x, double y) {
        this.oldPos = pos;
        this.pos = new Vector2D(x, y);
        return (T) this;
    }

    public T setPosition(Vector2D p) {
        this.oldPos = pos;
        this.pos = p;
        return (T) this;
    }

    public T setAcceleration(double ax, double ay) {
        this.acceleration = new Vector2D(ax, ay);
        return (T) this;
    }

    public T setAcceleration(Vector2D acc) {
        this.acceleration = acc;
        return (T) this;
    }


    public T setSize(int w, int h) {
        this.width = w;
        this.height = h;
        return (T) this;
    }

    public T setRotation(double r) {
        this.rotation = r;
        return (T) this;
    }

    public T setRotationSpeed(double dr) {
        this.dRotation = dr;
        return (T) this;
    }

    public T setMass(double m) {
        this.mass = m;
        return (T) this;
    }

    public T setMaterial(Material mat) {
        this.material = mat;
        return (T) this;
    }

    public Rectangle2D getBounds2D() {
        return new Double(x, y, width, height);
    }

    public String toString() {
        return String.format("%s:pos[%.02f,%.02f]", name, x, y);
    }

    public T setStickToCameraView(boolean b) {
        this.stickToCamera = b;
        return (T) this;
    }

    public T setPhysicType(int t) {
        assert (t == NONE || t == STATIC || t == DYNAMIC);
        this.physicType = t;
        return (T) this;
    }

    public T setType(int t) {
        this.type = t;
        return (T) this;
    }

    public T setColor(Color c) {
        this.color = c;
        return (T) this;
    }

    public T setFillColor(Color c) {
        this.fillColor = c;
        return (T) this;
    }

    public T setPriority(int t) {
        this.priority = t;
        return (T) this;
    }

    public T setContact(int c) {
        this.contact = c;
        return (T) this;
    }

    public T setConstrainedToPlayArea(boolean ctpa) {
        this.constrainedToPlayArea = ctpa;
        return (T) this;
    }

    public T setLayer(int l) {
        this.layer = l;
        return (T) this;
    }

    public List<String> getDebugInfo() {
        List<String> infos = new ArrayList<>();
        infos.add(String.format("1_#%d", id));
        infos.add(String.format("1_name:%s", name));
        infos.add(String.format("2_pos:(%.02f,%.02f)", pos.x, pos.y));
        infos.add(String.format("2_size:(%.02f,%.02f)", width, height));
        infos.add(String.format("3_vel:(%.02f,%.02f)", vel.x, vel.y));
        infos.add(String.format("4_acc:(%.02f,%.02f)", acceleration.x, acceleration.y));
        if (mass != 0.0)
            infos.add(String.format("4_mass:%.02f kg", mass));
        if (material != null)
            infos.add(String.format("4_mat:%s", material));
        return infos;
    }

    public T setParent(Entity<?> e) {
        this.parent = e;
        return (T) this;
    }

    public T addChild(Entity<?> c) {
        this.child.add(c);
        return (T) this;
    }

    public T setDuration(int d) {
        this.duration = d;
        return (T) this;
    }

    public T setOldPosition(double x, double y) {
        this.oldPos = pos;
        return (T) this;
    }

    public T setOldPosition(Vector2D p) {
        this.oldPos = p;
        return (T) this;
    }

    public List<Entity<?>> getChild() {
        return child;
    }

    public T addForce(Vector2D f) {
        this.forces.add(f);
        return (T) this;
    }

    public Material getMaterial() {
        return this.material;
    }

    public List<Vector2D> getForces() {
        return forces;
    }

    public int getPriority() {
        return this.priority;
    }
}
