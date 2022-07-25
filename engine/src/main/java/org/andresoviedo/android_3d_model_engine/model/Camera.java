package org.andresoviedo.android_3d_model_engine.model;

// http://stackoverflow.com/questions/14607640/rotating-a-vector-in-3d-space

import android.opengl.Matrix;
import android.util.Log;

import org.andresoviedo.util.math.Math3DUtils;

public class Camera {

    private final BoundingBox centerBox = new BoundingBox("scene", -Constants.ROOM_CENTER_SIZE, Constants.ROOM_CENTER_SIZE,
            -Constants.ROOM_CENTER_SIZE, Constants.ROOM_CENTER_SIZE, -Constants.ROOM_CENTER_SIZE, Constants.ROOM_CENTER_SIZE);
    private final BoundingBox roomBox = new BoundingBox("scene", -Constants.ROOM_SIZE, Constants.ROOM_SIZE,
            -Constants.ROOM_SIZE, Constants.ROOM_SIZE, -Constants.ROOM_SIZE, Constants.ROOM_SIZE);

    private Camera delegate;

    private Animation<Camera> animation;

    private final float[] buffer = new float[12 + 12 + 16 + 16];
    private boolean changed = false;

    // new vector model
	protected float[] pos = new float[]{0,0,0,1};
    protected float[] view = new float[]{0,0,0,1};
    protected float[] up = new float[]{0,0,0,1};

    // transformation matrix
    protected float[] matrix = new float[16];

    // camera mode
    private Projection projection = Projection.PERSPECTIVE;

    public Camera(float distance) {
        // Initialize variables...
        this(0, 0, distance, 0, 0, 0, 0, 1, 0);
    }

    public Camera(Camera source) {
        this.pos = source.pos;
        this.view = source.view;
        this.up = source.up;
    }

    public Camera(float xPos, float yPos, float zPos, float xView, float yView, float zView, float xUp, float yUp,
                  float zUp) {
        // Here we set the camera to the values sent in to us. This is mostly
        // used to set up a
        // default position.
        this.pos[0] = xPos;
        this.pos[1] = yPos;
        this.pos[2] = zPos;
        this.view[0] = xView;
        this.view[1] = yView;
        this.view[2] = zView;
        this.up[0] = xUp;
        this.up[1] = yUp;
        this.up[2] = zUp;
    }

    public Camera getDelegate() {
        return delegate;
    }

    public void setDelegate(Camera delegate) {
        this.delegate = delegate;
    }

    public void setAnimation(Animation<Camera> animation) {
        this.animation = animation;
    }
    public Animation<Camera> getAnimation() {
        return this.animation;
    }

    public void enable(){
    }

    public synchronized void animate() {
        if (getAnimation() != null){
            getAnimation().animate();
        }
    }

    public synchronized void MoveCameraZ(float direction) {
    }

    /**
     * Test whether specified position is either outside room "walls" or in the very center of the room.
     *
     * @param x x position
     * @param y y position
     * @param z z position
     * @return true if specified position is outside room "walls" or in the very center of the room
     */
    protected boolean isOutOfBounds(float x, float y, float z) {
        if (roomBox.outOfBound(x, y, z)) {
            Log.v("Camera", "Out of room walls. " + x + "," + y + "," + z);
            return true;
        }
        if (!centerBox.outOfBound(x, y, z)) {
            Log.v("Camera", "Inside absolute center");
            return true;
        }
        return false;
    }

    /**
     * Translation is the movement that makes the Earth around the Sun.
     * So in this context, translating the camera means moving the camera around the Zero (0,0,0)
     * <p>
     * This implementation makes uses of 3D Vectors Algebra.
     * <p>
     * The idea behind this implementation is to translate the 2D user vectors (the line in the
     * screen) with the 3D equivalents.
     * <p>
     * In order to to that, we need to calculate the Right and Arriba vectors so we have a match
     * for user 2D vector.
     *
     * @param dX the X component of the user 2D vector, that is, a value between [-1,1]
     * @param dY the Y component of the user 2D vector, that is, a value between [-1,1]
     */
    public synchronized void translateCamera(float dX, float dY) {
        if (getDelegate() != null){
            getDelegate().translateCamera(dX, dY);
        }
    }

