package com.rebelo.messgame.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.SerializationException;
import com.badlogic.gdx.utils.StreamUtils;
import com.rebelo.messgame.ai.steering.Box2dSteeringEntity;
import com.rebelo.messgame.entities.HumanAgent;
import com.rebelo.messgame.services.EventBus;
import org.newdawn.slick.util.Log;

import java.io.Console;
import java.io.Reader;

/**
 * Created by sondu on 7/3/2016.
 */
public class AIAgentController implements IAgentController, Pool.Poolable {

    static final double DT = 1/60.0;
    static final int MAX_UPDATES_PER_FRAME = 3; //for preventing spiral of death
    private long currentTimeMillis;

    private HumanAgent _humanAgent;
    private BehaviorTree<HumanAgent> _humanBehaviourTree;

    public AIAgentController(HumanAgent humanAgent) {
        currentTimeMillis = System.currentTimeMillis();

        _humanAgent = humanAgent;

        // Parse human behaviour tree
        // todo: THIS SHOULDN'T BE IN EACH AGENT!
        Reader reader = null;
        try {
            reader = Gdx.files.internal("ai/human.tree").reader();
            BehaviorTreeParser<HumanAgent> parser = new BehaviorTreeParser<HumanAgent>(BehaviorTreeParser.DEBUG_NONE);
            _humanBehaviourTree = parser.parse(reader, humanAgent);
        } catch (SerializationException ex) {
            Log.error("Failed to parse behavior tree", ex);
            _humanBehaviourTree = null;
        } finally {
            StreamUtils.closeQuietly(reader);
        }
    }

    @Override
    public void setOwner(Box2dSteeringEntity agent) {
        reset();
        _humanAgent = (HumanAgent) agent;
        _humanBehaviourTree.setObject(_humanAgent);
        _humanBehaviourTree.start();
    }

    public void update(float delta) {

        // todo: Have a behaviour system in Ashley that waits for all behaviors to be parsed
        long newTimeMillis = System.currentTimeMillis();
        float frameTimeSeconds = (newTimeMillis - currentTimeMillis) / 1000f;

        if (frameTimeSeconds > 5f) {
            currentTimeMillis = newTimeMillis;
            _humanBehaviourTree.step();
        }
        // TODO: How quickly do we step? Ask a service if we are ready for AI tick
        // Should be every time it has to make a decision
    }

    @Override
    public void destroy() {
        reset();
        _humanBehaviourTree = null;
    }

    @Override
    public void reset() {
        _humanAgent = null;
        _humanBehaviourTree.resetTask();
    }
}