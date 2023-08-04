package com.snapgames.core.utils;

import java.awt.Color;

/**
 * Utilities for Color management.
 *
 * @author Frédéric Delorme
 * @since 1.0.4
 */
public class Colors {

    private Colors() {
        // just to prevent instantiation.
    }

    /**
     * Generate a random {@link Color}.
     *
     * @return a new {@link Color}
     */
    public static Color random() {
        return new Color((float) Math.random(), (float) Math.random(), (float) Math.random(), 1.0f);
    }

    public static Color random(Color ref, int randomFactor) {
        return new Color(
            Math.max(0, Math.min(255, ref.getRed() - (int) ((randomFactor * 0.5) + (randomFactor * Math.random())))),
            Math.max(0, Math.min(255, ref.getGreen() - (int) ((randomFactor * 0.5) + (randomFactor * Math.random())))),
            Math.max(0, Math.min(255, ref.getBlue() - (int) ((randomFactor * 0.5) + (randomFactor * Math.random())))),
            255);
    }


}
