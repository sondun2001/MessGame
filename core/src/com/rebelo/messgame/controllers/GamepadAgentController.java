package com.rebelo.messgame.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.rebelo.messgame.entities.HumanAgent;

/**
 * Created by sondu on 7/17/2016.
 * TODO: Abstract out controls to support various gamepads
 * TODO: Support multiple agents and controllers
 */
public class GamepadAgentController implements IAgentController, ControllerListener {


    enum GamePad {
        XBOX_ONE,
        XBOX_360,
        SHIELD
    }


    @Override
    public void connected(Controller controller) {

    }

    @Override
    public void disconnected(Controller controller) {

    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        Gdx.app.log("GamepadAgentController", Integer.toString(buttonCode));
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        Gdx.app.log("GamepadAgentController", axisCode + " " + value);
        return false;
    }

    @Override
    public boolean povMoved(Controller controller, int povCode, PovDirection value) {
        return false;
    }

    @Override
    public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
        return false;
    }

    @Override
    public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
        return false;
    }

    @Override
    public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
        return false;
    }

    HumanAgent _humanAgent;
    Controller _agentController = null;
    Vector2 currentVelocity;

    GamePad gamePad;
    int controllerNum;

    final static float INPUT_FORCE = 2f;
    final static float TURBO_FORCE = 2f;
    final static float MAX_VELOCITY = 10f;
    final static float DAMPENING = 0.8f;

    public GamepadAgentController(HumanAgent humanAgent) {
        _humanAgent = humanAgent;

        for (Controller controller : Controllers.getControllers()) {
            Gdx.app.log("GamepadAgentController", controller.getName());
            if (_agentController == null) {
                _agentController = controller;

                if (_agentController.getName().toLowerCase().contains("xbox") && _agentController.getName().contains("360")){
                    gamePad = GamePad.XBOX_360;
                } else if (_agentController.getName().toLowerCase().contains("xbox") && _agentController.getName().toLowerCase().contains("one")){
                    gamePad = GamePad.XBOX_ONE;
                }

                controller.addListener(this);
                break;
            }
        }

        Controllers.addListener(new ControllerAdapter() {
            @Override
            public void connected(Controller controller) {
                Gdx.app.log("GamepadAgentController", "Connected");
                if (_agentController == null) {
                    _agentController = controller;
                    controller.addListener(this);
                }
            }

            public void disconnected(Controller controller) {
                Gdx.app.log("GamepadAgentController", "Disconnected");
                if (_agentController == controller) {
                    controller.removeListener(this);
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
            float axisXValue = 0f;
            float axisYValue = 0f;
            float turboButton = 0f;

            if (gamePad == GamePad.XBOX_360 || gamePad == GamePad.XBOX_ONE){
                axisXValue = _agentController.getAxis(XBox360Pad.AXIS_LEFT_X);
                axisYValue = _agentController.getAxis(XBox360Pad.AXIS_LEFT_Y);
                turboButton = _agentController.getAxis(XBox360Pad.AXIS_RIGHT_TRIGGER);
            }

            float xVelocity = processAxis(axisXValue, currentVelocity.x, turboButton);
            float yVelocity = processAxis(axisYValue * -1, currentVelocity.y, turboButton);

            body.setLinearVelocity(xVelocity, yVelocity);
        }
    }

    float processAxis(float axisValue, float currentVelocity, float turboButton) {
        float velocity = 0f;

        if (Math.abs(axisValue) > 0.02) {
            velocity = (axisValue * INPUT_FORCE);

            if (turboButton > 0f) {
                velocity *= (TURBO_FORCE * turboButton);
            }
        }

        if (Math.abs(velocity) < Math.abs(currentVelocity)) {
            velocity = currentVelocity * DAMPENING;
        } else if (velocity > MAX_VELOCITY) {
            velocity = MAX_VELOCITY;
        } else if (velocity < -MAX_VELOCITY) {
            velocity = -MAX_VELOCITY;
        }

        return velocity;
    }
}
