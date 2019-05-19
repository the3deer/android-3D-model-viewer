package org.andresoviedo.android_3d_model_engine.services.collada.loader;

import android.opengl.Matrix;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.services.collada.entities.AnimationData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.JointTransformData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.KeyFrameData;
import org.andresoviedo.util.xml.XmlNode;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;


public class AnimationLoader {

	private XmlNode animationData;
	private XmlNode jointHierarchy;
	
	public AnimationLoader(XmlNode animationData, XmlNode jointHierarchy){
		this.animationData = animationData;
		this.jointHierarchy = jointHierarchy;
	}
	
	public AnimationData extractAnimation(){
		TreeSet<Float> times = getKeyTimes();
		Log.i("AnimationLoader","Key times: ("+times.size()+"): "+times);
		float duration = times.last();
        Log.i("AnimationLoader","Duration: "+duration);
		List<Float> keyTimes = new ArrayList<>(times);
		KeyFrameData[] keyFrames = initKeyFrames(keyTimes);
		List<XmlNode> animationNodes = animationData.getChildren("animation");
		Log.i("AnimationLoader","Animations: "+animationNodes.size());
		for(XmlNode animationNode : animationNodes){
			if (animationNode.getChild("animation") != null){
				animationNode = animationNode.getChild("animation");
			}
			loadJointTransforms(keyTimes, keyFrames, animationNode);
		}
		return new AnimationData(duration, keyFrames);
	}

    /**
     * Process all animations and combine all key frames in order to have the global list of times
     * @return
     */
	private TreeSet<Float> getKeyTimes(){
		TreeSet<Float> ret = new TreeSet<>();
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
	
	private void loadJointTransforms(List<Float> keyTimes, KeyFrameData[] frames, XmlNode animationNode){
		String[] channel = getChannel(animationNode);
		String jointNameId = channel[0];
		String transform = channel[1];
		String dataId = getDataId(animationNode);
		String timeId = getTimeId(animationNode);
		try {
			XmlNode timeData = animationNode.getChildWithAttribute("source", "id", timeId);
			String[] rawTimes = timeData.getChild("float_array").getData().trim().split("\\s+");
			XmlNode transformData = animationNode.getChildWithAttribute("source", "id", dataId);
			String[] rawData = transformData.getChild("float_array").getData().trim().split("\\s+");
			XmlNode technique_common = transformData.getChild("technique_common");
			XmlNode accessor = technique_common.getChild("accessor");
			if (accessor.getAttribute("stride").equals("16")) {
				processTransforms(jointNameId, rawTimes, rawData, keyTimes, frames);
			}
			else if (accessor.getAttribute("stride").equals("2")){
				processXYTransforms(jointNameId, rawTimes, rawData, keyTimes, frames);
			}
			else if (accessor.getAttribute("stride").equals("1")) {
				if (accessor.getChildWithAttribute("param", "name", "X") != null) {
					processXTransforms(jointNameId, rawTimes, rawData, keyTimes, frames);
				} else if (accessor.getChildWithAttribute("param", "name", "Z") != null) {
					processZTransforms(jointNameId, rawTimes, rawData, keyTimes, frames);
				} else if (transform.equals("rotationZ.ANGLE")) {
					processRotationZTransforms(jointNameId, rawTimes, rawData, keyTimes, frames);
				}
			}
            Log.d("AnimationLoader","Animation (key frames: "+rawTimes.length+") "+jointNameId);
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

	private void processTransforms(String jointName, String[] rawTimes, String[] rawData, List<Float> keyTimes, KeyFrameData[] keyFrames){
		float[] matrixData = new float[16];
		for(int i=0;i<rawTimes.length;i++){
			Float keyTime = Float.parseFloat(rawTimes[i]);
			for(int j=0;j<16;j++){
				matrixData[j] = Float.parseFloat(rawData[i*16 + j]);
			}
			float[] transpose = new float[16];
			Matrix.transposeM(transpose,0,matrixData,0);
			int keyFrameIndex = keyTimes.indexOf(keyTime);

			keyFrames[keyFrameIndex].addJointTransform(new JointTransformData(jointName, transpose));
		}
	}

	private void processXYTransforms(String jointName, String[] rawTimes, String[] rawData, List<Float> keyTimes, KeyFrameData[] keyFrames){
		for(int i=0;i<rawTimes.length;i++){
			Float keyTime = Float.parseFloat(rawTimes[i]);
			float[] matrixData = new float[16];
			Matrix.setIdentityM(matrixData,0);
			Matrix.translateM(matrixData,0,matrixData,0, Float.parseFloat(rawData[i*2 + 0]),Float.parseFloat(rawData[i*2 + 1]),0);
			keyFrames[keyTimes.indexOf(keyTime)].addJointTransform(new JointTransformData(jointName, matrixData));
		}
	}

	private void processXTransforms(String jointName, String[] rawTimes, String[] rawData, List<Float> keyTimes, KeyFrameData[] keyFrames){
		for(int i=0;i<rawTimes.length;i++){
			Float keyTime = Float.parseFloat(rawTimes[i]);
			float[] matrixData = new float[16];
			Matrix.setIdentityM(matrixData,0);
			Matrix.translateM(matrixData,0,matrixData,0, Float.parseFloat(rawData[i]), 0, 0);
			keyFrames[keyTimes.indexOf(keyTime)].addJointTransform(new JointTransformData(jointName, matrixData));
		}
	}

	private void processZTransforms(String jointName, String[] rawTimes, String[] rawData, List<Float> keyTimes, KeyFrameData[] keyFrames){
		for(int i=0;i<rawTimes.length;i++){
			Float keyTime = Float.parseFloat(rawTimes[i]);
			float[] matrixData = new float[16];
			Matrix.setIdentityM(matrixData,0);
			Matrix.translateM(matrixData,0,matrixData,0, 0, 0, Float.parseFloat(rawData[i]));
			keyFrames[keyTimes.indexOf(keyTime)].addJointTransform(new JointTransformData(jointName, matrixData));
		}
	}

	private void processRotationZTransforms(String jointName, String[] rawTimes, String[] rawData, List<Float> keyTimes, KeyFrameData[] keyFrames){
		for(int i=0;i<rawTimes.length;i++){
			Float keyTime = Float.parseFloat(rawTimes[i]);
			float[] matrixData = new float[16];
			Matrix.setIdentityM(matrixData,0);
			Matrix.rotateM(matrixData,0,matrixData,0, Float.parseFloat(rawData[i]), 0,1,0);
			keyFrames[keyTimes.indexOf(keyTime)].addJointTransform(new JointTransformData(jointName, matrixData));
		}
	}
}
