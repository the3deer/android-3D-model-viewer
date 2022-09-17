/*
 * www.javagl.de - JglTF
 *
 * Copyright 2015-2017 Marco Hutter - http://www.javagl.de
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

import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Utility methods to create Supplier instances
 */
public class Suppliers
{
    /**
     * Create a supplier of a 4x4 matrix that is computed by applying 
     * the given computer to the given object and a 16-element array.<br>
     * <br>
     * If the given object is <code>null</code>, then the identity 
     * matrix will be supplied.<br>
     * <br>
     * Note: The supplier MAY always return the same array instance.
     * Callers MUST NOT store or modify the returned array.
     * 
     * @param <T> The object type
     * 
     * @param object The object
     * @param computer The computer function
     * @return The supplier
     */
    public static <T> Supplier<float[]> createTransformSupplier(
        T object, BiConsumer<T, float[]> computer)
    {
        float transform[] = new float[16];
        if (object == null)
        {
            return () -> 
            {
                MathUtils.setIdentity4x4(transform);
                return transform;
            };
        }
        return () ->
        {
            computer.accept(object, transform);
            return transform;
        };
    }

    
    /**
     * Private constructor to prevent instantiation
     */
    private Suppliers()
    {
        // Private constructor to prevent instantiation
    }
}
