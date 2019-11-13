package org.andresoviedo.android_3d_model_engine.services.collada.loader;

import android.opengl.Matrix;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.services.collada.entities.JointData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.MeshData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.SkeletonData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.SkinningData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.Vertex;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.VertexSkinData;
import org.andresoviedo.util.xml.XmlNode;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private FloatBuffer normalsBuffer;
	private FloatBuffer colorsBuffer;

	List<Vertex> vertices = new ArrayList<>();

	List<float[]> vertex = new ArrayList<>();
	List<float[]> textures = new ArrayList<>();
	List<float[]> normals = new ArrayList<>();
	List<Integer> indices = new ArrayList<>();
	List<float[]> colors = new ArrayList<>();

	// FIXME: clear this list - only tu debug !!!
	private final Set<String> includeGeometries = new HashSet<>();
	{
		/*includeGeometries.add("ID3393");
		includeGeometries.add("ID173");
        includeGeometries.add("ID23");
        includeGeometries.add("Cube-mesh"); // cowboy
        includeGeometries.add("U3DMesh-lib"); // raptor attack
        includeGeometries.add("Stormtroopermesh-mesh"); // stormtrooper
        includeGeometries.add("defaultmesh-mesh");
        includeGeometries.add("defaultmesh_001-mesh");
        includeGeometries.add("Astronaut_meshmesh_001-mesh");
        includeGeometries.clear();*/
	}

    boolean textureLinked = false;
	boolean colorsLinked = false;
	
	public GeometryLoader(XmlNode geometryNode, XmlNode materialsNode, XmlNode effectsNode, XmlNode imagesNode, Map<String,SkinningData> skinningData, SkeletonData skeletonData) {
		this.skinningDataMap = skinningData;
		this.geometryNode = geometryNode;
		this.materialsData = materialsNode;
		this.imagesNode = imagesNode;
		this.effectsData = effectsNode;
		this.skeletonData = skeletonData;
	}

	public List<MeshData> extractModelData(){
		Log.i("GeometryLoader","Loading geometries...");
		List<MeshData> ret = new ArrayList<>();
		for (XmlNode geometry : geometryNode.getChildren("geometry")) {


			String geometryId = geometry.getAttribute("id");
			String geometryName = geometry.getAttribute("name");
			if (!includeGeometries.isEmpty() && !includeGeometries.contains(geometryId)
					&& !includeGeometries.contains(geometryName)) {
				Log.d("GeometryLoader","Geometry ignored: "+geometryId);
				continue;
			}
			Log.i("GeometryLoader", "Loading geometry '" + geometryId + " ("+geometryName+")'...");

			vertices.clear(); vertex.clear();
			normals.clear(); textures.clear();
			indices.clear(); colors.clear();

			// process mesh...
			XmlNode meshData = geometry.getChild("mesh");

			// read vertices and normals
			textureLinked = false;
			colorsLinked = false;
            loadVertices(meshData, geometryId);
            if(vertices.isEmpty()){
            	Log.i("GeometryLoader","Ignoring geometry since it has no vertices: "+geometryId);
            	continue;
			}

            // read texture and normals

			XmlNode primitive = loadPrimitiveData(meshData);

			// default is no color, no texture
			Object[] colorAndTexture = new Object[2];

			// get all primitives
			List<XmlNode> polys = meshData.getChildren("polylist");
			if (!polys.isEmpty()) {
				Log.d("GeometryLoader", "Found polylist. size: " + polys.size());

				for (XmlNode poly : polys) {

					assembleVertices(poly);

					// process material
					String material = poly.getAttribute("material");
					if (material != null) {
						colorAndTexture = getMaterialColorAndTexture(material);
						if (colorAndTexture[0] == null) {
							JointData jointData = skeletonData.find(geometryId);
							if (jointData == null && geometryName != null){
								jointData = skeletonData.find(geometryName);
							}
							if (jointData != null && jointData.containsMaterial(material)) {
								colorAndTexture = getMaterialColorAndTexture(jointData.getMaterial(material));
							} else {
								Log.e("GeometryLoader", "Material for poly not found: " + material);
							}
						}
					}
				}
			}

			// triangle mesh
			List<XmlNode> triangless = meshData.getChildren("triangles");
			if (!triangless.isEmpty()) {
				Log.d("GeometryLoader", "Found triangles. size: " + triangless.size());

				for (XmlNode triangles : triangless) {

					assembleVertices(triangles);

					// process material
					String material = triangles.getAttribute("material");
					if (material != null) {
						colorAndTexture = getMaterialColorAndTexture(material);
						if (colorAndTexture[0] == null) {
							JointData jointData = skeletonData.find(geometryId);
							if (jointData == null && geometryName != null){
								jointData = skeletonData.find(geometryName);
							}
							Log.v("GeometryLoader", "joint data for geometry: " + geometryId + ":"+jointData);
							if (jointData != null && jointData.containsMaterial(material)) {
								colorAndTexture = getMaterialColorAndTexture(jointData.getMaterial(material));
							} else {
								Log.e("GeometryLoader", "Material for triangle not found: " + material);
							}
						}
					}
				}
			}

			// triangle mesh
			List<XmlNode> polygons = meshData.getChildren("polygons");
			if (!polygons.isEmpty()) {
				Log.d("GeometryLoader", "Found polygons. size: " + polygons.size());
				for (XmlNode polygon : polygons) {

					assembleVertices(polygon);

					// process material
					String material = polygon.getAttribute("material");
					if (material != null) {
						colorAndTexture = getMaterialColorAndTexture(material);
						if (colorAndTexture[0] == null) {
							JointData jointData = skeletonData.find(geometryId);
							if (jointData == null && geometryName != null){
								jointData = skeletonData.find(geometryName);
							}
							if (jointData != null && jointData.containsMaterial(material)) {
								colorAndTexture = getMaterialColorAndTexture(jointData.getMaterial(material));
							} else {
								Log.e("GeometryLoader", "Material for polygon not found: " + material);
							}
						}
					}
				}
			}

			if (polygons.isEmpty() && triangless.isEmpty() && polys.isEmpty()){
				Log.e("GeometryLoader","Mesh with no face info: "+meshData.getName());
				continue;
			}

			Log.d("GeometryLoader","Assembly mesh...");
			loadSkinningData(geometryId);
			initArrays(geometryId);
			convertDataToArrays();
			convertIndicesListToArray();

			float[] color = (float[]) colorAndTexture[0];
			String texture = (String) colorAndTexture[1];
			ret.add(new MeshData(geometryId, verticesArray, texturesArray, normalsArray, color, colorsBuffer,
					texture, indicesArray, jointIdsArray, weightsArray, normalsBuffer));

			Log.d("GeometryLoader","Geometry loaded. vertices: "+vertices.size()+
					", normals: "+(normals != null? normals.size():0)+
					", textures: "+(textures != null? textures.size():0)+
					", colors: "+(colors != null? colors.size(): 0));
			Log.d("GeometryLoader","Geometry loaded. indices: "+(indices != null? indices.size() : 0)+
					", jointIds: "+(jointIdsArray != null? jointIdsArray.length :0)+
					", weights: "+(weightsArray != null? weightsArray.length :0));
		}
		return ret;
	}

	private XmlNode loadPrimitiveData(XmlNode meshData) {

		// get actual primitive
		XmlNode primitiveNode = null;
		if (meshData.getChild("polylist") != null) {
			primitiveNode = meshData.getChild("polylist");
		} else if (meshData.getChild("triangles") != null) {
			primitiveNode = meshData.getChild("triangles");
		} else if (meshData.getChild("polygons") != null){
			primitiveNode = meshData.getChild("polygons");
		}

		// load primitive data
		if (primitiveNode != null){
			XmlNode inputNormal = primitiveNode.getChildWithAttribute("input", "semantic", "NORMAL");
			loadData(normals, meshData, inputNormal, 3);
			XmlNode inputCoord = primitiveNode.getChildWithAttribute("input", "semantic", "TEXCOORD");
			loadData(textures, meshData, inputCoord, 2);
			XmlNode inputColor = primitiveNode.getChildWithAttribute("input", "semantic", "COLOR");
			loadData(colors, meshData, inputColor, 4);
		}

		return primitiveNode;
	}

	// <vertices> - may contain "VERTEX" and "NORMAL" semantics
	private void loadVertices(XmlNode meshData, String geometryId) {

	    // get position & normal source ids
        XmlNode verticesNode = meshData.getChild("vertices");
        for (XmlNode node : verticesNode.getChildren("input")){
            String semanticId = node.getAttribute("semantic");
            if ("POSITION".equals(semanticId)){
				loadData(vertex,meshData,node,3);
            } else if ("NORMAL".equals(semanticId)){
				loadData(normals,meshData,node,3);
            } else if ("TEXCOORD".equals(semanticId)){
				loadData(textures,meshData,node,2);
				textureLinked = true;
			}
        }

        // load vertices
        for (int i=0; vertex != null && i<vertex.size(); i++){
        	vertices.add(new Vertex(vertex.get(i)));
		}

		// there are vertices, normals that are not pointed by any polylist index
		for (int i=0; normals != null && i<vertices.size(); i++){
			vertices.get(i).setNormalIndex(i);
		}
		for (int i=0; textures != null && i<vertices.size(); i++){
			vertices.get(i).setTextureIndex(i);
		}
    }

    private void loadSkinningData(final String geometryId) {

		float[] bindShapeMatrix = null;
		if (skinningDataMap != null && skinningDataMap.containsKey(geometryId)) {
			bindShapeMatrix = skinningDataMap.get(geometryId).getBindShapeMatrix();
		}

		List<VertexSkinData> verticesSkinData = null;
		if (skinningDataMap == null || !skinningDataMap.containsKey(geometryId)) {
			Log.d("GeometryLoader","No skinning data available");
		} else {
			verticesSkinData = skinningDataMap.get(geometryId).verticesSkinData;
		}

		JointData jointData = null;
		if (skeletonData != null){
			jointData = skeletonData.getHeadJoint().find(geometryId);
			// FIXME: remove this whole if
			if (jointData == null){
				jointData = skeletonData.getHeadJoint();
			}
		} else {
			Log.d("GeometryLoader","No skeleton data available");
		}

        // link vertex to weight data
        for (int i = 0; i < this.vertices.size(); i++) {

            Vertex vertex = this.vertices.get(i);

			// transform vertex according to bind_shape_matrix (trooper is using it)
			if (bindShapeMatrix != null) {
				float[] bindShaped = new float[16];
				float[] positionV = new float[]{vertex.getPosition()[0],vertex.getPosition()[1],vertex.getPosition()[2],1};
				Matrix.multiplyMV(bindShaped, 0, bindShapeMatrix, 0, positionV, 0);
				// FIXME: this is used by stormtrooper - i think it is skin data not geometry
				vertex.setPosition(new float[]{bindShaped[0],bindShaped[1],bindShaped[2]});
			}

            // skinning data
            VertexSkinData weightsData = null;
            if (verticesSkinData != null) {
				weightsData = verticesSkinData.get(i);
            }
			// FIXME: do we really need this?
			if (weightsData == null && jointData != null) {
				weightsData = new VertexSkinData();
				weightsData.addJointEffect(jointData.index, 1);
				weightsData.limitJointNumber(3);
			}
            vertex.setWeightsData(weightsData);
        }
    }

	private static void loadData(List<float[]> list, XmlNode node, XmlNode input, int size) {

		// no input, no data
		if (input == null) return;

		// get source data
		String sourceId = input.getAttribute("source").substring(1);
		XmlNode source = node.getChildWithAttribute("source", "id", sourceId);
		XmlNode data = source.getChild("float_array");
		int count = Integer.parseInt(data.getAttribute("count"));

		// no data ?
		Log.d("GeometryLoader","Loading data: "+sourceId+" . count: "+count);
		if (count <= 0){
			return;
		}

		// accessor
		int stride = 4;
		XmlNode technique = source.getChild("technique_common");
		if (technique != null && technique.getChild("accessor") != null){
			stride = Integer.parseInt(technique.getChild("accessor").getAttribute("stride"));
		}

		// parse floats
		String[] floatData = data.getData().trim().replace(',','.').split("\\s+");
		for (int i = 0; i < count; i+=stride) {
			float[] f = new float[size];
			for (int j=0; j<size; j++){
				float val = 1;
				if (j < stride) {
					val = Float.parseFloat(floatData[i+j]);
				}
				f[j]=val;
			}
			list.add(f);
		}
	}

	private boolean assembleVertices(XmlNode primitive){

		// vertices id
		String verticesId = null;

		// offsets
		int vertexOffset = 0;
		int normalOffset = -1;
		int colorOffset = -1;
		int texOffset = -1;

		// get max offset
		int maxOffset = 0;
		for (XmlNode input : primitive.getChildren("input")){
			String semantic = input.getAttribute("semantic");
			int offset = Integer.valueOf(input.getAttribute("offset"));
			if ("VERTEX".equals(semantic)){
				vertexOffset = offset;
				String source = input.getAttribute("source");
				verticesId = source != null ? source.substring(1) : null;
			} else if ("COLOR".equals(semantic)){
				colorOffset = offset;
			} else if ("TEXCOORD".equals(semantic)){
				// only parse set=1
				if (texOffset==-1) {
					texOffset = offset;
					textureLinked = true;
				}
			} else if ("NORMAL".equals(semantic)){
				normalOffset = offset;
			}
			if (offset > maxOffset){
				maxOffset = offset;
			}
		}

		// stride
		int stride = maxOffset + 1;
		Log.d("GeometryLoader", "Loading mesh... type: " + primitive.getName()+". offsets: " + vertexOffset+"," +
				+normalOffset+"," + texOffset);

		// update vertex info
		String[] vcountList = null;
		if (primitive.getChild("vcount") != null){
            vcountList = primitive.getChild("vcount").getData().trim().split("\\s+");
        }

        // there may be multiple polygons like: <p>1 2 3 4 5</p>
		List<XmlNode> polygons = primitive.getChildren("p");
		Log.d("GeometryLoader", "Found polygons: "+ polygons.size());
		for (XmlNode polygon : polygons) {
			String[] indexData = polygon.getData().trim().split("\\s+");
			if (vcountList != null) {

				if (false) {
					// triangle strip technique
					int offset = 0;
					int totalFaces = 0;
					for (int k = 0; k < vcountList.length; k++) {
						int vcount = Integer.parseInt(vcountList[k]);

						int vcounter = 0;
						for (int faceIndex = 0; vcounter < vcount; faceIndex++, vcounter++, offset += stride) {

							if (faceIndex > 2) { // if already a triangle then step back -2 to implement
								faceIndex = 0;
								offset -= stride * 2;
								vcounter -= 2;
								totalFaces++;
							}

							// get vertex
							final int positionIndex = Integer.parseInt(indexData[offset + vertexOffset]);
							Vertex currentVertex = vertices.get(positionIndex);

							// parse normal if available
							if (normalOffset >= 0) {
								currentVertex.setNormalIndex(Integer.parseInt(indexData[offset + normalOffset]));
							}

							// parse color if available
							if (colorOffset >= 0) {
								currentVertex.setColorIndex(Integer.parseInt(indexData[offset + colorOffset]));
							}

							// parse texture if available
							if (texOffset >= 0) {
								int textureIndex = Integer.parseInt(indexData[offset + texOffset]);
								if (textureIndex < 0) {
									throw new IllegalArgumentException("texture index < 0");
								}
								currentVertex.setTextureIndex(textureIndex);
							}

							// update vertex info
							indices.add(positionIndex);
						}
						totalFaces++;
					}
					Log.i("GeometryLoader", "Total STRIP faces: " + totalFaces);
				} else {
					// triangle fan technique
					int offset = 0;
					int totalFaces = 0;
					for (int k = 0; k < vcountList.length; k++) {
						int vcount = Integer.parseInt(vcountList[k]);

						int vcounter = 0;
						int firstVectorOffset = offset;
						boolean doFan = false, doClose = false;
						for (int faceIndex = 0; vcounter < vcount; faceIndex++, vcounter++, offset += stride) {

							if (doClose) {
								faceIndex = 3;
								doClose = false;
							} else if (doFan) {
								offset = firstVectorOffset + vcounter * stride;
								doClose = true;
								doFan = false;
							} else if (faceIndex > 2) { // if already a triangle then step back -2 to implement
								offset = firstVectorOffset;
								vcounter -= 2;
								totalFaces++;
								doFan = true;
								doClose = false;
							}

							// get vertex
							final int positionIndex = Integer.parseInt(indexData[offset + vertexOffset]);
							Vertex currentVertex = vertices.get(positionIndex);

							// parse normal if available
							if (normalOffset >= 0) {
								currentVertex.setNormalIndex(Integer.parseInt(indexData[offset + normalOffset]));
							}

							// parse color if available
							if (colorOffset >= 0) {
								currentVertex.setColorIndex(Integer.parseInt(indexData[offset + colorOffset]));
							}

							// parse texture if available
							if (texOffset >= 0) {
								int textureIndex = Integer.parseInt(indexData[offset + texOffset]);
								if (textureIndex < 0) {
									throw new IllegalArgumentException("texture index < 0");
								}
								currentVertex.setTextureIndex(textureIndex);
							}

							// update vertex info
							indices.add(positionIndex);
						}
						totalFaces++;
					}
					Log.i("GeometryLoader", "Total FAN faces: " + totalFaces + ", Total indices: " + indices.size());
				}
			} else {
				for (int i = 0; i < indexData.length; i += stride) {

					// get vertex
					final int positionIndex = Integer.parseInt(indexData[i + vertexOffset]);
					Vertex currentVertex = vertices.get(positionIndex);

					// parse normal if available
					if (normalOffset >= 0) {
						currentVertex.setNormalIndex(Integer.parseInt(indexData[i + normalOffset]));
					}

					// parse color if available
					if (colorOffset >= 0) {
						currentVertex.setColorIndex(Integer.parseInt(indexData[i + colorOffset]));
					}

					// parse texture if available
					if (texOffset >= 0) {
						currentVertex.setTextureIndex(Integer.parseInt(indexData[i + texOffset]));
					}

					// update vertex info
					indices.add(positionIndex);
				}
			}
		}

		return true;
	}

	private Object[] getMaterialColorAndTexture(String material){
		Object[] ret = new Object[2];
		try {
			XmlNode materialNode = materialsData.getChildWithAttribute("material","id",material);
			if (materialNode == null) {
				materialNode = materialsData.getChildWithAttribute("material","name",material);
			}
			if (materialNode == null) {
				return ret;
			}
			XmlNode instanceEffectNode = materialNode.getChild("instance_effect");
			String instanceEffectId = instanceEffectNode.getAttribute("url").substring(1);
			XmlNode effect = effectsData.getChildWithAttribute("effect","id", instanceEffectId);
			XmlNode profile_common = effect.getChild("profile_COMMON");

			// get technique implementation
			XmlNode technique = profile_common.getChild("technique");
			XmlNode techniqueImpl = null;
			if (technique.getChild("lambert") != null){
				techniqueImpl = technique.getChild("lambert");
			} else if (technique.getChild("phong") != null){
				techniqueImpl = technique.getChild("phong");
			} else if (technique.getChild("blinn") != null){
				techniqueImpl = technique.getChild("blinn");
			}

			// get ambient
			/*XmlNode colorAmbientNode = null;
			if (techniqueImpl != null){
				XmlNode ambient = techniqueImpl.getChild("ambient");
				if (ambient != null){
					colorAmbientNode = ambient.getChild("color");
				}
			}*/

			// get diffuse
			XmlNode diffuse = null;
			if (techniqueImpl != null){
				diffuse = techniqueImpl.getChild("diffuse");
			}

			// get color & texture
			XmlNode colorNode = null;
			XmlNode textureNode = null;
			if (diffuse != null){
				colorNode = diffuse.getChild("color");
				textureNode = diffuse.getChild("texture");
			}

			// got color?
			if (colorNode != null) {
				String colorString = colorNode.getData().trim().replace(',','.');
				String[] color = colorString.split("\\s+");
				ret[0] = new float[]{Float.valueOf(color[0]), Float.valueOf(color[1]), Float.valueOf(color[2]), Float
						.valueOf(color[3])};
				Log.d("GeometryLoader","Color '"+colorString+"'");
			}

			// fallback to ambient color
			/*if (colorAmbientNode != null){
				String colorString = colorAmbientNode.getData().trim();
				String[] color = colorString.split("\\s+");
				ret[0] = new float[]{Float.valueOf(color[0]), Float.valueOf(color[1]), Float.valueOf(color[2]), Float
						.valueOf(color[3])};
				Log.d("GeometryLoader","Ambient color '"+colorString+"'");
			}*/

			// get texture image
			String textureFile = null;
			if (textureNode != null) {
				String texture = textureNode.getAttribute("texture");
				XmlNode newParamNode = profile_common.getChildWithAttribute("newparam", "sid", texture);
				if (newParamNode != null) {
					String surface = newParamNode.getChild("sampler2D").getChild("source").getData();
					newParamNode = profile_common.getChildWithAttribute("newparam", "sid", surface);
					String imageRef = newParamNode.getChildWithAttribute("surface", "type", "2D").getChild("init_from").getData();
					textureFile = imagesNode.getChildWithAttribute("image", "id", imageRef).getChild("init_from")
							.getData();
				} else {
					// TODO: is this ok?
					textureFile = imagesNode.getChildWithAttribute("image", "id", texture).getChild("init_from").getData();
				}
			}
			Log.i("GeometryLoader","Material read. Texture '"+textureFile+"'");
			ret[1] = textureFile;
		} catch (Exception ex) {
			Log.e("GeometryLoader","Error reading material '"+material+"'",ex);
		}
		return ret;
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
			float[] position = currentVertex.getPosition();
			if (textureLinked &&  textures != null && !textures.isEmpty()) {
				float[] textureCoord = textures.get(currentVertex.getTextureIndex());
				texturesArray[i * 2] = textureCoord[0];
				texturesArray[i * 2 + 1] = 1 - textureCoord[1];
			}
			verticesArray[i * 3] = position[0];
			verticesArray[i * 3 + 1] = position[1];
			verticesArray[i * 3 + 2] = position[2];

			if (normals != null && !normals.isEmpty()) {
                float[] normalVector = normals.get(currentVertex.getNormalIndex());
                normalsArray[i * 3] = normalVector[0];
                normalsArray[i * 3 + 1] = normalVector[1];
                normalsArray[i * 3 + 2] = normalVector[2];
            }
            if (colors != null && !colors.isEmpty() && currentVertex.getColorIndex()>=0){
				float[] color = colors.get(currentVertex.getColorIndex());
				colorsBuffer.put(color);
                colorsLinked = true;
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

		for (int i=0; normals != null && i<normals.size(); i++){
			normalsBuffer.put(normals.get(i));
		}

		// clear buffer if we don't need it
		if (colorsBuffer != null && !colorsLinked){
		    colorsBuffer.clear();
		    colorsBuffer = null;
        }
		return furthestPoint;
	}
	
	private void initArrays(String geometryId){
		this.verticesArray = new float[vertices.size() * 3];
		if (textureLinked && textures != null && !textures.isEmpty()) {
			this.texturesArray = new float[vertices.size() * 2];
		}
		this.normalsArray = new float[vertices.size() * 3];
		if (skinningDataMap != null && skinningDataMap.containsKey(geometryId) ||
				vertices.size() > 0 && vertices.get(0).getWeightsData() != null) {
			this.jointIdsArray = new int[vertices.size() * vertices.get(0).getWeightsData().jointIds.size()];
			this.weightsArray = new float[vertices.size() * vertices.get(0).getWeightsData().weights.size()];
		}
		if (!normals.isEmpty()){
			this.normalsBuffer = createNativeByteBuffer(normals.size() * 3 * 4).asFloatBuffer();
		}
		if (!colors.isEmpty()) {
			this.colorsBuffer = createNativeByteBuffer(colors.size() * 4 * 4).asFloatBuffer();
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