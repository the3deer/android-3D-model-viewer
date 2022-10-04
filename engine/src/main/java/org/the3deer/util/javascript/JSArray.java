package org.the3deer.util.javascript;

public class JSArray {

    public static float[] arrayOf(float a, float b){
        return new float[]{a, b};
    }

    public static float[] arrayOf(float a, float b, float c, float d){
        return new float[]{a, b, c, d};
    }

    public static Object[] shift(float[][] src){
        final float[][] shifted = new float[src.length-1][];
        System.arraycopy(src, 1, shifted, 0, src.length-1);
        return new Object[]{src[0], shifted};
    }

    public static Object[] pop(float[][] src){
        final float[][] shifted = new float[src.length-1][];
        System.arraycopy(src, 0, shifted, 0, src.length-1);
        return new Object[]{src[src.length-1], shifted};
    }

    public static Object[] unshift(float[][] src, float e[]) {
        final float[][] shifted = new float[src.length+1][];
        System.arraycopy(src, 0, shifted, 1, src.length-1);
        shifted[0] = e;
        return new Object[]{shifted.length, shifted};
    }

    public static Object[] push(float[][] src, float[] e) {
        final float[][] ret = new float[src.length+1][];
        System.arraycopy(src, 0, ret, 0, src.length-1);
        ret[ret.length-1] = e;
        return new Object[]{ret.length, ret};
    }
}
