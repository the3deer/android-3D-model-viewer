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

/**
 * Methods to convert primitive arrays to arrays of Number objects
 */
class NumberArrays
{
    /**
     * Convert the given array into a Number array
     * 
     * @param array The array
     * @return The result
     */
    static Number[] asNumbers(int array[])
    {
        Number result[] = new Number[array.length];
        for (int i = 0; i < array.length; i++)
        {
            result[i] = array[i];
        }
        return result;
    }    

    /**
     * Convert the given array into a Number array
     * 
     * @param array The array
     * @return The result
     */
    static Number[] asNumbers(long array[])
    {
        Number result[] = new Number[array.length];
        for (int i = 0; i < array.length; i++)
        {
            result[i] = array[i];
        }
        return result;
    }
    
    /**
     * Convert the given array into a Number array
     * 
     * @param array The array
     * @return The result
     */
    static Number[] asNumbers(float array[])
    {
        Number result[] = new Number[array.length];
        for (int i = 0; i < array.length; i++)
        {
            result[i] = array[i];
        }
        return result;
    }
    

    /**
     * Private constructor to prevent instantiation
     */
    private NumberArrays()
    {
        // Private constructor to prevent instantiation
    }
}
