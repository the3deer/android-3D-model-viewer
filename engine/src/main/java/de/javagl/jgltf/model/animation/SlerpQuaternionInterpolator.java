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
package de.javagl.jgltf.model.animation;

/**
 * An {@link Interpolator} that performs a spherical linear interpolation
 * (SLERP) between quaternions, given as arrays of length 4
 */
class SlerpQuaternionInterpolator implements Interpolator
{

    @Override
    public void interpolate(float[] a, float[] b, float alpha, float[] result)
    {
        // Adapted from javax.vecmath.Quat4f
        float ax = a[0];
        float ay = a[1];
        float az = a[2];
        float aw = a[3];
        float bx = b[0];
        float by = b[1];
        float bz = b[2];
        float bw = b[3];

        float dot = ax * bx + ay * by + az * bz + aw * bw;
        if (dot < 0)
        {
            bx = -bx;
            by = -by;
            bz = -bz;
            bw = -bw;
            dot = -dot;
        }
        float epsilon = 1e-6f;
        float s0, s1;
        if ((1.0 - dot) > epsilon)
        {
            float omega = (float)Math.acos(dot);
            float invSinOmega = 1.0f / (float)Math.sin(omega);
            s0 = (float)Math.sin((1.0 - alpha) * omega) * invSinOmega;
            s1 = (float)Math.sin(alpha * omega) * invSinOmega;
        } 
        else
        {
            s0 = 1.0f - alpha;
            s1 = alpha;
        }
        float rx = s0 * ax + s1 * bx;
        float ry = s0 * ay + s1 * by;
        float rz = s0 * az + s1 * bz;
        float rw = s0 * aw + s1 * bw;
        result[0] = rx;
        result[1] = ry;
        result[2] = rz;
        result[3] = rw;
    }

}
