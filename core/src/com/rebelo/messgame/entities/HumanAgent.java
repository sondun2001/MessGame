package com.rebelo.messgame.entities;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.ObjectMap;
import com.rebelo.messgame.MessGame;
import com.rebelo.messgame.ai.steering.Box2dSteeringEntity;
import com.rebelo.messgame.controllers.IAgentController;
import com.rebelo.messgame.entities.states.HumanState;
import com.rebelo.messgame.map.MessMap;
import com.rebelo.messgame.models.Agent;
import com.rebelo.messgame.objects.projectiles.equippables.IEquippable;
import com.rebelo.messgame.objects.projectiles.equippables.Snowballs;
import com.rebelo.messgame.services.ProjectileFactory;
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

    public enum Hand {
        LEFT,
        RIGHT
    }

    ObjectMap<Hand, IEquippable> _itemByHand = new ObjectMap<Hand, IEquippable>();

    StateMachine<HumanAgent, HumanState> _stateMachine = new DefaultStateMachine<HumanAgent, HumanState>(this, HumanState.PHYSIOLIGICAL);

    public IAgentController getController() {
        return _controller;
    }

    IAgentController _controller;

    private Agent _agent;
    private MessMap _map;

    public HumanAgent(Sprite sprite, Body body, boolean independentFacing, int radiusInPixels, MessMap map) {
        super(sprite, body, independentFacing, radiusInPixels / MessGame.PIXELS_PER_METER);
        _map = map;

        // Equip snowball throwing by default!
        _itemByHand.put(Hand.RIGHT, new Snowballs(map.getAtlas().createSprite("light_on"), map));
    }

    public void setModel(Agent agent) {
        _agent = agent;
    }

    public Agent getModel() {
        return _agent;
    }

    public void setController(IAgentController controller) {
        if (_controller != null) {
            resetBehvaiour();
            _controller.destroy();
        }

        _controller = controller;
    }

    private void resetBehvaiour() {
        if (this.steeringBehavior != null) {
            this.steeringBehavior.setEnabled(false);
        }

        this.setSteeringBehavior(null);
    }

    public void wander() {
        this.setMaxLinearAcceleration(10);
        this.setMaxLinearSpeed(3);
        this.setMaxAngularAcceleration(.5f); // greater than 0 because independent facing is enabled
        this.setMaxAngularSpeed(5);

        Wander wander = new Wander<Vector2>(this) //
        .setFaceEnabled(true) // We want to use Face internally (independent facing is on)
        .setAlignTolerance(0.001f) // Used by Face
        .setDecelerationRadius(1) // Used by Face
        .setTimeToTarget(0.1f) // Used by Face
        .setWanderOffset(3) //
        .setWanderOrientation(3) //
        .setWanderRadius(1) //
        .setWanderRate(MathUtils.PI2 * 4);

        this.setSteeringBehavior(wander);
    }

    @Override
    public void update(float deltaTime) {
        if (_controller != null) {
            _controller.update(deltaTime);
        }

        super.update(deltaTime);
    }

    public void previousWeapon () {

    }

    public void nextWeapon () {

    }

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
        // If we do, let's use the item
        // If not, put them away and use our hands!
    }

    public void use (Hand hand,  float forcePercent) {
        //while ( totalRotation < -180 * MathUtils.degreesToRadians ) totalRotation += 360 * MathUtils.degreesToRadians;
        //while ( totalRotation >  180 * MathUtils.degreesToRadians ) totalRotation -= 360 * MathUtils.degreesToRadians;

        IEquippable item = _itemByHand.get(hand);
        if (item != null) {
            // TODO: Get point from weapon
            Vector2 pos = body.getWorldCenter();
            float totalRotation = body.getAngle();

            Vector2 direction = new Vector2();
            direction.x = MathUtils.cos(totalRotation);
            direction.y = MathUtils.sin(totalRotation);
            if (direction.len() > 0) {
                direction.nor();
            }
            float offset = 15;
            float xPos = pos.x + (direction.x * offset / MessGame.PIXELS_PER_METER);
            float yPos = pos.y + (direction.y * offset / MessGame.PIXELS_PER_METER);

            item.use(xPos, yPos, direction, forcePercent);
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
