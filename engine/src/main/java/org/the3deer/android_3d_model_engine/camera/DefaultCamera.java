package org.the3deer.android_3d_model_engine.camera;

import android.opengl.Matrix;
import android.util.Log;

import org.the3deer.android_3d_model_engine.model.Camera;
import org.the3deer.util.math.Math3DUtils;

public class DefaultCamera extends Camera {

    private final Camera delegate;

    private final float[] savePos;
    private final float[] saveView;
    private final float[] saveUp;

    public DefaultCamera(Camera delegate) {
        super(delegate);
        this.delegate = delegate;
        this.savePos = this.pos.clone();
        this.saveView = new float[]{0,0,0,0};
        this.saveUp = this.up.clone();
    }

    @Override
    public void enable(){
        delegate.setDelegate(this);
        delegate.setAnimation(new CameraAnimation(delegate, new Object[]{"moveTo", getxPos(), getyPos(), getzPos(), getxUp(), getyUp(), getzUp(),
                this.savePos[0], this.savePos[1], this.savePos[2], this.saveUp[0], this.saveUp[1], this.saveUp[2],
                this.getxView(), this.getyView(), this.getzView(), this.saveView[0], this.saveView[1], this.saveView[2]}));
    }

    private void save(){
        System.arraycopy(this.pos, 0, this.savePos, 0, this.pos.length);
        System.arraycopy(this.up, 0, this.saveUp, 0, this.up.length);
    }

    @Override
    public void translateCamera(float dX, float dY) {
        translateCameraImpl(dX, dY);
    }

    private void translateCameraImpl(float dX, float dY) {
        float vlen;

        // Translating the camera requires a directional vector to rotate
        // First we need to get the direction at which we are looking.
        // The look direction is the view minus the position (where we are).
        // Get the Direction of the view.
        float[] look = Math3DUtils.substract(view,pos);
        Math3DUtils.normalize(look);
        float xLook, yLook, zLook;
        xLook = look[0];
        yLook = look[1];
        zLook = look[2];

        // Arriba is the 3D vector that is **almost** equivalent to the 2D user Y vector
        // Get the direction of the up vector
        float[] arriba = up.clone(); //Math3DUtils.substract(up,pos);
        Math3DUtils.normalize(arriba);
        float xArriba, yArriba, zArriba;
        xArriba = arriba[0];
        yArriba = arriba[1];
        zArriba = arriba[2];

        // Right is the 3D vector that is equivalent to the 2D user X vector
        // In order to calculate the Right vector, we have to calculate the cross product of the
        // previously calculated vectors...

        // The cross product is defined like:
        // A x B = (a1, a2, a3) x (b1, b2, b3) = (a2 * b3 - b2 * a3 , - a1 * b3 + b1 * a3 , a1 * b2 - b1 * a2)
        float[] right = Math3DUtils.crossProduct(up, pos);
        Math3DUtils.normalize(right);

        float xRight, yRight, zRight;
        xRight = (yLook * zArriba) - (zLook * yArriba);
        yRight = (zLook * xArriba) - (xLook * zArriba);
        zRight = (xLook * yArriba) - (yLook * xArriba);
        // Normalize the Right.
        vlen = Matrix.length(xRight, yRight, zRight);
        xRight /= vlen;
        yRight /= vlen;
        zRight /= vlen;

        // Once we have the Look & Right vector, we can recalculate where is the final Arriba vector,
        // so its equivalent to the user 2D Y vector.
        xArriba = (yRight * zLook) - (zRight * yLook);
        yArriba = (zRight * xLook) - (xRight * zLook);
        zArriba = (xRight * yLook) - (yRight * xLook);
        // Normalize the Right.
        vlen = Matrix.length(xArriba, yArriba, zArriba);
        xArriba /= vlen;
        yArriba /= vlen;
        zArriba /= vlen;

        // coordinates = new float[] { pos[0], pos[1], pos[2], 1, xView, yView, zView, 1, xUp, yUp, zUp, 1 };
        final float[] coordinates = new float[12];
        coordinates[0] = getxPos();
        coordinates[1] = getyPos();
        coordinates[2] = getzPos();
        coordinates[3] = 1;
        coordinates[4] = getxView();
        coordinates[5] = getyView();
        coordinates[6] = getzView();
        coordinates[7] = 1;
        coordinates[8] = getxUp();
        coordinates[9] = getyUp();
        coordinates[10] = getzUp();
        coordinates[11] = 1;

        final float[] buffer = new float[16];

        if (dX != 0 && dY != 0) {

            // in this case the user is drawing a diagonal line:    \v     ^\    v/     /^
            // so, we have to calculate the perpendicular vector of that diagonal

            // The perpendicular vector is calculated by inverting the X/Y values
            // We multiply the initial Right and Arriba vectors by the User's 2D vector
            xRight *= dY;
            yRight *= dY;
            zRight *= dY;
            xArriba *= dX;
            yArriba *= dX;
            zArriba *= dX;

            float[] rightd = Math3DUtils.multiply(right, dY);
            float[] upd = Math3DUtils.multiply(up, dX);
            float[] rot = Math3DUtils.add(rightd,upd);
            float length = Math3DUtils.length(rot);
            Math3DUtils.normalize(rot);

            // in this case we use the vlen angle because the diagonal is not perpendicular
            // to the initial Right and Arriba vectors
            Math3DUtils.createRotationMatrixAroundVector(buffer, 0, length, rot[0], rot[1], rot[2]);
        } else if (dX != 0) {
            // in this case the user is drawing an horizontal line: <-- ó -->
            Math3DUtils.createRotationMatrixAroundVector(buffer, 0, dX, xArriba, yArriba, zArriba);
        } else {
            // in this case the user is drawing a vertical line: |^  v|
            Math3DUtils.createRotationMatrixAroundVector(buffer, 0, dY, xRight, yRight, zRight);
        }

        float[] newBuffer = new float[12];
        Math3DUtils.multiplyMMV(newBuffer, 0, buffer, 0, coordinates, 0);

        if (isOutOfBounds(newBuffer[0], newBuffer[1], newBuffer[2])) return;

        pos[0] = newBuffer[0];
        pos[1] = newBuffer[1];
        pos[2] = newBuffer[2];
        //view[0] = newBuffer[4];
        //view[1] = newBuffer[4 + 1];
        //view[2] = newBuffer[4 + 2];
        up[0] = newBuffer[8];
        up[1] = newBuffer[8 + 1];
        up[2] = newBuffer[8 + 2];
        Math3DUtils.normalize(up);

        delegate.setChanged(true);
        save();
    }

