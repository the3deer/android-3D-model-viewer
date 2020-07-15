package org.andresoviedo.android_3d_model_engine.objects;

import android.opengl.GLES20;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.model.AnimatedModel;
import org.andresoviedo.android_3d_model_engine.model.Element;
import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.util.io.IOUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class Wireframe {

    /**
     * Builds a wireframe of the model by drawing all lines (3) of the triangles.
     *
     * @param objData the 3d model
     * @return the 3d wireframe using indices
     */
    public static Object3DData build(Object3DData objData) {

        // TODO: create several wireframes elements instead of only 1 ?

        // log event
        Log.i("Wireframe", "Building wireframe... " + objData);

        try {
            final FloatBuffer vertexBuffer;
            final IntBuffer wireframeIndices;

            if (!objData.isDrawUsingArrays()) {

                // get total indices
                int totalIndex = 0;
                for (Element element : objData.getElements()) {
                    totalIndex += element.getIndexBuffer().capacity();
                }
                Log.i("Wireframe", "Building wireframe... Total indices: " + totalIndex);

                // we need 2 points x face side
                wireframeIndices = IOUtils.createIntBuffer(totalIndex * 2);

                // process all elements
                vertexBuffer = objData.getVertexBuffer();

                for (Element element : objData.getElements()) {
                    final IntBuffer drawBuffer = element.getIndexBuffer();
                    for (int i = 0; i < drawBuffer.capacity(); i += 3) {
                        int v0 = drawBuffer.get(i);
                        int v1 = drawBuffer.get(i + 1);
                        int v2 = drawBuffer.get(i + 2);
                        wireframeIndices.put(v0);
                        wireframeIndices.put(v1);
                        wireframeIndices.put(v1);
                        wireframeIndices.put(v2);
                        wireframeIndices.put(v2);
                        wireframeIndices.put(v0);
                    }
                }
            } else {
                Log.i("Wireframe", "Building wireframe... Total vertices: " + objData.getVertexBuffer().capacity()/3);
                vertexBuffer = objData.getVertexBuffer();
                wireframeIndices = IOUtils.createIntBuffer(vertexBuffer.capacity()/3 * 2);
                Log.i("Wireframe", "Building wireframe... First vertex " + vertexBuffer.get(0) + "," + vertexBuffer.get(1) + "," + vertexBuffer.get(2));
                for (int i = 0; i < vertexBuffer.capacity() / 3; i += 3) {
                    wireframeIndices.put(i);
                    wireframeIndices.put(i + 1);
                    wireframeIndices.put(i + 1);
                    wireframeIndices.put(i + 2);
                    wireframeIndices.put(i + 2);
                    wireframeIndices.put(i);
                }
            }
            final Object3DData ret;
            if (objData instanceof AnimatedModel) {
                final AnimatedModel object3DData = new AnimatedModel(vertexBuffer, wireframeIndices);
                AnimatedModel objDataAnim = (AnimatedModel) objData;
                object3DData.setVertexWeights(objDataAnim.getVertexWeights());
                object3DData.setJointIds(objDataAnim.getJointIds());
                object3DData.setJointsData(objDataAnim.getJointsData());
                object3DData.doAnimation(objDataAnim.getAnimation());
                object3DData.setBindShapeMatrix(objDataAnim.getBindShapeMatrix());
                object3DData.setRootJoint(objDataAnim.getRootJoint());
                ret = object3DData;
            } else {
                ret = new Object3DData(vertexBuffer, wireframeIndices);
            }
            Log.i("Wireframe", "Wireframe built. Total indices: " + ret.getDrawOrder().capacity());
            ret
                    .setNormalsBuffer(objData.getNormalsBuffer())
                    .setColorsBuffer(objData.getColorsBuffer())
                    .setColor(objData.getColor())
                    .setTextureBuffer(objData.getTextureBuffer())
                    .setLocation(objData.getLocation())
                    .setRotation(objData.getRotation())
                    .setScale(objData.getScale())
                    .setBindTransform(objData.getBindTransform())
                    .setDrawMode(GLES20.GL_LINES)
                    .setDrawUsingArrays(false)
                    .setId(objData.getId() + "_wireframe");
            return ret;
        } catch (Exception ex) {
            Log.e("Wireframe", ex.getMessage(), ex);
            throw new RuntimeException("Problem building wireframe", ex);
        }
    }
}
