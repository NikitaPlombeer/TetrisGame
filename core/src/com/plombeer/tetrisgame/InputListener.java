package com.plombeer.tetrisgame;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

/**
 * Created by hh on 12.06.2015.
 */
public class InputListener implements InputProcessor{

    GameScreen gameScreen;

    public InputListener(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.LEFT){
            gameScreen.getPole().move(Pole.TypeOfMove.left);
        }else
        if(keycode == Input.Keys.RIGHT){
            gameScreen.getPole().move(Pole.TypeOfMove.right);
        }
        else
        if(keycode == Input.Keys.DOWN){
            gameScreen.putDown();//move(Pole.TypeOfMove.down);
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            if(!gameScreen.qLose){
                if(gameScreen.isPaused)
                    gameScreen.pauseGame(false); else
                gameScreen.pauseButton.onClick(screenX, GameScreen.SCREEN_HEIGHT - screenY);
                gameScreen.rightButton.onClick(screenX, GameScreen.SCREEN_HEIGHT - screenY);
                gameScreen.leftButton.onClick(screenX, GameScreen.SCREEN_HEIGHT - screenY);
                gameScreen.downButton.onClick(screenX, GameScreen.SCREEN_HEIGHT - screenY);
                gameScreen.rotateButton.onClick(screenX, GameScreen.SCREEN_HEIGHT - screenY);
            } else
                gameScreen.newGameButton.onClick(screenX, GameScreen.SCREEN_HEIGHT - screenY);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
