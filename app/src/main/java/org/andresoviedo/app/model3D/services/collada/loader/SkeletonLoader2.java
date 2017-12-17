package org.andresoviedo.app.model3D.services.collada.loader;

import android.opengl.Matrix;
import android.renderscript.Matrix4f;
import android.util.Log;

import org.andresoviedo.app.model3D.services.collada.entities.JointData;
import org.andresoviedo.app.model3D.services.collada.entities.SkeletonData;
import org.andresoviedo.app.util.xml.XmlNode;

import java.util.ArrayList;
import java.util.List;


public class SkeletonLoader2 {

	private XmlNode visualScene;

	private List<String> boneOrder = new ArrayList<String>();

	private int jointCount = 0;

	private static final float[] CORRECTION = new float[16];
	static{
		Matrix.setIdentityM(CORRECTION,0);
		Matrix.rotateM(CORRECTION,0,CORRECTION,0,-90, 1, 0, 0);
	}

	public SkeletonLoader2(XmlNode visualSceneNode) {
		this.visualScene = visualSceneNode.getChild("visual_scene");
	}
	
	public SkeletonData extractBoneData(){
		XmlNode node = visualScene.getChildWithAttribute("node","id","Armature");
		JointData headJoint = loadJointData(node, true);
		if (jointCount != boneOrder.size()) {
			Log.e("SkeletonLoader2", "jointCount != boneOrder: " + jointCount + " != " + boneOrder.size());
			jointCount = boneOrder.size();
		}
		return new SkeletonData(jointCount, headJoint);
	}
	
	private JointData loadJointData(XmlNode jointNode, boolean isRoot){
		JointData joint = extractMainJointData(jointNode, isRoot);
		for(XmlNode childNode : jointNode.getChildren("node")){
			joint.addChild(loadJointData(childNode, false));
		}
		return joint;
	}
	
	private JointData extractMainJointData(XmlNode jointNode, boolean isRoot){
		XmlNode instance_geometry = jointNode.getChild("instance_geometry");
		String meshId = instance_geometry != null? instance_geometry.getAttribute("url").substring(1) : null;
		String nameId = jointNode.getAttribute("id");
		int index = boneOrder.indexOf(nameId);
		if (index == -1){
			Log.e("SkeletonLoader2","Joint not found in order: "+nameId);
			boneOrder.add(nameId);
			index = boneOrder.indexOf(nameId);
		}
		String[] matrixData = jointNode.getChild("translate").getData().split(" ");
		float[] matrix = new float[16];
		Matrix.setIdentityM(matrix,0);
		Matrix.translateM(matrix,0,matrix,0,Float.valueOf(matrixData[0]),Float.valueOf(matrixData[1]),Float.valueOf(matrixData[2]));
		// Matrix.transposeM(matrix,0,matrix,0);
		if(isRoot){
			//because in Blender z is up, but in our game y is up.
			// Matrix.multiplyMM(matrix,0,CORRECTION,0,matrix,0);
		}
		jointCount++;
		return new JointData(index, nameId, matrix).setMeshId(meshId);
	}
	
	private float[] convertData(String[] rawData){
		float[] matrixData = new float[16];
		for(int i=0;i<matrixData.length;i++){
			matrixData[i] = Float.parseFloat(rawData[i]);
		}
		return matrixData;
	}
}
