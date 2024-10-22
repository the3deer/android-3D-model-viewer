package org.the3deer.util.math;

import android.opengl.Matrix;
import android.util.Log;

import org.the3deer.android_3d_model_engine.animation.JointTransform;
import org.the3deer.android_3d_model_engine.model.Constants;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Utility class to calculate 3D stuff
 *
 * @author andresoviedo
 */
public class Math3DUtils {

    public static final float IDENTITY_MATRIX[] = new float[16];
    public static final float VECTOR_UNIT_X[] = {1,0,0};
    public static final float VECTOR_UNIT_Y[] = {0,1,0};
    public static final float VECTOR_UNIT_Z[] = {0,0,1};

    static {
        Matrix.setIdentityM(IDENTITY_MATRIX, 0);
    }

    public static float[] initMatrixIfNull(float[] matrix) {
        if (matrix != null) return matrix;
        matrix = new float[16];
        Matrix.setIdentityM(matrix, 0);
        return matrix;
    }

    /**
     * Matrix - column major order:
     *
     *          lhs[0] lhs[4] lhs[8] lhs[12]      rhs[0] rhs[4] rhs[8] rhs[12]
     *   t   =  lhs[1] lhs[5] lhs[9] lhs[13]   x  rhs[1] ...
     *          lhs[2] lhs[6] lhs[10] lhs[14]
     *          lhs[3] lhs[7] lhs[11] lhs[15]
     *
     *   t[0]  t[4]  t[8]  t[12]
     *   t[1]
     *
     * @param lhs
     * @param rhs
     * @param dest
     */
    public static void multiplyMM(float[] dest, float[] lhs, float[] rhs)
    {
        // first column
        dest[0] = lhs[0] * rhs[0] + lhs[4] * rhs[1] + lhs[8] * rhs[2] + lhs[12] * rhs[3];
        dest[1] = lhs[1] * rhs[0] + lhs[5] * rhs[1] + lhs[9] * rhs[2] + lhs[13] * rhs[3];
        dest[2] = lhs[2] * rhs[0] + lhs[6] * rhs[1] + lhs[10] * rhs[2] + lhs[14] * rhs[3];
        dest[3] = lhs[3] * rhs[0] + lhs[7] * rhs[1] + lhs[11] * rhs[2] + lhs[15] * rhs[3];

        // second column
        dest[4] = lhs[0] * rhs[4] + lhs[4] * rhs[5] + lhs[8] * rhs[6] + lhs[12] * rhs[7];
        dest[5] = lhs[1] * rhs[4] + lhs[5] * rhs[5] + lhs[9] * rhs[6] + lhs[13] * rhs[7];
        dest[6] = lhs[2] * rhs[4] + lhs[6] * rhs[5] + lhs[10] * rhs[6] + lhs[14] * rhs[7];
        dest[7] = lhs[3] * rhs[4] + lhs[7] * rhs[5] + lhs[11] * rhs[6] + lhs[15] * rhs[7];

        // third column
        dest[8] = lhs[0] * rhs[8] + lhs[4] * rhs[9] + lhs[8] * rhs[10] + lhs[12] * rhs[11];
        dest[9] = lhs[1] * rhs[8] + lhs[5] * rhs[9] + lhs[9] * rhs[10] + lhs[13] * rhs[11];
        dest[10] = lhs[2] * rhs[8] + lhs[6] * rhs[9] + lhs[10] * rhs[10] + lhs[14] * rhs[11];
        dest[11] = lhs[3] * rhs[8] + lhs[7] * rhs[9] + lhs[11] * rhs[10] + lhs[15] * rhs[11];

        // forth column
        dest[12] = lhs[0] * rhs[12] + lhs[4] * rhs[13] + lhs[8] * rhs[14] + lhs[12] * rhs[15];
        dest[13] = lhs[1] * rhs[12] + lhs[5] * rhs[13] + lhs[9] * rhs[14] + lhs[13] * rhs[15];
        dest[14] = lhs[2] * rhs[12] + lhs[6] * rhs[13] + lhs[10] * rhs[14] + lhs[14] * rhs[15];
        dest[15] = lhs[3] * rhs[12] + lhs[7] * rhs[13] + lhs[11] * rhs[14] + lhs[15] * rhs[15];
    };

