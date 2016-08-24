package com.rebelo.messgame.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.behaviors.BlendedSteering;
import com.badlogic.gdx.ai.steer.behaviors.LookWhereYouAreGoing;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.rebelo.messgame.ai.steering.Box2dSteeringEntity;
import com.rebelo.messgame.ai.steering.Box2dSteeringUtils;
import com.rebelo.messgame.entities.HumanAgent;
import com.rebelo.messgame.utils.FacePoint;
import com.rebelo.messgame.utils.GamePadSteering;

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

    private BlendedSteering<Vector2> _blendedSteering;
    private GamePadSteering<Vector2> _gamePadSteering;
    private LookWhereYouAreGoing<Vector2> _lookWhereYouAreGoing;
    private FacePoint<Vector2> _facePoint;

    HumanAgent _humanAgent;
    Controller _agentController = null;
    Vector2 _rotationTarget = new Vector2(0f, 0f);
    Vector2 _gamepadRotation = new Vector2(0f, 0f);

    GamePad _gamePad = GamePad.NONE;

    float _firePercent;

    final static float ROTATE_EPSILON = 0.075f;
    final static float INPUT_FORCE = 1f;

    public GamepadAgentController(HumanAgent humanAgent) {
        _humanAgent = humanAgent;

        _gamePadSteering = new GamePadSteering<Vector2>(_humanAgent);

        /*
        _lookWhereYouAreGoing = new LookWhereYouAreGoing<Vector2>(_humanAgent)
        .setAlignTolerance(0.0001f) // Used by Face
        .setDecelerationRadius(1f) // Used by Face
        .setTimeToTarget(0.1f); // Used by Face

        _facePoint = new FacePoint<Vector2>(_humanAgent)
        .setAlignTolerance(0.00001f) // Used by Face
        .setDecelerationRadius(1f) // Used by Face
        .setTimeToTarget(0.1f); // Used by Face

        _facePoint.setTargetVector(_rotationTarget);

        _blendedSteering = new BlendedSteering<Vector2>(_humanAgent);
        _blendedSteering.add(_gamePadSteering, 1f);
        _blendedSteering.add(_lookWhereYouAreGoing, 1f);
        _blendedSteering.add(_facePoint, 1f);
        */
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

        //_rotationLocation.setPosition(0f, 0f);
        _gamePad = GamePad.NONE;
    }

    @Override
    public void setOwner(Box2dSteeringEntity agent) {
        agent.setSteeringBehavior(_gamePadSteering);
    }

    @Override
    public void update(float delta) {
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

            float xVelocity = processAxis(axisXValue, turboButton);
            float yVelocity = processAxis(axisYValue * -1, turboButton);

            _gamePadSteering.setVelocity(xVelocity, yVelocity);
            _gamepadRotation.set(rotateXValue, rotateYValue * -1);

            Vector2 pos = _humanAgent.getPosition();
            if (Math.abs(_gamepadRotation.len()) > ROTATE_EPSILON) {
                //_gamepadRotation.scl(2f);
                _rotationTarget.set(pos.x + _gamepadRotation.x, pos.y + _gamepadRotation.y);
                _humanAgent.showTarget(_rotationTarget);
                _humanAgent.getBody().setTransform(pos.x, pos.y, Box2dSteeringUtils.vectorToAngle(_gamepadRotation));
                /*
                _blendedSteering.get(1).setWeight(0f);
                _blendedSteering.get(2).setWeight(1f);
                _lookWhereYouAreGoing.setEnabled(false);
                _facePoint.setEnabled(true);
                */
            } else {
                _humanAgent.showTarget(Vector2.Zero);
                Vector2 vel = _humanAgent.getLinearVelocity();
                _humanAgent.getBody().setTransform(pos.x, pos.y, Box2dSteeringUtils.vectorToAngle(vel));
                /*
                _blendedSteering.get(1).setWeight(1f);
                _blendedSteering.get(2).setWeight(0f);
                _lookWhereYouAreGoing.setEnabled(true);
                _facePoint.setEnabled(false);
                */
            }
        }
    }

    @Override
    public void destroy() {
        removeGamepad();
    }

    float processAxis(float axisValue, float turboButton) {
        float velocity = 0f;

        if (Math.abs(axisValue) > 0.01) {
            velocity = (axisValue * INPUT_FORCE);

            if (turboButton > 0.1f) {
                _humanAgent.turboEnabled(true);
            } else {
                _humanAgent.turboEnabled(false);
            }
        }
        /*
        if (velocity > MAX_VELOCITY) {
            velocity = MAX_VELOCITY;
        } else if (velocity < -MAX_VELOCITY) {
            velocity = -MAX_VELOCITY;
        }
        */
        return velocity;
    }
}
