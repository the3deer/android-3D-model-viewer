package org.andresoviedo.util.math;

import android.opengl.Matrix;

import org.junit.Test;

public class QuaternionTest {

    @Test
    public void testRotation1(){
        float[] matrix = new float[16];

        Math3DUtils.setRotateM(matrix,0,180,0,0,1);
        System.out.println(Math3DUtils.toString(matrix,0));
        Quaternion sut = Quaternion.fromMatrix(matrix);
        System.out.println(sut.toString());

        Math3DUtils.setRotateM(matrix,0,270,0,0,1);
        System.out.println(Math3DUtils.toString(matrix,0));
        sut = Quaternion.fromMatrix(matrix);
        System.out.println(sut.toString());

        Math3DUtils.setRotateM(matrix,0,-359,0,0,1);
        System.out.println(Math3DUtils.toString(matrix,0));
        sut = Quaternion.fromMatrix(matrix);
        System.out.println(sut.toString());
    }
}
