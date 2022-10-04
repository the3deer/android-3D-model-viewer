package org.the3deer.android_3d_model_engine.services.collada.entities;

import android.util.Log;

import org.the3deer.android_3d_model_engine.model.Element;
import org.the3deer.util.io.IOUtils;
import org.the3deer.util.math.Math3DUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This object contains all the mesh data for an animated model that is to be loaded into the VAO.
 * Notice that the buffers returned ignore any index beyond vertices length (colors or textures sometimes are greater than vertex length)
 * Smoothing groups are an alternative to providing explicit vertex normals.
 * If a polygon’s vertices include normal references, those supersede any smoothing group.
 * Generating vertex normals usually involves calculating face normals for each face,
 * then calculating a normal for each vertex as the average of the face normals of all of the faces which use that vertex.
 * When smoothing groups are used, this process is performed separately for each smoothing group.
 * So if a vertex is used by faces in multiple smoothing groups, there will be multiple normals associated with it. Each normal is calculated as the average of the face normals for all of the associated faces in a specific group.
 * The end result is that where the faces on either side of an edge belong to different smoothing groups,
 * the faces will use different normals for the same vertex,
 * meaning that the edge will appear as a sharp corner (i.e. there will be a discontinuity in the lighting)
 * <p>
 * Essentially, the vertex normal for a specific vertex ID and smoothing group ID is the normalised average of the face normals
 * for all faces in that smoothing group which reference that vertex ID.
 * Typically, you average the un-normalised face normals,
 * so that larger faces are given more weight in the calculation.
 *
 * @author andresoviedo
 */
public class MeshData {


    private static final float[] WRONG_NORMAL = {0, -1, 0};

    public static class Builder {

        private String id;
        private String name;

        private List<float[]> vertices;
        private List<float[]> normals;
        private List<float[]> colors;
        private List<float[]> textures;

        private List<Vertex> vertexAttributes;
        private List<Element> elements;

        private String materialFile;

        private Map<String, List<Vertex>> smoothingGroups;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder vertices(List<float[]> vertices) {
            this.vertices = vertices;
            return this;
        }

        public Builder normals(List<float[]> normals) {
            this.normals = normals;
            return this;
        }

        public Builder colors(List<float[]> colors) {
            this.colors = colors;
            return this;
        }

        public Builder textures(List<float[]> textures) {
            this.textures = textures;
            return this;
        }

        public Builder vertexAttributes(List<Vertex> vertices) {
            this.vertexAttributes = vertices;
            return this;
        }

        public Builder addElement(Element element) {
            if (this.elements == null) {
                this.elements = new ArrayList<>();
            }
            this.elements.add(element);
            return this;
        }

        public Builder materialFile(String materialFile) {
            this.materialFile = materialFile;
            return this;
        }

        public Builder smoothingGroups(Map<String, List<Vertex>> smoothingGroups) {
            this.smoothingGroups = smoothingGroups;
            return this;
        }

        public MeshData build() {
            return new MeshData(id, name, vertices, normals, colors, textures, vertexAttributes, elements, materialFile, smoothingGroups);
        }
    }

    private final String id;
    private final String name;

    private List<Vertex> verticesAttributes;

    private final List<float[]> vertices;
    private final List<float[]> textures;
    private List<float[]> normals; // we can build them
    private final List<float[]> colors;
    private final List<Element> elements;

    private FloatBuffer vertexBuffer;
    private FloatBuffer normalsBuffer;
    private FloatBuffer colorsBuffer;
    private FloatBuffer textureBuffer;

    // skinning data
    private float[] bindShapeMatrix;
    private int[] jointsArray;
    private float[] weightsArray;
    private FloatBuffer jointsBuffer;
    private FloatBuffer weightsBuffer;

    // external material file
    private final String materialFile;

    // smoothing
    private final Map<String, List<Vertex>> smoothingGroups;
    private List<float[]> normalsOriginal;
    private List<Vertex> verticesAttributesOriginal;

