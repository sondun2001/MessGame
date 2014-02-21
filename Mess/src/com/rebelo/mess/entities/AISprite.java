package com.rebelo.mess.entities;

/**
 * Created by sondun2001 on 1/3/14.
 */
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class AISprite extends Sprite {

    private Vector2 velocity = new Vector2();
    private float speed = 900, tolerance = 3;

    private Array<Vector2> path;
    private int waypoint = 0;

    public AISprite(Sprite sprite, Array<Vector2> path) {
        super(sprite);
        this.path = path;
    }

    public void draw(SpriteBatch spriteBatch) {
        update(Gdx.graphics.getDeltaTime());
        super.draw(spriteBatch);
    }

    public void update(float delta) {
        float angle = (float) Math.atan2(path.get(waypoint).y - getY(), path.get(waypoint).x - getX());
        velocity.set((float) Math.cos(angle) * speed, (float) Math.sin(angle) * speed);

        setPosition(getX() + velocity.x * delta, getY() + velocity.y * delta);
        setRotation(angle * MathUtils.radiansToDegrees);

        if(isWaypointReached()) {
            setPosition(path.get(waypoint).x, path.get(waypoint).y);
            if(waypoint + 1 >= path.size)
                waypoint = 0;
            else
                waypoint++;
        }
    }

    public boolean isWaypointReached() {
        return path.get(waypoint).x - getX() <= speed / tolerance * Gdx.graphics.getDeltaTime() && path.get(waypoint).y - getY() <= speed / tolerance * Gdx.graphics.getDeltaTime();
    }

    public Array<Vector2> getPath() {
        return path;
    }

    public int getWaypoint() {
        return waypoint;
    }

}