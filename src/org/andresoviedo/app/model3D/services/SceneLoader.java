package org.andresoviedo.app.model3D.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.andresoviedo.app.model3D.model.Object3D;
import org.andresoviedo.app.model3D.model.Object3DBuilder;
import org.andresoviedo.app.model3D.model.Object3DData;
import org.andresoviedo.app.model3D.model.ObjectV1;
import org.andresoviedo.app.model3D.model.ObjectV2;
import org.andresoviedo.app.model3D.view.ModelActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.opengl.GLES20;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * This class loads a 3D scena as an example of what can be done with the app
 * 
 * @author andresoviedo
 *
 */
public class SceneLoader {

	/**
	 * Parent component
	 */
	private final ModelActivity parent;
	/**
	 * The file from where the model should be loaded
	 */
	private File modelFile = null;
	/**
	 * Whether to draw the axis
	 */
	private boolean drawAxis = true;
	/**
	 * The 3D axis
	 */
	private Object3D axis;
	/**
	 * 3D object data
	 */
	private Object3DData obj3DData;
	/**
	 * The OpenGL object
	 */
	private Object3D obj3D;
	/**
	 * Set of 3D objects this scene has
	 */
	private List<Object3D> objects = new ArrayList<Object3D>();

	public SceneLoader(ModelActivity main) {
		this.parent = main;
		init();
	}

	private void init() {
		Log.i("Loader", "Opening stream...");
		final InputStream modelDataStream;
		try {
			if (parent.getParamFilename() != null) {
				modelFile = new File(parent.getParamFilename());
				modelDataStream = new FileInputStream(modelFile);
			} else if (parent.getParamAssetDir() != null && parent.getParamAssetFilename() != null) {
				modelDataStream = parent.getAssets().open(parent.getParamAssetDir() + parent.getParamAssetFilename());
			} else {
				Toast.makeText(parent.getApplicationContext(), "Model data source not specified", Toast.LENGTH_LONG)
						.show();
				return;
			}
		} catch (IOException ex) {
			Toast.makeText(parent.getApplicationContext(),
					"There was a problem loading model from '" + (modelFile != null ? modelFile
							: parent.getParamAssetDir() + parent.getParamAssetFilename()) + "'",
					Toast.LENGTH_LONG).show();
			return;
		}

		Log.i("Loader", "Loading model...");
		LoaderTask loaderTask = new LoaderTask(parent, modelFile != null ? modelFile.getParentFile() : null,
				parent.getParamAssetDir(), modelFile != null ? modelFile.getName() : parent.getParamAssetFilename()) {

			@Override
			protected void onPostExecute(Object3DData data) {
				super.onPostExecute(data);
				obj3DData = data;
				try {
					modelDataStream.close();
				} catch (IOException ex) {
					Log.e("Menu", "Problem closing stream: " + ex.getMessage(), ex);
				}
				parent.getgLView().requestRender();
			}
		};
		loaderTask.execute(modelDataStream);
	}

