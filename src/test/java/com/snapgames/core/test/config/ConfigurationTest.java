package com.snapgames.core.test.config;

import com.snapgames.core.math.Vector2D;
import com.snapgames.core.math.physic.PhysicEngine;
import com.snapgames.core.math.physic.World;
import com.snapgames.core.system.GSystemManager;
import com.snapgames.core.test.AppTest;
import com.snapgames.core.utils.config.Configuration;
import org.junit.jupiter.api.*;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ConfigurationTest {

    Configuration cfg;

    @BeforeEach
    public void setup() {
        // Just initialize a configuration, but no looping (testMode=true)
        cfg = new Configuration(null, "./test-config.properties", Arrays.asList("testMode=true"));
    }

    @AfterEach
    public void tearDown() {
        cfg = null;
    }

    @Test
    @Order(1)
    public void applicationHasExitArgument() {
        assertTrue(cfg.testMode, "testMode has not been set correctly.");
    }

    @Test
    @Order(2)
    public void applicationHasConfigurationPathArgument() {

        assertEquals("./test-config.properties", cfg.pathToConfigFile, "Configruation properties file path has not ben set correctly.");
    }

    @Test
    public void applicationHasDebugConfiguration() {

        assertTrue(cfg.debug, "Debug has not been activated.");
    }

    @Test
    public void applicationHasDebugLevelConfiguration() {

        assertEquals(4, cfg.debugLevel, "Debug level has not been set correctly.");
    }

    @Test
    public void applicationHasDebugFilterConfiguration() {

        assertEquals("testObj1", cfg.debugFilter, "Physic World configuration has not ben set correctly.");
    }

    @Test
    public void applicationHasExitConfiguration() {
        assertTrue(cfg.requestExit, "Exit configuration has not ben set correctly.");
    }

    @Test
    public void applicationHasWindowConfiguration() {

        assertEquals(new Dimension(800, 480), cfg.winSize, "Window size configuration has not ben set correctly.");
    }

    @Test
    public void applicationHasResolutionConfiguration() {

        assertEquals(new Dimension(400, 240), cfg.bufferResolution, "Buffer resolution configuration has not ben set correctly.");
    }

    @Test
    public void applicationHasFpsConfiguration() {

        assertEquals(60, cfg.fps, "Frame-Per-Second has not been set correctly.");
    }

    @Test
    public void applicationHasUpsConfiguration() {

        assertEquals(120, cfg.ups, "Update-Per-Second has not been set correctly.");
    }

    @Test
    public void applicationHasMaxSpeedConfiguration() {

        assertEquals(40.0, cfg.maxEntitySpeed, 0.0, "Physic maximum speed configuration has not ben set correctly.");
    }

    @Test
    public void applicationHasWorldConfiguration() {
        World w = new World("amazing").setGravity(new Vector2D(0, 0.10)).setPlayArea(new Rectangle2D.Double(0, 0, 1024, 1024));
        assertEquals(w.toString(), cfg.world.toString(), "Physic World configuration has not ben set correctly.");
    }

}
