package com.rebelo.messgame.ai.human;

import com.badlogic.gdx.ai.btree.LeafTask;
import com.badlogic.gdx.ai.btree.Task;
import com.rebelo.messgame.entities.HumanAgent;

/**
 * Created by sondu on 7/4/2016.
 */
public class LookForFoodTask extends LeafTask<HumanAgent> {

    @Override
    public void start () {
        HumanAgent human = getObject();
    }

    @Override
    public Status execute() {
        return null;
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