    public MeshData(String id, String name, List<float[]> vertices, List<float[]> normals, List<float[]> colors, List<float[]> textures, List<Vertex> verticesAttributes,
                    List<Element> elements, String materialFile, Map<String, List<Vertex>> smoothingGroups) {
        this.id = id;
        this.name = name;

        this.vertices = vertices;
        this.normals = normals;
        this.colors = colors;
        this.textures = textures;

        this.verticesAttributes = verticesAttributes;
        this.elements = elements;

        this.materialFile = materialFile;

        this.smoothingGroups = smoothingGroups;

    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<float[]> getNormals() {
        return this.normals;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void smooth() {

        // backup current data
        this.normalsOriginal = new ArrayList<>(this.normals.size());
        for (int i = 0; i < this.normals.size(); i++) {
            this.normalsOriginal.add(this.normals.get(i).clone());
        }
        if (this.verticesAttributes != null) {
            try {
                this.verticesAttributesOriginal = new ArrayList<>(this.verticesAttributes.size());
                for (int i = 0; i < this.verticesAttributes.size(); i++) {
                    this.verticesAttributesOriginal.add(this.verticesAttributes.get(i).clone());
                }
            } catch (CloneNotSupportedException e) {
                // this should never happen
            }
        }

        // check we have normals to smooth
        if (smoothingGroups == null || smoothingGroups.isEmpty()) {
            smoothAuto();
        } else {
            smoothGroups();
        }

        // refresh normals buffer
        refreshNormalsBuffer();
    }

    public void unSmooth() {
        if (this.normalsOriginal != null) {
            this.normals.clear();
            this.normals = this.normalsOriginal;
        }
        if (this.verticesAttributesOriginal != null) {
            this.verticesAttributes.clear();
            this.verticesAttributes = verticesAttributesOriginal;
        }

    }

    private void smoothAuto() {
        if (this.elements != null) {
            smoothAutoForElements();
        } else {
            smoothAutoForArrays();
        }
    }

    private void smoothGroups() {
        // log event
        Log.i("MeshData", "Smoothing groups... Total: " + smoothingGroups.size());

        // process all smoothing groups
        for (Map.Entry<String, List<Vertex>> smoothingGroup : smoothingGroups.entrySet()) {

            Log.v("MeshData", "Smoothing group... Total vertices: " + smoothingGroup.getValue().size());


            // accumulated normal
            float[] smoothNormal = new float[3];

            for (int i = 0; i < smoothingGroup.getValue().size(); i += 3) {
                Vertex va1 = smoothingGroup.getValue().get(i);
                Vertex va2 = smoothingGroup.getValue().get(i + 1);
                Vertex va3 = smoothingGroup.getValue().get(i + 2);
                float[] v1 = this.vertices.get(va1.getVertexIndex());
                float[] v2 = this.vertices.get(va2.getVertexIndex());
                float[] v3 = this.vertices.get(va3.getVertexIndex());
                float[] normal = calculateNormalFailsafe(v1, v2, v3);
                smoothNormal = Math3DUtils.add(smoothNormal, normal);
            }

            // normalize smooth normals
            Math3DUtils.normalize(smoothNormal);


            // add new normal
            final int newSmoothNormalIdx = this.normals.size();
            this.normals.add(newSmoothNormalIdx, smoothNormal);

            // update normal index to smoothed normal
            for (int i = 0; i < smoothingGroup.getValue().size(); i++) {

                // next vertex attribute linked to the smoothing group
                Vertex va = smoothingGroup.getValue().get(i);

                // When vertex normals are present, they supersede smoothing groups.
                if (va.getNormalIndex() != -1) {
                    continue;
                }

                // update with smoothed normal
                va.setNormalIndex(newSmoothNormalIdx);
            }
        }
    }

    /**
     * Calculate normal using high precision if needed. Otherwise return dummy normal
     *
     * @param v1
     * @param v2
     * @param v3
     * @return
     */
    private static float[] calculateNormalFailsafe(float[] v1, float[] v2, float[] v3) {
        float[] normal = Math3DUtils.calculateNormal(v1, v2, v3);
        try {
            Math3DUtils.normalize(normal);
        } catch (Exception e) {
            Log.w("MeshData", "Error calculating normal. " + e.getMessage()
                    + "," + Math3DUtils.toString(v1)
                    + "," + Math3DUtils.toString(v2)
                    + "," + Math3DUtils.toString(v3));
            normal = WRONG_NORMAL;
        }
        return normal;
    }


    /**
     * Fix missing or wrong normals.  Only for triangulated polygons
     */
    public void fixNormals() {

        Log.i("MeshData", "Fixing missing or wrong normals...");

        // check there is normals to fix
        if (this.normals == null || this.normals.isEmpty()) {

            // write new normals
            generateNormals();

        } else {

            // fix missing or wrong
            if (this.elements != null) {
                fixNormalsForElements();
            } else {
                fixNormalsForArrays();
            }
        }
    }


    /**
     * Fix missing or wrong normals.  Only for triangulated polygons
     */
    private void generateNormals() {

        Log.i("MeshData", "Generating normals...");

        // replaced normals
        final List<float[]> newNormals = new ArrayList<>();

        int counter = 0;
        for (Element element : getElements()) {

            for (int i = 0; i < element.getIndices().size(); i += 3) {

                final int idx1 = element.getIndices().get(i);
                final int idx2 = element.getIndices().get(i + 1);
                final int idx3 = element.getIndices().get(i + 2);

                final Vertex vertexAttribute1 = this.verticesAttributes.get(idx1);
                final Vertex vertexAttribute2 = this.verticesAttributes.get(idx2);
                final Vertex vertexAttribute3 = this.verticesAttributes.get(idx3);

                final int idxV1 = vertexAttribute1.getVertexIndex();
                final int idxV2 = vertexAttribute2.getVertexIndex();
                final int idxV3 = vertexAttribute3.getVertexIndex();

                final float[] v1 = this.vertices.get(idxV1);
                final float[] v2 = this.vertices.get(idxV2);
                final float[] v3 = this.vertices.get(idxV3);

                // check valid triangle
                if (Arrays.equals(v1, v2) || Arrays.equals(v2, v3) || Arrays.equals(v1, v3)) {

                    // update normal attribute
                    vertexAttribute1.setNormalIndex(newNormals.size());
                    vertexAttribute2.setNormalIndex(newNormals.size());
                    vertexAttribute3.setNormalIndex(newNormals.size());

                    // repeated vertex - no normal
                    newNormals.add(newNormals.size(), WRONG_NORMAL);

                    counter++;
                    continue;
                }

                // calculate normal
                final float[] calculatedNormal = calculateNormalFailsafe(v1, v2, v3);

                // update normal attribute
                vertexAttribute1.setNormalIndex(newNormals.size());
                vertexAttribute2.setNormalIndex(newNormals.size());
                vertexAttribute3.setNormalIndex(newNormals.size());

                // add normal
                newNormals.add(newNormals.size(), calculatedNormal);

            }
        }

        this.normals = newNormals;

        Log.i("MeshData", "Generated normals. Total: " + this.normals.size() + ", Faces/Lines: " + counter);
    }

    private void fixNormalsForArrays() {

        Log.i("MeshData", "Fixing normals...");

        // otherwise replaced with this normals
        final List<float[]> newNormals = new ArrayList<>();

        int counter = 0;
        for (int i = 0; i < vertices.size(); i += 3) {

            final float[] v1 = this.vertices.get(i);
            final float[] v2 = this.vertices.get(i + 1);
            final float[] v3 = this.vertices.get(i + 2);

            // check valid triangle
            if (Arrays.equals(v1, v2) || Arrays.equals(v2, v3) || Arrays.equals(v1, v3)) {

                // repeated vertex - no normal
                newNormals.add(newNormals.size(), WRONG_NORMAL);
                newNormals.add(newNormals.size(), WRONG_NORMAL);
                newNormals.add(newNormals.size(), WRONG_NORMAL);

                counter++;
                continue;
            }

            final float[] normalV1 = normals.get(i);
            final float[] normalV2 = normals.get(i + 1);
            final float[] normalV3 = normals.get(i + 2);

            // calculate normal
            float[] calculatedNormal = calculateNormalFailsafe(v1, v2, v3);

            // check normal attribute 1
            if (Math3DUtils.length(normalV1) < 0.1f) {

                // add normal
                newNormals.add(calculatedNormal);

                counter++;

            } else {

                // preserve current normal
                newNormals.add(normalV1);
            }

            // check normal attribute 2
            if (Math3DUtils.length(normalV2) < 0.1f) {

                // add normal
                newNormals.add(calculatedNormal);

                counter++;

            } else {

                // preserve current normal
                newNormals.add(normalV2);
            }

            // check normal attribute 3
            if (Math3DUtils.length(normalV3) < 0.1f) {

                // add normal
                newNormals.add(calculatedNormal);

                counter++;

            } else {

                // preserve current normal
                newNormals.add(normalV3);
            }
        }


        this.normals = newNormals;

        Log.i("MeshData", "Fixed normals. Total: " + counter);
    }


    private void fixNormalsForElements() {

        Log.i("MeshData", "Fixing normals for all elements...");

        // otherwise replaced with this normals
        final List<float[]> newNormals = new ArrayList<>();

        int counter = 0;
        for (Element element : getElements()) {

            if (element.getIndices().size() % 3 != 0) continue;

            for (int i = 0; i < element.getIndices().size(); i += 3) {

                final int idx1 = element.getIndices().get(i);
                final int idx2 = element.getIndices().get(i + 1);
                final int idx3 = element.getIndices().get(i + 2);

                final Vertex vertexAttribute1 = this.verticesAttributes.get(idx1);
                final Vertex vertexAttribute2 = this.verticesAttributes.get(idx2);
                final Vertex vertexAttribute3 = this.verticesAttributes.get(idx3);

                final int idxV1 = vertexAttribute1.getVertexIndex();
                final int idxV2 = vertexAttribute2.getVertexIndex();
                final int idxV3 = vertexAttribute3.getVertexIndex();

                final float[] v1 = this.vertices.get(idxV1);
                final float[] v2 = this.vertices.get(idxV2);
                final float[] v3 = this.vertices.get(idxV3);

                // check valid triangle
                if (Arrays.equals(v1, v2) || Arrays.equals(v2, v3) || Arrays.equals(v1, v3)) {

                    // update normal attribute
                    vertexAttribute1.setNormalIndex(newNormals.size());
                    vertexAttribute2.setNormalIndex(newNormals.size());
                    vertexAttribute3.setNormalIndex(newNormals.size());

                    // repeated vertex - no normal
                    newNormals.add(newNormals.size(), WRONG_NORMAL);

                    counter++;
                    continue;
                }

                final int normalIdxV1 = vertexAttribute1.getNormalIndex();
                final int normalIdxV2 = vertexAttribute2.getNormalIndex();
                final int normalIdxV3 = vertexAttribute3.getNormalIndex();

                final float[] normalV1 = normals.get(normalIdxV1);
                final float[] normalV2 = normals.get(normalIdxV2);
                final float[] normalV3 = normals.get(normalIdxV3);

                // calculate normal
                float[] calculatedNormal = calculateNormalFailsafe(v1, v2, v3);

                // check normal attribute 1
                if (normalIdxV1 == -1 || Math3DUtils.length(normalV1) < 0.1f) {

                    // update normal attribute
                    vertexAttribute1.setNormalIndex(newNormals.size());

                    // add normal
                    newNormals.add(calculatedNormal);

                    counter++;

                } else {

                    // update normal attribute
                    vertexAttribute1.setNormalIndex(newNormals.size());

                    // preserve current normal
                    newNormals.add(normalV1);
                }

                // check normal attribute 2
                if (normalIdxV2 == -1 || Math3DUtils.length(normalV2) < 0.1f) {

                    // update normal attribute
                    vertexAttribute2.setNormalIndex(newNormals.size());

                    // add normal
                    newNormals.add(calculatedNormal);

                    counter++;

                } else {

                    // update normal attribute
                    vertexAttribute2.setNormalIndex(newNormals.size());

                    // preserve current normal
                    newNormals.add(normalV2);
                }

                // check normal attribute 3
                if (normalIdxV3 == -1 || Math3DUtils.length(normalV3) < 0.1f) {

                    // update normal attribute
                    vertexAttribute3.setNormalIndex(newNormals.size());

                    // add normal
                    newNormals.add(calculatedNormal);

                    counter++;

                } else {

                    // update normal attribute
                    vertexAttribute3.setNormalIndex(newNormals.size());

                    // preserve current normal
                    newNormals.add(normalV3);
                }
            }
        }

        this.normals = newNormals;

        Log.i("MeshData", "Fixed normals. Total: " + counter);
    }

    private void smoothAutoForArrays() {

        // log event
        Log.i("MeshData", "Auto smoothing normals for arrays...");

        // smoothed normal
        final Map<String, float[]> smoothNormals = new HashMap<>();

        for (int i = 0; i < vertices.size(); i++) {

            final String idxKey = Arrays.toString(vertices.get(i));
            final float[] smoothNormal = smoothNormals.get(idxKey);
            if (smoothNormal == null) {
                smoothNormals.put(idxKey, this.normals.get(i));
                continue;
            }

            // if same normal, do nothing
            final float[] normal = this.normals.get(i);
            if (normal == smoothNormal || Arrays.equals(normal, smoothNormal)) {
                normals.set(i, smoothNormal);
                continue;
            }

            // smooth normal
            final float[] newSmoothNormal = Math3DUtils.mean(smoothNormal, normal);
            Math3DUtils.normalize(newSmoothNormal);

            // update smoothed normal
            smoothNormal[0] = newSmoothNormal[0];
            smoothNormal[1] = newSmoothNormal[1];
            smoothNormal[2] = newSmoothNormal[2];

            // replace with smoothed normal
            this.normals.set(i, smoothNormal);
        }
    }

    private void smoothAutoForElements() {

        // log event
        Log.i("MeshData", "Auto smoothing normals for all elements...");

        // list of normals associated to the vertex (vertexId --> set of normals)
        final Map<Integer, List<float[]>> vertexNormals = new HashMap<>();

        for (Element element : getElements()) {

            for (int i = 0; i < element.getIndices().size(); i++) {

                // next index
                final int idx = element.getIndices().get(i);

                // next vertex attributes
                final int vertexIndex = this.verticesAttributes.get(idx).getVertexIndex();
                final int normalIndex = this.verticesAttributes.get(idx).getNormalIndex();

                // current normal
                final float[] normal = this.normals.get(normalIndex);

                // add normal to normals list per vertex
                List<float[]> normals = vertexNormals.get(vertexIndex);
                if (normals == null) {
                    normals = new ArrayList<>();
                    normals.add(normal);
                    vertexNormals.put(vertexIndex, normals);
                } else {
                    normals.add(normal);
                }
            }
        }

        // list of saved smoothed normals
        final Map<Integer, float[]> vertexSmooths = new HashMap<>();
        for (Map.Entry<Integer,List<float[]>> entry : vertexNormals.entrySet()) {
            // if same normal, do nothing
            final List<float[]> normals = entry.getValue();

            // if only 1 normal per vertex, no need to do anything
            if (normals.size() == 1) {
                vertexSmooths.put(entry.getKey(), normals.get(0));
                continue;
            };

            // otherwise, calculate normal average and save
            final float[] smoothNormal = Math3DUtils.mean(normals);
            //final float[] smoothNormal = new float[]{1,0,0};
            Math3DUtils.normalize(smoothNormal);

            vertexSmooths.put(entry.getKey(), smoothNormal);
        }

        this.normals.clear();
        for (Element element : getElements()) {

            for (int i = 0; i < element.getIndices().size(); i++) {

                // next index
                final int idx = element.getIndices().get(i);

                // next vertex attributes
                final int vertexIndex = this.verticesAttributes.get(idx).getVertexIndex();
                final int normalIndex = this.verticesAttributes.get(idx).getNormalIndex();

                float[] smoothNormal = vertexSmooths.get(vertexIndex);
                //if (smoothNormal == null) smoothNormal = new float[]{0,1,0};
                this.verticesAttributes.get(idx).setNormalIndex(this.normals.size());
                this.normals.add(smoothNormal);
            }
        }

//        // last, we update smooth to all references normals
//        int count = 0;
//        for (Map.Entry<Integer,float[]> entry : vertexSmooths.entrySet()) {
//            final float[] smoothNormal = entry.getValue();
//            final List<float[]> normals = vertexNormals.get(entry.getKey());
//            for (int j = 0; j < normals.size(); j++) {
//                normals.get(j)[0] = smoothNormal[0];
//                normals.get(j)[1] = smoothNormal[1];
//                normals.get(j)[2] = smoothNormal[2];
//                count++;
//
////                float[] check = search(this.normals, normals.get(j));
////                if (check == null || !Arrays.equals(check, normals.get(j))){
////                    Log.e("MeshData","search returned ko");
////                }
//            }
//
//        }

        //Log.i("MeshData", "Smoothing fixed a total of " + count + " normals");
    }

    private static float[] search(List<float[]> list, float[] search){
        for (int i=0; i<list.size();i++){
            if (list.get(i) == search){
                return list.get(i);
            }
        }
        return null;
    }

    public void validate() {

        if (normals == null) return;

        for (int i = 0; i < normals.size(); i++) {
            float[] normal = normals.get(i);
            if (Float.isNaN(normal[0])) throw new IllegalArgumentException("NaN");
            if (Float.isNaN(normal[1])) throw new IllegalArgumentException("NaN");
            if (Float.isNaN(normal[2])) throw new IllegalArgumentException("NaN");

            if (Math3DUtils.length(normal) < 0.9f) {
                Log.e("MeshData","Wrong normal. Length < 0.9");
            }
        }

        for (Element element : elements) {
            for (int i = 0; i < element.getIndices().size(); i++) {

                // next vertex attribute
                final int idx = element.getIndices().get(i);
                Vertex vertexAttribute = verticesAttributes.get(idx);

                // check normals
                if (vertexAttribute.getNormalIndex() < 0 || vertexAttribute.getNormalIndex() >= normals.size()) {
                    //throw new IllegalArgumentException("Wrong normal index: " + vertexAttribute.getNormalIndex());
                }
            }
        }
    }


    public FloatBuffer getVertexBuffer() {
        if (this.vertexBuffer == null) {
            if (this.verticesAttributes != null) {
                this.vertexBuffer = IOUtils.createFloatBuffer(verticesAttributes.size() * 3);
                for (int i = 0; i < verticesAttributes.size(); i++)
                    this.vertexBuffer.put(vertices.get(verticesAttributes.get(i).getVertexIndex()));
            } else {
                this.vertexBuffer = IOUtils.createFloatBuffer(vertices.size() * 3);
                for (int i = 0; i < vertices.size(); i++)
                    this.vertexBuffer.put(vertices.get(i));
            }
        }
        return vertexBuffer;
    }

    public FloatBuffer getNormalsBuffer() {
        if (this.normalsBuffer == null && !this.normals.isEmpty()) {
            if (this.verticesAttributes != null) {
                this.normalsBuffer = IOUtils.createFloatBuffer(this.verticesAttributes.size() * 3);
                for (int i = 0; i < verticesAttributes.size(); i++) {
                    float[] normal = WRONG_NORMAL; // no normal in case of error
                    final int index = verticesAttributes.get(i).getNormalIndex();
                    if (index >= 0 && index < normals.size()) {
                        normal = normals.get(index);
                    } else {
                        Log.e("MeshData", "Wrong normal index: " + index);
                    }
                    this.normalsBuffer.put(normal);
                }
            } else {
                this.normalsBuffer = IOUtils.createFloatBuffer(normals.size() * 3);
                for (int i = 0; i < normals.size(); i++)
                    this.normalsBuffer.put(normals.get(i));
            }
        }
        return normalsBuffer;
    }

    public void refreshNormalsBuffer() {
        if (this.normalsBuffer == null || this.normals.isEmpty()) {
            Log.e("MeshData", "Can't refresh normals buffer. Either normals or normalsBuffer is empty");
            return;
        } else if (this.verticesAttributes != null && this.verticesAttributes.size() * 3 != this.normalsBuffer.capacity()) {
            Log.e("MeshData", "Can't refresh normals buffer. Buffer size doesn't match actual data");
        } else if (this.verticesAttributes == null && this.normals.size() * 3 != this.normalsBuffer.capacity()) {
            Log.e("MeshData", "Can't refresh normals buffer. Buffer size doesn't match actual data");
        }

        Log.i("MeshData", "Refreshing normals buffer...");
        if (this.verticesAttributes != null) {
            for (int i = 0; i < verticesAttributes.size(); i++) {
                float[] normal = WRONG_NORMAL; // no normal in case of error
                final int index = verticesAttributes.get(i).getNormalIndex();
                if (index >= 0 && index < normals.size()) {
                    normal = this.normals.get(index);
                } else {
                    Log.e("MeshData", "Wrong normal index: " + index);
                }
                this.normalsBuffer.put(i * 3, normal[0]);
                this.normalsBuffer.put(i * 3 + 1, normal[1]);
                this.normalsBuffer.put(i * 3 + 2, normal[2]);
            }
        } else {
            for (int i = 0; i < this.normals.size(); i++) {
                this.normalsBuffer.put(i * 3, this.normals.get(i)[0]);
                this.normalsBuffer.put(i * 3 + 1, this.normals.get(i)[1]);
                this.normalsBuffer.put(i * 3 + 2, this.normals.get(i)[2]);
            }
        }
    }

    public FloatBuffer getColorsBuffer() {
        if (this.colorsBuffer == null && !this.colors.isEmpty()) {
            this.colorsBuffer = IOUtils.createFloatBuffer(this.verticesAttributes.size() * 4);
            for (int i = 0; i < verticesAttributes.size() && i < colors.size(); i++) {
                float[] color = new float[]{1, 0, 0, 1}; // red to warn about error
                final int index = verticesAttributes.get(i).getColorIndex();
                if (index >= 0 && index < colors.size()) {
                    color = colors.get(index);
                }
                this.colorsBuffer.put(color);
            }
        }
        return colorsBuffer;
    }

    public FloatBuffer getTextureBuffer() {
        if (this.textureBuffer == null && !this.textures.isEmpty()) {
            this.textureBuffer = IOUtils.createFloatBuffer(this.verticesAttributes.size() * 2);
            for (int i = 0; i < verticesAttributes.size(); i++) {
                float[] texture = new float[2]; // no texture in case of error
                int index = verticesAttributes.get(i).getTextureIndex();
                if (index >= 0 && index < textures.size()) {
                    texture = textures.get(index);
                    texture = new float[]{texture[0], 1 - texture[1]};
                }
                this.textureBuffer.put(texture);
            }
        }
        return textureBuffer;
    }

    public String getMaterialFile() {
        return materialFile;
    }

    public FloatBuffer getJointsBuffer() {
        if (this.jointsBuffer == null && getJointsArray() != null) {
            this.jointsBuffer = IOUtils.createFloatBuffer(getJointsArray().length);
            for (int i = 0; i < getJointsArray().length; i++) {
                this.jointsBuffer.put(getJointsArray()[i]);
            }
        }
        return jointsBuffer;
    }

    public FloatBuffer getWeightsBuffer() {
        if (this.weightsBuffer == null && getWeightsArray() != null) {
            this.weightsBuffer = IOUtils.createFloatBuffer(getWeightsArray().length);
            this.weightsBuffer.put(getWeightsArray());
        }
        return weightsBuffer;
    }

    public List<Vertex> getVerticesAttributes() {
        return verticesAttributes;
    }

    public void setBindShapeMatrix(float[] bindShapeMatrix) {
        this.bindShapeMatrix = bindShapeMatrix;
    }

    public float[] getBindShapeMatrix() {
        return bindShapeMatrix;
    }

    public void setJointsArray(int[] jointIdsArray) {
        this.jointsArray = jointIdsArray;
    }

    public void setWeightsArray(float[] weightsArray) {
        this.weightsArray = weightsArray;
    }

    public int[] getJointsArray() {
        return jointsArray;
    }

    public float[] getWeightsArray() {
        return weightsArray;
    }

    public MeshData clone() {
        final MeshData ret = new MeshData(getId(), getName(), this.vertices, this.normals, this.colors, this.textures,
                getVerticesAttributes(), getElements(), materialFile, smoothingGroups);
        ret.setBindShapeMatrix(getBindShapeMatrix());
        ret.setJointsArray(getJointsArray());
        ret.setWeightsArray(getWeightsArray());
        return ret;
    }

}
