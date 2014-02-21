package com.rebelo.mess.map;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ObjectMap;
import com.rebelo.mess.MessGame;

/**
 * Created by sondun2001 on 12/25/13.
 */
public class MessMapPhysics {

    private static final int NUM_RAYS = 500;

    private World _world;
    public RayHandler rayHandler;
    private OrthographicCamera _camera;
    private MessMap _map;
    private ObjectMap<TiledMapTileLayer.Cell, Body> _bodyByCell = new ObjectMap<TiledMapTileLayer.Cell, Body>();
    private ObjectMap<TiledMapTileLayer.Cell, BoundingBox> _boxByCell = new ObjectMap<TiledMapTileLayer.Cell, BoundingBox>();
    private TiledMapTileLayer _buildingLayer;

    public MessMapPhysics(Camera lightCamera, World world)
    {
        _world = world;
        _camera = (OrthographicCamera)lightCamera;

        // PC Settings
        RayHandler.useDiffuseLight(true);
        rayHandler = new RayHandler(_world);
        rayHandler.setCombinedMatrix(lightCamera.combined);
        rayHandler.setAmbientLight(0.05f, 0.05f, 0.1f, 1f);
        rayHandler.setShadows(true);
        rayHandler.setBlur(true);
        rayHandler.setCulling(true);
    }

    public void dispose()
    {
        rayHandler.dispose();
    }

    public void loadCollisions(MessMap map)
    {
        _map = map;

        // Layer that will have walls, and other physics objects
        _buildingLayer = (TiledMapTileLayer)map.getMap().getLayers().get(1);

        int xPos;
        int yPos;
        float halfWidth;
        float halfHeight;
        BoundingBox box;
        TiledMapTileLayer.Cell mapCell;
        for (int y = 0; y < map.height; y++)
        {
            for (int x = 0; x < map.width; x++)
            {
                mapCell = _buildingLayer.getCell(x, y);

                // TODO: Define shape for each tile type and use that to create physics object
                boolean isBox = (mapCell != null && mapCell.getTile().getId() == 1);
                if (isBox)
                {
                    xPos = x * _map.tileWidth;
                    yPos = y * _map.tileHeight;

                    halfWidth = _map.tileWidth / 2;
                    halfHeight = _map.tileHeight / 2;

                    BodyDef groundBodyDef = new BodyDef();
                    groundBodyDef.type = BodyDef.BodyType.StaticBody;
                    groundBodyDef.position.set((xPos + halfWidth) / MessGame.PIXELS_PER_METER,
                            (yPos + halfHeight) / MessGame.PIXELS_PER_METER);
                    Body groundBody = _world.createBody(groundBodyDef);

                    PolygonShape environmentShape = new PolygonShape();
                    environmentShape.setAsBox(halfWidth / MessGame.PIXELS_PER_METER, halfHeight / MessGame.PIXELS_PER_METER);
                    groundBody.createFixture(environmentShape, 0);
                    environmentShape.dispose();

                    box = new BoundingBox(new Vector3(xPos, yPos, 0), new Vector3(xPos + _map.tileWidth, yPos + _map.tileHeight, 0));

                    _bodyByCell.put(mapCell, groundBody);
                    _boxByCell.put(mapCell, box);
                }

                // TODO: This is a test for lights, remove this and add ability to create
                if (!isBox)
                {
                    int diceRoll = (int)(Math.random() * 100);
                    if (diceRoll == 0)
                    {
                        new PointLight(rayHandler, NUM_RAYS,
                                new Color(0, 0, 1, 0.5f), (32 * 8) / MessGame.PIXELS_PER_METER,
                                ((x * map.tileWidth) + (map.tileWidth / 2)) / MessGame.PIXELS_PER_METER,
                                ((y * map.tileHeight) + (map.tileHeight / 2)) / MessGame.PIXELS_PER_METER);

                        // TODO: Other configurations
                        //new PointLight(rayHandler, 1000, Color.CYAN, (32 * 30) / MessGame.PIXELS_PER_METER, 640 / MessGame.PIXELS_PER_METER, 288 / MessGame.PIXELS_PER_METER);
                        //new PointLight(rayHandler, 1000, Color.LIGHT_GRAY, (32 * 10) / MessGame.PIXELS_PER_METER, 320 / MessGame.PIXELS_PER_METER, (2 * 32) / MessGame.PIXELS_PER_METER);
                        //new ConeLight(rayHandler, 1000, Color.WHITE, 1000 / MessGame.PIXELS_PER_METER, (32 * 6) / MessGame.PIXELS_PER_METER, (32 * 6) / MessGame.PIXELS_PER_METER, 45, 30);
                        //new DirectionalLight(rayHandler, 1000, new Color(0.3f, 0.3f, 0.3f, 1f), -45);
                    }
                }
            }
        }
    }

    public void updateAndRender(Matrix4 cameraMatrix)
    {
        TiledMapTileLayer.Cell mapCell;
        Body body;

        // TODO: Only do this if moving camera!
        for (int y = 0; y < _map.height; y++)
        {
            for (int x = 0; x < _map.width; x++)
            {
                mapCell = _buildingLayer.getCell(x, y);
                if (mapCell == null) continue;
                body = _bodyByCell.get(mapCell);
                if (body != null) continue;

                if(_camera.frustum.boundsInFrustum(_boxByCell.get(mapCell)))
                {
                    body.setActive(true);
                }
                else
                {
                    body.setActive(false);
                }
            }
        }

        rayHandler.setCombinedMatrix(cameraMatrix);
        rayHandler.updateAndRender();
    }
}