	public void refresh() {
		if (obj3DData == null) {
			return;
		}
		if (obj3D != null) {
			return;
		}

		// Draw the axis
		if (drawAxis) {
			axis = Object3DBuilder.build(new float[] { 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, // right
					0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, // left
					0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, // up
					0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, // down
					0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, // z+
					0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, // z-

					0.95f, 0.05f, 0, 1, 0, 0, 0.95f, -0.05f, 0, 1, 0f, 0f, // Arrow X (>)
					-0.95f, 0.05f, 0, -1, 0, 0, -0.95f, -0.05f, 0, -1, 0f, 0f, // Arrow X (<)
					-0.05f, 0.95f, 0, 0, 1, 0, 0.05f, 0.95f, 0, 0, 1f, 0f, // Arrox Y (^)
					-0.05f, 0, 0.95f, 0, 0, 1, 0.05f, 0, 0.95f, 0, 0, 1, // Arrox z (v)

					1.05F, 0.05F, 0, 1.10F, -0.05F, 0, 1.05F, -0.05F, 0, 1.10F, 0.05F, 0, // Letter X
					-0.05F, 1.05F, 0, 0.05F, 1.10F, 0, -0.05F, 1.10F, 0, 0.0F, 1.075F, 0, // Letter Y
					-0.05F, 0.05F, 1.05F, 0.05F, 0.05F, 1.05F, 0.05F, 0.05F, 1.05F, -0.05F, -0.05F, 1.05F, -0.05F,
					-0.05F, 1.05F, 0.05F, -0.05F, 1.05F }, GLES20.GL_LINES);
			axis.setColor(new float[] { 1.0f, 0, 0, 1.0f });
			objects.add(axis);
		}

		try {
			obj3D = Object3DBuilder.build(obj3DData, GLES20.GL_TRIANGLES, 3);
			objects.add(obj3D);
		} catch (IOException ex) {
			Toast.makeText(parent.getApplicationContext(), "There was a problem creating 3D object", Toast.LENGTH_LONG)
					.show();
		}
	}

	public List<Object3D> getObjects() {
		return objects;
	}

	public ObjectV1 getTriangle1() {
		return null;
	}

	public ObjectV2 getSquare1() {
		return null;
	}

}

/**
 * This component allows loading the model without blocking the UI.
 * 
 * @author andresoviedo
 *
 */
class LoaderTask extends AsyncTask<InputStream, Integer, Object3DData> {

	/**
	 * The parent activity
	 */
	private final Activity parent;
	/**
	 * Directory where the model is located (null when its loaded from asset)
	 */
	private final File currentDir;
	/**
	 * Asset directory where the model is loaded (null when its loaded from the filesystem)
	 */
	private final String assetsDir;
	/**
	 * Id of the data being loaded
	 */
	private final String modelId;
	/**
	 * The dialog that will show the progress of the loading
	 */
	private final ProgressDialog dialog;

	/**
	 * Build a new progress dialog for loading the data model asynchronously
	 * 
	 * @param currentDir
	 *            the directory where the model is located (null when the model is an asset)
	 * @param modelId
	 *            the id the data being loaded
	 * 
	 */
	public LoaderTask(Activity parent, File currentDir, String assetsDir, String modelId) {
		this.parent = parent;
		this.currentDir = currentDir;
		this.assetsDir = assetsDir;
		this.modelId = modelId;
		this.dialog = new ProgressDialog(parent);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// this.dialog = ProgressDialog.show(this.parent, "Please wait ...", "Loading model data...", true);
		// this.dialog.setTitle(modelId);
		// this.dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		this.dialog.setCancelable(false);
		this.dialog.show();
	}

	@Override
	protected Object3DData doInBackground(InputStream... params) {
		publishProgress(0);

		WavefrontLoader wfl = new WavefrontLoader("");
		wfl.loadModel(params[0]);
		publishProgress(1);

		Object3DData data3D = new Object3DData(wfl.getVerts(), wfl.getNormals(), wfl.getTexCoords(), wfl.getFaces(),
				wfl.getFaceMats(), wfl.getMaterials());
		data3D.setId(modelId);
		data3D.setCurrentDir(currentDir);
		data3D.setAssetsDir(assetsDir);

		try {
			Object3DBuilder.generateArrays(parent.getAssets(), data3D);
			publishProgress(2);
			return data3D;
		} catch (IOException ex) {
			Toast.makeText(parent.getApplicationContext(), "There was a problem building the model: " + ex.getMessage(),
					Toast.LENGTH_LONG).show();
			return null;
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		switch (values[0]) {
		case 0:
			this.dialog.setMessage("Loading data...");
			break;
		case 1:
			this.dialog.setMessage("Building 3D model...");
			break;
		case 2:
			this.dialog.setMessage("Model '" + modelId + "' built");
			break;
		}
	}

	@Override
	protected void onPostExecute(Object3DData success) {
		super.onPostExecute(success);
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
	}

}