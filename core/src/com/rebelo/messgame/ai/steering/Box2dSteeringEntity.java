/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.rebelo.messgame.ai.steering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.rebelo.messgame.MessGame;

/** A steering entity for box2d physics engine.
 * 
 * @author davebaol */
public class Box2dSteeringEntity implements Steerable<Vector2> {
	protected Sprite sprite;
	protected Body body;

	float boundingRadius;
	boolean tagged;

	float maxLinearSpeed;
	float maxLinearAcceleration;
	float maxAngularSpeed;
	float maxAngularAcceleration;

	boolean independentFacing;

	protected SteeringBehavior<Vector2> steeringBehavior;

	private static final SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<Vector2>(new Vector2());

	public Box2dSteeringEntity (Sprite sprite, Body body, boolean independentFacing, float boundingRadius) {
		this.sprite = sprite;
		this.body = body;
		this.independentFacing = independentFacing;
		this.boundingRadius = boundingRadius;
		this.tagged = false;

		body.setUserData(this);
	}

	public TextureRegion getRegion () {
		return sprite;
	}

	public void setRegion (Sprite region) {
		this.sprite = region;
	}

	public Body getBody () {
		return body;
	}

	public void setBody (Body body) {
		this.body = body;
	}

	public boolean isIndependentFacing () {
		return independentFacing;
	}

	public void setIndependentFacing (boolean independentFacing) {
		this.independentFacing = independentFacing;
	}

	@Override
	public Vector2 getPosition () {
		return body.getPosition();
	}

	@Override
	public float getOrientation () {
		return body.getAngle();
	}

	@Override
	public void setOrientation (float orientation) {
		body.setTransform(getPosition(), orientation);
	}

	@Override
	public Vector2 getLinearVelocity () {
		return body.getLinearVelocity();
	}

	@Override
	public float getAngularVelocity () {
		return body.getAngularVelocity();
	}

	@Override
	public float getBoundingRadius () {
		return boundingRadius;
	}

	@Override
	public boolean isTagged () {
		return tagged;
	}

	@Override
	public void setTagged (boolean tagged) {
		this.tagged = tagged;
	}

	@Override
	public Location<Vector2> newLocation () {
		return new Box2dLocation();
	}

	@Override
	public float vectorToAngle (Vector2 vector) {
		return Box2dSteeringUtils.vectorToAngle(vector);
	}

	@Override
	public Vector2 angleToVector (Vector2 outVector, float angle) {
		return Box2dSteeringUtils.angleToVector(outVector, angle);
	}

	public SteeringBehavior<Vector2> getSteeringBehavior () {
		return steeringBehavior;
	}

	public void setSteeringBehavior (SteeringBehavior<Vector2> steeringBehavior) {
		this.steeringBehavior = steeringBehavior;
	}

	public void update (float deltaTime) {
		if (steeringBehavior != null) {
			// Calculate steering acceleration
			steeringBehavior.calculateSteering(steeringOutput);

			/*
			 * Here you might want to add a motor control layer filtering steering accelerations.
			 * 
			 * For instance, a car in a driving game has physical constraints on its movement: it cannot turn while stationary; the
			 * faster it moves, the slower it can turn (without going into a skid); it can brake much more quickly than it can
			 * accelerate; and it only moves in the direction it is facing (ignoring power slides).
			 */

			// Apply steering acceleration
			applySteering(steeringOutput, deltaTime);
		}

        // TODO: Should be map width / height
        wrapAround(Gdx.graphics.getWidth() / MessGame.PIXELS_PER_METER, Gdx.graphics.getHeight() / MessGame.PIXELS_PER_METER);
	}

