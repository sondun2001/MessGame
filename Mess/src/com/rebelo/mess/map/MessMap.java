package com.rebelo.mess.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.physics.box2d.World;
import com.rebelo.mess.utils.OrthoCamController;

/**
 * Created by sondun2001 on 12/15/13.
 */
public class MessMap extends InputAdapter {

    // TODO: Pass in data from the server to generate original map

    private TiledMap _map;
    private World _world;
    private OrthoCamController _cameraController;
    //private Texture tiles;

    public int width;
    public int height;
    public int tileWidth;
    public int tileHeight;
    public MapLayers layers;

    public MessMap(OrthographicCamera camera, World world)
    {
        _world = world;

        TextureAtlas spriteSheet = new TextureAtlas("data/MessAssets.pack");

        /*tiles = new Texture(Gdx.files.internal("data/maps/tiled/tiles.png"));
        TextureRegion[][] splitTiles = TextureRegion.split(tiles, 32, 32);*/
        Sprite[] groundSprites = new Sprite[1];
        groundSprites[0] = spriteSheet.createSprite("concrete");

        Sprite[] buildingSprites = new Sprite[3];
        buildingSprites[0] = spriteSheet.createSprite("wall");
        buildingSprites[1] = spriteSheet.createSprite("wall_1_corner");
        buildingSprites[2] = spriteSheet.createSprite("support");

        _map = new TiledMap();
        layers = _map.getLayers();
        TiledMapTileLayer layer;
        Cell cell;
        StaticTiledMapTile tile;

        width = 512;
        height = 512;
        tileWidth = 32;
        tileHeight = 32;

        _cameraController = new OrthoCamController(this, camera);
        Gdx.input.setInputProcessor(_cameraController);

        // Ground
        layer = new TiledMapTileLayer(width, height, tileWidth, tileHeight);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cell = new Cell();
                tile = new StaticTiledMapTile(groundSprites[(int)(Math.random() * groundSprites.length)]);
                tile.setBlendMode(TiledMapTile.BlendMode.NONE);
                cell.setTile(tile);
                layer.setCell(x, y, cell);
            }
        }
        layers.add(layer);

        // Buildings
        layer = new TiledMapTileLayer(width, height, tileWidth, tileHeight);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int diceRoll = (int)(Math.random() * 10);
                if (diceRoll == 0)
                {
                    // Randomly put support walls up
                    cell = new Cell();
                    tile = new StaticTiledMapTile(buildingSprites[2]);
                    tile.setId(1);
                    cell.setTile(tile);
                    layer.setCell(x, y, cell);
                }
            }
        }
        layers.add(layer);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return super.touchDown(screenX, screenY, pointer, button);


    }

    public TiledMap getMap()
    {
        return _map;
    }
    public int getPixelWidth() { return width * tileWidth; }
    public int getPixelHeight() { return height * tileHeight; }
}
