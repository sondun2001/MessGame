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
        float axisXValue = 0f;
        float axisYValue = 0f;
        float rotateXValue = 0f;
        float rotateYValue = 0f;

        // Get network values
        /*
        float xVelocity = processAxis(axisXValue, _currentVelocity.x, turboButton);
        float yVelocity = processAxis(axisYValue * -1, _currentVelocity.y, turboButton);

        body.setLinearVelocity(xVelocity, yVelocity);

        // Rotation values
        _rotationTarget.set(rotateXValue, rotateYValue * -1);
        _currentRotation.lerp(_rotationTarget, delta * ROTATION_SPEED);
        float rotation = MathUtils.atan2(_currentRotation.y, _currentRotation.x);
        Vector2 pos = body.getWorldCenter();
        body.setTransform(pos, rotation);
        */
    }

    @Override
    public void destroy() {

    }
}
