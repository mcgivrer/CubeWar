package com.snapgames.core.test.stepdefs.config;

import com.snapgames.core.math.Vector2D;
import com.snapgames.core.math.physic.World;
import com.snapgames.core.utils.config.Configuration;
import io.cucumber.java8.En;
import org.junit.jupiter.api.Assertions;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class ConfigurationStepdefs implements En {
    Configuration configuration;
    String configFilePath;

    public ConfigurationStepdefs() {
        Given("A configuration file named {string}", (String configFilePath) -> {
            this.configFilePath = configFilePath;
        });
        When("I create an instance of Configuration", () -> {
            configuration = new Configuration(configFilePath, new ArrayList<>());
        });
        Then("I get the Configuration instance configured with file values.", () -> {
            Assertions.assertEquals("AppTest", configuration.name);

            Assertions.assertTrue(configuration.debug);
            Assertions.assertEquals(4, configuration.debugLevel);
            Assertions.assertEquals("testObj1", configuration.debugFilter);

            Assertions.assertTrue(configuration.testMode);
            Assertions.assertTrue(configuration.requestExit);

            Assertions.assertEquals(new Dimension(800, 480), configuration.winSize);
            Assertions.assertEquals(new Dimension(400, 240), configuration.bufferResolution);

            Assertions.assertEquals(60, configuration.fps);
            Assertions.assertTrue(configuration.physicConstrained);
            Assertions.assertEquals(120, configuration.ups);

            Assertions.assertEquals(40.0, configuration.maxEntitySpeed);
            Assertions.assertEquals(4.0, configuration.maxEntityAcc);
            Assertions.assertEquals(new World("amazing")
                .setGravity(new Vector2D(0.0, 0.10))
                .setPlayArea(new Rectangle2D.Double(0, 0, 1024, 1024)).toString(), configuration.world.toString());


        });
    }
}
