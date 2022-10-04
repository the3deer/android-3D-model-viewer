package org.the3deer.android_3d_model_engine.controller;

import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

class TouchScreen {

	// these matrices will be used to move and zoom image
	private android.graphics.Matrix matrix = new android.graphics.Matrix();
	private android.graphics.Matrix savedMatrix = new android.graphics.Matrix();
	// we can be in one of these 3 states
	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mode = NONE;
	// remember some things for zooming
	private PointF start = new PointF();
	private PointF mid = new PointF();
	private float oldDist = 1f;
	private float d = 0f;
	private float newRot = 0f;
	private float[] lastEvent = null;

	public boolean onTouch(View v, MotionEvent event) {
		// handle touch events here
		ImageView view = (ImageView) v;
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			mode = DRAG;
			lastEvent = null;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
			}
			lastEvent = new float[4];
			lastEvent[0] = event.getX(0);
			lastEvent[1] = event.getX(1);
			lastEvent[2] = event.getY(0);
			lastEvent[3] = event.getY(1);
			d = getRotation(event);
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			lastEvent = null;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				matrix.set(savedMatrix);
				float dx = event.getX() - start.x;
				float dy = event.getY() - start.y;
				matrix.postTranslate(dx, dy);
			} else if (mode == ZOOM) {
				float newDist = spacing(event);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = (newDist / oldDist);
					matrix.postScale(scale, scale, mid.x, mid.y);
				}
				if (lastEvent != null && event.getPointerCount() == 3) {
					newRot = getRotation(event);
					float r = newRot - d;
					float[] values = new float[9];
					matrix.getValues(values);
					float tx = values[2];
					float ty = values[5];
					float sx = values[0];
					float xc = (view.getWidth() / 2) * sx;
					float yc = (view.getHeight() / 2) * sx;
					matrix.postRotate(r, tx + xc, ty + yc);
				}
			}
			break;
		}

		view.setImageMatrix(matrix);
		return true;
	}

	/**
	 * Determine the space between the first two fingers
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float)Math.sqrt(x * x + y * y);
	}

	/**
	 * Calculate the mid point of the first two fingers
	 */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	/**
	 * Calculate the degree to be rotated by.
	 *
	 * @param event
	 * @return Degrees
	 */
	public static float getRotation(MotionEvent event) {
		double dx = (event.getX(0) - event.getX(1));
		double dy = (event.getY(0) - event.getY(1));
		double radians = Math.atan2(Math.abs(dy), Math.abs(dx));
		double degrees = Math.toDegrees(radians);
		return (float) degrees;
	}

	public static float getRotation360(MotionEvent event) {
		double dx = (event.getX(0) - event.getX(1));
		double dy = (event.getY(0) - event.getY(1));
		double radians = Math.atan2(Math.abs(dy), Math.abs(dx));
		double degrees = Math.toDegrees(radians);
		int square = 1;
		if (dx > 0 && dy == 0) {
			square = 1;
		} else if (dx > 0 && dy < 0) {
			square = 1;
		} else if (dx == 0 && dy < 0) {
			square = 2;
			degrees = 180 - degrees;
		} else if (dx < 0 && dy < 0) {
			square = 2;
			degrees = 180 - degrees;
		} else if (dx < 0 && dy == 0) {
			square = 3;
			degrees = 180 + degrees;
		} else if (dx < 0 && dy > 0) {
			square = 3;
			degrees = 180 + degrees;
		} else if (dx == 0 && dy > 0) {
			square = 4;
			degrees = 360 - degrees;
		} else if (dx > 0 && dy > 0) {
			square = 4;
			degrees = 360 - degrees;
		}
		return (float) degrees;
	}

	public static int getSquare(MotionEvent event) {
		double dx = (event.getX(0) - event.getX(1));
		double dy = (event.getY(0) - event.getY(1));
		int square = 1;
		if (dx > 0 && dy == 0) {
			square = 1;
		} else if (dx > 0 && dy < 0) {
			square = 1;
		} else if (dx == 0 && dy < 0) {
			square = 2;
		} else if (dx < 0 && dy < 0) {
			square = 2;
		} else if (dx < 0 && dy == 0) {
			square = 3;
		} else if (dx < 0 && dy > 0) {
			square = 3;
		} else if (dx == 0 && dy > 0) {
			square = 4;
		} else if (dx > 0 && dy > 0) {
			square = 4;
		}
		return square;
	}
}
