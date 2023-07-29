package com.snapgames.core.behavior;

import com.snapgames.core.entity.Entity;
import com.snapgames.core.math.physic.CollisionEvent;

public interface CollisionResponseBehavior extends Behavior<Entity<?>> {

    void response(CollisionEvent ce);
}
