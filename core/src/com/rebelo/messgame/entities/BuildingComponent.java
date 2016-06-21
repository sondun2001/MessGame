package com.rebelo.messgame.entities;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by sondu on 6/15/2016.
 */
public class BuildingComponent extends GameObject{

    public void init(Body body, TiledMapTileLayer.Cell cell, BoundingBox box) {
        super.init(GameObject.TYPE_BUILDING, cell, body, box);
    }
}
