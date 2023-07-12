package com.snapgames.core.tile;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Tile {
    int id;
    public BufferedImage image;
    public boolean blocker;
    public Map<String, Object> attributes = new HashMap<>();

    public Tile(int id, BufferedImage tileImage) {
        this.id = id;
        this.image = tileImage;
    }
}
