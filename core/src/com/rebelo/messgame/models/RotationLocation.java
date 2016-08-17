package com.rebelo.messgame.models;

import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector2;
import com.rebelo.messgame.ai.steering.Box2dSteeringUtils;

/**
 * Created by sondu on 8/17/2016.
 */
public class RotationLocation implements Location<Vector2> {

    Vector2 _rotationLocation;
    float _orientation;

    public RotationLocation() {
        _rotationLocation = new Vector2(0f, 0f);
        _orientation = 0f;
    }

    public void setPosition(float x, float y) {
        _rotationLocation.set(x, y);
    }

    @Override
    public Vector2 getPosition() {
        return _rotationLocation;
    }

    @Override
    public float getOrientation() {
        return _orientation;
    }

    @Override
    public void setOrientation(float orientation) {
        _orientation = orientation;
    }

    @Override
    public float vectorToAngle(Vector2 vector) {
        return Box2dSteeringUtils.vectorToAngle(vector);
    }

    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        return Box2dSteeringUtils.angleToVector(outVector, angle);
    }

    @Override
    public Location<Vector2> newLocation() {
        return null;
    }
}
