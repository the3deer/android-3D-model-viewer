package org.andresoviedo.util.math;

import android.opengl.Matrix;

import java.util.Locale;

/**
 * Utility class to calculate 3D stuff
 *
 * @author andresoviedo
 */
public class Math3DUtils {

    /**
     * Calculate face normal
     *
     * @param v0
     * @param v1
     * @param v2
     * @return
     */
    public static float[] calculateNormal(float[] v0, float[] v1, float[] v2) {

        // calculate perpendicular vector to the face. That is to calculate the cross product of v1-v0 x v2-v0
        float[] va = new float[]{v1[0] - v0[0], v1[1] - v0[1], v1[2] - v0[2]};
        float[] vb = new float[]{v2[0] - v0[0], v2[1] - v0[1], v2[2] - v0[2]};
        float[] n = new float[]{va[1] * vb[2] - va[2] * vb[1], va[2] * vb[0] - va[0] * vb[2],
                va[0] * vb[1] - va[1] * vb[0]};
        float modul = Matrix.length(n[0], n[1], n[2]);
        return new float[]{n[0] / modul, n[1] / modul, n[2] / modul};
    }

    /**
     * Calculate the 2 vectors, that is a line (x1,y1,z1-x2,y2,z2} corresponding to the normal of the specified face.
     * The calculated line will be positioned exactly in the middle of the face
     *
     * @param v0 the first vector of the face
     * @param v1 the second vector of the face
     * @param v2 the third vector of the face
     * @return the 2 vectors (line) corresponding to the face normal
     */
    public static float[][] getNormalLine(float[] v0, float[] v1, float[] v2) {

        // calculate perpendicular vector to the face. That is to calculate the cross product of v1-v0 x v2-v0
        float[] va = new float[]{v1[0] - v0[0], v1[1] - v0[1], v1[2] - v0[2]};
        float[] vb = new float[]{v2[0] - v0[0], v2[1] - v0[1], v2[2] - v0[2]};
        float[] n = new float[]{va[1] * vb[2] - va[2] * vb[1], va[2] * vb[0] - va[0] * vb[2],
                va[0] * vb[1] - va[1] * vb[0]};
        float modul = Matrix.length(n[0], n[1], n[2]);
        float[] vn = new float[]{n[0] / modul, n[1] / modul, n[2] / modul};

       return getNormalLine2(v0, v1, v2, vn);
    }

    /**
     * Calculate the 2 vectors, that is a line (x1,y1,z1-x2,y2,z2} corresponding to the normal of the specified face.
     * The calculated line will be positioned exactly in the middle of the face
     *
     * @param v0 the first vector of the face
     * @param v1 the second vector of the face
     * @param v2 the third vector of the face
     * @return the 2 vectors (line) corresponding to the face normal
     */
    public static float[][] getNormalLine2(float[] v0, float[] v1, float[] v2, float[] normal) {

        // calculate center of the face
        final float[] faceCenter = calculateFaceCenter(v0, v1, v2);

        // scale normal proportional to triangle perimeter (or area)
        final float[] va = new float[]{v1[0] - v0[0], v1[1] - v0[1], v1[2] - v0[2]};
        final float[] vb = new float[]{v2[0] - v0[0], v2[1] - v0[1], v2[2] - v0[2]};
        final float[] vc = new float[]{v2[0] - v1[0], v2[1] - v1[1], v2[2] - v1[2]};
        final float scaleFactor = (length(va[0], va[1], va[2])
                        + length(vb[0],vb[1],vb[2])
                        + length(vc[0], vc[1], vc[2]))/3;

        // calculate 2nd vertex position
        float[] vn2 = new float[]{
                faceCenter[0] + normal[0] * scaleFactor
                , faceCenter[1] + normal[1] * scaleFactor
                , faceCenter[2] + normal[2] * scaleFactor};

        return new float[][]{faceCenter, vn2};
    }

    public static float[] calculateFaceCenter(float[] v0, float[] v1, float[] v2) {
        return new float[]{(v0[0] + v1[0] + v2[0]) / 3, (v0[1] + v1[1] + v2[1]) / 3, (v0[2] + v1[2] + v2[2]) / 3};
    }

