package org.andresoviedo.android_3d_model_engine.services.collada.entities;

/**
 * This contains the data for a transformation of one joint, at a certain time
 * in an animation. It has the name of the joint that it refers to, and the
 * local transform of the joint in the pose position.
 * 
 * @author Karl
 *
 */
public class JointTransformData {

	public final String jointNameId;
	public final float[] jointLocalTransform;

	public JointTransformData(String jointNameId, float[] jointLocalTransform) {
		this.jointNameId = jointNameId;
		this.jointLocalTransform = jointLocalTransform;
	}
}
