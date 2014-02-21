package com.rebelo.mess;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Mess";
		cfg.useGL20 = true;
        cfg.vSyncEnabled = true;
        cfg.resizable = true;
        cfg.width = 1024;
        cfg.height = 768;
		/*cfg.width = 1920;
		cfg.height = 1080;
        cfg.fullscreen = true;*/

		new LwjglApplication(new MessGame(), cfg);
    }
}
