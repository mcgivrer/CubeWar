package com.snapgames.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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


}
