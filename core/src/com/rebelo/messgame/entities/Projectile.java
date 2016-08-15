package com.rebelo.messgame.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.rebelo.messgame.MessGame;
import com.rebelo.messgame.map.MessMap;

/**
 * Created by sondu on 8/13/2016.
 */
public class Projectile implements Pool.Poolable {

    Body _body;
    Sprite _sprite;
    MessMap _map;

    Vector2 _startingPosition = new Vector2();
    boolean _alive = false;

    final static float MAX_DISTANCE_POTENTIAL = 10f;
    float _maxDistance = 10f;

    public Projectile(Sprite sprite, Body body, MessMap map) {
        _sprite = sprite;
        _body = body;
        _map = map;
    }

    public void fly(float impulseX, float impulseY, float posX, float posY, float forcePercent) {
        _maxDistance = MAX_DISTANCE_POTENTIAL * forcePercent;
        _startingPosition.set(posX, posY);
        //_body.getFixtureList().get(0).setSensor(true);
        _alive = true;
        _body.applyLinearImpulse(impulseX * forcePercent, impulseY * forcePercent, posX, posY, true);
    }

    public void update(float delta) {
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
        //_body.setLinearDamping(0.1f * delta);
    }

    public void draw (Batch batch) {
        Vector2 pos = _body.getPosition();
        float w = _sprite.getRegionWidth();
        float h = _sprite.getRegionHeight();
        float ox = w / 2f;
        float oy = h / 2f;

        /*
		batch.draw(region, //
			(pos.x * MessGame.PIXELS_PER_METER) - ox, (pos.y * MessGame.PIXELS_PER_METER) - oy, //
			ox, oy, //
			w, h, //
			1, 1, //
			body.getAngle() * MathUtils.radiansToDegrees); //
			*/
        _sprite.setPosition(pos.x * MessGame.PIXELS_PER_METER - ox, pos.y * MessGame.PIXELS_PER_METER - oy);
        _sprite.setRotation(_body.getAngle() * MathUtils.radiansToDegrees);
        _sprite.draw(batch);
    }

    @Override
    public void reset() {

    }
}
