package org.andresoviedo.app.model3D.model;

import android.opengl.Matrix;

import org.andresoviedo.app.model3D.animation.Animation;
import org.andresoviedo.app.model3D.services.collada.entities.Joint;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * 
 * This class represents an entity in the world that can be animated. It
 * contains the model's VAO which contains the mesh data, the texture, and the
 * root joint of the joint hierarchy, or "skeleton". It also holds an int which
 * represents the number of joints that the model's skeleton contains, and has
 * its own {@link Animator} instance which can be used to apply animations to
 * this entity.
 * 
 * @author Karl
 *
 */
public class AnimatedModel extends Object3DData {

	// skeleton
	private Joint rootJoint;
	private int jointCount;
	private FloatBuffer jointIds;
	private FloatBuffer vertexWeigths;
	private Animation animation;

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
	public AnimatedModel setRootJoint(Joint rootJoint, int jointCount) {
		this.rootJoint = rootJoint;
		this.jointCount = jointCount;
		float[] parentTransform = new float[16];
		Matrix.setIdentityM(parentTransform,0);
		rootJoint.calcInverseBindTransform(parentTransform);
		return this;
	}

	public int getJointCount(){
		return jointCount;
	}

	public AnimatedModel setJointCount(int jointCount){
		this.jointCount = jointCount;
		return this;
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
		float[][] jointMatrices = new float[jointCount][16];
		for (int i=0; i<jointMatrices.length;i++){
			Matrix.setIdentityM(jointMatrices[i],0);
		}
		addJointsToArray(rootJoint, jointMatrices);
		return jointMatrices;
	}

	/**
	 * This adds the current model-space transform of a joint (and all of its
	 * descendants) into an array of transforms. The joint's transform is added
	 * into the array at the position equal to the joint's index.
	 * 
	 * @param headJoint
	 *            - the current joint being added to the array. This method also
	 *            adds the transforms of all the descendents of this joint too.
	 * @param jointMatrices
	 *            - the array of joint transforms that is being filled.
	 */
	private void addJointsToArray(Joint headJoint, float [][] jointMatrices) {
		jointMatrices[headJoint.index] = headJoint.getAnimatedTransform();
		for (Joint childJoint : headJoint.children) {
			addJointsToArray(childJoint, jointMatrices);
		}
	}

}
