package org.andresoviedo.app.model3D.model;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.List;

import org.andresoviedo.app.model3D.entities.BoundingBox;
import org.andresoviedo.app.model3D.util.GLUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * A 3D Object in OpenGLES 2.0 using lights?
 * 
 * @author andres
 *
 */
public class ObjectV6 implements Object3D {

	// @formatter:off
	protected static final String vertexShaderCode_textured =
			// This matrix member variable provides a hook to manipulate
			// the coordinates of the objects that use this vertex shader
			"uniform mat4 u_MVPMatrix;" + 
			"uniform mat4 u_MVMatrix;"+       // A constant representing the combined model/view matrix.
			
			"attribute vec4 a_Position;"+    // Per-vertex position information we will pass in.
			"attribute vec3 a_Normal;"+      // Per-vertex normal information we will pass in.
			"attribute vec4 a_Color;"+       // Per-vertex color information we will pass in.
			
			"varying vec3 v_Position;"+       // This will be passed into the fragment shader.
			"varying vec4 v_Color;"+         // This will be passed into the fragment shader.
			"varying vec3 v_Normal;"+         // This will be passed into the fragment shader.
			
			"attribute vec2 a_TexCoordinate;"+ // Per-vertex texture coordinate information we will pass in.
			"varying vec2 v_TexCoordinate;"+   // This will be passed into the fragment shader.
			
			"void main() {" +
			    // Transform the vertex into eye space.
				"  v_Position = vec3(u_MVMatrix * a_Position);" + 
			
				// Pass through the color.
				"  v_Color = a_Color;"+
				
				// Transform the normal's orientation into eye space.
				"  v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));"+
				
	//									"  v_useTextures = a_useTextures;"+
				"  v_TexCoordinate = a_TexCoordinate;"+
	//									"  v_TexCoordinate = a_TexCoordinate.st * vec2(1.0, -1.0);"+
				
				// gl_Position is a special variable used to store the final position.
			    // Multiply the vertex by the matrix to get the final point in normalized screen coordinates.
			    "  gl_Position = u_MVPMatrix * a_Position;"+
				
			"}";
	// @formatter:on

	// @formatter:off
	protected static final String fragmentShaderCode_textured = 
			"precision mediump float;"+
	
			//The position of the light in eye space.
			"uniform vec3 u_LightPos;"+
			
			// Interpolated position for this fragment.
			"varying vec3 v_Position;"+
			
  			// This is the color from the vertex shader interpolated across the
			"varying vec4 v_Color;"+
            
			// triangle per fragment.
			// Interpolated normal for this fragment.
			"varying vec3 v_Normal;"+         
			
			"uniform sampler2D u_Texture;"+    // The input texture.
			"varying vec2 v_TexCoordinate;"+ // Interpolated texture coordinate per fragment.
			
			"void main() {"	+ 
			// Will be used for attenuation.
		    "  float distance = length(u_LightPos - v_Position);"+
			  
			// Get a lighting direction vector from the light to the vertex.
			"  vec3 lightVector = normalize(u_LightPos - v_Position);"+
			 
			// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
			// pointing in the same direction then it will get max illumination.
			"  float diffuse = max(dot(v_Normal, lightVector), 0.1);"+
			
			//  Add attenuation.
//					"  diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance)));"+
			"  diffuse = diffuse * (1.0 / (1.0 + (0.10 * distance)));"+
//                    "  diffuse = 1.0;"+
			
			//  Add ambient lighting
			"  diffuse = diffuse + 0.3;"+
						
			//  Multiply the color by the diffuse illumination level to get final output color.
//					"  gl_FragColor = v_Color * diffuse;"+
//					"  gl_FragColor = (v_Color * diffuse * texture2D(u_Texture, v_TexCoordinate));"+
			"  gl_FragColor = v_Color * diffuse * texture2D(u_Texture, v_TexCoordinate);"+
			"}";
	// @formatter:on

	// @formatter:off
		protected static final String fragmentShaderCode_lighted = 
				"precision mediump float;"+
		
				//The position of the light in eye space.
				"uniform vec3 u_LightPos;"+
				
				// Interpolated position for this fragment.
				"varying vec3 v_Position;"+
				
	  			// This is the color from the vertex shader interpolated across the
				"varying vec4 v_Color;"+
	            
				// triangle per fragment.
				// Interpolated normal for this fragment.
				"varying vec3 v_Normal;"+         
				
				"void main() {"	+ 
				// Will be used for attenuation.
			    "  float distance = length(u_LightPos - v_Position);"+
				  
