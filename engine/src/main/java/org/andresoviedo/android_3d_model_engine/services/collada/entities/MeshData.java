package org.andresoviedo.android_3d_model_engine.services.collada.entities;

import java.nio.FloatBuffer;

/**
 * This object contains all the mesh data for an animated model that is to be loaded into the VAO.
 * 
 * @author Karl
 *
 */
public class MeshData {

	private static final int DIMENSIONS = 3;

	private String id;
	private float[] vertices;
	private float[] textureCoords;
	private float[] normals;
	private float[] color;
	private FloatBuffer colorsBuffer;
	private String texture;
	private int[] indices;
	private int[] jointIds;
	private float[] vertexWeights;

	public MeshData(String id, float[] vertices, float[] textureCoords, float[] normals, float[] color, FloatBuffer colorsBuffer, String texture, int[] indices,
					int[] jointIds, float[] vertexWeights) {
		this.id = id;
		this.vertices = vertices;
		this.textureCoords = textureCoords;
		this.normals = normals;
		this.color = color;
		this.colorsBuffer = colorsBuffer;
		this.texture = texture;
		this.indices = indices;
		this.jointIds = jointIds;
		this.vertexWeights = vertexWeights;
	}

	public int[] getJointIds() {
		return jointIds;
	}
	
	public float[] getVertexWeights(){
		return vertexWeights;
	}

	public float[] getVertices() {
		return vertices;
	}

	public float[] getTextureCoords() {
		return textureCoords;
	}

	public float[] getNormals() {
		return normals;
	}

	public float[] getColor() {
		return color;
	}

	public FloatBuffer getColorsBuffer(){
		return colorsBuffer;
	}

	public int[] getIndices() {
		return indices;
	}

	public int getVertexCount() {
		return vertices.length / DIMENSIONS;
	}

	public String getId() {
		return id;
	}

	public String getTexture() {
		return texture;
	}
}
