package com.snapgames.core.math.physic;

/**
 * Define the possible type of Physic nodes in the {@link PhysicEngine}.
 */
public enum PhysicType {
    /**
     * The node typed NONE will not be processed by the {@link PhysicEngine}.
     */
    NONE,
    /**
     * STATIC node will be processed without applying World constrains (gravity, Material, etc..).
     */
    STATIC,
    /**
     * Any DYNAMIC node will be fully processed by the {@link PhysicEngine}.
     */
    DYNAMIC;
}
