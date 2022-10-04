package org.the3deer.android_3d_model_engine.camera;

import android.opengl.Matrix;

import org.the3deer.android_3d_model_engine.model.Camera;
import org.the3deer.util.math.Math3DUtils;

public class PointOfViewCamera extends Camera {

    private final Camera delegate;

    private final float[] savePos;
    private final float[] saveUp;
    private final float[] saveView;

    public PointOfViewCamera(Camera delegate) {
        super(delegate);
        this.delegate = delegate;
        this.savePos = this.pos.clone();
        this.saveUp = this.up.clone();
        this.saveView = this.view.clone();
    }

    @Override
    public void enable(){
        delegate.setDelegate(this);
        saveAndAnimate(this.savePos[0], this.savePos[1], this.savePos[2], this.saveUp[0], this.saveUp[1], this.saveUp[2],
                this.saveView[0], this.saveView[1], this.saveView[2]);
    }

    private void save(){
        System.arraycopy(this.pos, 0, this.savePos, 0, this.pos.length);
        System.arraycopy(this.view, 0, this.saveView, 0, this.view.length);
        System.arraycopy(this.up, 0, this.saveUp, 0, this.up.length);
    }

    @Override
    public void translateCamera(float dX, float dY) {

        if (dX == 0 && dY == 0) return;

        // get current view and right
        float[] view = Math3DUtils.to4d(Math3DUtils.substract(this.view, this.pos));
        float[] right = Math3DUtils.to4d(Math3DUtils.crossProduct(view, this.up));
        if (Math3DUtils.length(right) == 0) return;

        Math3DUtils.normalize(right);

        // add deltas
        float[] rightd = Math3DUtils.multiply(right, dY);
        float[] upd = Math3DUtils.multiply(up, dX);

        // rot vectors
        float[] viewRot = Math3DUtils.add(rightd,upd);
        float length = Math3DUtils.length(viewRot);
        Math3DUtils.normalize(viewRot);

        // transform
        float[] matrixView = new float[16];
        Matrix.setIdentityM(matrixView,0);
        Matrix.translateM(matrixView,0, getxPos(), getyPos(), getzPos());
        Matrix.rotateM(matrixView, 0, -(float) Math.toDegrees(length), viewRot[0], viewRot[1], viewRot[2]);

        final float[] newView = new float[4];
        Matrix.multiplyMV(newView,0,matrixView,0, view,0);
        this.view[0] = newView[0];
        this.view[1] = newView[1];
        this.view[2] = newView[2];

        // ------------------------

        float[] matrixUp = new float[16];
        Matrix.setIdentityM(matrixUp,0);
        Matrix.rotateM(matrixUp, 0, -(float) Math.toDegrees(length), viewRot[0], viewRot[1], viewRot[2]);

        float[] newUp = new float[4];
        Matrix.multiplyMV(newUp,0,matrixUp,0, this.up,0);
        Math3DUtils.normalize(newUp);

        this.up[0] = newUp[0];
        this.up[1] = newUp[1];
        this.up[2] = newUp[2];

        delegate.setChanged(true);
    }

    public  synchronized void MoveCameraZ(float direction) {

        // First we need to get the direction at which we are looking.
        float xLookDirection, yLookDirection, zLookDirection;

        // The look direction is the view minus the position (where we are).
        xLookDirection = getxView() - pos[0];
        yLookDirection = getyView() - pos[1];
        zLookDirection = view[2] - pos[2];

        // Normalize the direction.
        float dp = Matrix.length(xLookDirection, yLookDirection, zLookDirection);
        xLookDirection /= dp;
        yLookDirection /= dp;
        zLookDirection /= dp;

        float x = pos[0] + xLookDirection * direction;
        float y = pos[1] + yLookDirection * direction;
        float z = pos[2] + zLookDirection * direction;

        if (isOutOfBounds(x, y, z)) return;

        pos[0] = x;
        pos[1] = y;
        pos[2] = z;

        view[0] += xLookDirection * direction;
        view[1] += yLookDirection * direction;
        view[2] += zLookDirection * direction;

        save();

        delegate.setChanged(true);
    }

    @Override
    public void Rotate(float angle) {

        if (angle == 0) return;

        // get current view and right
        float[] view = Math3DUtils.to4d(Math3DUtils.substract(this.view, this.pos));
        Math3DUtils.normalize(view);

        // transform
        float[] matrix = new float[16];
        Matrix.setRotateM(matrix, 0, (float) -Math.toDegrees(angle), view[0], view[1], view[2]);

        final float[] newUp = new float[4];
        Matrix.multiplyMV(newUp,0,matrix,0, this.up,0);
        this.up[0] = newUp[0];
        this.up[1] = newUp[1];
        this.up[2] = newUp[2];

        save();

        delegate.setChanged(true);
    }

    private void saveAndAnimate(float xp, float yp, float zp,
                                float xu, float yu, float zu,
                                float xv, float yv, float zv) {

            Object[] args = new Object[]{"moveTo", getxPos(), getyPos(), getzPos(), getxUp(), getyUp(), getzUp(),
                    xp, yp, zp, xu, yu, zu, getxView(), getyView(), getzView(), xv, yv, zv};

            savePos[0] = xp;
            savePos[1] = yp;
            savePos[2] = zp;
            saveUp[0] = xu;
            saveUp[1] = yu;
            saveUp[2] = zu;
            saveView[0] = xv;
            saveView[1] = yv;
            saveView[2] = zv;

            delegate.setAnimation(new CameraAnimation(delegate, args));

    }
}
