package org.andresoviedo.android_3d_model_engine.animation;

import android.opengl.Matrix;

import org.andresoviedo.android_3d_model_engine.services.collada.entities.JointData;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents a joint in a "skeleton". It contains the index of the joint which
 * determines where in the vertex shader uniform array the joint matrix for this
 * joint is loaded up to. It also contains the name of the bone, and a list of
 * all the child joints.
 * <p>
 * The "animatedTransform" matrix is the joint transform that I keep referring
 * to in the tutorial. This is the transform that gets loaded up to the vertex
 * shader and is used to transform vertices. It is a model-space transform that
 * transforms the joint from it's bind (original position, no animation applied)
 * position to it's current position in the current pose. Changing this
 * transform changes the position/rotation of the joint in the animated entity.
 * <p>
 * The two other matrices are transforms that are required to calculate the
 * "animatedTransform" in the {@link org.andresoviedo.android_3d_model_engine.animation.Animator}
 * class. It also has the local bind
 * transform which is the original (no pose/animation applied) transform of the
 * joint relative to the parent joint (in bone-space).
 * <p>
 * The "bindLocalTransform" is the original (bind) transform of the joint
 * relative to its parent (in bone-space). The inverseBindTransform is that bind
 * transform in model-space, but inversed.
 *
 * @author andresoviedo
 */
public class Joint {

    private final JointData data;

    // descendants
    private final List<Joint> children = new ArrayList<>();

    // this is the animated final matrix used when drawing in opengl
    private final float[] animatedTransform = new float[16];

    /**
     * This is called during set-up, after the joints hierarchy has been
     * created. This calculates the model-space bind transform of this joint
     * like so: </br>
     * </br>
     * {@code bindTransform = parentBindTransform * bindLocalTransform}</br>
     * </br>
     * where "bindTransform" is the model-space bind transform of this joint,
     * "parentBindTransform" is the model-space bind transform of the parent
     * joint, and "bindLocalTransform" is the bone-space bind transform of this
     * joint. It then calculates and stores the inverse of this model-space bind
     * transform, for use when calculating the final animation transform each
     * frame. It then recursively calls the method for all of the children
     * joints, so that they too calculate and store their inverse bind-pose
     * transform.
     */
    public Joint(JointData data) {
        this.data = data;
        Matrix.setIdentityM(animatedTransform,0);
    }

    /**
     * Constructs the joint-hierarchy skeleton from the data extracted from the
     * collada file.
     *
     * @return The created joint, with all its descendants added.
     */
    public static Joint buildJoints(JointData rootJointData) {
        return buildJoint(rootJointData);
    }

    /**
     * Creates a new entity capable of animation. The inverse bind transform for
     * all joints is calculated in this constructor. The bind transform is
     * simply the original (no pose applied) transform of a joint in relation to
     * the model's origin (model-space). The inverse bind transform is simply
     * that but inverted.
     *
     * @param data
     *            - the root joint of the joint hierarchy which makes up the
     *            "skeleton" of the entity.
     *
     */
    private static Joint buildJoint(JointData data){
        Joint ret = new Joint(data);
        for (JointData child : data.children) {
            ret.addChild(buildJoint(child));
        }
        return ret;
    }

    public int getIndex() {
        return data.getIndex();
    }

    public String getName() {
        return data.getId();
    }

    public List<Joint> getChildren() {
        return children;
    }

    public float[] getBindLocalTransform() {
        return data.getBindLocalTransform();
    }

    /**
     * Adds a child joint to this joint. Used during the creation of the joint
     * hierarchy. Joints can have multiple children, which is why they are
     * stored in a list (e.g. a "hand" joint may have multiple "finger" children
     * joints).
     *
     * @param child - the new child joint of this joint.
     */
    public void addChild(Joint child) {
        this.children.add(child);
    }

    /**
     * The animated transform is the transform that gets loaded up to the shader
     * and is used to deform the vertices of the "skin". It represents the
     * transformation from the joint's bind position (original position in
     * model-space) to the joint's desired animation pose (also in model-space).
     * This matrix is calculated by taking the desired model-space transform of
     * the joint and multiplying it by the inverse of the starting model-space
     * transform of the joint.
     *
     * @return The transformation matrix of the joint which is used to deform
     * associated vertices of the skin in the shaders.
     */
    public float[] getAnimatedTransform() {
        return animatedTransform;
    }

    /**
     * This returns the inverted model-space bind transform. The bind transform
     * is the original model-space transform of the joint (when no animation is
     * applied). This returns the inverse of that, which is used to calculate
     * the animated transform matrix which gets used to transform vertices in
     * the shader.
     *
     * @return The inverse of the joint's bind transform (in model-space).
     */
    public float[] getInverseBindTransform() {
        return data.getInverseBindTransform();
    }

    @Override
    public Joint clone() {
        final Joint ret = new Joint(data);
        for (final Joint child : this.children){
            ret.addChild(child.clone());
        }
        return ret;
    }

    @Override
    public String toString() {
        return data.toString();
    }

    public JointData find(String id) {
        return data.find(id);
    }

    public List<JointData> findAll(String id) {
        return data.findAll(id);
    }

}
