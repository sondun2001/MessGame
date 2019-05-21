package com.rebelo.messgame.controllers;

import com.badlogic.gdx.controllers.Controller;

import java.awt.*;

public class ButtonDownEvent {

    public enum GameButton {
        START,
        JOIN,
        JUMP,
        BLOCK,
        PICK_UP,
        TURBO,
        USE_LEFT,
        USE_RIGHT,
        TOGGLE_LEFT_PREVIOUS,
        TOGGLE_LEFT_NEXT,
        TOGGLE_RIGHT_PREVIOUS,
        TOGGLE_RIGHT_NEXT,
    }

    public GameButton button;
    public Controller controller;

    public ButtonDownEvent(GameButton button, Controller controller) {
        this.controller = controller;
        this.button = button;
    }
}
