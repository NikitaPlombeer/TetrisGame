package com.plombeer.tetrisgame;

import com.badlogic.gdx.Game;

public class MyGdxGame extends Game {

    GameScreen gameScreen;
    @Override
    public void create() {
        gameScreen = new GameScreen();
        setScreen(gameScreen);
    }
}
