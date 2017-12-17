package org.andresoviedo.app.model3D.services.collada.loader;

import android.opengl.Matrix;
import android.renderscript.Matrix4f;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

import org.andresoviedo.app.model3D.services.collada.entities.JointData;
import org.andresoviedo.app.model3D.services.collada.entities.SkeletonData;
import org.andresoviedo.app.util.xml.XmlNode;


public class SkeletonLoader {

	private XmlNode armatureData;
	
	private List<String> boneOrder;
	
	private int jointCount = 0;

	private static final float[] CORRECTION = new float[16];
	static{
		Matrix.setIdentityM(CORRECTION,0);
		Matrix.rotateM(CORRECTION,0,CORRECTION,0,-90, 1, 0, 0);
	}

	public SkeletonLoader(XmlNode visualSceneNode, List<String> boneOrder) {
		this.armatureData = visualSceneNode.getChild("visual_scene").getChildWithAttribute("node", "id", "Armature");
		this.boneOrder = boneOrder;
	}
	
	public SkeletonData extractBoneData(){
		XmlNode headNode = armatureData.getChildWithAttribute("node","type","JOINT");
		JointData headJoint = loadJointData(headNode, true);
		if (jointCount != boneOrder.size()){
			Log.e("SkeletonLoader","jointCount != boneOrder: "+jointCount+" != "+boneOrder.size());
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
		String nameId = jointNode.getAttribute("id");
		int index = boneOrder.indexOf(nameId);
		if (index == -1){
			Log.e("SkeletonLoader","Joint not found in order: "+nameId);
			boneOrder.add(nameId);
			index = boneOrder.indexOf(nameId);
		}
		String[] matrixData = jointNode.getChild("matrix").getData().trim().split("\\s+");
		Matrix4f matrix = new Matrix4f(convertData(matrixData));
		matrix.transpose();
		if(isRoot){
			//because in Blender z is up, but in our game y is up.
			Matrix4f correction = new Matrix4f(CORRECTION);
			correction.multiply(matrix);
			matrix = correction;
		}
		jointCount++;
		return new JointData(index, nameId, matrix.getArray());
	}
	
	private float[] convertData(String[] rawData){
		float[] matrixData = new float[16];
		for(int i=0;i<matrixData.length;i++){
			matrixData[i] = Float.parseFloat(rawData[i]);
		}
		return matrixData;
	}
}
