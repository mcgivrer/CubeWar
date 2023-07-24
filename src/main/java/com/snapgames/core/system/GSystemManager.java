package com.snapgames.core.system;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.snapgames.core.Application;
import com.snapgames.core.test.system.TestSystem;

/**
 * The {@link GSystemManager} intends to create/delete and manage the interbal
 * game systems (see {@link GSystem})
 * 
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public class GSystemManager {

    private static GSystemManager instance;
    private static Map<Class<? extends GSystem>, GSystem> systems = new ConcurrentHashMap<>();

    /**
     * Private constructor to prevent from instanciate this {@link GSystemManager}
     * externally.
     */
    private GSystemManager() {
        // protected constructor to prevent from instanciate this {@link GSystemManager}
        // externally.
    }

    /**
     * retrieve the {@link GSystemManager} instance.
     * 
     * @return
     */
    public static GSystemManager get() {
        if (instance == null) {
            instance = new GSystemManager();
        }
        return instance;
    }

    public static void add(GSystem sys) {
        systems.put(sys.getSystemName(), sys);
    }

    /**
     * Retrieve a GSYstem from the manager on its own class name.
     * 
     * @param <T>         the Type of the required system
     * @param systemClass
     * @return
     */
    public static <T extends GSystem> T find(Class<? extends GSystem> systemClass) {
        return (T) systems.get(systemClass);
    }

    public static void dispose() {
        if (Optional.ofNullable(systems).isPresent()) {
            systems.values().forEach(GSystem::dispose);
        }
    }

    public static int getSystemCount() {
        return systems.size();
    }

    public static void initialize(Application application) {
        if (Optional.ofNullable(systems).isPresent()) {
            systems.values().forEach(s -> s.initialize(application));
        }
    }

}
