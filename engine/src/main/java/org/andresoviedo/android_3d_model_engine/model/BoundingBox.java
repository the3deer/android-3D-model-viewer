package org.andresoviedo.android_3d_model_engine.model;

import android.opengl.Matrix;

import java.nio.FloatBuffer;

/**
 * @author andresoviedo
 */

public final class BoundingBox {

    private final String id;
    private final float xMin;
    private final float xMax;
    private final float yMin;
    private final float yMax;
    private final float zMin;
    private final float zMax;
    private final float[] min;
    private final float[] max;

    public static BoundingBox create(String id, FloatBuffer vertexBuffer, float[] modelMatrix) {
        float xMin = Float.MAX_VALUE, xMax = -Float.MAX_VALUE, yMin = Float.MAX_VALUE, yMax = -Float.MAX_VALUE, zMin =
                Float.MAX_VALUE, zMax = -Float.MAX_VALUE;
        vertexBuffer = vertexBuffer.asReadOnlyBuffer();
        vertexBuffer.position(0);
        while (vertexBuffer.hasRemaining()) {
            float vertexx = vertexBuffer.get();
            float vertexy = vertexBuffer.get();
            float vertexz = vertexBuffer.get();
            if (vertexx < xMin) {
                xMin = vertexx;
            }
            if (vertexx > xMax) {
                xMax = vertexx;
            }
            if (vertexy < yMin) {
                yMin = vertexy;
            }
            if (vertexy > yMax) {
                yMax = vertexy;
            }
            if (vertexz < zMin) {
                zMin = vertexz;
            }
            if (vertexz > zMax) {
                zMax = vertexz;
            }
        }
        float[] min = new float[]{xMin, yMin, zMin, 1};
        float[] max = new float[]{xMax, yMax, zMax, 1};
        Matrix.multiplyMV(min,0,modelMatrix,0,min,0);
        Matrix.multiplyMV(max,0,modelMatrix,0,max,0);
        return new BoundingBox(id, min[0], max[0], min[1], max[1], min[2], max[2]);
    }

    public BoundingBox(String id, float xMin, float xMax, float yMin, float yMax, float zMin, float zMax) {
        this.id = id;
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        this.zMin = zMin;
        this.zMax = zMax;
        this.min = new float[]{xMin, yMin, zMin, 1};
        this.max = new float[]{xMax, yMax, zMax, 1};
    }

    public float[] getMin() {
        return min;
    }

    /*public float[] getCurrentMin(){
        float[] ret = new float[4];
        Matrix.multiplyMV(ret,0,getModelMatrix(),0,getMin(),0);
        return ret;
    }

    public float[] getCurrentMax(){
        float[] ret = new float[4];
        Matrix.multiplyMV(ret,0,getModelMatrix(),0,getMax(),0);
        return ret;
    }*/

    public float[] getMax() {
        return max;
    }

    public float getxMin() {
        return xMin;
    }

    public float getxMax() {
        return xMax;
    }

    public float getyMin() {
        return yMin;
    }

    public float getyMax() {
        return yMax;
    }

    public float getzMin() {
        return zMin;
    }

    public float getzMax() {
        return zMax;
    }

    public float[] getCenter() {
        return new float[]{(xMax + xMin) / 2, (yMax + yMin) / 2, (zMax + zMin) / 2};
    }

    public boolean insideBounds(float x, float y, float z) {
        return !outOfBound(x, y, z);
    }

    public boolean outOfBound(float x, float y, float z) {
        if (x > getxMax()) {
            return true;
        }
        if (x < getxMin()) {
            return true;
        }
        if (y < getyMin()) {
            return true;
        }
        if (y > getyMax()) {
            return true;
        }
        if (z < getzMin()) {
            return true;
        }
        if (z > getzMax()) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "BoundingBoxBuilder{" +
                "id='" + id + '\'' +
                ", xMin=" + xMin +
                ", xMax=" + xMax +
                ", yMin=" + yMin +
                ", yMax=" + yMax +
                ", zMin=" + zMin +
                ", zMax=" + zMax +
                '}';
    }
}