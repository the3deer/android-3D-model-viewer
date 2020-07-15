package org.andresoviedo.android_3d_model_engine.util;

import android.opengl.GLES20;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.util.io.IOUtils;
import org.andresoviedo.util.math.Math3DUtils;

import java.nio.FloatBuffer;

public class Exploder {

    public static void centerAndScaleAndExplode(Object3DData object3DData, float maxSize, float explodeFactor) {

        if (object3DData.getDrawMode() != GLES20.GL_TRIANGLES) {
            Log.i("Object3DData", "Cant explode '" + object3DData.getId() + " because its not made of triangles...");
            return;
        }

        float leftPt = Float.MAX_VALUE, rightPt = Float.MIN_VALUE; // on x-axis
        float topPt = Float.MIN_VALUE, bottomPt = Float.MAX_VALUE; // on y-axis
        float farPt = Float.MAX_VALUE, nearPt = Float.MIN_VALUE; // on z-axis

        FloatBuffer vertexBuffer = object3DData.getVertexBuffer() != null ? object3DData.getVertexBuffer() : object3DData.getVertexBuffer();
        if (vertexBuffer == null) {
            Log.v("Object3DData", "Scaling for '" + object3DData.getId() + "' I found that there is no vertex data");
            return;
        }

        Log.i("Object3DData", "Calculating dimensions for '" + object3DData.getId() + "...");
        for (int i = 0; i < vertexBuffer.capacity(); i += 3) {
            if (vertexBuffer.get(i) > rightPt)
                rightPt = vertexBuffer.get(i);
            else if (vertexBuffer.get(i) < leftPt)
                leftPt = vertexBuffer.get(i);
            if (vertexBuffer.get(i + 1) > topPt)
                topPt = vertexBuffer.get(i + 1);
            else if (vertexBuffer.get(i + 1) < bottomPt)
                bottomPt = vertexBuffer.get(i + 1);
            if (vertexBuffer.get(i + 2) > nearPt)
                nearPt = vertexBuffer.get(i + 2);
            else if (vertexBuffer.get(i + 2) < farPt)
                farPt = vertexBuffer.get(i + 2);
        } // end

        // calculate center of 3D object
        float xc = (rightPt + leftPt) / 2.0f;
        float yc = (topPt + bottomPt) / 2.0f;
        float zc = (nearPt + farPt) / 2.0f;

        // calculate largest dimension
        float height = topPt - bottomPt;
        float depth = nearPt - farPt;
        float largest = rightPt - leftPt;
        if (height > largest)
            largest = height;
        if (depth > largest)
            largest = depth;

        // scale object

        // calculate a scale factor
        float scaleFactor = 1.0f;
        if (largest != 0.0f)
            scaleFactor = (maxSize / largest);
        Log.i("Object3DData",
                "Exploding '" + object3DData.getId() + "' to '" + xc + "," + yc + "," + zc + "' '" + scaleFactor + "'");

        // modify the model's vertices
        FloatBuffer vertexBufferNew = IOUtils.createFloatBuffer(vertexBuffer.capacity());
        for (int i = 0; i < vertexBuffer.capacity(); i += 3) {
            float x = vertexBuffer.get(i);
            float y = vertexBuffer.get(i + 1);
            float z = vertexBuffer.get(i + 2);
            x = (x - xc) * scaleFactor;
            y = (y - yc) * scaleFactor;
            z = (z - zc) * scaleFactor;
            vertexBuffer.put(i, x);
            vertexBuffer.put(i + 1, y);
            vertexBuffer.put(i + 2, z);
            vertexBufferNew.put(i, x * explodeFactor);
            vertexBufferNew.put(i + 1, y * explodeFactor);
            vertexBufferNew.put(i + 2, z * explodeFactor);
        }

        if (object3DData.getDrawOrder() != null) {
            Log.e("Object3DData", "Cant explode object composed of indexes '" + object3DData.getId() + "'");
            return;
        }

        for (int i = 0; i < vertexBuffer.capacity(); i += 9) {
            float x1 = vertexBuffer.get(i);
            float y1 = vertexBuffer.get(i + 1);
            float z1 = vertexBuffer.get(i + 2);
            float x2 = vertexBuffer.get(i + 3);
            float y2 = vertexBuffer.get(i + 4);
            float z2 = vertexBuffer.get(i + 5);
            float x3 = vertexBuffer.get(i + 6);
            float y3 = vertexBuffer.get(i + 7);
            float z3 = vertexBuffer.get(i + 8);
            float[] center1 = Math3DUtils.calculateFaceCenter(new float[]{x1, y1, z1}, new float[]{x2, y2, z2},
                    new float[]{x3, y3, z3});

            float xe1 = vertexBufferNew.get(i);
            float ye1 = vertexBufferNew.get(i + 1);
            float ze1 = vertexBufferNew.get(i + 2);
            float xe2 = vertexBufferNew.get(i + 3);
            float ye2 = vertexBufferNew.get(i + 4);
            float ze2 = vertexBufferNew.get(i + 5);
            float xe3 = vertexBufferNew.get(i + 6);
            float ye3 = vertexBufferNew.get(i + 7);
            float ze3 = vertexBufferNew.get(i + 8);
            float[] center2 = Math3DUtils.calculateFaceCenter(new float[]{xe1, ye1, ze1},
                    new float[]{xe2, ye2, ze2}, new float[]{xe3, ye3, ze3});

            vertexBuffer.put(i + 0, x1 + (center2[0] - center1[0]));
            vertexBuffer.put(i + 1, y1 + (center2[1] - center1[1]));
            vertexBuffer.put(i + 2, z1 + (center2[2] - center1[2]));
            vertexBuffer.put(i + 3, x2 + (center2[0] - center1[0]));
            vertexBuffer.put(i + 4, y2 + (center2[1] - center1[1]));
            vertexBuffer.put(i + 5, z2 + (center2[2] - center1[2]));
            vertexBuffer.put(i + 6, x3 + (center2[0] - center1[0]));
            vertexBuffer.put(i + 7, y3 + (center2[1] - center1[1]));
            vertexBuffer.put(i + 8, z3 + (center2[2] - center1[2]));
        }

        object3DData.setVertexBuffer(vertexBuffer);

    }
}
