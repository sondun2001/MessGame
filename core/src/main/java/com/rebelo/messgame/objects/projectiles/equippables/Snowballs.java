package com.rebelo.messgame.objects.projectiles.equippables;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.rebelo.messgame.map.MessMap;
import com.rebelo.messgame.objects.projectiles.SnowBall;
import com.rebelo.messgame.services.ProjectileFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by sondu on 8/16/2016.
 */
public class Snowballs implements IEquippable {

    Sprite _projectileSprite;
    MessMap _map;
    float _firePercent;
    float _xPos;
    float _yPos;
    Vector2 _direction;
    boolean _isActive;

    public Snowballs(Sprite projectileSprite, MessMap map) {
        _projectileSprite = projectileSprite;
        _map = map;
    }

    @Override
    public int getNumHandsRequired() {
        return 1;
    }

    @Override
    public void drop() {

    }

    @Override
    public void activate(float xPos, float yPos, Vector2 direction, float force, float delta)
    {
       _xPos = xPos;
       _yPos = yPos;
       _direction = direction;
       _firePercent += delta;
       _isActive = true;
    }

    @Override
    public void deactivate() {
        if (_isActive) {
            // Throw Snowballs!
            try {
                ProjectileFactory.getInstance().createProjectile(SnowBall.class, _projectileSprite, _map, _xPos, _yPos, _direction.x, _direction.y, _firePercent);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }

            _firePercent = 0.0f;
            _isActive = false;
        }
    }

    @Override
    public void equip() {
        _firePercent = 0.0f;
    }

    @Override
    public void unequip() {

    }

    @Override
    public void reload() {

    }

    @Override
    public void cycleOptions() {

    }
}
