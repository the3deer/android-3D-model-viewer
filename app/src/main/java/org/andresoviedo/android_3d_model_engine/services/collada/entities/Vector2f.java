package org.andresoviedo.android_3d_model_engine.services.collada.entities;

/**
 * Created by andres on 18/11/17.
 */

public class Vector2f {
	public float x, y;

	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;

	}

	/* (non-Javadoc)
	 * @see org.lwjgl.util.vector.WritableVector3f#set(float, float, float)
	 */
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}
}