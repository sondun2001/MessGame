package com.rebelo.messgame.services;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.rebelo.messgame.MessGame;
import com.rebelo.messgame.entities.Projectile;
import com.rebelo.messgame.map.MessMap;

import java.util.Iterator;

/**
 * Created by sondu on 8/13/2016.
 */
public class ProjectileFactory {
    private static ProjectileFactory ourInstance = new ProjectileFactory();
    public static ProjectileFactory getInstance() {
        return ourInstance;
    }

    private Array<Projectile> _projectiles = new Array<Projectile>();

    public ProjectileFactory() {
        _projectiles.ordered = false;
    }

    // TODO: Get sprite from map for this agent
    public Projectile createProjectile(Sprite sprite, MessMap map, float posX, float posY, float impulseX, float impulseY, float forcePercent) {
        // TODO: Get projectile from pool
        // TODO: FInd better way of getting sprites
        // TODO: Get sprite animation for this agent

        // TODO: Create physics body
        CircleShape circleShape = new CircleShape();
        circleShape.setPosition(new Vector2());
        int radiusInPixels = (int)((sprite.getRegionWidth() + sprite.getRegionHeight()) / 4f);
        circleShape.setRadius(radiusInPixels / MessGame.PIXELS_PER_METER);

        BodyDef characterBodyDef = new BodyDef();
        //characterBodyDef.position.set((posX / MessGame.PIXELS_PER_METER), (posY / MessGame.PIXELS_PER_METER));
        characterBodyDef.position.set(posX, posY);
        characterBodyDef.type = BodyDef.BodyType.DynamicBody;
        characterBodyDef.bullet = true;
        Body characterBody = map.world.createBody(characterBodyDef);

        FixtureDef charFixtureDef = new FixtureDef();
        charFixtureDef.density = 0.2f;
        charFixtureDef.shape = circleShape;
        charFixtureDef.filter.groupIndex = 0;
        characterBody.createFixture(charFixtureDef);
        circleShape.dispose();

        Projectile projectile = new Projectile(sprite, characterBody, map);
        characterBody.setUserData(projectile);

        _projectiles.add(projectile);


        projectile.fly(impulseX, impulseY, posX, posY, forcePercent);
        return projectile;
    }

    public void update(float delta) {
        // update all projectiles
        Iterator itr = _projectiles.iterator();
        while(itr.hasNext())
        {
            Projectile projectile = (Projectile) itr.next();
            projectile.update(delta);
        }
    }

    public void draw (Batch batch) {
        // Draw all projectiles

        Iterator itr = _projectiles.iterator();
        while(itr.hasNext())
        {
            Projectile projectile = (Projectile) itr.next();
            projectile.draw(batch);
        }
    }
}
