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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Locale;

/**
 * A class for accessing the data that is described by an accessor.
 * It allows accessing the byte buffer of the buffer view of the
 * accessor, depending on the accessor parameters.<br>
 * <br> 
 * This data consists of several elements (for example, 3D float vectors),
 * which consist of several components (for example, the 3 float values).  
 */
public final class AccessorFloatData 
    extends AbstractAccessorData
    implements AccessorData
{
    /**
     * Creates a new instance for accessing the data in the given 
     * byte buffer, according to the rules described by the given
     * accessor parameters.
     * @param componentType The component type
     * @param bufferViewByteBuffer The byte buffer of the buffer view
     * @param byteOffset The byte offset in the buffer view 
     * @param numElements The number of elements
     * @param elementType The {@link ElementType}
     * @param byteStride The byte stride between two elements. If this
     * is <code>null</code> or <code>0</code>, then the stride will
     * be the size of one element.
     * 
     * @throws NullPointerException If the bufferViewByteBuffer is 
     * <code>null</code>
     * @throws IllegalArgumentException If the component type is not 
     * <code>GL_FLOAT</code>
     * @throws IllegalArgumentException If the given byte buffer does not
     * have a sufficient capacity to provide the data for the accessor 
     */
    public AccessorFloatData(int componentType,
        ByteBuffer bufferViewByteBuffer, int byteOffset, int numElements,
        ElementType elementType, Integer byteStride)
    {
        super(componentType, float.class, bufferViewByteBuffer, byteOffset, 
            numElements, elementType, Float.BYTES, byteStride);
        AccessorDatas.validateFloatType(componentType);

        int numBytesPerElement = 
            getNumComponentsPerElement() * getNumBytesPerComponent();
        AccessorDatas.validateCapacity(byteOffset, getNumElements(), 
            numBytesPerElement, getByteStridePerElement(), 
            bufferViewByteBuffer.capacity());
    }
    
    /**
     * Returns the value of the specified component of the specified element
     * 
     * @param elementIndex The element index
     * @param componentIndex The component index
     * @return The value
     * @throws IndexOutOfBoundsException If the given indices cause the
     * underlying buffer to be accessed out of bounds
     */
    public float get(int elementIndex, int componentIndex)
    {
        int byteIndex = getByteIndex(elementIndex, componentIndex);
        return getBufferViewByteBuffer().getFloat(byteIndex);
    }
    
    /**
     * Returns the value of the specified component
     * 
     * @param globalComponentIndex The global component index
     * @return The value
     * @throws IndexOutOfBoundsException If the given index causes the
     * underlying buffer to be accessed out of bounds
     */
    public float get(int globalComponentIndex)
    {
        int elementIndex = 
            globalComponentIndex / getNumComponentsPerElement();
        int componentIndex = 
            globalComponentIndex % getNumComponentsPerElement();
        return get(elementIndex, componentIndex);
    }
    
    /**
     * Set the value of the specified component of the specified element
     * 
     * @param elementIndex The element index
     * @param componentIndex The component index
     * @param value The value
     * @throws IndexOutOfBoundsException If the given indices cause the
     * underlying buffer to be accessed out of bounds
     */
    public void set(int elementIndex, int componentIndex, float value)
    {
        int byteIndex = getByteIndex(elementIndex, componentIndex);
        getBufferViewByteBuffer().putFloat(byteIndex, value);
    }
    
    /**
     * Set the value of the specified component
     * 
     * @param globalComponentIndex The global component index
     * @param value The value
     * @throws IndexOutOfBoundsException If the given index causes the
     * underlying buffer to be accessed out of bounds
     */
    public void set(int globalComponentIndex, float value)
    {
        int elementIndex = 
            globalComponentIndex / getNumComponentsPerElement();
        int componentIndex = 
            globalComponentIndex % getNumComponentsPerElement();
        set(elementIndex, componentIndex, value);
    }
    
    
    /**
     * Returns an array containing the minimum component values of all elements 
     * of this accessor data. This will be an array whose length is the 
     * {@link #getNumComponentsPerElement() number of components per element}.
     * 
     * @return The minimum values
     */
    public float[] computeMin()
    {
        float result[] = new float[getNumComponentsPerElement()];
        Arrays.fill(result, Float.MAX_VALUE);
        for (int e = 0; e < getNumElements(); e++)
        {
            for (int c = 0; c < getNumComponentsPerElement(); c++)
            {
                result[c] = Math.min(result[c], get(e, c));
            }
        }
        return result;
    }

    /**
     * Returns an array containing the maximum component values of all elements 
     * of this accessor data. This will be an array whose length is the 
     * {@link #getNumComponentsPerElement() number of components per element}.
     * 
     * @return The minimum values
     */
    public float[] computeMax()
    {
        float result[] = new float[getNumComponentsPerElement()];
        Arrays.fill(result, -Float.MAX_VALUE);
        for (int e = 0; e < getNumElements(); e++)
        {
            for (int c = 0; c < getNumComponentsPerElement(); c++)
            {
                result[c] = Math.max(result[c], get(e, c));
            }
        }
        return result;
    }
    
    @Override
    public ByteBuffer createByteBuffer()
    {
        int totalNumComponents = getTotalNumComponents();
        int totalBytes = totalNumComponents * getNumBytesPerComponent();
        ByteBuffer result = ByteBuffer.allocateDirect(totalBytes)
            .order(ByteOrder.nativeOrder());
        for (int i=0; i<totalNumComponents; i++)
        {
            float component = get(i);
            result.putFloat(component);
        }
        result.position(0);
        return result;
    }
    
    /**
     * Creates a (potentially large!) string representation of the data
     * 
     * @param locale The locale used for number formatting
     * @param format The number format string
     * @param elementsPerRow The number of elements per row. If this
     * is not greater than 0, then all elements will be in a single row.
     * @return The data string
     */
    public String createString(
        Locale locale, String format, int elementsPerRow)
    {
        StringBuilder sb = new StringBuilder();
        int nc = getNumComponentsPerElement();
        sb.append("[");
        for (int e = 0; e < getNumElements(); e++)
        {
            if (e > 0)
            {
                sb.append(", ");
                if (elementsPerRow > 0 && (e % elementsPerRow) == 0)
                {
                    sb.append("\n ");
                }
            }
            if (nc > 1)
            {
                sb.append("(");
            }
            for (int c = 0; c < nc; c++)
            {
                if (c > 0)
                {
                    sb.append(", ");
                }
                float component = get(e, c);
                sb.append(String.format(locale, format, component));
            }
            if (nc > 1)
            {
                sb.append(")");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
}