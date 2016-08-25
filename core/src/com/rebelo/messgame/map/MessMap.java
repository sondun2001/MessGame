package com.rebelo.messgame.map;

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
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.TimeUtils;
import com.rebelo.messgame.MessGame;
import com.rebelo.messgame.controllers.AIAgentController;
import com.rebelo.messgame.entities.*;
import com.rebelo.messgame.models.Agent;
import com.rebelo.messgame.services.BuildingFactory;
import com.rebelo.messgame.services.ControllerManager;
import com.rebelo.messgame.services.HumanAgentFactory;
import com.rebelo.messgame.services.ProjectileFactory;
import com.rebelo.messgame.utils.OrthoCamController;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;

import java.util.Iterator;

/**
 * Created by sondun2001 on 12/15/13.
 */
// TODO: Pass in data from the server to generate original map
// TODO: State machine to handle different modes
public class MessMap extends InputAdapter implements TileBasedMap, ITileMap
{
    public static final int LAYER_GROUND = 0;
    public static final int LAYER_BUILDING = 1;
    public static final int LAYER_TALL = 2;

    private static final int MAX_LOCAL_SLOTS = 1;

    private static final float MIN_LIGHT = 0.2f;
    private static final float MAX_LIGHT = 0.5f;
    private static final float LIGHT_DELTA = 0.1f;

    private static final float PHYSICS_STEP = 1/60f;
    public static final int VELOCITYITERATIONS = 6;
    public static final int POSITIONITERATIONS = 3;

    // TODO: Make this configurable from the server
    private static final float DAY_NIGHT_LENGTH = 50f;

    private TiledMap _map;
    private OrthographicCamera _camera;
    private OrthogonalTiledMapRenderer _renderer;
    private OrthoCamController _cameraController;
    private Batch _tileMapSpriteBatch;
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

    public TextureAtlas getAtlas() {
        return _atlas;
    }

    // Sprites
    // TODO: Create map of sprites by name for each category
    private TextureAtlas _atlas;
    private Array<Sprite> _groundSprites = new Array<Sprite>(true, 32);
    private Array<Sprite> _buildingSprites = new Array<Sprite>(true, 32);
    private Array<Sprite> _objectSprites = new Array<Sprite>(true, 32);

    private ObjectMap<Cell, GameObject> _gameObjectByCell = new ObjectMap<TiledMapTileLayer.Cell, GameObject>();
    private ObjectSet<GameObject> _activeCells = new ObjectSet<GameObject>();
    private Array<GameObject> _gameObjects = new Array<GameObject>();

    //private AISprite _player;
    //private HumanAgent _aiAgent;
    //private HumanAgent _controlledAgent;

    private ControllerManager _controllerManager;

    public Array<HumanAgent> getAgents() {
        return _agents;
    }

    private Array<HumanAgent> _agents = new Array<HumanAgent>(true, MAX_LOCAL_SLOTS, HumanAgent.class);

    // TODO: Create 4 agents. Assign either AI or Gamepad

    public int width;
    public int height;
    public int tileSize;
    public MapLayers layers;
    public RayHandler rayHandler;

    public static World world;

    public MessMap(OrthographicCamera camera, World world)
    {

        this.world = world;
        world.setContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();

                if (!fixtureA.isSensor() && !fixtureB.isSensor()) {
                    Gdx.app.log("beginContact", "between " + fixtureA.toString() + " and " + fixtureB.toString());
                }
            }

