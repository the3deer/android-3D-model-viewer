package org.andresoviedo.app.model3D.model;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.andresoviedo.app.model3D.entities.BoundingBox;
import org.andresoviedo.app.model3D.util.GLUtil;
import org.andresoviedo.app.util.math.Math3DUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * A 3D Object in OpenGLES 2.0 using colors
 * 
 * @author andres
 *
 */
public class ObjectV4 implements Object3D {

	// number of coordinates per vertex in this array
	private static final int COORDS_PER_VERTEX = 3;
	private static final int VERTEX_STRIDE = COORDS_PER_VERTEX * 4; // 4 bytes per

	// @formatter:off
	protected final String vertexShaderCode =
			"uniform mat4 uMVPMatrix;" + 
			"attribute vec4 vPosition;"+
			"attribute vec4 a_Color;"+
			"varying vec4 vColor;"+
			"attribute vec2 a_TexCoordinate;"+ // Per-vertex texture coordinate information we will pass in.
			"varying vec2 v_TexCoordinate;"+   // This will be passed into the fragment shader.
			"void main() {" +
				"  vColor = a_Color;"+
				"  v_TexCoordinate = a_TexCoordinate;"+
			    "  gl_Position = uMVPMatrix * vPosition;"+
			"}";
	// @formatter:on

	// @formatter:off
	protected final String fragmentShaderCode = 
			"precision mediump float;"+
			"varying vec4 vColor;"+
			"uniform sampler2D u_Texture;"+
			"varying vec2 v_TexCoordinate;"+
			"void main() {"	+ 
			"  gl_FragColor = vColor * texture2D(u_Texture, v_TexCoordinate);"+
			"}";
	// @formatter:on

	// Model data
	protected final FloatBuffer vertexBuffer;
	protected final FloatBuffer vertexColors;
	protected final FloatBuffer textureCoordBuffer;
	protected final int drawMode;
	protected final int drawSize;
	protected float color[] = { 1.0f, 1.0f, 0.0f, 1.0f };

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
	protected Object3D faceNormalsObject;

	/**
	 * Sets up the drawing object data for use in an OpenGL ES context.
	 * 
	 * @param vertexColors
	 *            TODO
	 * @param textureIs
	 *            TODO
	 * @param vertices
	 */
	public ObjectV4(FloatBuffer vertices, FloatBuffer vertexColors, FloatBuffer textureCoords, int drawMode,
			int drawSize, InputStream textureIs) {
		this.vertexBuffer = vertices;
		this.textureCoordBuffer = textureCoords;
		this.vertexColors = vertexColors;
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

		// Get the link status.
		final int[] linkStatus = new int[1];
		GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, linkStatus, 0);

		// If the link failed, delete the program.
		if (linkStatus[0] == 0) {
			Log.e("ObjectV4", "Error compiling program: " + GLES20.glGetProgramInfoLog(mProgram));
			GLES20.glDeleteProgram(mProgram);
			// mProgram = 0;
		}
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

	public void setColor(float[] color) {
		this.color = color;
	}

	@Override
	public void draw(float[] mvpMatrix, float[] mvMatrix) {
		try {
			this.draw(mvpMatrix, mvMatrix, drawMode, drawSize);
		} catch (Exception ex) {
			Log.e("Object41", ex.getMessage(), ex);
		}
	}

	@Override
	public void draw(float[] mvpMatrix, float[] mvMatrix, int drawMode, int drawSize) {
		this.draw_with_textures(mvpMatrix, mvMatrix, drawMode, drawSize);
	}

	@Override
	public void drawBoundingBox(float[] mvpMatrix, float[] mvMatrix) {
		if (boundingBox == null) {
			// init bounding box
			boundingBox = new BoundingBox(vertexBuffer.asReadOnlyBuffer());
			boundingBoxObject = new ObjectV5(boundingBox.getVertices(), boundingBox.getDrawModeList(),
					boundingBox.getColors(), boundingBox.getDrawOrder(), null, boundingBox.getDrawMode(),
					boundingBox.getDrawSize(), null);
			boundingBoxObject.setPosition(getPosition());
			boundingBoxObject.setColor(getColor());
		}
		boundingBoxObject.draw(mvpMatrix, mvMatrix);

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
		mColorHandle = GLES20.glGetAttribLocation(mProgram, "a_Color");
		GLUtil.checkGlError("glGetAttribLocation");

		// Pass in the color information
		GLES20.glEnableVertexAttribArray(mColorHandle);
		checkGlError("glEnableVertexAttribArray");

		vertexColors.position(0);
		GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 0, vertexColors);
		GLUtil.checkGlError("glVertexAttribPointer");

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
		GLES20.glDisableVertexAttribArray(mColorHandle);

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

	@Override
	public void drawVectorNormals(float[] mvpMatrix, float[] mvMatrix) {
		// TODO: This only works for triangles. Make it useful for any kind of polygon
		if (drawSize != 3) {
			return;
		}
		// If the object has already been generated, just draw it
		if (faceNormalsObject != null) {
			faceNormalsObject.draw(mvpMatrix, mvMatrix);
			return;
		}
		Log.d("ObjectV4", "Generating face normals...");
		// Generate a new object that contains the all the line normals for this object
		FloatBuffer normalsLines = createNativeByteBuffer(2 * vertexBuffer.capacity() * 4).asFloatBuffer();
		// Normals for the lines
		FloatBuffer normalsNormals = createNativeByteBuffer(2 * vertexBuffer.capacity() * 4).asFloatBuffer();
		normalsLines.position(0);
		normalsNormals.position(0);
		vertexBuffer.position(0);
		for (int i = 0; i < vertexBuffer.capacity() / COORDS_PER_VERTEX / drawSize; i++) {
			float[][] normalLine = Math3DUtils.calculateFaceNormal(
					new float[] { vertexBuffer.get(), vertexBuffer.get(), vertexBuffer.get() },
					new float[] { vertexBuffer.get(), vertexBuffer.get(), vertexBuffer.get() },
					new float[] { vertexBuffer.get(), vertexBuffer.get(), vertexBuffer.get() });
			normalsLines.put(normalLine[0]).put(normalLine[1]);
			normalsNormals.put(new float[] { 0, 1.0f, 0 });
			normalsNormals.put(new float[] { 0, 1.0f, 0 });

			// debug
			String normal = new StringBuilder().append(normalLine[0][0]).append(",").append(normalLine[0][1])
					.append(",").append(normalLine[0][2]).append("-").append(normalLine[1][0]).append(",")
					.append(normalLine[1][1]).append(",").append(normalLine[1][2]).toString();
			Log.d("ObjectV4", "fNormal[" + i + "]:(" + normal + ")");
		}

		faceNormalsObject = new ObjectV1(normalsLines, GLES20.GL_LINES);
		faceNormalsObject.setPosition(getPosition());
		faceNormalsObject.setColor(getColor() != null ? getColor() : new float[] { 1.0f, 0, 0, 1.0f });
		faceNormalsObject.draw(mvpMatrix, mvMatrix);
	}

	public static void checkGlError(String glOperation) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e("objModel", glOperation + ": glError " + error);
			throw new RuntimeException(glOperation + ": glError " + error);
		}
	}

	private static ByteBuffer createNativeByteBuffer(int length) {
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(
				// (number of coordinate values * 2 bytes per short)
				length);
		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());
		return bb;
	}
}