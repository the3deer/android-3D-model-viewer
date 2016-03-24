package org.andresoviedo.app.model3D.view;

import java.io.File;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.andresoviedo.app.model3D.entities.Camera;
import org.andresoviedo.app.model3D.model.Object3D;
import org.andresoviedo.app.model3D.services.ExampleSceneLoader;
import org.andresoviedo.app.model3D.services.SceneLoader;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

public class ModelRenderer implements GLSurfaceView.Renderer {

	private final static String TAG = ModelRenderer.class.getName();

	// 3D window (parent component)
	private ModelSurfaceView main;

	// Camera for our 3D world
	private Camera camera;

	// 3D world
	private SceneLoader scene;

	// 3D matrices to project our 3D world
	private final float[] modelProjectionMatrix = new float[16];
	private final float[] modelViewMatrix = new float[16];
	// mvpMatrix is an abbreviation for "Model View Projection Matrix"
	private final float[] mvpMatrix = new float[16];

	// width of the screen
	private int width;
	// height of the screen
	private int height;

	/**
	 * Construct a new renderer for the specified surface view
	 * 
	 * @param modelSurfaceView
	 *            the 3D window
	 */
	public ModelRenderer(ModelSurfaceView modelSurfaceView) {
		this.main = modelSurfaceView;
		String paramUri = modelSurfaceView.getModelActivity().getParamUri();
		if (paramUri != null) {
			this.scene = new SceneLoader(modelSurfaceView, new File(paramUri));
		} else {
			this.scene = new ExampleSceneLoader(modelSurfaceView);
		}
	}

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {

		// Set the background frame color
		GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

		// Use culling to remove back faces.
		GLES20.glEnable(GLES20.GL_CULL_FACE);

		// Enable depth testing for hidden-surface elimination.
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);

		// Enable blending for combining colors when there is transparency
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

		// Lets create our 3D world components
		camera = new Camera();
		scene.init();
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		this.width = width;
		this.height = height;

		// Adjust the viewport based on geometry changes, such as screen rotation
		GLES20.glViewport(0, 0, width, height);

		// INFO: Set the camera position (View matrix)
		// The camera has 3 vectors (the position, the vector where we are looking at, and the up position (sky)
		Matrix.setLookAtM(modelViewMatrix, 0, camera.xPos, camera.yPos, camera.zPos, camera.xView, camera.yView,
				camera.zView, camera.xUp, camera.yUp, camera.zUp);

		// the projection matrix is the 3D virtual space (cube) that we want to project
		float ratio = (float) width / height;
		Log.d(TAG, "projection: [" + -ratio + "," + ratio + ",-1,1]-near/far[1,10]");
		Matrix.frustumM(modelProjectionMatrix, 0, -ratio, ratio, -1, 1, 1f, 10f);

		// Calculate the projection and view transformation
		Matrix.multiplyMM(mvpMatrix, 0, modelProjectionMatrix, 0, modelViewMatrix, 0);
	}

	@Override
	public void onDrawFrame(GL10 unused) {

		// Draw background color
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		// recalculate mvp matrix according to where we are looking at now
		if (camera.hasChanged()) {
			Matrix.setLookAtM(modelViewMatrix, 0, camera.xPos, camera.yPos, camera.zPos, camera.xView, camera.yView,
					camera.zView, camera.xUp, camera.yUp, camera.zUp);
			// Log.d("Camera", "Changed! :"+camera.ToStringVector());
			Matrix.multiplyMM(mvpMatrix, 0, modelProjectionMatrix, 0, modelViewMatrix, 0);
			camera.setChanged(false);
		}

		// Draw scene object
		float[] result = new float[16];
		float[] rotationMatrix = new float[16];
		float[] translationMatrix = new float[16];
		for (Object3D obj3D : scene.getObjects()) {
			Matrix.setIdentityM(rotationMatrix, 0);
			if (obj3D.getRotationZ() != 0) {
				Matrix.setRotateM(rotationMatrix, 0, obj3D.getRotationZ(), 0, 0, 1.0f);
			}
			Matrix.setIdentityM(translationMatrix, 0);
			Matrix.translateM(translationMatrix, 0, obj3D.getPosition()[0], obj3D.getPosition()[1],
					obj3D.getPosition()[2]);
			Matrix.multiplyMM(result, 0, translationMatrix, 0, rotationMatrix, 0);
			Matrix.multiplyMM(result, 0, mvpMatrix, 0, result, 0);
			obj3D.draw(result, modelViewMatrix);
			obj3D.drawBoundingBox(result, modelViewMatrix);
			// TODO: enable this only when user wants it
			// obj3D.drawVectorNormals(result, modelViewMatrix);
		}
	}

	public SceneLoader getScene() {
		return scene;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public float[] getModelProjectionMatrix() {
		return modelProjectionMatrix;
	}

	public float[] getModelViewMatrix() {
		return modelViewMatrix;
	}

	public Camera getCamera() {
		return camera;
	}
}