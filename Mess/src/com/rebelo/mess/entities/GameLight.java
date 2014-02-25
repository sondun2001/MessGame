package com.rebelo.mess.entities;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.PositionalLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by sondun2001 on 2/24/14.
 */
public class GameLight extends GameObject {

    public static final int TYPE_POINT = 0;
    public static final int TYPE_CONE = 1;

    private int _type;
    private PositionalLight _light;

    public GameLight(Sprite sprite, int type, RayHandler rayHandler, int numRays, Color color, float distance)
    {
        super(sprite);

        _type = type;

        if (_type == TYPE_POINT)
        {
            _light = new PointLight(rayHandler, numRays, color, distance, 0f, 0f);
            _light.setSoft(true);
            _light.setSoftnessLenght(2f);
        }
         else if (_type == TYPE_CONE)
        {
            _light = new ConeLight(rayHandler, numRays, color, distance, 0f, 0f, 45, 30);
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        _light.remove();
    }

    public void setDistance(float distance)
    {
        _light.setDistance(distance);
    }

    public void setPosition(float x, float y)
    {
        _light.setPosition(x, y);
    }

    public void setDirection(float direction)
    {
        _light.setDirection(direction);
    }

    public void attachToBody(Body body, float offsetX, float offsetY)
    {
        _light.attachToBody(body, offsetX, offsetY);
    }
}