    /**
     * Calculate face normal
     * <p>
     * So for a triangle p1, p2, p3, if the vector U = p2 - p1 and the vector V = p3 - p1 then the normal N = U X V and can be calculated by:
     * </p>
     * <pre>
     * Nx = UyVz - UzVy
     * Ny = UzVx - UxVz
     * Nz = UxVy - UyVx
     * </pre>
     *
     * <a href="https://www.khronos.org/opengl/wiki/Calculating_a_Surface_Normal">Calculating_a_Surface_Normal</a>
     *
     * @param v0
     * @param v1
     * @param v2
     * @return
     */
    public static float[] calculateNormal(float[] v0, float[] v1, float[] v2) {

        // calculate perpendicular vector to the face. That is to calculate the cross product of v1-v0 x v2-v0
        double[] va = new double[]{v1[0] - v0[0], v1[1] - v0[1], v1[2] - v0[2]};
        double[] vb = new double[]{v2[0] - v0[0], v2[1] - v0[1], v2[2] - v0[2]};
        float[] vn = {(float) (va[1] * vb[2] - va[2] * vb[1]), (float) (va[2] * vb[0] - va[0] * vb[2]),
                (float) (va[0] * vb[1] - va[1] * vb[0])};

        if (length(vn) != 0) {
            return vn;
        } else {
            return calculateNormal_highPrecision(v0, v1, v2);
        }
    }

    /**
     * Calculate face normal using high precision maths
     * <p>
     * So for a triangle p1, p2, p3, if the vector U = p2 - p1 and the vector V = p3 - p1 then the normal N = U X V and can be calculated by:
     * </p>
     * <pre>
     * Nx = UyVz - UzVy
     * Ny = UzVx - UxVz
     * Nz = UxVy - UyVx
     * </pre>
     *
     * <a href="https://www.khronos.org/opengl/wiki/Calculating_a_Surface_Normal">Calculating_a_Surface_Normal</a>
     *
     * @param v0
     * @param v1
     * @param v2
     * @return
     */
    public static float[] calculateNormal_highPrecision(float[] v0, float[] v1, float[] v2) {

        // calculate the 2 vectors
        final float[] u = substract(v1, v0);
        final float[] v = substract(v2, v0);

        final BigDecimal[] u_ = new BigDecimal[]{
                new BigDecimal(Float.toString(u[0])),
                new BigDecimal(Float.toString(u[1])),
                new BigDecimal(Float.toString(u[2]))
        };

        final BigDecimal[] v_ = new BigDecimal[]{
                new BigDecimal(Float.toString(v[0])),
                new BigDecimal(Float.toString(v[1])),
                new BigDecimal(Float.toString(v[2]))
        };

        final BigDecimal[] n_ = new BigDecimal[]{
                u_[1].multiply(v_[2]).subtract(u_[2].multiply(v_[1])),
                u_[2].multiply(v_[0]).subtract(u_[0].multiply(v_[2])),
                u_[0].multiply(v_[1]).subtract(u_[1].multiply(v_[0]))
        };

        return new float[]{
                n_[0].floatValue(),
                n_[1].floatValue(),
                n_[2].floatValue()
        };
    }

    /**
     * Calculate the 2 vectors, that is a line (x1,y1,z1-x2,y2,z2} corresponding to the normal of the specified face.
     * The calculated line will be positioned exactly in the middle of the face
     *
     * @param v0    the first vector of the face
     * @param v1    the second vector of the face
     * @param v2    the third vector of the face
     * @param scale if <code>true</code> scale normal line according to triangle size (bigger triangle bigger line)
     * @return the 2 vectors (line) corresponding to the face normal
     */
    public static float[][] calculateNormalLine(float[] v0, float[] v1, float[] v2, boolean scale) {

        // calculate perpendicular vector to the face. That is to calculate the cross product of v1-v0 x v2-v0
        float[] va = new float[]{v1[0] - v0[0], v1[1] - v0[1], v1[2] - v0[2]};
        float[] vb = new float[]{v2[0] - v0[0], v2[1] - v0[1], v2[2] - v0[2]};
        float[] n = new float[]{va[1] * vb[2] - va[2] * vb[1], va[2] * vb[0] - va[0] * vb[2],
                va[0] * vb[1] - va[1] * vb[0]};
        float modul = Matrix.length(n[0], n[1], n[2]);
        float[] vn = new float[]{n[0] / modul, n[1] / modul, n[2] / modul};

        return getNormalLine(v0, v1, v2, vn, scale, 1);
    }

