package org.andresoviedo.android_3d_model_engine.util;

import android.util.Log;

import org.andresoviedo.android_3d_model_engine.model.AnimatedModel;
import org.andresoviedo.android_3d_model_engine.model.Object3DData;

import java.nio.FloatBuffer;

public class Rescaler {

    public static void rescale(Object3DData object3DData, float maxSize) {
        float leftPt = Float.MAX_VALUE, rightPt = Float.MIN_VALUE; // on x-axis
        float topPt = Float.MIN_VALUE, bottomPt = Float.MAX_VALUE; // on y-axis
        float farPt = Float.MAX_VALUE, nearPt = Float.MIN_VALUE; // on z-axis

        FloatBuffer vertexBuffer = object3DData.getVertexBuffer();
        if (vertexBuffer == null) {
            Log.v("Rescaler", "Scaling for '" + object3DData.getId() + "' I found that there is no vertex data");
            return;
        }

        Log.i("Rescaler", "Calculating dimensions for '" + object3DData.getId() + "...");
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
        Log.i("Rescaler", "Dimensions for '" + object3DData.getId() + " (X left, X right): (" + leftPt + "," + rightPt + ")");
        Log.i("Rescaler", "Dimensions for '" + object3DData.getId() + " (Y top, Y bottom): (" + topPt + "," + bottomPt + ")");
        Log.i("Rescaler", "Dimensions for '" + object3DData.getId() + " (Z near, Z far): (" + nearPt + "," + farPt + ")");

        // calculate center of 3D object
        float xc = (rightPt + leftPt) / 2.0f;
        float yc = (topPt + bottomPt) / 2.0f;
        float zc = (nearPt + farPt) / 2.0f;

        // this.setOriginalPosition(new float[]{-xc,-yc,-zc});

        // calculate largest dimension
        float height = topPt - bottomPt;
        float depth = nearPt - farPt;
        float largest = rightPt - leftPt;
        if (height > largest)
            largest = height;
        if (depth > largest)
            largest = depth;
        Log.i("Rescaler", "Largest dimension [" + largest + "]");

        // scale object

        // calculate a scale factor
        float scaleFactor = 1.0f;
        if (largest != 0.0f)
            scaleFactor = (maxSize / largest);
        Log.i("Rescaler",
                "Scaling '" + object3DData.getId() + "' to (" + xc + "," + yc + "," + zc + ") scale: '" + scaleFactor + "'");

        // this.setOriginalScale(new float[]{scaleFactor,scaleFactor,scaleFactor});

        // modify the model's vertices
        if (object3DData instanceof AnimatedModel) {
            object3DData.setScale(new float[]{scaleFactor, scaleFactor, scaleFactor});
        } else {
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
            }

            object3DData.setVertexBuffer(vertexBuffer);
        }

        Log.i("Rescaler",
                "New dimensions for '" + object3DData.getId() + ": "+object3DData.getCurrentDimensions());
    }
}
