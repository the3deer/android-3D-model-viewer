package org.andresoviedo.android_3d_model_engine.model;

// http://stackoverflow.com/questions/14607640/rotating-a-vector-in-3d-space

import android.opengl.Matrix;
import android.util.Log;

import org.andresoviedo.util.math.Math3DUtils;

public class Camera {

    private static final float ROOM_CENTER_SIZE = 0.5f;
	private static final float ROOM_SIZE = 30;

	public float xPos, yPos; // Camera position.
	public float zPos;
	public float xView, yView, zView; // Look at position.
	public float xUp, yUp, zUp; // Up direction.

    private final BoundingBox centerBox = new BoundingBox("scene",-ROOM_CENTER_SIZE, ROOM_CENTER_SIZE,
            -ROOM_CENTER_SIZE, ROOM_CENTER_SIZE,-ROOM_CENTER_SIZE, ROOM_CENTER_SIZE);
	private final BoundingBox roomBox = new BoundingBox("scene",-ROOM_SIZE, ROOM_SIZE,
            -ROOM_SIZE, ROOM_SIZE,-ROOM_SIZE, ROOM_SIZE);

	private float[] buffer = new float[12 + 12 + 16 + 16];
	private long animationCounter;
	private Object[] lastAction;
	private boolean changed = false;

	public Camera() {
		// Initialize variables...
		this(0, 0, 6, 0, 0, 0, 0, 1, 0);
	}

	public Camera(float xPos, float yPos, float zPos, float xView, float yView, float zView, float xUp, float yUp,
			float zUp) {
		// Here we set the camera to the values sent in to us. This is mostly
		// used to set up a
		// default position.
		this.xPos = xPos;
		this.yPos = yPos;
		this.zPos = zPos;
		this.xView = xView;
		this.yView = yView;
		this.zView = zView;
		this.xUp = xUp;
		this.yUp = yUp;
		this.zUp = zUp;
	}

	public synchronized void animate(){
		if (lastAction == null || animationCounter == 0){
			lastAction = null;
			animationCounter = 100;
			return;
		}
		String method = (String) lastAction[0];
		if (method.equals("translate")){
			float dX = (Float) lastAction[1];
			float dY = (Float) lastAction[2];
			translateCameraImpl(dX*animationCounter/100, dY*animationCounter/100);
		} else if (method.equals("rotate")){
			float rotZ = (Float)lastAction[1];
			RotateImpl(rotZ/100*animationCounter);
		}
		animationCounter--;
	}

	public synchronized void MoveCameraZ(float direction){
		if (direction == 0) return;
		MoveCameraZImpl(direction);
		lastAction = new Object[]{"zoom",direction};
	}
	private void MoveCameraZImpl(float direction) {
		// Moving the camera requires a little more then adding 1 to the z or
		// subracting 1.
		// First we need to get the direction at which we are looking.
		float xLookDirection, yLookDirection, zLookDirection;

		// The look direction is the view minus the position (where we are).
		xLookDirection = xView - xPos;
		yLookDirection = yView - yPos;
		zLookDirection = zView - zPos;

		// Normalize the direction.
		float dp = Matrix.length(xLookDirection, yLookDirection, zLookDirection);
		xLookDirection /= dp;
		yLookDirection /= dp;
		zLookDirection /= dp;

        float x = xPos + xLookDirection * direction;
        float y = yPos + yLookDirection * direction;
        float z = zPos + zLookDirection * direction;

        if (isOutOfBounds(x, y , z)) return;

        xPos = x;
        yPos = y;
        zPos = z;

        setChanged(true);
	}

    /**
     * Test whether specified position is either outside room "walls" or in the very center of the room.
     * @param x x position
     * @param y y position
     * @param z z position
     * @return true if specified position is outside room "walls" or in the very center of the room
     */
	private boolean isOutOfBounds(float x, float y, float z) {
		if (roomBox.outOfBound(x,y,z)){
			Log.i("Camera", "Out of room walls");
			return true;
		}
        if (!centerBox.outOfBound(x,y,z)){
            Log.i("Camera", "Inside absolute center");
            return true;
        }
		return false;
	}

