package org.andresoviedo.android_3d_model_engine.objects;

import android.opengl.GLES20;
import androidx.annotation.NonNull;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.model.AnimatedModel;
import org.andresoviedo.android_3d_model_engine.model.Element;
import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.util.io.IOUtils;
import org.andresoviedo.util.math.Math3DUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public final class Normals {

    private static final int COORDS_PER_VERTEX = 3;

    /**
     * Generate a new object that contains all the line normals for all the faces for the specified object
     *
     * @param obj the object to which we calculate the normals.
     * @return the model with all the normal lines
     */
    public static Object3DData build(Object3DData obj) {
        if (obj.getDrawMode() != GLES20.GL_TRIANGLES) {
            return null;
        }

        if (obj.getVertexBuffer() == null) {
            Log.i("Normals", "Generating face normals for '" + obj.getId() + "' I found that there is no vertex data");
            return null;
        }

        Log.i("Normals", "Generating normals object... " + obj.getId());

        if (obj instanceof AnimatedModel) {
            if (obj.isDrawUsingArrays()) {
                // TODO: do we have AnimatedModel using arrays ?
                return null;
            } else {
                return buildNormalLinesForElements((AnimatedModel) obj);
            }
        } else {
            if (obj.isDrawUsingArrays()) {

                if (obj.getNormalsBuffer() != null) {
                    return buildNormalLines(obj);
                } else {
                    return calculateNormalsLines(obj);
                }
            } else {

                if (obj.getNormalsBuffer() != null) {
                    return buildNormalLinesByIndices(obj);
                } else {

                    return calculateNormalsLinesByIndices(obj);
                }
            }
        }
    }

    @NonNull
    private static Object3DData buildNormalLinesByIndices(Object3DData obj) {
        Log.i("Normals", "Building normals for '" + obj.getId() + "' using indices... "+obj);

        final List<float[]> normalsVertexArray = new ArrayList<>();
        final List<float[]> normalsNormalsArray = new ArrayList<>();
        final List<Element> normalsElements = new ArrayList<>();

        for (Element element : obj.getElements()) {

            // 1 line (2 vertex) per face (3 vertex)

            final List<Integer> normalsIndices = new ArrayList<>();

            final IntBuffer indices = element.getIndexBuffer();
            for (int i = 0; i < indices.capacity(); i += 3) {
                int offsetV1 = indices.get(i) * COORDS_PER_VERTEX;
                int offsetV2 = indices.get(i + 1) * COORDS_PER_VERTEX;
                int offsetV3 = indices.get(i + 2) * COORDS_PER_VERTEX;

                float[] normal1 = new float[]{obj.getNormalsBuffer().get(offsetV1), obj.getNormalsBuffer().get(offsetV1 + 1), obj.getNormalsBuffer().get(offsetV1 + 2)};
                float[] v1 = {obj.getVertexBuffer().get(offsetV1), obj.getVertexBuffer().get(offsetV1 + 1), obj.getVertexBuffer().get(offsetV1 + 2)};
                float[] e1 = Math3DUtils.add(v1, normal1);

                float[] normal2 = new float[]{obj.getNormalsBuffer().get(offsetV2), obj.getNormalsBuffer().get(offsetV2 + 1), obj.getNormalsBuffer().get(offsetV2 + 2)};
                float[] v2 = {obj.getVertexBuffer().get(offsetV2), obj.getVertexBuffer().get(offsetV2 + 1), obj.getVertexBuffer().get(offsetV2 + 2)};
                float[] e2 = Math3DUtils.add(v2, normal2);

                float[] normal3 = new float[]{obj.getNormalsBuffer().get(offsetV3), obj.getNormalsBuffer().get(offsetV3 + 1), obj.getNormalsBuffer().get(offsetV3 + 2)};
                float[] v3 = {obj.getVertexBuffer().get(offsetV3), obj.getVertexBuffer().get(offsetV3 + 1), obj.getVertexBuffer().get(offsetV3 + 2)};
                float[] e3 = Math3DUtils.add(v3, normal3);

                int idx = normalsVertexArray.size();
                normalsVertexArray.add(v1);
                normalsVertexArray.add(e1);
                normalsVertexArray.add(v2);
                normalsVertexArray.add(e2);
                normalsVertexArray.add(v3);
                normalsVertexArray.add(e3);
                normalsIndices.add(idx++);
                normalsIndices.add(idx++);
                normalsIndices.add(idx++);
                normalsIndices.add(idx++);
                normalsIndices.add(idx++);
                normalsIndices.add(idx++);
                normalsNormalsArray.add(normal1);
                normalsNormalsArray.add(normal1);
                normalsNormalsArray.add(normal2);
                normalsNormalsArray.add(normal2);
                normalsNormalsArray.add(normal3);
                normalsNormalsArray.add(normal3);
            }

            normalsElements.add(new Element(element.getId(), normalsIndices, element.getMaterialId()));
        }


        final Object3DData normalsObj = new Object3DData();
        normalsObj.setVertexBuffer(IOUtils.createFloatBuffer(normalsVertexArray, 3));
        normalsObj.setNormalsBuffer(IOUtils.createFloatBuffer(normalsNormalsArray, 3));
        normalsObj.setDrawMode(GLES20.GL_LINES).setColor(new float[]{1f, 1f, 1f, 1f});
        normalsObj.setScale(obj.getScale());
        normalsObj.setRotation(obj.getRotation());
        normalsObj.setLocation(obj.getLocation());
        normalsObj.setBindTransform(obj.getBindTransform());
        normalsObj.setElements(normalsElements);
        normalsObj.setDrawUsingArrays(false);

        Log.i("Normals", "Built normals object: '" + normalsObj);

        return normalsObj;
    }

    private static AnimatedModel buildNormalLinesForElements(AnimatedModel obj) {

        // log event
        Log.i("Normals", "Building animated normals for '" + obj.getId() + "' using indices...");


        // copy original vertex buffer to reuse positions
        final List<float[]> newVertexArray = new ArrayList<>();
        for (int i = 0; i < obj.getVertexBuffer().capacity(); i += 3) {
            newVertexArray.add(new float[]{obj.getVertexBuffer().get(i), obj.getVertexBuffer().get(i + 1), obj.getVertexBuffer().get(i + 2)});
        }

        final List<float[]> newNormalsArray = new ArrayList<>();
        for (int i = 0; i < obj.getNormalsBuffer().capacity(); i += 3) {
            newNormalsArray.add(new float[]{obj.getNormalsBuffer().get(i), obj.getNormalsBuffer().get(i + 1), obj.getNormalsBuffer().get(i + 2)});
        }


        Log.i("Normals", "Adding additional vertices and normals...");

        final List<Element> newElements = new ArrayList<>();

        for (Element element : obj.getElements()) {

            Log.i("Normals", "Adding additional vertices and normals... element: "+element.getId());

            // current triangle indices
            final IntBuffer indexBuffer = element.getIndexBuffer();

            // new lines indices
            final List<Integer> normalsIndices = new ArrayList<>();

            for (int i = 0; i < indexBuffer.capacity(); i += 3) {

                final int idxV1 = indexBuffer.get(i);
                final int idxV2 = indexBuffer.get(i + 1);
                final int idxV3 = indexBuffer.get(i + 2);

                final int offsetV1 = idxV1 * 3;
                final int offsetV2 = idxV2 * 3;
                final int offsetV3 = idxV3 * 3;

                float[] normal1 = new float[]{obj.getNormalsBuffer().get(offsetV1), obj.getNormalsBuffer().get(offsetV1 + 1), obj.getNormalsBuffer().get(offsetV1 + 2)};
                float[] v1 = {obj.getVertexBuffer().get(offsetV1), obj.getVertexBuffer().get(offsetV1 + 1), obj.getVertexBuffer().get(offsetV1 + 2)};
                float[] e1 = Math3DUtils.add(v1, normal1);

                float[] normal2 = new float[]{obj.getNormalsBuffer().get(offsetV2), obj.getNormalsBuffer().get(offsetV2 + 1), obj.getNormalsBuffer().get(offsetV2 + 2)};
                float[] v2 = {obj.getVertexBuffer().get(offsetV2), obj.getVertexBuffer().get(offsetV2 + 1), obj.getVertexBuffer().get(offsetV2 + 2)};
                float[] e2 = Math3DUtils.add(v2, normal2);

                float[] normal3 = new float[]{obj.getNormalsBuffer().get(offsetV3), obj.getNormalsBuffer().get(offsetV3 + 1), obj.getNormalsBuffer().get(offsetV3 + 2)};
                float[] v3 = {obj.getVertexBuffer().get(offsetV3), obj.getVertexBuffer().get(offsetV3 + 1), obj.getVertexBuffer().get(offsetV3 + 2)};
                float[] e3 = Math3DUtils.add(v3, normal3);

                normalsIndices.add(idxV1);
                normalsIndices.add(newVertexArray.size());
                newVertexArray.add(e1);

                normalsIndices.add(idxV2);
                normalsIndices.add(newVertexArray.size());
                newVertexArray.add(e2);

                normalsIndices.add(idxV3);
                normalsIndices.add(newVertexArray.size());
                newVertexArray.add(e3);

                newNormalsArray.add(normal1);
                newNormalsArray.add(normal2);
                newNormalsArray.add(normal3);



            }

            Log.i("Normals", "Added new element element: "+element.getId());
            newElements.add(new Element(element.getId(), normalsIndices, element.getMaterialId()));
        }

        final AnimatedModel normalsObj = new AnimatedModel();
        normalsObj.setVertexBuffer(IOUtils.createFloatBuffer(newVertexArray, 3));
        normalsObj.setNormalsBuffer(IOUtils.createFloatBuffer(newNormalsArray, 3));
        normalsObj.setDrawMode(GLES20.GL_LINES);
        normalsObj.setScale(obj.getScale());
        normalsObj.setRotation(obj.getRotation());
        normalsObj.setLocation(obj.getLocation());
        normalsObj.setBindTransform(obj.getBindTransform());
        normalsObj.setElements(newElements);
        normalsObj.setDrawUsingArrays(false);
        normalsObj.doAnimation(obj.getAnimation());
        normalsObj.setJointsData(obj.getJointsData());
        normalsObj.setRootJoint(obj.getRootJoint());
        normalsObj.setBindShapeMatrix(obj.getBindShapeMatrix());

        // skinned normals only for skinned models
        if (obj.getJointIds() != null && obj.getVertexWeights() != null) {

            // new buffers are twice the size (3 vertex per face --> 3 lines per face = 6 vertex per face)
            final FloatBuffer newVertexWeights = IOUtils.createFloatBuffer(obj.getVertexWeights().capacity() * 2);
            final FloatBuffer newJointIds = IOUtils.createFloatBuffer(obj.getJointIds().capacity() * 2);

            for (int i = 0; i < obj.getJointIds().capacity(); i += 3) {
                newJointIds.put(new float[]{obj.getJointIds().get(i), obj.getJointIds().get(i + 1), obj.getJointIds().get(i + 2)});
            }
            for (int i = 0; i < obj.getJointIds().capacity(); i += 3) {
                newJointIds.put(new float[]{obj.getJointIds().get(i), obj.getJointIds().get(i + 1), obj.getJointIds().get(i + 2)});
            }

            for (int i = 0; i < obj.getVertexWeights().capacity(); i += 3) {
                newVertexWeights.put(new float[]{obj.getVertexWeights().get(i), obj.getVertexWeights().get(i + 1), obj.getVertexWeights().get(i + 2)});
            }
            for (int i = 0; i < obj.getVertexWeights().capacity(); i += 3) {
                newVertexWeights.put(new float[]{obj.getVertexWeights().get(i), obj.getVertexWeights().get(i + 1), obj.getVertexWeights().get(i + 2)});
            }

            normalsObj.setJointIds(newJointIds);
            normalsObj.setVertexWeights(newVertexWeights);
        }

        Log.i("Normals", "New animated normal lines object created");

        return normalsObj;
    }

    @NonNull
    private static Object3DData calculateNormalsLines(Object3DData obj) {
        Log.d("Normals", "Calculating normals for '" + obj.getId() + "' using array...");

        FloatBuffer normalsLines = IOUtils.createFloatBuffer(obj.getVertexBuffer().capacity() / 3 * 2);

        for (int i = 0; i < obj.getVertexBuffer().capacity(); i += 9) {
            float[][] normalLine = Math3DUtils.calculateNormalLine(
                    new float[]{obj.getVertexBuffer().get(i), obj.getVertexBuffer().get(i + 1), obj.getVertexBuffer().get(i + 2)},
                    new float[]{obj.getVertexBuffer().get(i + 3), obj.getVertexBuffer().get(i + 4), obj.getVertexBuffer().get(i + 5)},
                    new float[]{obj.getVertexBuffer().get(i + 6), obj.getVertexBuffer().get(i + 7), obj.getVertexBuffer().get(i + 8)}, false);

            normalsLines.put(normalLine[0][0]).put(normalLine[0][1]).put(normalLine[0][2]);
            normalsLines.put(normalLine[1][0]).put(normalLine[1][1]).put(normalLine[1][2]);
        }

        Object3DData normalsObj = new Object3DData(normalsLines);
        normalsObj.setDrawMode(GLES20.GL_LINES).setColor(new float[]{1f, 0f, 0f, 1f});
        normalsObj.setScale(obj.getScale());
        normalsObj.setRotation(obj.getRotation());
        normalsObj.setLocation(obj.getLocation());
        normalsObj.setBindTransform(obj.getBindTransform());

        Log.i("Normals", "New face normal lines object created");
        return normalsObj;
    }

    @NonNull
    private static Object3DData calculateNormalsLinesByIndices(Object3DData obj) {
        Log.i("Normals", "Calculating normals for '" + obj.getId() + "' using indices...");

        FloatBuffer normalsLines = IOUtils.createFloatBuffer(obj.getDrawOrder().capacity() * 3);

        for (int i = 0; i < obj.getDrawOrder().capacity(); i += 3) {
            int v1 = obj.getDrawOrder().get(i) * COORDS_PER_VERTEX;
            int v2 = obj.getDrawOrder().get(i + 1) * COORDS_PER_VERTEX;
            int v3 = obj.getDrawOrder().get(i + 2) * COORDS_PER_VERTEX;

            float[][] normalLine = Math3DUtils.calculateNormalLine(
                    new float[]{obj.getVertexBuffer().get(v1), obj.getVertexBuffer().get(v1 + 1), obj.getVertexBuffer().get(v1 + 2)},
                    new float[]{obj.getVertexBuffer().get(v2), obj.getVertexBuffer().get(v2 + 1), obj.getVertexBuffer().get(v2 + 2)},
                    new float[]{obj.getVertexBuffer().get(v3), obj.getVertexBuffer().get(v3 + 1), obj.getVertexBuffer().get(v3 + 2)},
                    false);

            normalsLines.put(normalLine[0]).put(normalLine[1]);
        }

        Object3DData normalsObj = new Object3DData(normalsLines);
        normalsObj.setDrawMode(GLES20.GL_LINES).setColor(new float[]{1f, 0f, 0f, 1f});
        normalsObj.setScale(obj.getScale());
        normalsObj.setRotation(obj.getRotation());
        normalsObj.setLocation(obj.getLocation());
        normalsObj.setBindTransform(obj.getBindTransform());

        Log.i("Normals", "New face normal lines object created");

        return normalsObj;
    }

    @NonNull
    private static Object3DData buildNormalLines(Object3DData obj) {
        Log.v("Normals", "Building normals for '" + obj.getId() + "'...");

        FloatBuffer normalsLines = IOUtils.createFloatBuffer(obj.getVertexBuffer().capacity() * 2);

        for (int i = 0; i < obj.getNormalsBuffer().capacity(); i += 3) {

            // vertex is first vertex of line
            normalsLines.put(obj.getVertexBuffer().get(i));
            normalsLines.put(obj.getVertexBuffer().get(i + 1));
            normalsLines.put(obj.getVertexBuffer().get(i + 2));

            // get actual normals
            float nx = obj.getNormalsBuffer().get(i);
            float ny = obj.getNormalsBuffer().get(i + 1);
            float nz = obj.getNormalsBuffer().get(i + 2);
            if (nx == 0 && ny == 0 && nz == 0) {
                Log.e("Normals", "Wrong normal all zeros: " + i);
            }

            // calculate final normal position
            float n1 = (obj.getVertexBuffer().get(i) + nx / obj.getScaleX());
            normalsLines.put(n1);
            float n2 = (obj.getVertexBuffer().get(i + 1) + ny / obj.getScaleY());
            normalsLines.put(n2);
            float n3 = (obj.getVertexBuffer().get(i + 2) + nz / obj.getScaleZ());
            normalsLines.put(n3);
        }

        Object3DData normalsObj = new Object3DData(normalsLines);
        normalsObj.setDrawMode(GLES20.GL_LINES).setColor(new float[]{1f, 1f, 1f, 1f});
        normalsObj.setScale(obj.getScale());
        normalsObj.setRotation(obj.getRotation());
        normalsObj.setLocation(obj.getLocation());
        normalsObj.setBindTransform(obj.getBindTransform());

        Log.v("Normals", "New face normal lines object created. vertices: " + normalsLines.capacity() / 3);
        return normalsObj;
    }
}
