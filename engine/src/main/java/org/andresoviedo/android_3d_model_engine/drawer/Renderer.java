package org.andresoviedo.android_3d_model_engine.drawer;

import org.andresoviedo.android_3d_model_engine.model.Object3DData;

public interface Renderer {

	void draw(Object3DData obj, float[] pMatrix, float[] vMatrix, int textureId, float[] lightPosInWorldSpace, float[] cameraPos);

	void draw(Object3DData obj, float[] pMatrix, float[] vMatrix, int textureId, float[] lightPosInWorldSpace, float[] colorMask, float[] cameraPos);

	void draw(Object3DData obj, float[] pMatrix, float[] vMatrix, int drawType, int drawSize, int textureId, float[]
			lightPosInWorldSpace, float[] colorMask, float[] cameraPos);
}