    /**
     * Calculate the 2 vectors, that is a line (x1,y1,z1-x2,y2,z2} corresponding to the normal of the specified face.
     * The calculated line will be positioned exactly in the middle of the face
     *
     * @param v0     the first vector of the face
     * @param v1     the second vector of the face
     * @param v2     the third vector of the face
     * @param normal the normal vector
     * @param scale  if <code>true</code> scale normal line according to triangle size (bigger triangle bigger line)
     * @return the 2 vectors (line) corresponding to the face normal
     */
    public static float[][] getNormalLine(float[] v0, float[] v1, float[] v2, float[] normal, boolean scale, float rescale) {

        // calculate center of the face
        final float[] faceCenter = calculateFaceCenter(v0, v1, v2);

        final float[] va = new float[]{v1[0] - v0[0], v1[1] - v0[1], v1[2] - v0[2]};
        final float[] vb = new float[]{v2[0] - v0[0], v2[1] - v0[1], v2[2] - v0[2]};
        final float[] vc = new float[]{v2[0] - v1[0], v2[1] - v1[1], v2[2] - v1[2]};

        // scale normal proportional to triangle perimeter (or area)
        final float scaleFactor = scale ? (length(va[0], va[1], va[2])
                + length(vb[0], vb[1], vb[2])
                + length(vc[0], vc[1], vc[2])) / 3 : 1;

        // calculate 2nd vertex position
        float[] vn2 = new float[]{
                faceCenter[0] + normal[0] * scaleFactor * rescale
                , faceCenter[1] + normal[1] * scaleFactor * rescale
                , faceCenter[2] + normal[2] * scaleFactor * rescale};

        return new float[][]{faceCenter, vn2};
    }

    /**
     * Calculate the 3 lines, that is a line (x1,y1,z1-x2,y2,z2} corresponding to the normal of the specified face.
     *
     * @param v0     the first vector of the face
     * @param v1     the second vector of the face
     * @param v2     the third vector of the face
     * @param normal the normal vector
     * @param scale  if <code>true</code> scale normal line according to triangle size (bigger triangle bigger line)
     * @return the 2 vectors (line) corresponding to the face normal
     */
    public static float[][] getNormalLines(float[] v0, float[] v1, float[] v2, float[] normal, boolean scale, float rescale) {

        final float[] va = new float[]{v1[0] - v0[0], v1[1] - v0[1], v1[2] - v0[2]};
        final float[] vb = new float[]{v2[0] - v0[0], v2[1] - v0[1], v2[2] - v0[2]};
        final float[] vc = new float[]{v2[0] - v1[0], v2[1] - v1[1], v2[2] - v1[2]};

        // scale normal proportional to triangle perimeter (or area)
        final float scaleFactor = scale ? (length(va[0], va[1], va[2])
                + length(vb[0], vb[1], vb[2])
                + length(vc[0], vc[1], vc[2])) / 3 : 1;

        // calculate 2nd vertex position
        float[] vn0 = new float[]{
                v0[0] + normal[0] * scaleFactor * rescale
                , v0[1] + normal[1] * scaleFactor * rescale
                , v0[2] + normal[2] * scaleFactor * rescale};

        float[] vn1 = new float[]{
                v1[0] + normal[0] * scaleFactor * rescale
                , v1[1] + normal[1] * scaleFactor * rescale
                , v1[2] + normal[2] * scaleFactor * rescale};

        float[] vn2 = new float[]{
                v2[0] + normal[0] * scaleFactor * rescale
                , v2[1] + normal[1] * scaleFactor * rescale
                , v2[2] + normal[2] * scaleFactor * rescale};

        return new float[][]{v0, vn0, v1, vn1, v2, vn2};
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
    public static void normalizeVector(float[] a) {
        float length = length(a);
        if (length == 0) {
//            throw new IllegalArgumentException("vector length is zero");
            return;
        }
        a[0] = a[0] / length;
        a[1] = a[1] / length;
        a[2] = a[2] / length;
    }

    public static float[] normalize2(float[] a) {
        float[] copy = a.clone();
        normalizeVector(copy);
        return copy;
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

    public static void mult(float[] v, float t) {
        for (int i = 0; i < v.length; i++) v[i] = v[i] * t;
    }

    public static float[] multiply(float[] a, float t) {
        return new float[]{a[0] * t, a[1] * t, a[2] * t};
    }

    /**
     * Adds 2 vectors
     * @param a vector 1
     * @param b vector 2
     * @return a new float the with result of the addition
     */
    public static float[] add(float[] a, float[] b) {
        return new float[]{a[0] + b[0], a[1] + b[1], a[2] + b[2]};
    }

    public static float[] mean(List<float[]> normals) {
        float[] normal_mean = normals.get(0);
        for (int i=1; i<normals.size() ; i++) {
            float[] normal_next = normals.get(i);
            normal_mean = mean(normal_mean, normal_next);
        }
        return normal_mean;
    }

    public static float[] mean(float[] a, float[] b) {
        float[] add = add(a, b);
        add[0] /= 2;
        add[1] /= 2;
        add[2] /= 2;
        return add;
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
                ret.append(String.format(Locale.getDefault(), "%+.3f", matrix[j * 4 + i]));
                ret.append("  ");
            }
        }
        return ret.toString();
    }

