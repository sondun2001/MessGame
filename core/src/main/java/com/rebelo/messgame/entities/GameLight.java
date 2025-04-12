package com.rebelo.messgame.entities;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.PositionalLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by sondun2001 on 2/24/14.
 * This is specifically to be used with the tilemap
 */
// TODO: Fix pooling so we can re use lights by type and just update props
public class GameLight extends GameObject {

    public static final int NUM_RAYS = 800;

    private PositionalLight _light;
    private Cell _cell;
    private StaticTiledMapTile _tile;
    private Sprite _spriteOn;
    private Sprite _spriteOff;


    public void init(Cell cell, Sprite spriteOn, Sprite spriteOff, int type, RayHandler rayHandler)
    {
        super.init(type, null, null, null);

        _spriteOn = spriteOn;
        _spriteOff = spriteOff;

        _cell = cell;
        _tile = new StaticTiledMapTile(spriteOn);
        _tile.setId(2);
        _cell.setTile(_tile);

        _isActive = true;

        if (type == TYPE_LIGHT_POINT)
        {
            _light = new PointLight(rayHandler, NUM_RAYS, Color.WHITE, 0f, 0f, 0f);
            _light.setSoftnessLength(0f);
            _light.setXray(false);
            _light.setSoft(true);
        }
        else if (type == TYPE_LIGHT_CONE)
        {
            _light = new ConeLight(rayHandler, NUM_RAYS, Color.WHITE, 0f, 0f, 0f, 45, 30);
            _light.setSoftnessLength(0f);
            _light.setXray(false);
        }
    }

    @Override
    public void reset() {
        super.reset();

        _light.remove();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void setActive(boolean value)
    {
        if (_isActive != value) {
            _isActive = value;
            updateState();
        }
    }

    private void updateState() {
        Sprite sprite = (_isActive) ? _spriteOn : _spriteOff;
        _tile.getTextureRegion().setTexture(sprite.getTexture());
        _light.setActive(_isActive);
    }

    public void setColor(Color color)
    {
        _light.setColor(color);
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
