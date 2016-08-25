package com.rebelo.messgame.map;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

/**
 * Created by sondu on 8/24/2016.
 * http://gamedev.stackexchange.com/questions/100544/libgdx-how-can-i-correctly-handle-tile-transitions-connected-textures
 */
public class MapTile {

    public final int xPos;
    public final int yPos;

    private final TiledMapTileLayer tileMap;
    private TileType tileType;
    private TiledMapTile tile;
    private byte displayTile = 0;

    public MapTile(int x, int y, TiledMapTile tile, TiledMapTileLayer map){
        xPos = x;
        yPos = y;
        tileMap = map;
        this.tile = tile;
    }

    public void setTileType(TileType newType){
        tileType = newType;

        // Check the current tile
        checkNeighbours();

        MapTile neighbourTile;

        // Check the tile above
        neighbourTile = tileMap.getTile(xPos, yPos + 1);
        if(neighbourTile != null){
            neighbourTile.checkNeighbours();
        }
        // Check the tile below
        neighbourTile = tileMap.getTile(xPos, yPos - 1);
        if(neighbourTile != null){
            neighbourTile.checkNeighbours();
        }
        // Check the tile to the left
        neighbourTile = tileMap.getTile(xPos - 1, yPos);
        if(neighbourTile != null){
            neighbourTile.checkNeighbours();
        }
        // Check the tile to the right
        neighbourTile = tileMap.getTile(xPos + 1, yPos);
        if(neighbourTile != null){
            neighbourTile.checkNeighbours();
        }
    }

    public void checkNeighbours(){
        displayTile = 0;

        if(this.tileType == null){
            return;
        }

        // Check the tile above
        MapTile neighbourTile = tileMap.getTile(xPos, yPos + 1);
        if(neighbourTile != null){
            if(neighbourTile.tileType == tileType ){
                displayTile += 1;
            }
        }

        // Check the tile below
        neighbourTile = tileMap.getTile(xPos, yPos - 1);
        if(neighbourTile != null){
            if(neighbourTile.tileType == tileType ){
                displayTile += 4;
            }
        }

        // Check the tile to the left
        neighbourTile = tileMap.getTile(xPos + 1, yPos);
        if(neighbourTile != null){
            if(neighbourTile.tileType == tileType ){
                displayTile += 2;
            }
        }

        // Check the tile to the right
        neighbourTile = tileMap.getTile(xPos - 1, yPos);
        if(neighbourTile != null){
            if(neighbourTile.tileType == tileType ){
                displayTile += 8;
            }
        }
    }

    public void renderTile(SpriteBatch batch){
        if(tileType != null){
            batch.draw(
                    tileType.tileVariations[displayTile],
                    xPos * tileMap.getTileSize(),
                    yPos * tileMap.getTileSize(),
                    tileMap.getTileSize(),
                    tileMap.getTileSize()
            );
        }
    }
}