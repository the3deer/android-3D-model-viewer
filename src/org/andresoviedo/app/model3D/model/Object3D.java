package org.andresoviedo.app.model3D.model;

public interface Object3D {

	// number of coordinates per vertex in this array
	final int COORDS_PER_VERTEX = 3;
	final int VERTEX_STRIDE = COORDS_PER_VERTEX * 4; // 4 bytes per

	final float[] DEFAULT_COLOR = { 1.0f, 0.0f, 0, 1.0f };

	void draw(Object3DData obj, float[] pMatrix, float[] vMatrix, int textureId, float[] lightPosInEyeSpace);

	void draw(Object3DData obj, float[] pMatrix, float[] vMatrix, int drawType, int drawSize, int textureId, float[] lightPosInEyeSpace);
}