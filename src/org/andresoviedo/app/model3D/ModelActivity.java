package org.andresoviedo.app.model3D;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class ModelActivity extends Activity {

	private static final String TAG = ModelSurfaceView.class.getName();

	private GLSurfaceView mGLView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create a GLSurfaceView instance and set it
		// as the ContentView for this Activity.
		mGLView = new ModelSurfaceView(this);
		setContentView(mGLView);

		// Show the Up button in the action bar.
		setupActionBar();

		printTouchCapabilities();

	}

	private void printTouchCapabilities() {
		if (getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH)) {
			Log.i(TAG, "System supports multitouch (2 fingers)");
		}
		if (getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH_DISTINCT)) {
			Log.i(TAG,
					"System supports advanced multitouch (multiple fingers). Cool!");
		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//			getActionBar().setDisplayHomeAsUpEnabled(true);
//		}
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.model, menu);
//		return true;
//	}

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

}
