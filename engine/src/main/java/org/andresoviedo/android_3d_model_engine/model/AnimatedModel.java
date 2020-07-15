package org.andresoviedo.android_3d_model_engine.model;

import android.opengl.Matrix;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.animation.Animation;
import org.andresoviedo.android_3d_model_engine.animation.Joint;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.SkeletonData;
import org.andresoviedo.util.math.Math3DUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * This class represents an entity in the world that can be animated. It
 * contains the model's VAO which contains the mesh data, the texture, and the
 * root joint of the joint hierarchy, or "skeleton". It also holds an int which
 * represents the number of joints that the model's skeleton contains, and has
 * its own {@link org.andresoviedo.android_3d_model_engine.animation.Animator} instance which can be used to apply animations to
 * this entity.
 *
 * @author andresoviedo
 */
public class AnimatedModel extends Object3DData {

    // skeleton
    private SkeletonData jointsData;

    // bind_shape_matrix
	/* The bind shape matrix describes how to transform the geometry into the right
	coordinate system for use with the joints */
    private float[] bindShapeMatrix;

    private FloatBuffer jointIds;
    private FloatBuffer vertexWeigths;
    private Animation animation;

    // cache
    private Joint rootJoint;
    private float[][] jointMatrices;

    public AnimatedModel() {
        super();
    }

    public AnimatedModel(FloatBuffer vertexBuffer) {
        super(vertexBuffer);
    }

    public AnimatedModel(FloatBuffer vertexBuffer, IntBuffer drawOrderBuffer) {
        super(vertexBuffer, drawOrderBuffer);
    }

    /**
     * This is the bind shape transform found in sking (ie. {@code <library_controllers><skin><bind_shape_matrix>}
     * The issue on handling this in the shader, is that we lose the transformation and cannot calculate further attributes (i.e. dimension)
     */
    public void setBindShapeMatrix(float[] matrix) {
        //this.bindShapeMatrix = matrix;
        super.setBindShapeMatrix(matrix);
    }

    public float[] getBindShapeMatrix() {
        if (bindShapeMatrix == null) {
            return Math3DUtils.IDENTITY_MATRIX;
        }
        return bindShapeMatrix;
    }

    public AnimatedModel setRootJoint(Joint rootJoint) {
        this.rootJoint = rootJoint;
        return this;
    }

    public void setJointsData(SkeletonData jointsData) {
        this.jointsData = jointsData;
    }

    public SkeletonData getJointsData() {
        return jointsData;
    }

    public int getJointCount() {
        return jointsData.getJointCount();
    }

    public int getBoneCount() {
        return jointsData.getBoneCount();
    }

    public AnimatedModel setJointIds(FloatBuffer jointIds) {
        this.jointIds = jointIds;
        return this;
    }

    public FloatBuffer getJointIds() {
        return jointIds;
    }

    public AnimatedModel setVertexWeights(FloatBuffer vertexWeigths) {
        this.vertexWeigths = vertexWeigths;
        return this;
    }

    public FloatBuffer getVertexWeights() {
        return vertexWeigths;
    }

    public AnimatedModel doAnimation(Animation animation) {
        this.animation = animation;
        return this;
    }

    public Animation getAnimation() {
        return animation;
    }

    /**
     * @return The root joint of the joint hierarchy. This joint has no parent,
     * and every other joint in the skeleton is a descendant of this
     * joint.
     */
    public Joint getRootJoint() {
        if (this.rootJoint == null && this.jointsData != null) {
            this.rootJoint = Joint.buildJoints(this.jointsData.getHeadJoint());
        }
        return rootJoint;
    }

    /**
     * Gets an array of the all important model-space transforms of all the
     * joints (with the current animation pose applied) in the entity. The
     * joints are ordered in the array based on their joint index. The position
     * of each joint's transform in the array is equal to the joint's index.
     *
     * @return The array of model-space transforms of the joints in the current
     * animation pose.
     */
    public float[][] getJointTransforms() {
        return jointMatrices;
    }

    public void updateAnimatedTransform(Joint joint) {
        if (jointMatrices == null) {
            this.jointMatrices = new float[getBoneCount()][16];
        }
        jointMatrices[joint.getIndex()] = joint.getAnimatedTransform();
    }

    public Dimensions getCurrentDimensions() {

        // FIXME: dimensions when model is animated are different. what we can do ??
        if (true) return super.getCurrentDimensions();

        if (this.currentDimensions == null) {
            final float[] location = new float[4];
            final float[] ret = new float[4];

            final Dimensions newDimensions = new Dimensions();

            Log.i("AnimatedModel", "Calculating current dimensions...");
            Log.i("AnimatedModel", "id:" + getId() + ", elements:" + elements);
            if (this.elements == null || this.elements.isEmpty()) {
                for (int i = 0; i < vertexBuffer.capacity(); i += 3) {
                    location[0] = vertexBuffer.get(i);
                    location[1] = vertexBuffer.get(i + 1);
                    location[2] = vertexBuffer.get(i + 2);
                    location[3] = 1;
                    final float[] temp = new float[4];
                    Matrix.multiplyMV(temp, 0, this.getBindShapeMatrix(), 0, location, 0);
                    Matrix.multiplyMV(ret, 0, this.getModelMatrix(), 0, temp, 0);
                    newDimensions.update(ret[0], ret[1], ret[2]);
                }
            } else {
                for (Element element : getElements()) {
                    final IntBuffer indexBuffer = element.getIndexBuffer();
                    for (int i = 0; i < indexBuffer.capacity(); i++) {
                        final int idx = indexBuffer.get(i);
                        location[0] = vertexBuffer.get(idx * 3);
                        location[1] = vertexBuffer.get(idx * 3 + 1);
                        location[2] = vertexBuffer.get(idx * 3 + 2);
                        location[3] = 1;
                        final float[] temp = new float[4];
                        Matrix.multiplyMV(temp, 0, this.getBindShapeMatrix(), 0, location, 0);
                        Matrix.multiplyMV(ret, 0, this.getModelMatrix(), 0, temp, 0);
                        newDimensions.update(ret[0], ret[1], ret[2]);
                    }
                }
            }
            this.currentDimensions = newDimensions;

            Log.d("AnimatedModel", "Calculated current dimensions for '" + getId() + "': " + this.currentDimensions);
        }
        return currentDimensions;
    }

    @Override
    public AnimatedModel clone() {
        final AnimatedModel ret = new AnimatedModel();
        super.copy(ret);
        ret.setJointsData(this.getJointsData());
        ret.setJointIds(this.getJointIds());
        ret.setVertexWeights(this.getVertexWeights());
        ret.doAnimation(this.getAnimation());
        ret.jointMatrices = this.jointMatrices;
        ret.bindShapeMatrix = this.bindShapeMatrix;
        return ret;
    }


}
