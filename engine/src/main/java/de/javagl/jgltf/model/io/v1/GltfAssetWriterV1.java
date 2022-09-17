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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

import de.javagl.jgltf.impl.v1.GlTF;
import de.javagl.jgltf.model.io.GltfAssetWriter;
import de.javagl.jgltf.model.io.GltfWriter;

/**
 * A class for writing a glTF 1.0 asset in binary format to an output stream.
 * This class contains  implementations for the methods of the 
 * {@link GltfAssetWriter}, for glTF 1.0 assets. Clients should not use this 
 * class directly, but only the {@link GltfAssetWriter}.
 */
public class GltfAssetWriterV1
{
    /**
     * The magic binary glTF header.
     * This is an integer corresponding to the ASCII string <code>"glTF"</code>
     */
    private static final int MAGIC_BINARY_GLTF_HEADER = 0x46546C67;
    
    /**
     * The binary glTF version that is written by this writer
     */
    private static final int BINARY_GLTF_VERSION = 1;

    /**
     * The constant indicating JSON content format for glTF 1.0
     */
    private static final int CONTENT_FORMAT_JSON = 0;
    
    /**
     * Default constructor
     */
    public GltfAssetWriterV1()
    {
        // Default constructor
    }

    /**
     * Write the given {@link GltfAssetV1} as a binary glTF asset to the 
     * given output stream. The caller is responsible for closing the 
     * given stream.<br>
     * <br>
     * 
     * @param gltfAsset The {@link GltfAssetV1}
     * @param outputStream The output stream
     * @throws IOException If an IO error occurred
     */
    public void writeBinary(GltfAssetV1 gltfAsset, OutputStream outputStream) 
        throws IOException
    {
        // Write the JSON representation of the glTF, creating the scene data
        GlTF gltf = gltfAsset.getGltf();
        byte sceneData[];
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
        {
            GltfWriter gltfWriter = new GltfWriter();
            gltfWriter.setIndenting(false);
            gltfWriter.write(gltf, baos);
            sceneData = baos.toByteArray();
        }
        
        ByteBuffer binaryData = gltfAsset.getBinaryData();
        if (binaryData == null)
        {
            binaryData = ByteBuffer.wrap(new byte[0]);
        }

        // Create the header data and fill it 
        byte headerData[] = new byte[20];
        int magic = MAGIC_BINARY_GLTF_HEADER;
        int version = BINARY_GLTF_VERSION;
        int length = 
            headerData.length + sceneData.length + binaryData.capacity();
        int contentLength = sceneData.length;
        int contentFormat = CONTENT_FORMAT_JSON;
        
        IntBuffer headerBuffer = ByteBuffer.wrap(headerData)
            .order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
        headerBuffer.put(magic);
        headerBuffer.put(version);
        headerBuffer.put(length);
        headerBuffer.put(contentLength);
        headerBuffer.put(contentFormat);
        
        // Finally, write the header, scene and binary glTF buffer 
        @SuppressWarnings("resource")
        WritableByteChannel writableByteChannel = 
            Channels.newChannel(outputStream);
        writableByteChannel.write(ByteBuffer.wrap(headerData));
        writableByteChannel.write(ByteBuffer.wrap(sceneData));
        writableByteChannel.write(binaryData.slice());
    }
    
}
