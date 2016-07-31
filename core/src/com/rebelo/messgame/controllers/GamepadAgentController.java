package com.rebelo.messgame.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.rebelo.messgame.entities.HumanAgent;

/**
 * Created by sondu on 7/17/2016.
 * TODO: Abstract out controls to support various gamepads
 * TODO: Support multiple agents and controllers
 */
public class GamepadAgentController implements IAgentController {
    HumanAgent _humanAgent;
    ControllerAdapter _controlListener;
    Controller _agentController = null;
    Vector2 currentVelocity;

    final static float INPUT_FORCE = 2f;

    public GamepadAgentController(HumanAgent humanAgent) {
        _humanAgent = humanAgent;

        _controlListener = new ControllerAdapter() {

            public boolean buttonDown (Controller controller, int buttonCode){
                Gdx.app.log("GamepadAgentController", Integer.toString(buttonCode));
                return false;
            }

            public boolean axisMoved (Controller controller, int axisCode, float value){
                Gdx.app.log("GamepadAgentController", axisCode + " " + value);
                return false;
            }
        };

        for (Controller controller : Controllers.getControllers()) {
            Gdx.app.log("GamepadAgentController", controller.getName());
            if (_agentController == null) {
                _agentController = controller;
                controller.addListener(_controlListener);
                break;
            }
        }

        Controllers.addListener(new ControllerAdapter() {
            @Override
            public void connected(Controller controller) {
                Gdx.app.log("GamepadAgentController", "Connected");
                if (_agentController == null) {
                    _agentController = controller;
                    controller.addListener(_controlListener);
                }
            }

            public void disconnected(Controller controller) {
                Gdx.app.log("GamepadAgentController", "Disconnected");
                if (_agentController == controller) {
                    controller.removeListener(_controlListener);
                }
            }
        }); // receives events from all controllers
    }

    @Override
    public void update(float delta) {
        Body body = _humanAgent.getBody();
        //Vector2 pos = body.getWorldCenter();

        currentVelocity = _humanAgent.getBody().getLinearVelocity();

        if (_agentController != null) {
            float axisXValue = _agentController.getAxis(XBox360Pad.AXIS_LEFT_X);
            float axisYValue = _agentController.getAxis(XBox360Pad.AXIS_LEFT_Y);
            float turboButton = _agentController.getAxis(XBox360Pad.AXIS_RIGHT_TRIGGER);

            float xVelocity = axisXValue * INPUT_FORCE;
            float yVelocity = (axisYValue * INPUT_FORCE) * -1;

            if (turboButton > 0f) {
                xVelocity *= (2f * turboButton);
                yVelocity *= (2f * turboButton);
            }

            body.setLinearVelocity(xVelocity, yVelocity);
        }
    }
}
