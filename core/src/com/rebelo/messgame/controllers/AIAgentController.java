package com.rebelo.messgame.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.BehaviorTree;
import com.badlogic.gdx.ai.btree.utils.BehaviorTreeParser;
import com.badlogic.gdx.utils.StreamUtils;
import com.rebelo.messgame.entities.HumanAgent;

import java.io.Reader;

/**
 * Created by sondu on 7/3/2016.
 */
public class AIAgentController implements IAgentController {

    private HumanAgent _humanAgent;
    private BehaviorTree<HumanAgent> _humanBehaviourTree;

    public AIAgentController(HumanAgent humanAgent) {
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

    public void update(float delta) {
        // TODO: How quickly do we step? Ask a service if we are ready for AI tick
        _humanBehaviourTree.step();
    }
}