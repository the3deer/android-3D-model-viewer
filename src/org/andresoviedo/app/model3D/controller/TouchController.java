package org.andresoviedo.app.model3D.controller;

import org.andresoviedo.app.model3D.entities.TouchScreen;
import org.andresoviedo.app.model3D.view.ModelRenderer;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;

public class TouchController {

	private static final String TAG = TouchController.class.getName();

	private static final int TOUCH_STATUS_ZOOMING_CAMERA = 1;
	private static final int TOUCH_STATUS_ROTATING_CAMERA = 4;
	private static final int TOUCH_STATUS_MOVING_WORLD = 5;

	private GLSurfaceView view;
	private final ModelRenderer mRenderer;

	int pointerCount = 0;
	float x1 = Float.MIN_VALUE;
	float y1 = Float.MIN_VALUE;
	float x2 = Float.MIN_VALUE;
	float y2 = Float.MIN_VALUE;
	float dx1 = Float.MIN_VALUE;
	float dy1 = Float.MIN_VALUE;
	float dx2 = Float.MIN_VALUE;
	float dy2 = Float.MIN_VALUE;

	float length = Float.MIN_VALUE;
	float previousLength = Float.MIN_VALUE;
	float currentPress1 = Float.MIN_VALUE;
	float currentPress2 = Float.MIN_VALUE;

	float rotation = 0;
	int currentSquare = Integer.MIN_VALUE;

	float[] hit1 = null;
	float[] hit2 = null;
	float[] hit3 = null;
	float[] hit4 = null;
	float hitSquare = -1;
	float hitTriangle = -1;
	boolean isOneFixedAndOneMoving = false;
	boolean fingersAreClosing = false;
	boolean isRotating = false;

	boolean gestureChanged = false;
	private int touchDelay = -2;
	private int touchStatus = -1;

	private float previousX1;
	private float previousY1;
	private float previousX2;
	private float previousY2;
	float[] previousVector = new float[4];
	float[] vector = new float[4];
	float[] rotationVector = new float[4];
	private float previousRotationSquare;

	public TouchController(GLSurfaceView view, ModelRenderer renderer) {
		super();
		this.view = view;
		this.mRenderer = renderer;
	}

	public synchronized boolean onTouchEvent(MotionEvent motionEvent) {
		// MotionEvent reports input details from the touch screen
		// and other input controls. In this case, you are only
		// interested in events where the touch position changed.

		switch (motionEvent.getActionMasked()) {
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_POINTER_UP:
		case MotionEvent.ACTION_POINTER_DOWN:
		case MotionEvent.ACTION_OUTSIDE:
		case MotionEvent.ACTION_HOVER_ENTER:
		case MotionEvent.ACTION_HOVER_EXIT:
			Log.d("Touch", "Gesture changed...");
			gestureChanged = true;
			touchDelay = 0;
			break;
		case MotionEvent.ACTION_MOVE:
			touchDelay++;
			break;
		default:
			Log.w(TAG, "Unknown state: " + motionEvent.getAction());
			gestureChanged = true;
		}

		pointerCount = motionEvent.getPointerCount();

		if (pointerCount == 1) {
			x1 = motionEvent.getX();
			y1 = motionEvent.getY();
			if (gestureChanged) {
				Log.d("Touch", "x:" + x1 + ",y:" + y1);
				previousX1 = x1;
				previousY1 = y1;
			}
			dx1 = x1 - previousX1;
			dy1 = y1 - previousY1;
			hit1 = unproject(x1, y1, 0);
			hit2 = unproject(x1, y1, 1);
			// Log.d("Ray", "Ray2---> x:" + xyzw2[0] + " y:" + xyzw2[1] + " z:" + xyzw2[2] + " w(" + xyzw2[3] + ")");
		} else if (pointerCount == 2) {
			x1 = motionEvent.getX(0);
			y1 = motionEvent.getY(0);
			x2 = motionEvent.getX(1);
			y2 = motionEvent.getY(1);
			vector[0] = x2 - x1;
			vector[1] = y2 - y1;
			vector[2] = 0;
			vector[3] = 1;
			float len = Matrix.length(vector[0], vector[1], vector[2]);
			vector[0] /= len;
			vector[1] /= len;

			// Log.d("Touch", "x1:" + x1 + ",y1:" + y1 + ",x2:" + x2 + ",y2:" + y2);
			if (gestureChanged) {
				previousX1 = x1;
				previousY1 = y1;
				previousX2 = x2;
				previousY2 = y2;
				System.arraycopy(vector, 0, previousVector, 0, vector.length);
			}
			dx1 = x1 - previousX1;
			dy1 = y1 - previousY1;
			dx2 = x2 - previousX2;
			dy2 = y2 - previousY2;

			rotationVector[0] = (previousVector[1] * vector[2]) - (previousVector[2] * vector[1]);
			rotationVector[1] = (previousVector[2] * vector[0]) - (previousVector[0] * vector[2]);
			rotationVector[2] = (previousVector[0] * vector[1]) - (previousVector[1] * vector[0]);
			len = Matrix.length(rotationVector[0], rotationVector[1], rotationVector[2]);
			rotationVector[0] /= len;
			rotationVector[1] /= len;
			rotationVector[2] /= len;

			previousLength = (float) Math
					.sqrt(Math.pow(previousX2 - previousX1, 2) + Math.pow(previousY2 - previousY1, 2));
			length = (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));

			currentPress1 = motionEvent.getPressure(0);
			currentPress2 = motionEvent.getPressure(1);
			rotation = 0;
			hit1 = unproject(x1, y1, 0);
			hit2 = unproject(x1, y1, 1);
			hit3 = unproject(x2, y2, 0);
			hit4 = unproject(x2, y2, 1);
			// wzSquare = hit(xyzw3, xyzw2, mRenderer.getmSquare().getPosition());
			// wzTriangle = hit(xyzw1, xyzw2, mRenderer.getmTriangle().getPosition());
			rotation = TouchScreen.getRotation360(motionEvent);
			currentSquare = TouchScreen.getSquare(motionEvent);
			if (currentSquare == 1 && previousRotationSquare == 4) {
				rotation = 0;
			} else if (currentSquare == 4 && previousRotationSquare == 1) {
				rotation = 360;
			}

			// gesture detection
			isOneFixedAndOneMoving = ((dx1 + dy1) == 0) != (((dx2 + dy2) == 0));
			fingersAreClosing = !isOneFixedAndOneMoving && (Math.abs(dx1 + dx2) < 10 && Math.abs(dy1 + dy2) < 10);
			isRotating = !isOneFixedAndOneMoving && (dx1 != 0 && dy1 != 0 && dx2 != 0 && dy2 != 0)
					&& rotationVector[2] != 0;
		}

