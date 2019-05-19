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
	private boolean jointFound = false;

	public SkeletonLoader(XmlNode visualSceneNode, SkinningData skinningData) {
		this.visualScene = visualSceneNode.getChild("visual_scene");
		this.skinningData = skinningData;
		this.boneOrder = skinningData.jointOrder;
	}

	// <visual_scene>
	public SkeletonData extractBoneData(){

		Log.i("SkeletonLoader", "Loading skeleton...");

		// a visual scene may contain several nodes of different kinds
		List<XmlNode> nodes = visualScene.getChildren("node");
		if (nodes == null || nodes.isEmpty()){
			return null;
		}

		// does this model has any node containing a skeleton?
		JointData skeletonData = null;

		// analyze all nodes to get skeleton
		for (XmlNode node : nodes){

			// get first skeleton found
			JointData jointData = loadSkeleton(node);
			if (jointData != null && jointFound){
				skeletonData = jointData;
				break;
			}
			jointCount = 0;
		}

		// no skeleton found at all
		if (skeletonData == null){
			return null;
		}

		if (jointFound) {
			Log.i("SkeletonLoader", "Skeleton found. total joints: " + jointCount);
		} else {
			Log.i("SkeletonLoader", "Skeleton not found");
		}

		return new SkeletonData(jointCount, boneOrder.size(), skeletonData);
	}
	
	private JointData loadSkeleton(XmlNode jointNode){
		JointData joint = createJointData(jointNode);
		if (joint == null){
			return null;
		}
		// Log.i("SkeletonLoader","Joint: index "+joint.index+", name: "+joint.nameId);
		for(XmlNode childNode : jointNode.getChildren("node")){
			JointData child = loadSkeleton(childNode);
			if (child == null) continue;
			joint.addChild(child);
		}
		return joint;
	}
	
	private JointData createJointData(XmlNode jointNode){

		// joint transformation initialization
        float[] matrix = new float[16];
		Matrix.setIdentityM(matrix,0);

		// did we find any supported transformations?
		boolean matrixFound = false;
		if (jointNode.getChild("matrix") != null) {
			XmlNode jointMatrix = jointNode.getChild("matrix");
            float[] matrix1 = Math3DUtils.parseFloat(jointMatrix.getData().trim().split("\\s+"));
            Matrix.transposeM(matrix, 0, matrix1, 0);
			matrixFound = true;
        }

        if (jointNode.getChild("translate") != null) {
			XmlNode translateNode = jointNode.getChild("translate");
			float[] translate = Math3DUtils.parseFloat(translateNode.getData().trim().split("\\s+"));
			Matrix.translateM(matrix, 0, translate[0], translate[1], translate[2]);
			matrixFound = true;
		}

		if (jointNode.getChild("rotate") != null) {
			for (XmlNode rotateNode : jointNode.getChildren("rotate")) {
				float[] rotate = Math3DUtils.parseFloat(rotateNode.getData().trim().split("\\s+"));
				Matrix.rotateM(matrix, 0, rotate[3], rotate[0], rotate[1], rotate[2]);
			}
			matrixFound = true;
		}

		if (jointNode.getChild("scale") != null){
			XmlNode scaleNode = jointNode.getChild("scale");
			float[] scale = Math3DUtils.parseFloat(scaleNode.getData().trim().split("\\s+"));
			Matrix.scaleM(matrix,0,scale[0], scale[1], scale[2]);
			matrixFound = true;
		}

		// if no transformation was found, then this is not part of the skeleton transformation
		if (!matrixFound){
			return null;
		} else {
			jointCount++;
		}

		// get node attributes
		String nodeName = jointNode.getAttribute("name");
		String nodeSid = jointNode.getAttribute("sid");
		String nodeId = jointNode.getAttribute("id");

		// is this a joint bone?
		if ("JOINT".equals(jointNode.getAttribute("type"))){
			jointFound = true;
		}

		// index is only available for declared bones
		int index = boneOrder.indexOf(nodeName);
		if (index == -1){
			// fallback to node id
			index = boneOrder.indexOf(nodeSid);
			if (index == -1){
				index = boneOrder.indexOf(nodeId);
			}
		}

		// calculate inverse bind matrix in case it's a bone
        float[] inverseBindMatrix = null;
        if (index >= 0 && skinningData.inverseBindMatrix != null) {
            inverseBindMatrix = new float[16];
            Matrix.transposeM(inverseBindMatrix, 0, skinningData.inverseBindMatrix, index * 16);
        }

        return new JointData(index, nodeId, matrix, inverseBindMatrix);
	}
}
