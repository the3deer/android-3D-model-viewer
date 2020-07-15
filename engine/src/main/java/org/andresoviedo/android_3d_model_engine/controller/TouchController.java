package org.andresoviedo.android_3d_model_engine.controller;

import android.app.Activity;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import org.andresoviedo.util.android.AndroidUtils;
import org.andresoviedo.util.event.EventListener;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

public class TouchController {

	private static final String TAG = TouchController.class.getName();

	private static final int TOUCH_STATUS_ZOOMING_CAMERA = 1;
	private static final int TOUCH_STATUS_ROTATING_CAMERA = 4;
	private static final int TOUCH_STATUS_MOVING_WORLD = 5;

	// constants
	private int width;
	private int height;

	// variables
	private final List<EventListener> listeners = new ArrayList<>();

	private float x1 = Float.MIN_VALUE;
    private float y1 = Float.MIN_VALUE;
    private float x2 = Float.MIN_VALUE;
    private float y2 = Float.MIN_VALUE;
    private float dx1 = Float.MIN_VALUE;
    private float dy1 = Float.MIN_VALUE;
    private float dx2 = Float.MIN_VALUE;
    private float dy2 = Float.MIN_VALUE;

    private float length = Float.MIN_VALUE;
    private float previousLength = Float.MIN_VALUE;
    private float currentPress1 = Float.MIN_VALUE;
    private float currentPress2 = Float.MIN_VALUE;

    private float rotation = 0;
    private int currentSquare = Integer.MIN_VALUE;

    private boolean isOneFixedAndOneMoving = false;
    private boolean fingersAreClosing = false;
    private boolean isRotating = false;

    private boolean gestureChanged = false;
	private boolean moving = false;
	private boolean simpleTouch = false;
	private long lastActionTime;
	private int touchDelay = -2;
	private int touchStatus = -1;

	private float previousX1;
	private float previousY1;
	private float previousX2;
	private float previousY2;
    private float[] previousVector = new float[4];
    private float[] vector = new float[4];
    private float[] rotationVector = new float[4];
	private float previousRotationSquare;

	public TouchController(Activity parent) {
		super();
		try {
			if (!AndroidUtils.supportsMultiTouch(parent.getPackageManager())) {
				Log.w("ModelActivity","Multitouch not supported. Some app features may not be available");
			} else {
				Log.i("ModelActivity","Initializing TouchController...");
			}
		}catch (Exception e){
			Toast.makeText(parent, "Error loading Touch Controller:\n" +e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}

	public void setSize(int width, int height){
		this.width = width;
		this.height = height;
	}

	public void addListener(EventListener listener){
		this.listeners.add(listener);
	}

	private void fireEvent(EventObject eventObject){
		AndroidUtils.fireEvent(listeners, eventObject);
	}

	public boolean onTouchEvent(MotionEvent motionEvent) {

		Log.v("TouchController","Processing MotionEvent...");
		final int pointerCount = motionEvent.getPointerCount();

		switch (motionEvent.getActionMasked()) {
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_POINTER_UP:
			case MotionEvent.ACTION_HOVER_EXIT:
			case MotionEvent.ACTION_OUTSIDE:
				// this to handle "1 simple touch"
				if (lastActionTime > SystemClock.uptimeMillis() - 250) {
					Log.v(TAG, "Simple touch !");
					simpleTouch = true;
				} else {
					Log.v(TAG, "Large touch !");
					gestureChanged = true;
					touchDelay = 0;
					lastActionTime = SystemClock.uptimeMillis();
					simpleTouch = false;
				}
				moving = false;
				break;
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
			case MotionEvent.ACTION_HOVER_ENTER:
				Log.v(TAG, "Gesture changed...");
				gestureChanged = true;
				touchDelay = 0;
				lastActionTime = SystemClock.uptimeMillis();
				simpleTouch = false;
				break;
			case MotionEvent.ACTION_MOVE:
				moving = true;
				simpleTouch = false;
				touchDelay++;
				break;
			default:
				Log.w(TAG, "Unknown state: " + motionEvent.getAction());
				gestureChanged = true;
		}


		if (pointerCount == 1) {
			x1 = motionEvent.getX();
			y1 = motionEvent.getY();
			Log.v(TAG, "1 touch ! x:" + x1 + ",y:" + y1);
			if (gestureChanged) {
				previousX1 = x1;
				previousY1 = y1;
			}
			dx1 = x1 - previousX1;
			dy1 = y1 - previousY1;
		} else if (pointerCount == 2) {
			x1 = motionEvent.getX(0);
			y1 = motionEvent.getY(0);
			x2 = motionEvent.getX(1);
			y2 = motionEvent.getY(1);
			Log.v(TAG, "2 touch ! x1:" + x1 + ",y1:" + y1 + ",x2:" + x2 + ",y2:" + y2);

			vector[0] = x2 - x1;
			vector[1] = y2 - y1;
			vector[2] = 0;
			vector[3] = 1;
			float len = Matrix.length(vector[0], vector[1], vector[2]);
			vector[0] /= len;
			vector[1] /= len;

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

		if (pointerCount == 1 && simpleTouch) {
			fireEvent(new TouchEvent(this, TouchEvent.CLICK, width, height, x1, y1));
		}

		if (touchDelay > 1) {
			// INFO: Process gesture
			if (pointerCount == 1 && currentPress1 > 4.0f) {
			} else if (pointerCount == 1) {
				fireEvent(new TouchEvent(this, TouchEvent.MOVE, width, height, previousX1, previousY1,
						x1, y1, dx1, dy1, 0,
						null));
				touchStatus = TOUCH_STATUS_MOVING_WORLD;
			} else if (pointerCount == 2) {
				if (fingersAreClosing) {
					fireEvent(new TouchEvent(this, TouchEvent.PINCH, width, height, previousX1, previousY1,
							x1, y1, dx1, dy1, (length - previousLength), null));
					touchStatus = TOUCH_STATUS_ZOOMING_CAMERA;
				}
				if (isRotating) {
					fireEvent(new TouchEvent(this, TouchEvent.ROTATE, width, height, previousX1, previousY1
							, x1, y1, dx1, dy1, 0, rotationVector));
					touchStatus = TOUCH_STATUS_ROTATING_CAMERA;
				}
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
			Log.v(TAG, "Fin");
		}
		return true;
	}
}

