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

import de.javagl.jgltf.model.BufferViewModel;
import de.javagl.jgltf.model.ImageModel;
import de.javagl.jgltf.model.io.Buffers;

/**
 * Implementation of a {@link ImageModel}
 */
public class DefaultImageModel extends AbstractNamedModelElement
    implements ImageModel
{
    /**
     * The URI of the image
     */
    private String uri;
    
    /**
     * The MIME type of the image data in the buffer view model
     */
    private String mimeType;
    
    /**
     * The {@link BufferViewModel}
     */
    private BufferViewModel bufferViewModel;
    
    /**
     * The image data
     */
    private ByteBuffer imageData;
    
    /**
     * Creates a new instance
     */
    public DefaultImageModel()
    {
        // Default constructor
    }
    
    /**
     * Set the URI
     * 
     * @param uri The URI
     */
    public void setUri(String uri)
    {
        this.uri = uri;
    }
    
    /**
     * Set the MIME type
     * 
     * @param mimeType The MIME type
     */
    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }

    /**
     * Set the {@link BufferViewModel} 
     * 
     * @param bufferViewModel The {@link BufferViewModel}
     */
    public void setBufferViewModel(BufferViewModel bufferViewModel)
    {
        this.bufferViewModel = bufferViewModel;
    }
    
    /**
     * Set the image data
     * 
     * @param imageData The image data
     */
    public void setImageData(ByteBuffer imageData)
    {
        this.imageData = imageData;
    }
    
    @Override
    public String getUri()
    {
        return uri;
    }
    
    @Override
    public String getMimeType()
    {
        return mimeType;
    }
    
    @Override
    public BufferViewModel getBufferViewModel()
    {
        return bufferViewModel;
    }
    
    @Override
    public ByteBuffer getImageData()
    {
        if (imageData == null)
        {
            return bufferViewModel.getBufferViewData();
        }
        return Buffers.createSlice(imageData);
    }

    
}
