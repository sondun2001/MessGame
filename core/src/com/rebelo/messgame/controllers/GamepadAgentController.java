package com.rebelo.messgame.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.MathUtils;
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
        NONE,
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
        //Gdx.app.log("GamepadAgentController", axisCode + " " + value);
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
    Vector2 _currentVelocity;
    //Vector2 _currentRotation = new Vector2(0f, 0f);
    Vector2 _rotationTarget = new Vector2(0f, 0f);
    GamePad _gamePad = GamePad.NONE;
    float _firePercent;

    final static float INPUT_FORCE = 2f;
    final static float TURBO_FORCE = 2f;
    final static float MAX_VELOCITY = 10f;
    final static float DAMPENING = 0.8f;
    final static float ROTATION_SPEED = 3f;

    public GamepadAgentController(HumanAgent humanAgent) {
        _humanAgent = humanAgent;
    }

    // Assign controller to agent
    public void setGamepad(Controller controller) {
        if (_agentController != null) {
            _agentController.removeListener(this);
        }

        _agentController = controller;

        if (_agentController.getName().toLowerCase().contains("xbox") && _agentController.getName().contains("360")){
            _gamePad = GamePad.XBOX_360;
        } else if (_agentController.getName().toLowerCase().contains("xbox") && _agentController.getName().toLowerCase().contains("one")){
            _gamePad = GamePad.XBOX_ONE;
        }

        controller.addListener(this);
    }

    public void removeGamepad() {
        if (_agentController != null) {
            _agentController.removeListener(this);
        }

        _rotationTarget.set(0f, 0f);
        _gamePad = GamePad.NONE;
    }

    @Override
    public void update(float delta) {
        Body body = _humanAgent.getBody();

        _currentVelocity = _humanAgent.getBody().getLinearVelocity();

        if (_agentController != null) {
            float axisXValue = 0f;
            float axisYValue = 0f;
            float rotateXValue = 0f;
            float rotateYValue = 0f;

            float turboButton = 0f;

            if (_gamePad == GamePad.XBOX_360 || _gamePad == GamePad.XBOX_ONE){
                axisXValue = _agentController.getAxis(XBox360Pad.AXIS_LEFT_X);
                axisYValue = _agentController.getAxis(XBox360Pad.AXIS_LEFT_Y);
                rotateXValue = _agentController.getAxis(XBox360Pad.AXIS_RIGHT_X);
                rotateYValue = _agentController.getAxis(XBox360Pad.AXIS_RIGHT_Y);
                turboButton = _agentController.getAxis(XBox360Pad.AXIS_RIGHT_TRIGGER);

                if (_agentController.getButton(XBox360Pad.BUTTON_RB)) {
                    _firePercent += delta;
                } else if (_firePercent > 0) {
                    _humanAgent.use(HumanAgent.Hand.RIGHT, MathUtils.clamp(_firePercent, .2f, 1f));
                    _firePercent = 0;
                }
            }

            float xVelocity = processAxis(axisXValue, _currentVelocity.x, turboButton);
            float yVelocity = processAxis(axisYValue * -1, _currentVelocity.y, turboButton);

            body.setLinearVelocity(xVelocity, yVelocity);

            // Rotation values
            _rotationTarget.set(rotateXValue, rotateYValue * -1);
            //_currentRotation.lerp(_rotationTarget, delta * ROTATION_SPEED);
            float rotation = MathUtils.atan2(_rotationTarget.y, _rotationTarget.x);
            Vector2 pos = body.getWorldCenter();
            body.setTransform(pos, rotation);
        }
    }

    @Override
    public void destroy() {
        removeGamepad();
    }

    float processAxis(float axisValue, float currentVelocity, float turboButton) {
        float velocity = 0f;

        if (Math.abs(axisValue) > 0.02) {
            velocity = (axisValue * INPUT_FORCE);

            if (turboButton > 0f) {
                velocity *= ((TURBO_FORCE * turboButton) + 1);
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
