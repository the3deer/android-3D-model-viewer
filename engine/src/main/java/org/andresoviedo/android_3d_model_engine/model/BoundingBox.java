package org.andresoviedo.android_3d_model_engine.model;

import android.opengl.Matrix;

import org.andresoviedo.util.math.Math3DUtils;

/**
 * @author andresoviedo
 */
public final class BoundingBox {

    private final String id;
    private final float[] min;
    private final float[] max;

    // dynamic bounding box
    private final float[] modelMatrix;
    private final float[] actualMin;
    private final float[] actualMax;

    public BoundingBox(String id, float xMin, float xMax, float yMin, float yMax, float zMin, float zMax) {
        this(id, new float[]{xMin, yMin, zMin, 1}, new float[]{xMax, yMax, zMax, 1}, Math3DUtils.IDENTITY_MATRIX);
    }

    public static BoundingBox create(String id, Dimensions d, float[] modelMatrix) {
        return new BoundingBox(id, d.getMin(), d.getMax(), modelMatrix);
    }

    private BoundingBox(String id, float min[], float max[], float[] modelMatrix) {
        this.id = id;
        this.min = new float[]{min[0],min[1],min[2],1};
        this.max = new float[]{max[0],max[1],max[2],1};
        this.modelMatrix = modelMatrix;
        this.actualMin = new float[4];
        this.actualMax = new float[4];
        refresh();
    }

    private void refresh(){
        Matrix.multiplyMV(actualMin,0,modelMatrix,0,this.min,0);
        Matrix.multiplyMV(actualMax,0,modelMatrix,0,this.max,0);
    }

    public float[] getMin() {
        return actualMin;
    }

    public float[] getMax() {
        return actualMax;
    }

    public float getxMin() {
        return actualMin[0];
    }

    public float getxMax() {
        return actualMax[0];
    }

    public float getyMin() {
        return actualMin[1];
    }

    public float getyMax() {
        return actualMax[1];
    }

    public float getzMin() {
        return actualMin[2];
    }

    public float getzMax() {
        return actualMax[2];
    }

    public boolean insideBounds(float x, float y, float z) {
        return !outOfBound(x, y, z);
    }

    public boolean outOfBound(float x, float y, float z) {
        return x > getxMax() || x < getxMin() || y < getyMin() || y > getyMax() || z < getzMin() || z > getzMax();
    }

    @Override
    public String toString() {
        return "BoundingBox{" +
                "id='" + id + '\'' +
                ", xMin=" + getxMin() +
                ", xMax=" + getxMax() +
                ", yMin=" + getyMin() +
                ", yMax=" + getyMax() +
                ", zMin=" + getzMin() +
                ", zMax=" + getzMax() +
                '}';
    }
}