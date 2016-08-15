package com.rebelo.messgame.entities;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.rebelo.messgame.MessGame;
import com.rebelo.messgame.ai.steering.Box2dSteeringEntity;
import com.rebelo.messgame.controllers.IAgentController;
import com.rebelo.messgame.entities.states.HumanState;
import com.rebelo.messgame.map.MessMap;
import com.rebelo.messgame.models.Agent;
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

    StateMachine<HumanAgent, HumanState> _stateMachine = new DefaultStateMachine<HumanAgent, HumanState>(this, HumanState.PHYSIOLIGICAL);

    public IAgentController getController() {
        return _controller;
    }

    IAgentController _controller;

    private Agent _agent;
    private MessMap _map;
    private Sprite _currentProjectileSprite;

    public HumanAgent(Sprite sprite, Body body, boolean independentFacing, int radiusInPixels, MessMap map) {
        super(sprite, body, independentFacing, radiusInPixels / MessGame.PIXELS_PER_METER);
        _map = map;
        _currentProjectileSprite = map.getAtlas().createSprite("light_on");
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

    }

    public void build () {

    }

    public void fire (float forcePercent) {
        // Get sprite from current selected weapon
        float weaponForce = .7f * forcePercent;

        // TODO: Get point from weapon
        Vector2 pos = body.getWorldCenter();
        float totalRotation = body.getAngle();

        Vector2 direction = new Vector2();
        direction.x = MathUtils.cos(totalRotation);
        direction.y = MathUtils.sin(totalRotation);
        if (direction.len() > 0) {
            direction.nor();
        }
        //while ( totalRotation < -180 * MathUtils.degreesToRadians ) totalRotation += 360 * MathUtils.degreesToRadians;
        //while ( totalRotation >  180 * MathUtils.degreesToRadians ) totalRotation -= 360 * MathUtils.degreesToRadians;
        //float xImpulse = MathUtils.sin(totalRotation);
        //float yImpulse = MathUtils.cos(totalRotation);
        float offset = 15;
        float xPos = pos.x + (direction.x * offset / MessGame.PIXELS_PER_METER);
        float yPos = pos.y + (direction.y * offset / MessGame.PIXELS_PER_METER);

        ProjectileFactory.getInstance().createProjectile(_currentProjectileSprite, _map, xPos, yPos, direction.x, direction.y, forcePercent);
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