	protected void applySteering (SteeringAcceleration<Vector2> steering, float deltaTime) {
        boolean angularAcceleration = false;
        boolean linearAcceleration = false;

		// Update position and linear velocity.
		if (!steeringOutput.linear.isZero()) {
			// this method internally scales the force by deltaTime
			body.applyForceToCenter(steeringOutput.linear, true);
            linearAcceleration = true;
		}

        Vector2 linVel = getLinearVelocity();
		// Update orientation and angular velocity
		if (isIndependentFacing()) {
			if (steeringOutput.angular != 0) {
				// this method internally scales the torque by deltaTime
				body.applyTorque(steeringOutput.angular, true);
                angularAcceleration = true;
			}
		} else {
			// If we haven't got any velocity, then we can do nothing.
			if (!linVel.isZero(getZeroLinearSpeedThreshold())) {
				float newOrientation = vectorToAngle(linVel);
				body.setAngularVelocity((newOrientation - getAngularVelocity()) * deltaTime); // this is superfluous if independentFacing is always true
				body.setTransform(body.getPosition(), newOrientation);
                angularAcceleration = true;
			}
		}

        if (linearAcceleration) {
            // Cap the linear speed
            float currentSpeedSquare = linVel.len2();
            float maxLinearSpeed = getMaxLinearSpeed();
            if (currentSpeedSquare > maxLinearSpeed * maxLinearSpeed) {
                body.setLinearVelocity(linVel.scl(maxLinearSpeed / (float)Math.sqrt(currentSpeedSquare)));
            }
        } else {
            linVel.scl(0.9f);
            if (linVel.len() < 0.1f) {
                linVel.set(0f, 0f);
            }
            body.setLinearVelocity(linVel);
        }

        float angularVelocity = body.getAngularVelocity();
        if (angularAcceleration) {
            // Cap the angular speed
            float maxAngVelocity = getMaxAngularSpeed();
            if (angularVelocity > maxAngVelocity) {
                body.setAngularVelocity(maxAngVelocity);
            }
        } else if (!linearAcceleration) {
            angularVelocity *= 0.9f;
            if (angularVelocity < 0.1f) {
                angularVelocity = 0f;
            }
            body.setAngularVelocity(angularVelocity);
        }
	}

    // the display area is considered to wrap around from top to bottom
    // and from left to right
    protected void wrapAround (float maxX, float maxY) {
        float k = Float.POSITIVE_INFINITY;
        Vector2 pos = body.getPosition();

        if (pos.x > maxX) k = pos.x = 0.0f;

        if (pos.x < 0) k = pos.x = maxX;

        if (pos.y < 0) k = pos.y = maxY;

        if (pos.y > maxY) k = pos.y = 0.0f;

        if (k != Float.POSITIVE_INFINITY) body.setTransform(pos, body.getAngle());
    }

    // TODO: Don't look directly at body position, use another vec2 that has interopolated values
	public void draw (Batch batch) {
		Vector2 pos = body.getPosition();
		float w = sprite.getRegionWidth();
		float h = sprite.getRegionHeight();
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
        sprite.setPosition(pos.x * MessGame.PIXELS_PER_METER - ox, pos.y * MessGame.PIXELS_PER_METER - oy);
        sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
        sprite.draw(batch);
	}

	//
	// Limiter implementation
	//

	@Override
	public float getMaxLinearSpeed () {
		return maxLinearSpeed;
	}

	@Override
	public void setMaxLinearSpeed (float maxLinearSpeed) {
		this.maxLinearSpeed = maxLinearSpeed;
	}

	@Override
	public float getMaxLinearAcceleration () {
		return maxLinearAcceleration;
	}

	@Override
	public void setMaxLinearAcceleration (float maxLinearAcceleration) {
		this.maxLinearAcceleration = maxLinearAcceleration;
	}

	@Override
	public float getMaxAngularSpeed () {
		return maxAngularSpeed;
	}

	@Override
	public void setMaxAngularSpeed (float maxAngularSpeed) {
		this.maxAngularSpeed = maxAngularSpeed;
	}

	@Override
	public float getMaxAngularAcceleration () {
		return maxAngularAcceleration;
	}

	@Override
	public void setMaxAngularAcceleration (float maxAngularAcceleration) {
		this.maxAngularAcceleration = maxAngularAcceleration;
	}

	@Override
	public float getZeroLinearSpeedThreshold () {
		return 0.001f;
	}

	@Override
	public void setZeroLinearSpeedThreshold (float value) {
		throw new UnsupportedOperationException();
	}
}
