package com.rebelo.messgame.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.rebelo.messgame.MessGame;
import com.rebelo.messgame.ai.steering.Box2dSteeringEntity;
/*
 * Created by sondu on 6/11/2016.
 */
public class MovingAgent extends Box2dSteeringEntity {

    protected Box2dSteeringEntity _steeringEntity;

    public MovingAgent(Sprite sprite, Body body, boolean independentFacing, int radiusInPixels) {
        super(sprite, body, independentFacing, radiusInPixels / MessGame.PIXELS_PER_METER);
    }
}
