package org.andresoviedo.app.model3D.services.collada.loader;

import android.opengl.Matrix;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import org.andresoviedo.app.model3D.services.collada.entities.AnimationData;
import org.andresoviedo.app.model3D.services.collada.entities.JointTransformData;
import org.andresoviedo.app.model3D.services.collada.entities.KeyFrameData;
import org.andresoviedo.app.util.xml.XmlNode;


public class AnimationLoader {

	private static final float[] CORRECTION = new float[16];
	static{
		Matrix.setIdentityM(CORRECTION,0);
		Matrix.rotateM(CORRECTION,0,CORRECTION,0,-90, 1, 0, 0);
	}
	
	private XmlNode animationData;
	private XmlNode jointHierarchy;
	
	public AnimationLoader(XmlNode animationData, XmlNode jointHierarchy){
		this.animationData = animationData;
		this.jointHierarchy = jointHierarchy;
	}
	
	public AnimationData extractAnimation(){
		String rootNode = findRootJointName();
		TreeSet<Float> times = getKeyTimes();
		float duration = times.last();
		List<Float> keyTimes = new ArrayList<Float>(times);
		KeyFrameData[] keyFrames = initKeyFrames(keyTimes);
		List<XmlNode> animationNodes = animationData.getChildren("animation");
		for(XmlNode jointNode : animationNodes){
			if (jointNode.getChild("animation") != null){
				jointNode = jointNode.getChild("animation");
			}
			loadJointTransforms(keyTimes, keyFrames, jointNode, rootNode);
		}
		Log.i("AnimationLoader","Animation duration: "+duration+", key frames("+keyFrames.length+"):"+times);
		return new AnimationData(duration, keyFrames);
	}
	
	private TreeSet<Float> getKeyTimes(){
		TreeSet<Float> ret = new TreeSet<Float>();
		for (XmlNode animation : animationData.getChildren("animation")) {
			if (animation.getChild("animation") != null) {
				animation = animation.getChild("animation");
			}
			XmlNode timeData = animation.getChild("source").getChild("float_array");
			String[] rawTimes = timeData.getData().trim().split("\\s+");
			for (int i = 0; i < rawTimes.length; i++) {
				ret.add(Float.parseFloat(rawTimes[i]));

			}
		}
		return ret;
	}
	
	private KeyFrameData[] initKeyFrames(List<Float> times){
		KeyFrameData[] frames = new KeyFrameData[times.size()];
		int i=0;
		for(Float time : times){
			frames[i++] = new KeyFrameData(time);
		}
		return frames;
	}
	
	private void loadJointTransforms(List<Float> keyTimes, KeyFrameData[] frames, XmlNode jointData, String rootNodeId){
		String[] channel = getChannel(jointData);
		String jointNameId = channel[0];
		String transform = channel[1];
		String dataId = getDataId(jointData);
		String timeId = getTimeId(jointData);
		try {
			XmlNode timeData = jointData.getChildWithAttribute("source", "id", timeId);
			String[] rawTimes = timeData.getChild("float_array").getData().trim().split("\\s+");
			XmlNode transformData = jointData.getChildWithAttribute("source", "id", dataId);
			String[] rawData = transformData.getChild("float_array").getData().trim().split("\\s+");
			XmlNode technique_common = transformData.getChild("technique_common");
			XmlNode accessor = technique_common.getChild("accessor");
			if (accessor.getAttribute("stride").equals("16")) {
				processTransforms(jointNameId, rawTimes, rawData, keyTimes, frames, jointNameId.equals(rootNodeId));
			}
			else if (accessor.getAttribute("stride").equals("2")){
				processXYTransforms(jointNameId, rawTimes, rawData, keyTimes, frames, jointNameId.equals(rootNodeId));
			}
			else if (accessor.getAttribute("stride").equals("1")) {
				if (accessor.getChildWithAttribute("param", "name", "X") != null) {
					processXTransforms(jointNameId, rawTimes, rawData, keyTimes, frames, jointNameId.equals(rootNodeId));
				} else if (accessor.getChildWithAttribute("param", "name", "Z") != null) {
					processZTransforms(jointNameId, rawTimes, rawData, keyTimes, frames, jointNameId.equals(rootNodeId));
				} else if (transform.equals("rotationZ.ANGLE")) {
					processRotationZTransforms(jointNameId, rawTimes, rawData, keyTimes, frames, jointNameId.equals(rootNodeId));
				}
			}
		} catch (Exception e) {
			Log.e("AnimationLoader","Problem loading animation for joint '"+jointNameId+"' with source '"+dataId+"'",e);
			throw new RuntimeException(e);
		}
	}

