package com.rebelo.messgame.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.rebelo.messgame.controllers.*;
import com.rebelo.messgame.entities.HumanAgent;
import com.rebelo.messgame.map.MessMap;
import de.golfgl.gdx.controllers.mapping.ControllerMappings;
import de.golfgl.gdx.controllers.mapping.MappedControllerAdapter;

/**
 * Created by sondu on 8/14/2016.
 */

// TODO: Listen for button press on controllers to determine if a slot is available
// TODO: Assign controller on button press to open slot.
// TODO: Pool AIAgentController and GamepadAgentController
// TODO: Need dependency system, this would depend on settings.
public class ControllerManager extends MappedControllerAdapter implements ControllerListener {

    private static ControllerManager _instance;

    private static final int MAX_LOCAL_SLOTS = 4;

    IntMap<IAgentController> _controllerBySlot = new IntMap<IAgentController>();
    ObjectMap<Controller, HumanAgent> _agentByController = new ObjectMap<Controller, HumanAgent>();
    Array<IGamePadConsumer> subscribers = new Array<IGamePadConsumer>();

    int _numControllers = 0;

    public ControllerManager(ControllerMappings controllerMappings) {
        super(controllerMappings);

        Controllers.addListener(this);

        for (Controller controller : Controllers.getControllers()) {
            Gdx.app.log("GamepadAgentController", controller.getName());
            // TODO: Find available controllers
        }
    }

    // static method to create instance of Singleton class
    public static ControllerManager getInstance()
    {
        if (_instance == null) {
            ControllerMappings controllerMappings = new ControllerMappings();
            _instance = new ControllerManager(controllerMappings);
        }

        return _instance;
    }

    @Override
    public void connected(Controller controller) {

    }

    @Override
    public void disconnected(Controller controller) {
        HumanAgent agent =  _agentByController.get(controller);
        if (agent != null) {
            agent.setController(new AIAgentController(agent));
            _agentByController.put(controller, null);
        }
    }

    public void registerSubscriber(IGamePadConsumer gamePadConsumer) {
        subscribers.add(gamePadConsumer);
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        // todo: Check settings for override. Fire off button event.

        if (buttonCode == XBox360Pad.BUTTON_A) {
            EventBus.getInstance().post(new ButtonDownEvent(ButtonDownEvent.GameButton.JOIN, controller));
        }

        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
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
}
