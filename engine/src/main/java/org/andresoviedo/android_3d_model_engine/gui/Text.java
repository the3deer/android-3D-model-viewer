package org.andresoviedo.android_3d_model_engine.gui;

import android.util.Log;

import org.andresoviedo.android_3d_model_engine.model.Dimensions;
import org.andresoviedo.util.io.IOUtils;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public final class Text extends Widget {

    private final static float[] SYMBOL_MINUS = new float[]{
            0.0f,0.3f,0f,
            0.5f,0.3f,0f,
    };

    private final static float[] _0 = new float[]{
            0f,0.2f,0f,
            0f,0.1f,0f,
            0.1f,0f,0f,
            0.3f,0f,0f,
            0.4f,0.1f,0f,
            0.4f,0.5f,0f,
            0.3f,0.6f,0f,
            0.1f,0.6f,0f,
            0f,0.5f,0f,
            0f,0.2f,0f,
            0.4f,0.4f,0f
    };

    private final static float[] _1 = new float[]{
            0.1f,0f,0f,
            0.3f,0f,0f,
            0.2f,0f,0f,
            0.2f,0.6f,0f,
            0.1f,0.5f,0f
    };

    private final static float[] LETTER_l = new float[]{
            0.1f,0f,0f,
            0.3f,0f,0f,
            0.2f,0f,0f,
            0.2f,0.6f,0f,
            0.1f,0.6f,0f
    };

    private final static float[] LETTER_i = new float[]{
            0.1f,0f,0f,
            0.3f,0f,0f,
            0.2f,0f,0f,
            0.2f,0.4f,0f,
            0.1f,0.3f,0f
    };

    private final static float[] LETTER_m = new float[]{
            0.0f,0.0f,0f,
            0.0f,0.4f,0f,
            0.1f,0.4f,0f,
            0.2f,0.3f,0f,
            0.2f,0.0f,0f,
            0.2f,0.4f,0f,
            0.3f,0.4f,0f,
            0.4f,0.3f,0f,
            0.4f,0.0f,0f
    };

    private final static float[] _2 = new float[]{
            0f,0.5f,0f,
            0.1f,0.6f,0f,
            0.3f,0.6f,0f,
            0.4f,0.5f,0f,
            0.4f,0.4f,0f,
            0f,0f,0f,
            0.4f,0f,0f
    };

    private final static float[] _3 = new float[]{
            0.0f,0.6f,0f,
            0.4f,0.6f,0f,
            0.2f,0.4f,0f,
            0.4f,0.2f,0f,
            0.4f,0.1f,0f,
            0.3f,0.0f,0f,
            0.1f,0.0f,0f,
            0.0f,0.1f,0f
    };

    private final static float[] _4 = new float[]{
            0.3f,0.0f,0f,
            0.3f,0.6f,0f,
            0.0f,0.3f,0f,
            0.0f,0.2f,0f,
            0.4f,0.2f,0f,
    };

    private final static float[] _5 = new float[]{
            0.4f,0.6f,0f,
            0.0f,0.6f,0f,
            0.0f,0.4f,0f,
            0.3f,0.4f,0f,
            0.4f,0.3f,0f,
            0.4f,0.1f,0f,
            0.3f,0.0f,0f,
            0.0f,0.0f,0f,
    };

    private final static float[] _6 = new float[]{
            0.3f,0.6f,0f,
            0.2f,0.6f,0f,
            0.0f,0.4f,0f,
            0.0f,0.1f,0f,
            0.1f,0.0f,0f,
            0.3f,0.0f,0f,
            0.4f,0.1f,0f,
            0.4f,0.2f,0f,
            0.3f,0.3f,0f,
            0.0f,0.3f,0f,
    };

    private final static float[] _7 = new float[]{
            0.0f,0.6f,0f,
            0.4f,0.6f,0f,
            0.4f,0.5f,0f,
            0.1f,0.2f,0f,
            0.1f,0.0f,0f,
    };

    private final static float[] _8 = new float[]{
            0.1f,0.3f,0f,
            0.0f,0.2f,0f,
            0.0f,0.1f,0f,
            0.1f,0.0f,0f,
            0.3f,0.0f,0f,
            0.4f,0.1f,0f,
            0.4f,0.2f,0f,
            0.3f,0.3f,0f,
            0.1f,0.3f,0f,
            0.0f,0.4f,0f,
            0.0f,0.5f,0f,
            0.1f,0.6f,0f,
            0.3f,0.6f,0f,
            0.4f,0.5f,0f,
            0.4f,0.4f,0f,
            0.3f,0.3f,0f,
    };

    private final static float[] _9 = new float[]{
            0.1f,0.0f,0f,
            0.2f,0.0f,0f,
            0.4f,0.3f,0f,
            0.4f,0.5f,0f,
            0.3f,0.6f,0f,
            0.1f,0.6f,0f,
            0.0f,0.5f,0f,
            0.0f,0.4f,0f,
            0.1f,0.3f,0f,
            0.4f,0.3f,0f,
    };

    private final static float[] LETTER_c = new float[]{
            0.3f,0.4f,0f,
            0.1f,0.4f,0f,
            0.0f,0.3f,0f,
            0.0f,0.1f,0f,
            0.1f,0.0f,0f,
            0.3f,0.0f,0f,
            0.4f,0.1f,0f,
    };

    private final static float[] LETTER_d = new float[]{
            0.4f,0.6f,0f,
            0.4f,0.0f,0f,
            0.4f,0.2f,0f,
            0.2f,0.4f,0f,
            0.1f,0.4f,0f,
            0.0f,0.3f,0f,
            0.0f,0.1f,0f,
            0.1f,0.0f,0f,
            0.4f,0.0f,0f,
    };

    private final static float[] f = new float[]{
            0.1f,0.0f,0f,
            0.1f,0.3f,0f,
            0.0f,0.3f,0f,
            0.2f,0.3f,0f,
            0.1f,0.3f,0f,
            0.1f,0.5f,0f,
            0.2f,0.6f,0f,
            0.3f,0.6f,0f,
            0.4f,0.5f,0f,
    };

    private final static float[] p = new float[]{
            0.0f,0.0f,0f,
            0.0f,0.4f,0f,
            0.3f,0.4f,0f,
            0.4f,0.3f,0f,
            0.3f,0.2f,0f,
            0.0f,0.2f,0f,
    };

    private final static float[] s = new float[]{
            0.3f,0.4f,0f,
            0.1f,0.4f,0f,
            0.0f,0.3f,0f,
            0.1f,0.2f,0f,
            0.3f,0.2f,0f,
            0.4f,0.1f,0f,
            0.3f,0.0f,0f,
            0.0f,0.0f,0f,
    };

    private final static float[] LETTER_o = new float[]{
            0.1f,0.0f,0f,
            0.3f,0.0f,0f,
            0.4f,0.1f,0f,
            0.4f,0.3f,0f,
            0.3f,0.4f,0f,
            0.1f,0.4f,0f,
            0.0f,0.3f,0f,
            0.0f,0.1f,0f,
            0.1f,0.0f,0f,
    };

    private final static float[] LETTER_e = new float[]{
            0.3f,0.0f,0f,
            0.1f,0.0f,0f,
            0.0f,0.1f,0f,
            0.0f,0.3f,0f,
            0.1f,0.4f,0f,
            0.3f,0.4f,0f,
            0.4f,0.3f,0f,
            0.4f,0.2f,0f,
            0.0f,0.2f,0f,
    };

    private final static float[] LETTER_g = new float[]{
            0.1f,0.0f,0f,
            0.3f,0.0f,0f,
            0.4f,0.1f,0f,
            0.4f,0.4f,0f,
            0.1f,0.4f,0f,
            0.0f,0.3f,0f,
            0.1f,0.2f,0f,
            0.4f,0.2f,0f,
    };

    private final static float[] LETTER_h = new float[]{
            0.0f,0.6f,0f,
            0.0f,0.0f,0f,
            0.0f,0.2f,0f,
            0.2f,0.4f,0f,
            0.3f,0.4f,0f,
            0.4f,0.3f,0f,
            0.4f,0.0f,0f,
    };

    private final static float[] LETTER_n = new float[]{
            0.0f,0.4f,0f,
            0.0f,0.0f,0f,
            0.0f,0.2f,0f,
            0.2f,0.4f,0f,
            0.3f,0.4f,0f,
            0.4f,0.3f,0f,
            0.4f,0.0f,0f,
    };

    private final static float[] LETTER_t = new float[]{
            0.1f,0.6f,0f,
            0.1f,0.4f,0f,
            0.2f,0.4f,0f,
            0.0f,0.4f,0f,
            0.1f,0.4f,0f,
            0.1f,0.1f,0f,
            0.2f,0.0f,0f,
            0.3f,0.0f,0f,
            0.4f,0.1f,0f,
    };

    private final static float[] LETTER_x = new float[]{
            0.0f,0.4f,0f,
            0.4f,0.0f,0f,
            0.2f,0.2f,0f,
            0.4f,0.4f,0f,
            0.0f,0.0f,0f,
    };

    private final static float[] LETTER_C = new float[]{
            0.4f,0.5f,0f,
            0.3f,0.6f,0f,
            0.1f,0.6f,0f,
            0.0f,0.5f,0f,
            0.0f,0.1f,0f,
            0.1f,0.0f,0f,
            0.3f,0.0f,0f,
            0.4f,0.1f,0f,

    };

    private final static float[] LETTER_L = new float[]{
            0.0f,0.6f,0f,
            0.0f,0.0f,0f,
            0.4f,0.0f,0f,
    };

    private final static float[] LETTER_T = new float[]{
            0.2f,0.0f,0f,
            0.2f,0.6f,0f,
            0.0f,0.6f,0f,
            0.4f,0.6f,0f,
    };

    private final static float[] LETTER_w = new float[]{
            0.0f,0.4f,0f,
            0.0f,0.1f,0f,
            0.1f,0.0f,0f,
            0.2f,0.1f,0f,
            0.2f,0.2f,0f,
            0.2f,0.1f,0f,
            0.3f,0.0f,0f,
            0.4f,0.1f,0f,
            0.4f,0.4f,0f,
    };

    private final static float[] LETTER_u = new float[]{
            0.0f,0.4f,0f,
            0.0f,0.1f,0f,
            0.1f,0.0f,0f,
            0.2f,0.0f,0f,
            0.4f,0.2f,0f,
            0.4f,0.4f,0f,
            0.4f,0.0f,0f,
    };

    private final static float[] LETTER_r = new float[]{
            0.0f,0.4f,0f,
            0.0f,0.0f,0f,
            0.0f,0.2f,0f,
            0.2f,0.4f,0f,
            0.3f,0.4f,0f,
            0.4f,0.3f,0f,
    };

    private final static float[] LETTER_a = new float[]{
            0.1f,0.4f,0f,
            0.3f,0.4f,0f,
            0.4f,0.3f,0f,
            0.4f,0.0f,0f,
            0.1f,0.0f,0f,
            0.0f,0.1f,0f,
            0.1f,0.2f,0f,
            0.4f,0.2f,0f,
    };

    final static Map<Character, float[]> LETTERS = new HashMap<>();
    static {

        LETTERS.put('-',SYMBOL_MINUS);

        LETTERS.put('0',_0);
        LETTERS.put('1',_1);
        LETTERS.put('2',_2);
        LETTERS.put('3',_3);
        LETTERS.put('4',_4);
        LETTERS.put('5',_5);
        LETTERS.put('6',_6);
        LETTERS.put('7',_7);
        LETTERS.put('8',_8);
        LETTERS.put('9',_9);

        LETTERS.put('a',LETTER_a);
        LETTERS.put('c',LETTER_c);
        LETTERS.put('d',LETTER_d);
        LETTERS.put('e',LETTER_e);
        LETTERS.put('f',f);
        LETTERS.put('g',LETTER_g);
        LETTERS.put('h',LETTER_h);
        LETTERS.put('i',LETTER_i);
        LETTERS.put('m',LETTER_m);
        LETTERS.put('n',LETTER_n);
        LETTERS.put('l',LETTER_l);
        LETTERS.put('p',p);
        LETTERS.put('r',LETTER_r);
        LETTERS.put('s',s);
        LETTERS.put('o',LETTER_o);
        LETTERS.put('t',LETTER_t);
        LETTERS.put('u',LETTER_u);
        LETTERS.put('w',LETTER_w);
        LETTERS.put('x',LETTER_x);

        LETTERS.put('C',LETTER_C);
        LETTERS.put('L',LETTER_L);
        LETTERS.put('T',LETTER_T);
    }

    private final int rows;
    private final int columns;

    private String currentText = null;

    private Text(int columns, int rows) {
        super();
        this.columns = columns;
        this.rows = rows;
        init();
    }

    private void init(){
        setVertexBuffer(IOUtils.createFloatBuffer(columns * rows * 12 * 3));
        setColorsBuffer(IOUtils.createFloatBuffer(columns * rows * 12 * 4));
        setDimensions(new Dimensions(0,columns*0.5f,rows*0.7f,0,0,0));
        Log.d("Text","Created text: "+ getDimensions());
    }

    public static Text allocate(int columns, int rows){
        return new Text(columns, rows);
    }

    public void update(String text){
        if (text == null || text.equals(this.currentText)) return;

        final FloatBuffer vertexBuffer = getVertexBuffer();
        vertexBuffer.position(0);
        final FloatBuffer colorBuffer = getColorsBuffer();
        colorBuffer.position(0);

        int idx = 0;
        for (int row=0; row<this.rows; row++){
            for (int column=0; column<this.columns && idx < text.length(); column++, idx++){
                float offsetX = 0.5f * column;
                float offsetY = (rows - 1) * 0.7f - row * 0.7f;

                final char letter = text.charAt(idx);
                final float[] data = LETTERS.get(letter);
                if (data == null) continue;

                buildGlyph(vertexBuffer, colorBuffer, offsetX, offsetY, data);
            }
        }
        for (int i=vertexBuffer.position(); i<vertexBuffer.capacity(); i++){
            vertexBuffer.put(0f);
        }
        for (int i=colorBuffer.position(); i<colorBuffer.capacity(); i++){
            colorBuffer.put(0f);
        }
    }

    private void buildGlyph(FloatBuffer vertexBuffer, FloatBuffer colorBuffer, float offsetX, float offsetY, float[] data) {
        vertexBuffer.put(data[0]+offsetX);
        vertexBuffer.put(data[1]+offsetY);
        vertexBuffer.put(data[2]);
        for (int i=0; i<data.length; i+=3){
            vertexBuffer.put(data[i]+offsetX);
            vertexBuffer.put(data[i+1]+offsetY);
            vertexBuffer.put(data[i+2]);
        }
        vertexBuffer.put(data[data.length-3]+offsetX);
        vertexBuffer.put(data[data.length-2]+offsetY);
        vertexBuffer.put(data[data.length-1]);

        colorBuffer.put(0f);
        colorBuffer.put(0f);
        colorBuffer.put(0f);
        colorBuffer.put(0f);
        for (int i=0; i<data.length; i+=3){
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
