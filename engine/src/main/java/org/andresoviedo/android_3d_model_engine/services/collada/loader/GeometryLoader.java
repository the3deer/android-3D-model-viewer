package org.andresoviedo.android_3d_model_engine.services.collada.loader;

import android.opengl.Matrix;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.services.collada.entities.JointData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.MeshData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.SkeletonData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.SkinningData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.Vector2f;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.Vector3f;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.Vector4f;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.Vertex;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.VertexSkinData;
import org.andresoviedo.util.xml.XmlNode;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Loads the mesh data for a model from a collada XML file.
 * @author Karl
 *
 */
public class GeometryLoader {
	
	private final XmlNode geometryNode;
	private final XmlNode materialsData;
	private final XmlNode effectsData;
	private final XmlNode imagesNode;
	private Map<String,SkinningData> skinningDataMap;
	private SkeletonData skeletonData;
	
	private float[] verticesArray;
	private float[] normalsArray;
	private float[] texturesArray;
	private int[] indicesArray;
	private int[] jointIdsArray;
	private float[] weightsArray;
	private FloatBuffer colorsBuffer;

	List<Vertex> vertices = new ArrayList<>();
	List<Vector2f> textures = new ArrayList<>();
	List<Vector3f> normals = new ArrayList<>();
	List<Integer> indices = new ArrayList<>();
	List<float[]> colors = new ArrayList<>();
	
	public GeometryLoader(XmlNode geometryNode, XmlNode materialsNode, XmlNode effectsNode, XmlNode imagesNode, Map<String,SkinningData> skinningData, SkeletonData skeletonData) {
		this.skinningDataMap = skinningData;
		this.geometryNode = geometryNode;
		this.materialsData = materialsNode;
		this.imagesNode = imagesNode;
		this.effectsData = effectsNode;
		this.skeletonData = skeletonData;
	}
	
	public List<MeshData> extractModelData(){
		List<MeshData> ret = new ArrayList<MeshData>();
		for (XmlNode geometry : geometryNode.getChildren("geometry")) {
			vertices.clear(); normals.clear(); textures.clear(); indices.clear(); colors.clear();
			String geometryId = geometry.getAttribute("id");
			Log.i("GeometryLoader","Loading geometry '"+geometryId+"'");
			XmlNode meshData = geometry.getChild("mesh");
			readRawData(meshData, geometryId);
			List<XmlNode> polys = meshData.getChildren("polylist");
			String texture = null;
			for (XmlNode poly : polys){
				String material = poly.getAttribute("material");
				float[] color = getMaterialColor(material);
				texture = color == null? getTexture(material) : null;
				assembleVertices(poly, color);
			}
			List<XmlNode> triangless = meshData.getChildren("triangles");
			for (XmlNode triangles : triangless){
				String material = triangles.getAttribute("material");
				float[] color = getMaterialColor(material);
				texture = color == null? getTexture(material) : null;
				assembleVertices(triangles, color);
			}
			Log.i("GeometryLoader","Texture '"+texture+"'");
			removeUnusedVertices();
			initArrays(geometryId);
			convertDataToArrays();
			convertIndicesListToArray();
			ret.add(new MeshData(geometryId, verticesArray, texturesArray, normalsArray, colorsBuffer,
					texture, indicesArray, jointIdsArray, weightsArray));
		}
		return ret;
	}

	private void readRawData(XmlNode meshData, String geometryId) {
		readPositions(meshData, geometryId);
		XmlNode polylist = meshData.getChild("polylist");
		XmlNode triangles = meshData.getChild("triangles");
		String normalsId = null;
		String texCoordsId = null;
		if (polylist != null) {
			normalsId = polylist.getChildWithAttribute("input", "semantic", "NORMAL")
					.getAttribute("source").substring(1);
			XmlNode childWithAttribute = polylist.getChildWithAttribute("input", "semantic", "TEXCOORD");
			if (childWithAttribute != null){
				texCoordsId = childWithAttribute.getAttribute("source").substring(1);
			}
		} else if (triangles != null){
			XmlNode normals = triangles.getChildWithAttribute("input", "semantic", "NORMAL");
			if (normals != null) {
				normalsId = normals.getAttribute("source").substring(1);
			}
			XmlNode childWithAttribute = triangles.getChildWithAttribute("input", "semantic", "TEXCOORD");
			if (childWithAttribute != null) {
				texCoordsId = childWithAttribute.getAttribute("source").substring(1);
			}
		}
		if (normalsId != null)
			readNormals(meshData, normalsId);
		if (texCoordsId != null){
			readTextureCoords(meshData, texCoordsId);
		}
		else{
			Log.i("GeometryLoader","No texture data found for '"+geometryId+"'");
		}
	}

