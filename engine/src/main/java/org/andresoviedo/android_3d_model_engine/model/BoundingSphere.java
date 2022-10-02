package org.andresoviedo.android_3d_model_engine.model;

import android.opengl.Matrix;

import org.andresoviedo.util.math.Math3DUtils;

/**
 * @author andresoviedo
 */
public final class BoundingSphere {

    private final String id;
    private final float[] location;
    private final double radius;
    private final double radius2;

    public BoundingSphere(String id, float[] location, double radius) {
        this.id = id;
        this.location = location;
        this.radius = radius;
        this.radius2 = radius * radius;
    }

    public final boolean insideBounds(float[] point) {
        // we are using multiplications because is faster than calling Math.pow
        float x = point[0] - location[0];
        float y = point[1] - location[1];
        float z = point[2] - location[2];
        final double distance = (x * x + y * y + z * z);
        return distance < radius2;
    }
}