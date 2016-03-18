package org.andresoviedo.app.model3D;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import org.andresoviedo.app.model3D.entities.Axis;
import org.andresoviedo.app.model3D.entities.Camera;
import org.andresoviedo.app.model3D.impl1.GLES20Object;
import org.andresoviedo.app.model3D.loader3D.wavefront.WavefrontLoader;
import org.andresoviedo.app.model3D.models.Square;
import org.andresoviedo.app.model3D.models.Triangle;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

class ModelRenderer implements GLSurfaceView.Renderer {

	private final static String TAG = ModelRenderer.class.getName();

	private int width;
	private int height;

	private Axis axis;
	private Camera camera;
	private Triangle mTriangle;
	private Triangle mTriangle2;
	private Square mSquare;
	private Square mSquare2;
	private GLES20Object mSquare3;
	private WavefrontLoader wavefrontLoader;
	private GLES20Object wavefrontModel;
	private GLES20Object bicho;

	// mMVPMatrix is an abbreviation for "Model View Projection Matrix"
	private final float[] mMVPMatrix = new float[16];
	private final float[] mProjectionMatrix = new float[16];
	private final float[] mViewMatrix = new float[16];

	private float squarePosX;

	private float ratio;

	private ModelSurfaceView main;

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

		axis = new Axis();
		camera = new Camera();
		mTriangle = new Triangle();
		mTriangle2 = new Triangle();
		mTriangle2.setColor(new float[] { 0f, 0.76953125f, 0.22265625f, 0.0f });
		mSquare = new Square();
		mSquare2 = new Square();

		//@formatter:off
		mSquare3 = new GLES20Object(new float[] {
				-0.5f, 0.5f, 0.0f, // top left
				-0.5f, -0.5f, 0.0f, // bottom left
				0.5f, -0.5f, 0.0f, // bottom right
				0.5f, 0.5f, 0.0f, /* up right */
				
				-0.5f, 0.5f, 0.25f, /*  */
				0.5f, 0.5f, 0.25f }, 
				
				new short[] {
				  0,1,2,
				  0,2,3,
				  0,4,3,
				  5,4,3
				},new float[]{0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0},null, GLES20.GL_TRIANGLE_STRIP,-1, null);
		// @formatter:on
		mSquare3.setColor(new float[] { 0f, 0.76953125f, 0.22265625f, 0.5f });

		//@formatter:off
		try {InputStream open = main.getContext().getAssets().open("models/penguin.bmp");
				bicho = new GLES20Object(new float[] {
						-0.5f, 0.5f, 0.0f, // top left
						-0.5f, -0.5f, 0.0f, // bottom left
						0.5f, -0.5f, 0.0f, // bottom right
						0.5f, 0.5f, 0.0f, /* up right */}, 
						
						new short[] {
						  0,1,2,
						  0,2,3,
						},new float[]{0,0,1,0,0,1,0,0,1,0,0,1},new float[]{0f,1f,0f,0f,1f,0f,1f,1f}, GLES20.GL_TRIANGLE_STRIP,-1, open);
				// @formatter:on

			open.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		bicho.setColor(new float[] { 0f, 0.0f, 1f, 0.5f });

		wavefrontLoader = new WavefrontLoader("wavefront_loader");

		initializeModels();

	}

