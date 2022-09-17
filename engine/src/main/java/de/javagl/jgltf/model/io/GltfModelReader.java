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
import java.net.URI;
import java.nio.file.Path;
import java.util.function.Consumer;

import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.io.v1.GltfAssetV1;
import de.javagl.jgltf.model.io.v2.GltfAssetV2;
import de.javagl.jgltf.model.v1.GltfModelV1;
import de.javagl.jgltf.model.v2.GltfModelCreatorV2;

/**
 * A class for reading a {@link GltfModel} from a URI.
 */
public final class GltfModelReader
{
    /**
     * The consumer for {@link JsonError} instances
     */
    private Consumer<? super JsonError> jsonErrorConsumer = 
        JsonErrorConsumers.createLogging();
    
    /**
     * Default constructor
     */
    public GltfModelReader()
    {
        // Default constructor
    }
    
    /**
     * Set the given consumer to receive {@link JsonError}s that may 
     * occur when a glTF is read
     * 
     * @param jsonErrorConsumer The {@link JsonError} consumer
     */
    public void setJsonErrorConsumer(
        Consumer<? super JsonError> jsonErrorConsumer)
    {
        this.jsonErrorConsumer = jsonErrorConsumer;
    }
    
    /**
     * Read the {@link GltfModel} from the given URI
     * 
     * @param uri The URI
     * @return The {@link GltfModel}
     * @throws IOException If an IO error occurs
     */
    public GltfModel read(URI uri) throws IOException
    {
        GltfAssetReader gltfAssetReader = new GltfAssetReader();
        gltfAssetReader.setJsonErrorConsumer(jsonErrorConsumer);
        GltfAsset gltfAsset = gltfAssetReader.read(uri);
        return createModel(gltfAsset);
    }

    /**
     * Read the {@link GltfModel} from the given path
     *
     * @param path The path
     * @return The {@link GltfModel}
     * @throws IOException If an IO error occurs
     */
    public GltfModel read(Path path) throws IOException
    {
        GltfAssetReader gltfAssetReader = new GltfAssetReader();
        gltfAssetReader.setJsonErrorConsumer(jsonErrorConsumer);
        GltfAsset gltfAsset = gltfAssetReader.read(path);
        return createModel(gltfAsset);
    }

    /**
     * Read the {@link GltfModel} from the given URI. In contrast to the 
     * {@link #read(URI)} method, this method will not resolve any 
     * references that are contained in the {@link GltfModel}. <br>
     * <br>
     * This is mainly intended for binary- or embedded glTF assets that do not
     * have external references.
     * 
     * @param uri The URI
     * @return The {@link GltfModel}
     * @throws IOException If an IO error occurs
     */
    public GltfModel readWithoutReferences(URI uri) throws IOException
    {
        try (InputStream inputStream = uri.toURL().openStream())
        {
            GltfModel gltfModel = readWithoutReferences(inputStream);
            return gltfModel;
        }
    }
    
    /**
     * Read the {@link GltfModel} from the given input stream. In contrast
     * to the {@link #read(URI)} method, this method will not resolve any 
     * references that are contained in the {@link GltfAsset}. <br>
     * <br>
     * This is mainly intended for binary- or embedded glTF assets that do not
     * have external references.
     * 
     * @param inputStream The input stream to read from
     * @return The {@link GltfModel}
     * @throws IOException If an IO error occurs
     */
    public GltfModel readWithoutReferences(InputStream inputStream) 
        throws IOException
    {
        GltfAssetReader gltfAssetReader = new GltfAssetReader();
        gltfAssetReader.setJsonErrorConsumer(jsonErrorConsumer);
        GltfAsset gltfAsset = 
            gltfAssetReader.readWithoutReferences(inputStream);
        return createModel(gltfAsset);
    }
    
    /**
     * Creates a {@link GltfModel} instance from the given {@link GltfAsset}
     * 
     * @param gltfAsset The {@link GltfAsset}
     * @return The {@link GltfModel}
     * @throws IOException If the given asset has an unknown version
     */
    private static GltfModel createModel(GltfAsset gltfAsset) throws IOException
    {
        if (gltfAsset instanceof GltfAssetV1)
        {
            GltfAssetV1 gltfAssetV1 = (GltfAssetV1)gltfAsset;
            return new GltfModelV1(gltfAssetV1);
        }
        if (gltfAsset instanceof GltfAssetV2)
        {
            GltfAssetV2 gltfAssetV2 = (GltfAssetV2)gltfAsset;
            return GltfModelCreatorV2.create(gltfAssetV2);
        }
        throw new IOException(
            "The glTF asset has an unknown version: " + gltfAsset);
    }
    
}
