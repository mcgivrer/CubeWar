package com.snapgames.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Optional;

import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApplicationConfigurationTest {
    private Application application;

    @BeforeEach
    public void setup() {
        application = new Application();
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
        assertEquals("./test-config.properties", application.pathToConfigFile, "Configruation properties file path has not ben set correctly.");
    }

    @Test
    public void applicationHasDebugConfiguration() {
        application.run(new String[]{"exit=true", "configPath=./test-config.properties"});
        assertEquals(0, application.debug, "Debug level has not ben set correctly.");
    }

    @Test
    public void applicationHasExitConfiguration() {
        application.run(new String[]{"exit=true", "configPath=./test-config.properties"});
        assertTrue(application.exit, "Exit configuration has not ben set correctly.");
    }

    @Test
    public void applicationHasWindowConfiguration() {
        application.run(new String[]{"exit=true", "configPath=./test-config.properties"});
        assertEquals(new Dimension(800, 480), application.winSize, "Window size configuration has not ben set correctly.");
    }

    @Test
    public void applicationHasResolutionConfiguration() {
        application.run(new String[]{"exit=true", "configPath=./test-config.properties"});
        assertEquals(new Dimension(400, 240), application.bufferResolution, "Buffer resolution configuration has not ben set correctly.");
    }

    @Test
    public void applicationHasPlayAreaConfiguration() {
        application.run(new String[]{"exit=true", "configPath=./test-config.properties"});
        assertEquals(new Rectangle2D.Double(0, 0, 1024, 1024), application.playArea, "Play area size configuration has not ben set correctly.");
    }

    @Test
    public void applicationHasMaxSpeedConfiguration() {
        application.run(new String[]{"exit=true", "configPath=./test-config.properties"});
        assertEquals(64.0, application.maxEntitySpeed, 0.0, "Physic maximum speed configuration has not ben set correctly.");
    }

    @Test
    public void applicationHasWorldConfiguration() {
        application.run(new String[]{"exit=true", "configPath=./test-config.properties"});
        Application.World w = new Application.World("amazing").setGravity(0.981).setPlayArea(new Rectangle2D.Double(0, 0, 1024, 1024));
        assertEquals(w.toString(), application.world.toString(), "Physic World configuration has not ben set correctly.");
    }

}
