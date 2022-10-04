package org.the3deer.android_3d_model_engine.objects;

import android.opengl.GLES20;

import org.the3deer.android_3d_model_engine.model.Object3DData;
import org.the3deer.util.io.IOUtils;

public final class Line {

    public static Object3DData build(float[] line) {
        return new Object3DData(IOUtils.createFloatBuffer(line.length).put(line))
                .setDrawMode(GLES20.GL_LINES).setId("Line");
    }
}
