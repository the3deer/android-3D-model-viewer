package org.andresoviedo.android_3d_model_engine.services.collada.entities;

import java.util.List;

/**
 * Contains the extracted data for an animated model, which includes the mesh data, and skeleton (joints heirarchy) data.
 * @author Karl
 *
 */
public class AnimatedModelData {

	private final SkeletonData joints;
	private final List<MeshData> mesh;
	
	public AnimatedModelData(List<MeshData> mesh, SkeletonData joints){
		this.joints = joints;
		this.mesh = mesh;
	}
	
	public SkeletonData getJointsData(){
		return joints;
	}
	
	public List<MeshData> getMeshData(){
		return mesh;
	}
	
}
