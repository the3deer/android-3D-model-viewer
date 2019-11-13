package org.andresoviedo.android_3d_model_engine.services.collada.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Contains the extracted data for a single joint in the model. This stores the
 * joint's index, name, and local bind transform.
 * 
 * @author Karl
 *
 */
public class JointData {

	// index referenced by sknning data
	// the order may need to be provided by the bone ordered list
	public int index;

	// attributes
	private final String id;
	private final String name;
    private final String instance_geometry;
    private final Map<String,String> materials;

	// sum up of all matrix up to the "root"
	private final float[] bindLocalTransform;
	private final float[] bindTransform;
    private float[] inverseBindTransform;

	public final List<JointData> children = new ArrayList<>();

	public JointData(int index, String id, String name,
					 String geometryId, Map<String,String> materials, final float[] bindLocalTransform, final float[]
							 bindTransform, final float[] inverseBindTransform) {
		this.id = id;
        this.name = name;
        this.instance_geometry = geometryId;
		this.materials = materials;
		this.bindLocalTransform = bindLocalTransform;
        this.bindTransform = bindTransform;
        this.inverseBindTransform = inverseBindTransform;
        this.index = index;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void addChild(JointData child) {
		children.add(child);
	}

	public JointData find(String id) {
		if (id.equals(this.getId())) {
			return this;
		} else if (id.equals(this.getName())){
			return this;
		} else if (id.equals(this.instance_geometry)){
			return this;
		}

		for (JointData childJointData : this.children) {
			JointData candidate = childJointData.find(id);
			if (candidate != null) return candidate;
		}
		return null;
	}

	public boolean containsMaterial(String materialId){
		return materials.containsKey(materialId);
	}

	public String getMaterial(String materialId){
		return materials.get(materialId);
	}

	public float[] getBindTransform() {
		return bindTransform;
	}

	public float[] getBindLocalTransform() {
		return bindLocalTransform;
	}

	public float[] getInverseBindTransform() {
		return inverseBindTransform;
	}

	public void setInverseBindTransform(float[] inverseBindTransform) {
		this.inverseBindTransform = inverseBindTransform;
	}

	@Override
	public String toString() {
		return "JointData{" +
				"index=" + index +
				", id='" + id + '\'' +
				", name='" + name + '\'' +
				'}';
	}
}
