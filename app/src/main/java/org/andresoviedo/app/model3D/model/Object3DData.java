package org.andresoviedo.app.model3D.model;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import org.andresoviedo.app.model3D.entities.*;
import org.andresoviedo.app.model3D.entities.BoundingBox;
import org.andresoviedo.app.model3D.services.WavefrontLoader.FaceMaterials;
import org.andresoviedo.app.model3D.services.WavefrontLoader.Faces;
import org.andresoviedo.app.model3D.services.WavefrontLoader.Materials;
import org.andresoviedo.app.model3D.services.WavefrontLoader.Tuple3;
import org.andresoviedo.app.util.math.Math3DUtils;

import android.opengl.GLES20;
import android.util.Log;

/**
 * This is the basic 3D data necessary to build the 3D object
 * 
 * @author andres
 *
 */
public class Object3DData {

	// opengl version to use to draw this object
	private int version = 5;
	/**
	 * The directory where the files reside so we can load referenced files in the model like material and textures
	 * files
	 */
	private File currentDir;
	/**
	 * The assets directory where the files reside so we can load referenced files in the model like material and
	 * textures files
	 */
	private String assetsDir;
	private String id;
	private boolean drawUsingArrays = true;
	private boolean flipTextCoords = true;

	// Model data for the simplest object

	private boolean isVisible = true;

	private float[] color;
	private int drawMode;
	private int drawSize;

	// Model data
	private FloatBuffer verts;
	private FloatBuffer normals;
	private ArrayList<Tuple3> texCoords;
	private Faces faces;
	private FaceMaterials faceMats;
	private Materials materials;

	// Processed data
	private FloatBuffer vertexBuffer = null;
	private FloatBuffer vertexNormalsBuffer = null;
	private IntBuffer drawOrderBuffer = null;

	// Processed arrays
	private FloatBuffer vertexArrayBuffer = null;
	private FloatBuffer vertexColorsArrayBuffer = null;
	private FloatBuffer vertexNormalsArrayBuffer = null;
	private FloatBuffer textureCoordsArrayBuffer = null;
	private List<int[]> drawModeList = null;
	private byte[] textureData = null;
	private List<InputStream> textureStreams = null;

	// derived data
	private BoundingBox boundingBox;

	// Transformation data
	protected float[] position = new float[] { 0f, 0f, 0f };
	protected float[] rotation = new float[] { 0f, 0f, 0f };

	// whether the object has changed
	private boolean changed;

	public Object3DData(FloatBuffer vertexArrayBuffer) {
		this.vertexArrayBuffer = vertexArrayBuffer;
		this.version = 1;
	}

	public Object3DData(FloatBuffer vertexBuffer, IntBuffer drawOrder) {
		this.vertexBuffer = vertexBuffer;
		this.drawOrderBuffer = drawOrder;
		this.version = 2;
	}

	public Object3DData(FloatBuffer vertexArrayBuffer, FloatBuffer textureCoordsArrayBuffer, byte[] texData) {
		this.vertexArrayBuffer = vertexArrayBuffer;
		this.textureCoordsArrayBuffer = textureCoordsArrayBuffer;
		this.textureData = texData;
		this.version = 3;
	}

	public Object3DData(FloatBuffer vertexArrayBuffer, FloatBuffer vertexColorsArrayBuffer,
			FloatBuffer textureCoordsArrayBuffer, byte[] texData) {
		this.vertexArrayBuffer = vertexArrayBuffer;
		this.vertexColorsArrayBuffer = vertexColorsArrayBuffer;
		this.textureCoordsArrayBuffer = textureCoordsArrayBuffer;
		this.textureData = texData;
		this.version = 4;
	}

