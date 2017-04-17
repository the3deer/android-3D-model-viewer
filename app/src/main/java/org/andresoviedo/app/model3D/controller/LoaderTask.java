package org.andresoviedo.app.model3D.controller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.andresoviedo.app.model3D.model.Object3DBuilder;
import org.andresoviedo.app.model3D.model.Object3DData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * This component allows loading the model without blocking the UI.
 *
 * @author andresoviedo
 */
public abstract class LoaderTask extends AsyncTask<Void, Integer, Object3DData> {

	/**
	 * URL to the 3D model
	 */
	protected final URL url;
	/**
	 * Callback to notify of events
	 */
	protected final Object3DBuilder.Callback callback;
	/**
	 * The dialog that will show the progress of the loading
	 */
	protected final ProgressDialog dialog;
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
	 * Exception when loading data (if any)
	 */
	protected Exception error;

	/**
	 * Build a new progress dialog for loading the data model asynchronously
	 *
	 * @param url        the URL pointing to the 3d model
	 * @param currentDir the directory where the model is located (null when the model is an asset)
	 * @param modelId    the id the data being loaded
	 */
	public LoaderTask(Activity parent, URL url, File currentDir, String assetsDir, String modelId, Object3DBuilder.Callback callback) {
		this.parent = parent;
		this.url = url;
		this.currentDir = currentDir;
		this.assetsDir = assetsDir;
		this.modelId = modelId;
		this.dialog = new ProgressDialog(parent);
		this.callback = callback;
	}


	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// this.dialog = ProgressDialog.show(this.parent, "Please wait ...", "Loading model data...", true);
		// this.dialog.setTitle(modelId);
		this.dialog.setMessage("Loading...");
		this.dialog.setCancelable(false);
		this.dialog.show();
	}



	@Override
	protected Object3DData doInBackground(Void... params) {
		try {
			Object3DData data = build();
			callback.onLoadComplete(data);
			build(data);
			return  data;
		} catch (Exception ex) {
			error = ex;
			return null;
		}
	}

	protected abstract Object3DData build() throws Exception;

	protected abstract void build(Object3DData data) throws Exception;

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		switch (values[0]) {
			case 0:
				this.dialog.setMessage("Analyzing model...");
				break;
			case 1:
				this.dialog.setMessage("Allocating memory...");
				break;
			case 2:
				this.dialog.setMessage("Loading data...");
				break;
			case 3:
				this.dialog.setMessage("Scaling object...");
				break;
			case 4:
				this.dialog.setMessage("Building 3D model...");
				break;
			case 5:
				// Toast.makeText(parent, modelId + " Build!", Toast.LENGTH_LONG).show();
				break;
		}
	}

	@Override
	protected void onPostExecute(Object3DData data) {
		super.onPostExecute(data);
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		if (error != null) {
			callback.onLoadError(error);
		} else {
			callback.onBuildComplete(data);
		}
	}


}