package com.snapgames.core.math.physic;

import com.snapgames.core.entity.Entity;

public class CollisionEvent {
    private final Entity<?> entity1;
    private final Entity<?> entity2;

    public CollisionEvent(Entity<?> e1, Entity<?> e2) {
        entity1 = e1;
        entity2 = e2;
    }

    public Entity<?> getEntity1() {
        return entity1;
    }

    public Entity<?> getEntity2() {
        return entity2;
    }
}