				// Get a lighting direction vector from the light to the vertex.
				"  vec3 lightVector = normalize(u_LightPos - v_Position);"+
				 
				// Calculate the dot product of the light vector and vertex normal. If the normal and light vector are
				// pointing in the same direction then it will get max illumination.
				"  float diffuse = max(dot(v_Normal, lightVector), 0.1);"+
				
				//  Add attenuation.
//							"  diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance)));"+
				"  diffuse = diffuse * (1.0 / (1.0 + (0.10 * distance)));"+
//		                    "  diffuse = 1.0;"+
				
				//  Add ambient lighting
				"  diffuse = diffuse + 0.3;"+
							
				//  Multiply the color by the diffuse illumination level to get final output color.
				"  gl_FragColor = v_Color * diffuse;"+
				"}";
		// @formatter:on

	// number of coordinates per vertex in this array
	protected static final int COORDS_PER_VERTEX = 3;

	protected final int mProgram;
	protected final FloatBuffer vertexBuffer;
	/**
	 * This list will contain the vector index and the count of how many
	 */
	protected final List<int[]> drawModeList;
	protected final ShortBuffer drawListBuffer;
	protected final FloatBuffer normalsBuffer;
	protected final FloatBuffer textureCoordBuffer;
	protected final FloatBuffer vertexColors;

