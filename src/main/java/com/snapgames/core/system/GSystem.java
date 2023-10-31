package com.snapgames.core.system;

import com.snapgames.core.Application;
import com.snapgames.core.scene.Scene;
import com.snapgames.core.utils.config.Configuration;

import java.util.Map;

public interface GSystem {
    Class<? extends GSystem> getSystemName();

    void initialize(Configuration configuration);

    default void update(Scene scene, double elapsed, Map<String, Object> stats) {
        // nothing specific to do here.
    }

    void dispose();
}
