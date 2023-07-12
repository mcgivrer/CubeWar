package com.snapgames.core.tile;

import com.snapgames.core.entity.Camera;
import com.snapgames.core.entity.Entity;

import java.awt.*;

public class TileMap extends Entity<TileMap> {

    public TileSet tileSet;
    public Camera cameraView;
    private int mapWidth;
    private int mapHeight;
    private Tile[] map;

    public TileMap(String n, int tileMapWidth, int tileMapHeight) {
        super(n);
        this.mapHeight = tileMapHeight;
        this.mapWidth = tileMapWidth;
        this.map = new Tile[this.mapHeight * this.mapWidth];
    }


    @Override
    public void draw(Graphics2D g) {
        if (cameraView != null) {
            int startX = (int) (cameraView.pos.x / tileSet.tileWidth) - 1;
            int startY = (int) (cameraView.pos.y / tileSet.tileHeight) - 1;
            int endX = (int) ((cameraView.pos.x / tileSet.tileWidth) + mapWidth + 1);
            int endY = (int) ((cameraView.pos.x / tileSet.tileWidth) + mapHeight + 1);
            for (int ix = startX; ix < endX; ix += 1) {
                for (int iy = startY; iy < endY; iy += 1) {
                    g.drawImage(map[ix + (iy * mapWidth)].image,
                            startX * tileSet.tileWidth,
                            startY * tileSet.tileHeight,
                            null);
                }
            }
        }
    }

    /**
     * Set {@link TileMap} size
     *
     * @param mw width of the map
     * @param mh height of the map.
     * @return the updated TileMap.
     */
    public TileMap setMapSize(int mw, int mh) {
        this.mapWidth = mw;
        this.mapHeight = mh;
        return this;
    }

    // TODO implement the getter for a tile in the map.
    public boolean isTileAtIsABlocker(double x, double y) {
        return false;
    }
}
