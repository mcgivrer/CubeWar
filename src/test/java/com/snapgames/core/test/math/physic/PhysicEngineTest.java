package com.snapgames.core.test.math.physic;

import com.snapgames.core.entity.GameObject;
import com.snapgames.core.entity.GameObjectType;
import com.snapgames.core.math.Vector2D;
import com.snapgames.core.math.physic.PhysicEngine;
import com.snapgames.core.math.physic.PhysicType;
import com.snapgames.core.math.physic.entity.Perturbation;
import com.snapgames.core.scene.Scene;
import com.snapgames.core.test.AppTest;
import com.snapgames.core.test.scenes.TestScene;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PhysicEngineTest {
    private AppTest application;

    @BeforeEach
    public void setup() {
        application = new AppTest();
    }

    @AfterEach
    public void tearDown() {
        if (Optional.ofNullable(application).isPresent()) {
            application.dispose();
        }
        application = null;
    }

    @Test
    @Order(1)
    public void testStaticEntity() {
        application.run(new String[]{"exit=true", "config=./test-pe.properties"});
        PhysicEngine pe = new PhysicEngine(application);

        Scene testScene = new TestScene();
        GameObject obj1 =
                new GameObject("obj1")
                        .setPosition(100, 100)
                        .setSize(10, 10)
                        .setType(GameObjectType.TYPE_RECTANGLE)
                        .setPhysicType(PhysicType.STATIC);
        testScene.addEntity(obj1);

        Map<String, Object> stats = new HashMap<>();
        for (int i = 0; i < 200; i++) {
            pe.update(testScene, 16, stats);
        }
        Assertions.assertEquals(new Vector2D(100, 100).toString(),
                testScene.getEntity("obj1").getPosition().toString());

    }


    @Test
    @Order(2)
    public void testDynamicEntity() {
        // Just initialize an application with a specific configuration, but no looping (exit=true)
        application.run(new String[]{"exit=true", "configPath=./test-pe.properties"});
        // create a specific instance of thePhysicEngine for test purpose (isolated)
        PhysicEngine pe = new PhysicEngine(application);
        // add a TestScene
        Scene testScene = new TestScene();
        testScene.create(application);
        // gather the already existing player object from test scene
        GameObject player = (GameObject) testScene.getEntity("player");
        player.addForce(new Vector2D(0.0, 0.1));

        Map<String, Object> stats = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            pe.update(testScene, 0.1, stats);
        }
        Assertions.assertEquals(new Vector2D(100.0, 100.49748331212493),
                player.getPosition());

    }


    @Test
    @Order(3)
    public void testDynamicEntityWithPerturbation() {
        // Just initialize an application with a specific configuration, but no looping (exit=true)
        application.run(new String[]{"exit=true", "configPath=./test-pe.properties"});
        // create a specific instance of thePhysicEngine for test purpose (isolated)
        PhysicEngine pe = new PhysicEngine(application);
        // add a TestScene
        Scene testScene = new TestScene();
        testScene.create(application);
        // gather the already existing player object from test scene
        GameObject player = (GameObject) testScene.getEntity("player");
        player.addForce(new Vector2D(0.0, 0.1));

        Perturbation pert01 = new Perturbation("pert01",
                0,0,
                pe.world.getPlayArea().getWidth(),
                pe.world.getPlayArea().getHeight())
                .setForce(new Vector2D(0.10,0.0));
        pe.world.add(pert01);

        Map<String, Object> stats = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            pe.update(testScene, 0.1, stats);
        }
        Assertions.assertEquals(new Vector2D(125.16436206296397, 100.49748331212493),
                player.getPosition());

    }
}
