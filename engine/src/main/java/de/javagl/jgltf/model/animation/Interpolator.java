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
 * Package-private interface for classes that can interpolate between 
 * (equal-length) arrays of <code>float</code> values
 */
interface Interpolator
{
    /**
     * Interpolate between <code>a</code> and <code>b</code>, based on 
     * the given alpha value (that is usually in [0,1]), and place the
     * results in the given result array. None of the given arrays may
     * be <code>null</code>, and they must all have the same length.
     * 
     * @param a The first array
     * @param b The second array
     * @param alpha The interpolation value
     * @param result The array that will store the result
     * @throws NullPointerException If any argument is <code>null</code>
     * @throws IndexOutOfBoundsException May be thrown if the arrays do not 
     * have the same length
     */
    void interpolate(float a[], float b[], float alpha, float result[]);
}