	public Object3DData(FloatBuffer verts, FloatBuffer normals, ArrayList<Tuple3> texCoords, Faces faces,
			FaceMaterials faceMats, Materials materials) {
		super();
		this.verts = verts;
		this.normals = normals;
		this.texCoords = texCoords;
		this.faces = faces;
		this.faceMats = faceMats;
		this.materials = materials;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public int getVersion() {
		return version;
	}

	public Object3DData setVersion(int version) {
		this.version = version;
		return this;
	}

	public boolean isChanged() {
		return changed;
	}

	public Object3DData setId(String id) {
		this.id = id;
		return this;
	}

	public String getId() {
		return id;
	}

	public float[] getColor() {
		return color;
	}

	public float[] getColorInverted() {
		if (getColor() == null || getColor().length != 4) {
			return null;
		}
		return new float[] { 1 - getColor()[0], 1 - getColor()[1], 1 - getColor()[2], 1 };
	}

	public Object3DData setColor(float[] color) {
		this.color = color;
		return this;
	}

	public int getDrawMode() {
		return drawMode;
	}

	public Object3DData setDrawMode(int drawMode) {
		this.drawMode = drawMode;
		return this;
	}

	public int getDrawSize() {
		return drawSize;
	}

	public Object3DData setDrawSize(int drawSize) {
		this.drawSize = drawSize;
		return this;
	}

	// -----------

	public byte[] getTextureData() {
		return textureData;
	}

	public void setTextureData(byte[] textureData) {
		this.textureData = textureData;
	}

	public Object3DData setPosition(float[] position) {
		this.position = position;
		return this;
	}

	public float[] getPosition() {
		return position;
	}

	public float getPositionX() {
		return position != null ? position[0] : 0;
	}

	public float getPositionY() {
		return position != null ? position[1] : 0;
	}

	public float getPositionZ() {
		return position != null ? position[2] : 0;
	}

	public float[] getRotation() {
		return rotation;
	}

	public float getRotationZ() {
		return rotation != null ? rotation[2] : 0;
	}

	public void setRotation(float[] rotation) {
		this.rotation = rotation;
	}

	public IntBuffer getDrawOrder() {
		return drawOrderBuffer;
	}

	public Object3DData setDrawOrder(IntBuffer drawBuffer) {
		this.drawOrderBuffer = drawBuffer;
		return this;
	}

	public File getCurrentDir() {
		return currentDir;
	}

	public void setCurrentDir(File currentDir) {
		this.currentDir = currentDir;
	}

	public void setAssetsDir(String assetsDir) {
		this.assetsDir = assetsDir;
	}

	public String getAssetsDir() {
		return assetsDir;
	}

	public boolean isDrawUsingArrays() {
		return drawUsingArrays;
	}

	public boolean isFlipTextCoords() {
		return flipTextCoords;
	}

	public void setFlipTextCoords(boolean flipTextCoords) {
		this.flipTextCoords = flipTextCoords;
	}

	public void setDrawUsingArrays(boolean drawUsingArrays) {
		this.drawUsingArrays = drawUsingArrays;
	}

	public FloatBuffer getVerts() {
		return verts;
	}

	public FloatBuffer getNormals() {
		return normals;
	}

	public ArrayList<Tuple3> getTexCoords() {
		return texCoords;
	}

	public Faces getFaces() {
		return faces;
	}

	public FaceMaterials getFaceMats() {
		return faceMats;
	}

	public Materials getMaterials() {
		return materials;
	}

	// -------------------- Buffers ---------------------- //

	public FloatBuffer getVertexBuffer() {
		return vertexBuffer;
	}

	public Object3DData setVertexBuffer(FloatBuffer vertexBuffer) {
		this.vertexBuffer = vertexBuffer;
		return this;
	}

	public FloatBuffer getVertexNormalsBuffer() {
		return vertexNormalsBuffer;
	}

	public Object3DData setVertexNormalsBuffer(FloatBuffer vertexNormalsBuffer) {
		this.vertexNormalsBuffer = vertexNormalsBuffer;
		return this;
	}

	public FloatBuffer getVertexArrayBuffer() {
		return vertexArrayBuffer;
	}

	public void setVertexArrayBuffer(FloatBuffer vertexArrayBuffer) {
		this.vertexArrayBuffer = vertexArrayBuffer;
	}

	public FloatBuffer getVertexNormalsArrayBuffer() {
		return vertexNormalsArrayBuffer;
	}

	public Object3DData setVertexNormalsArrayBuffer(FloatBuffer vertexNormalsArrayBuffer) {
		this.vertexNormalsArrayBuffer = vertexNormalsArrayBuffer;
		return this;
	}

	public FloatBuffer getTextureCoordsArrayBuffer() {
		return textureCoordsArrayBuffer;
	}

	public void setTextureCoordsArrayBuffer(FloatBuffer textureCoordsArrayBuffer) {
		this.textureCoordsArrayBuffer = textureCoordsArrayBuffer;
	}

	public List<int[]> getDrawModeList() {
		return drawModeList;
	}

	public Object3DData setDrawModeList(List<int[]> drawModeList) {
		this.drawModeList = drawModeList;
		return this;
	}

	public FloatBuffer getVertexColorsArrayBuffer() {
		return vertexColorsArrayBuffer;
	}

	public Object3DData generateVertexColorsArrayBuffer() {
		FloatBuffer colorsArray = createNativeByteBuffer(4 * getVertexArrayBuffer().capacity() / 3 * 4).asFloatBuffer();
		for (int i = 0; i < getVertexArrayBuffer().capacity() / 3; i++) {
			colorsArray.put(1.0f).put(1.0f).put(1.0f).put(1.0f);
		}
		this.vertexColorsArrayBuffer = colorsArray;
		return this;
	}

	public Object3DData setVertexColorsArrayBuffer(FloatBuffer vertexColorsArrayBuffer) {
		this.vertexColorsArrayBuffer = vertexColorsArrayBuffer;
		return this;
	}

	public InputStream getTextureStream0() {
		if (textureData != null) {
			return new ByteArrayInputStream(textureData);
		}
		if (textureStreams == null) {
			return null;
		}
		return textureStreams.get(0);
	}

	public List<InputStream> getTextureStreams() {
		return textureStreams;
	}

	public void setTextureStreams(List<InputStream> textureStreams) {
		this.textureStreams = textureStreams;
	}

	public Object3DData centerAndScale(float maxSize) {
		float leftPt = Float.MAX_VALUE, rightPt = Float.MIN_VALUE; // on x-axis
		float topPt = Float.MIN_VALUE, bottomPt = Float.MAX_VALUE; // on y-axis
		float farPt = Float.MAX_VALUE, nearPt = Float.MIN_VALUE; // on z-axis

		FloatBuffer vertexBuffer = getVertexArrayBuffer() != null ? getVertexArrayBuffer() : getVertexBuffer();
		if (vertexBuffer == null) {
			Log.v("Object3DData", "Scaling for '" + getId() + "' I found that there is no vertex data");
			return this;
		}

		Log.i("Object3DData", "Calculating dimensions for '" + getId() + "...");
		for (int i = 0; i < vertexBuffer.capacity(); i += 3) {
			if (vertexBuffer.get(i) > rightPt)
				rightPt = vertexBuffer.get(i);
			else if (vertexBuffer.get(i) < leftPt)
				leftPt = vertexBuffer.get(i);
			if (vertexBuffer.get(i + 1) > topPt)
				topPt = vertexBuffer.get(i + 1);
			else if (vertexBuffer.get(i + 1) < bottomPt)
				bottomPt = vertexBuffer.get(i + 1);
			if (vertexBuffer.get(i + 2) > nearPt)
				nearPt = vertexBuffer.get(i + 2);
			else if (vertexBuffer.get(i + 2) < farPt)
				farPt = vertexBuffer.get(i + 2);
		} // end
		Log.i("Object3DData", "Dimensions for '" + getId() + " (X left, X right): ("+leftPt+","+rightPt+")");
		Log.i("Object3DData", "Dimensions for '" + getId() + " (Y top, Y bottom): ("+topPt+","+bottomPt+")");
		Log.i("Object3DData", "Dimensions for '" + getId() + " (Z near, Z far): ("+nearPt+","+farPt+")");

		// calculate center of 3D object
		float xc = (rightPt + leftPt) / 2.0f;
		float yc = (topPt + bottomPt) / 2.0f;
		float zc = (nearPt + farPt) / 2.0f;

		// calculate largest dimension
		float height = topPt - bottomPt;
		float depth = nearPt - farPt;
		float largest = rightPt - leftPt;
		if (height > largest)
			largest = height;
		if (depth > largest)
			largest = depth;
		Log.i("Object3DData", "Largest dimension ["+largest+"]");

		// scale object

		// calculate a scale factor
		float scaleFactor = 1.0f;
		// System.out.println("Largest dimension: " + largest);
		if (largest != 0.0f)
			scaleFactor = (maxSize / largest);
		Log.i("Object3DData",
				"Centering & scaling '" + getId() + "' to (" + xc + "," + yc + "," + zc + ") scale: '" + scaleFactor + "'");

		// modify the model's vertices
		for (int i = 0; i < vertexBuffer.capacity(); i += 3) {
			float x = vertexBuffer.get(i);
			float y = vertexBuffer.get(i + 1);
			float z = vertexBuffer.get(i + 2);
			x = (x - xc) * scaleFactor;
			y = (y - yc) * scaleFactor;
			z = (z - zc) * scaleFactor;
			vertexBuffer.put(i, x);
			vertexBuffer.put(i + 1, y);
			vertexBuffer.put(i + 2, z);
		}

		return this;
	}

	public Object3DData centerAndScaleAndExplode(float maxSize, float explodeFactor) {
		if (drawMode != GLES20.GL_TRIANGLES) {
			Log.i("Object3DData", "Cant explode '" + getId() + " because its not made of triangles...");
			return this;
		}

		float leftPt = Float.MAX_VALUE, rightPt = Float.MIN_VALUE; // on x-axis
		float topPt = Float.MIN_VALUE, bottomPt = Float.MAX_VALUE; // on y-axis
		float farPt = Float.MAX_VALUE, nearPt = Float.MIN_VALUE; // on z-axis

		FloatBuffer vertexBuffer = getVertexArrayBuffer() != null ? getVertexArrayBuffer() : getVertexBuffer();
		if (vertexBuffer == null) {
			Log.v("Object3DData", "Scaling for '" + getId() + "' I found that there is no vertex data");
			return this;
		}

		Log.i("Object3DData", "Calculating dimensions for '" + getId() + "...");
		for (int i = 0; i < vertexBuffer.capacity(); i += 3) {
			if (vertexBuffer.get(i) > rightPt)
				rightPt = vertexBuffer.get(i);
			else if (vertexBuffer.get(i) < leftPt)
				leftPt = vertexBuffer.get(i);
			if (vertexBuffer.get(i + 1) > topPt)
				topPt = vertexBuffer.get(i + 1);
			else if (vertexBuffer.get(i + 1) < bottomPt)
				bottomPt = vertexBuffer.get(i + 1);
			if (vertexBuffer.get(i + 2) > nearPt)
				nearPt = vertexBuffer.get(i + 2);
			else if (vertexBuffer.get(i + 2) < farPt)
				farPt = vertexBuffer.get(i + 2);
		} // end

		// calculate center of 3D object
		float xc = (rightPt + leftPt) / 2.0f;
		float yc = (topPt + bottomPt) / 2.0f;
		float zc = (nearPt + farPt) / 2.0f;

		// calculate largest dimension
		float height = topPt - bottomPt;
		float depth = nearPt - farPt;
		float largest = rightPt - leftPt;
		if (height > largest)
			largest = height;
		if (depth > largest)
			largest = depth;

		// scale object

		// calculate a scale factor
		float scaleFactor = 1.0f;
		// System.out.println("Largest dimension: " + largest);
		if (largest != 0.0f)
			scaleFactor = (maxSize / largest);
		Log.i("Object3DData",
				"Exploding '" + getId() + "' to '" + xc + "," + yc + "," + zc + "' '" + scaleFactor + "'");

		// modify the model's vertices
		FloatBuffer vertexBufferNew = createNativeByteBuffer(vertexBuffer.capacity() * 4).asFloatBuffer();
		for (int i = 0; i < vertexBuffer.capacity(); i += 3) {
			float x = vertexBuffer.get(i);
			float y = vertexBuffer.get(i + 1);
			float z = vertexBuffer.get(i + 2);
			x = (x - xc) * scaleFactor;
			y = (y - yc) * scaleFactor;
			z = (z - zc) * scaleFactor;
			vertexBuffer.put(i, x);
			vertexBuffer.put(i + 1, y);
			vertexBuffer.put(i + 2, z);
			vertexBufferNew.put(i, x * explodeFactor);
			vertexBufferNew.put(i + 1, y * explodeFactor);
			vertexBufferNew.put(i + 2, z * explodeFactor);
		}

		if (drawOrderBuffer != null) {
			Log.e("Object3DData", "Cant explode object composed of indexes '" + getId() + "'");
			return this;
		}

		for (int i = 0; i < vertexBuffer.capacity(); i += 9) {
			float x1 = vertexBuffer.get(i);
			float y1 = vertexBuffer.get(i + 1);
			float z1 = vertexBuffer.get(i + 2);
			float x2 = vertexBuffer.get(i + 3);
			float y2 = vertexBuffer.get(i + 4);
			float z2 = vertexBuffer.get(i + 5);
			float x3 = vertexBuffer.get(i + 6);
			float y3 = vertexBuffer.get(i + 7);
			float z3 = vertexBuffer.get(i + 8);
			float[] center1 = Math3DUtils.calculateFaceCenter(new float[] { x1, y1, z1 }, new float[] { x2, y2, z2 },
					new float[] { x3, y3, z3 });

			float xe1 = vertexBufferNew.get(i);
			float ye1 = vertexBufferNew.get(i + 1);
			float ze1 = vertexBufferNew.get(i + 2);
			float xe2 = vertexBufferNew.get(i + 3);
			float ye2 = vertexBufferNew.get(i + 4);
			float ze2 = vertexBufferNew.get(i + 5);
			float xe3 = vertexBufferNew.get(i + 6);
			float ye3 = vertexBufferNew.get(i + 7);
			float ze3 = vertexBufferNew.get(i + 8);
			float[] center2 = Math3DUtils.calculateFaceCenter(new float[] { xe1, ye1, ze1 },
					new float[] { xe2, ye2, ze2 }, new float[] { xe3, ye3, ze3 });

			vertexBuffer.put(i + 0, x1 + (center2[0] - center1[0]));
			vertexBuffer.put(i + 1, y1 + (center2[1] - center1[1]));
			vertexBuffer.put(i + 2, z1 + (center2[2] - center1[2]));
			vertexBuffer.put(i + 3, x2 + (center2[0] - center1[0]));
			vertexBuffer.put(i + 4, y2 + (center2[1] - center1[1]));
			vertexBuffer.put(i + 5, z2 + (center2[2] - center1[2]));
			vertexBuffer.put(i + 6, x3 + (center2[0] - center1[0]));
			vertexBuffer.put(i + 7, y3 + (center2[1] - center1[1]));
			vertexBuffer.put(i + 8, z3 + (center2[2] - center1[2]));
		}

		return this;
	}

	private static ByteBuffer createNativeByteBuffer(int length) {
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(length);
		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());
		return bb;
	}

	public BoundingBox getBoundingBox() {
		if (boundingBox == null && vertexBuffer != null) {
			float xMin = Float.MAX_VALUE, xMax = Float.MIN_VALUE, yMin = Float.MAX_VALUE, yMax = Float.MIN_VALUE, zMin = Float.MAX_VALUE, zMax = Float.MIN_VALUE;
			vertexBuffer.position(0);
			while (vertexBuffer.hasRemaining()) {
				float vertexx = vertexBuffer.get();
				float vertexy = vertexBuffer.get();
				float vertexz = vertexBuffer.get();
				if (vertexx < xMin) {
					xMin = vertexx;
				}
				if (vertexx > xMax) {
					xMax = vertexx;
				}
				if (vertexy < yMin) {
					yMin = vertexy;
				}
				if (vertexy > yMax) {
					yMax = vertexy;
				}
				if (vertexz < zMin) {
					zMin = vertexz;
				}
				if (vertexz > zMax) {
					zMax = vertexz;
				}
			}
			boundingBox = new BoundingBox(getId(), xMin+getPositionX(), xMax+getPositionX()
					, yMin+getPositionY(), yMax+getPositionY()
					, zMin+getPositionZ(), zMax+getPositionZ());
		}
		return boundingBox;
	}
}