	private void initializeModels() {
		mSquare.setPosition(new float[] { -1.5f, -1.5f, -1.5f });
		mSquare2.setPosition(new float[] { 1.5f, 1.5f, 1.5f });
		mTriangle.setPosition(new float[] { 1.5f, 1.5f, -1.5f });
		mTriangle2.setPosition(new float[] { 1.5f, -1.5f, 1.5f });

		mSquare3.setPosition(new float[] { 0f, 0.0f, -1.0f });

		try {
			wavefrontLoader.loadModelFromClasspath(main.getContext().getAssets(), "models/teapot.obj");
			wavefrontModel = wavefrontLoader.createGLES20Object(main.getContext().getAssets(), GLES20.GL_TRIANGLE_STRIP,
					3);
			wavefrontModel.setPosition(new float[] { 0f, 0.0f, 0.0f });
			wavefrontModel.setColor(new float[] { 0.9f, 0.0f, 0.0f, 0.5f });
		} catch (Exception ex) {
			Log.e("renderer", ex.getMessage(), ex);
		}

		bicho.setPosition(new float[] { -0.5f, 0.0f, 0.0f });
		bicho.setRotation(new float[] { 0f, 1.0f, 0.0f });
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height) {
		this.width = width;
		this.height = height;

		// Adjust the viewport based on geometry changes,
		// such as screen rotation
		GLES20.glViewport(0, 0, width, height);

		// INFO: Set the camera position (View matrix)
		// camera.xPos, camera.yPos, camera.zPos, camera.xView, camera.yView,
		// camera.zView, camera.xUp, camera.yUp, camera.zUp);
		Matrix.setLookAtM(mViewMatrix, 0, camera.xPos, camera.yPos, camera.zPos, camera.xView, camera.yView,
				camera.zView, camera.xUp, camera.yUp, camera.zUp);

		// this projection matrix is applied to object coordinates
		// in the onDrawFrame() method
		// bottom / top / near / far
		ratio = (float) width / height;
		Log.d(TAG, "projection: [" + -ratio + "," + ratio + ",-1,1]-near/far[3,7]");
		Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 1f, 10f);

