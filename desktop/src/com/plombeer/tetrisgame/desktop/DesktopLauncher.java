package com.plombeer.tetrisgame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.plombeer.tetrisgame.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Tetris";
        config.height = 970;
        config.width = config.height * 9 / 16;
		new LwjglApplication(new MyGdxGame(), config);
	}
}
