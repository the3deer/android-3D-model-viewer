package org.andresoviedo.android_3d_model_engine.services.collada.entities;

/**
 * Created by andres on 18/11/17.
 */

public class Vector4f {
	public float x,y,z,w;

	public Vector4f(float[] xyzw){
		this.x = xyzw[0];
		this.y = xyzw[1];
		this.z = xyzw[2];
		this.w = xyzw[3];
	}

	public Vector4f(float x, float y, float z, float w){
		this.x=x;
		this.y=y;
		this.z=z;
		this.w=w;
	}

	/* (non-Javadoc)
	 * @see org.lwjgl.util.vector.WritableVector3f#set(float, float, float)
	 */
	public void set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public float[] toArray(){
		return new float[]{x,y,z,w};
	}
}