    public static String toString(float[] array) {
        StringBuilder ret = new StringBuilder("[");
        for (int i = 0; i < array.length; i++) {
            if (i > 0) ret.append(" ");
            ret.append(String.format(Locale.getDefault(), "%+.4f", array[i]));
        }
        ret.append("]");
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
        rm[rmOffset + 11] = 0;
        rm[rmOffset + 12] = 0;
        rm[rmOffset + 13] = 0;
        rm[rmOffset + 14] = 0;
        rm[rmOffset + 15] = 1;
        a *= (float) (Math.PI / 180.0f);
        float s = (float) Math.sin(a);
        float c = (float) Math.cos(a);
        if (1.0f == x && 0.0f == y && 0.0f == z) {
            rm[rmOffset + 5] = c;
            rm[rmOffset + 10] = c;
            rm[rmOffset + 6] = s;
            rm[rmOffset + 9] = -s;
            rm[rmOffset + 1] = 0;
            rm[rmOffset + 2] = 0;
            rm[rmOffset + 4] = 0;
            rm[rmOffset + 8] = 0;
            rm[rmOffset + 0] = 1;
        } else if (0.0f == x && 1.0f == y && 0.0f == z) {
            rm[rmOffset + 0] = c;
            rm[rmOffset + 10] = c;
            rm[rmOffset + 8] = s;
            rm[rmOffset + 2] = -s;
            rm[rmOffset + 1] = 0;
            rm[rmOffset + 4] = 0;
            rm[rmOffset + 6] = 0;
            rm[rmOffset + 9] = 0;
            rm[rmOffset + 5] = 1;
        } else if (0.0f == x && 0.0f == y && 1.0f == z) {
            rm[rmOffset + 0] = c;
            rm[rmOffset + 5] = c;
            rm[rmOffset + 1] = s;
            rm[rmOffset + 4] = -s;
            rm[rmOffset + 2] = 0;
            rm[rmOffset + 6] = 0;
            rm[rmOffset + 8] = 0;
            rm[rmOffset + 9] = 0;
            rm[rmOffset + 10] = 1;
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
            rm[rmOffset + 0] = x * x * nc + c;
            rm[rmOffset + 4] = xy * nc - zs;
            rm[rmOffset + 8] = zx * nc + ys;
            rm[rmOffset + 1] = xy * nc + zs;
            rm[rmOffset + 5] = y * y * nc + c;
            rm[rmOffset + 9] = yz * nc - xs;
            rm[rmOffset + 2] = zx * nc - ys;
            rm[rmOffset + 6] = yz * nc + xs;
            rm[rmOffset + 10] = z * z * nc + c;
        }
    }

    /**
     * {@link Matrix}
     */
    public static float length(float[] vector) {
        return length(vector[0], vector[1], vector[2]);
    }

