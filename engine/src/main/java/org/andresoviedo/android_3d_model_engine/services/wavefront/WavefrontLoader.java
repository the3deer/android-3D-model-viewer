// WavefrontLoader.java
// Andrew Davison, February 2007, ad@fivedots.coe.psu.ac.th

/* Load the OBJ model from MODEL_DIR, centering and scaling it.
 The scale comes from the sz argument in the constructor, and
 is implemented by changing the vertices of the loaded model.

 The model can have vertices, normals and tex coordinates, and
 refer to materials in a MTL file.

 The OpenGL commands for rendering the model are stored in 
 a display list (modelDispList), which is drawn by calls to
 draw().

 Information about the model is printed to stdout.

 Based on techniques used in the OBJ loading code in the
 JautOGL multiplayer racing game by Evangelos Pournaras 
 (http://today.java.net/pub/a/today/2006/10/10/
 development-of-3d-multiplayer-racing-game.html 
 and https://jautogl.dev.java.net/), and the 
 Asteroids tutorial by Kevin Glass 
 (http://www.cokeandcode.com/asteroidstutorial)

 CHANGES (Feb 2007)
 - a global flipTexCoords boolean
 - drawToList() sets and uses flipTexCoords
 */

package org.andresoviedo.android_3d_model_engine.services.wavefront;

