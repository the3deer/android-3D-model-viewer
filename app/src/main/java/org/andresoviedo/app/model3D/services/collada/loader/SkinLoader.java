package org.andresoviedo.app.model3D.services.collada.loader;

import android.util.Log;

import org.andresoviedo.app.model3D.services.collada.entities.SkinningData;
import org.andresoviedo.app.model3D.services.collada.entities.VertexSkinData;
import org.andresoviedo.app.util.xml.XmlNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SkinLoader {

	private final XmlNode controllersNode;
	private XmlNode skinningData;
	private final int maxWeights;

	public SkinLoader(XmlNode controllersNode, int maxWeights) {
		this.maxWeights = maxWeights;
		this.controllersNode = controllersNode;
	}

	public Map<String,SkinningData> extractSkinData() {
		Map<String,SkinningData> ret = new HashMap<String, SkinningData>();
		for (XmlNode controller : controllersNode.getChildren("controller")) {
			XmlNode skinningDataNode = controller.getChild("skin");
			this.skinningData = skinningDataNode;
			String source = skinningData.getAttribute("source").substring(1);
			List<String> jointsList = loadJointsList();
			float[] weights = loadWeights();
			XmlNode weightsDataNode = skinningData.getChild("vertex_weights");
			int[] effectorJointCounts = getEffectiveJointsCounts(weightsDataNode);
			List<VertexSkinData> vertexWeights = getSkinData(weightsDataNode, effectorJointCounts, weights);
			ret.put(source,new SkinningData(jointsList, vertexWeights));
		}
		Log.d("SkinLoader","Skinning datas '"+ret.keySet()+"'");
		return ret;
	}

	private List<String> loadJointsList() {
		XmlNode inputNode = skinningData.getChild("vertex_weights");
		String jointDataId = inputNode.getChildWithAttribute("input", "semantic", "JOINT").getAttribute("source")
				.substring(1);
		XmlNode jointsNode = skinningData.getChildWithAttribute("source", "id", jointDataId).getChild("Name_array");
		String[] names = jointsNode.getData().trim().split("\\s+");
		List<String> jointsList = new ArrayList<String>();
		for (String name : names) {
			jointsList.add(name);
		}
		return jointsList;
	}

	private float[] loadWeights() {
		XmlNode inputNode = skinningData.getChild("vertex_weights");
		String weightsDataId = inputNode.getChildWithAttribute("input", "semantic", "WEIGHT").getAttribute("source")
				.substring(1);
		XmlNode weightsNode = skinningData.getChildWithAttribute("source", "id", weightsDataId).getChild("float_array");
		String[] rawData = weightsNode.getData().trim().split("\\s+");
		float[] weights = new float[rawData.length];
		for (int i = 0; i < weights.length; i++) {
			weights[i] = Float.parseFloat(rawData[i]);
		}
		return weights;
	}

	private int[] getEffectiveJointsCounts(XmlNode weightsDataNode) {
		String[] rawData = weightsDataNode.getChild("vcount").getData().trim().split("\\s+");
		int[] counts = new int[rawData.length];
		for (int i = 0; i < rawData.length; i++) {
			counts[i] = Integer.parseInt(rawData[i]);
		}
		return counts;
	}

	private List<VertexSkinData> getSkinData(XmlNode weightsDataNode, int[] counts, float[] weights) {
		String[] rawData = weightsDataNode.getChild("v").getData().trim().split("\\s+");
		List<VertexSkinData> skinningData = new ArrayList<VertexSkinData>();
		int pointer = 0;
		for (int count : counts) {
			VertexSkinData skinData = new VertexSkinData();
			for (int i = 0; i < count; i++) {
				int jointId = Integer.parseInt(rawData[pointer++]);
				int weightId = Integer.parseInt(rawData[pointer++]);
				skinData.addJointEffect(jointId, weights[weightId]);
			}
			skinData.limitJointNumber(maxWeights);
			skinningData.add(skinData);
		}
		return skinningData;
	}

}
