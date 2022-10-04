package org.the3deer.android_3d_model_engine.controller;

import java.util.EventObject;

public class TouchEvent extends EventObject {

    public enum Action {CLICK, MOVE, PINCH, ROTATE, SPREAD}

    public static final Action CLICK = Action.CLICK;
    public static final Action MOVE = Action.MOVE;
    public static final Action PINCH = Action.PINCH;
    public static final Action SPREAD = Action.SPREAD;
    public static final Action ROTATE = Action.ROTATE;

    private final int width;
    private final int height;
    private final Action action;
    private final float x;
    private final float y;
    private final float x2;
    private final float y2;
    private final float dX;
    private final float dY;
    private final float zoom;
    private final float angle;

    TouchEvent(Object source, Action action, int width, int height, float x, float y) {
        this(source, action, width, height, x, y, 0, 0, 0, 0, 0, 0f);
    }

    TouchEvent(Object source, Action action, int width, int height, float x, float y,
               float x2, float y2, float dX,
               float dY, float zoom, float angle) {
        super(source);
        this.width = width;
        this.height = height;
        this.action = action;
        this.x = x;
        this.y = y;
        this.dX = dX;
        this.dY = dY;
        this.x2 = x2;
        this.y2 = y2;
        this.zoom = zoom;
        this.angle = angle;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


    public Action getAction() {
        return action;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getX2() {
        return x2;
    }

    public float getY2() {
        return y2;
    }

    public float getdX() {
        return dX;
    }

    public float getdY() {
        return dY;
    }

    public float getZoom() {
        return zoom;
    }

    public float getAngle() {
        return angle;
    }

    public float getLength() {
        return (float) Math.sqrt(dX * dX + dY * dY);
    }

    @Override
    public String toString() {
        return "TouchEvent{" +
                "action=" + action +
                ", x=" + x +
                ", y=" + y +
                ", dX=" + dX +
                ", dY=" + dY +
                '}';
    }
}
