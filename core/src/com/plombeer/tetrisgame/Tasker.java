package com.plombeer.tetrisgame;

/**
 * Created by hh on 12.06.2015.
 */
public interface Tasker {

    void doAfterTime();
    void onTick(float delta);
}
