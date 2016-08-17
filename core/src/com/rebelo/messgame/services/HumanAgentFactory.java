package com.rebelo.messgame.services;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.rebelo.messgame.MessGame;
import com.rebelo.messgame.controllers.AIAgentController;
import com.rebelo.messgame.entities.HumanAgent;
import com.rebelo.messgame.map.MessMap;
import com.rebelo.messgame.models.Agent;

/**
 * Created by sondu on 6/13/2016.
 */
public class HumanAgentFactory {
    private static HumanAgentFactory ourInstance = new HumanAgentFactory();

    public static HumanAgentFactory getInstance() {
        return ourInstance;
    }

    private HumanAgentFactory() {
        // TODO: Init Factory
    }

    // TODO: Get sprite from map for this agent
    public HumanAgent createAgent(Sprite sprite, Agent agent, MessMap map, float posX, float posY) {
        // TODO: Get sprite animation for this agent
        // TODO: Create physics body
        CircleShape circleShape = new CircleShape();
        circleShape.setPosition(new Vector2());
        int radiusInPixels = (int)((sprite.getRegionWidth() + sprite.getRegionHeight()) / 4f);
        circleShape.setRadius(radiusInPixels / MessGame.PIXELS_PER_METER);

        BodyDef characterBodyDef = new BodyDef();
        characterBodyDef.position.set((posX / MessGame.PIXELS_PER_METER), (posY / MessGame.PIXELS_PER_METER));
        characterBodyDef.type = BodyDef.BodyType.DynamicBody;
        Body characterBody = map.world.createBody(characterBodyDef);

        FixtureDef charFixtureDef = new FixtureDef();
        charFixtureDef.density = 1;
        charFixtureDef.shape = circleShape;
        charFixtureDef.filter.groupIndex = 0;
        characterBody.createFixture(charFixtureDef);

        circleShape.dispose();

        HumanAgent humanAgent = new HumanAgent(sprite, characterBody, true, radiusInPixels, map);
        characterBody.setUserData(humanAgent);
        humanAgent.setModel(agent);

        return humanAgent;
    }
}
