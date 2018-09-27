package org.andresoviedo.android_3d_model_engine.model;

public interface Object3D {

	// number of coordinates per vertex in this array
	int COORDS_PER_VERTEX = 3;
	int VERTEX_STRIDE = COORDS_PER_VERTEX * 4; // 4 bytes per

	float[] DEFAULT_COLOR = { 1.0f, 1.0f, 1.0f, 1.0f };

	void draw(Object3DData obj, float[] pMatrix, float[] vMatrix, int textureId, float[] lightPosInEyeSpace);

	void draw(Object3DData obj, float[] pMatrix, float[] vMatrix, int drawType, int drawSize, int textureId, float[] lightPosInEyeSpace);
}