    public  synchronized void MoveCameraZ(float direction) {
        //if (true) return;
        // Moving the camera requires a little more then adding 1 to the z or
        // subracting 1.
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

        save();

        delegate.setChanged(true);
    }

    @Override
    public synchronized void Rotate(float angle) {

        if (angle == 0 || Float.isNaN(angle)) {
            Log.w("DefaultCamera", "NaN");
            return;
        }
        float xLook = getxView() - pos[0];
        float yLook = getyView() - pos[1];
        float zLook = view[2] - pos[2];
        float vlen = Matrix.length(xLook, yLook, zLook);
        xLook /= vlen;
        yLook /= vlen;
        zLook /= vlen;

        final float[] buffer = new float[16];
        Math3DUtils.createRotationMatrixAroundVector(buffer, 0, angle, xLook, yLook, zLook);
        // float[] coordinates = new float[] { xPos, pos[1], pos[2], 1, xView, yView, zView, 1, xUp, yUp, zUp, 1 };

        final float[] coordinates= new float[12];

        coordinates[0] = pos[0];
        coordinates[1] = pos[1];
        coordinates[2] = pos[2];
        coordinates[3] = 1;
        coordinates[4] = getxView();
        coordinates[5] = getyView();
        coordinates[6] = view[2];
        coordinates[7] = 1;
        coordinates[8] = getxUp();
        coordinates[9] = getyUp();
        coordinates[10] = getzUp();
        coordinates[11] = 1;

        float[] newBuffer = new float[16];
        Math3DUtils.multiplyMMV(newBuffer, 0, buffer, 0, coordinates, 0);

        pos[0] = newBuffer[0];
        pos[1] = newBuffer[1];
        pos[2] = newBuffer[2];
        //view[0] = buffer[4];
        //view[1] = buffer[4 + 1];
        //view[2] = buffer[4 + 2];
        up[0] = newBuffer[8];
        up[1] = newBuffer[8 + 1];
        up[2] = newBuffer[8 + 2];

        delegate.setChanged(true);
        save();
    }
}
