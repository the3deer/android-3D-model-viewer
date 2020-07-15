package org.andresoviedo.android_3d_model_engine.services.collada.entities;

public class SkeletonData {

    private final int jointCount;
    private final JointData headJoint;

    private int boneCount = 0;

    public SkeletonData(int jointCount, JointData headJoint) {
        this.jointCount = jointCount;
        this.headJoint = headJoint;
    }

    public void incrementBoneCount() {
        this.boneCount++;
    }

    public int getBoneCount() {
        return boneCount;
    }

    public JointData getHeadJoint() {
        return headJoint;
    }

    public int getJointCount() {
        return jointCount;
    }

    /**
     * use instead {@link JointData#findAll(String)}
     */
    @Deprecated
    public JointData find(String geometryId) {
        return headJoint.find(geometryId);
    }


}
