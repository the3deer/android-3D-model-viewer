package org.andresoviedo.android_3d_model_engine.objects;

import android.opengl.GLES20;

import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.util.io.IOUtils;

public final class Axis {

    private final static float[] axisVertexLinesData = new float[]{
            //@formatter:off
            0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, // right
            0.0f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f, // left
            0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, // up
            0.0f, 0.0f, 0.0f, 0.0f, -1.0f, 0.0f, // down
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, // z+
            0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -1.0f, // z-

            0.95f, 0.05f, 0, 1, 0, 0, 0.95f, -0.05f, 0, 1, 0f, 0f, // Arrow X (>)
            -0.95f, 0.05f, 0, -1, 0, 0, -0.95f, -0.05f, 0, -1, 0f, 0f, // Arrow X (<)
            -0.05f, 0.95f, 0, 0, 1, 0, 0.05f, 0.95f, 0, 0, 1f, 0f, // Arrox Y (^)
            -0.05f, 0, 0.95f, 0, 0, 1, 0.05f, 0, 0.95f, 0, 0, 1, // Arrox z (v)

            1.05F, 0.05F, 0, 1.10F, -0.05F, 0, 1.05F, -0.05F, 0, 1.10F, 0.05F, 0, // Letter X
            -0.05F, 1.05F, 0, 0.05F, 1.10F, 0, -0.05F, 1.10F, 0, 0.0F, 1.075F, 0, // Letter Y
            -0.05F, 0.05F, 1.05F, 0.05F, 0.05F, 1.05F, 0.05F, 0.05F, 1.05F, -0.05F, -0.05F, 1.05F, -0.05F, -0.05F,
            1.05F, 0.05F, -0.05F, 1.05F // letter z
            //@formatter:on
    };

    public static Object3DData build() {
        return new Object3DData(IOUtils.createFloatBuffer(axisVertexLinesData.length).put(axisVertexLinesData))
                .setDrawMode(GLES20.GL_LINES);
    }
}
