package com.rebelo.messgame.entities;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.rebelo.messgame.MessGame;
import com.rebelo.messgame.ai.steering.Box2dSteeringEntity;
import com.rebelo.messgame.ai.steering.Box2dSteeringUtils;
import com.rebelo.messgame.controllers.IAgentController;
import com.rebelo.messgame.entities.states.HumanState;
import com.rebelo.messgame.map.MessMap;
import com.rebelo.messgame.models.Agent;
import com.rebelo.messgame.objects.projectiles.equippables.Flashlight;
import com.rebelo.messgame.objects.projectiles.equippables.IEquippable;
import com.rebelo.messgame.objects.projectiles.equippables.Snowballs;
import eos.good.Good;

/**
 * Base agent class
 * Created by sondu on 6/12/2016.
 */
public class HumanAgent extends Box2dSteeringEntity implements IAgent{

    // TODO: Control sprite from here
    // TODO: Social Graph (Connections and strength in relationships)
    // TODO: Disable steering behaviour while in vehicle
    // TODO: Hide agent while in vehicle
    // !TODO: Change concept of controller, and useHand existing steering behavior system, that way we can stack behaviours :)
    // TODO: We need a generic component system that affects behaviour, and item / inventory system

    public enum Hand {
        LEFT,
        RIGHT
    }

    final static float MAX_ACCELERATION = 10f;
    final static float MAX_TURBO_ACCELERATION = 30f;
    final static float MAX_SPEED = 3f;
    final static float MAX_TURBO_SPEED = 5f;

    ObjectMap<Hand, IEquippable> _itemByHand = new ObjectMap<Hand, IEquippable>();

    StateMachine<HumanAgent, HumanState> _stateMachine = new DefaultStateMachine<HumanAgent, HumanState>(this, HumanState.PHYSIOLIGICAL);

    public IAgentController getController() {
        return _controller;
    }

    IAgentController _controller;

    private Agent _agent;
    private MessMap _map;
    private Array<IEquippable> _carriables = new Array<IEquippable>();

    private Wander<Vector2> _wander;
    private boolean _isTurboEnabled;

    public HumanAgent(Sprite sprite, Body body, boolean independentFacing, int radiusInPixels, MessMap map) {
        super(sprite, body, independentFacing, radiusInPixels / MessGame.PIXELS_PER_METER);
        _map = map;

        // Equip snowball throwing by default!
        IEquippable defaultCarriable = new Snowballs(map.getAtlas().createSprite("light_on"), map);
        _carriables.add(defaultCarriable);
        _itemByHand.put(Hand.RIGHT, defaultCarriable);

        IEquippable flashLight = new Flashlight(body, map);
        _carriables.add(flashLight);
        _itemByHand.put(Hand.LEFT, flashLight);

        this.setMaxLinearAcceleration(MAX_ACCELERATION);
        this.setMaxLinearSpeed(MAX_SPEED);
        this.setMaxAngularAcceleration(5f); // greater than 0 because independent facing is enabled
        this.setMaxAngularSpeed(8f);

        _wander = new Wander<Vector2>(this) //
        .setFaceEnabled(true) // We want to useHand Face internally (independent facing is on)
        .setAlignTolerance(0.001f) // Used by Face
        .setDecelerationRadius(1) // Used by Face
        .setTimeToTarget(0.1f) // Used by Face
        .setWanderOffset(3) //
        .setWanderOrientation(3) //
        .setWanderRadius(1) //
        .setWanderRate(MathUtils.PI2 * 4);
    }

    public void setModel(Agent agent) {
        _agent = agent;
    }

    public Agent getModel() {
        return _agent;
    }

    public void setController(IAgentController controller) {
        if (_controller != null) {
            resetSteeringBehvaiour();
            _controller.destroy();
        }

        _controller = controller;
        _controller.setOwner(this);
    }

    private Vector2 _targetLocation;
    public void showTarget(Vector2 position) {
        _targetLocation = position;
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);

