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
 * Interface for a buffer view, which represents a slice of a 
 * {@link BufferModel}
 */
public interface BufferViewModel extends NamedModelElement
{
    /**
     * Return the actual data that this view stands for. This will be a new
     * slice of the buffer of the data that is stored internally. Changes 
     * in the position or limit of this buffer will not affect this model
     *  
     * @return The buffer view data
     */
    ByteBuffer getBufferViewData();

    /**
     * Returns the {@link BufferModel} that this view refers to
     * 
     * @return The {@link BufferModel}
     */
    BufferModel getBufferModel();
    
    /**
     * Returns the offset of this view referring to the buffer
     * 
     * @return The offset, in bytes
     */
    int getByteOffset();
    
    /**
     * Returns the length of this view, in bytes
     * 
     * @return The length, in bytes
     */
    int getByteLength();
    
    /**
     * Returns the stride between two consecutive elements of this buffer view,
     * in bytes. If this is <code>null</code>, then the elements are tightly
     * packed.
     * 
     * @return The stride, in bytes
     */
    Integer getByteStride();
    
    /**
     * Returns the (optional) target that this buffer should be bound to.
     * If this is not <code>null</code>, then it will be the GL constant for 
     * <code>GL_ARRAY_BUFFER</code> or <code>GL_ELEMENT_ARRAY_BUFFER</code>. 
     * 
     * @return The target, or <code>null</code>
     */
    Integer getTarget();
}