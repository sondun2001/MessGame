package com.rebelo.messgame.utils;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by sondu on 8/17/2016.
 */
public class GamePadSteering <T extends Vector<T>> extends SteeringBehavior<T> {

    Vector2 _targetPosition = new Vector2(0f, 0f);
    float _angularVelocity = 0f;

    public GamePadSteering(Steerable<T> owner) {
        super(owner);
    }

    public void setVelocity(float xVelocity, float yVelocity) {
        _targetPosition.set(xVelocity, yVelocity);
        if (_targetPosition.len() > 0.2) {
            _targetPosition.scl(2f);
        } else {
            _targetPosition.set(0f, 0f);
        }
    }

    public void setAngularVelocity(float velocity) {
        _angularVelocity = velocity;
    }

    @Override
    protected SteeringAcceleration<T> calculateRealSteering(SteeringAcceleration<T> steering) {
        steering.linear.set((T) _targetPosition);
        steering.angular = _angularVelocity;
        return steering;
    }
}
