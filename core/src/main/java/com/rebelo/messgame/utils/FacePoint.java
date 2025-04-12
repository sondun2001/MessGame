package com.rebelo.messgame.utils;

import com.badlogic.gdx.ai.steer.Limiter;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.behaviors.ReachOrientation;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.Vector;

/**
 * Created by sondu on 8/17/2016.
 */
public class FacePoint<T extends Vector<T>> extends ReachOrientation<T> {

    private T _targetPosition;

    public FacePoint(Steerable<T> owner) {
        super(owner);
    }

    public void setTargetVector(T targetPosition) {
        _targetPosition = targetPosition;
    }

    @Override
    protected SteeringAcceleration<T> calculateRealSteering (SteeringAcceleration<T> steering) {
        // Get the direction to target
        T toTarget = steering.linear.set(_targetPosition).sub(owner.getPosition());

        // Check for a zero direction, and return no steering if so
        if (toTarget.isZero(getActualLimiter().getZeroLinearSpeedThreshold())) return steering.setZero();

        // Calculate the orientation to face the target
        float orientation = owner.vectorToAngle(toTarget);

        // Delegate to ReachOrientation
        return reachOrientation(steering, orientation);
    }

    @Override
    public FacePoint<T> setOwner (Steerable<T> owner) {
        this.owner = owner;
        return this;
    }

    @Override
    public FacePoint<T> setEnabled (boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /** Sets the limiter of this steering behavior. The given limiter must at least take care of the maximum angular speed and
     * acceleration.
     * @return this behavior for chaining. */
    @Override
    public FacePoint<T> setLimiter (Limiter limiter) {
        this.limiter = limiter;
        return this;
    }

    /** Sets the target to align to. Notice that this method is inherited from {@link ReachOrientation}, but is completely useless
     * for {@code FacePoint} because the target orientation is determined by the velocity of the owner itself.
     * @return this behavior for chaining. */
    @Override
    public FacePoint<T> setTarget (Location<T> target) {
        this.target = target;
        return this;
    }

    @Override
    public FacePoint<T> setAlignTolerance (float alignTolerance) {
        this.alignTolerance = alignTolerance;
        return this;
    }

    @Override
    public FacePoint<T> setDecelerationRadius (float decelerationRadius) {
        this.decelerationRadius = decelerationRadius;
        return this;
    }

    @Override
    public FacePoint<T> setTimeToTarget (float timeToTarget) {
        this.timeToTarget = timeToTarget;
        return this;
    }
}
