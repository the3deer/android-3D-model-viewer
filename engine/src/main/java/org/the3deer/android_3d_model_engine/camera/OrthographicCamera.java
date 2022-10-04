package org.the3deer.android_3d_model_engine.camera;

import android.util.Log;

import org.the3deer.android_3d_model_engine.model.Camera;
import org.the3deer.android_3d_model_engine.model.Constants;
import org.the3deer.util.math.Math3DUtils;

import java.util.Arrays;

public class OrthographicCamera extends Camera {

    /**
     * The distance between the origin and the orthographic coordinate
     * This should be greater than the near view so it's not clipped
     */
    public static final float UNIT = Constants.UNIT * 2f; // Constants.UNIT_SIN_3;

    private final Camera delegate;
    private boolean initialized = false;

    private final float[] savePos;
    private final float[] saveView;
    private final float[] saveUp;

    public OrthographicCamera(Camera delegate) {
        super(delegate);
        this.delegate = delegate;

        // final init
        this.savePos = this.pos.clone();
        this.saveView = new float[]{0,0,0,1};
        this.saveUp = this.up.clone();
    }

    private boolean init() {
        if (!initialized) {
            this.savePos[0] = Constants.UNIT_0;
            this.savePos[1] = Constants.UNIT_0;
            this.savePos[2] = UNIT;
            this.saveUp[0] = Constants.UNIT_0;
            this.saveUp[1] = Constants.UNIT_1;
            this.saveUp[2] = -Constants.UNIT_0;
            initialized = true;
            return true;
        }
        return false;
    }

    @Override
    public void enable() {
        init();
        delegate.setDelegate(this);
        saveAndAnimate(true, this.savePos[0], this.savePos[1], this.savePos[2], this.saveUp[0], this.saveUp[1], this.saveUp[2]);
    }

    @Override
    public synchronized void translateCamera(float dX, float dY) {

        float dXabs = Math.abs(dX);
        float dYabs = Math.abs(dY);
        if (dX < 0 && dXabs > dYabs) {  // right
            //float[] right = Math3DUtils.crossProduct(-getxPos(), -getyPos(), -getzPos(), getxUp(), getyUp(), getzUp());
            float[] right = Math3DUtils.crossProduct(-savePos[0], -savePos[1], -savePos[2], saveUp[0], saveUp[1], saveUp[2]);
            Math3DUtils.normalize(right);
            Math3DUtils.snapToGrid(right);
            saveAndAnimate(right[0] * UNIT, right[1] * UNIT, right[2] * UNIT);
        } else if (dX > 0 && dXabs > dYabs) {
            // float[] left = Math3DUtils.crossProduct(getxUp(), getyUp(), getzUp(), -getxPos(), -getyPos(), -getzPos());
            float[] left = Math3DUtils.crossProduct(saveUp[0], saveUp[1], saveUp[2],-savePos[0], -savePos[1], -savePos[2]);
            Math3DUtils.normalize(left);
            Math3DUtils.snapToGrid(left);
            saveAndAnimate(left[0] * UNIT, left[1] * UNIT, left[2] * UNIT);
        } else if (dY > 0 && dYabs > dXabs) {
            saveAndAnimate(saveUp[0] * UNIT, saveUp[1] * UNIT, saveUp[2] * UNIT);
        } else if (dY < 0 && dYabs > dXabs) {
            saveAndAnimate(-saveUp[0] * UNIT, -saveUp[1] * UNIT, -saveUp[2] * UNIT);
        }
    }

    @Override
    public synchronized void Rotate(float angle) {

        if (angle < 0) {
            float[] right = Math3DUtils.crossProduct(-savePos[0], -savePos[1], -savePos[2], saveUp[0], saveUp[1], saveUp[2]);
            Math3DUtils.normalize(right);
            Math3DUtils.snapToGrid(right);
            Log.v("OrthographicCamera", "Rotating 90 right: " + Arrays.toString(right));
            saveAndAnimate(savePos[0], savePos[1], savePos[2], right[0], right[1], right[2]);
        } else {
            float[] left = Math3DUtils.crossProduct(saveUp[0], saveUp[1], saveUp[2], -savePos[0], -savePos[1], -savePos[2]);
            Math3DUtils.normalize(left);
            Math3DUtils.snapToGrid(left);
            Log.v("OrthographicCamera", "Rotating 90 left: " + Arrays.toString(left));
            saveAndAnimate(savePos[0], savePos[1], savePos[2], left[0], left[1], left[2]);
        }
    }

    private void saveAndAnimate(float xp, float yp, float zp) {

        // UP vector must be recalculated
        // cross
        float[] right = Math3DUtils.crossProduct(-savePos[0], -savePos[1], -savePos[2],
                saveUp[0], saveUp[1], saveUp[2]);
        Math3DUtils.normalize(right);

        float[] cross = Math3DUtils.crossProduct(right[0], right[1], right[2], -xp, -yp, -zp);
        if (Math3DUtils.length(cross) > 0f){
            Math3DUtils.normalize(cross);
            Math3DUtils.snapToGrid(cross);
            saveAndAnimate(xp,yp,zp, cross[0], cross[1], cross[2]);
        } else {
            saveAndAnimate(xp,yp,zp, saveUp[0], saveUp[1], saveUp[2]);
        }
    }

    private void saveAndAnimate(float xp, float yp, float zp, float xu, float yu, float zu) {
        this.saveAndAnimate(false, xp, yp, zp, xu, yu, zu);
    }

    private void saveAndAnimate(boolean force, float xp, float yp, float zp, float xu, float yu, float zu) {

        synchronized (delegate) {
            if (delegate.getAnimation() == null || delegate.getAnimation().isFinished() || force) {




        /*delegate.setAnimation(new Object[]{"moveTo", getxPos(), getyPos(), getzPos(), getxUp(), getyUp(), getzUp(),
                savePos[0], savePos[1], savePos[2], saveUp[0], saveUp[1], saveUp[2]});*/
                Object[] args = new Object[]{"moveTo", getxPos(), getyPos(), getzPos(), getxUp(), getyUp(), getzUp(),
                        xp, yp, zp, xu, yu, zu, getxView(), getyView(), getzView(), saveView[0], saveView[1], saveView[2]};

                savePos[0] = xp;
                savePos[1] = yp;
                savePos[2] = zp;
                saveUp[0] = xu;
                saveUp[1] = yu;
                saveUp[2] = zu;

                delegate.setAnimation(new CameraAnimation(delegate, args));
            }

        }
    }
}
