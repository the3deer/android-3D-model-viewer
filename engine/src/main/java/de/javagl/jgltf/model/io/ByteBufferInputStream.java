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
package de.javagl.jgltf.model.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * Implementation of an input stream that reads from a byte buffer
 */
public class ByteBufferInputStream extends InputStream
{
    /**
     * The byte buffer from which this stream is reading
     */
    private final ByteBuffer byteBuffer;
    
    /**
     * Creates a new instance that read from the given byte buffer.
     * Reading from the stream will increase the position of the
     * given buffer. If this is not desired, a slice of the actual
     * buffer may be passed to this constructor. 
     * 
     * @param byteBuffer The byte buffer from which this stream is reading
     */
    public ByteBufferInputStream(ByteBuffer byteBuffer)
    {
        this.byteBuffer = Objects.requireNonNull(byteBuffer,
            "The byteBuffer may not be null");
    }
    
    @Override
    public int read() throws IOException
    {
        if (!byteBuffer.hasRemaining())
        {
            return -1;
        }
        return byteBuffer.get() & 0xFF;
    }

    @Override
    public int read(byte[] bytes, int off, int len) throws IOException
    {
        if (!byteBuffer.hasRemaining())
        {
            return -1;
        }
        int readLength = Math.min(len, byteBuffer.remaining());
        byteBuffer.get(bytes, off, readLength);
        return readLength;
    }
}

