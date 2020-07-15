package org.andresoviedo.android_3d_model_engine.services.collada.loader;

import android.util.Log;

import org.andresoviedo.android_3d_model_engine.model.Element;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.MeshData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.Vertex;
import org.andresoviedo.android_3d_model_engine.util.HoleCutter;
import org.andresoviedo.util.xml.XmlNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Loads the mesh data for a model from a collada XML file.
 *
 * @author andresoviedo
 */
public class GeometryLoader {

    private static final Pattern SPACE_PATTERN = Pattern.compile("\\s+");

    private final XmlNode geometryNode;

    private List<Vertex> verticesAttributes;
    private List<float[]> vertex = new ArrayList<>();
    private List<float[]> textures = new ArrayList<>();
    private List<float[]> normals = new ArrayList<>();
    private List<float[]> colors = new ArrayList<>();

    // populate this list - only tu debug !!!
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

    private boolean textureLinked = false;

    public GeometryLoader(XmlNode geometryNode) {
        this.geometryNode = geometryNode;
    }

    public MeshData loadGeometry(XmlNode geometry) {

        String geometryId = geometry.getAttribute("id");
        String geometryName = geometry.getAttribute("name");

        // INFO: this if is only to debug large complex model
        if (!includeGeometries.isEmpty() && !includeGeometries.contains(geometryId)
                && !includeGeometries.contains(geometryName)) {
            Log.d("GeometryLoader", "Geometry ignored: " + geometryId);
            return null;
        }

        Log.i("GeometryLoader", "Loading geometry '" + geometryId + " (" + geometryName + ")'...");

        final List<List<Integer>> oldElements = new ArrayList<>();

        // list with all indices together with it's texture
        final List<Element> elements = new ArrayList<>();

        // process mesh...
        XmlNode meshData = geometry.getChild("mesh");

        // read vertices and normals
        textureLinked = false;

        // load  //mesh/vertices
        verticesAttributes = new ArrayList<>();
        vertex = new ArrayList<>();
        textures = new ArrayList<>();
        normals = new ArrayList<>();
        colors = new ArrayList<>();

        // load vertices
        loadVertices(meshData, vertex, normals, textures);

        // check there is any vertex to draw
        if (vertex.isEmpty()) {
            Log.e("GeometryLoader", "Ignoring geometry since it has no vertices: " + geometryId);
            return null;
        }

        // read texture and normals
        loadPrimitiveData(meshData);

        // get all primitives
        List<XmlNode> polys = meshData.getChildren("polylist");
        if (!polys.isEmpty()) {
            Log.d("GeometryLoader", "Loading polylist polygons... " + polys.size());
            loadPolygon(geometryId, geometryName, polys, oldElements, elements);
        }

        // triangle mesh
        List<XmlNode> triangless = meshData.getChildren("triangles");
        if (!triangless.isEmpty()) {
            Log.d("GeometryLoader", "Loading triangulated polygons... " + triangless.size());
            loadPolygon(geometryId, geometryName, triangless, oldElements, elements);
        }

        // triangle mesh
        List<XmlNode> polygons = meshData.getChildren("polygons");
        if (!polygons.isEmpty()) {
            Log.d("GeometryLoader", "Loading polygons... " + polygons.size());
            loadPolygon(geometryId, geometryName, polygons, oldElements, elements);
        }

        // check for empty or null meshes
        if (polygons.isEmpty() && triangless.isEmpty() && polys.isEmpty()) {
            Log.e("GeometryLoader", "Mesh with no face info: " + meshData.getName());
            return null;
        }

        final List<int[]> indicesArray = convertIndicesListToArray(oldElements);

        //if (Log.isLoggable("GeometryLoader", Log.DEBUG)) {
        Log.i("GeometryLoader", "Loaded geometry "+geometryId+". vertices: " + verticesAttributes.size() +
                ", normals: " + (normals != null ? normals.size() : 0) +
                ", textures: " + (textures != null ? textures.size() : 0) +
                ", colors: " + (colors != null ? colors.size() : 0));
        Log.i("GeometryLoader", "Loaded geometry "+geometryId+". elements: " + indicesArray.size());

        return new MeshData(geometryId, geometryName, vertex, normals, colors, textures, verticesAttributes, elements, null, null);
    }

