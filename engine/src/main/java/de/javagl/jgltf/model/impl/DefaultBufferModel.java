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

import java.nio.ByteBuffer;

import de.javagl.jgltf.model.BufferModel;
import de.javagl.jgltf.model.io.Buffers;

/**
 * Implementation of a {@link BufferModel}
 */
public final class DefaultBufferModel extends AbstractNamedModelElement
    implements BufferModel
{
    /**
     * The URI of the buffer data
     */
    private String uri;
    
    /**
     * The actual data of the buffer
     */
    private ByteBuffer bufferData;
    
    /**
     * Creates a new instance
     */
    public DefaultBufferModel()
    {
        // Default constructor
    }
    
    /**
     * Set the URI for the buffer data
     *  
     * @param uri The URI of the buffer data
     */
    public void setUri(String uri)
    {
        this.uri = uri;
    }

    /**
     * Set the data of this buffer
     * 
     * @param bufferData The buffer data
     */
    public void setBufferData(ByteBuffer bufferData)
    {
        this.bufferData = bufferData;
    }
    
    @Override
    public String getUri()
    {
        return uri;
    }

    @Override
    public int getByteLength()
    {
        return bufferData.capacity();
    }
    
    @Override
    public ByteBuffer getBufferData()
    {
        return Buffers.createSlice(bufferData);
    }
    
}
