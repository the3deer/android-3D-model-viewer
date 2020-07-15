package org.andresoviedo.android_3d_model_engine.gui;

import android.util.Log;

import org.andresoviedo.util.io.IOUtils;

import java.nio.FloatBuffer;

public class Glyph extends Widget {

    // total bytes = SIZE + 3 floats x 4 bytes-per-float
    private static final int SIZE = 15;

    public static final int MENU = 1000;
    public static final int CHECKBOX_OFF = 2000;
    public static final int CHECKBOX_ON = 2001;
    static final int GLYPH_LESS_THAN_CODE = 3000;
    static final int GLYPH_GREATER_THAN_CODE = 3001;

    private final static float[] GLYPH_BOX = new float[]{
            0.0f, 0.0f, 0.0f,
            0.0f, 0.6f, 0.0f,
            0.4f, 0.6f, 0.0f,
            0.4f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.0f
    };

    private final static float[] GLYPH_LESS_THAN = new float[]{
            0.3f, 0.0f, 0.0f,
            0.1f, 0.3f, 0.0f,
            0.3f, 0.6f, 0.0f
    };

    private final static float[] GLYPH_GREATER_THAN = new float[]{
            0.1f, 0.0f, 0.0f,
            0.3f, 0.3f, 0.0f,
            0.1f, 0.6f, 0.0f
    };

    private int code;

    private Glyph(int code) {
        super();
        this.code = code;
        this.init();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
        build(getVertexBuffer(), getColorsBuffer(), code);
    }

    public static Glyph build(int code) {
        return new Glyph(code);
    }

    private void init() {
        try {
            // buffers
            final FloatBuffer vertexBuffer = IOUtils.createNativeByteBuffer(SIZE * 3 * 4).asFloatBuffer();
            final FloatBuffer colorBuffer = IOUtils.createNativeByteBuffer(SIZE * 4 * 4)
                    .asFloatBuffer();

            // build
            vertexBuffer.position(0);
            colorBuffer.position(0);
            build(vertexBuffer, colorBuffer, this.code);

            // setup
            setVertexBuffer(vertexBuffer);
            setColorsBuffer(colorBuffer);
        } catch (Exception e) {
            Log.e("Glyph", e.getMessage(), e);
        }
    }

    private static void build(FloatBuffer vertexBuffer, FloatBuffer colorBuffer, int code) {
        switch (code) {
            case MENU:
                buildLine(vertexBuffer, colorBuffer,
                        0f, 0.5f,
                        0.4f, 0.5f);
                buildLine(vertexBuffer, colorBuffer,
                        0f, 0.3f,
                        0.4f, 0.3f);
                buildLine(vertexBuffer, colorBuffer,
                        0f, 0.1f,
                        0.4f, 0.1f);
                break;
            case CHECKBOX_ON:
                buildLine(vertexBuffer, colorBuffer,
                        0.1f, 0.2f,
                        0.3f, 0.4f);
                buildLine(vertexBuffer, colorBuffer,
                        0.1f, 0.4f,
                        0.3f, 0.2f);
            case CHECKBOX_OFF:
                vertexBuffer.put(GLYPH_BOX[0]).put(GLYPH_BOX[1]).put(GLYPH_BOX[2]);
                colorBuffer.put(0).put(0).put(0).put(0);
                vertexBuffer.put(GLYPH_BOX);
                for (int i = 0; i < GLYPH_BOX.length; i += 3)
                    colorBuffer.put(1f).put(1f).put(1f).put(1f);
                break;
        }
        fillArraysWithZero(vertexBuffer, colorBuffer);
    }

    public static void build(FloatBuffer vertexBuffer, FloatBuffer colorBuffer,
                             int code, float offsetX, float offsetY, float offsetZ){
        build(vertexBuffer, colorBuffer, code, new float[]{1f,1f,1f,1f}, offsetX, offsetY, offsetZ);
    }

    public static void build(FloatBuffer vertexBuffer, FloatBuffer colorBuffer,
                             int code,
                             float[] color, float offsetX, float offsetY, float offsetZ) {

        switch (code){
            case CHECKBOX_ON:
                buildLine(vertexBuffer, colorBuffer,
                        0.1f + offsetX, 0.2f + offsetY,
                        0.3f + offsetX, 0.4f + offsetY);
                buildLine(vertexBuffer, colorBuffer,
                        0.1f + offsetX, 0.4f + offsetY,
                        0.3f + offsetX, 0.2f + offsetY);
            case CHECKBOX_OFF:
                build(vertexBuffer, colorBuffer, GLYPH_BOX, color, offsetX, offsetY, offsetZ);
                break;
            case GLYPH_LESS_THAN_CODE:
                build(vertexBuffer, colorBuffer, GLYPH_LESS_THAN, color, offsetX, offsetY, offsetZ);
                break;
            case GLYPH_GREATER_THAN_CODE:
                build(vertexBuffer, colorBuffer, GLYPH_GREATER_THAN, color, offsetX, offsetY, offsetZ);
                break;
        }
    }

    private static void build(FloatBuffer vertexBuffer, FloatBuffer colorBuffer,
                              float[] glyph,
                              float[] color,
                              float offsetX, float offsetY, float offsetZ) {

        vertexBuffer.put(glyph[0]+offsetX).put(glyph[1]+offsetY).put(glyph[2]+offsetZ);
        colorBuffer.put(0f).put(0f).put(0f).put(0f);
        for (int i = 0; i < glyph.length; i += 3) {
            vertexBuffer.put(glyph[i] + offsetX)
                    .put(glyph[i + 1] + offsetY)
                    .put(glyph[i + 2] + offsetZ);
            colorBuffer.put(color[0]).put(color[1]).put(color[2]).put(color[3]);
        }
        vertexBuffer.put(glyph[glyph.length-3]+offsetX)
                .put(glyph[glyph.length-2]+offsetY)
                .put(glyph[glyph.length-1]+offsetZ);
        colorBuffer.put(color[0]).put(0f).put(0f).put(0f);
    }

    private static void buildLine(FloatBuffer vertexBuffer, FloatBuffer colorBuffer,
                                  float x1, float y1,
                                  float x2, float y2) {
        vertexBuffer.put(x1).put(y1).put(0f);
        colorBuffer.put(0f).put(0f).put(0f).put(0f);
        vertexBuffer.put(x1).put(y1).put(0f);
        colorBuffer.put(1f).put(1f).put(1f).put(1f);

        vertexBuffer.put(x2).put(y2).put(0f);
        colorBuffer.put(1f).put(1f).put(1f).put(1f);
        vertexBuffer.put(x2).put(y2).put(0f);
        colorBuffer.put(0f).put(0f).put(0f).put(0f);
    }


}
