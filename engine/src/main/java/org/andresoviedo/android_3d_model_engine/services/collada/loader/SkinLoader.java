package org.andresoviedo.android_3d_model_engine.services.collada.loader;

import android.opengl.Matrix;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.services.collada.entities.SkinningData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.VertexSkinData;
import org.andresoviedo.util.math.Math3DUtils;
import org.andresoviedo.util.xml.XmlNode;

import java.util.ArrayList;
import java.util.Collections;
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

			// bind shape matrix
            float[] bindShapeMatrix = new float[16];
            Matrix.setIdentityM(bindShapeMatrix,0);
            XmlNode bindShapeMatrixNode = skinningData.getChild("bind_shape_matrix");
            if (bindShapeMatrixNode != null) {
                float[] temp = Math3DUtils.parseFloat(bindShapeMatrixNode.getData().trim().split("\\s+"));
                Matrix.transposeM(bindShapeMatrix, 0, temp, 0);
                Log.i("SkinLoader","Bind shape matrix set");
            }

            // Ordered joint list
            List<String> jointNames = loadJointNames();
            Log.i("SkinLoader","Joint names: "+jointNames);

            // Vertex weights
			float[] weights = loadWeights();

			// every vertex has 1 or more joints/weights associated
			XmlNode weightsDataNode = skinningData.getChild("vertex_weights");
			int[] effectorJointCounts = getEffectiveJointsCounts(weightsDataNode);

			// load skin data for every vertex
			List<VertexSkinData> vertexWeights = loadSkinData(weightsDataNode, effectorJointCounts, weights);

			// inverse bind matrix
			float[] inverseBindMatrix = null;
			try {
				XmlNode joints = skinningData.getChild("joints");
				XmlNode inverseBindMatrixNode = joints.getChildWithAttribute("input","semantic","INV_BIND_MATRIX");
				String invMatrixString = skinningData.getChildWithAttribute("source",
						"id",inverseBindMatrixNode.getAttribute("source").substring(1))
						.getChild("float_array").getData();
				Log.d("SkinLoader","invMatrix: "+invMatrixString.trim());
				inverseBindMatrix = Math3DUtils.parseFloat(invMatrixString.trim().split("\\s+"));
                Log.d("SkinLoader","Inverse bind matrix available");
			} catch (Exception e) {
				Log.d("SkinLoader","No inverse bind matrix available");
			}
			ret.put(source,new SkinningData(bindShapeMatrix, jointNames, vertexWeights, inverseBindMatrix));
		}
		Log.d("SkinLoader","Skinning datas '"+ret.keySet()+"'");
		return ret;
	}

	private List<String> loadJointNames() {
		XmlNode inputNode = skinningData.getChild("vertex_weights");
		String jointDataId = inputNode.getChildWithAttribute("input", "semantic", "JOINT").getAttribute("source")
				.substring(1);
		XmlNode jointsNode = skinningData.getChildWithAttribute("source", "id", jointDataId).getChild("Name_array");
		String[] names = jointsNode.getData().trim().split("\\s+");
		List<String> jointsList = new ArrayList<>();
        Collections.addAll(jointsList, names);
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

	private List<VertexSkinData> loadSkinData(XmlNode weightsDataNode, int[] counts, float[] weights) {
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