            @Override
            public void endContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                //Gdx.app.log("endContact", "between " + fixtureA.toString() + " and " + fixtureB.toString());
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        });

        _controllerManager = new ControllerManager(this);

        // TODO: Download updated sprite sheet from server
        _atlas = new TextureAtlas("data/MessAssets.pack");

        // TODO: Create sprites from XML or server
        //_groundSprites.add(spriteSheet.createSprite("concrete"));
        _groundSprites.add(_atlas.createSprite("grass"));

        _buildingSprites.add(_atlas.createSprite("wall"));
        _buildingSprites.add(_atlas.createSprite("support"));

        _objectSprites.add(_atlas.createSprite("light_on"));
        _objectSprites.add(_atlas.createSprite("light_off"));

        //_player = new AISprite(_objectSprites.get(1), this);
        //_player.setPosition(0, 0);

        // TODO: How will we manage agent creation?
        for (int i = 0; i < MAX_LOCAL_SLOTS; i++) {
            HumanAgent agent = HumanAgentFactory.getInstance().createAgent(_buildingSprites.get(1), new Agent(), this, 30, 30);
            agent.setController(new AIAgentController(agent));
            _agents.add(agent);
        }

        /*
        _aiAgent = HumanAgentFactory.getInstance().createAgent(_buildingSprites.get(1), new Agent(), this, 30, 30);
        _aiAgent.setController(new AIAgentController(_aiAgent));

        _controlledAgent = HumanAgentFactory.getInstance().createAgent(_buildingSprites.get(1), new Agent(), this, 60, 60);
        _controlledAgent.setController(new GamepadAgentController(_controlledAgent));
        */

        _map = new TiledMap();
        layers = _map.getLayers();

        _gameObjects.ordered = false;

        // Default map properties
        width = 512;
        height = 512;
        tileSize = 32;
        tileSize = 32;

        // Camera Setup
        _camera = camera;
        _cameraController = new OrthoCamController(this, camera);

        // Input processing
        _multiplexer = new InputMultiplexer();
        _multiplexer.addProcessor(this);
        _multiplexer.addProcessor(_cameraController);
        Gdx.input.setInputProcessor(_multiplexer);

        // Ground
        _groundLayer = new TiledMapTileLayer(width, height, tileSize, tileSize);
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
        _buildingLayer = new TiledMapTileLayer(width, height, tileSize, tileSize);
        layers.add(_buildingLayer);

        // Objects that will display over buildings
        _objectLayer = new TiledMapTileLayer(width, height, tileSize, tileSize);
        layers.add(_objectLayer);

        // For input
        _worldCoordinates = new Vector3();

        // PC Settings
        RayHandler.useDiffuseLight(true);
        rayHandler = new RayHandler(this.world);
        rayHandler.setCombinedMatrix(_camera);
        rayHandler.setShadows(true);
        rayHandler.setBlur(true);
        rayHandler.setCulling(true);

        // Our pathfinder!
        _aStarPathFinder = new AStarPathFinder(this, 32, false);

        _renderer = new OrthogonalTiledMapRenderer(_map);
        _tileMapSpriteBatch = _renderer.getBatch();
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

    double currentTime;
    double accumulator;

    void updatePhysics(float step) {

    }

    void interpolateObjects(float alpha) {

    }

    void drawObjects() {
        // TODO: Check if they are in frustrum

        _tileMapSpriteBatch.begin();

        // Iterate over all the players
        Iterator itr = _agents.iterator();
        while(itr.hasNext())
        {
            HumanAgent agent = (HumanAgent) itr.next();
            agent.draw(_tileMapSpriteBatch);
        }

        ProjectileFactory.getInstance().draw(_tileMapSpriteBatch);

        _tileMapSpriteBatch.end();
    }

    public void renderMap() {
        // Render date Map
        _renderer.setView(_camera);
        _renderer.render();
    }

    public void updateAndRender(Matrix4 cameraMatrix)
    {
        double newTime = TimeUtils.millis() / 1000.0;
        double frameTime = Math.min(newTime - currentTime, 0.25);

        accumulator += frameTime;
        currentTime = newTime;

        while (accumulator >= PHYSICS_STEP)
        {
            updatePhysics(PHYSICS_STEP);
            this.world.step(PHYSICS_STEP, VELOCITYITERATIONS, POSITIONITERATIONS);

            accumulator -= PHYSICS_STEP;
        }

        // Check which objects are in frustrum
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

        double alpha = accumulator / PHYSICS_STEP;

        interpolateObjects((float)alpha);

        // Render objects
        drawObjects();

        Iterator agnetItr = _agents.iterator();
        while(agnetItr.hasNext())
        {
            HumanAgent agent = (HumanAgent) agnetItr.next();
            agent.update(Gdx.graphics.getDeltaTime());
        }

        ProjectileFactory.getInstance().update(Gdx.graphics.getDeltaTime());

        // Keep track of day and time
        processDayAndTime();

        // TODO: Find alternative?
        rayHandler.setCombinedMatrix(cameraMatrix);
        rayHandler.updateAndRender();
    }

    // TODO: Add debug option to trigger DAY/NIGHT
    private int _daysPassed = 0;
    private void processDayAndTime() {
        float _elapsedTime = Gdx.graphics.getDeltaTime();
        _currentElapsedTOD += _elapsedTime;

        // Day / Night cycle
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
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        _camera.unproject(_worldCoordinates.set(screenX, screenY, 0));

        int tileX = (int)_worldCoordinates.x / tileSize;
        int tileY = (int)_worldCoordinates.y / tileSize;

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
                //Path path = _aStarPathFinder.findPath(_player, (int) _player.getX() / tileSize, (int) _player.getY() / tileSize, tileX, tileY);
                //_player.setPath(path);
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
    public int getPixelWidth() { return width * tileSize; }
    public int getPixelHeight() { return height * tileSize; }

    private boolean _createBuildingComponent(Sprite sprite, int x, int y)
    {
        if (_buildingLayer.getCell(x, y) != null) return false;

        BuildingComponent buildingComponent = BuildingFactory.getInstance().createBuildingComponent(this, _buildingLayer, sprite, x, y);
        _gameObjectByCell.put(buildingComponent.cell, buildingComponent);
        _gameObjects.add(buildingComponent);

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
        light.setPosition(((x * tileSize) + (tileSize / 2)) / MessGame.PIXELS_PER_METER, ((y * tileSize) + (tileSize / 2)) / MessGame.PIXELS_PER_METER);

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

    @Override
    public MapTile getTile(float x, float y) {
        return null;
    }

    @Override
    public float getTileSize() {
        return tileSize;
    }
}
