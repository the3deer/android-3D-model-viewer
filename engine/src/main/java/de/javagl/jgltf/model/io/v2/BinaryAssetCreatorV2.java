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
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import de.javagl.jgltf.impl.v2.Buffer;
import de.javagl.jgltf.impl.v2.BufferView;
import de.javagl.jgltf.impl.v2.GlTF;
import de.javagl.jgltf.impl.v2.Image;
import de.javagl.jgltf.model.BufferModel;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.ImageModel;
import de.javagl.jgltf.model.Optionals;
import de.javagl.jgltf.model.io.Buffers;
import de.javagl.jgltf.model.io.MimeTypes;
import de.javagl.jgltf.model.v2.GltfCreatorV2;

/**
 * A class for creating a binary {@link GltfAssetV2} from a 
 * {@link GltfModel}.<br>
 * <br>
 */
final class BinaryAssetCreatorV2
{
    /**
     * The logger used in this class
     */
    private static final Logger logger =
        Logger.getLogger(BinaryAssetCreatorV2.class.getName());
    
    /**
     * Creates a new asset creator
     */
    BinaryAssetCreatorV2()
    {
        // Default constructor
    }
    
    /**
     * Create a binary {@link GltfAssetV2} from the given {@link GltfModel}.
     * The resulting asset will have a {@link GlTF} that uses references to
     * {@link BufferView} objects in its {@link Buffer} and {@link Image}
     * elements. 
     * 
     * @param gltfModel The {@link GltfModel}
     * @return The {@link GltfAssetV2}
     */
    GltfAssetV2 create(GltfModel gltfModel)
    {
        GlTF outputGltf = GltfCreatorV2.create(gltfModel);
        
        // Create the new byte buffer for the data of the "binary_glTF" Buffer
        int binaryGltfBufferSize = 
            computeBinaryGltfBufferSize(gltfModel);
        ByteBuffer binaryGltfByteBuffer = 
            Buffers.create(binaryGltfBufferSize);

        // Create the binary Buffer, 
        Buffer binaryGltfBuffer = new Buffer();
        binaryGltfBuffer.setByteLength(binaryGltfBufferSize);
        outputGltf.setBuffers(Collections.singletonList(binaryGltfBuffer));

        // Create a defensive copy of the original image list
        List<Image> oldImages = copy(outputGltf.getImages());

        // Place the data from buffers and images into the new binary glTF 
        // buffer. The mappings from IDs to offsets inside the resulting 
        // buffer will be used to compute the offsets for the buffer views
        List<ByteBuffer> bufferDatas = 
            gltfModel.getBufferModels().stream()
            .map(BufferModel::getBufferData)
            .collect(Collectors.toList());
        Map<Integer, Integer> bufferOffsets = concatBuffers(
            bufferDatas, binaryGltfByteBuffer);
        List<ByteBuffer> imageDatas = 
            gltfModel.getImageModels().stream()
            .map(ImageModel::getImageData)
            .collect(Collectors.toList());
        Map<Integer, Integer> imageOffsets = concatBuffers(
            imageDatas, binaryGltfByteBuffer);
        binaryGltfByteBuffer.position(0);

        // For all existing BufferViews, create new ones that are updated to 
        // refer to the new binary glTF buffer, with the appropriate offset
        List<BufferView> oldBufferViews = 
            copy(outputGltf.getBufferViews());
        List<BufferView> newBufferViews = 
            new ArrayList<BufferView>();
        for (int i = 0; i < oldBufferViews.size(); i++)
        {
            BufferView oldBufferView = oldBufferViews.get(i);
            BufferView newBufferView = GltfUtilsV2.copy(oldBufferView);

            newBufferView.setBuffer(0);
            Integer oldBufferIndex = oldBufferView.getBuffer();
            int oldByteOffset = Optionals.of(oldBufferView.getByteOffset(), 0);
            int bufferOffset = bufferOffsets.get(oldBufferIndex);
            int newByteOffset = oldByteOffset + bufferOffset;
            newBufferView.setByteOffset(newByteOffset);

            newBufferViews.add(newBufferView);
        }

        // For all existing Images, create new ones that are updated to 
        // refer to the new binary glTF buffer, using a bufferView 
        // index that refers to a newly created BufferView
        List<Image> newImages = new ArrayList<Image>();
        for (int i = 0; i < oldImages.size(); i++)
        {
            Image oldImage = oldImages.get(i);
            Image newImage = GltfUtilsV2.copy(oldImage);

            // Create the BufferView for the image
            ImageModel imageModel = gltfModel.getImageModels().get(i);
            ByteBuffer imageData = imageModel.getImageData();
            int byteLength = imageData.capacity();
            int byteOffset = imageOffsets.get(i);
            BufferView imageBufferView = new BufferView();
            imageBufferView.setBuffer(0);
            imageBufferView.setByteOffset(byteOffset);
            imageBufferView.setByteLength(byteLength);

            int newBufferViewIndex = newBufferViews.size();
            newImage.setBufferView(newBufferViewIndex);
            newImage.setUri(null);
            
            String imageMimeTypeString =
                MimeTypes.guessImageMimeTypeString(
                    oldImage.getUri(), imageData);
            if (imageMimeTypeString == null)
            {
                logger.warning("Could not detect MIME type of image");
            }
            else
            {
                newImage.setMimeType(imageMimeTypeString);
            }
            
            newBufferViews.add(imageBufferView);
            newImages.add(newImage);
        }

        // Place the newly created lists into the output glTF,
        // if there have been non-null lists for them in the input
        if (!oldImages.isEmpty())
        {
            outputGltf.setImages(newImages);
        }
        if (!newBufferViews.isEmpty())
        {
            outputGltf.setBufferViews(newBufferViews);
        }
        return new GltfAssetV2(outputGltf, binaryGltfByteBuffer);
    }


