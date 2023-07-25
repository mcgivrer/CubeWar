package com.snapgames.core.system;

import com.snapgames.core.Application;

public interface GSystem {
    Class<? extends GSystem> getSystemName();

    void initialize(Application app);

    void dispose();
}
