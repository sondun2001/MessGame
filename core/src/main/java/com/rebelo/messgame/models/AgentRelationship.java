package com.rebelo.messgame.models;

import com.rebelo.messgame.entities.IAgent;

/**
 * Created by sondu on 6/19/2016.
 */

public class AgentRelationship {

    public enum RelationshipState {
        NONE,
        FAMILIAR,
        AQUAINTANCE,
        FRIEND,
        BEST_FRIEND
    }

    IAgent from;
    IAgent to;
    RelationshipState relationshipState;
    float relationshipStrength;
}
