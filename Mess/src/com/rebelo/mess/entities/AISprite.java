package com.rebelo.mess.entities;

/**
 * Created by sondun2001 on 1/3/14.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.rebelo.mess.map.MessMap;
import org.newdawn.slick.util.pathfinding.Mover;
import org.newdawn.slick.util.pathfinding.Path;

// TODO: Pool this class
public class AISprite extends Sprite implements Mover
{
    private Vector2 _velocity = new Vector2();
    private float _speed = 200;

    private Path _path;
    private MessMap _map;
    private int _currentStep = 0;

    public AISprite(Sprite sprite, MessMap map)
    {
        super(sprite);
        _map = map;
    }

    @Override
    public void draw(Batch batch)
    {
        update(Gdx.graphics.getDeltaTime());
        super.draw(batch);
    }

    public void update(float delta)
    {
        if (_path == null) return;

        int stepX = _path.getStep(_currentStep).getX() * _map.tileWidth;
        int stepY = _path.getStep(_currentStep).getY() * _map.tileHeight;
        float angle = (float) Math.atan2(stepY - getY(), stepX - getX());
        _velocity.set((float) Math.cos(angle) * _speed, (float) Math.sin(angle) * _speed);

        setPosition(getX() + _velocity.x * delta, getY() + _velocity.y * delta);
        setRotation(angle * MathUtils.radiansToDegrees);

        if(isWaypointReached(delta))
        {
            setPosition(stepX, stepY);
            if(++_currentStep >= _path.getLength())
            {
                _currentStep = 0;
                setPath(null);
            }
        }
    }

    public boolean isWaypointReached(float delta)
    {
        int stepX = _path.getStep(_currentStep).getX() * _map.tileWidth;
        int stepY = _path.getStep(_currentStep).getY() * _map.tileHeight;
        return Math.abs(stepX - getX()) <= _speed * delta && Math.abs(stepY - getY()) <= _speed * delta;
    }

    public Path getPath() {
        return _path;
    }

    public void setPath(Path path)
    {
        _currentStep = 0;
        _path = path;
    }
}