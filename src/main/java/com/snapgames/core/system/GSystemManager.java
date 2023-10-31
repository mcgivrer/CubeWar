package com.snapgames.core.system;

import com.snapgames.core.scene.Scene;
import com.snapgames.core.utils.config.Configuration;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

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

    public static void reset() {
        dispose();
        systems.clear();
    }

    public static int getSystemCount() {
        return systems.size();
    }

    public static void initialize(Configuration configuration) {
        if (Optional.ofNullable(systems).isPresent()) {
            systems.values().forEach(s -> s.initialize(configuration));
        }
    }

    /**
     * Update all services with the elapsed time on the {@link Scene}, and provide some stats.
     *
     * @param scene   the Scene to be updated
     * @param elapsed the elapsed time since previous call
     * @param stats   the internal statistics to feed up.
     */
    public static void update(Scene scene, double elapsed, Map<String, Object> stats) {
        if (Optional.ofNullable(systems).isPresent()) {
            systems.values().forEach(s -> s.update(scene, elapsed, stats));
        }
    }
}