	private JointData getJointData(JointData jointData, String geometryId){
		if (geometryId.equals(jointData.meshId)) {
			return jointData;
		}
		for (JointData childJointData : jointData.children) {
			JointData candidate = getJointData(childJointData, geometryId);
			if (candidate != null) return candidate;
		}
		return null;
	}

	private void readPositions(XmlNode meshData, String geometryId) {
		String positionsId = meshData.getChild("vertices").getChild("input").getAttribute("source").substring(1);
		XmlNode positionsData = meshData.getChildWithAttribute("source", "id", positionsId).getChild("float_array");
		int count = Integer.parseInt(positionsData.getAttribute("count"));
		String[] posData = positionsData.getData().trim().split("\\s+");
		for (int i = 0; i < count / 3; i++) {
			float x = Float.parseFloat(posData[i * 3]);
			float y = Float.parseFloat(posData[i * 3 + 1]);
			float z = Float.parseFloat(posData[i * 3 + 2]);

			float[] positionV = new float[]{x,y,z,1};

			// skinning data
			VertexSkinData weightsData = skinningDataMap != null && skinningDataMap.containsKey(geometryId) ?
					skinningDataMap.get(geometryId).verticesSkinData.get(vertices.size()) : null;

			// transform vertex according to bind_shape_matrix
			if (skinningDataMap.containsKey(geometryId)){
				float[] bindShapeMatrix = skinningDataMap.get(geometryId).bindShapeMatrix;
				float[] bindShaped = new float[16];
				Matrix.multiplyMV(bindShaped,0,bindShapeMatrix,0,positionV,0);
				positionV = bindShaped;
			}

			// TODO: review this. meshId is never set on skeleton data so this will probably never work
			if (weightsData == null && skeletonData != null){
				JointData jointData = getJointData(skeletonData.headJoint, geometryId);
				if (jointData != null) {
					weightsData = new VertexSkinData();
					weightsData.addJointEffect(jointData.index, 1);
					weightsData.limitJointNumber(3);
				}
			}

            vertices.add(vertices.size(), new Vertex(vertices.size(), new Vector3f(positionV[0],positionV[1],positionV[2]),
					weightsData));
		}
		Log.i("GeometryLoader", "Vertex count: " + vertices.size());

	}

	private void readNormals(XmlNode meshData, String normalsId) {
		XmlNode normalsData = meshData.getChildWithAttribute("source", "id", normalsId).getChild("float_array");
		int count = Integer.parseInt(normalsData.getAttribute("count"));
		String[] normData = normalsData.getData().trim().split("\\s+");
		for (int i = 0; i < count/3; i++) {
			float x = Float.parseFloat(normData[i * 3]);
			float y = Float.parseFloat(normData[i * 3 + 1]);
			float z = Float.parseFloat(normData[i * 3 + 2]);
			Vector4f norm = new Vector4f(x, y, z, 0f);
			norm = new Vector4f(norm.toArray());
			normals.add(new Vector3f(norm.x, norm.y, norm.z));
		}
	}

	private void readTextureCoords(XmlNode meshData, String texCoordsId) {
		XmlNode texCoordsData = meshData.getChildWithAttribute("source", "id", texCoordsId).getChild("float_array");
		int count = Integer.parseInt(texCoordsData.getAttribute("count"));
		String[] texData = texCoordsData.getData().trim().split("\\s+");
		for (int i = 0; i < count/2; i++) {
			float s = Float.parseFloat(texData[i * 2]);
			float t = Float.parseFloat(texData[i * 2 + 1]);
			textures.add(new Vector2f(s, t));
		}
	}
	
