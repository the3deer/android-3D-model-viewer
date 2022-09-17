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
package de.javagl.jgltf.model.io.v1;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import de.javagl.jgltf.impl.v1.GlTF;
import de.javagl.jgltf.model.io.GltfAssetWriter;
import de.javagl.jgltf.model.io.GltfModelWriter;
import de.javagl.jgltf.model.io.GltfWriter;
import de.javagl.jgltf.model.v1.GltfModelV1;

/**
 * A class for writing a {@link GltfModelV1}. This class contains  
 * implementations for the methods of the {@link GltfModelWriter},
 * for glTF 1.0 assets. Clients should not use this class directly,
 * but only the {@link GltfModelWriter}.
 */
public final class GltfModelWriterV1
{
    /**
     * Default constructor
     */
    public GltfModelWriterV1()
    {
        // Default constructor
    }
    
    /**
     * Write the given {@link GltfModelV1} to the given file. External
     * references of buffers, images and shaders that are given via 
     * the respective URI string will be resolved against the parent 
     * directory of the given file, and the corresponding data will 
     * be written into the corresponding files. 
     * 
     * @param gltfModel The {@link GltfModelV1}
     * @param file The file
     * @throws IOException If an IO error occurs
     */
    public void write(GltfModelV1 gltfModel, File file) 
        throws IOException
    {
        GltfAssetV1 gltfAsset = GltfAssetsV1.createDefault(gltfModel);
        GltfAssetWriter gltfAssetWriter = new GltfAssetWriter();
        gltfAssetWriter.write(gltfAsset, file);
    }
    
    /**
     * Write the given {@link GltfModelV1} as a binary glTF asset to the
     * given file
     * 
     * @param gltfModel The {@link GltfModelV1}
     * @param file The file
     * @throws IOException If an IO error occurs
     */
    public void writeBinary(GltfModelV1 gltfModel, File file) 
        throws IOException
    {
        try (OutputStream outputStream = new FileOutputStream(file))
        {
            writeBinary(gltfModel, outputStream);
        }
    }
    
    /**
     * Write the given {@link GltfModelV1} as a binary glTF asset to the
     * given output stream. The caller is responsible for closing the 
     * given stream.
     * 
     * @param gltfModel The {@link GltfModelV1}
     * @param outputStream The output stream
     * @throws IOException If an IO error occurs
     */
    public void writeBinary(GltfModelV1 gltfModel, OutputStream outputStream) 
        throws IOException
    {
        GltfAssetV1 gltfAsset = GltfAssetsV1.createBinary(gltfModel);
        GltfAssetWriterV1 gltfAssetWriter = new GltfAssetWriterV1();
        gltfAssetWriter.writeBinary(gltfAsset, outputStream);
    }
    
    /**
     * Write the given {@link GltfModelV1} as an embedded glTF asset to the
     * given output stream. The caller is responsible for closing the 
     * given stream.
     * 
     * @param gltfModel The {@link GltfModelV1}
     * @param outputStream The output stream
     * @throws IOException If an IO error occurs
     */
    public void writeEmbedded(GltfModelV1 gltfModel, OutputStream outputStream) 
        throws IOException
    {
        GltfAssetV1 gltfAsset = GltfAssetsV1.createEmbedded(gltfModel);
        GltfWriter gltfWriter = new GltfWriter();
        GlTF gltf = gltfAsset.getGltf();
        gltfWriter.write(gltf, outputStream);
    }
}
