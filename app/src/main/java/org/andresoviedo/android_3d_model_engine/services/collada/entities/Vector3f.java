package org.andresoviedo.android_3d_model_engine.services.collada.entities;

import android.opengl.Matrix;

/**
 * Created by andres on 18/11/17.
 */

public class Vector3f {
	public float x,y,z;

	public Vector3f(float x, float y, float z){
		this.x=x;
		this.y=y;
		this.z=z;
	}

	/* (non-Javadoc)
	 * @see org.lwjgl.util.vector.WritableVector3f#set(float, float, float)
	 */
	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public float length(){
		return Matrix.length(x,y,z);
	}

	/**
	 * Add a vector to another vector and place the result in a destination
	 * vector.
	 * @param left The LHS vector
	 * @param right The RHS vector
	 * @param dest The destination vector, or null if a new vector is to be created
	 * @return the sum of left and right in dest
	 */
	public static Vector3f add(Vector3f left, Vector3f right, Vector3f dest) {
		if (dest == null)
			return new Vector3f(left.x + right.x, left.y + right.y, left.z + right.z);
		else {
			dest.set(left.x + right.x, left.y + right.y, left.z + right.z);
			return dest;
		}
	}

	/* (non-Javadoc)
	 * @see org.lwjgl.vector.Vector#scale(float)
	 */
	public Vector3f scale(float scale) {

		x *= scale;
		y *= scale;
		z *= scale;

		return this;

	}

	/**
	 * Normalise this vector
	 * @return this
	 */
	public final Vector3f normalise() {
		float len = length();
		if (len != 0.0f) {
			float l = 1.0f / len;
			return scale(l);
		} else
			throw new IllegalStateException("Zero length vector");
	}
}
