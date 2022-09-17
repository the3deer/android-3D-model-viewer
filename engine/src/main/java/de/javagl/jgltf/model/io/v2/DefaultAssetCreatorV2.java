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
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.javagl.jgltf.impl.v2.Buffer;
import de.javagl.jgltf.impl.v2.GlTF;
import de.javagl.jgltf.impl.v2.Image;
import de.javagl.jgltf.model.BufferModel;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.ImageModel;
import de.javagl.jgltf.model.Optionals;
import de.javagl.jgltf.model.impl.UriStrings;
import de.javagl.jgltf.model.io.GltfAsset;
import de.javagl.jgltf.model.io.IO;
import de.javagl.jgltf.model.v2.GltfCreatorV2;

/**
 * A class for creating a {@link GltfAssetV2} with a default data 
 * representation from a {@link GltfModel}.<br>
 * <br>
 * In the default data representation, elements are referred to via URIs. 
 * Data elements like {@link Buffer} or {@link Image}
 * objects that used the binary glTF extension or data URIs will be 
 * converted to refer to their data using URIs.
 */
final class DefaultAssetCreatorV2
{
    /**
     * The {@link GltfAssetV2} that is currently being created
     */
    private GltfAssetV2 gltfAsset;
    
    /**
     * The set of {@link Buffer} URI strings that are already used
     */
    private Set<String> existingBufferUriStrings;

    /**
     * The set of {@link Image} URI strings that are already used
     */
    private Set<String> existingImageUriStrings;

    /**
     * Creates a new asset creator
     */
    DefaultAssetCreatorV2()
    {
        // Default constructor
    }

    /**
     * Create a default {@link GltfAssetV2} from the given {@link GltfModel}.
     *  
     * @param gltfModel The input {@link GltfModel}
     * @return The default {@link GltfAssetV2}
     */
    GltfAssetV2 create(GltfModel gltfModel)
    {
        GlTF outputGltf = GltfCreatorV2.create(gltfModel);

        List<Buffer> buffers = Optionals.of(outputGltf.getBuffers());
        List<Image> images = Optionals.of(outputGltf.getImages());
        
        existingBufferUriStrings = collectUriStrings(buffers, Buffer::getUri);
        existingImageUriStrings = collectUriStrings(images, Image::getUri);

        this.gltfAsset = new GltfAssetV2(outputGltf, null);
        
        for (int i = 0; i < buffers.size(); i++)
        {
            Buffer buffer = buffers.get(i);
            storeBufferAsDefault(gltfModel, i, buffer);
        }
        for (int i = 0; i < images.size(); i++)
        {
            Image image = images.get(i);
            storeImageAsDefault(gltfModel, i, image);
        }

        return gltfAsset;
    }

    /**
     * Collect all strings that are obtained from the given elements by 
     * applying the given function, if these strings are not <code>null</code>
     * and no data URI strings
     * 
     * @param elements The elements
     * @param uriFunction The function to obtain the string
     * @return The strings
     */
    private static <T> Set<String> collectUriStrings(Collection<T> elements, 
        Function<? super T, ? extends String> uriFunction)
    {
        return elements.stream()
            .map(uriFunction)
            .filter(Objects::nonNull)
            .filter(uriString -> !IO.isDataUriString(uriString))
            .collect(Collectors.toSet());
    }
    
    /**
     * Store the given {@link Buffer} with the given index in the current 
     * output asset. <br>
     * <br>
     * If the {@link Buffer#getUri() buffer URI} is <code>null</code> or a 
     * data URI, it will receive a new URI, which refers to the buffer data, 
     * which is then stored as {@link GltfAsset#getReferenceData(String) 
     * reference data} in the asset.<br>
     * <br>
     * The given {@link Buffer} object will be modified accordingly, if 
     * necessary: Its URI will be set to be the new URI. 
     * 
     * @param gltfModel The {@link GltfModel} 
     * @param index The index of the {@link Buffer}
     * @param buffer The {@link Buffer}
     */
    private void storeBufferAsDefault(
        GltfModel gltfModel, int index, Buffer buffer)
    {
        BufferModel bufferModel = gltfModel.getBufferModels().get(index);
        ByteBuffer bufferData = bufferModel.getBufferData();

        String oldUriString = buffer.getUri();
        String newUriString = oldUriString;
        if (oldUriString == null || IO.isDataUriString(oldUriString))
        {
            newUriString = UriStrings.createBufferUriString(
                existingBufferUriStrings);
            buffer.setUri(newUriString);
            existingBufferUriStrings.add(newUriString);
        }
        
        gltfAsset.putReferenceData(newUriString, bufferData);
    }

    
    /**
     * Store the given {@link Image} with the given index in the current 
     * output asset. <br>
     * <br>
     * If the {@link Image#getUri() image URI} is <code>null</code> or a 
     * data URI, it will receive a new URI, which refers to the image data, 
     * which is then stored as {@link GltfAsset#getReferenceData(String) 
     * reference data} in the asset.<br>
     * <br>
     * The given {@link Image} object will be modified accordingly, if 
     * necessary: Its URI will be set to be the new URI. If it referred
     * to a {@link Image#getBufferView() image buffer view}, then this
     * reference will be set to be <code>null</code>.
     *  
     * @param gltfModel The {@link GltfModel} 
     * @param index The index of the {@link Image}
     * @param image The {@link Image}
     */
    private void storeImageAsDefault(
        GltfModel gltfModel, int index, Image image)
    {
        ImageModel imageModel = gltfModel.getImageModels().get(index);
        ByteBuffer imageData = imageModel.getImageData();

        String oldUriString = image.getUri();
        String newUriString = oldUriString;
        if (oldUriString == null || IO.isDataUriString(oldUriString))
        {
            newUriString = UriStrings.createImageUriString(
                imageModel, existingImageUriStrings);
            image.setUri(newUriString);
            existingImageUriStrings.add(newUriString);
        }
        
        if (image.getBufferView() != null)
        {
            image.setBufferView(null);
        }
        
        gltfAsset.putReferenceData(newUriString, imageData);
    }

}