		if (touchDelay > 1) {
			// INFO: Procesar gesto
			if (pointerCount == 1 && currentPress1 > 4.0f) {
				// TODO: enable this
				// hitSquare = hit(hit1, hit2, mRenderer.getScene().getSquare1().getPosition());
				// hitTriangle = hit(hit1, hit2, mRenderer.getScene().getSquare1().getPosition());
			} else if (pointerCount == 1) {
				touchStatus = TOUCH_STATUS_MOVING_WORLD;
				// Log.i("Touch", "Moving World '" + dx1 + "','" + dy1 + "'...");
				mRenderer.getCamera().translateCamera(dx1 * 10 / mRenderer.getWidth(),
						dy1 * 10 / mRenderer.getHeight());
			} else if (pointerCount == 2) {
				if (fingersAreClosing) {
					touchStatus = TOUCH_STATUS_ZOOMING_CAMERA;
					float zoomFactor = (length - previousLength) / ((mRenderer.getWidth() + mRenderer.getHeight()) / 2)
							* 10;
					Log.i("Camera", "Zooming '" + zoomFactor + "'...");
					mRenderer.getCamera().MoveCameraZ(zoomFactor);
				}
				if (isRotating) {
					touchStatus = TOUCH_STATUS_ROTATING_CAMERA;
					Log.i("Camera", "Rotating camera '" + Math.signum(rotationVector[2]) + "'...");
					mRenderer.getCamera().Rotate((float) (Math.signum(rotationVector[2]) / Math.PI) / 4);
				}
			}

			// INFO: Realizamos la acci�n
			switch (touchStatus) {
			// case TOUCH_STATUS_ROTATING_OBJECT:
			// // reverse direction of rotation above the mid-line
			// if (y > getHeight() / 2) {
			// // Log.d(TAG, "Reversing dx");
			// dx = dx * -1;
			// }
			//
			// // reverse direction of rotation to left of the mid-line
			// if (x < getWidth() / 2) {
			// // Log.d(TAG, "Reversing dy");
			// dy = dy * -1;
			// }
			// Log.w("Object", "Rotating '" + dx + "','" + dy + "'...");
			//
			// if (wzSquare > wzTriangle) {
			// mRenderer.getmSquare().setRotationZ(mRenderer.getmSquare().getRotationZ() + ((dx + dy) *
			// TOUCH_SCALE_FACTOR));
			// } else if (wzTriangle > wzSquare) {
			// mRenderer.getmTriangle().setRotationZ(mRenderer.getmTriangle().getRotationZ() + ((dx + dy) *
			// TOUCH_SCALE_FACTOR));
			// }
			//
			// break;
			//
			// case TOUCH_STATUS_MOVING_OBJECT:
			// // TODO: guess front object
			// boolean sqHit = wzSquare > wzTriangle;
			// boolean triHit = wzTriangle > wzSquare;
			// Log.w("Object", "Moving '" + sqHit + "','" + triHit + "'...");
			//
			// if (sqHit) {
			// mRenderer.getmSquare().translateX((dx * mRenderer.getRatio() * 2 / mRenderer.getWidth()) *
			// TOUCH_MOVE_FACTOR);
			// mRenderer.getmSquare().translateY((-dy * 1 / mRenderer.getHeight()) * TOUCH_MOVE_FACTOR);
			// }
			// if (triHit) {
			// mRenderer.getmTriangle().translateX((dx * mRenderer.getRatio() * 2 / mRenderer.getWidth()) *
			// TOUCH_MOVE_FACTOR);
			// mRenderer.getmTriangle().translateY((-dy * 1 / mRenderer.getHeight()) * TOUCH_MOVE_FACTOR);
			// }
			// break;
			//
			// case TOUCH_STATUS_ROTATING_OBJECT2:
			// Log.w("Object", "Rotating '" + wzSquare + "','" + wzTriangle + "'...");
			// // INFO: We are moving 2 fingers in different directions
			// // Rotate Camera
			// // TODO: Rotationfactor deber�a ser proporcional a la z?
			// if (wzSquare > wzTriangle) {
			// mRenderer.getmSquare().setRotationZ(mRenderer.getmSquare().getRotationZ() + (actualRotation *
			// TOUCH_ROTATION_FACTOR));
			// } else if (wzTriangle > wzSquare) {
			// mRenderer.getmTriangle().setRotationZ(mRenderer.getmTriangle().getRotationZ() + (actualRotation *
			// TOUCH_ROTATION_FACTOR));
			// }
			// // mRenderer.setAngle(mRenderer.getAngle() + actualRotation
			// // * TOUCH_ROTATION_FACTOR);
			//
			// // mRenderer.getCamera().Rotate(rotation *
			// // CAMERA_ROTATION_FACTOR,
			// // 0);
			//
			// break;

			}
		}

