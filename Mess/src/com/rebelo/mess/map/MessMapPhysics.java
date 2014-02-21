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
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.rebelo.mess.MessGame;
import kirchner.jumper.LineSegment;

import java.util.ArrayList;
import java.util.HashMap;

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

        /**
         * Detect the tiles and dynamically create a representation of the map
         * layout, for collision detection. Each tile has its own collision
         * rules stored in an associated file.
         *
         * The file contains lines in this format (one line per type of tile):
         * tileNumber XxY,XxY XxY,XxY
         *
         * Ex:
         *
         * 3 0x0,31x0 ... 4 0x0,29x0 29x0,29x31
         *
         * For a 32x32 tileset, the above describes one line segment for tile #3
         * and two for tile #4. Tile #3 has a line segment across the top. Tile
         * #1 has a line segment across most of the top and a line segment from
         * the top to the bottom, 30 pixels in.


        FileHandle fh = Gdx.files.internal(collisionsFile);
        String collisionFile = fh.readString();
        String lines[] = collisionFile.split("\\r?\\n");

        HashMap<Integer, ArrayList<LineSegment>> tileCollisionJoints = new HashMap<Integer, ArrayList<LineSegment>>();
         */
        /**
         * Some locations on the map (perhaps most locations) are "undefined",
         * empty space, and will have the tile type 0. This code adds an empty
         * list of line segments for this "default" tile.

        tileCollisionJoints.put(Integer.valueOf(0),
                new ArrayList<LineSegment>());

        for (int n = 0; n < lines.length; n++) {
            String cols[] = lines[n].split(" ");
            int tileNo = Integer.parseInt(cols[0]);

            ArrayList<LineSegment> tmp = new ArrayList<LineSegment>();

            for (int m = 1; m < cols.length; m++) {
                String coords[] = cols[m].split(",");

                String start[] = coords[0].split("x");
                String end[] = coords[1].split("x");

                tmp.add(new LineSegment(Integer.parseInt(start[0]), Integer
                        .parseInt(start[1]), Integer.parseInt(end[0]), Integer
                        .parseInt(end[1])));
            }

            tileCollisionJoints.put(Integer.valueOf(tileNo), tmp);
        }

        ArrayList<LineSegment> collisionLineSegments = new ArrayList<LineSegment>();

        for (int y = 0; y < map.height; y++) {
            for (int x = 0; x < map.width; x++) {

                // TODO: Fix this
                //int tileType = map.layers.get(0).getProperties()[(map.height - 1) - y][x];
                int tileType = 1;

                for (int n = 0; n < tileCollisionJoints.get(
                        Integer.valueOf(tileType)).size(); n++) {
                    LineSegment lineSeg = tileCollisionJoints.get(
                            Integer.valueOf(tileType)).get(n);

                    addOrExtendCollisionLineSegment(x * map.tileWidth
                            + lineSeg.start().x, y * map.tileHeight
                            - lineSeg.start().y + 32, x * map.tileWidth
                            + lineSeg.end().x, y * map.tileHeight
                            - lineSeg.end().y + 32, collisionLineSegments);
                }
            }
        }

        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.type = BodyDef.BodyType.StaticBody;
        Body groundBody = world.createBody(groundBodyDef);
        for (LineSegment lineSegment : collisionLineSegments) {
            EdgeShape environmentShape = new EdgeShape();

            environmentShape.set(
                    lineSegment.start().mul(1 / pixelsPerMeter), lineSegment
                    .end().mul(1 / pixelsPerMeter));
            groundBody.createFixture(environmentShape, 0);
            environmentShape.dispose();
        }
        */
        /**
         * Drawing a boundary around the entire map. We can't use a box because
         * then the world objects would be inside and the physics engine would
         * try to push them out.
         */

        /*EdgeShape mapBounds = new EdgeShape();
        mapBounds.set(new Vector2(0.0f, 0.0f), new Vector2(getWidth()
                / pixelsPerMeter, 0.0f));
        groundBody.createFixture(mapBounds, 0);

        mapBounds.set(new Vector2(0.0f, getHeight() / pixelsPerMeter),
                new Vector2(getWidth() / pixelsPerMeter, getHeight()
                        / pixelsPerMeter));
        groundBody.createFixture(mapBounds, 0);

        mapBounds.set(new Vector2(0.0f, 0.0f), new Vector2(0.0f,
                getHeight() / pixelsPerMeter));
        groundBody.createFixture(mapBounds, 0);

        mapBounds.set(new Vector2(getWidth() / pixelsPerMeter, 0.0f),
                new Vector2(getWidth() / pixelsPerMeter, getHeight()
                        / pixelsPerMeter));
        groundBody.createFixture(mapBounds, 0);

        mapBounds.dispose();*/

        //HashMap<Integer, ArrayList<LineSegment>> tileCollisionJoints = new HashMap<Integer, ArrayList<LineSegment>>();
        /**
         * Some locations on the map (perhaps most locations) are "undefined",
         * empty space, and will have the tile type 0. This code adds an empty
         * list of line segments for this "default" tile.
         * */

        /*IntMap<ArrayList<LineSegment>> tileCollisionJoints = new IntMap<ArrayList<LineSegment>>();
        tileCollisionJoints.put(Integer.valueOf(0), new ArrayList<LineSegment>());

        // 1 Will be a square
        ArrayList<LineSegment> tmp = new ArrayList<LineSegment>();
        int tileNo = 1;

        tmp.add(new LineSegment(0, 0, 32, 0));
        tmp.add(new LineSegment(32, 0, 32, 32));
        tmp.add(new LineSegment(32, 32, 0, 32));
        tmp.add(new LineSegment(0, 32, 0, 0));

        tileCollisionJoints.put(Integer.valueOf(tileNo), tmp);


        ArrayList<LineSegment> collisionLineSegments = new ArrayList<LineSegment>();
        */
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

                    /*for (int n = 0; n < tileCollisionJoints.get(
                            Integer.valueOf(1)).size(); n++) {
                        LineSegment lineSeg = tileCollisionJoints.get(
                                Integer.valueOf(1)).get(n);

                        addOrExtendCollisionLineSegment(x * map.tileWidth
                                + lineSeg.start().x, y * map.tileHeight
                                - lineSeg.start().y + 32, x * map.tileWidth
                                + lineSeg.end().x, y * map.tileHeight
                                - lineSeg.end().y + 32, collisionLineSegments);
                    }*/
                }
                else
                {
                    int diceRoll = (int)(Math.random() * 100);
                    if (diceRoll == 0)
                    {
                        new PointLight(rayHandler, NUM_RAYS,
                                new Color(0, 0, 1, 0.5f), (32 * 8) / MessGame.PIXELS_PER_METER,
                                ((x * map.tileWidth) + (map.tileWidth / 2)) / MessGame.PIXELS_PER_METER,
                                ((y * map.tileHeight) + (map.tileHeight / 2)) / MessGame.PIXELS_PER_METER);
                    }
                }

                //Gdx.app.log("Tile", "tileType=" + tileType);

                // This will get the proper line segments per tile type
                /*for (int n = 0; n < tileCollisionJoints.get(
                        Integer.valueOf(tileType)).size(); n++) {
                    LineSegment lineSeg = tileCollisionJoints.get(
                            Integer.valueOf(tileType)).get(n);

                    addOrExtendCollisionLineSegment(x * map.tileWidth
                            + lineSeg.start().x, y * map.tileHeight
                            - lineSeg.start().y + 32, x * map.tileWidth
                            + lineSeg.end().x, y * map.tileHeight
                            - lineSeg.end().y + 32, collisionLineSegments);
                }*/
            }
        }

        //new PointLight(rayHandler, 1000, Color.CYAN, (32 * 30) / MessGame.PIXELS_PER_METER, 640 / MessGame.PIXELS_PER_METER, 288 / MessGame.PIXELS_PER_METER);
        //new PointLight(rayHandler, 1000, Color.LIGHT_GRAY, (32 * 10) / MessGame.PIXELS_PER_METER, 320 / MessGame.PIXELS_PER_METER, (2 * 32) / MessGame.PIXELS_PER_METER);
        //new ConeLight(rayHandler, 1000, Color.WHITE, 1000 / MessGame.PIXELS_PER_METER, (32 * 6) / MessGame.PIXELS_PER_METER, (32 * 6) / MessGame.PIXELS_PER_METER, 45, 30);
        //new DirectionalLight(rayHandler, 1000, new Color(0.3f, 0.3f, 0.3f, 1f), -45);

        /*BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.type = BodyDef.BodyType.StaticBody;
        Body groundBody = _world.createBody(groundBodyDef);

        for (LineSegment lineSegment : collisionLineSegments) {
            EdgeShape environmentShape = new EdgeShape();

            environmentShape.set(lineSegment.start().scl(1 / MessGame.PIXELS_PER_METER),
                    lineSegment.end().scl(1 / MessGame.PIXELS_PER_METER));
            groundBody.createFixture(environmentShape, 0);
            environmentShape.dispose();
        }*/
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

    /**
     * This is a helper function that makes calls that will attempt to extend
     * one of the line segments already tracked by TiledMapHelper, if possible.
     * The goal is to have as few line segments as possible.
     *
     * Ex: If you have a line segment in the system that is from 1x1 to 3x3 and
     * this function is called for a line that is 4x4 to 9x9, rather than add a
     * whole new line segment to the list, the 1x1,3x3 line will be extended to
     * 1x1,9x9. See also: LineSegment.extendIfPossible.
     *
     * @param lsx1
     *            starting x of the new line segment
     * @param lsy1
     *            starting y of the new line segment
     * @param lsx2
     *            ending x of the new line segment
     * @param lsy2
     *            ending y of the new line segment
     * @param collisionLineSegments
     *            the current list of line segments
     */
    private void addOrExtendCollisionLineSegment(float lsx1, float lsy1,
                                                 float lsx2, float lsy2, ArrayList<LineSegment> collisionLineSegments) {
        LineSegment line = new LineSegment(lsx1, lsy1, lsx2, lsy2);

        boolean didextend = false;

        for (LineSegment test : collisionLineSegments) {
            if (test.extendIfPossible(line)) {
                didextend = true;
                break;
            }
        }

        if (!didextend) {
            collisionLineSegments.add(line);
        }
    }
}
