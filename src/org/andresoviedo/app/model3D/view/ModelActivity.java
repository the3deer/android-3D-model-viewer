package org.andresoviedo.app.model3D.view;

import org.andresoviedo.app.model3D.services.ExampleSceneLoader;
import org.andresoviedo.app.model3D.services.SceneLoader;
import org.andresoviedo.app.util.Utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;

/**
 * This activity represents the container for our 3D viewer.
 * 
 * @author andresoviedo
 */
public class ModelActivity extends Activity {

	private String paramAssetDir;
	private String paramAssetFilename;
	/**
	 * The file to load. Passed as input parameter
	 */
	private String paramFilename;

	private GLSurfaceView gLView;

	private SceneLoader sceneLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Try to get input parameters
		Bundle b = getIntent().getExtras();
		if (b != null) {
			this.paramAssetDir = b.getString("assetDir");
			this.paramAssetFilename = b.getString("assetFilename");
			this.paramFilename = b.getString("uri");
		}
		Log.i("Renderer", "Params: assetDir '" + paramAssetDir + "', assetFilename '" + paramAssetFilename + "', uri '"
				+ paramFilename + "'");

		// Create our 3D sceneario
		if (paramFilename == null && paramAssetFilename == null) {
			sceneLoader = new ExampleSceneLoader(this);
		} else {
			sceneLoader = new SceneLoader(this);
		}

		// Create a GLSurfaceView instance and set it
		// as the ContentView for this Activity.
		gLView = new ModelSurfaceView(this);
		setContentView(gLView);

		// Show the Up button in the action bar.
		setupActionBar();

		// TODO: Alert user when there is no multitouch support (2 fingers). He won't be able to rotate or zoom for
		// example
		Utils.printTouchCapabilities(getPackageManager());
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		// getActionBar().setDisplayHomeAsUpEnabled(true);
		// }
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.model, menu);
	// return true;
	// }

	// TODO: set up menu & buttons
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public String getParamAssetDir() {
		return paramAssetDir;
	}

	public String getParamAssetFilename() {
		return paramAssetFilename;
	}

	public String getParamFilename() {
		return paramFilename;
	}

	public SceneLoader getScene() {
		return sceneLoader;
	}

	public GLSurfaceView getgLView() {
		return gLView;
	}

}
