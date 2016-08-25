package com.rebelo.messgame.map;

/**
 * Created by sondu on 8/24/2016.
 */
public interface ITileMap {
    public MapTile getTile(float x, float y);
    public float getTileSize();
}
