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
package de.javagl.jgltf.model.io.v1;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import de.javagl.jgltf.model.io.Buffers;
import de.javagl.jgltf.model.io.RawGltfData;

/**
 * A class for reading a {@link RawGltfData} from a buffer that contains
 * binary glTF 1.0 data
 */
public class RawBinaryGltfDataReaderV1
{
    /**
     * The length of the binary glTF header for glTF 1.0, in bytes
     */
    private static final int BINARY_GLTF_VERSION_1_HEADER_LENGTH_IN_BYTES = 20;
    
    /**
     * The constant indicating JSON content format for glTF 1.0
     */
    private static final int CONTENT_FORMAT_JSON = 0;
    
    /**
     * Read the {@link RawGltfData} from the given buffer, which contains
     * the binary glTF 1.0 data
     * 
     * @param data The input data
     * @return The {@link RawGltfData}
     * @throws IOException If an IO error occurs
     */
    public static RawGltfData readBinaryGltf(ByteBuffer data) 
        throws IOException
    {
        int headerLength = BINARY_GLTF_VERSION_1_HEADER_LENGTH_IN_BYTES;
        if (data.capacity() < headerLength)
        {
            throw new IOException("Expected header of size " + headerLength
                + ", but only found " + data.capacity() + " bytes");
        }
        IntBuffer intData = data.asIntBuffer();
        int length = intData.get(2);
        if (length != data.capacity())
        {
            throw new IOException(
                "Data length is " + data.capacity() + ", expected " + length);
        }
        
        int contentLength = intData.get(3); 
        int contentFormat = intData.get(4);
        if (contentFormat != CONTENT_FORMAT_JSON)
        {
            throw new IOException("Expected content format to be JSON ("
                + CONTENT_FORMAT_JSON + "), but found " + contentFormat);
        }
        ByteBuffer contentData = Buffers.createSlice(
            data, headerLength, contentLength);
        int bodyByteOffset = headerLength + contentLength;
        int bodyByteLength = length - bodyByteOffset;
        ByteBuffer bodyData = null;
        if (bodyByteLength > 0)
        {
            bodyData = Buffers.createSlice(
                data, bodyByteOffset, bodyByteLength);
        }
        
        return new RawGltfData(contentData, bodyData);
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private RawBinaryGltfDataReaderV1()
    {
        // Private constructor to prevent instantiation
    }
}
