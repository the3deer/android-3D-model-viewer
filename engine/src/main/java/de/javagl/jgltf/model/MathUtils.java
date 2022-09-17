/*
 * www.javagl.de - JglTF
 *
 * Copyright 2015-2016 Marco Hutter - http://www.javagl.de
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package de.javagl.jgltf.model;

import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Mathematical utility methods. These methods are mainly related to 
 * computations on arrays that represent matrices.<br> 
 * <br>
 * Unless otherwise noted, the matrices are assumed to be in 
 * <b>column-major</b> order.<br>
 * <br>
 * Unless otherwise noted, none of the arguments to these
 * methods may be <code>null</code>.<br>
 * <br>
 * Unless otherwise noted, each 4x4 matrix is assumed to have a length of 
 * at least 16, and each 3x3 matrix is assumed to have a length of 
 * at least 9. Points in 3D are assumed to have a length of at least 3.
 * 
 * TODO This class should not be considered as part of the public API!
 */
public class MathUtils
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(MathUtils.class.getName());
    
    /**
     * Epsilon for floating point computations
     */
    private static final float FLOAT_EPSILON = 1e-8f;
    
    /**
     * Creates a 4x4 identity matrix
     * 
     * @return The matrix
     */
    public static float[] createIdentity4x4()
    {
        float m[] = new float[16];
        setIdentity4x4(m);
        return m;
    }
    
    /**
     * Set the given matrix to be the identity matrix.
     * 
     * @param m The matrix
     */
    public static void setIdentity4x4(float m[])
    {
        Arrays.fill(m, 0.0f);
        m[0] = 1.0f;
        m[5] = 1.0f;
        m[10] = 1.0f;
        m[15] = 1.0f;
    }

    /**
     * Set the given matrix to be the identity matrix. 
     * 
     * @param m The matrix
     */
    static void setIdentity3x3(float m[])
    {
        Arrays.fill(m, 0.0f);
        m[0] = 1.0f;
        m[4] = 1.0f;
        m[8] = 1.0f;
    }
    
    /**
     * Copy the contents of the source array to the given target array. 
     * The length of the shorter array will determine how many elements 
     * are copied.
     * 
     * @param source The source array
     * @param target The target array
     */
    static void set(float source[], float target[])
    {
        System.arraycopy(source, 0, target, 0, 
            Math.min(source.length, target.length));
    }
    
    /**
     * Obtains the upper 3x3 matrix (which describes the rotation- and 
     * scaling part of the transformation) of the given 4x4 source matrix, 
     * and writes it into the given target 3x3 matrix. 
     * 
     * @param sourceMatrix4x4 The source matrix
     * @param targetMatrix3x3 The target matrix
     */
    public static void getRotationScale(
        float sourceMatrix4x4[], float targetMatrix3x3[])
    {
        targetMatrix3x3[0] = sourceMatrix4x4[ 0];
        targetMatrix3x3[1] = sourceMatrix4x4[ 1];
        targetMatrix3x3[2] = sourceMatrix4x4[ 2];
        targetMatrix3x3[3] = sourceMatrix4x4[ 4];
        targetMatrix3x3[4] = sourceMatrix4x4[ 5];
        targetMatrix3x3[5] = sourceMatrix4x4[ 6];
        targetMatrix3x3[6] = sourceMatrix4x4[ 8];
        targetMatrix3x3[7] = sourceMatrix4x4[ 9];
        targetMatrix3x3[8] = sourceMatrix4x4[10];
        
    }
    
    /**
     * Transpose the given matrix, and write the result into the given
     * target matrix. 
     * 
     * @param m The input matrix
     * @param t The target matrix
     */
    static void transpose3x3(float m[], float t[])
    {
        float m0 = m[0];
        float m1 = m[1];
        float m2 = m[2];
        float m3 = m[3];
        float m4 = m[4];
        float m5 = m[5];
        float m6 = m[6];
        float m7 = m[7];
        float m8 = m[8];
        t[0] = m0;
        t[1] = m3;
        t[2] = m6;
        t[3] = m1;
        t[4] = m4;
        t[5] = m7;
        t[6] = m2;
        t[7] = m5;
        t[8] = m8;
    }
    
    /**
     * Transpose the given matrix, and write the result into the given
     * target matrix. 
     * 
     * @param m The input matrix
     * @param t The target matrix
     */
    public static void transpose4x4(float m[], float t[])
    {
        float m0 = m[ 0];
        float m1 = m[ 1];
        float m2 = m[ 2];
        float m3 = m[ 3];
        float m4 = m[ 4];
        float m5 = m[ 5];
        float m6 = m[ 6];
        float m7 = m[ 7];
        float m8 = m[ 8];
        float m9 = m[ 9];
        float mA = m[10];
        float mB = m[11];
        float mC = m[12];
        float mD = m[13];
        float mE = m[14];
        float mF = m[15];
        t[ 0] = m0;
        t[ 1] = m4;
        t[ 2] = m8;
        t[ 3] = mC;
        t[ 4] = m1;
        t[ 5] = m5;
        t[ 6] = m9;
        t[ 7] = mD;
        t[ 8] = m2;
        t[ 9] = m6;
        t[10] = mA;
        t[11] = mE;
        t[12] = m3;
        t[13] = m7;
        t[14] = mB;
        t[15] = mF;
    }
    
    /**
     * Fills the given result matrix with the product of the given matrices.
     * 
     * @param a The first matrix
     * @param b The second matrix
     * @param m The result matrix
     */
    public static void mul4x4(float a[], float b[], float m[])
    {
        float a00 = a[ 0];
        float a10 = a[ 1];
        float a20 = a[ 2];
        float a30 = a[ 3];
        float a01 = a[ 4];
        float a11 = a[ 5];
        float a21 = a[ 6];
        float a31 = a[ 7];
        float a02 = a[ 8];
        float a12 = a[ 9];
        float a22 = a[10];
        float a32 = a[11];
        float a03 = a[12];
        float a13 = a[13];
        float a23 = a[14];
        float a33 = a[15];

        float b00 = b[ 0];
        float b10 = b[ 1];
        float b20 = b[ 2];
        float b30 = b[ 3];
        float b01 = b[ 4];
        float b11 = b[ 5];
        float b21 = b[ 6];
        float b31 = b[ 7];
        float b02 = b[ 8];
        float b12 = b[ 9];
        float b22 = b[10];
        float b32 = b[11];
        float b03 = b[12];
        float b13 = b[13];
        float b23 = b[14];
        float b33 = b[15];

        float m00 = a00 * b00 + a01 * b10 + a02 * b20 + a03 * b30;
        float m01 = a00 * b01 + a01 * b11 + a02 * b21 + a03 * b31;
        float m02 = a00 * b02 + a01 * b12 + a02 * b22 + a03 * b32;
        float m03 = a00 * b03 + a01 * b13 + a02 * b23 + a03 * b33;

        float m10 = a10 * b00 + a11 * b10 + a12 * b20 + a13 * b30;
        float m11 = a10 * b01 + a11 * b11 + a12 * b21 + a13 * b31;
        float m12 = a10 * b02 + a11 * b12 + a12 * b22 + a13 * b32;
        float m13 = a10 * b03 + a11 * b13 + a12 * b23 + a13 * b33;

        float m20 = a20 * b00 + a21 * b10 + a22 * b20 + a23 * b30;
        float m21 = a20 * b01 + a21 * b11 + a22 * b21 + a23 * b31;
        float m22 = a20 * b02 + a21 * b12 + a22 * b22 + a23 * b32;
        float m23 = a20 * b03 + a21 * b13 + a22 * b23 + a23 * b33;

        float m30 = a30 * b00 + a31 * b10 + a32 * b20 + a33 * b30;
        float m31 = a30 * b01 + a31 * b11 + a32 * b21 + a33 * b31;
        float m32 = a30 * b02 + a31 * b12 + a32 * b22 + a33 * b32;
        float m33 = a30 * b03 + a31 * b13 + a32 * b23 + a33 * b33;

        m[ 0] = m00;
        m[ 1] = m10;
        m[ 2] = m20;
        m[ 3] = m30;
        m[ 4] = m01;
        m[ 5] = m11;
        m[ 6] = m21;
        m[ 7] = m31;
        m[ 8] = m02;
        m[ 9] = m12;
        m[10] = m22;
        m[11] = m32;
        m[12] = m03;
        m[13] = m13;
        m[14] = m23;
        m[15] = m33;
    }
    

    /**
     * Fills the given matrix, with the values for the rotation that is 
     * described by the given quaternion. None of the arguments may be 
     * <code>null</code>. The quaternion must have at least length 4. 
     *  
     * @param q The quaternion
     * @param m The matrix
     */
    public static void quaternionToMatrix4x4(float q[], float m[])
    {
        float invLength = 1.0f / (float)Math.sqrt(dot(q, q));

        // Adapted from javax.vecmath.Matrix4f
        float qx = q[0] * invLength;
        float qy = q[1] * invLength;
        float qz = q[2] * invLength;
        float qw = q[3] * invLength;
        m[ 0] = 1.0f - 2.0f * qy * qy - 2.0f * qz * qz;
        m[ 1] = 2.0f * (qx * qy + qw * qz);
        m[ 2] = 2.0f * (qx * qz - qw * qy);
        m[ 3] = 0.0f;
        m[ 4] = 2.0f * (qx * qy - qw * qz);
        m[ 5] = 1.0f - 2.0f * qx * qx - 2.0f * qz * qz;
        m[ 6] = 2.0f * (qy * qz + qw * qx);
        m[ 7] = 0.0f;
        m[ 8] = 2.0f * (qx * qz + qw * qy);
        m[ 9] = 2.0f * (qy * qz - qw * qx);
        m[10] = 1.0f - 2.0f * qx * qx - 2.0f * qy * qy;
        m[11] = 0.0f;
        m[12] = 0.0f;
        m[13] = 0.0f;
        m[14] = 0.0f;
        m[15] = 1.0f;
    }
    
    /**
     * Inverts the given matrix and writes the result into the given target
     * matrix. If the given matrix is not invertible, then the target matrix 
     * will be set to identity.  
     * 
     * @param m The input matrix
     * @param inv The inverse matrix
     */
    public static void invert4x4(float m[], float inv[])
    {
        // Adapted from The Mesa 3-D graphics library. 
        // Copyright (C) 1999-2007  Brian Paul   All Rights Reserved.
        // Published under the MIT license (see the header of this file)
        float m0 = m[ 0];
        float m1 = m[ 1];
        float m2 = m[ 2];
        float m3 = m[ 3];
        float m4 = m[ 4];
        float m5 = m[ 5];
        float m6 = m[ 6];
        float m7 = m[ 7];
        float m8 = m[ 8];
        float m9 = m[ 9];
        float mA = m[10];
        float mB = m[11];
        float mC = m[12];
        float mD = m[13];
        float mE = m[14];
        float mF = m[15];

        inv[ 0] =  m5 * mA * mF - m5 * mB * mE - m9 * m6 * mF + 
                   m9 * m7 * mE + mD * m6 * mB - mD * m7 * mA;
        inv[ 4] = -m4 * mA * mF + m4 * mB * mE + m8 * m6 * mF - 
                   m8 * m7 * mE - mC * m6 * mB + mC * m7 * mA;
        inv[ 8] =  m4 * m9 * mF - m4 * mB * mD - m8 * m5 * mF + 
                   m8 * m7 * mD + mC * m5 * mB - mC * m7 * m9;
        inv[12] = -m4 * m9 * mE + m4 * mA * mD + m8 * m5 * mE - 
                   m8 * m6 * mD - mC * m5 * mA + mC * m6 * m9;
        inv[ 1] = -m1 * mA * mF + m1 * mB * mE + m9 * m2 * mF - 
                   m9 * m3 * mE - mD * m2 * mB + mD * m3 * mA;
        inv[ 5] =  m0 * mA * mF - m0 * mB * mE - m8 * m2 * mF + 
                   m8 * m3 * mE + mC * m2 * mB - mC * m3 * mA;
        inv[ 9] = -m0 * m9 * mF + m0 * mB * mD + m8 * m1 * mF - 
                   m8 * m3 * mD - mC * m1 * mB + mC * m3 * m9;
        inv[13] =  m0 * m9 * mE - m0 * mA * mD - m8 * m1 * mE + 
                   m8 * m2 * mD + mC * m1 * mA - mC * m2 * m9;
        inv[ 2] =  m1 * m6 * mF - m1 * m7 * mE - m5 * m2 * mF + 
                   m5 * m3 * mE + mD * m2 * m7 - mD * m3 * m6;
        inv[ 6] = -m0 * m6 * mF + m0 * m7 * mE + m4 * m2 * mF - 
                   m4 * m3 * mE - mC * m2 * m7 + mC * m3 * m6;
        inv[10] =  m0 * m5 * mF - m0 * m7 * mD - m4 * m1 * mF + 
                   m4 * m3 * mD + mC * m1 * m7 - mC * m3 * m5;
        inv[14] = -m0 * m5 * mE + m0 * m6 * mD + m4 * m1 * mE - 
                   m4 * m2 * mD - mC * m1 * m6 + mC * m2 * m5;
        inv[ 3] = -m1 * m6 * mB + m1 * m7 * mA + m5 * m2 * mB - 
                   m5 * m3 * mA - m9 * m2 * m7 + m9 * m3 * m6;
        inv[ 7] =  m0 * m6 * mB - m0 * m7 * mA - m4 * m2 * mB + 
                   m4 * m3 * mA + m8 * m2 * m7 - m8 * m3 * m6;
        inv[11] = -m0 * m5 * mB + m0 * m7 * m9 + m4 * m1 * mB - 
                   m4 * m3 * m9 - m8 * m1 * m7 + m8 * m3 * m5;
        inv[15] =  m0 * m5 * mA - m0 * m6 * m9 - m4 * m1 * mA + 
                   m4 * m2 * m9 + m8 * m1 * m6 - m8 * m2 * m5;
        // (Ain't that pretty?)
        
        float det = m0 * inv[0] + m1 * inv[4] + m2 * inv[8] + m3 * inv[12];
        if (Math.abs(det) <= FLOAT_EPSILON)
        {
            if (logger.isLoggable(Level.FINE)) 
            {
                logger.fine("Matrix is not invertible, determinant is " + det
                    + ", returning identity");
            }
            setIdentity4x4(inv);
            return;
        }
        float invDet = 1.0f / det;
        for (int i = 0; i < 16; i++)
        {
            inv[i] *= invDet;
        }
    }    
    
    /**
     * Inverts the given matrix and writes the result into the given target
     * matrix. If the given matrix is not invertible, then the target matrix 
     * will be set to identity.  
     * 
     * @param m The input matrix
     * @param inv The inverse matrix
     */
    public static void invert3x3(float m[], float inv[])
    {
        // Adapted from http://stackoverflow.com/a/18504573
        float m0 = m[0];
        float m1 = m[1];
        float m2 = m[2];
        float m3 = m[3];
        float m4 = m[4];
        float m5 = m[5];
        float m6 = m[6];
        float m7 = m[7];
        float m8 = m[8];
        float det = m0 * (m4 * m8 - m5 * m7) -
                    m3 * (m1 * m8 - m7 * m2) +
                    m6 * (m1 * m5 - m4 * m2);
        if (Math.abs(det) <= FLOAT_EPSILON)
        {
            if (logger.isLoggable(Level.FINE)) 
            {
                logger.fine("Matrix is not invertible, determinant is " + det
                    + ", returning identity");
            }
            setIdentity3x3(inv);
            return;
        }
        float invDet = 1.0f / det;
        inv[0] = (m4 * m8 - m5 * m7) * invDet;
        inv[3] = (m6 * m5 - m3 * m8) * invDet;
        inv[6] = (m3 * m7 - m6 * m4) * invDet;
        inv[1] = (m7 * m2 - m1 * m8) * invDet;
        inv[4] = (m0 * m8 - m6 * m2) * invDet;
        inv[7] = (m1 * m6 - m0 * m7) * invDet;
        inv[2] = (m1 * m5 - m2 * m4) * invDet;
        inv[5] = (m2 * m3 - m0 * m5) * invDet;
        inv[8] = (m0 * m4 - m1 * m3) * invDet;        
    }
    
    /**
     * Writes the given matrix into the given result matrix, with the
     * given values added to the translation component
     * 
     * @param m The input matrix
     * @param x The x-translation
     * @param y The y-translation
     * @param z The z-translation
     * @param result The result matrix
     */
    public static void translate(
        float m[], float x, float y, float z, float result[])
    {
        set(m,  result);
        result[12] += x;
        result[13] += y;
        result[14] += z;
    }
    
    /**
     * Fill the given matrix to describe an infinite perspective projection 
     * with the given parameters. 
     * 
     * @param fovyDeg The Field-Of-View, in y-direction, in degrees
     * @param aspect The aspect ratio
     * @param zNear The z-value of the near clipping plane
     * @param m The matrix to fill
     */
    public static void infinitePerspective4x4(
        float fovyDeg, float aspect, float zNear, float m[])
    {
        setIdentity4x4(m);
        float fovyRad = (float)Math.toRadians(fovyDeg);
        float t = (float)Math.tan(0.5 * fovyRad);
        m[0] = 1.0f / (aspect * t);
        m[5] = 1.0f / t;
        m[10] = -1.0f;
        m[11] = -1.0f;
        m[14] = 2.0f * zNear;
        m[15] = 0.0f;
    }
    
    /**
     * Fill the given matrix to describe a perspective projection with the
     * given parameters. 
     * 
     * @param fovyDeg The Field-Of-View, in y-direction, in degrees
     * @param aspect The aspect ratio
     * @param zNear The z-value of the near clipping plane
     * @param zFar The z-value of the far clipping plane
     * @param m The matrix to fill
     */
    public static void perspective4x4(
        float fovyDeg, float aspect, float zNear, float zFar, float m[])
    {
        setIdentity4x4(m);
        float fovyRad = (float)Math.toRadians(fovyDeg);
        float t = (float)Math.tan(0.5 * fovyRad);
        m[0] = 1.0f / (aspect * t);
        m[5] = 1.0f / t;
        m[10] = (zFar + zNear) / (zNear - zFar);
        m[11] = -1.0f;
        m[14] = 2.0f * zFar * zNear / (zNear - zFar);
        m[15] = 0.0f;
    }
    
    
    /**
     * Computes the dot product of the given arrays. The arrays must have 
     * equal length.
     * 
     * @param a The first array
     * @param b The second array
     * @return The dot product
     */
    private static float dot(float a[], float b[])
    {
        float sum = 0;
        for (int i=0; i<a.length; i++)
        {
            sum += a[i] * b[i];
        }
        return sum;
    }
    
    
    /**
     * Transform the given 3D point with the given 4x4 matrix (thus, 
     * treating the non-present fourth component of the point as 
     * being 1.0), and write the result into the given result array.
     * 
     * @param matrix4x4 The matrix
     * @param point3D The input point
     * @param result3D The result point
     */
    public static void transformPoint3D(
        float matrix4x4[], float point3D[], float result3D[])
    {
        Arrays.fill(result3D, 0.0f);
        for (int r=0; r<3; r++)
        {
            for (int c=0; c<3; c++)
            {
                int index = c * 4 + r;
                float m = matrix4x4[index];
                result3D[r] += m * point3D[c];
            }
            int index = 3 * 4 + r;
            float m = matrix4x4[index];
            result3D[r] += m;
        }
    }
    
    /**
     * Create a string representation of the given array, as a matrix, 
     * interpreting it as a matrix that is stored in column-major order. The 
     * given array may be <code>null</code>. If it is not <code>null</code>,
     * then it must either have 3x3 elements or 4x4 elements.
     * 
     * @param array The array
     * @return The string representation
     */
    public static String createMatrixString(float array[])
    {
        if (array == null)
        {
            return "null";
        }
        if (array.length == 9)
        {
            return createMatrixString(array, 3, 3);
        }
        if (array.length == 16)
        {
            return createMatrixString(array, 4, 4);
        }
        return "WARNING: Not a matrix: "+Arrays.toString(array);
    }
    
    /**
     * Creates a string representation of the given matrix, which is given
     * in column-major order
     * 
     * @param array The array storing the matrix
     * @param rows The number of rows
     * @param cols The number of columns
     * @return The string representation
     */
    private static String createMatrixString(float array[], int rows, int cols)
    {
        StringBuilder sb = new StringBuilder();
        for (int r=0; r<rows; r++)
        {
            for (int c=0; c<cols; c++)
            {
                sb.append(array[r + c * cols]);
                sb.append(", ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    
    /**
     * Create a string representation of the given array, as a matrix, 
     * interpreting it as a matrix that is stored in column-major order. The 
     * given array may be <code>null</code>. If it is not <code>null</code>,
     * then it must either have 3x3 elements or 4x4 elements. <br>
     * <br>
     * The individual elements of the matrix will be formatted (in an 
     * unspecified way) so that the matrix entries are aligned.
     * 
     * @param array The array
     * @return The string representation
     */
    public static String createFormattedMatrixString(float array[])
    {
        if (array == null)
        {
            return "null";
        }
        String format = "%10.5f ";
        if (array.length == 9)
        {
            return createFormattedMatrixString(array, 3, 3, format);
        }
        if (array.length == 16)
        {
            return createFormattedMatrixString(array, 4, 4, format);
        }
        return "WARNING: Not a matrix: "+Arrays.toString(array);
    }
    
    /**
     * Creates a string representation of the given matrix, which is given
     * in column-major order. The elements of the matrix will be formatted
     * with the given string.
     * 
     * @param array The array storing the matrix
     * @param rows The number of rows
     * @param cols The number of columns
     * @param format The format string
     * @return The string representation
     */
    private static String createFormattedMatrixString(
        float array[], int rows, int cols, String format)
    {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < rows; r++)
        {
            for (int c = 0; c < cols; c++)
            {
                sb.append(String.format(
                    Locale.ENGLISH, format, array[r + c * cols]));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    

    /**
     * Private constructor to prevent instantiation
     */
    private MathUtils()
    {
        // Private constructor to prevent instantiation
    }

}
