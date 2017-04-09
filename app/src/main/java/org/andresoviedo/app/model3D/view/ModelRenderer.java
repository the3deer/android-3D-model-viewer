package org.andresoviedo.app.model3D.view;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.andresoviedo.app.model3D.entities.Camera;
import org.andresoviedo.app.model3D.model.Object3D;
import org.andresoviedo.app.model3D.model.Object3DBuilder;
import org.andresoviedo.app.model3D.model.Object3DData;
import org.andresoviedo.app.model3D.services.SceneLoader;
import org.andresoviedo.app.model3D.util.GLUtil;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
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
	// frustrum - nearest pixel
	private float near = 1f;
	// frustrum - fartest pixel
	private float far = 10f;

	private Object3DBuilder drawer;
	// The wireframe associated shape (it should be made of lines only)
	private Map<Object3DData, Object3DData> wireframes = new HashMap<Object3DData, Object3DData>();
	// The loaded textures
	private Map<byte[], Integer> textures = new HashMap<byte[], Integer>();
	// The corresponding opengl bounding boxes and drawer
	private Map<Object3DData, Object3DData> boundingBoxes = new HashMap<Object3DData, Object3DData>();
	// The corresponding opengl bounding boxes
	private Map<Object3DData, Object3DData> normals = new HashMap<Object3DData, Object3DData>();

	// 3D matrices to project our 3D world
	private final float[] modelProjectionMatrix = new float[16];
	private final float[] modelViewMatrix = new float[16];
	// mvpMatrix is an abbreviation for "Model View Projection Matrix"
	private final float[] mvpMatrix = new float[16];

	// light
	private final float[] lightPosInEyeSpace = new float[4];
	private final float[] mMatrixLight = new float[16];
	private final float[] rotation =  new float[]{0, 0, 0};
	private final float[] mLightPosInWorldSpace = new float[4];
	private final float[] mvMatrixLight = new float[16];
	private final float[] mvpMatrixLight = new float[16];
	private final Object3DData lightPoint = Object3DBuilder.buildPoint(new float[4]).setId("light");

	/**
	 * Construct a new renderer for the specified surface view
	 *
	 * @param modelSurfaceView
	 *            the 3D window
	 */
	public ModelRenderer(ModelSurfaceView modelSurfaceView) {
		this.main = modelSurfaceView;
	}

	public float getNear() {
		return near;
	}

	public float getFar() {
		return far;
	}

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config) {
		// Set the background frame color
		GLES20.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);

		// Use culling to remove back faces.
		// Don't remove back faces so we can see them
		// GLES20.glEnable(GLES20.GL_CULL_FACE);

		// Enable depth testing for hidden-surface elimination.
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);

		// Enable blending for combining colors when there is transparency
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

		// Lets create our 3D world components
		camera = new Camera();

		// This component will draw the actual models using OpenGL
		drawer = new Object3DBuilder();
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
		Matrix.frustumM(modelProjectionMatrix, 0, -ratio, ratio, -1, 1, getNear(), getFar());

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

		SceneLoader scene = main.getModelActivity().getScene();
		if (scene == null) {
			// scene not ready
			return;
		}


		// camera should know about objects that collision with it
		camera.setScene(scene);


		if (scene.isDrawLighting()) {
			float[] lightPos = scene.getLightPos();

			// Do a complete rotation every 10 seconds.
			long time = SystemClock.uptimeMillis() % 10000L;
			float angleInDegrees = (360.0f / 10000.0f) * ((int) time);

			lightPoint.getVertexArrayBuffer().clear();
			lightPoint.getVertexArrayBuffer().put(lightPos);

			rotation[1] = angleInDegrees;
			lightPoint.setRotation(rotation);

			// calculate light matrix

			// Calculate position of the light. Rotate and then push into the distance.
			Matrix.setIdentityM(mMatrixLight, 0);
			// Matrix.translateM(mMatrixLight, 0, lightPos[0], lightPos[1], lightPos[2]);
			Matrix.rotateM(mMatrixLight, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
			// Matrix.translateM(mMatrixLight, 0, 0.0f, 0.0f, 2.0f);

			Matrix.multiplyMV(mLightPosInWorldSpace, 0, mMatrixLight, 0, lightPos, 0);

			Matrix.multiplyMV(lightPosInEyeSpace, 0, modelViewMatrix, 0, mLightPosInWorldSpace, 0);

			Matrix.multiplyMM(mvMatrixLight, 0, modelViewMatrix, 0, mMatrixLight, 0);
			Matrix.multiplyMM(mvpMatrixLight, 0, modelProjectionMatrix, 0, mvMatrixLight, 0);

			drawer.getPointDrawer().draw(lightPoint, modelProjectionMatrix, modelViewMatrix, -1, lightPosInEyeSpace);
			// // Draw a point to indicate the light.
			// GLES20.glUseProgram(mPointProgramHandle);
			// drawLight(mvpMatrixLight, lightPos);
		}

		List<Object3DData> objects = scene.getObjects();
		for (int i=0; i<objects.size(); i++) {
			try {
				Object3DData objData = objects.get(i);
				boolean changed = objData.isChanged();

				Object3D drawerObject = drawer.getDrawer(objData, scene.isDrawTextures(), scene.isDrawLighting());
				// Log.d("ModelRenderer","Drawing object using '"+drawerObject.getClass()+"'");

				Integer textureId = textures.get(objData.getTextureData());
				if (textureId == null && objData.getTextureData() != null) {
					ByteArrayInputStream textureIs = new ByteArrayInputStream(objData.getTextureData());
					textureId = GLUtil.loadTexture(textureIs);
					textureIs.close();
					textures.put(objData.getTextureData(), textureId);
				}

				if (scene.isDrawWireframe() && objData.getDrawMode() != GLES20.GL_POINTS
						&& objData.getDrawMode() != GLES20.GL_LINES && objData.getDrawMode() != GLES20.GL_LINE_STRIP
						&& objData.getDrawMode() != GLES20.GL_LINE_LOOP) {
					Log.d("ModelRenderer","Drawing wireframe model...");
					try{
						// Only draw wireframes for objects having faces (triangles)
						Object3DData wireframe = wireframes.get(objData);
						if (wireframe == null || changed) {
							Log.i("ModelRenderer","Generating wireframe model...");
//							wireframe = Object3DBuilder.buildWireframe4(objData);
//							wireframe.centerAndScale(5.0f);
							wireframe = Object3DBuilder.buildWireframe(objData);
							wireframes.put(objData, wireframe);
						}
						Object3D wireframeDrawer = drawer.getWireframeDrawer();
						wireframeDrawer.draw(wireframe, modelProjectionMatrix, modelViewMatrix, wireframe.getDrawMode(), wireframe.getDrawSize(),
								textureId != null ? textureId : -1, lightPosInEyeSpace);
					}catch(Error e){
						Log.e("ModelRenderer",e.getMessage(),e);
					}
				} else {

					drawerObject.draw(objData, modelProjectionMatrix, modelViewMatrix,
							textureId != null ? textureId : -1, lightPosInEyeSpace);
				}

				// Draw bounding box
				if (scene.isDrawBoundingBox() || scene.getSelectedObject() == objData) {
					Object3DData boundingBoxData = boundingBoxes.get(objData);
					if (boundingBoxData == null || changed) {
						boundingBoxData = Object3DBuilder.buildBoundingBox(objData);
						boundingBoxes.put(objData, boundingBoxData);
					}
					Object3D boundingBoxDrawer = drawer.getBoundingBoxDrawer();
					boundingBoxDrawer.draw(boundingBoxData, modelProjectionMatrix, modelViewMatrix, -1, null);
				}

				// Draw bounding box
				if (scene.isDrawNormals()) {
					Object3DData normalData = normals.get(objData);
					if (normalData == null || changed) {
						normalData = Object3DBuilder.buildFaceNormals(objData);
						if (normalData != null) {
							// it can be null if object isnt made of triangles
							normals.put(objData, normalData);
						}
					}
					if (normalData != null) {
						Object3D normalsDrawer = drawer.getFaceNormalsDrawer();
						normalsDrawer.draw(normalData, modelProjectionMatrix, modelViewMatrix, -1, null);
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