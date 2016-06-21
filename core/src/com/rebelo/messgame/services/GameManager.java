package com.rebelo.messgame.services;

/**
 * TODO: Load map, agents, and game objects
 * TODO: Manage game state / Input processing?
 * Created by sondu on 6/14/2016.
 */
public class GameManager {
    private static GameManager ourInstance = new GameManager();

    public static GameManager getInstance() {
        return ourInstance;
    }

    private GameManager() {

    }
}
