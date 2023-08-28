package com.snapgames.core.test.stepdefs.entity;

import com.snapgames.core.Application;
import com.snapgames.core.entity.Entity;
import com.snapgames.core.scene.Scene;
import com.snapgames.core.scene.SceneManager;
import com.snapgames.core.system.GSystemManager;
import com.snapgames.core.test.scenes.TestScene;
import io.cucumber.java8.En;
import org.junit.jupiter.api.Assertions;

public class EntityStepdefs implements En {

    private Application appTest;

    public EntityStepdefs() {
        Given("A Test Scene {string} is created", (String sceneName) -> {
            appTest = new Application() {
                @Override
                protected void createScenes() {
                    ((SceneManager) GSystemManager.find(SceneManager.class)).add(new TestScene(sceneName));
                }
            };
            appTest.run(new String[]{"configPath=test-config.properties", "testMode=true"});
        });
        When("I add a new  Entity named {string} to this Scene {string}", (String entityName, String sceneName) -> {
            Scene scn01 = ((SceneManager) GSystemManager.find(SceneManager.class)).activate(sceneName);
            scn01.addEntity(new Entity<>(entityName));
        });
        Then("The Entity {string} appear in the Scene {string} entity list", (String entityName, String sceneName) -> {
            Scene scn01 = ((SceneManager) GSystemManager.find(SceneManager.class)).getScene(sceneName);
            Entity<?> entity = scn01.getEntity(entityName);
            Assertions.assertFalse(scn01.getEntities().isEmpty());
            Assertions.assertEquals(entityName, entity.getName());
        });

    }
}
