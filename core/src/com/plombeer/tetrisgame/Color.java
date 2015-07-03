package com.plombeer.tetrisgame;

/**
 * Created by hh on 27.06.2015.
 */
public class Color{
    public int R = 255;
    public int G = 255;
    public int B = 255;

    Color(int r, int g, int b) {
        R = r;
        G = g;
        B = b;
    }

    Color(){

    }

    Color(Color color){
        set(color);
    }

    public void set(int r, int g, int b) {
        R = r;
        G = g;
        B = b;
    }

    public void set(Color color) {
        R = color.R;
        G = color.G;
        B = color.B;
    }

    public Boolean equals(Color color){
        return (color.R == R && color.G == G && color.B == B);
    }

}