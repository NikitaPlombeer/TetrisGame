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

    /**
     *
     * @param enabled
     * Включен таймер изначально или нет
     *
     * @param interval
     * Количество секунд работы таймера
     */
    public MiniTimer(boolean enabled, float interval) {
        this.enabled = enabled;
        this.interval = interval;
    }

    public Tasker getTasker() {
        return tasker;
    }

    /**
     * Вызывается после каждого update дисплея
     * @param delta
     * количество секунд, прошедшее с отображения последнего кадра
     */
    void tick(float delta){
        if(enabled){
            time += delta;
            if(time >= interval){
                if(tasker != null) {
                    disabled();
                    tasker.doAfterTime();
                }
                time = 0f;
            } else
            if(tasker != null){
                tasker.onTick(delta);
            }
        }

    }

    /**
     *
     * @return включен таймер или нет
     */
    public boolean isEnabled() {
        return enabled;
    }

    public void setTasker(Tasker tasker) {
        this.tasker = tasker;
    }

    /**
     * Включить таймер
     */
    public void enabled(){
        enabled = true;
    }

    /**
     * Выключить таймер
     */
    public void disabled(){
        enabled = false;
    }
}
