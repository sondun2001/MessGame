package com.rebelo.messgame.objects.projectiles;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
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

    protected Vector2 _startingPosition = new Vector2();
    protected float _projectileDrag = 0.9f;

    protected boolean _alive = false;

    public Projectile(Sprite sprite, MessMap map) {
        // TODO: Get projectile from pool
        // TODO: FInd better way of getting sprites
        // TODO: Get sprite animation for this agent
        // TODO: Create physics body
        CircleShape circleShape = new CircleShape();
        circleShape.setPosition(new Vector2());
        int radiusInPixels = (int)((sprite.getRegionWidth() + sprite.getRegionHeight()) / 4f);
        circleShape.setRadius(radiusInPixels / MessGame.PIXELS_PER_METER);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.bullet = true;
        Body body = map.world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 0.2f;
        fixtureDef.shape = circleShape;
        fixtureDef.filter.groupIndex = 0;
        body.createFixture(fixtureDef);
        circleShape.dispose();

        body.setUserData(this);

        _sprite = sprite;
        _body = body;
        _map = map;
    }

    public void fly(float impulseX, float impulseY, float posX, float posY, float forcePercent) {
        _startingPosition.set(posX, posY);
        _body.setTransform(posX, posY, _body.getAngle());
        //_body.getFixtureList().get(0).setSensor(true);
        _alive = true;
        _body.applyLinearImpulse(impulseX * forcePercent, impulseY * forcePercent, posX, posY, true);
    }

    /**
     * Fixed step update, delta not needed
     * @param delta
     */
    public void update(float delta) {
        Vector2 linVel = _body.getLinearVelocity();
        linVel.scl(_projectileDrag);

        if (linVel.isZero(5f)) {
            die();
        } else {
            _body.setLinearVelocity(linVel);
        }
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

    void die() {
        _alive = false;
        _body.setLinearVelocity(0f, 0f);
        _body.setAwake(false);
        _body.setActive(false);
    }

    @Override
    public void reset() {

    }
}
