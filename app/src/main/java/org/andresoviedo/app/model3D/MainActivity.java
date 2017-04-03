package org.andresoviedo.app.model3D;

import java.io.File;

import org.andresoviedo.app.model3D.view.MenuActivity;
import org.andresoviedo.app.model3D.view.ModelActivity;
import org.andresoviedo.app.util.Utils;
import org.andresoviedo.dddmodel2.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

/**
 * This is the main android activity. From here we launch the whole stuff.
 * 
 * Basically, this activity may serve to show a Splash screen and copy the assets (obj models) from the jar to external
 * directory.
 * 
 * @author andresoviedo
 *
 */
public class MainActivity extends Activity {

	/**
	 * User's directory where we are going store the assets (models, textures, etc). It will be copied to
	 * /storage/OpenSource3DModelViewer
	 */
	private static final String ASSETS_TARGET_DIRECTORY = Environment.getExternalStorageDirectory() + File.separator
			+ "3DModelViewerOS";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set main layout controls.
		// Basically, this is a screen with the app name just in the middle of the scree
		setContentView(R.layout.activity_main);

		// TODO: Enable this when I have stabilized the app
		// This is the animated logo
		// From here we get the WebView component then we load the gif from the jar
		// WebView myWebView = (WebView) findViewById(R.id.main_logo_webview);
		// myWebView.loadUrl("file:///android_res/raw/ic_launcher.gif");
		// init();

		// Install example models & textures so the user can get started with app
		// TODO: install? not yet!
		// installExamples();

		// Start Model activity.
		MainActivity.this.startActivity(new Intent(MainActivity.this.getApplicationContext(), MenuActivity.class));
		MainActivity.this.finish();
	}

	@SuppressWarnings("unused")
	private void init() {
		try {
			Thread tcopy = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						installExamples();
					} catch (Exception ex) {
						Toast.makeText(MainActivity.this.getApplicationContext(),
								"Unexpected error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
						Log.e("init", "Unexpected error: " + ex.getMessage(), ex);
					}
				}
			});
			Thread tsplash = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(000);
						MainActivity.this.startActivity(
								new Intent(MainActivity.this.getApplicationContext(), ModelActivity.class));
						MainActivity.this.finish();
					} catch (InterruptedException ex) {
						Toast.makeText(MainActivity.this.getApplicationContext(),
								"Unexpected error: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
						Log.e("init", "Unexpected error: " + ex.getMessage(), ex);
					}
				}
			});
			// tcopy.start();
			// tsplash.start();
		} catch (Exception ex) {
			Toast.makeText(MainActivity.this.getApplicationContext(), "Unexpected error: " + ex.getMessage(),
					Toast.LENGTH_SHORT).show();
			Log.e("init", "Unexpected error: " + ex.getMessage(), ex);
		}

		MainActivity.this.startActivity(new Intent(MainActivity.this.getApplicationContext(), ModelActivity.class));
		MainActivity.this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * Copy all models from jar to the user's sd card.
	 * 
	 * TODO: Alert when the directory already exists and our app is not the directory owner (cause copy will fail)
	 */
	private void installExamples() {
		// TODO: Enable TODO: copy also in internal memory
		if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			Toast.makeText(MainActivity.this.getApplicationContext(), "Couldn't copy assets. Please install an sd-card",
					Toast.LENGTH_SHORT).show();
			return;
		}

		Utils.copyAssets(getApplicationContext(), "models", new File(ASSETS_TARGET_DIRECTORY, "models"));
		// Utils.copyAssets(getApplicationContext(), "textures", new File(ASSETS_TARGET_DIRECTORY, "textures"));
	}
}
