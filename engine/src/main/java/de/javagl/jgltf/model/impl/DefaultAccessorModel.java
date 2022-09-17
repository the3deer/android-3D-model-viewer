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
package de.javagl.jgltf.model.impl;

import de.javagl.jgltf.model.AccessorData;
import de.javagl.jgltf.model.AccessorDatas;
import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.Accessors;
import de.javagl.jgltf.model.BufferViewModel;
import de.javagl.jgltf.model.ElementType;

/**
 * Implementation of an {@link AccessorModel}
 */
public final class DefaultAccessorModel extends AbstractNamedModelElement
    implements AccessorModel
{
    /**
     * The component type, as a GL constant
     */
    private final int componentType;
    
    /**
     * Whether the accessor is normalized
     */
    private boolean normalized;
    
    /**
     * The offset in bytes, referring to the buffer view
     */
    private int byteOffset;
    
    /**
     * The {@link BufferViewModel} for this model
     */
    private BufferViewModel bufferViewModel;
    
    /**
     * The {@link ElementType} of this accessor
     */
    private final ElementType elementType;
    
    /**
     * The number of elements
     */
    private final int count;
    
    /**
     * The stride between the start of one element and the next
     */
    private int byteStride;
    
    /**
     * The {@link AccessorData}
     */
    private AccessorData accessorData;
    
    /**
     * The minimum components
     */
    private Number[] max;
    
    /**
     * The maximum components
     */
    private Number[] min;
    
    /**
     * Creates a new instance
     * 
     * @param componentType The component type GL constant
     * @param count The number of elements
     * @param elementType The element type
     */
    public DefaultAccessorModel(
        int componentType,
        int count, 
        ElementType elementType)
    {
        this.componentType = componentType;
        this.count = count;
        this.elementType = elementType;
        this.byteStride = elementType.getByteStride(componentType);
    }
    
    /**
     * Set the {@link BufferViewModel} for this model
     * 
     * @param bufferViewModel The {@link BufferViewModel}
     */
    public void setBufferViewModel(BufferViewModel bufferViewModel)
    {
        this.bufferViewModel = bufferViewModel;
    }
    
    /**
     * Set the byte offset, referring to the {@link BufferViewModel}
     * 
     * @param byteOffset The byte offset
     */
    public void setByteOffset(int byteOffset)
    {
        this.byteOffset = byteOffset;
    }
    
    /**
     * Set the byte stride, indicating the number of bytes between the start
     * of one element and the start of the next element.
     * 
     * @param byteStride The byte stride
     */
    public void setByteStride(int byteStride)
    {
        this.byteStride = byteStride;
    }

    @Override
    public BufferViewModel getBufferViewModel()
    {
        return bufferViewModel;
    }
    
    @Override
    public int getComponentType()
    {
        return componentType;
    }
    
    @Override
    public Class<?> getComponentDataType()
    {
        return Accessors.getDataTypeForAccessorComponentType(
            getComponentType());
    }
    
    @Override
    public boolean isNormalized()
    {
        return normalized;
    }
    
    /**
     * Set whether the underlying data is normalized
     * 
     * @param normalized Whether the underlying data is normalized 
     */
    public void setNormalized(boolean normalized)
    {
        this.normalized = normalized;
    }
    
    @Override
    public int getComponentSizeInBytes()
    {
        return Accessors.getNumBytesForAccessorComponentType(componentType);
    }
    
    @Override
    public int getElementSizeInBytes()
    {
        return elementType.getNumComponents() * getComponentSizeInBytes();
    }
    
    @Override
    public int getPaddedElementSizeInBytes()
    {
        return elementType.getByteStride(componentType);
    }
    
    @Override
    public int getByteOffset()
    {
        return byteOffset;
    }
    
    @Override
    public int getCount()
    {
        return count;
    }
    
    @Override
    public ElementType getElementType()
    {
        return elementType;
    }
    
    @Override
    public int getByteStride()
    {
        return byteStride;
    }
    
    /**
     * Set the {@link AccessorData} for this accessor
     * 
     * @param accessorData The {@link AccessorData}
     */
    public void setAccessorData(AccessorData accessorData)
    {
        this.accessorData = accessorData;
    }
    
    @Override
    public AccessorData getAccessorData()
    {
        return accessorData;
    }
    
    
    @Override
    public Number[] getMin()
    {
        if (min == null)
        {
            min = AccessorDatas.computeMin(getAccessorData());
        }
        return min.clone();
    }
    
    @Override
    public Number[] getMax()
    {
        if (max == null)
        {
            max = AccessorDatas.computeMax(getAccessorData());
        }
        return max.clone();
    }
    
}
