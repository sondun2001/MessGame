package com.rebelo.messgame.models;

/**
 * Data model for the agent (used to serialize to JSON for storage
 * TODO: JSON deserializer / serializer (Jackson?)
 * Created by sondu on 6/13/2016.
 */
public class Agent {
    public float hunger = 0;
    public float thirst = 0;
    public float fatigue = 0;
    public float happiness = 0;

    // TODO: Array of agents that matter, and relationship state
}
