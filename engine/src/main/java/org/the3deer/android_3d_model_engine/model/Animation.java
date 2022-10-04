package org.the3deer.android_3d_model_engine.model;

public class Animation<T> {

    private final T target;
    protected boolean finished;

    public Animation(T target) {
        this.target = target;
    }

    public void animate(){
        finished = true;
    }

    public T getTarget() {
        return target;
    }

    public boolean isFinished() {
        return finished;
    }
}
