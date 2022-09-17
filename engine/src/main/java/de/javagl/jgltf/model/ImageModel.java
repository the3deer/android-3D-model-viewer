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
 * Interface for an image in a glTF asset
 */
public interface ImageModel extends NamedModelElement
{
    /**
     * Returns the URI of the image data (optional)
     * 
     * @return The URI
     */
    String getUri();
    
    /**
     * Returns the MIME type of the image data that is contained in 
     * the buffer view
     * 
     * @return The MIME type
     */
    String getMimeType();
    
    /**
     * Returns the (optional) {@link BufferViewModel} that contains
     * the image data
     * 
     * @return The {@link BufferViewModel}
     */
    BufferViewModel getBufferViewModel();
    
    /**
     * Returns the actual image data. This will return a slice of the
     * buffer that is stored internally. Thus, changes to the contents
     * of this buffer will affect this model, but modifications of the 
     * position and limit of the returned buffer will not affect this 
     * model.<br>
     * 
     * @return The image data
     */
    ByteBuffer getImageData();
}