package org.andresoviedo.android_3d_model_engine.services.stl;

import android.app.Activity;
import android.opengl.GLES20;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.android_3d_model_engine.services.LoadListener;
import org.andresoviedo.android_3d_model_engine.services.LoaderTask;
import org.andresoviedo.android_3d_model_engine.services.collada.entities.MeshData;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * + STL loader supported by the org.j3d STL parser
 *
 * @author andresoviedo
 */
public final class STLLoaderTask extends LoaderTask {

    private STLFileReader stlFileReader;

    public STLLoaderTask(Activity parent, URI uri, LoadListener callback) {
        super(parent, uri, callback);
    }

    @Override
    protected List<Object3DData> build() throws IOException {

        // current facet counter
        int counter = 0;

        try {

            // log event
            Log.i("STLLoaderTask", "Parsing model...");
            super.publishProgress("Parsing model...");

            // Parse STL
            this.stlFileReader = new STLFileReader(new URL(uri.toString()));

            // get total facets
            int totalFaces = stlFileReader.getNumOfFacets()[0];

            // log event
            Log.i("STLLoaderTask", "Num of objects found: " + stlFileReader.getNumOfObjects());
            Log.i("STLLoaderTask", "Num facets found '" + totalFaces + "' facets");
            Log.i("STLLoaderTask", "Parsing messages: " + stlFileReader.getParsingMessages());

            // primitive data
            final List<float[]> vertices = new ArrayList<>();
            final List<float[]> normals = new ArrayList<>();

            // Parse all facets...
            double[] normal = new double[3];
            double[][] triangle = new double[3][3];

            // notify user
            super.publishProgress("Loading facets...");

            // load data
            while (stlFileReader.getNextFacet(normal, triangle) && counter++ < totalFaces) {

                normals.add(new float[]{(float)normal[0], (float)normal[1], (float)normal[2]});
                normals.add(new float[]{(float)normal[0], (float)normal[1], (float)normal[2]});
                normals.add(new float[]{(float)normal[0], (float)normal[1], (float)normal[2]});

                vertices.add(new float[]{(float)triangle[0][0],(float)triangle[0][1],(float)triangle[0][2]});
                vertices.add(new float[]{(float)triangle[1][0],(float)triangle[1][1],(float)triangle[1][2]});
                vertices.add(new float[]{(float)triangle[2][0],(float)triangle[2][1],(float)triangle[2][2]});
            }

            // log event
            Log.i("STLLoaderTask", "Loaded model. Facets: " + counter + ", vertices:" +vertices.size()+", normals: "+normals.size());

            // build data
            final MeshData mesh = new MeshData.Builder().vertices(vertices).normals(normals).build();

            // fix missing or wrong normals
            super.publishProgress("Validating data...");
            mesh.fixNormals();

            // smooth
            super.publishProgress("Smoothing faces...");
            mesh.smooth();

            // notify succeded!
            Object3DData data = new Object3DData(mesh.getVertexBuffer()).setNormalsBuffer(mesh.getNormalsBuffer());
            data.setDrawUsingArrays(true);
            data.setDrawMode(GLES20.GL_TRIANGLES);
            data.setId(uri.toString());

            // super.publishProgress("Loading facets... "+counter+"/"+totalFaces);
            super.onLoad(data);

            return Collections.singletonList(data);
        } catch (IOException e) {
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