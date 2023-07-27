package com.snapgames.core.test.system;

import com.snapgames.core.Application;
import com.snapgames.core.system.GSystem;

public class TestSystem implements GSystem {

    private int value;

    @Override
    public Class<? extends GSystem> getSystemName() {
        return TestSystem.class;
    }

    @Override
    public void initialize(Application app) {
        System.out.println("TestSystem initialized");
    }

    @Override
    public void dispose() {
        this.value = 0;
        System.out.println("TestSystem disposed");
    }

    public int testMethod1() {
        return 1234;
    }

    public void setValue(int i) {
        this.value = i;
    }

    public int getValue() {
        return value;
    }
}