		previousX1 = x1;
		previousY1 = y1;
		previousX2 = x2;
		previousY2 = y2;

		previousRotationSquare = currentSquare;

		System.arraycopy(vector, 0, previousVector, 0, vector.length);

		if (gestureChanged && touchDelay > 1) {
			gestureChanged = false;
			Log.i("Fin", "Fin");
		}

		view.requestRender();

		return true;

	}

	public float[] unproject(float rx, float ry, float rz) {
		float[] xyzw = { 0, 0, 0, 0 };

		ry = (float) mRenderer.getHeight() - ry;

		int[] viewport = { 0, 0, mRenderer.getWidth(), mRenderer.getHeight() };

		GLU.gluUnProject(rx, ry, rz, mRenderer.getModelViewMatrix(), 0, mRenderer.getModelProjectionMatrix(), 0,
				viewport, 0, xyzw, 0);

		xyzw[0] /= xyzw[3];
		xyzw[1] /= xyzw[3];
		xyzw[2] /= xyzw[3];
		xyzw[3] = 1;
		return xyzw;
	}

	public float hit(float[] xyzw, float[] xyzw2, float[] position) {
		float zPrecition = 1000f;

		float xDif = (xyzw2[0] - xyzw[0]) / zPrecition;
		float yDif = (xyzw2[1] - xyzw[1]) / zPrecition;
		float zDif = (xyzw2[2] - xyzw[2]) / zPrecition;

		// @formatter:off
		for (int i = 0; i < zPrecition; i++) {
//			Log.d("Hit cube", "HIT");
			double objWidth = 1;
			double objHalfWidth = objWidth/2;
			float xIncr = xDif * i;
			if ((       xyzw[0] + xIncr) > position[0] - objHalfWidth
					&& (xyzw[0] + xIncr) < position[0] + objHalfWidth
					&& (xyzw[1] + (yDif * i)) > position[1] - objHalfWidth
					&& (xyzw[1] + (yDif * i)) < position[1] + objHalfWidth
					&& (xyzw[2] + (zDif * i)) > position[2] - objHalfWidth
					&& (xyzw[2] + (zDif * i)) < position[2] + objHalfWidth) {
				Log.w("Hit", "HIT: i["+i+"] wz["+(xyzw[2] + (zDif * i))+"]");
				return xyzw[2] + (zDif * i);
			}
		}
		// @formatter:on
		return -1;
	}
}
