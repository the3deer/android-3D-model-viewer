package org.andresoviedo.android_3d_model_engine.objects;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.animation.Joint;
import org.andresoviedo.android_3d_model_engine.model.AnimatedModel;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.JointData;
import org.andresoviedo.util.io.IOUtils;
import org.andresoviedo.util.math.Math3DUtils;

public final class Skeleton {

    public static AnimatedModel build(AnimatedModel animatedModel) {

        AnimatedModel skeleton = new AnimatedModel(IOUtils.createFloatBuffer(animatedModel.getJointCount() * 9));
        skeleton.setNormalsBuffer(IOUtils.createFloatBuffer(animatedModel.getJointCount() * 9));
        skeleton.setJointsData(animatedModel.getJointsData());

        skeleton.setJointIds(IOUtils.createFloatBuffer(skeleton.getJointCount() * 9));
        skeleton.doAnimation(animatedModel.getAnimation());
        skeleton.setVertexWeights(IOUtils.createFloatBuffer(skeleton.getJointCount() * 9));
        skeleton.setLocation(animatedModel.getLocation());
        skeleton.setScale(animatedModel.getScale());
        skeleton.setRootJoint(animatedModel.getRootJoint());

        Log.i("Skeleton", "Building " + skeleton.getJointCount() + " joints...");
        buildBones(skeleton, skeleton.getJointsData().getHeadJoint(), Math3DUtils.IDENTITY_MATRIX, new float[]{0, 0, 0}, skeleton
                .getJointsData().getHeadJoint().getIndex());

        skeleton.setId(animatedModel.getId() + "-skeleton");
        skeleton.setDrawMode(GLES20.GL_TRIANGLES);
        skeleton.setDrawUsingArrays(true);


        return skeleton;
    }

    private static void buildBones(AnimatedModel animatedModel, JointData joint, float[] parentTransform, float[]
            parentPoint, int parentJoinIndex) {

        float[] point = new float[4];
        float[] transform = new float[16];
        Matrix.multiplyMM(transform, 0, parentTransform, 0, joint.getBindLocalTransform(), 0);
        Matrix.multiplyMV(point, 0, transform, 0, new float[]{0, 0, 0, 1}, 0);

        float[] v = Math3DUtils.substract(point, parentPoint);
        float[] point1 = new float[]{point[0], point[1], point[2] - Matrix.length(v[0], v[1], v[2]) * 0.05f};
        float[] point2 = new float[]{point[0], point[1], point[2] + Matrix.length(v[0], v[1], v[2]) * 0.05f};

        float[] normal = Math3DUtils.calculateNormal(parentPoint, point1, point2);
        if (Math3DUtils.length(normal) == 0) {
            // this may happen in first iteration - root point == root joint
            // do nothing
        } else {
            Math3DUtils.normalize(normal);
        }

        animatedModel.getVertexBuffer().put(parentPoint[0]);
        animatedModel.getVertexBuffer().put(parentPoint[1]);
        animatedModel.getVertexBuffer().put(parentPoint[2]);
        animatedModel.getVertexBuffer().put(point1[0]);
        animatedModel.getVertexBuffer().put(point1[1]);
        animatedModel.getVertexBuffer().put(point1[2]);
        animatedModel.getVertexBuffer().put(point2[0]);
        animatedModel.getVertexBuffer().put(point2[1]);
        animatedModel.getVertexBuffer().put(point2[2]);

        animatedModel.getNormalsBuffer().put(normal);
        animatedModel.getNormalsBuffer().put(normal);
        animatedModel.getNormalsBuffer().put(normal);

        animatedModel.getJointIds().put(parentJoinIndex);
        animatedModel.getJointIds().put(0);
        animatedModel.getJointIds().put(0);
        animatedModel.getVertexWeights().put(parentJoinIndex >= 0 ? 1 : 0);
        animatedModel.getVertexWeights().put(0);
        animatedModel.getVertexWeights().put(0);

        for (int i = 3; i < 9; i += 3) {
            animatedModel.getJointIds().put(joint.getIndex());
            animatedModel.getJointIds().put(0);
            animatedModel.getJointIds().put(0);
        }
        for (int i = 3; i < 9; i += 3) {
            animatedModel.getVertexWeights().put(1);
            animatedModel.getVertexWeights().put(0);
            animatedModel.getVertexWeights().put(0);
        }

        for (JointData child : joint.getChildren()) {
            buildBones(animatedModel, child, transform, point, joint.getIndex());
        }
    }
}
