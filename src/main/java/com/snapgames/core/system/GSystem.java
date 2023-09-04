package com.snapgames.core.system;

import com.snapgames.core.Application;
import com.snapgames.core.utils.config.Configuration;

public interface GSystem {
    Class<? extends GSystem> getSystemName();

    void initialize(Configuration configuration);

    void dispose();
}