	private String getDataId(XmlNode jointData){
		XmlNode node = jointData.getChild("sampler").getChildWithAttribute("input", "semantic", "OUTPUT");
		return node.getAttribute("source").substring(1);
	}

	private String getTimeId(XmlNode jointData){
		XmlNode node = jointData.getChild("sampler").getChildWithAttribute("input", "semantic", "INPUT");
		return node.getAttribute("source").substring(1);
	}
	
	private String[] getChannel(XmlNode jointData){
		XmlNode channelNode = jointData.getChild("channel");
		String data = channelNode.getAttribute("target");
		return data.split("/");
	}
	
	private void processTransforms(String jointName, String[] rawTimes, String[] rawData, List<Float> keyTimes, KeyFrameData[] keyFrames, boolean root){
		float[] matrixData = new float[16];
		for(int i=0;i<rawTimes.length;i++){
			Float keyTime = Float.parseFloat(rawTimes[i]);
			for(int j=0;j<16;j++){
				matrixData[j] = Float.parseFloat(rawData[i*16 + j]);
			}
			float[] transpose = new float[16];
			Matrix.transposeM(transpose,0,matrixData,0);
			if(root){
				//because up axis in Blender is different to up axis in game
				Matrix.multiplyMM(transpose,0,CORRECTION,0,transpose,0);
			}
			int keyFrameIndex = keyTimes.indexOf(keyTime);
			keyFrames[keyFrameIndex].addJointTransform(new JointTransformData(jointName, transpose));
		}
	}

	private void processXYTransforms(String jointName, String[] rawTimes, String[] rawData, List<Float> keyTimes, KeyFrameData[] keyFrames, boolean root){
		for(int i=0;i<rawTimes.length;i++){
			Float keyTime = Float.parseFloat(rawTimes[i]);
			float[] matrixData = new float[16];
			Matrix.setIdentityM(matrixData,0);
			Matrix.translateM(matrixData,0,matrixData,0, Float.parseFloat(rawData[i*2 + 0]),Float.parseFloat(rawData[i*2 + 1]),0);
			if(root){
				//because up axis in Blender is different to up axis in game
				Matrix.multiplyMM(matrixData,0,CORRECTION,0,matrixData,0);
			}
			keyFrames[keyTimes.indexOf(keyTime)].addJointTransform(new JointTransformData(jointName, matrixData));
		}
	}

	private void processXTransforms(String jointName, String[] rawTimes, String[] rawData, List<Float> keyTimes, KeyFrameData[] keyFrames, boolean root){
		for(int i=0;i<rawTimes.length;i++){
			Float keyTime = Float.parseFloat(rawTimes[i]);
			float[] matrixData = new float[16];
			Matrix.setIdentityM(matrixData,0);
			Matrix.translateM(matrixData,0,matrixData,0, Float.parseFloat(rawData[i]), 0, 0);
			if(root){
				//because up axis in Blender is different to up axis in game
				Matrix.multiplyMM(matrixData,0,CORRECTION,0,matrixData,0);
			}
			keyFrames[keyTimes.indexOf(keyTime)].addJointTransform(new JointTransformData(jointName, matrixData));
		}
	}

	private void processZTransforms(String jointName, String[] rawTimes, String[] rawData, List<Float> keyTimes, KeyFrameData[] keyFrames, boolean root){
		for(int i=0;i<rawTimes.length;i++){
			Float keyTime = Float.parseFloat(rawTimes[i]);
			float[] matrixData = new float[16];
			Matrix.setIdentityM(matrixData,0);
			Matrix.translateM(matrixData,0,matrixData,0, 0, 0, Float.parseFloat(rawData[i]));
			if(root){
				//because up axis in Blender is different to up axis in game
				Matrix.multiplyMM(matrixData,0,CORRECTION,0,matrixData,0);
			}
			keyFrames[keyTimes.indexOf(keyTime)].addJointTransform(new JointTransformData(jointName, matrixData));
		}
	}

	private void processRotationZTransforms(String jointName, String[] rawTimes, String[] rawData, List<Float> keyTimes, KeyFrameData[] keyFrames, boolean root){
		for(int i=0;i<rawTimes.length;i++){
			Float keyTime = Float.parseFloat(rawTimes[i]);
			float[] matrixData = new float[16];
			Matrix.setIdentityM(matrixData,0);
			Matrix.rotateM(matrixData,0,matrixData,0, Float.parseFloat(rawData[i]), 0,1,0);
			keyFrames[keyTimes.indexOf(keyTime)].addJointTransform(new JointTransformData(jointName, matrixData));
		}
	}
	
	private String findRootJointName(){
		XmlNode skeleton = jointHierarchy.getChild("visual_scene").getChildWithAttribute("node", "id", "Armature");
		return skeleton.getChild("node").getAttribute("id");
	}
}
