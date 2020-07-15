package org.andresoviedo.android_3d_model_engine.objects;

import android.opengl.GLES20;

import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.util.io.IOUtils;

import java.nio.FloatBuffer;

public class Grid {

    public static Object3DData build(float xStart, float yStart, float zStart, float xEnd, float yEnd, float zEnd,
                                     float step) {
        int nbLines = (int) ((xEnd - xStart) / step) + 1 + (int) ((zEnd - zStart) / step) + 1 + (int) ((yEnd -
                yStart) / step) + 1;
        int nbVertex = nbLines * 2;
        FloatBuffer vertexBuffer = IOUtils.createNativeByteBuffer(nbVertex * 3 * 4).asFloatBuffer();
        if (xStart < xEnd) {
            for (float x = xStart; x <= xEnd; x += step) {
                vertexBuffer.put(x).put(yStart).put(zStart);
                vertexBuffer.put(x).put(yEnd).put(zEnd);
            }
        }
        if (yStart < yEnd) {
            for (float y = yStart; y <= yEnd; y += step) {
                vertexBuffer.put(xStart).put(y).put(zStart);
                vertexBuffer.put(xEnd).put(y).put(zEnd);
            }
        }
        if (zStart < zEnd) {
            for (float z = zStart; z <= zEnd; z += step) {
                vertexBuffer.put(xStart).put(yStart).put(z);
                vertexBuffer.put(xEnd).put(yEnd).put(z);
            }
        }
        return new Object3DData(vertexBuffer).setDrawMode(GLES20.GL_LINES);
    }
}
