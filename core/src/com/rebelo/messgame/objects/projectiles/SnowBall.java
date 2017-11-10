package com.rebelo.messgame.objects.projectiles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.rebelo.messgame.map.MessMap;

/**
 * Created by sondu on 8/16/2016.
 */
public class SnowBall extends Projectile {

    public SnowBall(Sprite sprite, MessMap map) {
        super(sprite, map);
    }

    @Override
    public void fly(float impulseX, float impulseY, float posX, float posY, float forcePercent) {
        super.fly(impulseX, impulseY, posX, posY, forcePercent);
        _projectileDrag = 0.95f;
    }

    @Override
    public void update(float delta) {
        super.update(delta);


    }
}
