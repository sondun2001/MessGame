package com.rebelo.messgame.services;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.rebelo.messgame.objects.projectiles.Projectile;
import com.rebelo.messgame.map.MessMap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

/**
 * Created by sondu on 8/13/2016.
 */
public class ProjectileFactory {
    private static ProjectileFactory ourInstance = new ProjectileFactory();
    public static ProjectileFactory getInstance() {
        return ourInstance;
    }

    // TODO: Don't update dead projectiless
    private Array<Projectile> _projectiles = new Array<Projectile>();

    public ProjectileFactory() {
        _projectiles.ordered = false;
    }

    // TODO: Get sprite from map for this agent
    public Projectile createProjectile(Class projectileClass, Sprite sprite, MessMap map, float posX, float posY, float impulseX, float impulseY, float forcePercent) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<?> cons = projectileClass.getConstructor(Sprite.class, MessMap.class);
        Projectile projectile = (Projectile) cons.newInstance(sprite, map);
        _projectiles.add(projectile);

        projectile.fly(impulseX, impulseY, posX, posY, forcePercent);
        return projectile;
    }

    public void update(float delta) {
        // update all projectiles
        for (Projectile projectile : _projectiles) {
            projectile.update(delta);
        }
    }

    public void draw (Batch batch) {
        // Draw all projectiles

        for (Projectile projectile : _projectiles) {
            projectile.draw(batch);
        }
    }
}
