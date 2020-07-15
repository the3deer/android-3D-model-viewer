package org.andresoviedo.android_3d_model_engine.gui;

import android.util.Log;

import org.andresoviedo.util.io.IOUtils;

import java.nio.FloatBuffer;

public class Menu extends Widget {

    private final static float padding = 0.1f;
    private final String[] items;
    private final int totalGlyphs;

    private Menu(String[] items) {
        super();
        this.items = items;
        // count total chars
        int count = 0;
        for (String item : items) count += item.length();
        // add border
        count++;
        this.totalGlyphs = count;
        this.init();
    }

    public static Menu build(String... items) {
        return new Menu(items);
    }

    void init() {
        try {
            if (items == null || items.length == 0) return;

            // allocate buffers
            final FloatBuffer vertexBuffer = IOUtils.createNativeByteBuffer(totalGlyphs * 12 * 3 * 4).asFloatBuffer();
            final FloatBuffer colorBuffer = IOUtils.createNativeByteBuffer(totalGlyphs * 12 * 4 * 4)
                    .asFloatBuffer();

            vertexBuffer.position(0);
            colorBuffer.position(0);

            final int rows = this.items.length;
            int maxLength = 0;

            int idx = 0;
            float[] data = null;
            for (int row = 0; row < rows; row++) {
                final String text = this.items[row];
                if (text.length() > maxLength) maxLength = text.length();
                for (int column = 0; column < text.length(); column++, idx++) {
                    float offsetX = 0.5f * column + padding;
                    float offsetY = (rows * 0.7f + padding * 2) - ((row+1) * 0.7f + padding);

                    final char letter = text.charAt(column);
                    data = Text.LETTERS.get(letter);
                    if (data == null) continue;

                    vertexBuffer.put(data[0] + offsetX);
                    vertexBuffer.put(data[1] + offsetY);
                    vertexBuffer.put(data[2]);
                    for (int i = 0; i < data.length; i += 3) {
                        vertexBuffer.put(data[i] + offsetX);
                        vertexBuffer.put(data[i + 1] + offsetY);
                        vertexBuffer.put(data[i + 2]);
                    }
                    vertexBuffer.put(data[data.length - 3] + offsetX);
                    vertexBuffer.put(data[data.length - 2] + offsetY);
                    vertexBuffer.put(data[data.length - 1]);

                    colorBuffer.put(0f);
                    colorBuffer.put(0f);
                    colorBuffer.put(0f);
                    colorBuffer.put(0f);
                    for (int i = 0; i < data.length; i += 3) {
                        colorBuffer.put(1f);
                        colorBuffer.put(1f);
                        colorBuffer.put(1f);
                        colorBuffer.put(1f);
                    }
                    colorBuffer.put(0f);
                    colorBuffer.put(0f);
                    colorBuffer.put(0f);
                    colorBuffer.put(0f);
                }
            }

            // draw border
            vertexBuffer.put(0);
            vertexBuffer.put(0);
            vertexBuffer.put(0);
            for (int i=0; i<4; i++) colorBuffer.put(0f);

            vertexBuffer.put(0);
            vertexBuffer.put(0);
            vertexBuffer.put(0);
            for (int i=0; i<4; i++) colorBuffer.put(1f);

            vertexBuffer.put(maxLength*0.5f+padding*2);
            vertexBuffer.put(0);
            vertexBuffer.put(0);
            for (int i=0; i<4; i++) colorBuffer.put(1f);

            vertexBuffer.put(maxLength*0.5f+padding*2);
            vertexBuffer.put(rows*0.7f+padding*2);
            vertexBuffer.put(0);
            for (int i=0; i<4; i++) colorBuffer.put(1f);

            vertexBuffer.put(0);
            vertexBuffer.put(rows*0.7f+padding*2);
            vertexBuffer.put(0);
            for (int i=0; i<4; i++) colorBuffer.put(1f);

            vertexBuffer.put(0);
            vertexBuffer.put(0);
            vertexBuffer.put(0);
            for (int i=0; i<4; i++) colorBuffer.put(1f);

            for (int i = vertexBuffer.position(); i < vertexBuffer.capacity(); i++) {
                vertexBuffer.put(0f);
            }
            for (int i = colorBuffer.position(); i < colorBuffer.capacity(); i++) {
                colorBuffer.put(0f);
            }

            setVertexBuffer(vertexBuffer);
            setColorsBuffer(colorBuffer);
        } catch (Exception e) {
            Log.e("Menu",e.getMessage(), e);
        }
    }

    @Override
    public void toggleVisible() {
        super.toggleVisible();

    }
}