    public boolean hasChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
        if (getDelegate()!=null) {
            getDelegate().setChanged(this.changed);
        }
    }

    @Override
    public String toString() {
        return "Camera [xPos=" + pos[0] + ", yPos=" + pos[1] + ", zPos=" + pos[2] + ", xView=" + getxView() + ", yView=" + getyView()
                + ", zView=" + view[2] + ", xUp=" + getxUp() + ", yUp=" + getyUp() + ", zUp=" + getzUp() + "]";
    }

    public synchronized void Rotate(float angle) {
    }

    public Camera[] toStereo(float eyeSeparation) {

        // look vector
        float xLook = getxView() - pos[0];
        float yLook = getyView() - pos[1];
        float zLook = view[2] - pos[2];

        // right vector
        float[] crossRight = Math3DUtils.crossProduct(xLook, yLook, zLook, getxUp(), getyUp(), getzUp());
        Math3DUtils.normalize(crossRight);

        // new left pos
        float xPosLeft = pos[0] - crossRight[0] * eyeSeparation / 2;
        float yPosLeft = pos[1] - crossRight[1] * eyeSeparation / 2;
        float zPosLeft = pos[2] - crossRight[2] * eyeSeparation / 2;
        float xViewLeft = getxView() - crossRight[0] * eyeSeparation / 2;
        float yViewLeft = getyView() - crossRight[1] * eyeSeparation / 2;
        float zViewLeft = view[2] - crossRight[2] * eyeSeparation / 2;

        // new right pos
        float xPosRight = pos[0] + crossRight[0] * eyeSeparation / 2;
        float yPosRight = pos[1] + crossRight[1] * eyeSeparation / 2;
        float zPosRight = pos[2] + crossRight[2] * eyeSeparation / 2;
        float xViewRight = getxView() + crossRight[0] * eyeSeparation / 2;
        float yViewRight = getyView() + crossRight[1] * eyeSeparation / 2;
        float zViewRight = view[2] + crossRight[2] * eyeSeparation / 2;

        //xViewLeft = getxView();
        //yViewLeft = getyView();
        //zViewLeft = view[2];

        //xViewRight = getxView();
        //yViewRight = getyView();
        //zViewRight = view[2];


        Camera left = new Camera(xPosLeft, yPosLeft, zPosLeft, xViewLeft, yViewLeft, zViewLeft, getxUp(), getyUp(), getzUp());
        Camera right = new Camera(xPosRight, yPosRight, zPosRight, xViewRight, yViewRight, zViewRight, getxUp(), getyUp(), getzUp());

        return new Camera[]{left, right};
    }

    public float getxView() {
        return view[0];
    }

    public float getyView() {
        return view[1];
    }


    public float getzView() {
        return view[2];
    }

    public float getxUp() {
        return up[0];
    }

    public float getyUp() {
        return up[1];
    }

    public float getzUp() {
        return up[2];
    }

    public float getxPos() {
        return pos[0];
    }

    public float getyPos() {
        return pos[1];
    }

    public float getzPos() {
        return pos[2];
    }

    public void set(float xPos, float yPos, float zPos, float xView, float yView, float zView, float xUp, float yUp,
                    float zUp) {

        // check that we have valid coordinates


        // Here we set the camera to the values sent in to us. This is mostly
        // used to set up a
        // default position.
        this.pos[0] = xPos;
        this.pos[1] = yPos;
        this.pos[2] = zPos;
        this.view[0] = xView;
        this.view[1] = yView;
        this.view[2] = zView;
        this.up[0] = xUp;
        this.up[1] = yUp;
        this.up[2] = zUp;
        setChanged(true);
    }

    public void set(float[] pos, float[] view, float[] up){
        this.set(pos[0], pos[1], pos[2],view[0], view[1], view[2], up[0],up[1], up[2]);
    }

    public Projection getProjection() {
        return projection;
    }

    public void setProjection(Projection projection) {
        this.projection = projection;
    }

    public float getDistance() {
        return Math3DUtils.length(this.pos);
    }

    public float[] getPos() {
        return this.pos;
    }

    public float[] getRight() {
        return Math3DUtils.normalize2(Math3DUtils.crossProduct(this.up, this.pos));
    }

    public float[] getUp() {
        return this.up;
    }

    public float[] getView() {
        return this.view;
    }

    // cellphone orientation
    private int orientation;
    /**
     * Rotate using the current view vector
     *
     * @param angle angle in degrees
     */
    public void setOrientation(int angle) {

        if (angle == this.orientation) return;
        else if (Math.abs(this.orientation-angle)<5) return;
            else{
            int previous = this.orientation;
            this.orientation = angle;
            angle = previous - angle;
        }

        float dX = (float) Math.sin(Math.toRadians(angle));
        float dY = (float) Math.cos(Math.toRadians(angle));
        float[] right = Math3DUtils.to4d(getRight());

        // screen orientation vector
        /*float[] ovector1 = Math3DUtils.multiply(up, dX);
        float[] ovector2 = Math3DUtils.multiply(getRight(), -dY);
        float[] ovector = Math3DUtils.add(ovector1,ovector2);
        Math3DUtils.normalize(ovector);*/

        // rotation matrix
        float[] matrix = new float[16];
        Matrix.setIdentityM(matrix,0);
        Matrix.setRotateM(matrix,0, angle, getxPos(),getyPos(),getzPos());

        float[] newUp = new float[4];
        Matrix.multiplyMV(newUp,0,matrix,0,up,0);
        Math3DUtils.normalize(newUp);

        this.up[0] = newUp[0];
        this.up[1] = newUp[1];
        this.up[2] = newUp[2];

        setChanged(true);

        /*float rota = -(float) Math.tan(angle-180)/5f; //-angle / 90f;

        float dot = Math.abs(Math3DUtils.dotProduct(up, newUp));
        if (Math.abs(dot) < 0.1f){
            Log.v("Camera","angle: "+angle+", rot:"+rota+", dx:"+dX+", dy:"+dY+" HIT! "+Math3DUtils.dotProduct(up,newUp));
            return;
        } else {
            Log.v("Camera","angle: "+angle+", rot:"+rota+", dx:"+dX+", dy:"+dY+" DOT! "+Math3DUtils.dotProduct(up,newUp));
            //return;
        }

        Matrix.setIdentityM(matrix,0);
        Matrix.setRotateM(matrix,0, -angle/90f, getxPos(),getyPos(),getzPos());
        Matrix.multiplyMV(newUp,0,matrix,0,up,0);
        Math3DUtils.normalize(newUp);

        this.up[0] = newUp[0];
        this.up[1] = newUp[1];
        this.up[2] = newUp[2];
        setChanged(true);*/
    }

    public float[] getMatrix() {
        Matrix.setLookAtM(this.matrix,0,getxPos(),getyPos(),getzPos(),
                getxView(),getyView(),getzView(),getxUp(),getyUp(),getzUp());
        return matrix;
    }
}
