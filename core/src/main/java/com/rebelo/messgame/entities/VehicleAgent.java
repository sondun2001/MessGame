package com.rebelo.messgame.entities;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.rebelo.messgame.MessGame;
import com.rebelo.messgame.ai.steering.Box2dSteeringEntity;

/**
 * Created by sondu on 6/12/2016.
 */
public class VehicleAgent extends Box2dSteeringEntity{
    public IAgent driver; // Can be human, AI (autonomouse vehicles)
    public Array<IAgent> passengers;

    public VehicleAgent(Sprite sprite, Body body, boolean independentFacing, int radiusInPixels) {
        super(sprite, body, independentFacing, radiusInPixels / MessGame.PIXELS_PER_METER);
    }
}
