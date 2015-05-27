package org.andresoviedo.app.model3D.entities;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.andresoviedo.app.model3D.util.GLUtil;

import android.opengl.GLES20;

/**
 * A 3D Axis for use as a drawn object in OpenGL ES 2.0.
 */
public class Axis {

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
	private final int mProgram;
	private int mPositionHandle;
	private int mColorHandle;
	private int mMVPMatrixHandle;

	// number of coordinates per vertex in this array
	static final int COORDS_PER_VERTEX = 3;
	// @formatter:off
	static float objCoords[] = { 
		0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, // right
		0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, // left
		0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, // up
		0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, // down
		0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, // z+
		0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, // z-
		
		 0.95f, 0.05f, 0,  1, 0, 0,        0.95f, -0.05f, 0,  1, 0f, 0f,// Arrow X (>)
		-0.95f, 0.05f, 0, -1, 0, 0,       -0.95f, -0.05f, 0, -1, 0f, 0f, // Arrow X (<)
	    -0.05f, 0.95f, 0,  0, 1, 0,        0.05f, 0.95f, 0 , 0, 1f, 0f, // Arrox Y (^)
		-0.05f, 0, 0.95f , 0, 0, 1 ,      0.05f, 0, 0.95f ,0, 0, 1,  // Arrox z (v)
		
		1.05F, 0.05F, 0 , 1.10F, -0.05F, 0 , 1.05F, -0.05F, 0, 1.10F, 0.05F, 0,  // Letter X
		-0.05F, 1.05F, 0 , 0.05F, 1.10F, 0 , -0.05F, 1.10F, 0 , 0.0F, 1.075F, 0,  // Letter Y
		-0.05F, 0.05F, 1.05F, 0.05F, 0.05F, 1.05F, 0.05F, 0.05F, 1.05F, -0.05F, -0.05F, 1.05F, -0.05F, -0.05F, 1.05F, 0.05F, -0.05F, 1.05F
		
		};
	// @formatter:on

	private final int vertexCount = objCoords.length / COORDS_PER_VERTEX;
	private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per
															// vertex

	float color[] = { 1.0f, 0.0f, 0.0f, 1.0f };

	float[] position = new float[] { 0f, 0f, 0f };
	float[] rotation = new float[] { 0f, 0f, 0f };

	/**
	 * Sets up the drawing object data for use in an OpenGL ES context.
	 */
	public Axis() {
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(
		// (number of coordinate values * 4 bytes per float)
				objCoords.length * 4);
		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());

		// create a floating point buffer from the ByteBuffer
		vertexBuffer = bb.asFloatBuffer();
		// add the coordinates to the FloatBuffer
		vertexBuffer.put(objCoords);
		// set the buffer to read the first coordinate
		vertexBuffer.position(0);

		// prepare shaders and OpenGL program
		int vertexShader = GLUtil.loadShader(GLES20.GL_VERTEX_SHADER,
				vertexShaderCode);
		int fragmentShader = GLUtil.loadShader(GLES20.GL_FRAGMENT_SHADER,
				fragmentShaderCode);

		mProgram = GLES20.glCreateProgram(); // create empty OpenGL Program
		GLES20.glAttachShader(mProgram, vertexShader); // add the vertex shader
														// to program
		GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment
															// shader to program
		GLES20.glLinkProgram(mProgram); // create OpenGL program executables

	}

	public float[] getColor() {
		return color;
	}

	public void setColor(float[] color) {
		this.color = color;
	}

	public void SetColor(float red, float green, float blue, float alpha) {
		color[0] = red;
		color[1] = green;
		color[2] = blue;
		color[3] = alpha;
	}

	public void SetVerts(float v0, float v1, float v2, float v3, float v4,
			float v5) {
		objCoords[0] = v0;
		objCoords[1] = v1;
		objCoords[2] = v2;
		objCoords[3] = v3;
		objCoords[4] = v4;
		objCoords[5] = v5;

		vertexBuffer.put(objCoords);
		// set the buffer to read the first coordinate
		vertexBuffer.position(0);
	}

	public void draw(float[] mvpMatrix) {
		// Add program to OpenGL environment
		GLES20.glUseProgram(mProgram);

		// get handle to vertex shader's vPosition member
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

		// Enable a handle to the triangle vertices
		GLES20.glEnableVertexAttribArray(mPositionHandle);

		// Prepare the triangle coordinate data
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
				GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);

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

		// Draw the triangle
		GLES20.glDrawArrays(GLES20.GL_LINES, 0, vertexCount);

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);
	}
}