    private void loadPolygon(String geometryId, String geometryName, List<XmlNode> polygons, List<List<Integer>> elementsOld, List<Element> elements) {
        for (XmlNode polygon : polygons) {

            List<Integer> indices = new ArrayList<>();

            String material = polygon.getAttribute("material");

            setupVertices(polygon, indices);
            if (indices.size() % 3 != 0){
                Log.e("GeometryLoader","Wrong geometry not triangulated: "+indices.size());
                continue;
            }

            // old indices without texture
            elementsOld.add(indices);

            elements.add(new Element(geometryId, indices, material));
        }
    }

    private XmlNode loadPrimitiveData(XmlNode meshData) {

        // get actual primitive
        XmlNode primitiveNode = null;
        if (meshData.getChild("polylist") != null) {
            primitiveNode = meshData.getChild("polylist");
        } else if (meshData.getChild("triangles") != null) {
            primitiveNode = meshData.getChild("triangles");
        } else if (meshData.getChild("polygons") != null) {
            primitiveNode = meshData.getChild("polygons");
        }

        // load primitive data
        if (primitiveNode != null) {
            XmlNode inputNormal = primitiveNode.getChildWithAttribute("input", "semantic", "NORMAL");
            loadData(normals, meshData, inputNormal, 3, "NORMAL");
            XmlNode inputCoord = primitiveNode.getChildWithAttribute("input", "semantic", "TEXCOORD");
            loadData(textures, meshData, inputCoord, 2, "TEXCOORD");
            XmlNode inputColor = primitiveNode.getChildWithAttribute("input", "semantic", "COLOR");
            loadData(colors, meshData, inputColor, 4, "COLOR");
        }

        return primitiveNode;
    }

    // <vertices> - may contain "VERTEX" and "NORMAL" semantics
    private void loadVertices(XmlNode meshData, List<float[]> vertex, List<float[]> normals, List<float[]> textures) {

        // get position & normal source ids
        XmlNode verticesNode = meshData.getChild("vertices");
        assert verticesNode != null;
        for (XmlNode node : verticesNode.getChildren("input")) {
            String semanticId = node.getAttribute("semantic");
            if ("POSITION".equals(semanticId)) {
                loadData(vertex, meshData, node, 3, "POSITION");
            } else if ("NORMAL".equals(semanticId)) {
                loadData(normals, meshData, node, 3, "NORMAL");
            } else if ("TEXCOORD".equals(semanticId)) {
                loadData(textures, meshData, node, 2, "TEXCOORD");
                textureLinked = true;
            }
        }
    }



    private static void loadData(List<float[]> list, XmlNode node, XmlNode input, int size, String semantic) {

        // no input, no data
        if (input == null) return;

        // get source data
        String sourceId = input.getAttribute("source").substring(1);
        XmlNode source = node.getChildWithAttribute("source", "id", sourceId);
        XmlNode data = source.getChild("float_array");
        int count = Integer.parseInt(data.getAttribute("count"));

        // no data ?
        Log.d("GeometryLoader", "Loading data... " + sourceId + ", "+semantic+", count: " + count);
        if (count <= 0) {
            return;
        }

        // accessor
        int stride = 4;
        XmlNode technique = source.getChild("technique_common");
        if (technique != null && technique.getChild("accessor") != null) {
            stride = Integer.parseInt(technique.getChild("accessor").getAttribute("stride"));
        }

        // parse floats
        String[] floatData = SPACE_PATTERN.split(data.getData().trim().replace(',', '.'));
        for (int i = 0; i < count; i += stride) {
            float[] f = new float[size];
            for (int j = 0; j < size; j++) {
                float val = 1;
                if (j < stride) {
                    val = Float.parseFloat(floatData[i + j]);
                }
                f[j] = val;
            }
            list.add(f);
        }
    }