    /**
     * Calculates the distance of the intersection between the specified ray and the target, or return -1 if the ray
     * doesn't intersect the target
     *
     * @param rayPoint1 where the ray starts
     * @param rayPoint2 where the ray ends
     * @param target    where is the object to intersect
     * @param precision the radius to test for intersection
     * @return the distance of intersection
     * @deprecated
     */
    public static float calculateDistanceOfIntersection(float[] rayPoint1, float[] rayPoint2, float[] target,
                                                        float precision) {
        float raySteps = 100f;
        float objHalfWidth = precision / 2;

        float length = Matrix.length(rayPoint2[0] - rayPoint1[0], rayPoint2[1] - rayPoint1[1],
                rayPoint2[2] - rayPoint1[2]);
        float lengthDiff = length / raySteps;

        float xDif = (rayPoint2[0] - rayPoint1[0]) / raySteps;
        float yDif = (rayPoint2[1] - rayPoint1[1]) / raySteps;
        float zDif = (rayPoint2[2] - rayPoint1[2]) / raySteps;

        for (int i = 0; i < raySteps; i++) {
            // @formatter:off
            if ((rayPoint1[0] + (xDif * i)) > target[0] - objHalfWidth
                    && (rayPoint1[0] + (xDif * i)) < target[0] + objHalfWidth
                    && (rayPoint1[1] + (yDif * i)) > target[1] - objHalfWidth
                    && (rayPoint1[1] + (yDif * i)) < target[1] + objHalfWidth
                    && (rayPoint1[2] + (zDif * i)) > target[2] - objHalfWidth
                    && (rayPoint1[2] + (zDif * i)) < target[2] + objHalfWidth) {
                // @formatter:on
                // Log.v(TouchController.TAG, "HIT: i[" + i + "] wz[" + (rayPoint1[2] + (zDif * i)) + "]");
                // return new Object[] { i * lengthDiff, new float[] { rayPoint1[0] + (xDif * i),
                // rayPoint1[1] + (yDif * i), rayPoint1[2] + (zDif * i) } };
                return i * lengthDiff;
            }
        }
        return -1;
    }

    /**
     * Substract 2 vertex: a-b
     *
     * @param a
     * @param b
     * @return a-b
     */
    public static float[] substract(float[] a, float[] b) {
        return new float[]{a[0] - b[0], a[1] - b[1], a[2] - b[2]};
    }

    /**
     * Divide 2 vertex: a/b
     *
     * @param a
     * @param b
     * @return a/b
     */
    public static float[] divide(float[] a, float[] b) {
        return new float[]{a[0] / b[0], a[1] / b[1], a[2] / b[2]};
    }

    /**
     * Divide vertex: a/b
     *
     * @param a
     * @param b
     * @return a/b
     */
    public static float[] divide(float[] a, float b) {
        return new float[]{a[0] / b, a[1] / b, a[2] / b};
    }

    /**
     * Get the min of both vertex
     *
     * @param a
     * @param b
     * @return min of both vertex
     */
    public static float[] min(float[] a, float[] b) {
        return new float[]{Math.min(a[0], b[0]), Math.min(a[1], b[1]), Math.min(a[2], b[2])};
    }

    /**
     * Get the max of both vertex
     *
     * @param a
     * @param b
     * @return max of both vertex
     */
    public static float[] max(float[] a, float[] b) {
        return new float[]{Math.max(a[0], b[0]), Math.max(a[1], b[1]), Math.max(a[2], b[2])};
    }

    /**
     * Normalize the specified vector
     *
     * @param a
     */
    public static void normalize(float[] a) {
        float length = Matrix.length(a[0], a[1], a[2]);
        a[0] = a[0] / length;
        a[1] = a[1] / length;
        a[2] = a[2] / length;
    }

    public static float[] crossProduct(float[] a, float[] b) {
        // AxB = (AyBz − AzBy, AzBx − AxBz, AxBy − AyBx)
        //(r)[0] = (a)[1] * (b)[2] - (b)[1] * (a)[2]; \
        //(r)[1] = (a)[2] * (b)[0] - (b)[2] * (a)[0]; \
        //(r)[2] = (a)[0] * (b)[1] - (b)[0] * (a)[1];
        float x = a[1] * b[2] - a[2] * b[1];
        float y = a[2] * b[0] - a[0] * b[2];
        float z = a[0] * b[1] - a[1] * b[0];
        return new float[]{x, y, z};
    }

    public static float[] crossProduct(float ax, float ay, float az, float bx, float by, float bz) {
        // AxB = (AyBz − AzBy, AzBx − AxBz, AxBy − AyBx)
        //(r)[0] = (a)[1] * (b)[2] - (b)[1] * (a)[2]; \
        //(r)[1] = (a)[2] * (b)[0] - (b)[2] * (a)[0]; \
        //(r)[2] = (a)[0] * (b)[1] - (b)[0] * (a)[1];
        float x = ay * bz - az * by;
        float y = az * bx - ax * bz;
        float z = ax * by - ay * bx;
        return new float[]{x, y, z};
    }

