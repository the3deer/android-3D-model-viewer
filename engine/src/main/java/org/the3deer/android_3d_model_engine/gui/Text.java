package org.the3deer.android_3d_model_engine.gui;

import android.util.Log;

import org.the3deer.android_3d_model_engine.model.Dimensions;
import org.the3deer.util.io.IOUtils;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * 8 bit font
 *
 * Based on 5 x 7 monospaced pixel font
 *
 * All Glyphs are drawn using a line strip
 */
public final class Text extends Widget {

    private final static float[] SYMBOL_MINUS = new float[]{
            0.0f,0.3f,0f,
            0.5f,0.3f,0f,
    };

    private final static float[] SYMBOL_POINT = new float[]{
            0.1f,0.1f,0f,
            0.1f,0.2f,0f,
            0.2f,0.2f,0f,
            0.2f,0.1f,0f,
            0.1f,0.1f,0f,
    };

    private final static float[] SYMBOL_COMMA = new float[]{
            0.1f,0.0f,0f,
            0.2f,0.1f,0f,
            0.2f,0.2f,0f,
    };

    private final static float[] SYMBOL_COLON = new float[]{
            0.1f,0.0f,0f,
            0.1f,0.2f,0f,
              0.1f,0.2f,0f,
              0.1f,0.3f,0f,
            0.1f,0.3f,0f,
            0.1f,0.5f,0f
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
            0.2f,0.3f,0f,
            0.1f,0.3f,0f,
              0.1f,0.3f,0f,
              0.2f,0.4f,0f,
            0.2f,0.4f,0f,
            0.2f,0.5f,0f,
    };
    private final static float[] LETTER_j = new float[]{
            0.1f,0.1f,0f,
            0.2f,0.0f,0f,
            0.3f,0.1f,0f,
            0.3f,0.4f,0f,
              0.3f,0.4f,0f,
              0.3f,0.5f,0f,
            0.3f,0.5f,0f,
            0.3f,0.6f,0f,
    };

