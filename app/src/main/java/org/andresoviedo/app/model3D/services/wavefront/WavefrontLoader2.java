package org.andresoviedo.app.model3D.services.wavefront;

import android.app.Activity;
import android.opengl.GLES20;
import android.util.Log;

import org.andresoviedo.app.model3D.controller.LoaderTask;
import org.andresoviedo.app.model3D.model.Object3DBuilder;
import org.andresoviedo.app.model3D.model.Object3DData;
import org.andresoviedo.app.model3D.services.stl.STLLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Wavefront loader implementation
 *
 * @author andresoviedo
 */

public class WavefrontLoader2 {

	public static void loadAsync(final Activity parent, URL url, final File currentDir,
								 final String assetsDir, final String modelId, final Object3DBuilder.Callback callback)
	{
		new LoaderTask(parent,url,currentDir,assetsDir,modelId,callback){

			// TODO: move this method inside the wavefront loader
			private InputStream getInputStream() {
				Log.i("LoaderTask", "Opening " + modelId + "...");
				try {
					final InputStream ret;
					if (currentDir != null) {
						return new FileInputStream(new File(currentDir, modelId));
					} else if (assetsDir != null) {
						return parent.getAssets().open(assetsDir + "/" + modelId);
					} else {
						throw new IllegalArgumentException("Model data source not specified");
					}
				} catch (IOException ex) {
					throw new RuntimeException(
							"There was a problem opening file/asset '" + (currentDir != null ? currentDir : assetsDir) + "/" + modelId + "'");
				}
			}

			private void closeStream(InputStream stream) {
				if (stream == null) return;
				try {
					if (stream != null) {
						stream.close();
					}
				} catch (IOException ex) {
					Log.e("LoaderTask", "Problem closing stream: " + ex.getMessage(), ex);
				}
			}

			@Override
			protected Object3DData build() throws IOException {
				InputStream params0 = getInputStream();
				org.andresoviedo.app.model3D.services.WavefrontLoader wfl = new org.andresoviedo.app.model3D.services.WavefrontLoader("");

				// allocate memory
				publishProgress(0);
				wfl.analyzeModel(params0);
				closeStream(params0);

				// Allocate memory
				publishProgress(1);
				wfl.allocateBuffers();
				wfl.reportOnModel();

				// create the 3D object
				Object3DData data3D = new Object3DData(wfl.getVerts(), wfl.getNormals(), wfl.getTexCoords(), wfl.getFaces(),
						wfl.getFaceMats(), wfl.getMaterials());
				data3D.setId(modelId);
				data3D.setCurrentDir(currentDir);
				data3D.setAssetsDir(assetsDir);
				data3D.setLoader(wfl);
				data3D.setDrawMode(GLES20.GL_TRIANGLES);
				data3D.setDimensions(data3D.getLoader().getDimensions());

				return data3D;
			}

			@Override
			protected void build(Object3DData data) throws Exception {
				InputStream stream = getInputStream();
				try {
					// parse model
					publishProgress(2);
					data.getLoader().loadModel(stream);
					closeStream(stream);

					// scale object
					publishProgress(3);
					data.centerScale();

					// draw triangles instead of points
					data.setDrawMode(GLES20.GL_TRIANGLES);

					// build 3D object buffers
					publishProgress(4);
					Object3DBuilder.generateArrays(parent.getAssets(), data);
					publishProgress(5);

				} catch (Exception e) {
					Log.e("Object3DBuilder", e.getMessage(), e);
					throw e;
				} finally {
					closeStream(stream);
				}
			}
		}.execute();
	}
}
