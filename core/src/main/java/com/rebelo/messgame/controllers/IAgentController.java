package com.rebelo.messgame.controllers;

import com.rebelo.messgame.ai.steering.Box2dSteeringEntity;

/**
 *
 * Created by sondu on 7/3/2016.
 */
public interface IAgentController {
    void setOwner(Box2dSteeringEntity agent);
    void update(float delta);
    void destroy();
}