    /**
     * Compute the total size that is required for the binary glTF buffer
     * for the given {@link GltfModel}, which is the sum of all buffer
     * sizes of all buffers and images
     * 
     * @param gltfModel The {@link GltfModel}
     * @return The total size for the binary glTF buffer
     */
    private static int computeBinaryGltfBufferSize(GltfModel gltfModel)
    {
        int binaryGltfBufferSize = 0;
        for (BufferModel bufferModel : gltfModel.getBufferModels())
        {
            ByteBuffer bufferData = bufferModel.getBufferData();
            binaryGltfBufferSize += bufferData.capacity();
        }
        for (ImageModel imageModel : gltfModel.getImageModels())
        {
            ByteBuffer imageData = imageModel.getImageData();
            binaryGltfBufferSize += imageData.capacity();
        }
        return binaryGltfBufferSize;
    }

    /**
     * Put the contents of all byte buffers of the given list into the given 
     * target buffer. This method assumes that the target buffer has a 
     * sufficient capacity to hold all buffers.
     * 
     * @param buffers The buffers
     * @param targetBuffer The target buffer
     * @return A mapping from each key to the offset inside the target buffer
     */
    private static Map<Integer, Integer> concatBuffers(
        List<? extends ByteBuffer> buffers, ByteBuffer targetBuffer)
    {
        Map<Integer, Integer> offsets = new LinkedHashMap<Integer, Integer>();
        for (int i = 0; i < buffers.size(); i++)
        {
            ByteBuffer oldByteBuffer = buffers.get(i);
            int offset = targetBuffer.position();
            offsets.put(i, offset);
            targetBuffer.put(oldByteBuffer.slice());
        }
        return offsets;
    }


    /**
     * Creates a copy of the given list. If the given list is <code>null</code>, 
     * then an unmodifiable empty list will be returned
     * 
     * @param list The input list
     * @return The copy
     */
    private static <T> List<T> copy(List<T> list)
    {
        if (list == null)
        {
            return Collections.emptyList();
        }
        return new ArrayList<T>(list);
    }
    

}
