package org.andresoviedo.app.model3D.services.collada.loader;

import android.opengl.Matrix;
import android.renderscript.Matrix4f;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;

import org.andresoviedo.app.model3D.services.collada.entities.AnimationData;
import org.andresoviedo.app.model3D.services.collada.entities.JointTransformData;
import org.andresoviedo.app.model3D.services.collada.entities.KeyFrameData;
import org.andresoviedo.app.model3D.services.collada.entities.Vector3f;
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
		float[] times = getKeyTimes();
		float duration = times[times.length-1];
		KeyFrameData[] keyFrames = initKeyFrames(times);
		List<XmlNode> animationNodes = animationData.getChildren("animation");
		for(XmlNode jointNode : animationNodes){
			loadJointTransforms(keyFrames, jointNode, rootNode);
		}
		Log.i("AnimationLoader","Animation duration: "+duration+", key frames: "+keyFrames.length);
		return new AnimationData(duration, keyFrames);
	}
	
	private float[] getKeyTimes(){
		// TODO: there are multiple animations with different key frames
		XmlNode timeData = animationData.getChild("animation").getChild("source").getChild("float_array");
		String[] rawTimes = timeData.getData().split(" ");
		float[] times = new float[rawTimes.length];
		for(int i=0;i<times.length;i++){
			times[i] = Float.parseFloat(rawTimes[i]);
		}
		return times;
	}
	
	private KeyFrameData[] initKeyFrames(float[] times){
		KeyFrameData[] frames = new KeyFrameData[times.length];
		for(int i=0;i<frames.length;i++){
			frames[i] = new KeyFrameData(times[i]);
		}
		return frames;
	}
	
	private void loadJointTransforms(KeyFrameData[] frames, XmlNode jointData, String rootNodeId){
		String jointNameId = getJointName(jointData);
		String dataId = getDataId(jointData);
		XmlNode transformData = jointData.getChildWithAttribute("source", "id", dataId);
		String[] rawData = transformData.getChild("float_array").getData().split(" ");
		XmlNode technique_common = transformData.getChild("technique_common");
		XmlNode accessor = technique_common.getChild("accessor");
		if (accessor.getAttribute("stride").equals("16")) {
			processTransforms(jointNameId, rawData, frames, jointNameId.equals(rootNodeId));
		}
		else if (accessor.getAttribute("stride").equals("2")){
			processXYTransforms(jointNameId, rawData, frames, jointNameId.equals(rootNodeId));
		}
		else if (accessor.getAttribute("stride").equals("1")) {
			if (accessor.getChildWithAttribute("param", "name", "X") != null) {
				processXTransforms(jointNameId, rawData, frames, jointNameId.equals(rootNodeId));
			} else if (accessor.getChildWithAttribute("param", "name", "Z") != null) {
				processZTransforms(jointNameId, rawData, frames, jointNameId.equals(rootNodeId));
			}
		}
	}

	private String getDataId(XmlNode jointData){
		XmlNode node = jointData.getChild("sampler").getChildWithAttribute("input", "semantic", "OUTPUT");
		return node.getAttribute("source").substring(1);
	}

	private String getDataId2(XmlNode jointData){
		XmlNode node = jointData.getChild("sampler").getChildWithAttribute("input", "semantic", "OUT_TANGENT");
		return node.getAttribute("source").substring(1);
	}
	
	private String getJointName(XmlNode jointData){
		XmlNode channelNode = jointData.getChild("channel");
		String data = channelNode.getAttribute("target");
		return data.split("/")[0];
	}
	
	private void processTransforms(String jointName, String[] rawData, KeyFrameData[] keyFrames, boolean root){
		float[] matrixData = new float[16];
		for(int i=0;i<keyFrames.length;i++){
			for(int j=0;j<16;j++){
				matrixData[j] = Float.parseFloat(rawData[i*16 + j]);
			}
			float[] transpose = new float[16];
			Matrix.transposeM(transpose,0,matrixData,0);
			if(root){
				//because up axis in Blender is different to up axis in game
				Matrix.multiplyMM(transpose,0,CORRECTION,0,transpose,0);
			}
			keyFrames[i].addJointTransform(new JointTransformData(jointName, transpose));
		}
	}

	private void processXYTransforms(String jointName, String[] rawData, KeyFrameData[] keyFrames, boolean root){
		float[] matrixData = new float[16];
		Matrix.setIdentityM(matrixData,0);
		for(int i=0;i<keyFrames.length;i++){
			Matrix.translateM(matrixData,0,matrixData,0, Float.parseFloat(rawData[i*2 + 0]),Float.parseFloat(rawData[i*2 + 1]),0);
			float[] transpose = new float[16];
			Matrix.transposeM(transpose,0,matrixData,0);
			if(root){
				//because up axis in Blender is different to up axis in game
				Matrix.multiplyMM(transpose,0,CORRECTION,0,transpose,0);
			}
			keyFrames[i].addJointTransform(new JointTransformData(jointName, transpose));
		}
	}

	private void processXTransforms(String jointName, String[] rawData, KeyFrameData[] keyFrames, boolean root){
		for(int i=0;i<keyFrames.length;i++){
			float[] matrixData = new float[16];
			Matrix.setIdentityM(matrixData,0);
			Matrix.translateM(matrixData,0,matrixData,0, Float.parseFloat(rawData[i]), 0, 0);
			float[] transpose = new float[16];
			Matrix.transposeM(transpose,0,matrixData,0);
			if(root){
				//because up axis in Blender is different to up axis in game
				Matrix.multiplyMM(matrixData,0,CORRECTION,0,transpose,0);
			}
			keyFrames[i].addJointTransform(new JointTransformData(jointName, matrixData));
			Log.d("AnimationLoader",jointName+"["+i+"]:"+ Arrays.toString(matrixData));
		}
	}

	private void processZTransforms(String jointName, String[] rawData, KeyFrameData[] keyFrames, boolean root){
		for(int i=0;i<keyFrames.length;i++){
			float[] matrixData = new float[16];
			Matrix.setIdentityM(matrixData,0);
			Matrix.translateM(matrixData,0,matrixData,0, 0, 0, Float.parseFloat(rawData[i]));
			float[] transpose = new float[16];
			Matrix.transposeM(transpose,0,matrixData,0);
			if(root){
				//because up axis in Blender is different to up axis in game
				Matrix.multiplyMM(matrixData,0,CORRECTION,0,transpose,0);
			}
			keyFrames[i].addJointTransform(new JointTransformData(jointName, matrixData));
			Log.d("AnimationLoader",jointName+"["+i+"]:"+ Arrays.toString(matrixData));
		}
	}
	
	private String findRootJointName(){
		XmlNode skeleton = jointHierarchy.getChild("visual_scene").getChildWithAttribute("node", "id", "Armature");
		return skeleton.getChild("node").getAttribute("id");
	}
}
