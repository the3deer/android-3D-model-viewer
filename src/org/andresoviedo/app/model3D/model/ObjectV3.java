package org.andresoviedo.app.model3D.model;

import java.io.InputStream;
import java.nio.FloatBuffer;

import org.andresoviedo.app.model3D.entities.BoundingBox;
import org.andresoviedo.app.model3D.util.GLUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * A 3D Object in OpenGLES 2.0 using texture
 * 
 * @author andres
 *
 */
public class ObjectV3 implements Object3D {

	// number of coordinates per vertex in this array
	private static final int COORDS_PER_VERTEX = 3;
	private static final int VERTEX_STRIDE = COORDS_PER_VERTEX * 4; // 4 bytes per

	private static float[] DEFAULT_COLOR = { 0.0f, 1.0f, 0, 1.0f };

	// @formatter:off
	private final String vertexShaderCode =
		"uniform mat4 uMVPMatrix;" + 
		"attribute vec4 vPosition;" + 
		"attribute vec2 a_TexCoordinate;"+ // Per-vertex texture coordinate information we will pass in.
		"varying vec2 v_TexCoordinate;"+   // This will be passed into the fragment shader.
		"void main() {" +
			"  v_TexCoordinate = a_TexCoordinate;"+
			"  gl_Position = uMVPMatrix * vPosition;" + 
		"}";
	// @formatter:on

	// @formatter:off
	private final String fragmentShaderCode = 
		"precision mediump float;"+ 
		"uniform vec4 vColor;"+
		"uniform sampler2D u_Texture;"+ 
		"varying vec2 v_TexCoordinate;"+ 
		"void main() {"	+ 
		"  gl_FragColor = vColor * texture2D(u_Texture, v_TexCoordinate);"+
		"}";
	// @formatter:on

	// Model data
	protected final FloatBuffer vertexBuffer;
	protected final FloatBuffer textureCoordBuffer;
	protected final int drawMode;
	protected final int drawSize;
	protected float color[] = { 0.0f, 0.0f, 1.0f, 1.0f };

	// Transformation data
	protected float[] position = new float[] { 0f, 0f, 0f };
	protected float[] rotation = new float[] { 0f, 0f, 0f };

	// OpenGL data
	protected final int mProgram;
	protected int mMVPMatrixHandle;
	protected int mPositionHandle;
	protected int mColorHandle;
	private final Integer textureId;

	// Bounding box
	protected BoundingBox boundingBox;
	protected Object3D boundingBoxObject;

	/**
	 * Sets up the drawing object data for use in an OpenGL ES context.
	 * 
	 * @param vertices
	 * @param textureIs
	 *            TODO
	 */
	public ObjectV3(FloatBuffer vertices, FloatBuffer textureCoords, int drawMode, int drawSize,
			InputStream textureIs) {
		this.vertexBuffer = vertices;
		this.textureCoordBuffer = textureCoords;
		this.drawMode = drawMode;
		this.drawSize = drawSize;

		// prepare shaders and OpenGL program
		int vertexShader = GLUtil.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int fragmentShader = GLUtil.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

		mProgram = GLES20.glCreateProgram(); // create empty OpenGL Program
		GLES20.glAttachShader(mProgram, vertexShader); // add the vertex shader to program
		GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
		textureId = loadTexture(textureIs);
		GLES20.glLinkProgram(mProgram); // create OpenGL program executables
	}

	public static Integer loadTexture(final InputStream is) {
		if (is == null) {
			return null;
		}
		Log.v("loadTexture", "Loading texture '" + is + "' from stream...");

		final int[] textureHandle = new int[1];

		GLES20.glGenTextures(1, textureHandle, 0);
		GLUtil.checkGlError("glGenTextures");

		if (textureHandle[0] != 0) {
			Log.i("texture", "Handler: " + textureHandle[0]);

			final BitmapFactory.Options options = new BitmapFactory.Options();
			// By default, Android applies pre-scaling to bitmaps depending on the resolution of your device and which
			// resource folder you placed the image in. We don’t want Android to scale our bitmap at all, so to be sure,
			// we set inScaled to false.
			options.inScaled = false;

			// Read in the resource
			final Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
			if (bitmap == null) {
				throw new RuntimeException("couldnt load bitmap");
			}

			// Bind to the texture in OpenGL
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
			GLUtil.checkGlError("glBindTexture");
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
			GLUtil.checkGlError("texImage2D");
			bitmap.recycle();
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

		}

		if (textureHandle[0] == 0) {
			throw new RuntimeException("Error loading texture.");
		}

		return textureHandle[0];
	}

	public int getTextureId() {
		return textureId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andresoviedo.app.model3D.model.Object3D#getPosition()
	 */
	@Override
	public float[] getPosition() {
		return position;
	}

	public void setPosition(float[] position) {
		this.position = position;
	}

	public float[] getColor() {
		return color;
	}

	public Object3D setColor(float[] color) {
		this.color = color;
		return this;
	}

	@Override
	public void draw(float[] mvpMatrix, float[] mvMatrix) {
		this.draw(mvpMatrix, mvMatrix, drawMode, drawSize);
	}

	@Override
	public void draw(float[] mvpMatrix, float[] mvMatrix, int drawMode, int drawSize) {
		this.draw_with_textures(mvpMatrix, mvMatrix, drawMode, drawSize);
	}

	/**
	 * Encapsulates the OpenGL ES instructions for drawing this shape.
	 * 
	 * @param mvpMatrix
	 *            - The Model View Project matrix in which to draw this shape.
	 * @param mvMatrix
	 *            TODO
	 * @param drawMode
	 *            The method used for drawing the polygons. For example GLES20.GL_TRIANGLE_STRIP or GLES20.GL_LINE_STRIP
	 * @param drawSize
	 *            The number of vertices of the polygon to draw
	 */
	public void draw_with_textures(float[] mvpMatrix, float[] mvMatrix, int drawType, int drawSize) {
		// Add program to OpenGL environment
		GLES20.glUseProgram(mProgram);

		int mTextureCoordinateHandle = -1;
		if (textureId != null) {

			int mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
			checkGlError("glGetUniformLocation");

			// Set the active texture unit to texture unit 0.
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			checkGlError("glActiveTexture");

			// Bind to the texture in OpenGL
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
			checkGlError("glBindTexture");

			// Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
			GLES20.glUniform1i(mTextureUniformHandle, 0);
			checkGlError("glUniform1i");

			mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");
			GLUtil.checkGlError("glGetAttribLocation");

			// Enable a handle to the triangle vertices
			GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
			GLUtil.checkGlError("glEnableVertexAttribArray");

			// Prepare the triangle coordinate data
			textureCoordBuffer.position(0);
			GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, textureCoordBuffer);
			GLUtil.checkGlError("glVertexAttribPointer");
		}

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
		float[] color = getColor() != null ? getColor() : DEFAULT_COLOR;
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

		GLES20.glDrawArrays(drawMode, 0, vertexBuffer.capacity() / COORDS_PER_VERTEX);

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);

		// Disable vertex array
		if (textureId != null) {
			GLES20.glDisableVertexAttribArray(mTextureCoordinateHandle);
		}
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

	public static void checkGlError(String glOperation) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e("objModel", glOperation + ": glError " + error);
			throw new RuntimeException(glOperation + ": glError " + error);
		}
	}
}