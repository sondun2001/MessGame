package com.rebelo.messgame.entities;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool;
import com.rebelo.messgame.map.MessMap;

/**
 * Created by sondun2001 on 2/24/14.
 */
// TODO: Don't pool base class, pool objects with defined box2d bodies
// TODO: Don't specify type in base class
public class GameObject implements Pool.Poolable
{
    // TODO: Create enum of types of objects
    public static final int TYPE_LIGHT_POINT = 0;
    public static final int TYPE_LIGHT_CONE = 1;
    public static final int TYPE_BUILDING = 2;

    public BoundingBox boundingBox;
    public Body body;
    public int type;
    public TiledMapTileLayer.Cell cell;

    private float _quality = 1;
    private float _durability = 1;
    private float _efficiency = 1;

    public void init(int type, TiledMapTileLayer.Cell cell, Body body, BoundingBox box) {
        this.type = type;
        this.body = body;
        this.boundingBox = box;
        this.cell = cell;
    }

    public void dispose()
    {
        reset();
    }

    @Override
    public void reset()
    {
        // TODO: Don't destroy body, just move out of world and deactivate
        if (body != null) MessMap.world.destroyBody(body);
        boundingBox = null;
    }
}
