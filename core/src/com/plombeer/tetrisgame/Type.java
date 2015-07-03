package com.plombeer.tetrisgame;

/**
 * Created by hh on 03.03.2015.
 */
public class Type {
    public int W;
    public int H;
    boolean figs[][];
    Type(int W, int H, boolean figs[][]){
        this.H = H;
        this.W = W;
        this.figs = figs;
    }

}