import android.net.Uri;
import android.opengl.GLES20;
import androidx.annotation.Nullable;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.model.Element;
import org.andresoviedo.android_3d_model_engine.model.Material;
import org.andresoviedo.android_3d_model_engine.model.Materials;
import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.android_3d_model_engine.services.LoadListener;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.MeshData;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.Vertex;
import org.andresoviedo.util.android.ContentUtils;
import org.andresoviedo.util.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WavefrontLoader {

    private final int triangulationMode;
    private final LoadListener callback;

    public WavefrontLoader(int triangulationMode, LoadListener callback) {
        this.triangulationMode = triangulationMode;
        this.callback = callback;
    }

    @Nullable
    public static String getMaterialLib(Uri uri) {
        return getParameter(uri, "mtllib ");
    }

    @Nullable
    public static String getTextureFile(Uri uri) {
        return getParameter(uri, "map_Kd ");
    }

    @Nullable
    private static String getParameter(Uri uri, String parameter) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(ContentUtils.getInputStream(uri)))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith(parameter)) {
                    return line.substring(parameter.length()).trim();
                }
            }
        } catch (IOException e) {
            Log.e("WavefrontLoader", "Problem reading file '" + uri + "': " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<Object3DData> load(URI modelURI) {
        try {

            // log event
            Log.i("WavefrontLoader", "Loading model... " + modelURI.toString());

            // log event
            Log.i("WavefrontLoader", "--------------------------------------------------");
            Log.i("WavefrontLoader", "Parsing geometries... ");
            Log.i("WavefrontLoader", "--------------------------------------------------");

            // open stream, parse model, then close stream
            final InputStream is = modelURI.toURL().openStream();
            final List<MeshData> meshes = loadModel(modelURI.toString(), is);
            is.close();

            // 3D meshes
            final List<Object3DData> ret = new ArrayList<>();

            // log event
            Log.i("WavefrontLoader", "Processing geometries... ");

            // notify listener
            callback.onProgress("Processing geometries...");

            // proces all meshes
            for (MeshData meshData : meshes) {

                // notify listener
                callback.onProgress("Processing normals...");

                // fix missing or wrong normals
                meshData.fixNormals();

                // smooth normals
                meshData.smooth();

                // check we didn't brake normals
                meshData.validate();

                // create 3D object
                Object3DData data3D = new Object3DData(meshData.getVertexBuffer());
                data3D.setId(meshData.getId());
                data3D.setName(meshData.getName());
                data3D.setNormalsBuffer(meshData.getNormalsBuffer());
                data3D.setTextureBuffer(meshData.getTextureBuffer());
                data3D.setElements(meshData.getElements());
                data3D.setId(modelURI.toString());
                data3D.setUri(modelURI);
                data3D.setDrawUsingArrays(false);
                data3D.setDrawMode(GLES20.GL_TRIANGLES);

                // add model to scene
                callback.onLoad(data3D);

                // notify listener
                callback.onProgress("Loading materials...");

                // load colors and textures
                loadMaterials(meshData);

                ret.add(data3D);
            }

            // log event
            Log.i("WavefrontLoader", "Loaded geometries: " + ret.size());

            return ret;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void loadMaterials(MeshData meshData) {

        // process materials
        if (meshData.getMaterialFile() == null) return;

        // log event
        Log.i("WavefrontLoader", "--------------------------------------------------");
        Log.i("WavefrontLoader", "Parsing materials... ");
        Log.i("WavefrontLoader", "--------------------------------------------------");

        try {

            // get materials stream
            final InputStream inputStream = ContentUtils.getInputStream(meshData.getMaterialFile());

            // parse materials
            final WavefrontMaterialsParser materialsParser = new WavefrontMaterialsParser();
            final Materials materials = materialsParser.parse(meshData.getMaterialFile(), inputStream);

            // check if there is any material
            if (materials.size() > 0) {

                // bind materials
                for (int e = 0; e < meshData.getElements().size(); e++) {

                    // get element
                    final Element element = meshData.getElements().get(e);

                    // log event
                    Log.i("WavefrontLoader", "Processing element... " + element.getId());

                    // get material id
                    final String elementMaterialId = element.getMaterialId();

                    // check if element has material
                    if (elementMaterialId != null && materials.contains(elementMaterialId)) {

                        // get material for element
                        final Material elementMaterial = materials.get(elementMaterialId);

                        // bind material
                        element.setMaterial(elementMaterial);

                        // check if element has texture mapped
                        if (elementMaterial.getTextureFile() != null) {

                            // log event
                            Log.i("WavefrontLoader", "Reading texture file... " + elementMaterial.getTextureFile());

                            // read texture data
                            try (InputStream stream = ContentUtils.getInputStream(elementMaterial.getTextureFile())) {

                                // read data
                                elementMaterial.setTextureData(IOUtils.read(stream));

                                // log event
                                Log.i("WavefrontLoader", "Texture linked... " + element.getMaterial().getTextureFile());

                            } catch (Exception ex) {
                                Log.e("WavefrontLoader", String.format("Error reading texture file: %s", ex.getMessage()));
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Log.e("WavefrontLoader", "Error loading materials... " + meshData.getMaterialFile());
        }
    }

    private List<MeshData> loadModel(String id, InputStream is) {

        // log event
        Log.i("WavefrontLoader", "Loading model... " + id);

        // String fnm = MODEL_DIR + modelNm + ".obj";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(is));

            // debug model purposes
            int lineNum = 0;
            String line = null;

            // primitive data
            final List<float[]> vertexList = new ArrayList<>();
            final List<float[]> normalsList = new ArrayList<>();
            final List<float[]> textureList = new ArrayList<>();

            // mesh data
            final List<MeshData> meshes = new ArrayList<>();
            final List<Vertex> verticesAttributes = new ArrayList<>();

            // material file
            String mtllib = null;

            // smoothing groups
            final Map<String,List<Vertex>> smoothingGroups = new HashMap<>();
            List<Vertex> currentSmoothingList = null;

            // mesh current
            MeshData.Builder meshCurrent = new MeshData.Builder().id(id);
            Element.Builder elementCurrent = new Element.Builder().id("default");
            List<Integer> indicesCurrent = new ArrayList<>();
            boolean buildNewMesh = false;
            boolean buildNewElement = false;

            try {
                while (((line = br.readLine()) != null)) {
                    lineNum++;
                    line = line.trim();
                    if (line.length() == 0) continue;
                    if (line.startsWith("v ")) { // vertex
                        parseVector(vertexList, line.substring(2).trim());
                    } else if (line.startsWith("vn")) { // normal
                        parseVector(normalsList, line.substring(3).trim());
                    } else if (line.startsWith("vt")) { // tex coord
                        parseVariableVector(textureList, line.substring(3).trim());
                    } else if (line.charAt(0) == 'o') { // object group
                        if (buildNewMesh) {
                            // build mesh
                            meshCurrent.vertices(vertexList).normals(normalsList).textures(textureList)
                                    .vertexAttributes(verticesAttributes)
                                    .addElement(elementCurrent.indices(indicesCurrent).build());

                            // add current mesh
                            final MeshData build = meshCurrent.build();
                            meshes.add(build);

                            // log event
                            Log.d("WavefrontLoader", "Loaded mesh. id:" + build.getId() + ", indices: " + indicesCurrent.size()
                                    + ", vertices:" + vertexList.size()
                                    + ", normals: " + normalsList.size()
                                    + ", textures:" + textureList.size()
                                    + ", elements: " + build.getElements());

                            // next mesh
                            meshCurrent = new MeshData.Builder().id(line.substring(1).trim());

                            // next element
                            elementCurrent = new Element.Builder();
                            indicesCurrent = new ArrayList<>();
                        } else {
                            meshCurrent.id(line.substring(1).trim());
                            buildNewMesh = true;
                        }
                    } else if (line.charAt(0) == 'g') { // group name
                        if (buildNewElement && indicesCurrent.size() > 0) {

                            // add current element
                            elementCurrent.indices(indicesCurrent);
                            meshCurrent.addElement(elementCurrent.build());

                            // log event
                            Log.d("WavefrontLoader", "New element. indices: " + indicesCurrent.size());

                            // prepare next element
                            indicesCurrent = new ArrayList<>();
                            elementCurrent = new Element.Builder().id(line.substring(1).trim());
                        } else {
                            elementCurrent.id(line.substring(1).trim());
                            buildNewElement = true;
                        }
                    } else if (line.startsWith("f ")) { // face
                        parseFace(verticesAttributes, indicesCurrent, vertexList, normalsList, textureList, line.substring(2), currentSmoothingList);
                    } else if (line.startsWith("mtllib ")) {// build material
                        mtllib = line.substring(7);
                    } else if (line.startsWith("usemtl ")) {// use material
                        if (elementCurrent.getMaterialId() != null) {

                            // change element since we are dealing with different material
                            elementCurrent.indices(indicesCurrent);
                            meshCurrent.addElement(elementCurrent.build());

                            // log event
                            Log.v("WavefrontLoader", "New material: " + line);

                            // prepare next element
                            indicesCurrent = new ArrayList<>();
                            elementCurrent = new Element.Builder().id(elementCurrent.getId());
                        }

                        elementCurrent.materialId(line.substring(7));
                    } else if (line.charAt(0) == 's') { // smoothing group
                        final String smoothingGroupId = line.substring(1).trim();
                        if ("0".equals(smoothingGroupId) || "off".equals(smoothingGroupId)){
                            currentSmoothingList = null;
                        } else {
                            currentSmoothingList = new ArrayList<>();
                            smoothingGroups.put(smoothingGroupId, currentSmoothingList);
                        }
                    } else if (line.charAt(0) == '#') { // comment line
                        Log.v("WavefrontLoader", line);
                    } else
                        Log.w("WavefrontLoader", "Ignoring line " + lineNum + " : " + line);

                }

                // build mesh
                final Element element = elementCurrent.indices(indicesCurrent).build();
                final MeshData meshData = meshCurrent.vertices(vertexList).normals(normalsList).textures(textureList)
                        .vertexAttributes(verticesAttributes).materialFile(mtllib)
                        .addElement(element).smoothingGroups(smoothingGroups).build();

                Log.i("WavefrontLoader", "Loaded mesh. id:" + meshData.getId() + ", indices: " + indicesCurrent.size()
                        + ", vertices:" + vertexList.size()
                        + ", normals: " + normalsList.size()
                        + ", textures:" + textureList.size()
                        + ", elements: " + meshData.getElements());

                // add mesh
                meshes.add(meshData);

                // return all meshes
                return meshes;

            } catch (Exception e) {
                Log.e("WavefrontLoader", "Error reading line: " + lineNum + ":" + line, e);
                Log.e("WavefrontLoader", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    Log.e("WavefrontLoader", e.getMessage(), e);
                }
            }
        }
    }

    /**
     * List of texture coordinates, in (u, [,v ,w]) coordinates, these will vary between 0 and 1. v, w are optional and default to 0.
     * There may only be 1 tex coords  on the line, which is determined by looking at the first tex coord line.
     */
    private void parseVector(List<float[]> vectorList, String line) {
        try {
            final String[] tokens = line.split(" +");
            final float[] vector = new float[3];
            vector[0] = Float.parseFloat(tokens[0]);
            vector[1] = Float.parseFloat(tokens[1]);
            vector[2] = Float.parseFloat(tokens[2]);
            vectorList.add(vector);
        } catch (Exception ex) {
            Log.e("WavefrontLoader", "Error parsing vector '"+line+"': "+ex.getMessage());
            vectorList.add(new float[3]);
        }

    }

    /**
     * List of texture coordinates, in (u, [,v ,w]) coordinates, these will vary between 0 and 1. v, w are optional and default to 0.
     * There may only be 1 tex coords  on the line, which is determined by looking at the first tex coord line.
     */
    private void parseVariableVector(List<float[]> textureList, String line) {
        try {
            final String[] tokens = line.split(" +");
            final float[] vector = new float[2];
            vector[0] = Float.parseFloat(tokens[0]);
            if (tokens.length > 1) {
                vector[1] = Float.parseFloat(tokens[1]);
                // ignore 3d coordinate
				/*if (tokens.length > 2) {
					vector[2] = Float.parseFloat(tokens[2]);
				}*/
            }
            textureList.add(vector);
        } catch (Exception ex) {
            Log.e("WavefrontLoader", ex.getMessage());
            textureList.add(new float[2]);
        }

    }

    /**
     * get this face's indicies from line "f v/vt/vn ..." with vt or vn index values perhaps being absent.
     */
    private void parseFace(List<Vertex> vertexAttributes, List<Integer> indices,
                           List<float[]> vertexList, List<float[]> normalsList, List<float[]> texturesList,
                           String line, List<Vertex> currentSmoothingList) {
        try {

            // cpu optimization
            final String[] tokens;
            if (line.contains("  ")) {
                tokens = line.split(" +");
            } else {
                tokens = line.split(" ");
            }

            // number of v/vt/vn tokens
            final int numTokens = tokens.length;

            for (int i = 0, faceIndex = 0; i < numTokens; i++, faceIndex++) {

                // convert to triangles all polygons
                if (faceIndex > 2) {
                    // Converting polygon to triangle
                    faceIndex = 0;

                    i -= 2;
                }

                // triangulate polygon
                final String faceToken;
                if (this.triangulationMode == GLES20.GL_TRIANGLE_FAN) {
                    // In FAN mode all meshObject shares the initial vertex
                    if (faceIndex == 0) {
                        faceToken = tokens[0];// get a v/vt/vn
                    } else {
                        faceToken = tokens[i]; // get a v/vt/vn
                    }
                } else {
                    // GL.GL_TRIANGLES | GL.GL_TRIANGLE_STRIP
                    faceToken = tokens[i]; // get a v/vt/vn
                }

                // parse index tokens
                // how many '/'s are there in the token
                final String[] faceTokens = faceToken.split("/");
                final int numSeps = faceTokens.length;

                int vertIdx = Integer.parseInt(faceTokens[0]);
                // A valid vertex index matches the corresponding vertex elements of a previously defined vertex list.
                // If an index is positive then it refers to the offset in that vertex list, starting at 1.
                // If an index is negative then it relatively refers to the end of the vertex list,
                // -1 referring to the last element.
                if (vertIdx < 0) {
                    vertIdx = vertexList.size() + vertIdx;
                } else {
                    vertIdx--;
                }

                int textureIdx = -1;
                if (numSeps > 1 && faceTokens[1].length() > 0) {
                    textureIdx = Integer.parseInt(faceTokens[1]);
                    if (textureIdx < 0) {
                        textureIdx = texturesList.size() + textureIdx;
                    } else {
                        textureIdx--;
                    }
                }
                int normalIdx = -1;
                if (numSeps > 2 && faceTokens[2].length() > 0) {
                    normalIdx = Integer.parseInt(faceTokens[2]);
                    if (normalIdx < 0) {
                        normalIdx = normalsList.size() + normalIdx;
                    } else {
                        normalIdx--;
                    }
                }

                // create VertexAttribute
                final Vertex vertexAttribute = new Vertex(vertIdx);
                vertexAttribute.setNormalIndex(normalIdx);
                vertexAttribute.setTextureIndex(textureIdx);

                // add VertexAtribute
                final int idx = vertexAttributes.size();
                vertexAttributes.add(idx, vertexAttribute);

                // store the indices for this face
                indices.add(idx);

                // smoothing
                if (currentSmoothingList != null){
                    currentSmoothingList.add(vertexAttribute);
                }
            }
        } catch (NumberFormatException e) {
            Log.e("WavefrontLoader", e.getMessage(), e);
        }
    }

}