		// Calculate the projection and view transformation
		Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

	}

	@Override
	public void onDrawFrame(GL10 unused) {
		float[] result = new float[16];

		// Draw background color
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		if (camera.hasChanged()) {
			Matrix.setLookAtM(mViewMatrix, 0, camera.xPos, camera.yPos, camera.zPos, camera.xView, camera.yView,
					camera.zView, camera.xUp, camera.yUp, camera.zUp);
			// Log.d("Camera", "Changed! :"+camera.ToStringVector());
			Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
			camera.setChanged(false);
		}

		axis.draw(mMVPMatrix);

		// Log.d(TAG, "angle: " + mAngle + ", tr: " + tr);

		float[] rotationMatrix = new float[16];
		float[] translationMatrix = new float[16];

		// Draw square 1
		Matrix.setIdentityM(rotationMatrix, 0);
		Matrix.setRotateM(rotationMatrix, 0, mSquare.getRotationZ(), 0, 0, 1.0f);
		Matrix.setIdentityM(translationMatrix, 0);
		Matrix.translateM(translationMatrix, 0, mSquare.getPosition()[0], mSquare.getPosition()[1],
				mSquare.getPosition()[2]);
		Matrix.multiplyMM(result, 0, translationMatrix, 0, rotationMatrix, 0);
		Matrix.multiplyMM(result, 0, mMVPMatrix, 0, result, 0);

		mSquare.draw(result);

		// Draw square 2
		Matrix.setIdentityM(rotationMatrix, 0);
		Matrix.setRotateM(rotationMatrix, 0, mSquare2.getRotationZ(), 0, 0, 1.0f);
		Matrix.setIdentityM(translationMatrix, 0);
		Matrix.translateM(translationMatrix, 0, mSquare2.getPosition()[0], mSquare2.getPosition()[1],
				mSquare2.getPosition()[2]);
		Matrix.multiplyMM(result, 0, translationMatrix, 0, rotationMatrix, 0);
		Matrix.multiplyMM(result, 0, mMVPMatrix, 0, result, 0);
		mSquare2.draw(result);

		// Draw square 3
		Matrix.setIdentityM(rotationMatrix, 0);
		Matrix.setRotateM(rotationMatrix, 0, mSquare3.getRotationZ(), 0, 0, 1.0f);
		Matrix.setIdentityM(translationMatrix, 0);
		Matrix.translateM(translationMatrix, 0, mSquare3.getPosition()[0], mSquare3.getPosition()[1],
				mSquare3.getPosition()[2]);
		Matrix.multiplyMM(result, 0, translationMatrix, 0, rotationMatrix, 0);
		Matrix.multiplyMM(result, 0, mMVPMatrix, 0, result, 0);
		mSquare3.draw(result, mViewMatrix);
		mSquare3.drawBoundingBox(result, mViewMatrix);

		Matrix.setIdentityM(rotationMatrix, 0);
		Matrix.setRotateM(rotationMatrix, 0, 0, bicho.getRotation()[1], 0, 1.0f);
		Matrix.setIdentityM(translationMatrix, 0);
		Matrix.translateM(translationMatrix, 0, bicho.getPosition()[0], bicho.getPosition()[1], bicho.getPosition()[2]);
		Matrix.multiplyMM(result, 0, translationMatrix, 0, rotationMatrix, 0);
		Matrix.multiplyMM(result, 0, mMVPMatrix, 0, result, 0);
		bicho.draw(result, mViewMatrix);
		// bicho.drawBoundingBox(scratch, mViewMatrix);

		// Draw penguin
		Matrix.setIdentityM(rotationMatrix, 0);
		Matrix.setRotateM(rotationMatrix, 0, wavefrontModel.getRotationZ(), 0, 0, 1.0f);
		Matrix.setIdentityM(translationMatrix, 0);
		Matrix.translateM(translationMatrix, 0, wavefrontModel.getPosition()[0], wavefrontModel.getPosition()[1],
				wavefrontModel.getPosition()[2]);
		Matrix.multiplyMM(result, 0, translationMatrix, 0, rotationMatrix, 0);
		Matrix.multiplyMM(result, 0, mMVPMatrix, 0, result, 0);
		// wavefrontModel.setColor(new float[] { 0, 0, 1.0f, 1 });
		// wavefrontModel.draw(result, mViewMatrix);
		wavefrontModel.setColor(new float[] { 0, 0, 1, 0 });
		wavefrontModel.draw(result, mViewMatrix);
		wavefrontModel.drawBoundingBox(result, mViewMatrix);

		// Create a rotation for the triangle

		// Use the following code to generate constant rotation.
		// Leave this code out when using TouchEvents.
		// long time = SystemClock.uptimeMillis() % 4000L;
		// float angle = 0.090f * ((int) time);

		// Combine the rotation matrix with the projection and camera view
		// Note that the mMVPMatrix factor *must be first* in order
		// for the matrix multiplication product to be correct.
		float[] matrix = new float[16];
		Matrix.setIdentityM(matrix, 0);
		// Matrix.rotateM(triangleTranslateMatrix, 0, mTriangle.getRotationZ(), 0, 0f, 1f);
		Matrix.translateM(matrix, 0, mTriangle.getPosition()[0], mTriangle.getPosition()[1],
				mTriangle.getPosition()[2]);
		Matrix.multiplyMM(result, 0, mMVPMatrix, 0, matrix, 0);
		// Draw triangle
		mTriangle.draw(result);

		Matrix.setIdentityM(matrix, 0);
		// Matrix.rotateM(triangleTranslateMatrix, 0, mTriangle.getRotationZ(), 0, 0f, 1f);
		Matrix.translateM(matrix, 0, mTriangle2.getPosition()[0], mTriangle2.getPosition()[1],
				mTriangle2.getPosition()[2]);
		Matrix.multiplyMM(result, 0, mMVPMatrix, 0, matrix, 0);
		mTriangle2.draw(result);
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Triangle getmTriangle() {
		return mTriangle;
	}

	public void setmTriangle(Triangle mTriangle) {
		this.mTriangle = mTriangle;
	}

	public Square getmSquare() {
		return mSquare;
	}

	public void setmSquare(Square mSquare) {
		this.mSquare = mSquare;
	}

	public float[] getmMVPMatrix() {
		return mMVPMatrix;
	}

	public float[] getmProjectionMatrix() {
		return mProjectionMatrix;
	}

	public float[] getmViewMatrix() {
		return mViewMatrix;
	}

	public Camera getCamera() {
		return camera;
	}

	public float getRatio() {
		return ratio;
	}

	public void setRatio(float ratio) {
		this.ratio = ratio;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public float getSquarePosX() {
		return squarePosX;
	}

	public void setSquarePosX(float squarePosX) {
		this.squarePosX = squarePosX;
	}

}