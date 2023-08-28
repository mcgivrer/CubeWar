package com.snapgames.core.scene;

import com.snapgames.core.Application;
import com.snapgames.core.system.GSystem;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The {@link SceneManager} intends to manage the scene and all its processing
 * into the {@link com.snapgames.core.Application#loop} and game lifecycle.
 *
 * @author Frédéric Delorme
 * @version 1.0
 * @since 1.0
 */
public class SceneManager implements GSystem {
    private final Application application;
    private Scene current;
    private Map<String, Scene> scenes = new HashMap<>();

    public SceneManager(Application application) {
        this.application = application;
    }

    public SceneManager add(Scene s) {
        this.scenes.put(s.getName(), s);
        System.out.printf(">> <!> Add Scene '%s'(%s)%n", s.getName(), s.getClass().getName());
        if (!Optional.ofNullable(this.current).isPresent()) {
            setCurrentScene(s);
            System.out.printf(">> <!> Set '%s'(%s) as current one.%n", s.getName(), s.getClass().getName());
        }
        return this;
    }

    public void setCurrentScene(Scene s) {
        this.current = s;
    }

    public Scene getCurrent() {
        return this.current;
    }

    public Scene activate(String name) {
        if (current != null) {
            System.out.printf(">> <!> Disable current scene '%s'(%s) as current one.%n", current.getName(),
                current.getClass().getName());
            this.current.dispose();
        }
        this.current = scenes.get(name);
        this.current.create(application);
        // execute all SceneBehavior on the creation event.
        this.current.getBehaviors().forEach(sb -> sb.create(this.current));
        return this.current;
    }

    @Override
    public Class<? extends GSystem> getSystemName() {
        return SceneManager.class;
    }

    @Override
    public void initialize(Application app) {

    }

    @Override
    public void dispose() {
        scenes.clear();
        System.out.printf(">> <!> Scenes managed list has been cleared.%n");
    }

    public Scene getScene(String sceneName) {
        return scenes.get(sceneName);
    }
}
