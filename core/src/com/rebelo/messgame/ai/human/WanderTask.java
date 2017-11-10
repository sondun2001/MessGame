package com.rebelo.messgame.ai.human;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.rebelo.messgame.entities.HumanAgent;

/**
 * Created by sondu on 7/4/2016.
 */
public class WanderTask extends LeafTask<HumanAgent> {

    @Override
    public void start () {
        Gdx.app.debug("WanderTask", "Started");
        HumanAgent human = getObject();
        human.wander();
    }

    @Override
    public Status execute() {
        return Status.RUNNING;
    }

    @Override
    public void end () {
        HumanAgent human = getObject();
    }

    @Override
    protected Task<HumanAgent> copyTo(Task<HumanAgent> task) {
        return null;
    }
}