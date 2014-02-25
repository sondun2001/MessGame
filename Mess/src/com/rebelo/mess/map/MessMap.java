package com.rebelo.mess.map;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.rebelo.mess.MessGame;
import com.rebelo.mess.entities.GameLight;
import com.rebelo.mess.entities.GameObject;
import com.rebelo.mess.utils.OrthoCamController;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

/**
 * Created by sondun2001 on 12/15/13.
 */
public class MessMap extends InputAdapter implements TileBasedMap {

    // TODO: Pass in data from the server to generate original map
    public static final int LAYER_GROUND = 0;
    public static final int LAYER_BUILDING = 1;
    public static final int LAYER_TALL = 2;

    private static final int NUM_RAYS = 1000;

    // TODO: State machine to handle different modes

    private TiledMap _map;
    private World _world;
    private OrthographicCamera _camera;
    private OrthoCamController _cameraController;
    private Vector3 _worldCoordinates;
    private InputMultiplexer _multiplexer;
    private AStarPathFinder _aStarPathFinder;

    private int _entityToBuild = 0;

    // Layers
    private TiledMapTileLayer _groundLayer;
    private TiledMapTileLayer _buildingLayer;
    private TiledMapTileLayer _objectLayer;

    // Sprites
    private Array<Sprite> _buildingSprites = new Array<Sprite>(true, 32);
    private Array<Sprite> _objectSprites = new Array<Sprite>(true, 32);

    private ObjectMap<Cell, Body> _bodyByCell = new ObjectMap<TiledMapTileLayer.Cell, Body>();
    private ObjectMap<Cell, GameObject> _gameObjectByCell = new ObjectMap<TiledMapTileLayer.Cell, GameObject>();
    private ObjectMap<TiledMapTileLayer.Cell, BoundingBox> _boxByCell = new ObjectMap<TiledMapTileLayer.Cell, BoundingBox>();
    private ObjectSet<Cell> _activeCells = new ObjectSet<TiledMapTileLayer.Cell>();

    public int width;
    public int height;
    public int tileWidth;
    public int tileHeight;
    public MapLayers layers;
    public RayHandler rayHandler;

