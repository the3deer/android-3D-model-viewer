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
package de.javagl.jgltf.model.io.v2;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Arrays;

import de.javagl.jgltf.impl.v2.GlTF;
import de.javagl.jgltf.model.io.Buffers;
import de.javagl.jgltf.model.io.GltfAssetWriter;
import de.javagl.jgltf.model.io.GltfWriter;

/**
 * A class for writing a glTF 2.0 asset in binary format to an output stream.
 * This class contains  implementations for the methods of the 
 * {@link GltfAssetWriter}, for glTF 2.0 assets. Clients should not use this 
 * class directly, but only the {@link GltfAssetWriter}.
 */
public final class GltfAssetWriterV2
{
    /**
     * The magic binary glTF header.
     * This is an integer corresponding to the ASCII string <code>"glTF"</code>
     */
    private static final int MAGIC_BINARY_GLTF_HEADER = 0x46546C67;
    
    /**
     * The binary glTF version that is written by this writer
     */
    private static final int BINARY_GLTF_VERSION = 2;

    /**
     * The constant indicating JSON chunk type for glTF 2.0
     * This is an integer corresponding to the ASCII string <code>"JSON"</code>
     */
    private static final int CHUNK_TYPE_JSON = 0x4E4F534A;
    
    /**
     * The constant indicating BIN chunk type for glTF 2.0
     * This is an integer corresponding to the ASCII string <code>"BIN"</code>
     */
    private static final int CHUNK_TYPE_BIN = 0x004E4942;
    
    /**
     * Default constructor
     */
    public GltfAssetWriterV2()
    {
        // Default constructor
    }
    
    /**
     * Write the given {@link GltfAssetV2} as a binary glTF asset to the 
     * given output stream. The caller is responsible for closing the 
     * given stream.<br>
     * <br>
     * 
     * @param gltfAsset The {@link GltfAssetV2}
     * @param outputStream The output stream
     * @throws IOException If an IO error occurred
     */
    public void writeBinary(GltfAssetV2 gltfAsset, OutputStream outputStream) 
        throws IOException
    {
        // Write the JSON representation of the glTF
        GlTF gltf = gltfAsset.getGltf();
        byte jsonData[];
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
        {
            GltfWriter gltfWriter = new GltfWriter();
            gltfWriter.setIndenting(false);
            gltfWriter.write(gltf, baos);
            jsonData = baos.toByteArray();
        }
        // Ensure 4-byte-alignment for the jsonData
        if (jsonData.length % 4 != 0)
        {
            int oldLength = jsonData.length;
            int padding = 4 - (oldLength % 4);
            jsonData = Arrays.copyOf(jsonData, oldLength + padding);
            for (int i = 0; i < padding; i++)
            {
                jsonData[oldLength + i] = ' ';
            }
        }
        
        // Obtain the binary data, and ensure it's 4-byte aligned
        ByteBuffer binaryData = gltfAsset.getBinaryData();
        if (binaryData == null)
        {
            binaryData = ByteBuffer.wrap(new byte[0]);
        }
        if (binaryData.capacity() % 4 != 0)
        {
            int padding = 4 - (binaryData.capacity() % 4);
            binaryData = 
                Buffers.copyOf(binaryData, binaryData.capacity() + padding);
        }

        // Create the JSON chunk data
        ChunkData jsonChunkData = new ChunkData();
        jsonChunkData.append(jsonData.length);
        jsonChunkData.append(CHUNK_TYPE_JSON);
        jsonChunkData.append(ByteBuffer.wrap(jsonData));
        
        // Create the BIN chunk data
        ChunkData binChunkData = new ChunkData();
        binChunkData.append(binaryData.capacity());
        binChunkData.append(CHUNK_TYPE_BIN);
        binChunkData.append(binaryData);
        
        // Create the header data
        ChunkData headerData = new ChunkData();
        headerData.append(MAGIC_BINARY_GLTF_HEADER);
        headerData.append(BINARY_GLTF_VERSION);
        int length = 12 + jsonData.length + 8 + binaryData.capacity() + 8;
        headerData.append(length);
        
        // Finally, write the header, JSON and BIN data to the output stream 
        @SuppressWarnings("resource")
        WritableByteChannel writableByteChannel = 
            Channels.newChannel(outputStream);
        writableByteChannel.write(headerData.get());
        writableByteChannel.write(jsonChunkData.get());
        writableByteChannel.write(binChunkData.get());
    }

    /**
     * Utility class for collecting the data of one binary glTF 2.0 chunk
     */
    private static class ChunkData
    {
        /**
         * The stream that will collect the data
         */
        private ByteArrayOutputStream baos;
        
        /**
         * Default constructor
         */
        ChunkData()
        {
            baos = new ByteArrayOutputStream();
        }
        
        /**
         * Append the given value to this data
         * 
         * @param value The value
         * @throws IOException If an IO error occurs
         */
        void append(int value) throws IOException
        {
            baos.write((value >> 0) & 0xFF);
            baos.write((value >> 8) & 0xFF);
            baos.write((value >> 16) & 0xFF);
            baos.write((value >> 24) & 0xFF);
        }
        
        /**
         * Append the given buffer to this data
         * 
         * @param buffer The buffer
         * @throws IOException If an IO error occurs
         */
        void append(ByteBuffer buffer) throws IOException
        {
            @SuppressWarnings("resource")
            WritableByteChannel writableByteChannel = 
                Channels.newChannel(baos);
            writableByteChannel.write(buffer.slice());
        }
        
        /**
         * Returns the chunk data as a (non-direct!) buffer
         * 
         * @return The chunk data
         */
        ByteBuffer get()
        {
            return ByteBuffer.wrap(baos.toByteArray());
        }
        
    }
    
    
    
}