    private final static float[] LETTER_k = new float[]{
            0.0f,0.0f,0f,
            0.0f,0.6f,0f,
            0.0f,0.2f,0f,
            0.1f,0.2f,0f,
            0.3f,0.4f,0f,
            0.1f,0.2f,0f,
            0.3f,0.0f,0f,
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
            0.4f,0.3f,0f,
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
            0.2f,0.0f,0f,
            0.4f,0.2f,0f,
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

    private final static float[] LETTER_q = new float[]{
            0.4f,0.0f,0f,
            0.4f,0.4f,0f,
            0.1f,0.4f,0f,
            0.0f,0.3f,0f,
            0.1f,0.2f,0f,
            0.4f,0.2f,0f,
    };

    private final static float[] s = new float[]{
            0.4f,0.4f,0f,
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
            0.4f,0.0f,0f,
            0.1f,0.0f,0f,
            0.0f,0.1f,0f,
            0.0f,0.3f,0f,
            0.1f,0.4f,0f,
            0.3f,0.4f,0f,
            0.4f,0.3f,0f,
            0.4f,0.2f,0f,
            0.0f,0.2f,0f
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


    private final static float[] LETTER_y = new float[]{
            0.0f,0.4f,0f,
            0.0f,0.3f,0f,
            0.1f,0.2f,0f,
            0.4f,0.2f,0f,
            0.4f,0.4f,0f,
            0.4f,0.1f,0f,
            0.3f,0.0f,0f,
            0.1f,0.0f,0f,
    };

    private final static float[] LETTER_z = new float[]{
            0.0f,0.4f,0f,
            0.4f,0.4f,0f,
            0.0f,0.0f,0f,
            0.4f,0.0f,0f
    };

    private final static float[] LETTER_A = new float[]{
            0.0f,0.0f,0f,
            0.0f,0.5f,0f,
            0.1f,0.6f,0f,
            0.3f,0.6f,0f,
            0.4f,0.5f,0f,
            0.4f,0.0f,0f,
            0.4f,0.2f,0f,
            0.0f,0.2f,0f
    };

    private final static float[] LETTER_B = new float[]{
            0.0f,0.0f,0f,
            0.0f,0.6f,0f,
            0.3f,0.6f,0f,
            0.4f,0.5f,0f,
            0.4f,0.4f,0f,
            0.3f,0.3f,0f,
            0.0f,0.3f,0f,
            0.3f,0.3f,0f,
            0.4f,0.2f,0f,
            0.4f,0.1f,0f,
            0.3f,0.0f,0f,
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

    private final static float[] LETTER_M = new float[]{
            0.0f,0.0f,0f,
            0.0f,0.6f,0f,
            0.2f,0.3f,0f,
            0.5f,0.6f,0f,
            0.5f,0.0f,0f,
    };

    private final static float[] LETTER_P = new float[]{
            0.0f,0.0f,0f,
            0.0f,0.6f,0f,
            0.3f,0.6f,0f,
            0.4f,0.5f,0f,
            0.4f,0.3f,0f,
            0.3f,0.3f,0f,
            0.0f,0.3f,0f,
    };

    private final static float[] LETTER_T = new float[]{
            0.2f,0.0f,0f,
            0.2f,0.6f,0f,
            0.0f,0.6f,0f,
            0.4f,0.6f,0f,
    };

    private final static float[] LETTER_V = new float[]{
            0.0f,0.6f,0f,
            0.2f,0.0f,0f,
            0.4f,0.6f,0f
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

    private final static float[] LETTER_v = new float[]{
            0.0f,0.4f,0f,
            0.2f,0.0f,0f,
            0.4f,0.4f,0f,
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

    private final static float[] LETTER_b = new float[]{
            0.0f,0.0f,0f,
            0.0f,0.7f,0f,
            0.0f,0.3f,0f,
            0.1f,0.3f,0f,
            0.2f,0.4f,0f,
            0.3f,0.4f,0f,
            0.4f,0.3f,0f,
            0.4f,0.1f,0f,
            0.3f,0.0f,0f,
            0.2f,0.0f,0f,
            0.1f,0.1f,0f,
            0.0f,0.1f,0f
    };

    final static Map<Character, float[]> LETTERS = new HashMap<>();
    static {

        LETTERS.put('-',SYMBOL_MINUS);
        LETTERS.put('.',SYMBOL_POINT);
        LETTERS.put(',',SYMBOL_COMMA);
        LETTERS.put(':',SYMBOL_COLON);

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
        LETTERS.put('b',LETTER_b);
        LETTERS.put('c',LETTER_c);
        LETTERS.put('d',LETTER_d);
        LETTERS.put('e',LETTER_e);
        LETTERS.put('f',f);
        LETTERS.put('g',LETTER_g);
        LETTERS.put('h',LETTER_h);
        LETTERS.put('i',LETTER_i);
        LETTERS.put('j',LETTER_j);
        LETTERS.put('k',LETTER_k);
        LETTERS.put('m',LETTER_m);
        LETTERS.put('n',LETTER_n);
        LETTERS.put('l',LETTER_l);
        LETTERS.put('p',p);
        LETTERS.put('q',LETTER_q);
        LETTERS.put('r',LETTER_r);
        LETTERS.put('s',s);
        LETTERS.put('o',LETTER_o);
        LETTERS.put('t',LETTER_t);
        LETTERS.put('u',LETTER_u);
        LETTERS.put('v',LETTER_v);
        LETTERS.put('w',LETTER_w);
        LETTERS.put('x',LETTER_x);
        LETTERS.put('y',LETTER_y);
        LETTERS.put('z',LETTER_z);

        LETTERS.put('A',LETTER_A);
        LETTERS.put('B',LETTER_B);
        LETTERS.put('C',LETTER_C);
        LETTERS.put('L',LETTER_L);
        LETTERS.put('M',LETTER_M);
        LETTERS.put('P',LETTER_P);
        LETTERS.put('T',LETTER_T);
        LETTERS.put('V',LETTER_V);
    }

    private final int rows;
    private final int columns;
    private float padding;

    private String currentText = null;

    private Text(int columns, int rows) {
        this(columns, rows, 0);
    }

    private Text(int columns, int rows, float padding) {
        super();
        this.columns = columns;
        this.rows = rows;
        this.padding = padding;
        init();
    }

    private void init(){
        setVertexBuffer(IOUtils.createFloatBuffer(columns * rows * 12 * 3));
        setColorsBuffer(IOUtils.createFloatBuffer(columns * rows * 12 * 4));
        setDimensions(new Dimensions(0,
                columns*(0.5f+padding * 2),rows*(0.7f+padding * 2),0,0,0));
        Log.d("Text","Created text: "+ getDimensions());
    }

    public static Text allocate(int columns, int rows){
        return allocate(columns, rows, 0);
    }

    public static Text allocate(int columns, int rows, float padding){
        return new Text(columns, rows, padding);
    }

    public void update(String text){
        if (text == null || text.equals(this.currentText)) return;

        final String[] lines = text.split("\\r?\\n");

        final FloatBuffer vertexBuffer = getVertexBuffer();
        final FloatBuffer colorBuffer = getColorsBuffer();

        int idx = 0;
        for (int row=0; row<this.rows && row<lines.length; row++){
            for (int column=0; column<this.columns && column < lines[row].length(); column++){
                float offsetX = column * (0.5f + padding * 2) + padding;
                float offsetY = (this.rows -1 ) * (0.7f + padding * 2) - row * (0.7f + padding * 2) + padding;

                final char letter = lines[row].charAt(column);
                if (letter == '\n') {
                    break;
                }

                final float[] data = LETTERS.get(letter);
                if (data == null) continue;

                idx = buildGlyph(idx, vertexBuffer, colorBuffer, offsetX, offsetY, data);
            }
        }

        int idxColor = idx / 3 * 4;

        for (int i=idx; i<vertexBuffer.capacity(); i++){
            vertexBuffer.put(idx++, 0f);
        }
        for (int i=idxColor; i<colorBuffer.capacity(); i++){
            colorBuffer.put(idxColor++, 0f);
        }
    }

    private int buildGlyph(int idx, FloatBuffer vertexBuffer, FloatBuffer colorBuffer, float offsetX, float offsetY, float[] data) {

        int idxColor = idx / 3 * 4;

        vertexBuffer.put(idx++, data[0]+offsetX);
        vertexBuffer.put(idx++, data[1]+offsetY);
        vertexBuffer.put(idx++, data[2]-0.1f);

        for (int i=0; i<data.length; i+=3){
            vertexBuffer.put(idx++, data[i]+offsetX);
            vertexBuffer.put(idx++, data[i+1]+offsetY);


            if (i>=3 && i < data.length - 6 &&
                    (data[i-3] == data[i] && data[i-2] == data[i+1] && data[i-1] == data[i+2]
                            ||
                            (data[i+3] == data[i] && data[i+4] == data[i+1] && data[i+5] == data[i+2]))){
                vertexBuffer.put(idx++, data[i+2]-0.1f);
            } else {
                vertexBuffer.put(idx++, data[i+2]);
            }
        }
        vertexBuffer.put(idx++, data[data.length-3]+offsetX);
        vertexBuffer.put(idx++, data[data.length-2]+offsetY);
        vertexBuffer.put(idx++, data[data.length-1]-0.1f);



        colorBuffer.put(idxColor++, 1f);
        colorBuffer.put(idxColor++,0f);
        colorBuffer.put(idxColor++,0f);
        colorBuffer.put(idxColor++,0f);

        boolean isBlind = false;
        for (int i=0; i<data.length; i+=3){


            if (isBlind){
                colorBuffer.put(idxColor++,0f);
                colorBuffer.put(idxColor++,0f);
                colorBuffer.put(idxColor++,0f);
                colorBuffer.put(idxColor++,0f);
                isBlind = false;
            }
            else if (i>=3 && i < data.length - 6 &&
                    (data[i-3] == data[i] && data[i-2] == data[i+1] && data[i-1] == data[i+2])){
                // same vertex - blind spot
                isBlind = true;
                colorBuffer.put(idxColor++,0f);
                colorBuffer.put(idxColor++,0f);
                colorBuffer.put(idxColor++,0f);
                colorBuffer.put(idxColor++,0f);
                Log.v("Text","Blind spot: "+i);
            } else {
                colorBuffer.put(idxColor++,1f);
                colorBuffer.put(idxColor++,1f);
                colorBuffer.put(idxColor++,1f);
                colorBuffer.put(idxColor++,1f);
            }

        }
        colorBuffer.put(idxColor++,0f);
        colorBuffer.put(idxColor++,0f);
        colorBuffer.put(idxColor++,0f);
        colorBuffer.put(idxColor++,0f);

        return idx;
    }
}