	private void assembleVertices(XmlNode poly, float[] color){
		int typeCount = 0;
		for (XmlNode input : poly.getChildren("input")){
			int offset = Integer.valueOf(input.getAttribute("offset"))+1;
			if (offset > typeCount){
				typeCount = offset;
			}
		}
		Log.i("GeometryLoader", "Loading polygon. Stride: " + typeCount);

		XmlNode texcoordSemantic = poly.getChildWithAttribute("input","semantic","TEXCOORD");
		int texcoordOffset = texcoordSemantic != null? Integer.parseInt(texcoordSemantic.getAttribute("offset")) : -1;

		String[] indexData = poly.getChild("p").getData().trim().split("\\s+");
		for (int i = 0; i < indexData.length / typeCount; i++) {
			int positionIndex = Integer.parseInt(indexData[i * typeCount]);
			int normalIndex = Integer.parseInt(indexData[i * typeCount + 1]);
			int texCoordIndex = -1;
			if (texcoordOffset != -1) {
				texCoordIndex = Integer.parseInt(indexData[i * typeCount + texcoordOffset]);
			}
			processVertex(positionIndex, normalIndex, texCoordIndex, color);
		}

	}
	

	private Vertex processVertex(int posIndex, int normIndex, int texIndex, float[] color) {
		Vertex currentVertex = vertices.get(posIndex);
		if (!currentVertex.isSet()) {
			currentVertex.setTextureIndex(texIndex);
			currentVertex.setNormalIndex(normIndex);
			indices.add(posIndex);
			if (color != null) colors.add(color);
			return currentVertex;
		} else {
			return dealWithAlreadyProcessedVertex(currentVertex, texIndex, normIndex, color);
		}
	}

	private float[] getMaterialColor(String material){
		try {
			XmlNode materialNode = materialsData.getChildWithAttribute("material","id",material);
			XmlNode instanceEffectNode = materialNode.getChild("instance_effect");
			String instanceEffectId = instanceEffectNode.getAttribute("url").substring(1);
			XmlNode effect = effectsData.getChildWithAttribute("effect","id", instanceEffectId);
			XmlNode profile_common = effect.getChild("profile_COMMON");
			XmlNode technique = profile_common.getChild("technique");
			XmlNode lambert = technique.getChild("lambert");
			if (lambert == null){
				lambert = technique.getChild("phong");
			}
			XmlNode diffuse = lambert.getChild("diffuse");
			XmlNode colorNode = diffuse.getChild("color");
			if (colorNode != null) {
				String[] color = colorNode.getData().trim().split("\\s+");
				return new float[]{Float.valueOf(color[0]), Float.valueOf(color[1]), Float.valueOf(color[2]), Float.valueOf(color[3])};
			}
			return null;
		} catch (Exception ex) {
			Log.e("GeometryLoader","No color found for material '"+material+"'",ex);
			return null;
		}
	}

	private String getTexture(String material){
		try {
			XmlNode materialNode = materialsData.getChildWithAttribute("material","id",material);
			XmlNode instanceEffectNode = materialNode.getChild("instance_effect");
			String instanceEffectId = instanceEffectNode.getAttribute("url").substring(1);
			XmlNode effect = effectsData.getChildWithAttribute("effect","id", instanceEffectId);
			XmlNode profile_common = effect.getChild("profile_COMMON");
			XmlNode technique = profile_common.getChild("technique");
			XmlNode lambert = technique.getChild("lambert");
			if (lambert == null){
				lambert = technique.getChild("phong");
			}
			XmlNode diffuse = lambert.getChild("diffuse");
			XmlNode textureNode = diffuse.getChild("texture");
			if (textureNode != null){
				String texture = textureNode.getAttribute("texture");
				XmlNode newParamNode = profile_common.getChildWithAttribute("newparam","sid",texture);
				if (newParamNode != null) {
					String surface = newParamNode.getChild("sampler2D").getChild("source").getData();
					newParamNode = profile_common.getChildWithAttribute("newparam", "sid", surface);
					String imageRef = newParamNode.getChildWithAttribute("surface", "type", "2D").getChild("init_from").getData();
					String image = imagesNode.getChildWithAttribute("image", "id", imageRef).getChild("init_from").getData();
					return image;
				} else {
                    String image = imagesNode.getChildWithAttribute("image", "id", texture).getChild("init_from").getData();
                    return image;
				}
			}
			return null;
		} catch (Exception ex) {
			Log.e("GeometryLoader","No texture found for material '"+material+"'",ex);
			return null;
		}
	}

