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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.io.v1.GltfModelWriterV1;
import de.javagl.jgltf.model.io.v2.GltfModelWriterV2;
import de.javagl.jgltf.model.v1.GltfModelV1;

/**
 * A class for writing a {@link GltfModel}. The model can be written as
 * a default glTF, consisting of a JSON file and the files that are 
 * referred to via URIs, or as a binary file, or an embedded file where
 * all external references are replaced by data URIs.
 */
public class GltfModelWriter
{
    /**
     * Default constructor
     */
    public GltfModelWriter()
    {
        // Default constructor
    }

    /**
     * Write the given {@link GltfModel} to a file with the given name. 
     * External references of buffers or images that are given via the 
     * respective URI string will be resolved against the parent directory 
     * of the file, and the corresponding data will be written into the 
     * corresponding files. 
     * 
     * @param gltfModel The {@link GltfModel}
     * @param fileName The file name
     * @throws IOException If an IO error occurs
     */
    public void write(GltfModel gltfModel, String fileName) 
        throws IOException
    {
        write(gltfModel, new File(fileName));
    }
    
    /**
     * Write the given {@link GltfModel} to the given file. External
     * references of buffers or images that are given via the respective 
     * URI string will be resolved against the parent directory of the 
     * given file, and the corresponding data will be written into the 
     * corresponding files. 
     * 
     * @param gltfModel The {@link GltfModel}
     * @param file The file
     * @throws IOException If an IO error occurs
     */
    public void write(GltfModel gltfModel, File file) 
        throws IOException
    {
        if (gltfModel instanceof GltfModelV1)
        {
            GltfModelV1 gltfModelV1 = (GltfModelV1)gltfModel;
            GltfModelWriterV1 gltfModelWriterV1 = 
                new GltfModelWriterV1();
            gltfModelWriterV1.write(gltfModelV1, file);
            return;
        }
        GltfModelWriterV2 gltfModelWriterV2 = 
            new GltfModelWriterV2();
        gltfModelWriterV2.write(gltfModel, file);
    }
    
    /**
     * Write the given {@link GltfModel} as a binary glTF asset to the
     * given file
     * 
     * @param gltfModel The {@link GltfModel}
     * @param file The file
     * @throws IOException If an IO error occurs
     */
    public void writeBinary(GltfModel gltfModel, File file) 
        throws IOException
    {
        try (OutputStream outputStream = new FileOutputStream(file))
        {
            writeBinary(gltfModel, outputStream);
        }
    }
    
    /**
     * Write the given {@link GltfModel} as a binary glTF asset to the
     * given output stream. The caller is responsible for closing the 
     * given stream.
     * 
     * @param gltfModel The {@link GltfModel}
     * @param outputStream The output stream
     * @throws IOException If an IO error occurs
     */
    public void writeBinary(GltfModel gltfModel, OutputStream outputStream) 
        throws IOException
    {
        if (gltfModel instanceof GltfModelV1)
        {
            GltfModelV1 gltfModelV1 = (GltfModelV1)gltfModel;
            GltfModelWriterV1 gltfModelWriterV1 = 
                new GltfModelWriterV1();
            gltfModelWriterV1.writeBinary(gltfModelV1, outputStream);
            return;
        }
        GltfModelWriterV2 gltfModelWriterV2 = 
            new GltfModelWriterV2();
        gltfModelWriterV2.writeBinary(gltfModel, outputStream);
    }
    

    /**
     * Write the given {@link GltfModel} as an embedded glTF asset to the
     * given file
     * 
     * @param gltfModel The {@link GltfModel}
     * @param file The file
     * @throws IOException If an IO error occurs
     */
    public void writeEmbedded(GltfModel gltfModel, File file) 
        throws IOException
    {
        try (OutputStream outputStream = new FileOutputStream(file))
        {
            writeEmbedded(gltfModel, outputStream);
        }
    }
    
    /**
     * Write the given {@link GltfModel} as an embedded glTF asset to the
     * given output stream. The caller is responsible for closing the 
     * given stream.
     * 
     * @param gltfModel The {@link GltfModel}
     * @param outputStream The output stream
     * @throws IOException If an IO error occurs
     */
    public void writeEmbedded(GltfModel gltfModel, OutputStream outputStream) 
        throws IOException
    {
        if (gltfModel instanceof GltfModelV1)
        {
            GltfModelV1 gltfModelV1 = (GltfModelV1)gltfModel;
            GltfModelWriterV1 gltfModelWriterV1 = 
                new GltfModelWriterV1();
            gltfModelWriterV1.writeEmbedded(gltfModelV1, outputStream);
            return;
        }
        GltfModelWriterV2 gltfModelWriterV2 = 
            new GltfModelWriterV2();
        gltfModelWriterV2.writeEmbedded(gltfModel, outputStream);
    }
}

