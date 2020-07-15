package org.andresoviedo.android_3d_model_engine.model;

public final class Transform {

    private final float[] scale;
    private final float[] rotation;
    private final float[] location;

    public Transform(float[] scale, float[] rotation, float[] location) {
        this.scale = scale;
        this.rotation = rotation;
        this.location = location;
    }

    public float[] getScale() {
        return scale;
    }

    public float[] getRotation() {
        return rotation;
    }

    public float[] getLocation() {
        return location;
    }
}
