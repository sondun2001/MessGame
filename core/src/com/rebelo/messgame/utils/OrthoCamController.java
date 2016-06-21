package com.rebelo.messgame.utils;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.rebelo.messgame.map.MessMap;

/**
 * Created by sondun2001 on 12/15/13.
 */
public class OrthoCamController extends InputAdapter {
    final OrthographicCamera camera;
    final Vector3 curr = new Vector3();
    final Vector3 last = new Vector3(-1, -1, -1);
    final Vector3 delta = new Vector3();

    final float MIN_ZOOM = 0.5f;
    final float MAX_ZOOM = 2f;

    private MessMap _messMap;

    public OrthoCamController (MessMap map, OrthographicCamera camera)
    {
        this.camera = camera;
        _messMap = map;
    }

    @Override
    public boolean touchDragged (int x, int y, int pointer)
    {
        camera.unproject(curr.set(x, y, 0));
        if (!(last.x == -1 && last.y == -1 && last.z == -1)) {
            camera.unproject(delta.set(last.x, last.y, 0));
            delta.sub(curr);
            camera.position.add(delta.x, delta.y, 0);

            _clampCamera();
        }
        last.set(x, y, 0);
        return false;
    }

    @Override
    public boolean touchUp (int x, int y, int pointer, int button)
    {
        last.set(-1, -1, -1);
        return false;
    }

    @Override
    public boolean touchDown (int x, int y, int pointer, int button)
    {
        return false;
    }

    @Override
    public boolean scrolled (int amount)
    {
        float currentZoom = camera.zoom;
        float deltaZoom = (amount * 0.2f);
        if (currentZoom + deltaZoom < MAX_ZOOM && currentZoom + deltaZoom > MIN_ZOOM)
        {
            camera.zoom = currentZoom + deltaZoom;

            _clampCamera();
        }
        return false;
    }

    @Override
    public boolean keyDown (int keycode) {
        return false;
    }

    @Override
    public boolean keyUp (int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped (char character) {
        return false;
    }

    private void _clampCamera() {
        // http://stackoverflow.com/questions/12039465/keep-libgdx-camera-inside-boundaries-when-panning-and-zooming/16474998#16474998
        // Sean Clifford
        float camX = camera.position.x;
        float camY = camera.position.y;

        Vector2 camMin = new Vector2(camera.viewportWidth, camera.viewportHeight);
        camMin.scl(camera.zoom / 2); //bring to center and scale by the zoom level
        Vector2 camMax = new Vector2(_messMap.getPixelWidth(), _messMap.getPixelHeight());
        camMax.sub(camMin); //bring to center

        //keep camera within borders
        camX = Math.min(camMax.x, Math.max(camX, camMin.x));
        camY = Math.min(camMax.y, Math.max(camY, camMin.y));

        camera.position.set(camX, camY, camera.position.z);
        // End stackoverflow
    }
}