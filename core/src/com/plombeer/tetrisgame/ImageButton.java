package com.plombeer.tetrisgame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

/**
 * Created by hh on 29.06.2015.
 */
public class ImageButton extends Rectangle{

    private Sprite texture;

    private ClikedInterface clikedInterface;
    public ImageButton(float x, float y, float width, float height, String filename, boolean flip, boolean rotate90, float alpha) {
        super(x, y, width, height);
        texture = new Sprite(new Texture(filename));
        texture.setAlpha(alpha);
        texture.setPosition(x, y);
        texture.setSize(width, height);
        if(rotate90)
            texture.rotate90(false);
        texture.flip(flip, false);
    }

    public void draw(SpriteBatch batch){
        batch.begin();
        texture.draw(batch);
        batch.end();
    }

    public void setClikedInterface(ClikedInterface clikedInterface) {
        this.clikedInterface = clikedInterface;
    }

    public boolean onClick(int x, int y){
        if(x > this.x && y > this.y && x < this.x + this.width && y < this.y + this.height){
            if(clikedInterface != null) {
                clikedInterface.onClick();
                return true;
            }
        }
        return false;
    }

    public void translateY(float deltaY){
        y += deltaY;
        texture.translateY(deltaY);
    }

    public void setPos(float x, float y){
        texture.setPosition(x, y);
        this.x = x;
        this.y = y;
    }
}

