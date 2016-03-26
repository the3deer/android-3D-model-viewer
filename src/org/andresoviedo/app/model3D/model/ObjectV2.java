package org.andresoviedo.app.model3D.model;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.andresoviedo.app.model3D.util.GLUtil;

import android.opengl.GLES20;
import android.util.Log;

/**
 * A 3D object in OpenGL ES 2.0 using indices with {@link GLES20#glDrawElements(int, int, int, java.nio.Buffer)}
 */
public class ObjectV2 implements Object3D {

	// number of coordinates per vertex in this array
	private static final int COORDS_PER_VERTEX = 3;
	private static final int VERTEX_STRIDE = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

	// @formatter:off
	private final String vertexShaderCode =
		"uniform mat4 uMVPMatrix;" + 
		"attribute vec4 vPosition;" + 
		"void main() {" +
			"  gl_Position = uMVPMatrix * vPosition;" + 
		"}";
	// @formatter:on

	// @formatter:off
	private final String fragmentShaderCode = 
		"precision mediump float;"+ 
		"uniform vec4 vColor;" + 
		"void main() {"+ 
		"  gl_FragColor = vColor;" + 
		"}";
	// @formatter:on

	// Model data
	private final FloatBuffer vertexBuffer;
	private final ShortBuffer drawListBuffer;
	private final int drawMode;
	private float color[] = { 0.0f, 1.0f, 0, 1.0f }; // default color is blue

	// Transformation data
	private float[] position = new float[] { 0f, 0f, 0f };
	private float[] rotation = new float[] { 0f, 0f, 0f };

	// OpenGL data
	private final int mProgram;
	private int mMVPMatrixHandle;
	private int mPositionHandle;
	private int mColorHandle;

	public ObjectV2(FloatBuffer vertices, ShortBuffer drawList, int drawMode) {
		this.vertexBuffer = vertices;
		this.drawListBuffer = drawList;
		this.drawMode = drawMode;

		// prepare shaders and OpenGL program
		int vertexShader = GLUtil.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int fragmentShader = GLUtil.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

		mProgram = GLES20.glCreateProgram(); // create empty OpenGL Program
		GLES20.glAttachShader(mProgram, vertexShader); // add the vertex shader to program
		GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
		GLES20.glLinkProgram(mProgram); // create OpenGL program executables
	}

	public float[] getPosition() {
		return position;
	}

	public void setPosition(float[] position) {
		this.position = position;
	}

	public float[] getColor() {
		return color;
	}

	public void setColor(float[] color) {
		this.color = color;
	}

	@Override
	public void draw(float[] mvpMatrix, float[] mvMatrix) {
		this.draw(mvpMatrix);
	}

	@Override
	public void draw(float[] mvpMatrix, float[] mvMatrix, int drawType, int drawSize) {
		this.draw(mvpMatrix);

	}

	@Override
	public void drawBoundingBox(float[] mvpMatrix, float[] mvMatrix) {
		// TODO: implement this
	}

	/**
	 * Encapsulates the OpenGL ES instructions for drawing this shape.
	 * 
	 * @param mvpMatrix
	 *            - The Model View Project matrix in which to draw this shape.
	 */
	public void draw(float[] mvpMatrix) {
		// Add program to OpenGL environment
		GLES20.glUseProgram(mProgram);

		// get handle to vertex shader's vPosition member
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
		checkGlError("glGetAttribLocation");

		// Enable a handle to the triangle vertices
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		checkGlError("glEnableVertexAttribArray");

		// get handle to fragment shader's vColor member
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
		GLUtil.checkGlError("glGetUniformLocation");

		// Set color for drawing the triangle
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);
		GLUtil.checkGlError("glUniform4fv");

		// get handle to shape's transformation matrix
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		GLUtil.checkGlError("glGetUniformLocation");

		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
		GLUtil.checkGlError("glUniformMatrix4fv");

		vertexBuffer.position(0);
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE,
				vertexBuffer);

		drawListBuffer.position(0);
		GLES20.glDrawElements(drawMode, drawListBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}

	public void translateX(float f) {
		position[0] += f;
	}

	public void translateY(float f) {
		position[1] += f;

	}

	public float[] getRotation() {
		return rotation;
	}

	public void setRotationZ(float rz) {
		rotation[2] = rz;
	}

	public float getRotationZ() {
		return rotation[2];
	}

	public void setRotation(float[] rotation) {
		this.rotation = rotation;
	}

	@Override
	public void drawVectorNormals(float[] result, float[] modelViewMatrix) {
		// TODO Auto-generated method stub

	}

	public static void checkGlError(String glOperation) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e("objModel", glOperation + ": glError " + error);
			throw new RuntimeException(glOperation + ": glError " + error);
		}
	}
}