package org.andresoviedo.app.model3D.view;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.andresoviedo.app.model3D.entities.Camera;
import org.andresoviedo.app.model3D.model.Object3D;
import org.andresoviedo.app.model3D.model.Object3DBuilder;
import org.andresoviedo.app.model3D.model.Object3DData;
import org.andresoviedo.app.model3D.services.SceneLoader;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.widget.Toast;

public class ModelRenderer implements GLSurfaceView.Renderer {

	private final static String TAG = ModelRenderer.class.getName();

	// 3D window (parent component)
	private ModelSurfaceView main;
	// width of the screen
	private int width;
	// height of the screen
	private int height;
	// Out point of view handler
	private Camera camera;
	// Whether to draw the axis
	private boolean drawAxis = true;
	// Whether to draw objects as wireframe
	private boolean drawWireframe = true;
	// Whether to draw a cube bounding the model
	private boolean drawBoundingBox = true;
	// Whether to draw the face normals
	private boolean drawNormals = true;
	// The 3D axis
	private Object3D axis;
	// The corresponding scene opengl object wireframes
	private Map<Object3DData, Object3D> wireframes = new HashMap<Object3DData, Object3D>();
	// The corresponding scene opengl objects
	private Map<Object3DData, Object3D> objects = new HashMap<Object3DData, Object3D>();
	// The corresponding opengl bounding boxes
	private Map<Object3DData, Object3D> boundingBoxes = new HashMap<Object3DData, Object3D>();
	// The corresponding opengl bounding boxes
	private Map<Object3DData, Object3D> normals = new HashMap<Object3DData, Object3D>();

	// 3D matrices to project our 3D world
	private final float[] modelProjectionMatrix = new float[16];
	private final float[] modelViewMatrix = new float[16];
	// mvpMatrix is an abbreviation for "Model View Projection Matrix"
	private final float[] mvpMatrix = new float[16];

	/**
	 * Construct a new renderer for the specified surface view
	 * 
	 * @param modelSurfaceView
	 *            the 3D window
	 */
	public ModelRenderer(ModelSurfaceView modelSurfaceView) {
		this.main = modelSurfaceView;
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

		// Draw Axis
		if (drawAxis) {
			if (axis == null) {
				axis = Object3DBuilder.buildAxis();
				axis.setColor(new float[] { 1.0f, 0, 0, 1.0f });
			}
			axis.draw(mvpMatrix, modelViewMatrix);
		}

		// Draw scene object
		float[] result = new float[16];
		float[] rotationMatrix = new float[16];
		float[] translationMatrix = new float[16];
		SceneLoader scene = main.getModelActivity().getScene();
		if (scene == null) {
			// scene not ready
			return;
		}

		for (Object3DData objData : scene.getObjects()) {
			try {
				boolean changed = objData.isChanged();

				// calculate mvp matrix
				Matrix.setIdentityM(rotationMatrix, 0);
				if (objData.getRotationZ() != 0) {
					Matrix.setRotateM(rotationMatrix, 0, objData.getRotationZ(), 0, 0, 1.0f);
				}
				Matrix.setIdentityM(translationMatrix, 0);
				Matrix.translateM(translationMatrix, 0, objData.getPositionX(), objData.getPositionY(),
						objData.getPositionZ());
				Matrix.multiplyMM(result, 0, translationMatrix, 0, rotationMatrix, 0);
				Matrix.multiplyMM(result, 0, mvpMatrix, 0, result, 0);

				// draw objects
				if (drawWireframe) {
					Object3D object = wireframes.get(objData);
					if (object == null || changed) {
						object = Object3DBuilder.buildWireframe(objData);
						if (object != null)
							wireframes.put(objData, object);
					}
					if (object != null)
						object.draw(result, modelViewMatrix);
				}
				
				Object3D object = objects.get(objData);
				if (object == null || changed) {
					object = Object3DBuilder.build(objData);
					objects.put(objData, object);
				}
				object.draw(result, modelViewMatrix);

				// Draw bounding box
				if (drawBoundingBox) {
					Object3D boundingBox = boundingBoxes.get(objData);
					if (boundingBox == null || changed) {
						boundingBox = Object3DBuilder.build(Object3DBuilder.buildBoundingBox(objData));
						boundingBoxes.put(objData, boundingBox);
					}
					boundingBox.draw(result, modelViewMatrix);
				}

				// Draw bounding box
				if (drawNormals) {
					Object3D normal = normals.get(objData);
					if (normal == null || changed) {
						Object3DData normalData = Object3DBuilder.buildFaceNormals(objData);
						if (normalData != null) {
							// it can be null if object isnt made of triangles
							normal = Object3DBuilder.build(normalData);
							normals.put(objData, normal);
						}
					}
					if (normal != null) {
						normal.draw(result, modelViewMatrix);
					}
				}
				// TODO: enable this only when user wants it
				// obj3D.drawVectorNormals(result, modelViewMatrix);
			} catch (IOException ex) {
				Toast.makeText(main.getModelActivity().getApplicationContext(),
						"There was a problem creating 3D object", Toast.LENGTH_LONG).show();
			}
		}
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