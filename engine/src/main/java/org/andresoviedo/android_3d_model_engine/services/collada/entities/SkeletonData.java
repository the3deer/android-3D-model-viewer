package org.andresoviedo.android_3d_model_engine.services.collada.entities;

public class SkeletonData {

    private final int jointCount;
    private final JointData headJoint;
    private final int boneCount;

    public SkeletonData(int jointCount, int boneCount, JointData headJoint) {
        this.jointCount = jointCount;
        this.boneCount = boneCount;
        this.headJoint = headJoint;
    }

    /**
     * Constructs the joint-hierarchy skeleton from the data extracted from the
     * collada file.
     *
     * @return The created joint, with all its descendants added.
     */
    public Joint buildJoints() {
        return buildJoint(getHeadJoint());
    }

    private Joint buildJoint(JointData data){
        Joint ret = new Joint(data);
        for (JointData child : data.children) {
            ret.addChild(buildJoint(child));
        }
        return ret;
    }

    public JointData getHeadJoint() {
        return headJoint;
    }

    public int getBoneCount() {
        return boneCount;
    }

    public int getJointCount() {
        return jointCount;
    }

    public JointData find(String geometryId) {
        return headJoint.find(geometryId);
    }
}
