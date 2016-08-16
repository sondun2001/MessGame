package com.rebelo.messgame.objects.projectiles;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.rebelo.messgame.map.MessMap;

/**
 * Created by sondu on 8/16/2016.
 */
public class SnowBall extends Projectile {

    final static float MAX_DISTANCE_POTENTIAL = 10f;
    float _maxDistance = 10f;

    public SnowBall(Sprite sprite, MessMap map) {
        super(sprite, map);
    }

    @Override
    public void fly(float impulseX, float impulseY, float posX, float posY, float forcePercent) {
        super.fly(impulseX, impulseY, posX, posY, forcePercent);
        _maxDistance = MAX_DISTANCE_POTENTIAL * forcePercent;
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (_alive) {
            float distance = _startingPosition.dst(_body.getPosition());
            /*
            if (distance > 9) {
                _body.getFixtureList().get(0).setSensor(false);
            }
            */
            if (distance >= _maxDistance) {
                _alive = false;
                _body.setLinearVelocity(0f, 0f);
                _body.setAwake(false);
                _body.setActive(false);
            }
        }
    }
}
