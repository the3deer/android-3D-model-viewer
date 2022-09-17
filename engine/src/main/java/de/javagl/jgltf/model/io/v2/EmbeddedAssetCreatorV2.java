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
package de.javagl.jgltf.model.io.v2;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.List;

import de.javagl.jgltf.impl.v2.Buffer;
import de.javagl.jgltf.impl.v2.GlTF;
import de.javagl.jgltf.impl.v2.Image;
import de.javagl.jgltf.model.BufferModel;
import de.javagl.jgltf.model.GltfException;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.ImageModel;
import de.javagl.jgltf.model.Optionals;
import de.javagl.jgltf.model.io.GltfAsset;
import de.javagl.jgltf.model.io.IO;
import de.javagl.jgltf.model.io.MimeTypes;
import de.javagl.jgltf.model.v2.GltfCreatorV2;

/**
 * A class for creating a {@link GltfAssetV2} with an "embedded" data 
 * representation from a {@link GltfModel}.<br>
 * <br>
 * In the "embedded" data representation, the data of elements like 
 * {@link Buffer} or {@link Image} objects is stored in data URIs.
 */
final class EmbeddedAssetCreatorV2
{
    /**
     * Creates a new asset creator
     */
    EmbeddedAssetCreatorV2()
    {
        // Default constructor
    }

    /**
     * Create a {@link GltfAssetV2} with "embedded" data representation from 
     * the given {@link GltfModel}.<br>
     * <br>
     * The returned {@link GltfAssetV2} will contain a {@link GlTF} where the
     * the URIs that appear in {@link Buffer} and {@link Image} instances are 
     * replaced with data URIs that contain the corresponding data. 
     * Its {@link GltfAsset#getBinaryData() binary data} will be
     * <code>null</code>, and its {@link GltfAsset#getReferenceDatas() 
     * reference data elements} will be empty.
     *  
     * @param gltfModel The input {@link GltfModel}
     * @return The embedded {@link GltfAssetV2}
     */
    GltfAssetV2 create(GltfModel gltfModel)
    {
        GlTF outputGltf = GltfCreatorV2.create(gltfModel);

        List<Buffer> buffers = Optionals.of(outputGltf.getBuffers());
        for (int i = 0; i < buffers.size(); i++)
        {
            Buffer buffer = buffers.get(i);
            convertBufferToEmbedded(gltfModel, i, buffer);
        }
        
        List<Image> images = Optionals.of(outputGltf.getImages());
        for (int i = 0; i < images.size(); i++)
        {
            Image image = images.get(i);
            convertImageToEmbedded(gltfModel, i, image);
        }

        return new GltfAssetV2(outputGltf, null);
    }

    /**
     * Convert the given {@link Buffer} into an embedded buffer, by replacing 
     * its URI with a data URI, if the URI is not already a data URI
     * 
     * @param gltfModel The {@link GltfModel}
     * @param index The index of the {@link Buffer}
     * @param buffer The {@link Buffer}
     */
    private static void convertBufferToEmbedded(
        GltfModel gltfModel, int index, Buffer buffer)
    {
        String uriString = buffer.getUri();
        if (IO.isDataUriString(uriString))
        {
            return;
        }
        BufferModel bufferModel = gltfModel.getBufferModels().get(index);
        ByteBuffer bufferData = bufferModel.getBufferData();
        
        byte data[] = new byte[bufferData.capacity()];
        bufferData.slice().get(data);
        String encodedData = Base64.getEncoder().encodeToString(data);
        String dataUriString = 
            "data:application/gltf-buffer;base64," + encodedData;
        
        buffer.setUri(dataUriString);
    }

    /**
     * Convert the given {@link Image} into an embedded image, by replacing 
     * its URI with a data URI, if the URI is not already a data URI
     * 
     * @param gltfModel The {@link GltfModel}
     * @param index The index of the {@link Image}
     * @param image The {@link Image}
     * @throws GltfException If the image format (and thus, the MIME type)
     * can not be determined from the image data  
     */
    private static void convertImageToEmbedded(
        GltfModel gltfModel, int index, Image image)
    {
        String uriString = image.getUri();
        if (IO.isDataUriString(uriString))
        {
            return;
        }
        ImageModel imageModel = gltfModel.getImageModels().get(index);
        ByteBuffer imageData = imageModel.getImageData();
        
        String uri = image.getUri();
        String imageMimeTypeString =
            MimeTypes.guessImageMimeTypeString(uri, imageData);
        if (imageMimeTypeString == null)
        {
            throw new GltfException(
                "Could not detect MIME type of image " + index);
        }

        byte data[] = new byte[imageData.capacity()];
        imageData.slice().get(data);
        String encodedData = Base64.getEncoder().encodeToString(data);
        String dataUriString =
            "data:" + imageMimeTypeString + ";base64," + encodedData;
        
        image.setUri(dataUriString);
    }


}
