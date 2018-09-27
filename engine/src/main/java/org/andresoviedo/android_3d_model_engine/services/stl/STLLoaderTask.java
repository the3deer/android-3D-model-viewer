package org.andresoviedo.android_3d_model_engine.services.stl;

import android.app.Activity;
import android.net.Uri;
import android.opengl.GLES20;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.android_3d_model_engine.services.LoaderTask;
import org.andresoviedo.android_3d_model_engine.services.wavefront.WavefrontLoader;

import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.List;

/**
 * + STL loader supported by the org.j3d STL parser
 *
 * @author andresoviedo
 */
public final class STLLoaderTask extends LoaderTask {

    private STLFileReader stlFileReader;

    public STLLoaderTask(Activity parent, Uri uri, Callback callback) {
        super(parent, uri, callback);
    }

    @Override
    protected List<Object3DData> build() throws IOException {
        // Parse STL
        this.stlFileReader = new STLFileReader(new URL(uri.toString()));
        int totalFaces = stlFileReader.getNumOfFacets()[0];
        Log.i("STLLoaderTask", "Num of objects: " + stlFileReader.getNumOfObjects());
        Log.i("STLLoaderTask", "Found '" + totalFaces + "' facets");
        Log.i("STLLoaderTask", "Parsing messages: " + stlFileReader.getParsingMessages());

        // Allocate data
        FloatBuffer normalsBuffer = createNativeByteBuffer(totalFaces * 3 * 3 * 4).asFloatBuffer();
        FloatBuffer vertexBuffer = createNativeByteBuffer(totalFaces * 3 * 3 * 4).asFloatBuffer();

        // Initialize model dimensions (needed by the Object3DData#scaleCenter()
        WavefrontLoader.ModelDimensions modelDimensions = new WavefrontLoader.ModelDimensions();

        // notify succeded!
        Object3DData data3D = new Object3DData(vertexBuffer).setVertexNormalsArrayBuffer(normalsBuffer);
        data3D.setDimensions(modelDimensions);
        data3D.setDrawUsingArrays(true);
        data3D.setDrawMode(GLES20.GL_TRIANGLES);
        if (totalFaces > 0) {
            data3D.setFaces(new WavefrontLoader.Faces(totalFaces));
        }
        data3D.setId(uri.toString());
        return Collections.singletonList(data3D);
    }

    @Override
    protected void build(List<Object3DData> datas) throws Exception {
        int counter = 0;
        try {

            Object3DData data = datas.get(0);

            // Parse all facets...
            double[] normal = new double[3];
            double[][] vertices = new double[3][3];
            int normalCounter = 0, vertexCounter = 0;

            FloatBuffer normalsBuffer = data.getVertexNormalsArrayBuffer();
            FloatBuffer vertexBuffer = data.getVertexArrayBuffer();
            WavefrontLoader.ModelDimensions modelDimensions = data.getDimensions();

            int totalFaces = stlFileReader.getNumOfFacets()[0];
            boolean first = true;
            while (stlFileReader.getNextFacet(normal, vertices) && counter++ < totalFaces) {
                normalsBuffer.put(normalCounter++, (float) normal[0]);
                normalsBuffer.put(normalCounter++, (float) normal[1]);
                normalsBuffer.put(normalCounter++, (float) normal[2]);
                normalsBuffer.put(normalCounter++, (float) normal[0]);
                normalsBuffer.put(normalCounter++, (float) normal[1]);
                normalsBuffer.put(normalCounter++, (float) normal[2]);
                normalsBuffer.put(normalCounter++, (float) normal[0]);
                normalsBuffer.put(normalCounter++, (float) normal[1]);
                normalsBuffer.put(normalCounter++, (float) normal[2]);

                vertexBuffer.put(vertexCounter++, (float) vertices[0][0]);
                vertexBuffer.put(vertexCounter++, (float) vertices[0][1]);
                vertexBuffer.put(vertexCounter++, (float) vertices[0][2]);
                vertexBuffer.put(vertexCounter++, (float) vertices[1][0]);
                vertexBuffer.put(vertexCounter++, (float) vertices[1][1]);
                vertexBuffer.put(vertexCounter++, (float) vertices[1][2]);
                vertexBuffer.put(vertexCounter++, (float) vertices[2][0]);
                vertexBuffer.put(vertexCounter++, (float) vertices[2][1]);
                vertexBuffer.put(vertexCounter++, (float) vertices[2][2]);

                // update model dimensions
                if (first) {
                    modelDimensions.set((float) vertices[0][0], (float) vertices[0][1], (float) vertices[0][2]);
                    first = false;
                }
                modelDimensions.update((float) vertices[0][0], (float) vertices[0][1], (float) vertices[0][2]);
                modelDimensions.update((float) vertices[1][0], (float) vertices[1][1], (float) vertices[1][2]);
                modelDimensions.update((float) vertices[2][0], (float) vertices[2][1], (float) vertices[2][2]);
            }

            Log.i("STLLoaderTask", "Building 3D object...");
            data.centerAndScale(5, new float[]{0, 0, 0});

        } catch (Exception e) {
            Log.e("STLLoaderTask", "Face '" + counter + "'" + e.getMessage(), e);
            throw e;
        } finally {
            try {
                stlFileReader.close();
            } catch (IOException e) {
                throw e;
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