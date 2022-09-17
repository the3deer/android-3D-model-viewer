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

import java.nio.ByteBuffer;

/**
 * Interface for classes that provide typed access to raw accessor data.
 * The exact type of the data (and thus, the implementing class) is 
 * defined by the {@link #getComponentType() component type}:<br>
 * <ul>
 *   <li>For <code>byte.class</code>, the implementation is an 
 *   {@link AccessorByteData}</li>
 *   <li>For <code>short.class</code>, the implementation is an 
 *   {@link AccessorShortData}</li>
 *   <li>For <code>int.class</code>, the implementation is an 
 *   {@link AccessorIntData}</li>
 *   <li>For <code>float.class</code>, the implementation is an 
 *   {@link AccessorFloatData}</li>
 * </ul>
 */
public interface AccessorData
{
    /**
     * Returns the type of the components that this class provides access to.
     * This will usually be a primitive type, like <code>float.class</code>
     * or <code>short.class</code>.
     * 
     * @return The component type
     */
    Class<?> getComponentType();
    
    /**
     * Returns the number of elements in this data (for example, the number
     * of 3D vectors)
     * 
     * @return The number of elements
     */
    int getNumElements();

    /**
     * Returns the number of components per element (for example, 3 if the
     * elements are 3D vectors)
     * 
     * @return The number of components per element
     */
    int getNumComponentsPerElement();

    /**
     * Returns the total number of components (that is, the number of elements
     * multiplied with the number of components per element)
     * 
     * @return The total number of components
     */
    int getTotalNumComponents();

    /**
     * Creates a new, direct byte buffer (with native byte order) that
     * contains the data for the accessor, in a compact form,
     * without any offset, and without any additional stride (that is,
     * all elements will be tightly packed).  
     * 
     * @return The byte buffer
     */
    ByteBuffer createByteBuffer();

}