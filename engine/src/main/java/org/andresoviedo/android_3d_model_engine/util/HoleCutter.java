package org.andresoviedo.android_3d_model_engine.util;

import android.opengl.Matrix;
import android.util.Log;

import org.andresoviedo.util.math.Math3DUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HoleCutter {

    public static final float[] Z_NORMAL = {0, 0, 1};

    public static List<Integer> pierce(List<float[]> triangles, List<List<float[]>> holesList) {

        // calculate polygon normal
        final float[] normal = Math3DUtils.calculateNormal(triangles.get(0), triangles.get(1), triangles.get(2));
        Math3DUtils.normalize(normal);

        // calculate 2D rotation
        final float dot = Math3DUtils.dotProduct(Z_NORMAL, normal);
        final float angle = (float) Math.acos(dot);
        final float[] cross = Math3DUtils.crossProduct(Z_NORMAL, normal);
        Math3DUtils.normalize(cross);
        cross[1] = 0;
        cross[2] = 0;
        float[] rotationMatrix = Math3DUtils.createRotationMatrixAroundVector(angle, cross[0], cross[1], cross[2]);

        Log.i("HoleCutter", "normal: " + Arrays.toString(normal) + ", angle: " + angle + ", axis: " + Arrays.toString(cross));

        // map 3D to 2D
        final List<Float> list2D = new ArrayList<>();
        final float[] temp1 = new float[4];
        final float[] mult = new float[4];
        for (float[] triangle : triangles) {
            temp1[0] = triangle[0];
            temp1[1] = triangle[1];
            temp1[2] = triangle[2];
            temp1[3] = 1;
            Matrix.multiplyMV(mult, 0, rotationMatrix, 0, temp1, 0);
            list2D.add(mult[0]);
            list2D.add(mult[1]);

            //Log.i("HoleCutter", "p: " + Arrays.toString(temp1));
            //Log.i("HoleCutter","v1: "+ Arrays.toString(mult));
        }

        // convert to integers
        List<Integer> ints = new ArrayList<>();
        for (int i = 0; i < list2D.size(); i++) {
            ints.add(((int)(list2D.get(i)*1000000)));
        }
        Log.i("HoleCutter","Ints:"+ints.toString());
        Log.i("HoleCutter", "Ints size: " + list2D.size());

        final List<Integer> holeIndices = new ArrayList<>();
        for (List<float[]> holeList : holesList) {

            holeIndices.add(list2D.size()/2);

            for (int i=0; i<holeList.size(); i++) {
                float[] hole = holeList.get(i);
                temp1[0] = hole[0];
                temp1[1] = hole[1];
                temp1[2] = hole[2];
                temp1[3] = 1;
                Matrix.multiplyMV(mult, 0, rotationMatrix, 0, temp1, 0);

                int holeIndex = list2D.size() / 2;
                list2D.add(mult[0]);
                list2D.add(mult[1]);
            }

            //Log.i("HoleCutter", "h: " + Arrays.toString(temp1));
        }


        ints = new ArrayList<>();
        for (int i = 0; i < list2D.size(); i++) {
            ints.add(((int)(list2D.get(i)*1000000)));
        }
        Log.i("HoleCutter","Ints with Holes:"+ints.toString());
        Log.i("HoleCutter", "Ints holes indices: " + holeIndices.toString());
        Log.i("HoleCutter", "Ints size: " + list2D.size());

        // triangulate
        float[] rq = new float[list2D.size()];
        for (int i = 0; i < list2D.size(); i++) {
            rq[i] = list2D.get(i);
        }
        int[] h = new int[holeIndices.size()];
        for (int i = 0; i < holeIndices.size(); i++) {
            h[i] = holeIndices.get(i);
        }

        return EarCut.earcut(rq, h, 2);
    }
}