	/**
	 * Translation is the movement that makes the Earth around the Sun.
	 * So in this context, translating the camera means moving the camera around the Zero (0,0,0)
	 *
	 * This implementation makes uses of 3D Vectors Algebra.
	 *
	 * The idea behind this implementation is to translate the 2D user vectors (the line in the
	 * screen) with the 3D equivalents.
	 *
	 * In order to to that, we need to calculate the Right and Arriba vectors so we have a match
	 * for user 2D vector.
	 *
	 * @param dX the X component of the user 2D vector, that is, a value between [-1,1]
	 * @param dY the Y component of the user 2D vector, that is, a value between [-1,1]
	 */
	public synchronized void translateCamera(float dX, float dY) {
		//Log.v("Camera","translate:"+dX+","+dY);
		if (dX == 0 && dY == 0) return;
		translateCameraImpl(dX, dY);
		lastAction = new Object[]{"translate",dX, dY};
	}

	private void translateCameraImpl(float dX, float dY) {
		float vlen;

		// Translating the camera requires a directional vector to rotate
		// First we need to get the direction at which we are looking.
		// The look direction is the view minus the position (where we are).
		// Get the Direction of the view.
		float xLook, yLook, zLook;
		xLook = xView - xPos;
		yLook = yView - yPos;
		zLook = zView - zPos;
		vlen = Matrix.length(xLook, yLook, zLook);
		xLook /= vlen;
		yLook /= vlen;
		zLook /= vlen;

		// Arriba is the 3D vector that is **almost** equivalent to the 2D user Y vector
		// Get the direction of the up vector
		float xArriba, yArriba, zArriba;
		xArriba = xUp - xPos;
		yArriba = yUp - yPos;
		zArriba = zUp - zPos;
		// Normalize the Right.
		vlen = Matrix.length(xArriba, yArriba, zArriba);
		xArriba /= vlen;
		yArriba /= vlen;
		zArriba /= vlen;

		// Right is the 3D vector that is equivalent to the 2D user X vector
		// In order to calculate the Right vector, we have to calculate the cross product of the
		// previously calculated vectors...

		// The cross product is defined like:
		// A x B = (a1, a2, a3) x (b1, b2, b3) = (a2 * b3 - b2 * a3 , - a1 * b3 + b1 * a3 , a1 * b2 - b1 * a2)
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

		float[] coordinates = new float[] { xPos, yPos, zPos, 1, xView, yView, zView, 1, xUp, yUp, zUp, 1 };

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

			// Then we add the 2 affected vectors to the the final rotation vector
			float rotX, rotY, rotZ;
			rotX = xRight + xArriba;
			rotY = yRight + yArriba;
			rotZ = zRight + zArriba;
			vlen = Matrix.length(rotX, rotY, rotZ);
			rotX /= vlen;
			rotY /= vlen;
			rotZ /= vlen;

			// in this case we use the vlen angle because the diagonal is not perpendicular
			// to the initial Right and Arriba vectors
			createRotationMatrixAroundVector(buffer, 24, vlen, rotX, rotY, rotZ);
		}
		else if (dX != 0){
			// in this case the user is drawing an horizontal line: <-- ó -->
			createRotationMatrixAroundVector(buffer, 24, dX, xArriba, yArriba, zArriba);
		}
		else{
			// in this case the user is drawing a vertical line: |^  v|
			createRotationMatrixAroundVector(buffer, 24, dY, xRight, yRight, zRight);
		}
		multiplyMMV(buffer, 0, buffer, 24, coordinates, 0);

		if (isOutOfBounds(buffer[0], buffer[1], buffer[2])) return;

		xPos = buffer[0] / buffer[3];
		yPos = buffer[1] / buffer[3];
		zPos = buffer[2] / buffer[3];
		xView = buffer[4] / buffer[4 + 3];
		yView = buffer[4 + 1] / buffer[4 + 3];
		zView = buffer[4 + 2] / buffer[4 + 3];
		xUp = buffer[8] / buffer[8 + 3];
		yUp = buffer[8 + 1] / buffer[8 + 3];
		zUp = buffer[8 + 2] / buffer[8 + 3];

