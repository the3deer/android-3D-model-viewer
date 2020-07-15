package org.andresoviedo.android_3d_model_engine.services.collada.entities;

/**
 * This contains the data for a transformation of one joint, at a certain time
 * in an animation. It has the name of the joint that it refers to, and the
 * local transform of the joint in the pose position.
 * 
 * @author andresoviedo
 *
 */
public class JointTransformData {

	public final String jointId;

	public final float[] matrix;
	public final Float[] location;
	public final Float[] rotation;
	public final Float[] scale;

	private JointTransformData(String jointId, float[] matrix, Float[] location, Float[] rotation, Float[] scale) {
		this.jointId = jointId;
		this.matrix = matrix;
		this.location = location;
		this.rotation = rotation;
		this.scale = scale;
	}

	public static JointTransformData ofMatrix(String jointId, float[] matrix) {
		return new JointTransformData(jointId, matrix, null, null, null);
	}

	public static JointTransformData ofLocation(String jointId, Float[] location) {
		return new JointTransformData(jointId, null, location, null, null);
	}

	public static JointTransformData ofRotation(String jointId, Float[] rotation) {
		return new JointTransformData(jointId, null, null, rotation, null);
	}

	public static JointTransformData ofScale(String jointId, Float[] scale) {
		return new JointTransformData(jointId, null, null, null, scale);
	}
}
