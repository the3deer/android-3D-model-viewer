package org.andresoviedo.android_3d_model_engine.collision;

import org.andresoviedo.android_3d_model_engine.model.Object3DData;

import java.util.EventObject;

public class CollisionEvent extends EventObject {

    private final Object3DData object;
    private final float x;
    private final float y;
    private final Object3DData point;

    public CollisionEvent(Object source, Object3DData object, float x, float y, Object3DData point) {
        super(source);
        this.object = object;
        this.x = x;
        this.y = y;
        this.point = point;
    }

    public Object3DData getObject() {
        return object;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Object3DData getPoint() {
        return point;
    }
}
