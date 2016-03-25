package org.andresoviedo.app.model3D.model;

import java.io.File;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.andresoviedo.app.model3D.services.WavefrontLoader.FaceMaterials;
import org.andresoviedo.app.model3D.services.WavefrontLoader.Faces;
import org.andresoviedo.app.model3D.services.WavefrontLoader.Materials;
import org.andresoviedo.app.model3D.services.WavefrontLoader.Tuple3;

/**
 * This is the basic 3D data necessary to build the 3D object
 * 
 * @author andres
 *
 */
public class Object3DData {

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

	// Model data
	private final ArrayList<Tuple3> verts;
	private final ArrayList<Tuple3> normals;
	private final ArrayList<Tuple3> texCoords;
	private final Faces faces;
	private final FaceMaterials faceMats;
	private final Materials materials;

	// Processed data
	private FloatBuffer vertexBuffer = null;
	private FloatBuffer vertexNormalsBuffer = null;
	private FloatBuffer textureCoordsBuffer = null;
	private FloatBuffer vertexArrayBuffer = null;
	private FloatBuffer vertexNormalsArrayBuffer = null;
	private FloatBuffer textureCoordsArrayBuffer = null;
	private List<int[]> drawModeList = null;
	private FloatBuffer vertexColorsArrayBuffer = null;
	private List<InputStream> textureStreams = null;

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

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
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

	public void setDrawModeList(List<int[]> drawModeList) {
		this.drawModeList = drawModeList;
	}

	public FloatBuffer getVertexColorsArrayBuffer() {
		return vertexColorsArrayBuffer;
	}

	public void setVertexColorsArrayBuffer(FloatBuffer vertexColorsArrayBuffer) {
		this.vertexColorsArrayBuffer = vertexColorsArrayBuffer;
	}

	public List<InputStream> getTextureStreams() {
		return textureStreams;
	}

	public void setTextureStreams(List<InputStream> textureStreams) {
		this.textureStreams = textureStreams;
	}

}
