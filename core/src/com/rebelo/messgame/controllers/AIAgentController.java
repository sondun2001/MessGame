package com.rebelo.messgame.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.StreamUtils;
import com.rebelo.messgame.ai.steering.Box2dSteeringEntity;
import com.rebelo.messgame.entities.HumanAgent;
import com.rebelo.messgame.services.EventBus;

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
        Reader reader = null;
        try {
            reader = Gdx.files.internal("ai/human.tree").reader();
            BehaviorTreeParser<HumanAgent> parser = new BehaviorTreeParser<HumanAgent>(BehaviorTreeParser.DEBUG_HIGH);
            _humanBehaviourTree = parser.parse(reader, humanAgent);
        } finally {
            StreamUtils.closeQuietly(reader);
        }
    }

    @Override
    public void setOwner(Box2dSteeringEntity agent) {
        reset();
        _humanBehaviourTree.start();
    }

    public void update(float delta) {
        long newTimeMillis = System.currentTimeMillis();
        float frameTimeSeconds = (newTimeMillis - currentTimeMillis) / 1000f;

        if (frameTimeSeconds > 1f) {
            currentTimeMillis = newTimeMillis;

            _humanBehaviourTree.step();
        }
        // TODO: How quickly do we step? Ask a service if we are ready for AI tick
    }

    @Override
    public void destroy() {
        reset();
        _humanBehaviourTree = null;
    }

    @Override
    public void reset() {
        _humanBehaviourTree.reset();
    }
}