package org.andresoviedo.android_3d_model_engine.services.collada.loader;

import android.opengl.Matrix;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.services.collada.entities.JointData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.SkeletonData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.SkinningData;
import org.andresoviedo.util.math.Math3DUtils;
import org.andresoviedo.util.xml.XmlNode;

import java.util.List;


public class SkeletonLoader {

	private final XmlNode visualScene;

	private final SkinningData skinningData;

	private List<String> boneOrder;

	private int jointCount = 0;

	public SkeletonLoader(XmlNode visualSceneNode, SkinningData skinningData) {
		this.visualScene = visualSceneNode.getChild("visual_scene");
		this.skinningData = skinningData;
		this.boneOrder = skinningData.jointOrder;
	}
	
	public SkeletonData extractBoneData(){
        XmlNode headNode = visualScene.getChildWithAttribute("node","type","JOINT");
		if (headNode == null){
	        // Why storm-trooper and cowboy has a "Armature" instead of "JOINT"?
        	headNode = visualScene.getChildWithAttribute("node", "id", "Armature");
		}
		JointData headJoint = loadJointData(headNode);
		if (jointCount != boneOrder.size()) {
			Log.i("SkeletonLoader", "jointCount != boneOrder: " + jointCount + " != " + boneOrder.size());
		}
		return new SkeletonData(jointCount, boneOrder.size(), headJoint);
	}
	
	private JointData loadJointData(XmlNode jointNode){
		JointData joint = extractMainJointData(jointNode);
		for(XmlNode childNode : jointNode.getChildren("node")){
			joint.addChild(loadJointData(childNode));
		}
		return joint;
	}
	
	private JointData extractMainJointData(XmlNode jointNode){
		String nameId = jointNode.getAttribute("id");
		int index = boneOrder.indexOf(nameId);
		XmlNode jointMatrix = jointNode.getChild("matrix");
        float[] matrix = new float[16];

		if (jointMatrix != null) {
            float[] matrix1 = Math3DUtils.parseFloat(jointMatrix.getData().trim().split("\\s+"));
            Matrix.transposeM(matrix, 0, matrix1, 0);
        } else {
            Matrix.setIdentityM(matrix,0);
            XmlNode translateNode = jointNode.getChild("translate");
            float[] translate = Math3DUtils.parseFloat(translateNode.getData().trim().split("\\s+"));
            Matrix.translateM(matrix,0,translate[0],translate[1],translate[2]);
            for (XmlNode rotateNode : jointNode.getChildren("rotate")){
                float[] rotate = Math3DUtils.parseFloat(rotateNode.getData().trim().split("\\s+"));
                Matrix.rotateM(matrix,0, rotate[3],rotate[0],rotate[1],rotate[2]);
            }
            XmlNode scaleNode = jointNode.getChild("scale");
            float[] scale = Math3DUtils.parseFloat(scaleNode.getData().trim().split("\\s+"));
            Matrix.scaleM(matrix,0,scale[0], scale[1], scale[2]);
        }

        jointCount++;

        float[] inverseBindMatrix = null;
        if (index >= 0 && skinningData.inverseBindMatrix != null) {
            inverseBindMatrix = new float[16];
            Matrix.transposeM(inverseBindMatrix, 0, skinningData.inverseBindMatrix, index * 16);
        }
        return new JointData(index, nameId, matrix, inverseBindMatrix);
	}
}
