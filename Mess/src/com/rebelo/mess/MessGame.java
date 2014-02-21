package com.rebelo.mess;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.rebelo.mess.map.MessMap;
import com.rebelo.mess.map.MessMapPhysics;

public class MessGame implements ApplicationListener
{
    /**
     * Box2d works best with small values. If you use pixels directly you will
     * get weird results -- speeds and accelerations not feeling quite right.
     * Common practice is to use a constant to convert pixels to and from
     * "meters".
     */
    public static final float PIXELS_PER_METER = 60.0f;
    public static final float MAP_WIDTH = 1024;
    public static final float MAP_HEIGHT = 1024;

    public static final float TIMESTEP = 1.0f / 60.0f;
    public static final int VELOCITYITERATIONS = 8;
    public static final int POSITIONITERATIONS = 3;

    public static float WINDOW_WIDTH;
    public static float WINDOW_HEIGHT;
    public static float DENSITY;

    private MessMap _messMap;
    private MessMapPhysics _messPhysics;

    private World _world;

    private BitmapFont _font;
    private SpriteBatch _batch;
    private TiledMapRenderer _renderer;
    private OrthographicCamera _camera;
    //private OrthographicCamera _lightCam;

    private Box2DDebugRenderer _box2dRenderer;
	
	@Override
	public void create() {
        WINDOW_WIDTH = Gdx.graphics.getWidth();
        WINDOW_HEIGHT = Gdx.graphics.getHeight();
        DENSITY = Gdx.graphics.getDensity();

        if (Gdx.app.getType() == Application.ApplicationType.Desktop)
        {
            // set resolution to default and set full-screen to true
            //Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width, Gdx.graphics.getDesktopDisplayMode().height, true);
        }

		/*
		batch = new SpriteBatch();
		
		texture = new Texture(Gdx.files.internal("data/libgdx.png"));
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
		TextureRegion region = new TextureRegion(texture, 0, 0, 512, 275);
		
		sprite = new Sprite(region);
		sprite.setSize(0.9f, 0.9f * sprite.getHeight() / sprite.getWidth());
		sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
		sprite.setPosition(-sprite.getWidth()/2, -sprite.getHeight()/2);*/
        _batch = new SpriteBatch();
        _camera = new OrthographicCamera();
        _camera.setToOrtho(false, WINDOW_WIDTH, WINDOW_HEIGHT);
        _camera.update();

        /**
         * You can set the world's gravity in its constructor. Here, the gravity
         * is negative in the y direction (as in, pulling things down).
         */
        _world = new World(new Vector2(0.0f, 0.0f), true);

        // TODO: Create async loading operations of the following
        _messMap = new MessMap(_camera, _world);

        // Pass in the light cam for the RayHandler
        _messPhysics = new MessMapPhysics(_camera, _world);
        _messPhysics.loadCollisions(_messMap);

        _renderer = new OrthogonalTiledMapRenderer(_messMap.getMap());

        _box2dRenderer = new Box2DDebugRenderer();

        _font = new BitmapFont();
        _font.setScale(DENSITY);
	}

	@Override
	public void dispose() {
        _batch.dispose();

        _box2dRenderer.dispose();
        _world.dispose();
	}

	@Override
	public void render() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        _camera.update();

        _renderer.setView(_camera);
        _renderer.render();

        // TODO: Create RayHandler in MessGame?

        Matrix4 cameraMatrix = _camera.combined.scale(PIXELS_PER_METER, PIXELS_PER_METER, PIXELS_PER_METER);
        _messPhysics.updateAndRender(cameraMatrix);

        // Render UI

        /*_box2dRenderer.render(_world, _camera.combined.scale(
                PIXELS_PER_METER,
                PIXELS_PER_METER,
                PIXELS_PER_METER));*/
        //_box2dRenderer.render(_world, cameraMatrix);

        _batch.begin();

        // TODO: Optimize by caching off y loc
        _font.draw(_batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 20 * DENSITY);

        _batch.end();

        _world.step(TIMESTEP, VELOCITYITERATIONS, POSITIONITERATIONS);
    }

	@Override
	public void resize(int width, int height) {

        WINDOW_WIDTH = Gdx.graphics.getWidth();
        WINDOW_HEIGHT = Gdx.graphics.getHeight();
        DENSITY = Gdx.graphics.getDensity();

        _camera.setToOrtho(false, WINDOW_WIDTH, WINDOW_HEIGHT);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
