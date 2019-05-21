package com.rebelo.messgame.objects.projectiles.equippables;

import box2dLight.ConeLight;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.rebelo.messgame.map.MessMap;
import com.rebelo.messgame.objects.projectiles.SnowBall;
import com.rebelo.messgame.services.ProjectileFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by sondu on 8/16/2016.
 */
public class Flashlight implements IEquippable {

    MessMap _map;

    private ConeLight _light;
    private static final int NUM_RAYS = 800;

    public Flashlight(Body body, MessMap map)
    {
        _light = new ConeLight(MessMap.rayHandler, NUM_RAYS, Color.WHITE, 10f, 0f, 0f, 0f, 20f);

        _light.setSoftnessLength(0f);
        _light.setXray(false);
        _light.attachToBody(body, 0.0f, 0.0f, 90f);
        _light.setActive(true);

        _map = map;
    }

    @Override
    public int getNumHandsRequired() {
        return 1;
    }

    @Override
    public void drop() {
        // cleanup, about to drop
    }

    @Override
    public void equip() {

    }

    @Override
    public void unequip() {

    }

    @Override
    public void activate(float xPos, float yPos, Vector2 direction, float force, float delta) {
        _light.setActive(true);
    }

    @Override
    public void deactivate() {
        _light.setActive(false);
    }

    @Override
    public void reload() {

    }

    @Override
    public void cycleOptions() {

    }
}
