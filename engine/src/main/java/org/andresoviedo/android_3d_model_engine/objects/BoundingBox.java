package org.andresoviedo.android_3d_model_engine.objects;

import android.opengl.GLES20;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.model.Dimensions;
import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.util.io.IOUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public final class BoundingBox {

    public static Object3DData build(Object3DData obj) {

        Log.i("BoundingBox","Building bounding box... "+obj);

        Dimensions box = obj.getDimensions();

        final FloatBuffer vertices = IOUtils.createFloatBuffer(8 * 3);
        //@formatter:off
        vertices.put(box.getMin()[0]).put(box.getMin()[1]).put(box.getMin()[2]);  // down-left (far)
        vertices.put(box.getMin()[0]).put(box.getMax()[1]).put(box.getMin()[2]);  // up-left (far)
        vertices.put(box.getMax()[0]).put(box.getMax()[1]).put(box.getMin()[2]);  // up-right (far)
        vertices.put(box.getMax()[0]).put(box.getMin()[1]).put(box.getMin()[2]);  // down-right  (far)
        vertices.put(box.getMin()[0]).put(box.getMin()[1]).put(box.getMax()[2]);  // down-left (near)
        vertices.put(box.getMin()[0]).put(box.getMax()[1]).put(box.getMax()[2]);  // up-left (near)
        vertices.put(box.getMax()[0]).put(box.getMax()[1]).put(box.getMax()[2]);  // up-right (near)
        vertices.put(box.getMax()[0]).put(box.getMin()[1]).put(box.getMax()[2]);  // down-right (near)
        //@formatter:on

        final IntBuffer indexBuffer = IOUtils.createIntBuffer(6 * 4);

        // back-face
        indexBuffer.put(0);
        indexBuffer.put(1);
        indexBuffer.put(2);
        indexBuffer.put(3);

        // front-face
        indexBuffer.put(4);
        indexBuffer.put(5);
        indexBuffer.put(6);
        indexBuffer.put(7);

        // left-face
        indexBuffer.put(4);
        indexBuffer.put(5);
        indexBuffer.put(1);
        indexBuffer.put(0);

        // right-face
        indexBuffer.put(3);
        indexBuffer.put(2);
        indexBuffer.put(6);
        indexBuffer.put(7);

        // top-face
        indexBuffer.put(1);
        indexBuffer.put(2);
        indexBuffer.put(6);
        indexBuffer.put(5);

        // bottom-face
        indexBuffer.put(0);
        indexBuffer.put(3);
        indexBuffer.put(7);
        indexBuffer.put(4);

        List<int[]> drawList = new ArrayList<>();
        int drawOrderPos = 0;
        for (int i = 0; i < indexBuffer.capacity(); i += 4) {
            drawList.add(new int[]{GLES20.GL_LINE_LOOP, drawOrderPos, 4});
            drawOrderPos += 4;
        }

        return new Object3DData(vertices, indexBuffer).setDrawModeList(drawList)
                .setDrawMode(GLES20.GL_LINE_LOOP)
                .setLocation(obj.getLocation())
                .setScale(obj.getScale())
                .setRotation(obj.getRotation())
                .setDrawUsingArrays(false)
                .setBindTransform(obj.getBindTransform())
                .setId(obj.getId() + "_boundingBox");
    }
}