    /**
     * {@link Matrix}
     */
    public static float length(float x, float y, float z) {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public static void interpolate(JointTransform result, JointTransform start, JointTransform end, float progression) {
        interpolate(result.getScale(), start.getScale(), end.getScale(), progression);
        interpolate(result.getLocation(), start.getLocation(), end.getLocation(), progression);
        /*interpolate(result.getRotation1(), start.getRotation1(), end.getRotation1(), progression);
        interpolate(result.getRotation2(), start.getRotation2(), end.getRotation2(), progression);
        interpolate(result.getRotation2Location(), start.getRotation2Location(), end.getRotation2Location(), progression);*/
        Quaternion.interpolate(result.getQRotation(), start.getQRotation(), end.getQRotation(), progression);
    }

    /**
     * Linearly interpolates between two translations based on a "progression"
     * value.
     *
     * @param start       - the start translation.
     * @param end         - the end translation.
     * @param progression - a value between 0 and 1 indicating how far to interpolate
     *                    between the two translations.
     * @return
     */
    public static void interpolate(Float result[], Float[] start, Float[] end, float progression) {
        if (start == null || end == null) return;
        for (int i = 0; i < result.length; i++) {
            result[i] = start[i] + (end[i] - start[i]) * progression;
        }
    }

    public static float[] negate(float[] vector) {
        float[] ret = new float[vector.length];
        for (int i = 0; i < vector.length; i++) ret[i] = -vector[i];
        return ret;
    }

    public static float[] mult(float[] vector1, float[] vector2) {
        float[] ret = new float[vector1.length];
        for (int i = 0; i < vector1.length; i++) ret[i] = vector1[i] * vector2[i];
        return ret;
    }

    public static Float[] scaleFromMatrix(float[] matrix) {

        // check
        if (matrix == null) return null;

        // |A| = a(ei − fh) − b(di − fg) + c(dh − eg)
        Float[] ret = new Float[3];
        ret[0] = (float) Math.sqrt(Math.pow(matrix[0], 2) + Math.pow(matrix[1], 2) + Math.pow(matrix[2], 2));
        ret[1] = (float) Math.sqrt(Math.pow(matrix[4], 2) + Math.pow(matrix[5], 2) + Math.pow(matrix[6], 2));
        ret[2] = (float) Math.sqrt(Math.pow(matrix[8], 2) + Math.pow(matrix[9], 2) + Math.pow(matrix[10], 2));
        if (determinant(matrix) < 0) {
            ret[1] = -ret[1];
        }
        return ret;
    }

    public static float[] scaleFromMatrixf(float[] matrix) {
        // |A| = a(ei − fh) − b(di − fg) + c(dh − eg)
        float[] ret = new float[3];
        ret[0] = (float) Math.sqrt(Math.pow(matrix[0], 2) + Math.pow(matrix[1], 2) + Math.pow(matrix[2], 2));
        ret[1] = (float) Math.sqrt(Math.pow(matrix[4], 2) + Math.pow(matrix[5], 2) + Math.pow(matrix[6], 2));
        ret[2] = (float) Math.sqrt(Math.pow(matrix[8], 2) + Math.pow(matrix[9], 2) + Math.pow(matrix[10], 2));
        if (determinant(matrix) < 0) {
            ret[1] = -ret[1];
        }
        return ret;
    }

    public static float determinant(float[] matrix) {
        float ret = 0;
        ret += matrix[0] * (matrix[5] * (matrix[10] * matrix[15] - matrix[11] * matrix[14]));
        ret -= matrix[1] * (matrix[6] * (matrix[11] * matrix[12] - matrix[8] * matrix[15]));
        ret += matrix[2] * (matrix[7] * (matrix[8] * matrix[13] - matrix[9] * matrix[12]));
        ret -= matrix[3] * (matrix[4] * (matrix[9] * matrix[14] - matrix[10] * matrix[13]));
        return ret;
    }

    public static float[] createRotationMatrixAroundVector(float angle, float x, float y, float z) {

        final float[] matrix = new float[16];
        final int offset = 0;

        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        float cos_1 = 1 - cos;

        // @formatter:off
        matrix[offset] = cos_1 * x * x + cos;
        matrix[offset + 1] = cos_1 * x * y - z * sin;
        matrix[offset + 2] = cos_1 * z * x + y * sin;
        matrix[offset + 3] = 0;
        matrix[offset + 4] = cos_1 * x * y + z * sin;
        matrix[offset + 5] = cos_1 * y * y + cos;
        matrix[offset + 6] = cos_1 * y * z - x * sin;
        matrix[offset + 7] = 0;
        matrix[offset + 8] = cos_1 * z * x - y * sin;
        matrix[offset + 9] = cos_1 * y * z + x * sin;
        matrix[offset + 10] = cos_1 * z * z + cos;
        matrix[offset + 11] = 0;
        matrix[offset + 12] = 0;
        matrix[offset + 13] = 0;
        matrix[offset + 14] = 0;
        matrix[offset + 15] = 1;

        // @formatter:on

        return matrix;
    }

    /**
     * Calculate the angle between the 2 specified vectors
     *
     * @param v1
     * @param v2
     * @return
     */
    public static double calculateAngleBetween(float[] v1, float[] v2){
        // Using acos
        //float[] v1n = normalize2(v1);
        //float[] v2n = normalize2(v2);

        // perp dot-product
        // https://stackoverflow.com/questions/2150050/finding-signed-angle-between-vectors
        //return Math.atan2( v1n[0]*v2n[1] - v1n[1]*v2n[0], v1n[0]*v2n[0] + v1n[1]*v2n[1] );
        return Math.atan2( v1[0]*v2[1] - v1[1]*v2[0], v1[0]*v2[0] + v1[1]*v2[1] );

    }

    public static void createRotationMatrixAroundVector(float[] matrix, int offset, double angle, float[] vector) {
        createRotationMatrixAroundVector(matrix, offset, angle, vector[0], vector[1], vector[2]);
    }

    /**
     *
     * @param matrix output matrix
     * @param offset output matrix offset
     * @param angle in radians
     * @param x rotation vector x
     * @param y rotation vector y
     * @param z rotation vector z
     */
    public static void createRotationMatrixAroundVector(float[] matrix, int offset, double angle, float x, float y,
                                                        float z) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        float cos_1 = 1 - cos;

        // @formatter:off
        matrix[offset] = cos_1 * x * x + cos;
        matrix[offset + 1] = cos_1 * x * y - z * sin;
        matrix[offset + 2] = cos_1 * z * x + y * sin;
        matrix[offset + 3] = 0;
        matrix[offset + 4] = cos_1 * x * y + z * sin;
        matrix[offset + 5] = cos_1 * y * y + cos;
        matrix[offset + 6] = cos_1 * y * z - x * sin;
        matrix[offset + 7] = 0;
        matrix[offset + 8] = cos_1 * z * x - y * sin;
        matrix[offset + 9] = cos_1 * y * z + x * sin;
        matrix[offset + 10] = cos_1 * z * z + cos;
        matrix[offset + 11] = 0;
        matrix[offset + 12] = 0;
        matrix[offset + 13] = 0;
        matrix[offset + 14] = 0;
        matrix[offset + 15] = 1;

        // @formatter:on
    }

