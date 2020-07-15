package org.andresoviedo.android_3d_model_engine.model;

import org.andresoviedo.util.math.Math3DUtils;

import java.text.DecimalFormat;
import java.util.Arrays;

public class Dimensions {

    // edge coordinates
    private float leftPt = Float.MAX_VALUE, rightPt = -Float.MAX_VALUE; // on x-axis
    private float topPt = -Float.MAX_VALUE, bottomPt = Float.MAX_VALUE; // on y-axis
    private float farPt = Float.MAX_VALUE, nearPt = -Float.MAX_VALUE; // on z-axis

    // min max center
    private final float[] center = new float[]{0,0,0};
    private final float[] min = new float[]{0,0,0};
    private final float[] max = new float[]{0,0,0};

    // whether at least 1 vertex was processed
    private boolean initialized = false;

    // for reporting
    private static final DecimalFormat df = new DecimalFormat("0.##"); // 2 dp

    public Dimensions() {
        //();
    }

    public Dimensions(float leftPt, float rightPt, float topPt, float bottomPt, float nearPt, float farPt) {
        this.leftPt = leftPt;
        this.rightPt = rightPt;
        this.topPt = topPt;
        this.bottomPt = bottomPt;
        this.nearPt = nearPt;
        this.farPt = farPt;
        refresh();
    }

    public float[] getMin() {
        return min;
    }

    public float[] getMax() {
        return max;
    }

    public void update(float x, float y, float z){
        if (x > rightPt)
            rightPt = x;
        if (x < leftPt)
            leftPt = x;

        if (y > topPt)
            topPt = y;
        if (y < bottomPt)
            bottomPt = y;

        if (z > nearPt)
            nearPt = z;
        if (z < farPt)
            farPt = z;

        refresh();
    }

    private void refresh() {
        this.min[0] = getLeftPt();
        this.min[1] = getBottomPt();
        this.min[2] = getFarPt();

        this.max[0] = getRightPt();
        this.max[1] = getTopPt();
        this.max[2] = getNearPt();

        this.center[0] = (getRightPt() + getLeftPt()) / 2.0f;
        this.center[1] = (getTopPt() + getBottomPt()) / 2.0f;
        this.center[2] = (getNearPt() + getFarPt()) / 2.0f;

        initialized = true;
    }

    // ------------- use the edge coordinates ----------------------------

    public float getWidth() {
        return Math.abs(getRightPt() - getLeftPt());
    }

    public float getHeight() {
        return Math.abs(getTopPt() - getBottomPt());
    }

    public float getDepth() {
        return Math.abs(getNearPt() - getFarPt());
    }

    public float getLargest() {
        float height = getHeight();
        float depth = getDepth();

        float largest = getWidth();
        if (height > largest)
            largest = height;
        if (depth > largest)
            largest = depth;

        return largest;
    }

    private float getRightPt(){
        if (!initialized) return 0;
        return rightPt;
    }

    private float getLeftPt(){
        if (!initialized) return 0;
        return leftPt;
    }

    private float getTopPt(){
        if (!initialized) return 0;
        return topPt;
    }

    private float getBottomPt(){
        if (!initialized) return 0;
        return bottomPt;
    }

    private float getNearPt(){
        if (!initialized) return 0;
        return nearPt;
    }

    private float getFarPt(){
        if (!initialized) return 0;
        return farPt;
    }

    /**
     * @return the center of the bounding box
     */
    public float[] getCenter() {
        return center;
    }

    public float[] getCornerLeftTopNearVector() {
        return new float[]{getLeftPt(), getTopPt(), getNearPt(), 1};
    }

    public float[] getCornerRightBottomFar() {
        return new float[]{getRightPt(), getBottomPt(), getFarPt(), 1};
    }

    public Dimensions translate(float[] diff) {
        return new Dimensions(leftPt+diff[0],rightPt+diff[0],
                topPt+diff[1],bottomPt+diff[1],
                nearPt+diff[2],farPt+diff[2]);
    }

    public Dimensions scale(float scale){
        return new Dimensions(leftPt*scale,rightPt*scale,
                topPt*scale,bottomPt*scale,
                nearPt*scale,farPt*scale);
    }

    @Override
    public String toString() {
        return "Dimensions{" +
                "min=" + Arrays.toString(min) +
                ", max=" + Arrays.toString(max) +
                ", center=" + Arrays.toString(center) +
                ", width=" + getWidth()+
                ", height="+ getHeight()+
                ", depth="+getDepth()+
                '}';
    }

    public float getRelationTo(Dimensions other) {
        return this.getLargest() / other.getLargest();
    }


}
