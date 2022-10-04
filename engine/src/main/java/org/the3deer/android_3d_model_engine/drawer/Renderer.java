package org.the3deer.android_3d_model_engine.drawer;

import org.the3deer.android_3d_model_engine.model.Object3DData;

public interface Renderer {

	void draw(Object3DData obj, float[] pMatrix, float[] vMatrix, int textureId, float[] lightPosInWorldSpace, float[] colorMask, float[] cameraPos, int drawType, int drawSize);

    void setAutoUseProgram(boolean autoUseProgram);

    void useProgram();

    int getProgram();
}