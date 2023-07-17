package com.snapgames.core.test;

import com.snapgames.core.Application;
import com.snapgames.core.math.physic.Vector2D;
import com.snapgames.core.math.physic.World;

import com.snapgames.core.test.scenes.AppTest;
import org.junit.jupiter.api.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApplicationConfigurationTest {
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
    public void applicationHasExitArgument() {
        application.run(new String[]{"exit=true"});
        assertTrue(application.exit, "Exit has not ben set correctly.");
    }

    @Test
    @Order(2)
    public void applicationHasConfigurationPathArgument() {
        application.run(new String[]{"exit=true", "configPath=./test-config.properties"});
        assertEquals("./test-config.properties", application.pathToConfigFile,
                "Configruation properties file path has not ben set correctly.");
    }

    @Test
    public void applicationHasDebugConfiguration() {
        application.run(new String[]{"exit=true", "configPath=./test-config.properties"});
        assertTrue(application.getConfiguration().debug, "Debug has not been activated.");
    }

    @Test
    public void applicationHasDebugLevelConfiguration() {
        application.run(new String[]{"exit=true", "configPath=./test-config.properties"});
        assertEquals(4, application.getConfiguration().debugLevel, "Debug level has not been set correctly.");
    }

    @Test
    public void applicationHasDebugFilterConfiguration() {
        application.run(new String[]{"exit=true", "configPath=./test-config.properties"});
        assertEquals("testObj1", application.getDebugFilter(),
                "Physic World configuration has not ben set correctly.");
    }

    @Test
    public void applicationHasExitConfiguration() {
        application.run(new String[]{"exit=true", "configPath=./test-config.properties"});
        assertTrue(application.exit, "Exit configuration has not ben set correctly.");
    }

    @Test
    public void applicationHasWindowConfiguration() {
        application.run(new String[]{"exit=true", "configPath=./test-config.properties"});
        assertEquals(new Dimension(800, 480), application.getConfiguration().winSize,
                "Window size configuration has not ben set correctly.");
    }

    @Test
    public void applicationHasResolutionConfiguration() {
        application.run(new String[]{"exit=true", "configPath=./test-config.properties"});
        assertEquals(new Dimension(400, 240), application.getConfiguration().bufferResolution,
                "Buffer resolution configuration has not ben set correctly.");
    }


    @Test
    public void applicationHasFpsConfiguration() {
        application.run(new String[]{"exit=true", "configPath=./test-config.properties"});
        assertEquals(60, application.getConfiguration().fps, "Frame-Per-Second has not been set correctly.");
    }

    @Test
    public void applicationHasUpsConfiguration() {
        application.run(new String[]{"exit=true", "configPath=./test-config.properties"});
        assertEquals(120, application.getConfiguration().ups, "Update-Per-Second has not been set correctly.");
    }

    @Test
    public void applicationHasMaxSpeedConfiguration() {
        application.run(new String[]{"exit=true", "configPath=./test-config.properties"});
        assertEquals(40.0, application.getConfiguration().maxEntitySpeed, 0.0,
                "Physic maximum speed configuration has not ben set correctly.");
    }

    @Test
    public void applicationHasWorldConfiguration() {
        application.run(new String[]{"exit=true", "configPath=./test-config.properties"});
        World w = new World("amazing").setGravity(new Vector2D(0, 0.10))
                .setPlayArea(new Rectangle2D.Double(0, 0, 1024, 1024));
        assertEquals(w.toString(), application.getPhysicEngine().getWorld().toString(),
                "Physic World configuration has not ben set correctly.");
    }


}