	protected float lightPos[] = { -1.0f, 1, 0f, 0.0f };
	protected float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };
	protected float[] position = new float[] { 0f, 0f, 0f };
	protected float[] rotation = new float[] { 0f, 0f, 0f };

	protected int mMVPMatrixHandle;
	protected int mMVMatrixHandle;
	protected int mPositionHandle;
	protected int mColorHandle;
	protected int normalHandle;
	protected int lightPositionHandle;

	// Lazy objects
	protected final int drawMode;
	protected final int drawSize;

	// Bounding box
	protected BoundingBox boundingBox;
	protected Object3D boundingBoxObject;
	protected Object3D faceNormalsObject;

	private final int vertexShaderHandle;
	private final int fragmentShaderHandle;

	private final int[] textureId;

	private static ByteBuffer createNativeByteBuffer(int length) {
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(
				// (number of coordinate values * n bytes per type)
				length);
		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());
		return bb;
	}

	public ObjectV6(float[] objCoords, List<int[]> drawSizeList, float[] vertexColors, short[] drawOrder,
			float[] vNormals, float[] textCoord, int drawType, int drawSize, List<InputStream> open) {
		this(createNativeByteBuffer(4 * objCoords.length).asFloatBuffer().put(objCoords).asReadOnlyBuffer(),
				drawSizeList,
				vertexColors == null ? null
						: createNativeByteBuffer(4 * vertexColors.length).asFloatBuffer().put(vertexColors)
								.asReadOnlyBuffer(),
				drawOrder == null ? null
						: createNativeByteBuffer(2 * drawOrder.length).asShortBuffer().put(drawOrder)
								.asReadOnlyBuffer(),
				createNativeByteBuffer(4 * vNormals.length).asFloatBuffer().put(vNormals).asReadOnlyBuffer(),
				textCoord == null ? null
						: createNativeByteBuffer(4 * textCoord.length).asFloatBuffer().put(textCoord)
								.asReadOnlyBuffer(),
				drawType, drawSize, open);
	}

	/**
	 * Sets up the drawing object data for use in an OpenGL ES context.
	 * 
	 * @param vertexColors
	 *            TODO
	 * @param textureIs
	 *            TODO
	 * @param vertices
	 */
	public ObjectV6(FloatBuffer objCoords, List<int[]> drawModeList, FloatBuffer vertexColors, ShortBuffer drawOrder,
			FloatBuffer normalsBuffer, FloatBuffer textureCoords, int drawMode, int drawSize,
			List<InputStream> textureIs) {
		// { 0, 1, 2, 0, 2, 3, 3, 4, 5, 5, 4, 0 }

		this.vertexBuffer = objCoords;
		this.vertexBuffer.position(0);

		this.drawModeList = drawModeList;

		this.drawListBuffer = drawOrder;
		if (drawListBuffer != null) {
			this.drawListBuffer.position(0);
		}

		this.normalsBuffer = normalsBuffer;
		this.normalsBuffer.position(0);

		this.textureCoordBuffer = textureCoords;
		if (this.textureCoordBuffer != null) {
			this.textureCoordBuffer.position(0);
		}

		this.vertexColors = vertexColors;
		vertexColors.position(0);

		this.drawMode = drawMode;
		this.drawSize = drawSize;

		// prepare shaders and OpenGL program
		vertexShaderHandle = GLUtil.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode_textured);

		Log.i("fa", "Using textures [" + textureIs + "]");
		fragmentShaderHandle = GLUtil.loadShader(GLES20.GL_FRAGMENT_SHADER,
				textureIs != null ? fragmentShaderCode_textured : fragmentShaderCode_lighted);

		mProgram = GLES20.glCreateProgram(); // create empty OpenGL Program

		GLES20.glAttachShader(mProgram, vertexShaderHandle); // add the vertex shader

		// GLES20.glBindAttribLocation(mProgram, 0, "a_Position");
		// GLES20.glBindAttribLocation(mProgram, 1, "u_MVPMatrix");

		// to program
		GLES20.glAttachShader(mProgram, fragmentShaderHandle); // add the fragment
																// shader to program

		if (textureIs != null) {
			Log.i("globject", "Binding texture...");

			// GLES20.glBindAttribLocation(mProgram, 0, "a_Color");
			// GLES20.glBindAttribLocation(mProgram, 1, "u_Texture");
			// GLES20.glBindAttribLocation(mProgram, 2, "a_TexCoordinate");
			textureId = loadTexture(textureIs);

		} else {
			textureId = null;
		}

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

		// debug();
	}

	public static int[] loadTexture(final List<InputStream> is) {
		Log.v("loadTexture", "Loading texture '" + is + "' from stream...");

		final int[] textureHandle = new int[is.size()];

		GLES20.glGenTextures(textureHandle.length, textureHandle, 0);
		GLUtil.checkGlError("glGenTextures");

		for (int i = 0; i < textureHandle.length; i++) {
			if (textureHandle[i] != 0) {
				Log.i("texture", "Handler: " + textureHandle[0]);

				final BitmapFactory.Options options = new BitmapFactory.Options();
				// By default, Android applies pre-scaling to bitmaps depending on the resolution of your device and
				// which
				// resource folder you placed the image in. We don’t want Android to scale our bitmap at all, so to be
				// sure,
				// we set inScaled to false.
				options.inScaled = false;

				// Read in the resource
				final Bitmap bitmap = BitmapFactory.decodeStream(is.get(i), null, options);
				if (bitmap == null) {
					throw new RuntimeException("couldnt load bitmap");
				}

				// Bind to the texture in OpenGL
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
				GLUtil.checkGlError("glBindTexture");

				// Load the bitmap into the bound texture.
				GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
				GLUtil.checkGlError("texImage2D");

				// Recycle the bitmap, since its data has been loaded into OpenGL.
				bitmap.recycle();

				// Set filtering
				// This tells OpenGL what type of filtering to apply when drawing the texture smaller than the original
				// size
				// in pixels.
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
				// This tells OpenGL what type of filtering to apply when magnifying the texture beyond its original
				// size in
				// pixels.
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

			}

			if (textureHandle[i] == 0) {
				throw new RuntimeException("Error loading texture.");
			}
		}

		return textureHandle;
	}

	public static void checkGlError(String glOperation) {
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
			Log.e("objModel", glOperation + ": glError " + error);
			throw new RuntimeException(glOperation + ": glError " + error);
		}
	}

	public int[] getTextureId() {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andresoviedo.app.model3D.model.Object3D#setPosition(float[])
	 */
	@Override
	public void setPosition(float[] position) {
		this.position = position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andresoviedo.app.model3D.model.Object3D#getColor()
	 */
	@Override
	public float[] getColor() {
		return color;
	}

	public Object3D setColor(float[] color) {
		this.color = color;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andresoviedo.app.model3D.model.Object3D#draw(float[], float[])
	 */
	@Override
	public void draw(float[] mvpMatrix, float[] mvMatrix) {
		this.draw(mvpMatrix, mvMatrix, drawMode, drawSize);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andresoviedo.app.model3D.model.Object3D#draw(float[], float[], int, int)
	 */
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
	public void draw_with_textures(float[] mvpMatrix, float[] mvMatrix, int drawMode, int drawSize) {
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
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0]);
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
			GLES20.glVertexAttribPointer(mTextureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, textureCoordBuffer);
			GLUtil.checkGlError("glVertexAttribPointer");
		}

		// get handle to vertex shader's vPosition member
		mPositionHandle = GLES20.glGetAttribLocation(mProgram, "a_Position");
		checkGlError("glGetAttribLocation");

		// Enable a handle to the triangle vertices
		GLES20.glEnableVertexAttribArray(mPositionHandle);
		checkGlError("glEnableVertexAttribArray");

		// Prepare the triangle coordinate data
		vertexBuffer.position(0);
		GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, vertexBuffer);

		// get handle to fragment shader's vColor member
		mColorHandle = GLES20.glGetAttribLocation(mProgram, "a_Color");
		GLUtil.checkGlError("glGetAttribLocation");

		// Pass in the color information
		GLES20.glEnableVertexAttribArray(mColorHandle);
		checkGlError("glEnableVertexAttribArray");
		GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 0, vertexColors);
		GLUtil.checkGlError("glVertexAttribPointer");

		// get handle to shape's transformation matrix
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVPMatrix");
		GLUtil.checkGlError("glGetUniformLocation");

		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
		GLUtil.checkGlError("glUniformMatrix4fv");

		// -- testing start

		lightPositionHandle = GLES20.glGetUniformLocation(mProgram, "u_LightPos");
		GLUtil.checkGlError("glGetUniformLocation");
		GLES20.glUniform3fv(lightPositionHandle, 1, lightPos, 0);

		// Enable a handle to the triangle vertices
		normalHandle = GLES20.glGetAttribLocation(mProgram, "a_Normal");
		GLUtil.checkGlError("glGetAttribLocation");
		GLES20.glEnableVertexAttribArray(normalHandle);
		normalsBuffer.position(0);
		GLES20.glVertexAttribPointer(normalHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, normalsBuffer);

		// get handle to shape's transformation matrix
		mMVMatrixHandle = GLES20.glGetUniformLocation(mProgram, "u_MVMatrix");
		GLUtil.checkGlError("glGetUniformLocation");
		GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mvMatrix, 0);
		GLUtil.checkGlError("glUniformMatrix4fv");

		//
		if (drawListBuffer == null) {
			if (drawModeList != null) {
				// Log.d("ObjectV5", "Drawing heterogeneous shape using arrays...");
				for (int[] polygon : drawModeList) {
					GLES20.glDrawArrays(polygon[0], polygon[1], polygon[2]);
				}
			} else {
				// assume we are drawing normal triangles
				// Log.d("ObjectV5", "Drawing homogeneous shape using arrays...");
				GLES20.glDrawArrays(drawMode, 0, vertexBuffer.capacity() / COORDS_PER_VERTEX);
			}
		} else {
			int capacity = drawListBuffer.capacity();
			if (drawModeList != null) {
				// Log.d("ObjectV5", "Drawing heterogeneous polygon...");
				for (int[] drawPart : drawModeList) {
					int drawModePolygon = drawPart[0];
					int vertexPos = drawPart[1];
					int drawSizePolygon = drawPart[2];
					drawListBuffer.position(vertexPos);
					GLES20.glDrawElements(drawModePolygon, drawSizePolygon, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
				}
			} else {
				// Log.d("ObjectV5", "Drawing homogeneous polygon...");
				if (drawSize != -1 && capacity % drawSize != 0) {
					throw new RuntimeException(capacity + "<>" + drawSize);
				}
				for (int i = 0; i < capacity - drawSize; i += drawSize) {
					drawListBuffer.position(i);
					GLES20.glDrawElements(drawMode, drawSize, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
				}
			}
		}

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mPositionHandle);

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(normalHandle);

		if (vertexColors != null) {
			GLES20.glDisableVertexAttribArray(mColorHandle);
		}

		if (textureId != null) {
			GLES20.glDisableVertexAttribArray(mTextureCoordinateHandle);
			// GLES20.glDisable(GLES20.GL_TEXTURE0);
			// GLES20.glDisable(GLES20.GL_TEXTURE_2D);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andresoviedo.app.model3D.model.Object3D#translateX(float)
	 */
	@Override
	public void translateX(float f) {
		position[0] += f;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andresoviedo.app.model3D.model.Object3D#translateY(float)
	 */
	@Override
	public void translateY(float f) {
		position[1] += f;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andresoviedo.app.model3D.model.Object3D#getRotation()
	 */
	@Override
	public float[] getRotation() {
		return rotation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andresoviedo.app.model3D.model.Object3D#setRotationZ(float)
	 */
	@Override
	public void setRotationZ(float rz) {
		rotation[2] = rz;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andresoviedo.app.model3D.model.Object3D#getRotationZ()
	 */
	@Override
	public float getRotationZ() {
		return rotation[2];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.andresoviedo.app.model3D.model.Object3D#setRotation(float[])
	 */
	@Override
	public void setRotation(float[] rotation) {
		this.rotation = rotation;
	}

}