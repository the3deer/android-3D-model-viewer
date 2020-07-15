package org.andresoviedo.android_3d_model_engine.gui;

import android.util.Log;

import org.andresoviedo.android_3d_model_engine.animation.JointTransform;
import org.andresoviedo.android_3d_model_engine.model.Dimensions;
import org.andresoviedo.util.io.IOUtils;

import java.nio.FloatBuffer;
import java.util.EventObject;

public class Menu3D extends Widget {

    public static final float ROW_HEIGHT = 1f;
    public static final float COL_WIDTH = 0.5f;

    public static class ItemSelected extends EventObject {

        private final String[] items;
        private final int selected;

        ItemSelected(Object source, String[] items, int selected){
            super(source);
            this.items = items;
            this.selected = selected;
        }

        public int getSelected() {
            return selected;
        }
    }

    private final static int GLYPH_SIZE = 18;

    private final String[] items;
    private final int totalGlyphs;
    private final float height;
    private final int rows;
    private final int cols;

    private boolean[] states;
    private int selected = -1;

    private Menu3D(String[] items, boolean[] states) {
        super();
        if (items == null || items.length == 0) throw new IllegalArgumentException();

        this.items = items;
        this.states = states;
        // count total chars
        int max = 0;
        int count = 0;
        for (String item : items) {
            if (item.length() > max) max = item.length();
            count += item.length();
        }
        this.cols = max;
        this.totalGlyphs = count;
        this.rows = this.items.length;
        this.height = rows * ROW_HEIGHT;
        this.init();
        for (int i=0; i<items.length; i++) refresh(i);
    }

    public static Menu3D build(String[] items) {
        return new Menu3D(items,new boolean[items.length]);
    }

    public void setState(int idx, boolean state) {
        states[idx] = state;
        refresh(idx);
    }

    private void refresh(int idx){
        float offsetX = COL_WIDTH * cols;
        float offsetY = (rows * ROW_HEIGHT) - ((idx + 1) * ROW_HEIGHT);
        int mark = GLYPH_SIZE * 3 * totalGlyphs + GLYPH_SIZE * 3 * idx;
        int mark2 = GLYPH_SIZE * 4 * totalGlyphs + GLYPH_SIZE * 4 * idx ;
        getVertexBuffer().position(mark);
        getColorsBuffer().position(mark2);
        if (states[idx]) {
            Glyph.build(getVertexBuffer(), getColorsBuffer(), Glyph.CHECKBOX_ON, offsetX, offsetY, 0);
        } else {
            Glyph.build(getVertexBuffer(), getColorsBuffer(), Glyph.CHECKBOX_OFF, offsetX, offsetY, 0);
        }
        setVertexBuffer(getVertexBuffer());
    }

    private void init() {
        try {
            // allocate buffers
            final int total = totalGlyphs + items.length + 1; // +1 for border
            final FloatBuffer vertexBuffer = IOUtils.createNativeByteBuffer(total * GLYPH_SIZE * 3 * 4).asFloatBuffer();
            final FloatBuffer colorBuffer = IOUtils.createNativeByteBuffer(total * GLYPH_SIZE * 4 * 4)
                    .asFloatBuffer();

            vertexBuffer.position(0);
            colorBuffer.position(0);

            int idx = 0;
            float[] data;
            for (int row = 0; row < rows; row++) {
                final String text = this.items[row];
                for (int column = 0; column < text.length(); column++, idx++) {
                    float offsetX = COL_WIDTH * column;
                    float offsetY = (rows * ROW_HEIGHT) - ((row + 1) * ROW_HEIGHT);

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

            float totalWidth = cols * COL_WIDTH + COL_WIDTH;

            Log.v("Menu3D","Size. width:"+ totalWidth +", height:"+height+", depth:"+ totalWidth);

            buildBorder(vertexBuffer, colorBuffer, totalWidth, rows * ROW_HEIGHT);

            //buildCube(vertexBuffer, colorBuffer, 0, getPosition(), totalWidth, height, totalWidth);

            fillArraysWithZero(vertexBuffer, colorBuffer);

            setVertexBuffer(vertexBuffer);
            setColorsBuffer(colorBuffer);
        } catch (Exception e) {
            Log.e("Menu", e.getMessage(), e);
        }
    }

    private static void buildCube(FloatBuffer resultVertexBuffer, FloatBuffer resultColorBuffer,
                                  int offset, float[] location, float width, float height, float depth) {

        resultVertexBuffer.put(location[0]).put(location[1]).put(location[2]);
        resultColorBuffer.put(1f).put(1f).put(1f).put(1f);

        resultVertexBuffer.put(location[0]+width).put(location[1]).put(location[2]);
        resultColorBuffer.put(1f).put(1f).put(1f).put(1f);

        resultVertexBuffer.put(location[0]+width).put(location[1]).put(location[2]-depth);
        resultColorBuffer.put(1f).put(1f).put(1f).put(1f);

        resultVertexBuffer.put(location[0]).put(location[1]).put(location[2]-depth);
        resultColorBuffer.put(1f).put(1f).put(1f).put(1f);
    }

    @Override
    public boolean onEvent(EventObject event) {
        super.onEvent(event);
        if (event instanceof GUI.ClickEvent) {
            GUI.ClickEvent clickEvent = (GUI.ClickEvent) event;
            if (clickEvent.getWidget() != this) return true;
            float y = clickEvent.getY();
            y -= getLocationY();
            y /= getScaleY();
            y /= ROW_HEIGHT;
            int idx = items.length - 1 - (int) y;

            Log.i("Menu3D","select: "+idx);
            fireEvent(new ItemSelected(this, items, idx));
        }
        return true;
    }

    private int getSelected() {
        return selected;
    }

    @Override
    public void toggleVisible(){
        if (isVisible()) {
            Log.i("Menu3D", "Hiding menu...");

            JointTransform start = new JointTransform(new float[16]);
            start.setLocation(getInitialPosition());
            start.setScale(getInitialScale());

            JointTransform end = new JointTransform(new float[16]);
            end.setScale(new float[]{0, 0, 0});
            end.setLocation(getLocation());
            if (getParent() != null) {
                end.setLocation(getParent().getLocation());
            }
            end.setVisible(false);
            animate(start, end, 250);
        } else {
            Log.i("Menu3D", "Showing menu... parent> "+getParent()+","+getParent().getLocationX());

            JointTransform start = new JointTransform(new float[16]);
            start.setScale(new float[]{0, 0, 0});
            start.setLocation(getInitialPosition());
            if (getParent() != null)
                start.setLocation(getParent().getLocation());

            JointTransform end = new JointTransform(new float[16]);
            end.setScale(getInitialScale());
            end.setLocation(getInitialPosition());
            animate(start, end, 250);
        }
    }

    @Override
    public void setCurrentDimensions(Dimensions currentDimensions) {
        super.setCurrentDimensions(currentDimensions);
        Log.d("Menu3D","new dimensions: "+ currentDimensions);
    }
}
