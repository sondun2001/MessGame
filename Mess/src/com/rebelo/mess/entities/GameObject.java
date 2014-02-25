package com.rebelo.mess.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by sondun2001 on 2/24/14.
 */
public class GameObject {

    private Sprite _sprite;

    private float _quality = 1;
    private float _durability = 1;
    private float _efficiency = 1;

    public GameObject(Sprite sprite)
    {
        _sprite = sprite;
    }

    public void dispose()
    {
        // Used by children
    }
}
