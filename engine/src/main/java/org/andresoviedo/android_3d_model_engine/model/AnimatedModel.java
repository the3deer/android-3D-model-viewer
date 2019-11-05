package org.andresoviedo.android_3d_model_engine.model;

import android.opengl.Matrix;

import org.andresoviedo.android_3d_model_engine.animation.Animation;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.Joint;

import java.nio.FloatBuffer;

/**
 * 
 * This class represents an entity in the world that can be animated. It
 * contains the model's VAO which contains the mesh data, the texture, and the
 * root joint of the joint hierarchy, or "skeleton". It also holds an int which
 * represents the number of joints that the model's skeleton contains, and has
 * its own {@link org.andresoviedo.android_3d_model_engine.animation.Animator} instance which can be used to apply animations to
 * this entity.
 * 
 * @author Karl
 *
 */
public class AnimatedModel extends Object3DData {

	// skeleton
	private Joint rootJoint;
	private int jointCount;
	private int boneCount;
	private FloatBuffer jointIds;
	private FloatBuffer vertexWeigths;
	private Animation animation;

	// cache
	private float[][] jointMatrices;

	public AnimatedModel(FloatBuffer vertexArrayBuffer){
		super(vertexArrayBuffer);
	}

	/**
	 * Creates a new entity capable of animation. The inverse bind transform for
	 * all joints is calculated in this constructor. The bind transform is
	 * simply the original (no pose applied) transform of a joint in relation to
	 * the model's origin (model-space). The inverse bind transform is simply
	 * that but inverted.
	 *
	 * @param rootJoint
	 *            - the root joint of the joint hierarchy which makes up the
	 *            "skeleton" of the entity.
	 * @param jointCount
	 *            - the number of joints in the joint hierarchy (skeleton) for
	 *            this entity.
	 * 
	 */
	public AnimatedModel setRootJoint(Joint rootJoint, int jointCount, int boneCount) {
		this.rootJoint = rootJoint;
		this.jointCount = jointCount;
		this.boneCount = boneCount;
        this.jointMatrices = new float[boneCount][16];
        this.jointMatrices[rootJoint.getIndex()] = rootJoint.getAnimatedTransform();
		return this;
	}

	public int getJointCount(){
		return jointCount;
	}

	public int getBoneCount() {
		return boneCount;
	}

	public AnimatedModel setJointIds(FloatBuffer jointIds){
		this.jointIds = jointIds;
		return this;
	}

	public FloatBuffer getJointIds(){
		return jointIds;
	}

	public AnimatedModel setVertexWeights(FloatBuffer vertexWeigths){
		this.vertexWeigths = vertexWeigths;
		return this;
	}

	public FloatBuffer getVertexWeights(){
		return vertexWeigths;
	}

	public AnimatedModel doAnimation(Animation animation){
		this.animation = animation;
		return this;
	}

	public Animation getAnimation(){
		return animation;
	}

	/**
	 * @return The root joint of the joint hierarchy. This joint has no parent,
	 *         and every other joint in the skeleton is a descendant of this
	 *         joint.
	 */
	public Joint getRootJoint() {
		return rootJoint;
	}

	/**
	 * Gets an array of the all important model-space transforms of all the
	 * joints (with the current animation pose applied) in the entity. The
	 * joints are ordered in the array based on their joint index. The position
	 * of each joint's transform in the array is equal to the joint's index.
	 * 
	 * @return The array of model-space transforms of the joints in the current
	 *         animation pose.
	 */
	public float[][] getJointTransforms() {
		return jointMatrices;
	}

	public void updateAnimatedTransform(Joint joint){
		jointMatrices[joint.getIndex()] = joint.getAnimatedTransform();
	}

}
