package com.plombeer.tetrisgame;

/**
 * Created by hh on 12.06.2015.
 */
public class MiniTimer {

    private boolean enabled;
    private float time;
    private float interval;
    private Tasker tasker;


    public float getInterval() {
        return interval;
    }

    public MiniTimer(boolean enabled, float time, float interval) {
        this.enabled = enabled;
        this.time = time;
        this.interval = interval;
    }

    void tick(float delta){
        if(enabled){
            time += delta;
            if(time >= interval){
                if(tasker != null) {
                    tasker.doAfterTime();
                }
                time = 0f;
            } else
            if(tasker != null){
                tasker.onTick(delta);
            }
        }

    }

    public void setTasker(Tasker tasker) {
        this.tasker = tasker;
    }

    public void enabled(){
        enabled = true;
    }

    public void disabled(){
        enabled = false;
    }
}
