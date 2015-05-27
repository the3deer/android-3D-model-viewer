package org.andresoviedo.app.model3D.models;

import java.nio.FloatBuffer;

public class Model3D {
	private boolean hasNormalCoords;
	private boolean hasTextureCoords;
	private int hasTextureW;
	private FloatBuffer normalBuffer;
	private int numFaces;
	private FloatBuffer textureBuffer;
	private int[] textures = new int[3];
	private FloatBuffer vertexBuffer;
	private boolean wireframe = false;

	public Model3D(float[] vertices, float[] texture, float[] normals, int hasTextureW) {

	}
}
