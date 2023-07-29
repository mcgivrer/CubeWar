package com.snapgames.demo;

import com.snapgames.core.Application;
import com.snapgames.core.input.InputHandler;
import com.snapgames.core.scene.SceneManager;
import com.snapgames.core.system.GSystemManager;
import com.snapgames.demo.input.GameKeyInput;
import com.snapgames.demo.scenes.DemoScene;
import com.snapgames.demo.scenes.TitleScene;

import java.util.Optional;

/**
 * This {@link CubeWar} demonstration app highlight the way to implement your
 * own {@link Application}.
 *
 * @author Frédéric Delorme
 * @since 1.0.0
 */
public class CubeWar extends Application {
    @Override
    protected void createScenes() {
        InputHandler ih = ((InputHandler) GSystemManager.find(InputHandler.class));
        ih.add(new GameKeyInput());
        ((SceneManager) GSystemManager.find(SceneManager.class))
            .add(new TitleScene())
            .add(new DemoScene());
        // if a defaultScene argument is set, switch to the required scene (mainly used for debug purpose.
        if (Optional.ofNullable(ih.getApplication().getConfiguration().defaultScene).isPresent()) {
            ((SceneManager) GSystemManager.find(SceneManager.class))
                .activate(ih.getApplication().getConfiguration().defaultScene);
        }
    }

    /**
     * The entrypoint for our Application to be initialized and executed.
     *
     * @param argc the list of arguments from the Java command line.
     */
    public static void main(String[] argc) {
        CubeWar app = new CubeWar();
        app.run(argc);
    }
}