    public MessMap(OrthographicCamera camera, World world)
    {
        _world = world;

        // TODO: Download updated sprite sheet from server
        TextureAtlas spriteSheet = new TextureAtlas("data/game_assets.atlas");

        // TODO: Create sprites from XML or server
        Sprite[] groundSprites = new Sprite[1];
        groundSprites[0] = spriteSheet.createSprite("concrete");

        _buildingSprites.add(spriteSheet.createSprite("wall"));
        _buildingSprites.add(spriteSheet.createSprite("support"));

        _objectSprites.add(spriteSheet.createSprite("light"));

        _map = new TiledMap();
        layers = _map.getLayers();

        Cell cell;
        StaticTiledMapTile tile;

        // Default map properties
        width = 512;
        height = 512;
        tileWidth = 32;
        tileHeight = 32;

        _camera = camera;
        _cameraController = new OrthoCamController(this, camera);

        // Input processing
        _multiplexer = new InputMultiplexer();
        _multiplexer.addProcessor(this);
        _multiplexer.addProcessor(_cameraController);
        Gdx.input.setInputProcessor(_multiplexer);

        // Ground
        _groundLayer = new TiledMapTileLayer(width, height, tileWidth, tileHeight);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cell = new Cell();
                tile = new StaticTiledMapTile(groundSprites[(int)(Math.random() * groundSprites.length)]);
                tile.setBlendMode(TiledMapTile.BlendMode.NONE);
                cell.setTile(tile);
                _groundLayer.setCell(x, y, cell);
            }
        }
        layers.add(_groundLayer);

        // Buildings
        _buildingLayer = new TiledMapTileLayer(width, height, tileWidth, tileHeight);
        layers.add(_buildingLayer);

        // Objects that will display over buildings
        _objectLayer = new TiledMapTileLayer(width, height, tileWidth, tileHeight)
        layers.add(_objectLayer);

        // For input
        _worldCoordinates = new Vector3();

        // PC Settings
        RayHandler.useDiffuseLight(true);
        rayHandler = new RayHandler(_world);
        rayHandler.setCombinedMatrix(_camera.combined);
        //rayHandler.setAmbientLight(0.05f, 0.05f, 0.1f, 1f);
        rayHandler.setShadows(true);
        rayHandler.setBlur(false);
        rayHandler.setCulling(true);

        _aStarPathFinder = new AStarPathFinder(this, 32, false);
    }

    public void dispose()
    {
        _multiplexer.clear();
        Gdx.input.setInputProcessor(null);

        rayHandler.dispose();
        rayHandler = null;

        _activeCells.clear();
        _activeCells = null;

        _bodyByCell.clear();
        _bodyByCell = null;

        _boxByCell.clear();
        _boxByCell = null;

        _gameObjectByCell.clear();
        _gameObjectByCell = null;

        _aStarPathFinder = null;
    }

    public void updateAndRender(Matrix4 cameraMatrix)
    {
        TiledMapTileLayer.Cell mapCell;
        Body body;

        // TODO: Only do this if moving camera!
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                mapCell = _buildingLayer.getCell(x, y);
                if (mapCell == null) continue;

                body = _bodyByCell.get(mapCell);
                if (body == null) continue;

                boolean inFrustum = _camera.frustum.boundsInFrustum(_boxByCell.get(mapCell));
                if (inFrustum && !_activeCells.contains(mapCell))
                {
                    body.setActive(true);
                    _activeCells.add(mapCell);
                }
                else if(!inFrustum && _activeCells.contains(mapCell))
                {
                    body.setActive(false);
                    _activeCells.remove(mapCell);
                }
            }
        }

        rayHandler.setCombinedMatrix(cameraMatrix);
        rayHandler.updateAndRender();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        _camera.unproject(_worldCoordinates.set(screenX, screenY, 0));

        int tileX = (int)_worldCoordinates.x / tileWidth;
        int tileY = (int)_worldCoordinates.y / tileHeight;

        TiledMapTileLayer.Cell cell = _buildingLayer.getCell(tileX, tileY);

        if (cell != null)
        {
            int id = cell.getTile().getId();
            Gdx.app.log("MessMap", "cell = " + id);
            if (id == 1)
            {
                _destroyBuildingComponent(tileX, tileY);
            }
            else if (id == 2)
            {
                _destroyLightComponent(tileX, tileY);
            }

            return true;
        }
        else
        {
            if (_entityToBuild == 0) return false;

            if (_entityToBuild == 1)
            {
                if (_createBuildingComponent(_buildingSprites.get(1), tileX, tileY)) return true;
            }
            else if (_entityToBuild == 2)
            {
                if (_createLight(_objectSprites.get(0), tileX, tileY)) return true;
            }
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        if (keycode == 8)
        {
            _entityToBuild = 1;
        }
        else if (keycode == 9)
        {
            _entityToBuild = 2;
        }
        else
        {
            _entityToBuild = 0;
        }

        return false;
    }

    public TiledMap getMap()
    {
        return _map;
    }
    public int getPixelWidth() { return width * tileWidth; }
    public int getPixelHeight() { return height * tileHeight; }

    private boolean _createBuildingComponent(Sprite sprite, int x, int y)
    {
        if (_buildingLayer.getCell(x, y) != null) return false;

        int type = 1;

        Cell cell = new Cell();
        StaticTiledMapTile tile = new StaticTiledMapTile(sprite);
        tile.setId(type);
        cell.setTile(tile);
        _buildingLayer.setCell(x, y, cell);

        // TODO: Define shape for each tile type and use that to create physics object

        int xPos = x * tileWidth;
        int yPos = y * tileHeight;

        float halfWidth = tileWidth / 2;
        float halfHeight = tileHeight / 2;

        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.type = BodyDef.BodyType.StaticBody;
        groundBodyDef.position.set((xPos + halfWidth) / MessGame.PIXELS_PER_METER,
                (yPos + halfHeight) / MessGame.PIXELS_PER_METER);
        Body groundBody = _world.createBody(groundBodyDef);

        PolygonShape environmentShape = new PolygonShape();
        environmentShape.setAsBox(halfWidth / MessGame.PIXELS_PER_METER, halfHeight / MessGame.PIXELS_PER_METER);
        groundBody.createFixture(environmentShape, 0);
        environmentShape.dispose();

        BoundingBox box = new BoundingBox(new Vector3(xPos, yPos, 0), new Vector3(xPos + tileWidth, yPos + tileHeight, 0));

        _bodyByCell.put(cell, groundBody);
        _boxByCell.put(cell, box);

        return true;
    }

    private boolean _destroyBuildingComponent(int x, int y)
    {
        Cell previousCell = _buildingLayer.getCell(x, y);
        if (previousCell == null || previousCell.getTile().getId() != 1) return false;

        Body body = _bodyByCell.get(previousCell);
        if (body != null) _world.destroyBody(body);
        _bodyByCell.remove(previousCell);

        BoundingBox box = _boxByCell.get(previousCell);
        _boxByCell.remove(previousCell);

        if (_activeCells.contains(previousCell)) _activeCells.remove(previousCell);

        _buildingLayer.setCell(x, y, null);

        return true;
    }

    private boolean _createLight(Sprite sprite, int x, int y)
    {
        if (_buildingLayer.getCell(x, y) != null) return false;

        GameLight light = new GameLight(sprite, GameLight.TYPE_POINT, rayHandler, NUM_RAYS, new Color(0, 0, 1, 0.5f), (32 * 8) / MessGame.PIXELS_PER_METER);
        light.setPosition(((x * tileWidth) + (tileWidth / 2)) / MessGame.PIXELS_PER_METER, ((y * tileHeight) + (tileHeight / 2)) / MessGame.PIXELS_PER_METER);

        Cell cell = new Cell();
        StaticTiledMapTile tile = new StaticTiledMapTile(sprite);
        tile.setId(2);
        cell.setTile(tile);

        _buildingLayer.setCell(x, y, cell);
        _gameObjectByCell.put(cell, light);

        return true;
    }

    private boolean _destroyLightComponent(int x, int y)
    {
        Cell previousCell = _buildingLayer.getCell(x, y);
        if (previousCell == null || previousCell.getTile().getId() != 2) return false;

        GameLight light = (GameLight)_gameObjectByCell.get(previousCell);
        light.dispose();
        _gameObjectByCell.remove(previousCell);

        _buildingLayer.setCell(x, y, null);

        return true;
    }

    // A* Functions!

    @Override
    public int getWidthInTiles() {
        return 0;
    }

    @Override
    public int getHeightInTiles() {
        return 0;
    }

    @Override
    public void pathFinderVisited(int x, int y) {

    }

    @Override
    public boolean blocked(PathFindingContext context, int tx, int ty) {
        return false;
    }

    @Override
    public float getCost(PathFindingContext context, int tx, int ty) {
        return 0;
    }
}