        if (_targetLocation != null && !_targetLocation.isZero()) {
            float w = sprite.getRegionWidth();
            float h = sprite.getRegionHeight();
            float ox = w / 2f;
            float oy = h / 2f;

            Vector2 mapPos = new Vector2(_targetLocation.x * MessGame.PIXELS_PER_METER - ox, _targetLocation.y * MessGame.PIXELS_PER_METER - oy);
            float rotation = body.getAngle() * MathUtils.radiansToDegrees;

            sprite.setPosition(mapPos.x, mapPos.y);
            sprite.setRotation(rotation);
            sprite.draw(batch);
        }
    }

    private void resetSteeringBehvaiour() {
        this.setSteeringBehavior(null);
    }

    @Override
    public void setSteeringBehavior(SteeringBehavior<Vector2> behavior) {
        if (this.steeringBehavior != null) {
            this.steeringBehavior.setOwner(null);
            this.steeringBehavior.setEnabled(false);
        }

        if (behavior != null) {
            behavior.setEnabled(true);
            behavior.setOwner(this);
        }

        super.setSteeringBehavior(behavior);
    }

    public void wander() {
        this.setSteeringBehavior(_wander);
    }

    @Override
    public void update(float deltaTime) {
        if (_controller != null) {
            _controller.update(deltaTime);
        }
        // TODO: Check closest resource (within focus / reach)

        super.update(deltaTime);
    }

    public void turboEnabled(boolean isTurbo) {
        if (isTurbo && !_isTurboEnabled) {
            // TODO: Apply impulse if stamina full
            this.setMaxLinearSpeed(MAX_TURBO_SPEED);
            this.setMaxLinearAcceleration(MAX_TURBO_ACCELERATION);
        } else if (!isTurbo) {
            // TODO: Gradually set this if we are higher
            this.setMaxLinearSpeed(MAX_SPEED);
            this.setMaxLinearAcceleration(MAX_ACCELERATION);
        }

        _isTurboEnabled = isTurbo;
    }

    public void previousWeapon () {

    }

    public void nextWeapon () {

    }

    // Pick up what?
    public void pickUp () {
        // If 2 items equipped, or 1 item that requires 2 hands, put items away
        IEquippable leftHandItem = _itemByHand.get(Hand.LEFT);
        IEquippable rightHandItem = _itemByHand.get(Hand.RIGHT);

        if (leftHandItem != null && rightHandItem != null) {
            leftHandItem.deactivate();
            rightHandItem.deactivate();
        } else if (rightHandItem != null && rightHandItem.getNumHandsRequired() == 2) {
            rightHandItem.deactivate();
        }

        // TODO: Gather closest item / resource

        // TODO: When complete, reactivate deactivated items
    }

    public void build () {
        // TODO: Do we have an item equipped that helps us build?
        // If we do, let's useHand the item
        // If not, put them away and useHand our hands!
    }

    public void useHand(Hand hand, float force, float delta) {
        //while ( totalRotation < -180 * MathUtils.degreesToRadians ) totalRotation += 360 * MathUtils.degreesToRadians;
        //while ( totalRotation >  180 * MathUtils.degreesToRadians ) totalRotation -= 360 * MathUtils.degreesToRadians;

        IEquippable item = _itemByHand.get(hand);
        if (item != null) {
            // TODO: Get point from weapon
            Vector2 pos = body.getWorldCenter();
            float totalRotation = body.getAngle();
            Vector2 direction = new Vector2();
            Box2dSteeringUtils.angleToVector(direction, totalRotation);
            if (direction.len() > 0) {
                direction.nor();
            }
            float offset = 15;
            float xPos = pos.x + (direction.x * offset / MessGame.PIXELS_PER_METER);
            float yPos = pos.y + (direction.y * offset / MessGame.PIXELS_PER_METER);

            item.activate(xPos, yPos, direction, force, delta);
        }
    }

    public IEquippable getEquippedItemForHand(Hand hand) {
        return _itemByHand.get(hand);
    }

    public void stopUsingHand(Hand hand) {
        IEquippable item = _itemByHand.get(hand);
        if (item != null) {
            item.deactivate();
        }
    }

    // TODO: Decrease fatigue
    public void rest() {

    }

    // TODO: Follow another agent
    public void follow(HumanAgent agent) {

    }

    // TODO: Attempt to stay out of sight
    public void hide() {

    }

    @Override
    public void consumeGood(Good good) {

    }
}
