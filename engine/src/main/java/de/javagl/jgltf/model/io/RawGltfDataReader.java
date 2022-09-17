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
package de.javagl.jgltf.model.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import de.javagl.jgltf.model.io.v1.RawBinaryGltfDataReaderV1;
import de.javagl.jgltf.model.io.v2.RawBinaryGltfDataReaderV2;

/**
 * A class for reading the raw data of a glTF asset from an input stream.
 * The given stream may provide any form of glTF data:
 * <ul>
 *   <li>glTF 1.0 JSON data</li>
 *   <li>glTF 2.0 JSON data</li>
 *   <li>binary glTF 1.0 data</li>
 *   <li>binary glTF 2.0 data</li>
 * </ul>
 * The result will in all cases be a {@link RawGltfData} that contains
 * the JSON data and the binary data, and which may then be processed
 * further.<br>
 * <br>
 * Clients will usually not use this class directly.
 */
public class RawGltfDataReader
{
    /**
     * The magic binary glTF header.
     * This is an integer corresponding to the ASCII string <code>"glTF"</code>
     */
    private static final int MAGIC_BINARY_GLTF_HEADER = 0x46546C67;
    
    /**
     * The version number indicating glTF 1.0
     */
    private static final int BINARY_GLTF_VERSION_1 = 1;
    
    /**
     * The version number indicating glTF 2.0
     */
    private static final int BINARY_GLTF_VERSION_2 = 2;

    /**
     * Read the raw glTF data from the given input stream. The caller is
     * responsible for closing the given stream.
     * 
     * @param inputStream The input stream
     * @return The {@link RawGltfData}
     * @throws IOException If an IO error occurs
     */
    public static RawGltfData read(InputStream inputStream) throws IOException
    {
        byte rawData[] = IO.readStream(inputStream);
        if (rawData.length >= 8)
        {
            ByteBuffer data = 
                ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN);
            IntBuffer intData = data.asIntBuffer();
            int magic = intData.get(0);
            if (magic == MAGIC_BINARY_GLTF_HEADER)
            {
                int version = intData.get(1);
                if (version == BINARY_GLTF_VERSION_1)
                {
                    return RawBinaryGltfDataReaderV1.readBinaryGltf(data);
                }
                if (version == BINARY_GLTF_VERSION_2)
                {
                    return RawBinaryGltfDataReaderV2.readBinaryGltf(data);
                }
                throw new IOException(
                    "Unknown binary glTF version: " + version);
            }
        }
        ByteBuffer jsonData = Buffers.create(rawData);
        return new RawGltfData(jsonData, null);
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private RawGltfDataReader()
    {
        // Private constructor to prevent instantiation
    }
    
}


