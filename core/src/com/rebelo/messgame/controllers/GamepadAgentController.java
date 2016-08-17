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

    GamePad _gamePad = GamePad.NONE;

    float _firePercent;

    final static float ROTATE_EPSILON = 0.06f;
    final static float INPUT_FORCE = 2f;
    final static float TURBO_FORCE = 2f;
    final static float MAX_VELOCITY = 10f;
    final static float DAMPENING = 0.8f;
    final static float ROTATION_SPEED = 3f;

    public GamepadAgentController(HumanAgent humanAgent) {
        _humanAgent = humanAgent;

        _gamePadSteering = new GamePadSteering<Vector2>(_humanAgent);

        _lookWhereYouAreGoing = new LookWhereYouAreGoing<Vector2>(_humanAgent)
        .setAlignTolerance(0.001f) // Used by Face
        .setDecelerationRadius(1) // Used by Face
        .setTimeToTarget(0.1f); // Used by Face

        _facePoint = new FacePoint<Vector2>(_humanAgent)
        .setAlignTolerance(0.001f) // Used by Face
        .setDecelerationRadius(1) // Used by Face
        .setTimeToTarget(0.1f); // Used by Face

        _facePoint.setTargetVector(_rotationTarget);

        _blendedSteering = new BlendedSteering<Vector2>(_humanAgent);
        _blendedSteering.add(_gamePadSteering, 1f);
        _blendedSteering.add(_lookWhereYouAreGoing, 1f);
        _blendedSteering.add(_facePoint, 1f);
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

    /*
    @Override
    protected SteeringAcceleration<T> calculateRealSteering(SteeringAcceleration<T> steering) {


        float delta = GdxAI.getTimepiece().getDeltaTime();

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
            _targetPosition.set(xVelocity, yVelocity);

            steering.linear.set((T) _targetPosition);

            // Rotation values
            if (Math.abs(rotateXValue) > ROTATE_EPSILON || Math.abs(rotateYValue) > ROTATE_EPSILON) {
                //_rotationLocation.setPosition(_humanAgent.getPosition().x + rotateXValue * 2, _humanAgent.getPosition().y + (rotateYValue * -1) * 2);
                _rotationTarget.set(_humanAgent.getPosition().x + rotateXValue, _humanAgent.getPosition().y + (rotateYValue * -1));
                _facePoint.setEnabled(true);
                _lookWhereYouAreGoing.setEnabled(false);
                _facePoint.facePoint((SteeringAcceleration<Vector2>) steering, _rotationTarget);
                //steering.angular = Box2dSteeringUtils.vectorToAngle(_rotationTarget);
                //_humanAgent.face(_rotationTarget);
            } else {
                // No angular acceleration
                steering.angular = 0;
                _facePoint.setEnabled(false);
                _lookWhereYouAreGoing.setEnabled(true);
                //_humanAgent.lookWhereYouAreGoing();
            }
        }
        return steering;
    }
    */

    @Override
    public void setOwner(Box2dSteeringEntity agent) {
        agent.setSteeringBehavior(_blendedSteering);
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

            if (Math.abs(rotateXValue) > ROTATE_EPSILON || Math.abs(rotateYValue) > ROTATE_EPSILON) {
                _rotationTarget.set(_humanAgent.getPosition().x + rotateXValue, _humanAgent.getPosition().y + (rotateYValue * -1));
                _facePoint.setEnabled(true);
                _lookWhereYouAreGoing.setEnabled(false);
            } else {
                _facePoint.setEnabled(false);
                _lookWhereYouAreGoing.setEnabled(true);
            }

            // Rotation values
            /*
            _rotationTarget.set(rotateXValue, rotateYValue * -1);
            //_currentRotation.lerp(_rotationTarget, delta * ROTATION_SPEED);
            float rotation = MathUtils.atan2(_rotationTarget.y, _rotationTarget.x);
            Vector2 pos = body.getWorldCenter();
            body.setTransform(pos, rotation);
            */
        }
    }

    @Override
    public void destroy() {
        removeGamepad();
    }

    float processAxis(float axisValue, float turboButton) {
        float velocity = 0f;

        if (Math.abs(axisValue) > 0.02) {
            velocity = (axisValue * INPUT_FORCE);

            if (turboButton > 0f) {
                velocity *= ((TURBO_FORCE * turboButton) + 1);
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
