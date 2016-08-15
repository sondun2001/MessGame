package com.rebelo.messgame.services;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.rebelo.messgame.controllers.AIAgentController;
import com.rebelo.messgame.controllers.GamepadAgentController;
import com.rebelo.messgame.controllers.IAgentController;
import com.rebelo.messgame.controllers.XBox360Pad;
import com.rebelo.messgame.entities.HumanAgent;
import com.rebelo.messgame.map.MessMap;

/**
 * Created by sondu on 8/14/2016.
 */

// TODO: Listen for button press on controllers to determine if a slot is available
// TODO: Assign controller on button press to open slot.
// TODO: Pool AIAgentController and GamepadAgentController
public class ControllerManager implements ControllerListener {

    private static final int MAX_LOCAL_SLOTS = 4;

    MessMap _map;

    IntMap<IAgentController> _controllerBySlot = new IntMap<IAgentController>();
    ObjectMap<Controller, HumanAgent> _agentByController = new ObjectMap<Controller, HumanAgent>();
    int _numControllers = 0;

    public ControllerManager(MessMap map) {
        _map = map;

        Controllers.addListener(this);

        for (Controller controller : Controllers.getControllers()) {
            Gdx.app.log("GamepadAgentController", controller.getName());
            // TODO: Find available controllers
        }
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

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        if (buttonCode == XBox360Pad.BUTTON_A && _agentByController.get(controller) == null) {
            HumanAgent[] agents = _map.getAgents().items;

            // Find an agent that isn't being controlled
            for (int i = 0; i < agents.length; i++) {
                HumanAgent agent = agents[i];

                if (agent.getController() instanceof AIAgentController) {
                    GamepadAgentController gamepadAgentController = new GamepadAgentController(agent);
                    gamepadAgentController.setGamepad(controller);
                    agent.setController(gamepadAgentController);
                    _agentByController.put(controller, agent);

                    Gdx.app.log("GamepadAgentController", "Assigning controller to agent!");
                    break;
                }
            }
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
