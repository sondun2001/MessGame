package com.rebelo.messgame.controllers;

import com.rebelo.messgame.entities.HumanAgent;

/**
 * Created by sondu on 7/17/2016.
 */
public class NetworkedAgentController implements IAgentController {
    HumanAgent _humanAgent;

    public NetworkedAgentController(HumanAgent humanAgent) {
        _humanAgent = humanAgent;
    }

    @Override
    public void update(float delta) {

    }
}
