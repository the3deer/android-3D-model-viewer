package org.the3deer.android_3d_model_engine.objects;

import android.opengl.GLES20;

import org.the3deer.android_3d_model_engine.model.Object3DData;
import org.the3deer.util.io.IOUtils;

public final class Point {

    public static Object3DData build(float[] location) {
        float[] point = new float[]{location[0], location[1], location[2]};
        return new Object3DData(IOUtils.createFloatBuffer(point.length).put(point))
                .setDrawMode(GLES20.GL_POINTS).setId("Point");
    }
}
