package org.andresoviedo.android_3d_model_engine.services.collada.entities;

import java.util.List;
import java.util.Map;

/**
 * Contains the extracted data for an animated model, which includes the mesh data, and skeleton (joints heirarchy) data.
 * @author Karl
 *
 */
public class AnimatedModelData {

	private final SkeletonData joints;
	private final List<MeshData> mesh;
	private final Map<String, SkinningData> skinningData;

	public AnimatedModelData(List<MeshData> mesh, SkeletonData joints, Map<String, SkinningData> skinningData){
		this.joints = joints;
		this.mesh = mesh;
		this.skinningData = skinningData;
	}
	
	public SkeletonData getJointsData(){
		return joints;
	}
	
	public List<MeshData> getMeshData(){
		return mesh;
	}

	public Map<String, SkinningData> getSkinningData() {
		return skinningData;
	}
}
