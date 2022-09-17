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
package de.javagl.jgltf.model.image;

import java.nio.ByteBuffer;

/**
 * Default implementation of a {@link PixelData}
 */
final class DefaultPixelData implements PixelData
{
    /**
     * The width
     */
    private final int width;
    
    /**
     * The height
     */
    private final int height;
    
    /**
     * The pixels, as RGBA values
     */
    private final ByteBuffer pixelsRGBA;
    
    /**
     * Creates a new instance
     * 
     * @param width The width
     * @param height The height
     * @param pixelsRGBA The pixels, as RGBA values
     */
    DefaultPixelData(int width, int height, ByteBuffer pixelsRGBA)
    {
        this.width = width;
        this.height = height;
        this.pixelsRGBA = pixelsRGBA;
    }
    
    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public ByteBuffer getPixelsRGBA()
    {
        // The slice is BIG_ENDIAN by default
        return pixelsRGBA.slice();
    }

}
