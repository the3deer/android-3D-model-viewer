package org.andresoviedo.android_3d_model_engine.services.wavefront;

import android.app.Activity;
import android.net.Uri;
import android.opengl.GLES20;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.android_3d_model_engine.services.LoaderTask;
import org.andresoviedo.android_3d_model_engine.services.Object3DBuilder;
import org.andresoviedo.util.android.ContentUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * Wavefront loader implementation
 *
 * @author andresoviedo
 */

public class WavefrontLoaderTask extends LoaderTask {

    public WavefrontLoaderTask(final Activity parent, final Uri uri, final Callback callback) {
        super(parent, uri, callback);
    }

    @Override
    protected List<Object3DData> build() throws IOException {
        InputStream params0 = ContentUtils.getInputStream(uri);
        WavefrontLoader wfl = new WavefrontLoader("");

        // allocate memory
        super.publishProgress("Analyzing model...");
        wfl.analyzeModel(params0);
        params0.close();

        // Allocate memory
        super.publishProgress("Allocating memory...");
        wfl.allocateBuffers();
        wfl.reportOnModel();

        // create the 3D object
        Object3DData data3D = new Object3DData(wfl.getVerts(), wfl.getNormals(), wfl.getTexCoords(), wfl.getFaces(),
                wfl.getFaceMats(), wfl.getMaterials());
        data3D.setId(uri.getPath());
        data3D.setUri(uri);
        data3D.setLoader(wfl);
        data3D.setDrawMode(GLES20.GL_TRIANGLES);
        data3D.setDimensions(data3D.getLoader().getDimensions());
        build(data3D);
        return Collections.singletonList(data3D);
    }

    private void build(Object3DData data) throws IOException {
        InputStream stream = ContentUtils.getInputStream(uri);
        try {

            // parse model
            super.publishProgress("Loading data...");
            data.getLoader().loadModel(stream);
            stream.close();

            // scale object
            super.publishProgress("Scaling object...");
            data.centerScale();
            data.setScale(new float[]{5, 5, 5});

            // draw triangles instead of points
            data.setDrawMode(GLES20.GL_TRIANGLES);

            // build 3D object buffers
            super.publishProgress("Building 3D model...");
            Object3DBuilder.generateArrays(data);
            data.setDrawUsingArrays(true);

        } catch (IOException e) {
            Log.e("Object3DBuilder", e.getMessage(), e);
            throw e;
        }
    }
}
