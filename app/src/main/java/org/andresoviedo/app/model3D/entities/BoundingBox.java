package org.andresoviedo.app.model3D.entities;

/**
 * @author andresoviedo
 */

public final class BoundingBox {
	private final String id;
	private float xMin = Float.MAX_VALUE;
	private float xMax = Float.MIN_VALUE;
	private float yMin = Float.MAX_VALUE;
	private float yMax = Float.MIN_VALUE;
	private float zMin = Float.MAX_VALUE;
	private float zMax = Float.MIN_VALUE;

	public BoundingBox(String id, float xMin, float xMax, float yMin, float yMax, float zMin, float zMax) {
		this.id = id;
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
		this.zMin = zMin;
		this.zMax = zMax;
	}

	public float getxMin() {
		return xMin;
	}

	public void setxMin(float xMin) {
		this.xMin = xMin;
	}

	public float getxMax() {
		return xMax;
	}

	public void setxMax(float xMax) {
		this.xMax = xMax;
	}

	public float getyMin() {
		return yMin;
	}

	public void setyMin(float yMin) {
		this.yMin = yMin;
	}

	public float getyMax() {
		return yMax;
	}

	public void setyMax(float yMax) {
		this.yMax = yMax;
	}

	public float getzMin() {
		return zMin;
	}

	public void setzMin(float zMin) {
		this.zMin = zMin;
	}

	public float getzMax() {
		return zMax;
	}

	public void setzMax(float zMax) {
		this.zMax = zMax;
	}

	public boolean insideBounds(float x, float y, float z){
		return !outOfBound(x,y,z);
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
		if (y > getyMax()){
			return true;
		}
		if (z < getzMin()){
			return true;
		}
		if (z > getzMax()){
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "BoundingBox{" +
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