    public static void multiplyMMV(float[] result, int retOffset, float[] matrix, int matOffet, float[] vector4Matrix,
                                   int vecOffset) {
        for (int i = 0; i < vector4Matrix.length / 4; i++) {
            Matrix.multiplyMV(result, retOffset + (i * 4), matrix, matOffet, vector4Matrix, vecOffset + (i * 4));
        }
    }

    public static void snapToGrid(float[] v) {
        final float[] TEST_VALUES = {
                Constants.UNIT_SIN_1, Constants.UNIT_SIN_2, Constants.UNIT_SIN_3, Constants.UNIT_SIN_5,
                Constants.UNIT_SIN_1 * Constants.UNIT, Constants.UNIT_SIN_2 * Constants.UNIT,
                Constants.UNIT_SIN_3 * Constants.UNIT, Constants.UNIT_SIN_5 * Constants.UNIT,
                Constants.UNIT_0, Constants.UNIT_1, Constants.UNIT_2, Constants.UNIT_3, Constants.UNIT_5,
                Constants.UNIT_0 * Constants.UNIT, Constants.UNIT_1 * Constants.UNIT, Constants.UNIT_2 * Constants.UNIT,
                Constants.UNIT_3 * Constants.UNIT, Constants.UNIT_5 * Constants.UNIT};
        for (int i = 0; i < v.length; i++) {
            for (int j = 0; j < TEST_VALUES.length; j++) {
                final float testValue = TEST_VALUES[j] ;
                if (v[i] >= (-testValue - Constants.SNAP_TO_GRID_THRESHOLD)
                        && v[i] <= (-testValue + Constants.SNAP_TO_GRID_THRESHOLD)) {
                    v[i] = -testValue;
                    break;
                } else if (v[i] >= (testValue - Constants.SNAP_TO_GRID_THRESHOLD)
                        && v[i] <= (testValue + Constants.SNAP_TO_GRID_THRESHOLD)) {
                    v[i] = testValue;
                    break;
                }
            }
        }
    }

