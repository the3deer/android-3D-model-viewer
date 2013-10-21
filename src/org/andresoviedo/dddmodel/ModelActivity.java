package org.andresoviedo.dddmodel;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

public class ModelActivity extends Activity {

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
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.model, menu);
		return true;
	}

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

class ModelSurfaceView extends GLSurfaceView {

	public ModelSurfaceView(Context context) {
		super(context);
		setRenderer(new ModelRenderer());
	}

}

class ModelRenderer implements GLSurfaceView.Renderer {

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Set the background frame color
		// GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
		
		// enable face culling feature
		gl.glEnable(GL10.GL_CULL_FACE);
		// specify which faces to not draw
		gl.glCullFace(GL10.GL_BACK);

	}

	public void onDrawFrame(GL10 gl) {
		// Redraw background color
		// GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		
		 // Set GL_MODELVIEW transformation mode
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();                      // reset the matrix to its default state

        // When using GL_MODELVIEW, you must set the camera view
        GLU.gluLookAt(gl, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		// GLES20.glViewport(0, 0, width, height);

		gl.glViewport(0, 0, width, height);

		// make adjustments for screen ratio
		float ratio = (float) width / height;
		gl.glMatrixMode(GL10.GL_PROJECTION); // set matrix to projection mode
		gl.glLoadIdentity(); // reset the matrix to its default state
		gl.glFrustumf(-ratio, ratio, -1, 1, 3, 7); // apply the projection
													// matrix
	}

}
