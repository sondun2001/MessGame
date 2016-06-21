package com.rebelo.messgame.entities;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.rebelo.messgame.MessGame;
import com.rebelo.messgame.ai.steering.Box2dSteeringEntity;
import com.rebelo.messgame.entities.states.AnimalState;
import eos.good.Good;

/**
 * Shared among all living animals (including humans, pets, farm animals, livestock, etc)
 * Created by sondu on 6/12/2016.
 */
public class AnimalAgent extends Box2dSteeringEntity implements IAgent {

    private float _hunger = 0;
    private float _thirst = 0;
    private float _fatigue = 0;

    StateMachine<AnimalAgent, AnimalState> stateMachine = new DefaultStateMachine<AnimalAgent, AnimalState>(this, AnimalState.REST);

    public AnimalAgent(Sprite sprite, Body body, boolean independentFacing, int radiusInPixels) {
        super(sprite, body, independentFacing, radiusInPixels / MessGame.PIXELS_PER_METER);

        // TODO: Set state machine!
        // TODO: Create behaviour trees within state machine
        // TODO: Add steering behaviours within state machine / behaviour tree
    }

    @Override
    public void consumeGood(Good good) {

    }
}
