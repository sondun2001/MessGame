package com.rebelo.mess.entities;

import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool;
import com.rebelo.mess.map.MessMap;

/**
 * Created by sondun2001 on 2/24/14.
 */
public class GameObject implements Pool.Poolable
{
    // TODO: Create enum of types of objects
    public static final int TYPE_LIGHT_POINT = 0;
    public static final int TYPE_LIGHT_CONE = 1;
    public static final int TYPE_BUILDING = 2;

    public BoundingBox boundingBox;
    public Body body;
    public int type;

    private float _quality = 1;
    private float _durability = 1;
    private float _efficiency = 1;

    public void init(int type, Body body, BoundingBox box)
    {
        this.type = type;
        this.body = body;
        this.boundingBox = box;
    }

    public GameObject()
    {
       super();
    }

    public void dispose()
    {
        reset();
    }

    @Override
    public void reset()
    {
        if (body != null) MessMap.world.destroyBody(body);
        boundingBox = null;
    }
}