    public static float[] to4d(float[] v3d) {
        return new float[]{v3d[0], v3d[1], v3d[2], 1};
    }

    public static boolean lineEquals(float[] v1, float[] v2, float[] v3, float[] v4) {
        return equals(v1,v3) && equals(v2,v4) || equals(v1,v4) && equals(v2,v3);
    }

    public static boolean equals(float[] v1, float[] v2) {
        if (v1 == v2) return true;
        if (v1 == null || v2 == null) return false;

        //if (v1 != null && v2 == null) return false;
        //if (v1 == null) return false;
        return v1[0] == v2[0] && v1[1] == v2[1] && v1[2] == v2[2];
    }

    public static void round(float[] v, int factor) {
        v[0] = (float)Math.round(v[0]*factor)/factor;
        v[1] = (float)Math.round(v[1]*factor)/factor;
        v[2] = (float)Math.round(v[2]*factor)/factor;
    }

    public static float dot(float[] a, float[] b){
        // a1b1+a2b2+a3b3
        return a[0]*b[0]+a[1]*b[1]+a[2]*b[2];
    }

    public static float[] cross(float[] a, float[] b){
        // AxB = (AyBz − AzBy, AzBx − AxBz, AxBy − AyBx)
        //(r)[0] = (a)[1] * (b)[2] - (b)[1] * (a)[2]; \
        //(r)[1] = (a)[2] * (b)[0] - (b)[2] * (a)[0]; \
        //(r)[2] = (a)[0] * (b)[1] - (b)[0] * (a)[1];
        float x = a[1]*b[2] - a[2]*b[1];
        float y = a[2]*b[0] - a[0]*b[2];
        float z = a[0]*b[1] - a[1]*b[0];
        return new float[]{x,y,z};
    }

    public static float[] getRotation(float[] v1, float[] v2, float[] v3, float[] newOrientation){
        // calculate polygon normal
        final float[] normal = calculateNormal(v1, v2, v3);
        Math3DUtils.normalizeVector(normal);

        // check if triangle is already facing the new orientation
        if (Math3DUtils.equals(normal, newOrientation)){
            return Math3DUtils.IDENTITY_MATRIX;
        }

        // calculate 2D rotation
        final float dot = Math3DUtils.dotProduct(newOrientation, normal);
        final float angle = (float) Math.acos(dot);
        final float[] cross = Math3DUtils.crossProduct(Constants.Z_NORMAL, normal);
        Math3DUtils.normalizeVector(cross);
        //cross[1] = 0;
        //cross[2] = 0;
        float[] rotationMatrix = Math3DUtils.createRotationMatrixAroundVector(angle, cross[0], cross[1], cross[2]);

        Log.i("HoleCutter", "normal: " + Arrays.toString(normal) + ", angle: " + angle + ", axis: " + Arrays.toString(cross));
        return rotationMatrix;
    }

    public static float[] extractTranslation(float[] matrix, float[] ret) {
        if (ret == null){
            ret = new float[3];
        }
        ret[0]=matrix[12]; ret[1]=matrix[13]; ret[2]=matrix[14];
        return ret;
    }

    public static Float[] extractTranslation2(float[] matrix, Float[] ret) {

        // check
        if (matrix == null) return null;

        if (ret == null){
            ret = new Float[3];
        }
        ret[0]=matrix[12]; ret[1]=matrix[13]; ret[2]=matrix[14];
        return ret;
    }

    public static float[] extractRotationMatrix(float[] matrix) {
        float[] s = new float[]{Matrix.length(matrix[0],matrix[4],matrix[8]),Matrix.length(matrix[1],matrix[5],matrix[9]),Matrix.length
                (matrix[2],matrix[6],matrix[10])};
        return new float[]{
                matrix[0]/s[0], matrix[1]/s[1], matrix[2]/s[2], 0,
                matrix[4]/s[0], matrix[5]/s[1], matrix[6]/s[2], 0,
                matrix[8]/s[0], matrix[9]/s[1], matrix[10]/s[2], 0,
                0,0,0,1
        };
    }

