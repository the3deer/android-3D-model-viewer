package org.andresoviedo.app.model3D.model;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import org.andresoviedo.app.model3D.util.GLUtil;

import android.opengl.GLES20;

/**
 * A 3D object OpenGL ES 2.0 using indices {@link GLES20#glDrawElements(int, int, int, java.nio.Buffer)}
 */
public class ObjectV2 implements Object3D {

	@SuppressWarnings("unused")
	private float vertices[]; // top right
	private final short drawOrder[]; // order to draw vertices
	private final int drawMode;
	private float color[] = { 0, 1.0f, 0, 1.0f }; // default color is blue
	private float[] position = new float[] { 0f, 0f, 0f };
	private float[] rotation = new float[] { 0f, 0f, 0f };

	// @formatter:off
	private final String vertexShaderCode =
		// This matrix member variable provides a hook to manipulate
		// the coordinates of the objects that use this vertex shader
		"uniform mat4 uMVPMatrix;" + 
		"attribute vec4 vPosition;" + 
		"void main() {" +
			// The matrix must be included as a modifier of gl_Position.
			// Note that the uMVPMatrix factor *must be first* in order
			// for the matrix multiplication product to be correct.
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

	private final FloatBuffer vertexBuffer;
	private final ShortBuffer drawListBuffer;
	private final int mProgram;
	private int mPositionHandle;
	private int mColorHandle;
	private int mMVPMatrixHandle;

	// number of coordinates per vertex in this array
	static final int COORDS_PER_VERTEX = 3;

	private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

	/**
	 * Sets up the drawing object data for use in an OpenGL ES context.
	 */
	public ObjectV2(float[] vertices, short[] drawOrder, int drawMode) {
		this.vertices = vertices;
		this.drawOrder = drawOrder;
		this.drawMode = drawMode;

		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(
				// (number of coordinate values * 4 bytes per float)
				vertices.length * 4);
		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());

		// create a floating point buffer from the ByteBuffer
		vertexBuffer = bb.asFloatBuffer();
		// add the coordinates to the FloatBuffer
		vertexBuffer.put(vertices);
		// set the buffer to read the first coordinate
		vertexBuffer.position(0);

		// initialize byte buffer for the draw list
		ByteBuffer dlb = ByteBuffer.allocateDirect(
				// (# of coordinate values * 2 bytes per short)
				drawOrder.length * 2);
		dlb.order(ByteOrder.nativeOrder());
		drawListBuffer = dlb.asShortBuffer();
		drawListBuffer.put(drawOrder);
		drawListBuffer.position(0);

		// prepare shaders and OpenGL program
		int vertexShader = GLUtil.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int fragmentShader = GLUtil.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

		mProgram = GLES20.glCreateProgram(); // create empty OpenGL Program
		GLES20.glAttachShader(mProgram, vertexShader); // add the vertex shader
														// to program
		GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment
															// shader to program
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

		// Enable a handle to the triangle vertices
		GLES20.glEnableVertexAttribArray(mPositionHandle);

		// Prepare the triangle coordinate data
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, vertexStride,
				vertexBuffer);

		// get handle to fragment shader's vColor member
		mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

		// Set color for drawing the triangle
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);

		// get handle to shape's transformation matrix
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		GLUtil.checkGlError("glGetUniformLocation");

		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
		GLUtil.checkGlError("glUniformMatrix4fv");

		// Draw the model
		GLES20.glDrawElements(drawMode, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

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
}