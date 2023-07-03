package com.snapgames.core.scene;

import com.snapgames.core.Application;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The {@link SceneManager} intends to manage the scene and all its processing into the {@link com.snapgames.core.Application#loop} and game lifecycle.
 *
 * @author Frédéric Delorme
 * @version 1.0
 * @since 1.0
 */
public class SceneManager {
    private final Application application;
    private Scene current;
    private Map<String, Scene> scenes = new HashMap<>();

    public SceneManager(Application application) {
        this.application = application;
    }

    public SceneManager add(Scene s) {
        this.scenes.put(s.getName(), s);
        if (!Optional.ofNullable(this.current).isPresent()) {
            setCurrentScene(s);
        }
        return this;
    }

    public void setCurrentScene(Scene s) {
        this.current = s;
    }

    public Scene getCurrent() {
        return this.current;
    }

    public void activate(String name) {
        if (current != null) {
            this.current.dispose();
        }
        this.current = scenes.get(name);
        this.current.create(application);
    }

    public void dispose() {
        scenes.clear();
    }
}
