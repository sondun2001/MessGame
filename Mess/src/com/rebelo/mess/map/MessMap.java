package com.rebelo.mess.map;

import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
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
import com.rebelo.mess.entities.AISprite;
import com.rebelo.mess.entities.GameLight;
import com.rebelo.mess.entities.GameObject;
import com.rebelo.mess.utils.OrthoCamController;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

import java.util.Iterator;

/**
 * Created by sondun2001 on 12/15/13.
 */
// TODO: Pass in data from the server to generate original map
// TODO: State machine to handle different modes
public class MessMap extends InputAdapter implements TileBasedMap
{
    public static final int LAYER_GROUND = 0;
    public static final int LAYER_BUILDING = 1;
    public static final int LAYER_TALL = 2;

    private static final float MIN_LIGHT = 0.02f;
    private static final float MAX_LIGHT = 0.5f;
    private static final float LIGHT_DELTA = 0.1f;

    // TODO: Make this configurable from the server
    private static final float DAY_NIGHT_LENGTH = 50f;

    private TiledMap _map;
    private OrthographicCamera _camera;
    private OrthoCamController _cameraController;
    private Vector3 _worldCoordinates;
    private InputMultiplexer _multiplexer;
    private AStarPathFinder _aStarPathFinder;

    // Build Mode Variables
    private int _mode = 0;
    private int _entityOption = 0;

    // Lights
    private float _currentAmbientLight = 0;
    private float _currentElapsedTOD;
    private boolean _gettingDark;

    // Layers
    private TiledMapTileLayer _groundLayer;
    private TiledMapTileLayer _buildingLayer;
    private TiledMapTileLayer _objectLayer;

    // Sprites
    private TextureAtlas _atlas;
    private Array<Sprite> _groundSprites = new Array<Sprite>(true, 32);
    private Array<Sprite> _buildingSprites = new Array<Sprite>(true, 32);
    private Array<Sprite> _objectSprites = new Array<Sprite>(true, 32);

    private ObjectMap<Cell, GameObject> _gameObjectByCell = new ObjectMap<TiledMapTileLayer.Cell, GameObject>();
    private ObjectSet<GameObject> _activeCells = new ObjectSet<GameObject>();
    private Array<GameObject> _gameObjects = new Array<GameObject>();

    private AISprite _player;

    public int width;
    public int height;
    public int tileWidth;
    public int tileHeight;
    public MapLayers layers;
    public RayHandler rayHandler;

    public static World world;