    public static float[] extractAxisAngle(float[] rotationMatrix){
        float[] ret = new float[4];
        ret[0] = rotationMatrix[9]-rotationMatrix[6];
        ret[1] = rotationMatrix[2]-rotationMatrix[8];
        ret[2] = rotationMatrix[4]-rotationMatrix[1];
        normalizeVector(ret);
        float trace = rotationMatrix[0]+rotationMatrix[5]+rotationMatrix[10];
        ret[3] = (float)Math.acos((trace-1)/2);
        return ret;
    }

    public static float[] normal(float[] v0, float[] v1, float[] v2) {

        // calculate perpendicular vector to the face. That is to calculate the cross product of v1-v0 x v2-v0
        float[] va = substract(v1,v0);
        float[] vb = substract(v2,v0);
        float nx = va[1] * vb[2] - va[2] * vb[1];
        float ny = va[2] * vb[0] - va[0] * vb[2];
        float nz = va[0] * vb[1] - va[1] * vb[0];
        float[] n = new float[]{nx, ny, nz};
        normalizeVector(n);
        return n;
    }

    public static float[] transform(float x, float y, float z, float[] matrix){
        float[] xyz1 = new float[]{x, y, z, 1};
        Matrix.multiplyMV(xyz1, 0, matrix, 0, xyz1, 0);
        Math3DUtils.round(xyz1, 100000);
        return new float[]{xyz1[0], xyz1[1], xyz1[2]};
    }

    public static float[] centroid(float[] v0, float[] v1, float[] v2) {
        return new float[]{(v0[0] + v1[0] + v2[0]) / 3, (v0[1] + v1[1] + v2[1]) / 3, (v0[2] + v1[2] + v2[2]) / 3};
    }

    public static boolean isCoplanar(float[] v1, float[] v2, float[] v3, float[] p1, float[] p2){
        float[] n1 = Math3DUtils.normal(v1, v2, v3);
        return dot(n1, substract(p2, p1)) == 0;
    }

    public static boolean coplanar(float[] n1, float[] n2) {
        if (Math3DUtils.equals(n1, n2)){
            return true;
        }
        invert(n1);
        return Math3DUtils.equals(n1, n2);
    }

    private static void invert(float[] v) {
        v[0] = -v[0];
        v[1] = -v[1];
        v[2] = -v[2];
    }

    public static boolean hasAreaZero(float[] v1, float[] v2, float[] v3){
        if (equals(v1,v2) || equals(v1,v3) || equals(v2,v3)){
            return true;
        }

        float[] v12 = substract(v2,v1);
        float[] v23 = substract(v3,v2);
        float[] v31 = substract(v1,v3);
        float l12 = length(v12);
        float l23 = length(v23);
        float l31 = length(v31);
        float area;
        if (l12 >= l23 && l12 >= l31){
            area = l12-l23-l31;
        } else if (l23 >= l12 && l23 >= l31){
            area = l23-l12-l31;
        } else if (l31 >= l12 && l31 >= l23){
            area = l31-l12-l23;
        } else {
            // equilateral triangle have area
            return false;
        }
        return Math.abs(area)<0.001f;
    }

    public static boolean hasLine(float[] v1, float[] v2, float[] v3, float[] p1, float[] p2){
        return lineEquals(v1, v2, p1, p2) || lineEquals(v2, v3, p1, p2) || lineEquals(v3, v1, p1, p2);
    }

    public static boolean isZero(float[] v){
        return v[0] == 0 && v[1] == 0 && v[2] == 0;
    }

    public static boolean isCollinear(float[] v1, float[] v2, float[] v3){
        // v1-v2-p1 are collinear?
        float[] v2v1 = substract(v2,v1);
        float[] v3v1 = substract(v3,v1);
        return isZero(cross(v2v1, v3v1));
    }

    public static boolean areCollinear(float[] v1, float[] v2, float[] v3, float[] v4){
        return isCollinear(v1,v2,v3) && isCollinear(v1,v2,v4);
    }

    public static boolean equals(float[] v1, float[] v2, float factor) {
        return v1 == v2 || v1[0]*factor/factor == v2[0]*factor/factor  && v1[1]*factor/factor  == v2[1]*factor/factor
                && v1[2]*factor/factor  == v2[2]*factor/factor ;
    }
}

