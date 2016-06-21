package com.rebelo.messgame.entities;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.rebelo.messgame.MessGame;
import com.rebelo.messgame.ai.steering.Box2dSteeringEntity;
import com.rebelo.messgame.entities.states.HumanState;
import eos.good.Good;

/**
 * Base agent class
 * Created by sondu on 6/12/2016.
 */
public class HumanAgent extends Box2dSteeringEntity implements IAgent{

    // TODO: Control sprite from here
    // TODO: Social Graph (Connections and strength in relationships)
    // TODO: Disable steering behaviour while in vehicle
    // TODO: Hide agent while in vehicle

    StateMachine<HumanAgent, HumanState> stateMachine = new DefaultStateMachine<HumanAgent, HumanState>(this, HumanState.PHYSIOLIGICAL);

    public HumanAgent(Sprite sprite, Body body, boolean independentFacing, int radiusInPixels) {
        super(sprite, body, independentFacing, radiusInPixels / MessGame.PIXELS_PER_METER);
    }

    public void wander() {
        this.setMaxLinearAcceleration(10);
        this.setMaxLinearSpeed(3);
        this.setMaxAngularAcceleration(.5f); // greater than 0 because independent facing is enabled
        this.setMaxAngularSpeed(5);

        Wander wander = new Wander<Vector2>(this) //
                .setFaceEnabled(true) // We want to use Face internally (independent facing is on)
                .setAlignTolerance(0.001f) // Used by Face
                .setDecelerationRadius(1) // Used by Face
                .setTimeToTarget(0.1f) // Used by Face
                .setWanderOffset(3) //
                .setWanderOrientation(3) //
                .setWanderRadius(1) //
                .setWanderRate(MathUtils.PI2 * 4);

        this.setSteeringBehavior(wander);
    }

    // TODO: Decrease fatigue
    public void rest() {

    }

    // TODO: Follow another agent
    public void follow(HumanAgent agent) {

    }

    // TODO: Attempt to stay out of sight
    public void hide() {

    }

    @Override
    public void consumeGood(Good good) {

    }
}
