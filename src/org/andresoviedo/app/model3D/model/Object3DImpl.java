package org.andresoviedo.app.model3D.model;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.List;

import org.andresoviedo.app.model3D.util.GLUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * A 3D object in OpenGL ES 2.0 using {@link GLES20#glDrawArrays(int, int, int)}
 */
public class Object3DImpl implements Object3D {

	// number of coordinates per vertex in this array
	private static final int COORDS_PER_VERTEX = 3;
	private static final int VERTEX_STRIDE = COORDS_PER_VERTEX * 4; // 4 bytes per

	private static float[] DEFAULT_COLOR = { 1.0f, 0.0f, 0, 1.0f };

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

	// @formatter:off
	private final String vertexShaderCode_color =
		"uniform mat4 uMVPMatrix;" + 
		"attribute vec4 vPosition;" +
		"attribute vec4 a_Color;"+
		"varying vec4 vColor;"+
		"void main() {" +
			"  vColor = a_Color;"+
			"  gl_Position = uMVPMatrix * vPosition;" + 
		"}";
	// @formatter:on

	// @formatter:off
	private final String fragmentShaderCode_color = 
		"precision mediump float;"+ 
		"varying vec4 vColor;"+
		"void main() {"+ 
		"  gl_FragColor = vColor;" + 
		"}";
	// @formatter:on

	// @formatter:off
	private final String vertexShaderCode_textured =
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
	private final String fragmentShaderCode_textured = 
		"precision mediump float;"+ 
		"uniform vec4 vColor;"+
		"uniform sampler2D u_Texture;"+ 
		"varying vec2 v_TexCoordinate;"+ 
		"void main() {"	+ 
		"  gl_FragColor = vColor * texture2D(u_Texture, v_TexCoordinate);"+
		"}";
	// @formatter:on

	// @formatter:off
	protected final String vertexShaderCode_textured_color =
		"uniform mat4 uMVPMatrix;" + 
		"attribute vec4 vPosition;"+
		"attribute vec4 a_Color;"+
		"varying vec4 vColor;"+
		"attribute vec2 a_TexCoordinate;"+
		"varying vec2 v_TexCoordinate;"+
		"void main() {" +
			"  vColor = a_Color;"+
			"  v_TexCoordinate = a_TexCoordinate;"+
		    "  gl_Position = uMVPMatrix * vPosition;"+
		"}";
	// @formatter:on

	// @formatter:off
	protected final String fragmentShaderCode_textured_color = 
		"precision mediump float;"+
		"varying vec4 vColor;"+
		"uniform sampler2D u_Texture;"+
		"varying vec2 v_TexCoordinate;"+
		"void main() {"	+ 
		"  gl_FragColor = vColor * texture2D(u_Texture, v_TexCoordinate);"+
		"}";
	// @formatter:on

	// Model data
	private final FloatBuffer vertexBuffer;
	private ShortBuffer drawOrderBuffer;
	private final int drawMode;
	private int drawSize = -1; // by default draw all
	private float color[] = { 1.0f, 0.0f, 0.0f, 1.0f }; // default color is red
	private final FloatBuffer vertexColors;
	private FloatBuffer textureCoordsBuffer;
	private List<int[]> drawModeList;

	// Transformation data
	private float[] position = new float[] { 0f, 0f, 0f };
	private float[] rotation = new float[] { 0f, 0f, 0f };

	// OpenGL data
	private final int mProgram;
	private int mMVPMatrixHandle;
	private int mPositionHandle;
	private int mColorHandle;
	private int mTextureCoordinateHandle;
	private final Integer textureId;

	public Object3DImpl(FloatBuffer vertices, int drawMode) {
		this(vertices, null, drawMode, null, null);
	}

	public Object3DImpl(FloatBuffer vertices, FloatBuffer vertexColors, int drawMode, FloatBuffer textureCoordsBuffer,
			InputStream textureIs) {
		this.vertexBuffer = vertices;
		this.vertexColors = vertexColors;
		this.textureCoordsBuffer = textureCoordsBuffer;
		this.drawMode = drawMode;

		// prepare shaders and OpenGL program
		int vertexShader;
		int fragmentShader;
		if (textureCoordsBuffer != null && textureIs != null) {
			if (vertexColors != null) {
				vertexShader = GLUtil.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode_textured_color);
				fragmentShader = GLUtil.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode_textured_color);
			} else {
				vertexShader = GLUtil.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode_textured);
				fragmentShader = GLUtil.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode_textured);
			}
		} else {
			if (vertexColors != null) {
				vertexShader = GLUtil.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode_color);
				fragmentShader = GLUtil.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode_color);
			} else {
				vertexShader = GLUtil.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
				fragmentShader = GLUtil.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
			}
		}

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

	public Object3DImpl setDrawSize(int drawSize) {
		this.drawSize = drawSize;
		return this;
	}

	public Object3DImpl setDrawOrder(ShortBuffer drawOrder) {
		this.drawOrderBuffer = drawOrder;
		return this;
	}

	public List<int[]> getDrawModeList() {
		return drawModeList;
	}

	public Object3DImpl setDrawModeList(List<int[]> drawModeList) {
		this.drawModeList = drawModeList;
		return this;
	}

	public Object3DImpl setTextureCoordsData(FloatBuffer textureCoordsBuffer) {
		this.textureCoordsBuffer = textureCoordsBuffer;
		return this;
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

	public Object3D setColor(float[] color) {
		this.color = color;
		return this;
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

	@Override
	public void draw(float[] mvpMatrix, float[] mvMatrix) {
		this.draw(mvpMatrix);
	}

	@Override
	public void draw(float[] mvpMatrix, float[] mvMatrix, int drawType, int drawSize) {
		this.draw(mvpMatrix);

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

		mTextureCoordinateHandle = -1;
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
			textureCoordsBuffer.position(0);
			GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, textureCoordsBuffer);
			GLUtil.checkGlError("glVertexAttribPointer");
		}

		// get handle to vertex shader's vPosition member
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
		checkGlError("glGetAttribLocation");

		// Enable a handle to the triangle vertices
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		checkGlError("glEnableVertexAttribArray");

		if (vertexColors != null) {
			// get handle to fragment shader's vColor member
			mColorHandle = GLES20.glGetAttribLocation(mProgram, "a_Color");
			GLUtil.checkGlError("glGetAttribLocation");

			// Pass in the color information
			GLES20.glEnableVertexAttribArray(mColorHandle);
			checkGlError("glEnableVertexAttribArray");

			vertexColors.position(0);
			GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 0, vertexColors);
			GLUtil.checkGlError("glVertexAttribPointer");
		} else {
			// get handle to fragment shader's vColor member
			mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
			GLUtil.checkGlError("glGetUniformLocation");

			// Set color for drawing the triangle
			float[] color = getColor() != null ? getColor() : DEFAULT_COLOR;
			GLES20.glUniform4fv(mColorHandle, 1, color, 0);
			GLUtil.checkGlError("glUniform4fv");
		}

		// get handle to shape's transformation matrix
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		GLUtil.checkGlError("glGetUniformLocation");

		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
		GLUtil.checkGlError("glUniformMatrix4fv");

		vertexBuffer.position(0);
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, VERTEX_STRIDE,
				vertexBuffer);

		if (drawModeList != null) {
			if (drawOrderBuffer == null) {
				// Log.d("ObjectV5", "Drawing heterogeneous shape using arrays...");
				for (int[] polygon : drawModeList) {
					GLES20.glDrawArrays(polygon[0], polygon[1], polygon[2]);
				}
			} else {
				for (int[] drawPart : drawModeList) {
					int drawModePolygon = drawPart[0];
					int vertexPos = drawPart[1];
					int drawSizePolygon = drawPart[2];
					drawOrderBuffer.position(vertexPos);
					GLES20.glDrawElements(drawModePolygon, drawSizePolygon, GLES20.GL_UNSIGNED_SHORT, drawOrderBuffer);
				}
			}
		} else {
			if (drawOrderBuffer != null) {
				if (drawSize <= 0) {
					drawOrderBuffer.position(0);
					GLES20.glDrawElements(drawMode, drawOrderBuffer.capacity(), GLES20.GL_UNSIGNED_SHORT,
							drawOrderBuffer);
				} else {
					for (int i = 0; i < drawOrderBuffer.capacity(); i += drawSize) {
						drawOrderBuffer.position(i);
						GLES20.glDrawElements(drawMode, drawSize, GLES20.GL_UNSIGNED_SHORT, drawOrderBuffer);
					}
				}
			} else {
				if (drawSize <= 0) {
					GLES20.glDrawArrays(drawMode, 0, vertexBuffer.capacity() / COORDS_PER_VERTEX);
				} else {
					for (int i = 0; i < vertexBuffer.capacity() / COORDS_PER_VERTEX; i += drawSize) {
						GLES20.glDrawArrays(drawMode, i, drawSize);
					}
				}
			}
		}

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);

		if (vertexColors != null) {
			GLES20.glDisableVertexAttribArray(mColorHandle);
		}

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