    public MessMap(OrthographicCamera camera, World world)
    {
        this.world = world;

        // TODO: Download updated sprite sheet from server
        _atlas = new TextureAtlas("data/MessAssets.pack");

        // TODO: Create sprites from XML or server
        //_groundSprites.add(spriteSheet.createSprite("concrete"));
        _groundSprites.add(_atlas.createSprite("grass"));

        _buildingSprites.add(_atlas.createSprite("wall"));
        _buildingSprites.add(_atlas.createSprite("support"));

        _objectSprites.add(_atlas.createSprite("light_on"));
        _objectSprites.add(_atlas.createSprite("light_off"));

        _player = new AISprite(_objectSprites.get(1), this);
        _player.setPosition(0, 0);

        _map = new TiledMap();
        layers = _map.getLayers();

        _gameObjects.ordered = false;

        // Default map properties
        width = 512;
        height = 512;
        tileWidth = 32;
        tileHeight = 32;

        // Camera Setup
        _camera = camera;
        _cameraController = new OrthoCamController(this, camera);

        // Input processing
        _multiplexer = new InputMultiplexer();
        _multiplexer.addProcessor(this);
        _multiplexer.addProcessor(_cameraController);
        Gdx.input.setInputProcessor(_multiplexer);

        // Ground
        _groundLayer = new TiledMapTileLayer(width, height, tileWidth, tileHeight);
        for (int x = 0; x < width; x++)
        {
            for (int y = 0; y < height; y++)
            {
                Cell cell = new Cell();
                StaticTiledMapTile tile = new StaticTiledMapTile(_groundSprites.get((int)(Math.random() * _groundSprites.size)));
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
        _objectLayer = new TiledMapTileLayer(width, height, tileWidth, tileHeight);
        layers.add(_objectLayer);

        // For input
        _worldCoordinates = new Vector3();

        // PC Settings
        RayHandler.useDiffuseLight(true);
        rayHandler = new RayHandler(this.world);
        rayHandler.setCombinedMatrix(_camera.combined);
        rayHandler.setShadows(true);
        rayHandler.setBlur(false);
        rayHandler.setCulling(true);

        // Our pathfinder!
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

        Iterator itr = _gameObjects.iterator();
        while(itr.hasNext())
        {
            GameObject obj = (GameObject) itr.next();
            obj.dispose();
        }

        _gameObjectByCell.clear();
        _gameObjectByCell = null;

        _aStarPathFinder = null;

        _atlas.dispose();
        _atlas = null;
    }

    public void updateAndRender(Matrix4 cameraMatrix)
    {
        Iterator itr = _gameObjects.iterator();
        while(itr.hasNext())
        {
            GameObject obj = (GameObject) itr.next();

            if (obj.type == GameObject.TYPE_BUILDING)
            {
                Body body = obj.body;
                BoundingBox box = obj.boundingBox;
                boolean inFrustum = _camera.frustum.boundsInFrustum(box);
                if (inFrustum && !_activeCells.contains(obj))
                {
                    body.setActive(true);
                    _activeCells.add(obj);
                }
                else if(!inFrustum && _activeCells.contains(obj))
                {
                    body.setActive(false);
                    _activeCells.remove(obj);
                }
            }
            else if (obj.type == GameObject.TYPE_LIGHT_POINT || obj.type == GameObject.TYPE_LIGHT_CONE)
            {
                ((GameLight) obj).setActive(_currentAmbientLight < 0.3);
            }
        }

        float _elapsedTime = Gdx.graphics.getDeltaTime();
        _currentElapsedTOD += _elapsedTime;

        // TODO: Day / Night cycle
        if (_currentAmbientLight < MAX_LIGHT && !_gettingDark)
        {
            _setAmbientLight(_currentAmbientLight + LIGHT_DELTA * _elapsedTime);
        }
        else if (_currentAmbientLight >= MAX_LIGHT && !_gettingDark && _currentElapsedTOD >= DAY_NIGHT_LENGTH)
        {
            _gettingDark = true;
            _currentElapsedTOD = 0;
        }
        else if (_gettingDark && _currentAmbientLight > MIN_LIGHT)
        {
            _setAmbientLight(_currentAmbientLight - LIGHT_DELTA * _elapsedTime);
        }
        else if (_gettingDark && _currentAmbientLight <= MIN_LIGHT && _currentElapsedTOD >= DAY_NIGHT_LENGTH)
        {
            _gettingDark = false;
            _currentElapsedTOD = 0;
        }

        rayHandler.setCombinedMatrix(cameraMatrix);
        rayHandler.updateAndRender();
    }

    public void updateSprites(Batch batch)
    {
        // TODO: Iterate over all the players
        // TODO: Check if they are in frustrum
        _player.draw(batch);
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
            if (_mode == 0) return false;

            if (_mode == 1)
            {
                if (_createBuildingComponent(_buildingSprites.get(1), tileX, tileY)) return true;
            }
            else if (_mode == 2)
            {
                if (_createLight(_objectSprites.get(0), _objectSprites.get(1), tileX, tileY)) return true;
            }
            else if (_mode == 3)
            {
                Path path = _aStarPathFinder.findPath(_player, (int) _player.getX() / tileWidth, (int) _player.getY() / tileHeight, tileX, tileY);
                _player.setPath(path);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        if (keycode == 8)
        {
            _mode = 1;
        }
        else if (keycode == 9)
        {
            _mode = 2;
        }
        else if (keycode == 10)
        {
            _mode = 3;
        }
        else
        {
            _mode = 0;
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
        Body groundBody = world.createBody(groundBodyDef);

        PolygonShape environmentShape = new PolygonShape();
        environmentShape.setAsBox(halfWidth / MessGame.PIXELS_PER_METER, halfHeight / MessGame.PIXELS_PER_METER);
        groundBody.createFixture(environmentShape, 0);
        environmentShape.dispose();

        BoundingBox box = new BoundingBox(new Vector3(xPos, yPos, 0), new Vector3(xPos + tileWidth, yPos + tileHeight, 0));

        // TODO: Pool game objects
        GameObject gameObject = new GameObject();
        gameObject.init(GameObject.TYPE_BUILDING, groundBody, box);
        _gameObjectByCell.put(cell, gameObject);
        _gameObjects.add(gameObject);

        return true;
    }

    private boolean _destroyBuildingComponent(int x, int y)
    {
        Cell previousCell = _buildingLayer.getCell(x, y);
        if (previousCell == null || previousCell.getTile().getId() != 1) return false;

        GameObject gameObject = _gameObjectByCell.get(previousCell);
        if (_activeCells.contains(gameObject)) _activeCells.remove(gameObject);
        _gameObjects.removeValue(gameObject, true);

        gameObject.dispose();

        _buildingLayer.setCell(x, y, null);

        return true;
    }

    // Pass in config data
    private boolean _createLight(Sprite spriteOn, Sprite spriteOff, int x, int y)
    {
        Cell cell = _buildingLayer.getCell(x, y);
        if (cell == null) cell = new Cell();

        // TODO: Pool light objects
        GameLight light = new GameLight();
        light.init(cell, spriteOn, spriteOff, GameLight.TYPE_LIGHT_POINT, rayHandler);
        light.setDistance((32 * 8) / MessGame.PIXELS_PER_METER);
        light.setColor(new Color(.8f, .8f, .8f, 0.5f));
        light.setPosition(((x * tileWidth) + (tileWidth / 2)) / MessGame.PIXELS_PER_METER, ((y * tileHeight) + (tileHeight / 2)) / MessGame.PIXELS_PER_METER);

        _buildingLayer.setCell(x, y, cell);
        _gameObjectByCell.put(cell, light);
        _gameObjects.add(light);

        return true;
    }

    private boolean _destroyLightComponent(int x, int y)
    {
        Cell previousCell = _buildingLayer.getCell(x, y);
        if (previousCell == null || previousCell.getTile().getId() != 2) return false;

        GameLight light = (GameLight)_gameObjectByCell.get(previousCell);
        _gameObjects.removeValue(light, true);

        light.dispose();
        _gameObjectByCell.remove(previousCell);

        _buildingLayer.setCell(x, y, null);

        return true;
    }

    private void _setAmbientLight(float amount)
    {
        _currentAmbientLight = amount;

        if (_currentAmbientLight < MIN_LIGHT)
        {
            _currentAmbientLight = MIN_LIGHT;
        }
        else if (_currentAmbientLight > MAX_LIGHT)
        {
            _currentAmbientLight = MAX_LIGHT;
        }

        rayHandler.setAmbientLight(_currentAmbientLight, _currentAmbientLight, _currentAmbientLight, 1f);
    }

    // A* Functions!

    @Override
    public int getWidthInTiles() {
        return width;
    }

    @Override
    public int getHeightInTiles() {
        return height;
    }

    @Override
    public void pathFinderVisited(int x, int y) {

    }

    @Override
    public boolean blocked(PathFindingContext context, int tx, int ty)
    {
        TiledMapTileLayer.Cell cell = _buildingLayer.getCell(tx, ty);

        if (cell != null)
        {
            int id = cell.getTile().getId();
            if (id == 1) return true;
        }

        return false;
    }

    @Override
    public float getCost(PathFindingContext context, int tx, int ty) {
        return 1;
    }
}