		setChanged(true);

	}

	private static void createRotationMatrixAroundVector(float[] matrix, int offset, float angle, float x, float y,
                                                         float z) {
		float cos = (float) Math.cos(angle);
		float sin = (float) Math.sin(angle);
		float cos_1 = 1 - cos;

		// @formatter:off
		matrix[offset]=cos_1*x*x + cos     ;    	matrix[offset+1 ]=cos_1*x*y - z*sin   ;   matrix[offset+2 ]=cos_1*z*x + y*sin   ;   matrix[offset+3]=0   ;
		matrix[offset+4 ]=cos_1*x*y + z*sin   ;  	matrix[offset+5 ]=cos_1*y*y + cos     ;   matrix[offset+6 ]=cos_1*y*z - x*sin   ;   matrix[offset+7]=0   ;
		matrix[offset+8 ]=cos_1*z*x - y*sin   ;  	matrix[offset+9 ]=cos_1*y*z + x*sin   ;   matrix[offset+10]=cos_1*z*z + cos    ;   matrix[offset+11]=0  ;
		matrix[offset+12]=0           		 ;      matrix[offset+13]=0          		  ;   matrix[offset+14]=0          		  ;   matrix[offset+15]=1  ;
		
		// @formatter:on
	}

	private static void multiplyMMV(float[] result, int retOffset, float[] matrix, int matOffet, float[] vector4Matrix,
                                    int vecOffset) {
		for (int i = 0; i < vector4Matrix.length / 4; i++) {
			Matrix.multiplyMV(result, retOffset + (i * 4), matrix, matOffet, vector4Matrix, vecOffset + (i * 4));
		}
	}

	public boolean hasChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}

	@Override
	public String toString() {
		return "Camera [xPos=" + xPos + ", yPos=" + yPos + ", zPos=" + zPos + ", xView=" + xView + ", yView=" + yView
				+ ", zView=" + zView + ", xUp=" + xUp + ", yUp=" + yUp + ", zUp=" + zUp + "]";
	}

	public synchronized void Rotate(float rotViewerZ) {
		if (rotViewerZ == 0) return;
		RotateImpl(rotViewerZ);
		lastAction = new Object[]{"rotate",rotViewerZ};
	}

	private void RotateImpl(float rotViewerZ) {
		if (Float.isNaN(rotViewerZ)) {
			Log.w("Rot", "NaN");
			return;
		}
		float xLook = xView - xPos;
		float yLook = yView - yPos;
		float zLook = zView - zPos;
		float vlen = Matrix.length(xLook, yLook, zLook);
		xLook /= vlen;
		yLook /= vlen;
		zLook /= vlen;

		createRotationMatrixAroundVector(buffer, 24, rotViewerZ, xLook, yLook, zLook);
		float[] coordinates = new float[] { xPos, yPos, zPos, 1, xView, yView, zView, 1, xUp, yUp, zUp, 1 };
		multiplyMMV(buffer, 0, buffer, 24, coordinates, 0);

		xPos = buffer[0];
		yPos = buffer[1];
		zPos = buffer[2];
		xView = buffer[4];
		yView = buffer[4 + 1];
		zView = buffer[4 + 2];
		xUp = buffer[8];
		yUp = buffer[8 + 1];
		zUp = buffer[8 + 2];

		setChanged(true);
	}

	public Camera[] toStereo(float eyeSeparation){

	    // look vector
	    float xLook = xView - xPos;
        float yLook = yView - yPos;
        float zLook = zView - zPos;

        // right vector
        float[] crossRight = Math3DUtils.crossProduct(xLook, yLook, zLook, xUp, yUp, zUp);
        Math3DUtils.normalize(crossRight);

        // new left pos
        float xPosLeft = xPos - crossRight[0] * eyeSeparation/2;
        float yPosLeft = yPos - crossRight[1] * eyeSeparation/2;
        float zPosLeft = zPos - crossRight[2] * eyeSeparation/2;
        float xViewLeft = xView -  crossRight[0] * eyeSeparation/2;
        float yViewLeft = yView -  crossRight[1] * eyeSeparation/2;
        float zViewLeft = zView -  crossRight[2] * eyeSeparation/2;

        // new right pos
        float xPosRight = xPos + crossRight[0] * eyeSeparation/2;
        float yPosRight = yPos + crossRight[1] * eyeSeparation/2;
        float zPosRight = zPos + crossRight[2] * eyeSeparation/2;
        float xViewRight = xView +  crossRight[0] * eyeSeparation/2;
        float yViewRight = yView +  crossRight[1] * eyeSeparation/2;
        float zViewRight = zView +  crossRight[2] * eyeSeparation/2;

        xViewLeft = xView;
        yViewLeft = yView;
        zViewLeft = zView;

        xViewRight = xView;
        yViewRight = yView;
        zViewRight = zView;


        Camera left = new Camera(xPosLeft, yPosLeft, zPosLeft, xViewLeft, yViewLeft, zViewLeft, xUp, yUp, zUp);
	    Camera right = new Camera(xPosRight, yPosRight, zPosRight, xViewRight, yViewRight, zViewRight, xUp, yUp, zUp);

	    return new Camera[]{left, right};
    }
}