    private void setupVertices(XmlNode primitive, List<Integer> indices) {

        // vertices id
        String verticesId = null;

        // offsets
        int vertexOffset = 0;
        int normalOffset = -1;
        int colorOffset = -1;
        int texOffset = -1;

        // get max offset
        int maxOffset = 0;
        for (XmlNode input : primitive.getChildren("input")) {
            String semantic = input.getAttribute("semantic");
            int offset = Integer.valueOf(input.getAttribute("offset"));
            if ("VERTEX".equals(semantic)) {
                vertexOffset = offset;
                String source = input.getAttribute("source");
                verticesId = source != null ? source.substring(1) : null;
            } else if ("COLOR".equals(semantic)) {
                colorOffset = offset;
            } else if ("TEXCOORD".equals(semantic)) {
                // only parse set=1
                if (texOffset == -1) {
                    texOffset = offset;
                    textureLinked = true;
                }
            } else if ("NORMAL".equals(semantic)) {
                normalOffset = offset;
            }
            if (offset > maxOffset) {
                maxOffset = offset;
            }
        }

        // stride
        int stride = maxOffset + 1;
        Log.d("GeometryLoader", "Loading data for '" + primitive.getName() + "'. offsets: vertex=" + vertexOffset + ", normal=" +
                +normalOffset + ", texture=" + texOffset + ", color=" + colorOffset);

        // update vertex info
        String[] vcountList = null;
        if (primitive.getChild("vcount") != null) {
            vcountList = SPACE_PATTERN.split(primitive.getChild("vcount").getData().trim());
        }

        /*// there may be multiple polygons like: <p>1 2 3 4 5</p>
        List<XmlNode> polygons = primitive.getChildren("p");
        if (polygons.isEmpty()){
            List<XmlNode> polygonsWithHoles = primitive.getChildren("ph");
            if (!polygonsWithHoles.isEmpty()){
                Log.d("GeometryLoader", "Found polygons with holes: "+polygonsWithHoles.size());
                for (XmlNode polygon : polygonsWithHoles) {
                    polygon = polygonsWithHoles.get(0).getChildren("p").get(0);
                    String[] indexData = SPACE_PATTERN.split(polygon.getData().trim());
                    triangulateFannedPolygon(indices, vertexOffset, normalOffset, colorOffset, texOffset, stride, indexData);

                }
            }
            return;
        }*/
        // there may be multiple polygons like: <p>1 2 3 4 5</p>
        List<XmlNode> polygons = primitive.getChildren("p");
        if (polygons.isEmpty()){
            List<XmlNode> polygonsWithHoles = primitive.getChildren("ph");
            if (!polygonsWithHoles.isEmpty()){
                Log.d("GeometryLoader", "Found polygons with holes: "+polygonsWithHoles.size());

                // save index to index later the polygon with holes
                final int offset = verticesAttributes.size();

                for (XmlNode polygonWithHole : polygonsWithHoles) {
                    final XmlNode polygon = polygonsWithHoles.get(0).getChild("p");


                    // polygon with holes indices
                    final List<Vertex> polygonWithHolesIndices = new ArrayList<>();

                    // parse vertices
                    final String[] indexData = SPACE_PATTERN.split(polygon.getData().trim());
                    for (int i=0; i<indexData.length; i+=stride){
                        final int positionIndex = Integer.parseInt(indexData[i + vertexOffset]);

                        final Vertex vertexAttribute = new Vertex(positionIndex);

                        // parse normal if available
                        if (normalOffset >= 0) {
                            vertexAttribute.setNormalIndex(Integer.parseInt(indexData[i + normalOffset]));
                        }

                        // parse color if available
                        if (colorOffset >= 0) {
                            vertexAttribute.setColorIndex(Integer.parseInt(indexData[i + colorOffset]));
                        }

                        // parse texture if available
                        if (texOffset >= 0) {
                            vertexAttribute.setTextureIndex(Integer.parseInt(indexData[i + texOffset]));
                        }

                        // add vertex attribute
                        this.verticesAttributes.add(vertexAttribute);

                        // save to cut later
                        polygonWithHolesIndices.add(vertexAttribute);
                    }

                    // list of holes
                    final List<List<Vertex>> allHoles = new ArrayList<>();

                    // parse holes list
                    for (XmlNode hole : polygonWithHole.getChildren("h")){

                        // single hole
                        final List<Vertex> holeVertices = new ArrayList<>();

                        // parse vertices
                        String[] holeData = SPACE_PATTERN.split(hole.getData().trim());
                        for (int i=0; i<holeData.length; i+=stride){
                            final int positionIndex = Integer.parseInt(holeData[i + vertexOffset]);

                            final Vertex vertexAttribute = new Vertex(positionIndex);

                            // parse normal if available
                            if (normalOffset >= 0) {
                                vertexAttribute.setNormalIndex(Integer.parseInt(holeData[i + normalOffset]));
                            }

                            // parse color if available
                            if (colorOffset >= 0) {
                                vertexAttribute.setColorIndex(Integer.parseInt(holeData[i + colorOffset]));
                            }

                            // parse texture if available
                            if (texOffset >= 0) {
                                vertexAttribute.setTextureIndex(Integer.parseInt(holeData[i + texOffset]));
                            }

                            // add vertex attribute
                            this.verticesAttributes.add(vertexAttribute);

                            // add to cut later
                            holeVertices.add(vertexAttribute);
                        }

                        allHoles.add(holeVertices);
                    }

                    try {

                        final List<float[]> triangles = new ArrayList<>();
                        for (Vertex va : polygonWithHolesIndices){
                            triangles.add(this.vertex.get(va.getVertexIndex()));
                        }

                        final List<List<float[]>> holes = new ArrayList<>();
                        for (List<Vertex> holeList : allHoles){
                            final List<float[]> hole = new ArrayList<>();
                            for (Vertex va : holeList){
                                hole.add(this.vertex.get(va.getVertexIndex()));
                            }
                            holes.add(hole);
                        }

                        List<Integer> pierced = HoleCutter.pierce(triangles, holes);
                        for (int i=0; i<pierced.size(); i++){
                            indices.add(offset + pierced.get(i));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return;
        }
        Log.d("GeometryLoader", "Loading " + primitive.getName() + "... " + polygons.size());
        for (XmlNode polygon : polygons) {
            String[] indexData = SPACE_PATTERN.split(polygon.getData().trim());
            if (vcountList != null) {
                if (false) {
                    triangulateStrippedPolygon(indices, vertexOffset, normalOffset, colorOffset, texOffset, stride, vcountList, indexData);
                } else {
                    triangulateFannedPolygon(indices, vertexOffset, normalOffset, colorOffset, texOffset, stride, vcountList, indexData);
                }
            } else {
                Log.d("GeometryLoader", "Loading faces.... " + indexData.length / 3);
                for (int i = 0; i < indexData.length; i += stride) {

                    // get vertex
                    final int positionIndex = Integer.parseInt(indexData[i + vertexOffset]);
                    Vertex vertexAttribute = new Vertex(positionIndex);

                    // parse normal if available
                    if (normalOffset >= 0) {
                        vertexAttribute.setNormalIndex(Integer.parseInt(indexData[i + normalOffset]));
                    }

                    // parse color if available
                    if (colorOffset >= 0) {
                        vertexAttribute.setColorIndex(Integer.parseInt(indexData[i + colorOffset]));
                    }

                    // parse texture if available
                    if (texOffset >= 0) {
                        vertexAttribute.setTextureIndex(Integer.parseInt(indexData[i + texOffset]));
                    }

                    // update vertex info
                    indices.add(this.verticesAttributes.size());

                    // add vertex attribute
                    this.verticesAttributes.add(vertexAttribute);
                }
            }
        }
    }

    private void triangulateStrippedPolygon(List<Integer> indices, int vertexOffset, int normalOffset, int colorOffset, int texOffset, int stride, String[] vcountList, String[] indexData) {
        Log.d("GeometryLoader", "Loading using triangle strip technique. vcount: " + vcountList.length);

        // triangle strip technique
        int offset = 0;
        int totalFaces = 0;
        for (String s : vcountList) {
            int vcount = Integer.parseInt(s);

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
                Vertex vertexAttribute = new Vertex(positionIndex);

                // parse normal if available
                if (normalOffset >= 0) {
                    vertexAttribute.setNormalIndex(Integer.parseInt(indexData[offset + normalOffset]));
                }

                // parse color if available
                if (colorOffset >= 0) {
                    vertexAttribute.setColorIndex(Integer.parseInt(indexData[offset + colorOffset]));
                }

                // parse texture if available
                if (texOffset >= 0) {
                    int textureIndex = Integer.parseInt(indexData[offset + texOffset]);
                    if (textureIndex < 0) {
                        throw new IllegalArgumentException("texture index < 0");
                    }
                    vertexAttribute.setTextureIndex(textureIndex);
                }

                // update vertex info
                indices.add(this.verticesAttributes.size());

                // add vertex attribute
                this.verticesAttributes.add(vertexAttribute);
            }
            totalFaces++;
        }
        Log.i("GeometryLoader", "Total STRIP faces: " + totalFaces);
    }

    private void triangulateFannedPolygon(List<Integer> indices, int vertexOffset, int normalOffset, int colorOffset, int texOffset, int stride, String[] vcountList, String[] indexData) {

        Log.d("GeometryLoader", "Loading using fan technique. vcount: " + vcountList.length);

        // triangle fan technique
        int offset = 0;
        int totalFaces = 0;
        for (String s : vcountList) {
            int vcount = Integer.parseInt(s);

            int vcounter = 0;
            int firstVectorOffset = offset;
            boolean doFan = false, doClose = false;
            for (int faceIndex = 0; vcounter < vcount; faceIndex++, vcounter++, offset += stride) {

                if (doClose) {
                    faceIndex = 2;
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
                Vertex vertexAttribute = new Vertex(positionIndex);

                // parse normal if available
                if (normalOffset >= 0) {
                    vertexAttribute.setNormalIndex(Integer.parseInt(indexData[offset + normalOffset]));
                }

                // parse color if available
                if (colorOffset >= 0) {
                    vertexAttribute.setColorIndex(Integer.parseInt(indexData[offset + colorOffset]));
                }

                // parse texture if available
                if (texOffset >= 0) {
                    int textureIndex = Integer.parseInt(indexData[offset + texOffset]);
                    if (textureIndex < 0) {
                        throw new IllegalArgumentException("texture index < 0");
                    }
                    vertexAttribute.setTextureIndex(textureIndex);
                }

                // update vertex info
                indices.add(this.verticesAttributes.size());

                // add vertex attribute
                this.verticesAttributes.add(vertexAttribute);

            }
            totalFaces++;
        }
        Log.i("GeometryLoader", "Total FAN faces: " + totalFaces + ", Total indices: " + indices.size());
    }

    private void triangulateFannedPolygon(List<Integer> indices, int vertexOffset, int normalOffset, int colorOffset, int texOffset, int stride, String[] indexData) {

        Log.d("GeometryLoader", "Loading using fan technique. Indices: "+indexData.length+", MeshObject: " + (indexData.length/stride -2));

        int totalFaces = 0;

        boolean doFan = false, doClose = false;

        for (int offset = 0, faceIndex=0; offset < indexData.length; offset += stride, faceIndex++) {

            /*// state machine
            if (doClose) {
                doClose = false;
                faceIndex = 2;
            } else if (doFan) {
                doClose = true;
                doFan = false;
                offset = totalFaces * stride + vertexOffset;
            } else if (faceIndex > 2) { // if already a triangle then step back -2 to implement
                doFan = true;
                doClose = false;
                offset = 0;
                faceIndex = 0;
                totalFaces++;
            } else if (faceIndex == 2){
                totalFaces++;
            }*/

            // state machine
            if (doClose) {
                doClose = false;
            } else if (doFan) {
                doClose = true;
                doFan = false;
                offset = totalFaces * stride + stride + vertexOffset;
            } else if (faceIndex == 3) {
                doFan = true;
                doClose = false;
                offset = 0;
                faceIndex = 0;
            }

            if (faceIndex == 2){
                totalFaces++;
            }

            // get vertex
            final int positionIndex = Integer.parseInt(indexData[offset + vertexOffset]);
            Vertex vertexAttribute = new Vertex(positionIndex);

            // parse normal if available
            if (normalOffset >= 0) {
                vertexAttribute.setNormalIndex(Integer.parseInt(indexData[offset + normalOffset]));
            }

            // parse color if available
            if (colorOffset >= 0) {
                vertexAttribute.setColorIndex(Integer.parseInt(indexData[offset + colorOffset]));
            }

            // parse texture if available
            if (texOffset >= 0) {
                int textureIndex = Integer.parseInt(indexData[offset + texOffset]);
                if (textureIndex < 0) {
                    throw new IllegalArgumentException("texture index < 0");
                }
                vertexAttribute.setTextureIndex(textureIndex);
            }

            // update vertex info
            indices.add(this.verticesAttributes.size());

            // add vertex attriute
            this.verticesAttributes.add(vertexAttribute);

        }
        Log.i("GeometryLoader", "Total FAN faces: " + totalFaces + ", Total indices: " + indices.size());
    }

    private List<int[]> convertIndicesListToArray(List<List<Integer>> allIndices) {
        List<int[]> ret = new ArrayList<>();
        for (int e = 0; e < allIndices.size(); e++) {
            int[] indicesArray = new int[allIndices.get(e).size()];
            for (int i = 0; i < indicesArray.length; i++) {
                indicesArray[i] = allIndices.get(e).get(i);
            }
            ret.add(indicesArray);
        }
        return ret;
    }

}