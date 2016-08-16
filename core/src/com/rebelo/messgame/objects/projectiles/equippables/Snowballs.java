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
    public void use(float xPos, float yPos, Vector2 direction, float forcePercent) {
        // Throw Snowballs!
        try {
            ProjectileFactory.getInstance().createProjectile(SnowBall.class, _projectileSprite, _map, xPos, yPos, direction.x, direction.y, forcePercent);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    @Override
    public void reload() {

    }

    @Override
    public void cycleOptions() {

    }
}
