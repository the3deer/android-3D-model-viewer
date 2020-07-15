package org.andresoviedo.android_3d_model_engine.services.collada.loader;

import android.opengl.Matrix;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.services.collada.entities.JointData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.SkeletonData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.SkinningData;
import org.andresoviedo.util.math.Math3DUtils;
import org.andresoviedo.util.xml.XmlNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SkeletonLoader {

	private final XmlNode xml;

	private final XmlNode geometries;

	private final XmlNode visualScene;

	private int jointCount = 0;
	private boolean jointFound = false;

	public SkeletonLoader(XmlNode xml) {
		this.xml = xml;
		this.visualScene = xml.getChild("library_visual_scenes").getChild("visual_scene");
		this.geometries = xml.getChild("library_geometries");
	}

	// <visual_scene>
	public SkeletonData loadJoints(){

		Log.i("SkeletonLoader", "Loading skeleton...");


		// a visual scene may contain several nodes of different kinds
		List<XmlNode> nodes = visualScene.getChildren("node");
		if (nodes == null || nodes.isEmpty()){
			return null;
		}

		// int index = boneOrder.size();

		String visualSceneId = visualScene.getAttribute("id");
		final JointData rootJoint = new JointData(visualSceneId);
		/*boneOrder.add(index, skeletonId);*/
		this.jointCount = 1;  // root counts

		// analyze all nodes to get skeleton
		for (XmlNode node : nodes){

			// get first skeleton found
			JointData jointData = loadSkeleton(node, rootJoint);
			if (jointData == null) continue;
			rootJoint.addChild(jointData);
		}

		// no skeleton found at all
		if (rootJoint.children.isEmpty()){
			Log.i("SkeletonLoader", "Skeleton not found");
			return null;
		}

		Log.i("SkeletonLoader", "Skeleton found. joints: " + jointCount);

		return new SkeletonData(jointCount, rootJoint);
	}

	private JointData loadSkeleton(XmlNode jointNode, JointData parent){
		JointData joint = createJointData(jointNode, parent);

		// Log.i("SkeletonLoader","Joint: index "+joint.index+", name: "+joint.nameId);
		for(XmlNode childNode : jointNode.getChildren("node")){
			JointData child = loadSkeleton(childNode, joint);
			joint.addChild(child);
		}
		return joint;
	}

	private JointData createJointData(XmlNode jointNode, JointData parent){

		// joint transformation initialization
        float[] bindLocalTransform = null;

        float[] bindLocalMatrix = null;
		Float[] bindLocalScale = null;
		Float[] bindLocalRotation = null;
		Float[] bindLocalLocation = null;

		// did we find any supported transformations?
		if (jointNode.getChild("matrix") != null) {
			XmlNode jointMatrix = jointNode.getChild("matrix");
			String data = jointMatrix.getData().trim();
			if (!data.equals("1 0 0 0 0 1 0 0 0 0 1 0 0 0 0 1")) {
				float[] matrix1 = Math3DUtils.parseFloat(data.split("\\s+"));
				bindLocalTransform = new float[16];
				Matrix.transposeM(bindLocalTransform, 0, matrix1, 0);

				// local matrix
				bindLocalMatrix = new float[16];
				Matrix.transposeM(bindLocalMatrix, 0, matrix1, 0);
			}
        }

		if (jointNode.getChild("translate") != null) {
			XmlNode translateNode = jointNode.getChild("translate");
			String data = translateNode.getData().trim().replace(',', '.');
			if (!data.equals("0 0 0")) {
				float[] translate = Math3DUtils.parseFloat(data.split("\\s+"));
				bindLocalTransform = Math3DUtils.initMatrixIfNull(bindLocalTransform);
				Matrix.translateM(bindLocalTransform, 0, translate[0], translate[1], translate[2]);

				// bind local location
				bindLocalLocation = new Float[3];
				bindLocalLocation[0] = translate[0];
				bindLocalLocation[1] = translate[1];
				bindLocalLocation[2] = translate[2];
			}
		}

		if (jointNode.getChild("rotate") != null) {
			for (XmlNode rotateNode : jointNode.getChildren("rotate")) {
				String data = rotateNode.getData().trim();
				if (data.equals("0 0 0 0")) continue;
				float[] rotation = Math3DUtils.parseFloat(data.split("\\s+"));
				bindLocalTransform = Math3DUtils.initMatrixIfNull(bindLocalTransform);
				Matrix.rotateM(bindLocalTransform, 0, rotation[3], rotation[0], rotation[1], rotation[2]);
			}

			// local rotation
			bindLocalRotation = new Float[3];
			for (XmlNode rotateNode : jointNode.getChildren("rotate")) {
				String data = rotateNode.getData().trim();
				if (data.equals("0 0 0 0")) continue;
				float[] rotation = Math3DUtils.parseFloat(data.split("\\s+"));
				if (rotation[0] == 1f) bindLocalRotation[0] = rotation[3];
				if (rotation[1] == 1f) bindLocalRotation[1] = rotation[3];
				if (rotation[2] == 1f) bindLocalRotation[2] = rotation[3];
			}
		}

		if (jointNode.getChild("scale") != null){
			XmlNode scaleNode = jointNode.getChild("scale");
			String data = scaleNode.getData().trim();
			if (!data.equals("1 1 1")) {
				float[] scale = Math3DUtils.parseFloat(data.replace(',', '.').split("\\s+"));
				bindLocalTransform = Math3DUtils.initMatrixIfNull(bindLocalTransform);
				Matrix.scaleM(bindLocalTransform, 0, scale[0], scale[1], scale[2]);

				// local scale
				bindLocalScale = new Float[3];
				bindLocalScale[0] = scale[0];
				bindLocalScale[1] = scale[1];
				bindLocalScale[2] = scale[2];
			}
		}

		// if no transformation was found, then this is not part of the skeleton transformation
		if (bindLocalTransform == null){
			bindLocalTransform = Math3DUtils.IDENTITY_MATRIX;
		}

		jointCount++;

		// get node attributes
		String nodeName = jointNode.getAttribute("name");
		String nodeSid = jointNode.getAttribute("sid");
		String nodeId = jointNode.getAttribute("id");
		String geometryId = null;
		Map<String,String> materials = new HashMap<>();
		XmlNode instance_geometry_node = jointNode.getChild("instance_geometry");
		if (instance_geometry_node == null){
			instance_geometry_node = jointNode.getChild("instance_controller");
		}
		if (instance_geometry_node != null){
			if (instance_geometry_node.getAttribute("url") != null) {
				geometryId = instance_geometry_node.getAttribute("url").substring(1);
				if (geometries.getChildWithAttribute("geometry", "id", geometryId) == null){
					geometryId = null;
				}
			}

			try {
				XmlNode bind_material = instance_geometry_node.getChild("bind_material");
				if (bind_material != null){
					XmlNode technique_common = bind_material.getChild("technique_common");
					if (technique_common != null){
						XmlNode instance_material = technique_common.getChild("instance_material");
						if (instance_material != null){
							String material_symbol = instance_material.getAttribute("symbol");
							String material_name = instance_material.getAttribute("target").substring(1);
							materials.put(material_symbol,material_name);
							Log.v("SkeletonLoader",String.format("Loaded material: " +
									"%s->%s",material_symbol,material_name));
						}
					}
				}
			} catch (Exception e) {
				Log.e("SkeletonLoader","Error loading material bindings... "+e.getMessage());
			}
		}

		// is this a joint bone?
		if ("JOINT".equals(jointNode.getAttribute("type"))){
			jointFound = true;
		}

		float[] bindTransform = Math3DUtils.IDENTITY_MATRIX;
        if (parent.getBindTransform() != Math3DUtils.IDENTITY_MATRIX || bindLocalTransform != Math3DUtils.IDENTITY_MATRIX) {
			bindTransform = new float[16];
       		Matrix.multiplyMM(bindTransform, 0, parent.getBindTransform(), 0, bindLocalTransform, 0);
		}

        return new JointData(nodeId, nodeName, nodeSid, bindLocalMatrix, bindLocalScale, bindLocalRotation, bindLocalLocation, bindLocalTransform, bindTransform, geometryId, materials
		);
	}

	public void updateJointData(JointData rootJoint, Map<String, SkinningData> skinningDataMap, SkeletonData skeletonData) {
		List<String> boneOrder = new ArrayList<>();
		SkinningData skinningData = null;
		if (skinningDataMap != null && skinningDataMap.size() > 0) {
			skinningData = skinningDataMap.values().iterator().next();
			boneOrder = skinningData.jointOrder;
		}

		for (JointData jointData : rootJoint.children){
			updateChildJointData(jointData, skinningDataMap,skeletonData, boneOrder);
		}

		// log event
		StringBuilder jointIndicesString = new StringBuilder();
		List<JointData> pending = new ArrayList<>();
		pending.add(rootJoint);
		while(!pending.isEmpty()){
			JointData current = pending.get(0);
			if (current.getIndex() != -1) {
				jointIndicesString.append(current.getName() != null? current.getName():current.getId())
						.append(":").append(current.getIndex()).append(", ");
			}
			pending.addAll(current.children);
			pending.remove(0);
		}
		Log.i("SkeletonLoader", "Loaded joint indices: "+jointIndicesString);
	}

	private void updateChildJointData(JointData childJoint, Map<String, SkinningData> skinningDataMap, SkeletonData skeletonData, List<String> boneOrder) {
		upateJointData_impl(childJoint, skinningDataMap,skeletonData, boneOrder);
		for (JointData jointData : childJoint.children){
			updateChildJointData(jointData, skinningDataMap,skeletonData, boneOrder);
		}
	}

	private void upateJointData_impl(JointData jointData, Map<String, SkinningData> skinningDataMap, SkeletonData skeletonData, List<String> boneOrder){

		Log.v("SkeletonLoader", "Updating joint: "+jointData.getId());

		SkinningData skinningData = null;
		if (skinningDataMap != null && skinningDataMap.size() > 0) {
			skinningData = skinningDataMap.values().iterator().next();
		}

		final String nodeName = jointData.getName();
		final String nodeSid = jointData.getSid();
		final String nodeId = jointData.getId();
		final String geometryId = jointData.getGeometryId();

		// index is only available for declared bones
		int index = boneOrder.indexOf(nodeName);
		if (index == -1) {
			// fallback to node id
			index = boneOrder.indexOf(nodeSid);
			if (index == -1) {
				index = boneOrder.indexOf(nodeId);
			}
		}

		// calculate inverse bind matrix in case it's a bone
		float[] inverseBindMatrix = Math3DUtils.IDENTITY_MATRIX;;
		if (index >= 0 && skinningData.getInverseBindMatrix() != null) {
			inverseBindMatrix = new float[16];
			Matrix.transposeM(inverseBindMatrix, 0, skinningData.getInverseBindMatrix(), index * 16);
		}

		if (index == -1 && geometryId != null) {
			index = boneOrder.size();
			boneOrder.add(geometryId);
			Log.d("SkeletonLoader","Linked geometry have new index: "+geometryId);
		}
		if (index == -1){
			Log.v("SkeletonLoader", "Found unlinked node. " + nodeId);
		} else{
			skeletonData.incrementBoneCount();
		}

		jointData.setIndex(index);
		jointData.setInverseBindTransform(inverseBindMatrix);
	}
}
