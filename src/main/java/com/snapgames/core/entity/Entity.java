package com.snapgames.core.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import com.snapgames.core.behavior.Behavior;
import com.snapgames.core.math.physic.Material;
import com.snapgames.core.math.physic.PhysicType;
import com.snapgames.core.math.physic.Vector2D;

/**
 * {@link Entity} class representing an entity in the game.
 *
 * <p>
 * Each entity has a name, a position, a size, a velocity, a lifespan, attributes, and graphical properties.
 * It is defined and declared into a {@link com.snapgames.core.scene.Scene}, and will be managed by the
 * {@link com.snapgames.core.math.physic.PhysicEngine} and the {@link com.snapgames.core.graphics.Renderer} as a node.
 * </p>
 */
public class Entity<T extends Entity<?>> extends Rectangle2D.Double {


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
    int lifespan;

    Color color = Color.WHITE;
    Color fillColor = Color.RED;
    int priority = 1;

    Map<String, Object> attributes = new HashMap<>();
    public List<Behavior<T>> behaviors = new ArrayList<>();
    public PhysicType physicType = PhysicType.DYNAMIC;
    public Material material;
    public int layer;
    public boolean constrainedToPlayArea;
    public boolean stickToCamera;
    public int contact;
    public int debug = 5;
    public Entity<?> parent;
    public List<Entity<?>> child = new CopyOnWriteArrayList<>();

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
        setName(n);
        setPosition(x, y);
        setSize((int) w, (int) h);
        setActive(true);
        setStickToCameraView(false);
        setConstrainedToPlayArea(true);
        setMaterial(Material.DEFAULT);
        setMass(1.0);
    }

    /**
     * Entity's constructor with a name, a position (x,y) and a size (w,h).
     *
     * @param n Name for this new {@link Entity}
     */
    public Entity(String n) {
        this(n, 0, 0, 0, 0);
    }

    public T setName(String name) {
        this.name = name;
        return (T) this;
    }

    /**
     * Update the internal Entity's attribute for time management.
     *
     * @param elapsed Elapsed time since previous call.
     */
    public void update(double elapsed) {

        lifespan += elapsed;
        if (lifespan > duration && duration != -1) {
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
            lifespan = duration;
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
     * Draw everything about your {@link Entity} implementation.
     *
     * @param g the Graphics API instance to be used.
     */
    public void draw(Graphics2D g) {
        // nothing to do for ths entity.
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
        return (T) setSpeed(new Vector2D(dx, dy));
    }

    public T setSpeed(Vector2D d) {
        this.vel = d;
        return (T) this;
    }

    public T setPosition(double x, double y) {
        return (T) setPosition(new Vector2D(x, y));
    }

    public T setPosition(Vector2D p) {
        this.oldPos = pos;
        this.pos = p;
        this.x = pos.x;
        this.y = pos.y;
        return (T) this;
    }

    public T setAcceleration(double ax, double ay) {
        return (T) setAcceleration(new Vector2D(ax, ay));
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
        return new Rectangle2D.Double(x, y, width, height);
    }

    public String toString() {
        return String.format("%s:pos[%.02f,%.02f]", name, x, y);
    }

    public T setStickToCameraView(boolean b) {
        this.stickToCamera = b;
        return (T) this;
    }

    public T setPhysicType(PhysicType t) {
        this.physicType = t;
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
        this.oldPos = new Vector2D(x, y);
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

    public String getName() {
        return name;
    }

    public Vector2D getPosition() {
        return pos;
    }

    public Vector2D getVelocity() {
        return vel;
    }

    public Vector2D getAcceleration() {
        return acceleration;
    }
}
