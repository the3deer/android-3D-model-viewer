package org.andresoviedo.android_3d_model_engine.gui;

import android.opengl.GLES20;

import org.andresoviedo.android_3d_model_engine.model.Dimensions;
import org.andresoviedo.android_3d_model_engine.model.Object3DData;
import org.andresoviedo.util.io.IOUtils;

import java.nio.FloatBuffer;
import java.util.EventObject;

public class Quad extends Widget {

    private final Dimensions dimensions;

    private Quad(Dimensions dimensions) {
        this.dimensions = dimensions;
        // buffers
        int size = 4;
        final FloatBuffer vertexBuffer = IOUtils.createNativeByteBuffer(size * 3 * 4).asFloatBuffer();
        // build
        build(vertexBuffer, dimensions);
        // setup
        setVertexBuffer(vertexBuffer);

        setDrawMode(GLES20.GL_TRIANGLE_STRIP);
    }

    public static Quad build(Dimensions dimensions) {
        return new Quad(dimensions);
    }

    @Override
    public boolean onEvent(EventObject event) {
        super.onEvent(event);
        if (event instanceof ChangeEvent) {
            Object3DData source = (Object3DData) event.getSource();
            /*if (this.dimensions != source.getDimensions()) {
                build(getVertexArrayBuffer(), source.getDimensions());
            }*/
            this.setLocation(source.getLocation());
            this.setScale(source.getScale());
            this.setRotation(source.getRotation());
            this.setRotation2(source.getRotation2(), source.getRotation2Location());
            this.setVisible(source.isVisible());
        }
        return true;
    }

    private static void build(FloatBuffer vertexBuffer, Dimensions dimensions) {

        float[] min = dimensions.getMin();
        float[] max = dimensions.getMax();

        vertexBuffer.position(0);

        vertexBuffer.put(min[0]).put(min[1]).put(min[2]);
        vertexBuffer.put(min[0]).put(max[1]).put(min[2]);
        vertexBuffer.put(max[0]).put(min[1]).put(min[2]);
        vertexBuffer.put(max[0]).put(max[1]).put(min[2]);

        // 3D
        /*if (min[2] != max[2]) {

            vertexBuffer.put(max[0]).put(min[1]).put(max[2]);
            vertexBuffer.put(max[0]).put(max[1]).put(max[2]);

            vertexBuffer.put(min[0]).put(min[1]).put(max[2]);
            vertexBuffer.put(min[0]).put(max[1]).put(max[2]);

            vertexBuffer.put(min[0]).put(min[1]).put(min[2]);
            vertexBuffer.put(min[0]).put(max[1]).put(min[2]);
        }*/
    }
}
