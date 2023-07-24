package com.snapgames.core.test.system;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.snapgames.core.system.GSystem;
import com.snapgames.core.system.GSystemManager;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GSystemTest {
    GSystemManager gsm;

    @BeforeEach
    public void setup() {
        gsm = GSystemManager.get();
    }

    @AfterEach
    public void tearDown() {

        GSystemManager.dispose();
        gsm = null;
    }

    @Test
    @Order(1)
    public void applicationHasASystemManager() {

        Assertions.assertNotNull(gsm);
    }

    @Test
    @Order(2)
    public void aTestSystemCanBeAddedToTheGSManager() {
        GSystem sys = new TestSystem();
        GSystemManager.add(sys);
        Assertions.assertEquals(1, GSystemManager.getSystemCount());
    }

    @Test
    @Order(3)
    public void aTestSystemCanBeFindFromTheGSManager() {
        GSystem sys = new TestSystem();
        GSystemManager.add(sys);
        TestSystem ts = GSystemManager.find(TestSystem.class);
        Assertions.assertEquals(1234, ts.testMethod1());
    }

    @Test
    @Order(4)
    public void aGSystemCanBeDisposed() {
        GSystem sys = new TestSystem();
        TestSystem tsys = (TestSystem) sys;
        tsys.setValue(223);
        sys.dispose();
        Assertions.assertEquals(0, tsys.getValue());
    }

    @Test
    @Order(5)
    public void allGSystemCanBeDisposed() {
        TestSystem tsys = new TestSystem();
        GSystemManager.add(tsys);
        tsys.setValue(223);
        GSystemManager.dispose();
        Assertions.assertEquals(0, tsys.getValue());
    }

}