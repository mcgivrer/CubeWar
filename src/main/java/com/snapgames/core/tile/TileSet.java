package com.snapgames.core.tile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TileSet {
    public int tileWidth;
    public int tileHeight;
    private int xOffset;
    private int yOffset;
    private Map<String, Tile> tiles = new HashMap<>();
    private BufferedImage srcImage;
    private BufferedImage[] tileImages;
    private static int index = 0;

    TileSet(String name, int tileW, int tileH, int xOffset, int yOffset, String pathToImage) {
        this.tileWidth = tileW;
        this.tileHeight = tileH;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        try {
            srcImage = ImageIO.read(TileSet.class.getResourceAsStream(pathToImage));
            extractTiles();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void extractTiles() {
        int nbX = (srcImage.getWidth() - xOffset) / tileWidth;
        int nbY = (srcImage.getHeight() - yOffset) / tileHeight;
        tileImages = new BufferedImage[nbX * nbY];
        int index = 0;
        for (int x = xOffset; x < srcImage.getWidth(); x += tileWidth) {
            for (int y = xOffset; y < srcImage.getHeight(); y += tileHeight) {
                Tile t = new Tile(++index, srcImage.getSubimage(x, y, tileWidth, tileHeight));
                tiles.put("" + index, t);
                tileImages[index] = srcImage.getSubimage(x, y, tileWidth, tileHeight);
            }
        }
    }

    public Tile getTile(String id) {
        return tiles.get(id);
    }
}