	private int[] convertIndicesListToArray() {
		this.indicesArray = new int[indices.size()];
		for (int i = 0; i < indicesArray.length; i++) {
			indicesArray[i] = indices.get(i);
		}
		return indicesArray;
	}

	private float convertDataToArrays() {
		float furthestPoint = 0;
		int gw = 0, gj = 0; // global weights, global joints
		for (int i = 0; i < vertices.size(); i++) {
			Vertex currentVertex = vertices.get(i);
			if (currentVertex.getLength() > furthestPoint) {
				furthestPoint = currentVertex.getLength();
			}
			Vector3f position = currentVertex.getPosition();
			if (textures != null && !textures.isEmpty()) {
				Vector2f textureCoord = textures.get(currentVertex.getTextureIndex());
				texturesArray[i * 2] = textureCoord.x;
				texturesArray[i * 2 + 1] = 1 - textureCoord.y;
			}
			verticesArray[i * 3] = position.x;
			verticesArray[i * 3 + 1] = position.y;
			verticesArray[i * 3 + 2] = position.z;

			if (normals != null && !normals.isEmpty()) {
                Vector3f normalVector = normals.get(currentVertex.getNormalIndex());
                normalsArray[i * 3] = normalVector.x;
                normalsArray[i * 3 + 1] = normalVector.y;
                normalsArray[i * 3 + 2] = normalVector.z;
            }
			VertexSkinData weights = currentVertex.getWeightsData();
			if (weights != null) {
			    for (int j=0; j<weights.jointIds.size(); j++) {
                    jointIdsArray[gj++] = weights.jointIds.get(j);
                }
                for (int w=0; w<weights.weights.size(); w++) {
                    weightsArray[gw++] = weights.weights.get(w);
                }
			}
		}
		for (float[] color : colors){
			colorsBuffer.put(color);
		}
		return furthestPoint;
	}

	private Vertex dealWithAlreadyProcessedVertex(Vertex previousVertex, int newTextureIndex, int newNormalIndex, float[] color) {
		if (previousVertex.hasSameTextureAndNormal(newTextureIndex, newNormalIndex)) {
			indices.add(previousVertex.getIndex());
			return previousVertex;
		} else {
			Vertex anotherVertex = previousVertex.getDuplicateVertex();
			if (anotherVertex != null) {
				return dealWithAlreadyProcessedVertex(anotherVertex, newTextureIndex, newNormalIndex, color);
			} else {
				Vertex duplicateVertex = new Vertex(vertices.size(), previousVertex.getPosition(), previousVertex.getWeightsData());
				duplicateVertex.setTextureIndex(newTextureIndex);
				duplicateVertex.setNormalIndex(newNormalIndex);
				previousVertex.setDuplicateVertex(duplicateVertex);
				vertices.add(duplicateVertex);
				indices.add(duplicateVertex.getIndex());
				if (color != null) colors.add(color);
				return duplicateVertex;
			}

		}
	}
	
	private void initArrays(String geometryId){
		this.verticesArray = new float[vertices.size() * 3];
		if (textures != null && !textures.isEmpty()) {
			this.texturesArray = new float[vertices.size() * 2];
		}
		this.normalsArray = new float[vertices.size() * 3];
		if (skinningDataMap != null && skinningDataMap.containsKey(geometryId) || vertices.get(0).getWeightsData() != null) {
			this.jointIdsArray = new int[vertices.size() * vertices.get(0).getWeightsData().jointIds.size()];
			this.weightsArray = new float[vertices.size() * vertices.get(0).getWeightsData().weights.size()];
		}
		if (!colors.isEmpty())
			this.colorsBuffer = createNativeByteBuffer(colors.size()*4*4).asFloatBuffer();
	}

	private void removeUnusedVertices() {
		for (Vertex vertex : vertices) {
			vertex.averageTangents();
			if (!vertex.isSet()) {
				vertex.setTextureIndex(0);
				vertex.setNormalIndex(0);
			}
		}
	}

	private static ByteBuffer createNativeByteBuffer(int length) {
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(length);
		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());
		return bb;
	}
}