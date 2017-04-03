package org.andresoviedo.app.model3D.view;

import java.io.File;

import org.andresoviedo.app.model3D.services.ExampleSceneLoader;
import org.andresoviedo.app.model3D.services.SceneLoader;
import org.andresoviedo.app.util.Utils;
import org.andresoviedo.dddmodel2.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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

	private SceneLoader scene;

	private Handler handler;

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

		handler = new Handler(getMainLooper());

		// Create a GLSurfaceView instance and set it
		// as the ContentView for this Activity.
		gLView = new ModelSurfaceView(this);
		setContentView(gLView);

		// Create our 3D sceneario
		if (paramFilename == null && paramAssetFilename == null) {
			scene = new ExampleSceneLoader(this);
		} else {
			scene = new SceneLoader(this);
		}
		scene.init();

		// Show the Up button in the action bar.
		setupActionBar();

		// TODO: Alert user when there is no multitouch support (2 fingers). He won't be able to rotate or zoom for
		// example
		Utils.printTouchCapabilities(getPackageManager());

		setupOnSystemVisibilityChangeListener();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.model, menu);
		return true;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupOnSystemVisibilityChangeListener() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			return;
		}
		getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
			@Override
			public void onSystemUiVisibilityChange(int visibility) {
				// Note that system bars will only be "visible" if none of the
				// LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
				if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
					// TODO: The system bars are visible. Make any desired
					// adjustments to your UI, such as showing the action bar or
					// other navigational controls.
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
						hideSystemUIDelayed(3000);
					}
				} else {
					// TODO: The system bars are NOT visible. Make any desired
					// adjustments to your UI, such as hiding the action bar or
					// other navigational controls.
				}
			}
		});
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				hideSystemUIDelayed(3000);
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.model_toggle_wireframe:
			scene.toggleWireframe();
			break;
		case R.id.model_toggle_boundingbox:
			scene.toggleBoundingBox();
			break;
		case R.id.model_toggle_textures:
			scene.toggleTextures();
			break;
		case R.id.model_toggle_lights:
			scene.toggleLighting();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void hideSystemUIDelayed(long millis) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			return;
		}
		handler.postDelayed(new Runnable() {
			public void run() {
				hideSystemUI();
			}
		}, millis);
	}

	private void hideSystemUI() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			hideSystemUIKitKat();
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			hideSystemUIJellyBean();
		}
	}

	// This snippet hides the system bars.
	@TargetApi(Build.VERSION_CODES.KITKAT)
	private void hideSystemUIKitKat() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			return;
		}
		// Set the IMMERSIVE flag.
		// Set the content to appear under the system bars so that the content
		// doesn't resize when the system bars hide and show.
		final View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
				| View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
				| View.SYSTEM_UI_FLAG_IMMERSIVE);
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void hideSystemUIJellyBean() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			return;
		}
		final View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LOW_PROFILE);
	}

	// This snippet shows the system bars. It does this by removing all the flags
	// except for the ones that make the content appear under the system bars.
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void showSystemUI() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			return;
		}
		final View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
	}

	public File getParamFile() {
		return getParamFilename() != null ? new File(getParamFilename()) : null;
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
		return scene;
	}

	public GLSurfaceView getgLView() {
		return gLView;
	}

}
