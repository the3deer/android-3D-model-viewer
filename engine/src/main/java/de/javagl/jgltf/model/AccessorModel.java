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

/**
 * Interface for a data accessor
 */
public interface AccessorModel extends NamedModelElement
{
    /**
     * Returns the {@link BufferViewModel} for the data that this accessor
     * provides access to. Typed access to the data is possible by obtaining
     * the {@link AccessorData} using {@link #getAccessorData()}.
     * 
     * @return The {@link BufferViewModel}
     */
    BufferViewModel getBufferViewModel();
 
    /**
     * Returns the component type of this accessor, as a GL constant. For
     * example, <code>GL_FLOAT</code> or <code>GL_UNSIGNED_SHORT</code>
     * 
     * @return The component type
     */
    int getComponentType();
    
    /**
     * Returns the data type of the components of this accessor. For example,
     * <code>float.class</code> or <code>short.class</code>.
     * 
     * @return The component data type
     */
    Class<?> getComponentDataType();
    
    /**
     * Returns whether this accessor contains normalized data
     * 
     * @return Whether this accessor contains normalized data
     */
    boolean isNormalized();
    
    /**
     * Returns the size of one component, in bytes
     * 
     * @return The component size, in bytes
     */
    int getComponentSizeInBytes();
    
    /**
     * Returns the size of one element, in bytes.
     * 
     * This does <b>not</b> include any padding that may have to be inserted
     * after the columns of certain matrix types (see section
     * 3.6.2.4. "Data Alignment"). 
     * 
     * To obtain the padded size of the elements, 
     * {@link #getPaddedElementSizeInBytes()} can be used. 
     * 
     * @return The element size, in bytes
     */
    int getElementSizeInBytes();
    
    /**
     * Obtain the padded size of one element, in bytes.
     * 
     * This <b>does</b> include any padding that may have to be inserted
     * after the columns of certain matrix types (see section
     * 3.6.2.4. "Data Alignment"). 
     * 
     * For example, for a MAT2 with BYTE components, this will return 8,
     * which is equivalent to calling 
     * <pre><code>
     * ElementType elementType = accessorModel.getElementType();
     * int componentType = accessorModel.getComponentType(); 
     * int sizeWithPadding = elementType.getByteStride(componentType);
     * </code></pre>
     * 
     * See {@link ElementType#getByteStride(int)}.
     * 
     * @return The padded element size, in bytes
     */
    int getPaddedElementSizeInBytes();
    
    /**
     * Returns the byte offset of this accessor referring to its 
     * {@link BufferViewModel}
     * 
     * @return The byte offset
     */
    int getByteOffset();
    
    /**
     * Returns the number of elements that this accessor provides access to
     * 
     * @return The number of elements
     */
    int getCount();
    
    /**
     * Returns the {@link ElementType} that this accessor provides access to
     * 
     * @return The {@link ElementType}
     */
    ElementType getElementType();
    
    /**
     * Returns the byte stride between the starts of two consecutive elements
     * of this accessor.
     * 
     * If this is 0, then the elements are tightly packed. This means that
     * the byte stride is equal to the {@link #getElementSizeInBytes() element 
     * size}. Note that for glTF 2.0, the byte stride for vertex attributes
     * must be a multiple 4. Callers must check whether the returned value
     * is 0 or not a multiple of 4 accordingly.
     * 
     * @return The byte stride
     */
    int getByteStride();
    
    /**
     * Returns the {@link AccessorData} for this accessor. The exact type
     * of the returned {@link AccessorData} object will depend on the 
     * {@link #getComponentDataType() component data type}. It will be
     * {@link AccessorByteData}, {@link AccessorShortData}, 
     * {@link AccessorIntData} or {@link AccessorFloatData} for a component
     * data type of <code>byte.class</code>, <code>short.class</code>, 
     * <code>int.class</code> or <code>float.class</code>, respectively,
     * and can be safely cast to the respective type.   
     * 
     * @return The {@link AccessorData}
     */
    AccessorData getAccessorData();
    
    /**
     * Returns the minimum components of the {@link AccessorData}. The 
     * returned array will be a clone of the array that is stored internally,
     * and have a length that matches the {@link ElementType#getNumComponents()
     * number of components per element}.
     *  
     * @return The minimum components
     */
    Number[] getMin();
    
    /**
     * Returns the maximum components of the {@link AccessorData}. The 
     * returned array will be a clone of the array that is stored internally,
     * and have a length that matches the {@link ElementType#getNumComponents()
     * number of components per element}.
     *  
     * @return The maximum components
     */
    Number[] getMax();
}
