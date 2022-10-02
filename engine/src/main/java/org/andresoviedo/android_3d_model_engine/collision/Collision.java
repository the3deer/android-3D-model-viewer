package org.andresoviedo.android_3d_model_engine.collision;

import org.andresoviedo.android_3d_model_engine.model.Triangle;

import java.util.Arrays;

/**
 * Collision between a ray and an object
 */
public final class Collision {

    private final float distance;
    private final float[] point;
    private final Triangle triangle;


    public Collision(float distance, float[] point, Triangle triangle) {
        this.distance = distance;
        this.point = point;
        this.triangle = triangle;
    }

    public float getDistance() {
        return distance;
    }

    public float[] getPoint() {
        return point;
    }

    public Triangle getTriangle() {
        return triangle;
    }

    @Override
    public String toString() {
        return "Collision{" +
                "distance=" + distance +
                ", position=" + Arrays.toString(point) +
                ", triangle=" + triangle +
                '}';
    }
}