    public static float dotProduct(float[] a, float[] b) {
        // a1b1+a2b2+a3b3
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

    public static float[] multiply(float[] a, float t) {
        return new float[]{a[0] * t, a[1] * t, a[2] * t};
    }

    public static float[] add(float[] a, float[] b) {
        return new float[]{a[0] + b[0], a[1] + b[1], a[2] + b[2]};
    }

    /**
     * Matrices are 4 x 4 column-vector matrices stored in column-major order:
     * m[offset +  0] m[offset +  4] m[offset +  8] m[offset + 12]
     * m[offset +  1] m[offset +  5] m[offset +  9] m[offset + 13]
     * m[offset +  2] m[offset +  6] m[offset + 10] m[offset + 14]
     * m[offset +  3] m[offset +  7] m[offset + 11] m[offset + 15]
     *
     * @param matrix the matrix to stringify
     * @param indent the spaces to add at beginning
     * @return the string representation of the matrix
     */
    public static String toString(float[] matrix, int indent) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            ret.append("\n");
            for (int k = 0; k < indent; k++) {
                ret.append(" ");
            }
            for (int j = 0; j < 4; j++) {
                if (matrix[j * 4 + i] >= 0) {
                    ret.append("+");
                }
                ret.append(String.format(Locale.getDefault(), "%.3f", matrix[j * 4 + i]));
                ret.append("  ");
            }
        }
        return ret.toString();
    }

    public static float[] parseFloat(String[] rawData) {
        float[] matrixData = new float[rawData.length];
        for (int i = 0; i < rawData.length; i++) {
            matrixData[i] = Float.parseFloat(rawData[i]);
        }
        return matrixData;
    }

    /**
     * {@link Matrix}
     */
    public static void setRotateM(float[] rm, int rmOffset,
                                  float a, float x, float y, float z) {
        rm[rmOffset + 3] = 0;
        rm[rmOffset + 7] = 0;
        rm[rmOffset + 11]= 0;
        rm[rmOffset + 12]= 0;
        rm[rmOffset + 13]= 0;
        rm[rmOffset + 14]= 0;
        rm[rmOffset + 15]= 1;
        a *= (float) (Math.PI / 180.0f);
        float s = (float) Math.sin(a);
        float c = (float) Math.cos(a);
        if (1.0f == x && 0.0f == y && 0.0f == z) {
            rm[rmOffset + 5] = c;   rm[rmOffset + 10]= c;
            rm[rmOffset + 6] = s;   rm[rmOffset + 9] = -s;
            rm[rmOffset + 1] = 0;   rm[rmOffset + 2] = 0;
            rm[rmOffset + 4] = 0;   rm[rmOffset + 8] = 0;
            rm[rmOffset + 0] = 1;
        } else if (0.0f == x && 1.0f == y && 0.0f == z) {
            rm[rmOffset + 0] = c;   rm[rmOffset + 10]= c;
            rm[rmOffset + 8] = s;   rm[rmOffset + 2] = -s;
            rm[rmOffset + 1] = 0;   rm[rmOffset + 4] = 0;
            rm[rmOffset + 6] = 0;   rm[rmOffset + 9] = 0;
            rm[rmOffset + 5] = 1;
        } else if (0.0f == x && 0.0f == y && 1.0f == z) {
            rm[rmOffset + 0] = c;   rm[rmOffset + 5] = c;
            rm[rmOffset + 1] = s;   rm[rmOffset + 4] = -s;
            rm[rmOffset + 2] = 0;   rm[rmOffset + 6] = 0;
            rm[rmOffset + 8] = 0;   rm[rmOffset + 9] = 0;
            rm[rmOffset + 10]= 1;
        } else {
            float len = length(x, y, z);
            if (1.0f != len) {
                float recipLen = 1.0f / len;
                x *= recipLen;
                y *= recipLen;
                z *= recipLen;
            }
            float nc = 1.0f - c;
            float xy = x * y;
            float yz = y * z;
            float zx = z * x;
            float xs = x * s;
            float ys = y * s;
            float zs = z * s;
            rm[rmOffset +  0] = x*x*nc +  c;
            rm[rmOffset +  4] =  xy*nc - zs;
            rm[rmOffset +  8] =  zx*nc + ys;
            rm[rmOffset +  1] =  xy*nc + zs;
            rm[rmOffset +  5] = y*y*nc +  c;
            rm[rmOffset +  9] =  yz*nc - xs;
            rm[rmOffset +  2] =  zx*nc - ys;
            rm[rmOffset +  6] =  yz*nc + xs;
            rm[rmOffset + 10] = z*z*nc +  c;
        }
    }

    /**
     * {@link Matrix}
     */
    public static float length(float x, float y, float z) {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }
}
