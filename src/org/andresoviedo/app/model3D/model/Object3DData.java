package org.andresoviedo.app.model3D.model;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import org.andresoviedo.app.model3D.services.WavefrontLoader.FaceMaterials;
import org.andresoviedo.app.model3D.services.WavefrontLoader.Faces;
import org.andresoviedo.app.model3D.services.WavefrontLoader.Materials;
import org.andresoviedo.app.model3D.services.WavefrontLoader.Tuple3;

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

	private float[] color;
	private int drawMode;
	private int drawSize;

	// Model data
	private ArrayList<Tuple3> verts;
	private ArrayList<Tuple3> normals;
	private ArrayList<Tuple3> texCoords;
	private Faces faces;
	private FaceMaterials faceMats;
	private Materials materials;

	// Processed data
	private FloatBuffer vertexBuffer = null;
	private FloatBuffer vertexNormalsBuffer = null;
	private FloatBuffer textureCoordsBuffer = null;
	private ShortBuffer drawOrderBuffer = null;

	// Processed arrays
	private FloatBuffer vertexArrayBuffer = null;
	private FloatBuffer vertexColorsArrayBuffer = null;
	private FloatBuffer vertexNormalsArrayBuffer = null;
	private FloatBuffer textureCoordsArrayBuffer = null;
	private List<int[]> drawModeList = null;
	private byte[] _texData = null;
	private List<InputStream> textureStreams = null;

	// Transformation data
	protected float[] position = new float[] { 0f, 0f, 0f };
	protected float[] rotation = new float[] { 0f, 0f, 0f };

	// whether the object has changed
	private boolean changed;

	public Object3DData(FloatBuffer vertexArrayBuffer) {
		this.vertexArrayBuffer = vertexArrayBuffer;
		this.version = 1;
	}

	public Object3DData(FloatBuffer vertexBuffer, ShortBuffer drawOrder) {
		this.vertexBuffer = vertexBuffer;
		this.drawOrderBuffer = drawOrder;
		this.version = 2;
	}

	public Object3DData(FloatBuffer vertexArrayBuffer, FloatBuffer textureCoordsArrayBuffer, byte[] texData) {
		this.vertexArrayBuffer = vertexArrayBuffer;
		this.textureCoordsArrayBuffer = textureCoordsArrayBuffer;
		this._texData = texData;
		this.version = 3;
	}

	public Object3DData(FloatBuffer vertexArrayBuffer, FloatBuffer vertexColorsArrayBuffer,
			FloatBuffer textureCoordsArrayBuffer, byte[] texData) {
		this.vertexArrayBuffer = vertexArrayBuffer;
		this.vertexColorsArrayBuffer = vertexColorsArrayBuffer;
		this.textureCoordsArrayBuffer = textureCoordsArrayBuffer;
		this._texData = texData;
		this.version = 4;
	}

	public Object3DData(ArrayList<Tuple3> verts, ArrayList<Tuple3> normals, ArrayList<Tuple3> texCoords, Faces faces,
			FaceMaterials faceMats, Materials materials) {
		super();
		this.verts = verts;
		this.normals = normals;
		this.texCoords = texCoords;
		this.faces = faces;
		this.faceMats = faceMats;
		this.materials = materials;
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

	public ShortBuffer getDrawOrder() {
		return drawOrderBuffer;
	}

	public Object3DData setDrawOrder(ShortBuffer drawBuffer) {
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

	public ArrayList<Tuple3> getVerts() {
		return verts;
	}

	public ArrayList<Tuple3> getNormals() {
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

	public void setVertexBuffer(FloatBuffer vertexBuffer) {
		this.vertexBuffer = vertexBuffer;
	}

	public FloatBuffer getVertexNormalsBuffer() {
		return vertexNormalsBuffer;
	}

	public void setVertexNormalsBuffer(FloatBuffer vertexNormalsBuffer) {
		this.vertexNormalsBuffer = vertexNormalsBuffer;
	}

	public FloatBuffer getTextureCoordsBuffer() {
		return textureCoordsBuffer;
	}

	public void setTextureCoordsBuffer(FloatBuffer textureCoordsBuffer) {
		this.textureCoordsBuffer = textureCoordsBuffer;
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

	public void setVertexNormalsArrayBuffer(FloatBuffer vertexNormalsArrayBuffer) {
		this.vertexNormalsArrayBuffer = vertexNormalsArrayBuffer;
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

	public Object3DData setVertexColorsArrayBuffer(FloatBuffer vertexColorsArrayBuffer) {
		this.vertexColorsArrayBuffer = vertexColorsArrayBuffer;
		return this;
	}

	public InputStream getTextureStream0() {
		if (_texData != null) {
			return new ByteArrayInputStream(_texData);
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
				"Centering & scaling '" + getId() + "' to '" + xc + "," + yc + "," + zc + "' '" + scaleFactor + "'");

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

}
