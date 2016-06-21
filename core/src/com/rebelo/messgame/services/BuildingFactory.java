package com.rebelo.messgame.services;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.rebelo.messgame.MessGame;
import com.rebelo.messgame.entities.BuildingComponent;
import com.rebelo.messgame.map.MessMap;

/**
 * Created by sondu on 6/15/2016.
 */
public class BuildingFactory {
    private static BuildingFactory ourInstance = new BuildingFactory();

    public static BuildingFactory getInstance() {
        return ourInstance;
    }

    private BuildingFactory() {

    }

    // TODO: Define shape for each tile type and use that to create physics object (http://www.aurelienribon.com/blog/projects/physics-body-editor/)
    // TODO: Pool game objects

    public BuildingComponent createBuildingComponent(MessMap map, TiledMapTileLayer layer, Sprite sprite, int x, int y) {

        int type = 1;

        StaticTiledMapTile tile = new StaticTiledMapTile(sprite);
        tile.setId(type);

        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        cell.setTile(tile);

        layer.setCell(x, y, cell);

        int xPos = x * map.tileWidth;
        int yPos = y * map.tileHeight;
        float halfWidth = map.tileWidth / 2;
        float halfHeight = map.tileHeight / 2;

        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.type = BodyDef.BodyType.StaticBody;
        groundBodyDef.position.set((xPos + halfWidth) / MessGame.PIXELS_PER_METER, (yPos + halfHeight) / MessGame.PIXELS_PER_METER);
        Body groundBody = map.world.createBody(groundBodyDef);

        PolygonShape environmentShape = new PolygonShape();
        environmentShape.setAsBox(halfWidth / MessGame.PIXELS_PER_METER, halfHeight / MessGame.PIXELS_PER_METER);
        groundBody.createFixture(environmentShape, 0);
        environmentShape.dispose();

        BoundingBox box = new BoundingBox(new Vector3(xPos, yPos, 0), new Vector3(xPos + map.tileWidth, yPos + map.tileHeight, 0));

        // TODO: Get from pool
        BuildingComponent buildingComponent = new BuildingComponent();
        buildingComponent.init(groundBody, cell, box);
        return buildingComponent;
    }
}
