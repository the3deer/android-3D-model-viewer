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

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import de.javagl.jgltf.impl.v1.Buffer;
import de.javagl.jgltf.impl.v1.BufferView;
import de.javagl.jgltf.impl.v1.GlTF;
import de.javagl.jgltf.impl.v1.Image;
import de.javagl.jgltf.impl.v1.Shader;
import de.javagl.jgltf.model.BufferModel;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.ImageModel;
import de.javagl.jgltf.model.gl.ShaderModel;
import de.javagl.jgltf.model.io.Buffers;
import de.javagl.jgltf.model.v1.BinaryGltfV1;
import de.javagl.jgltf.model.v1.GltfCreatorV1;
import de.javagl.jgltf.model.v1.GltfExtensionsV1;
import de.javagl.jgltf.model.v1.GltfIds;
import de.javagl.jgltf.model.v1.GltfModelV1;

/**
 * A class for creating a binary {@link GltfAssetV1} from a 
 * {@link GltfModelV1}.<br>
 * <br>
 */
final class BinaryAssetCreatorV1
{
    /**
     * Creates a new asset creator
     */
    BinaryAssetCreatorV1()
    {
        // Default constructor
    }
    
    /**
     * Create a binary {@link GltfAssetV1} from the given {@link GltfModelV1}.
     * The resulting asset will have a {@link GlTF} that uses the binary
     * glTF extension objects in its {@link Buffer}, {@link Image} and
     * {@link Shader} elements, to refer to the 
     * {@link GltfAssetV1#getBinaryData() binary data} of the asset. 
     * 
     * @param gltfModel The {@link GltfModelV1}
     * @return The {@link GltfAssetV1}
     */
    GltfAssetV1 create(GltfModelV1 gltfModel)
    {
        GlTF outputGltf = GltfCreatorV1.create(gltfModel);
        
        // Create the new byte buffer for the data of the "binary_glTF" Buffer
        int binaryGltfBufferSize = 
            computeBinaryGltfBufferSize(gltfModel);
        ByteBuffer binaryGltfByteBuffer = 
            Buffers.create(binaryGltfBufferSize);

        // Create the "binary_glTF" Buffer, 
        GltfExtensionsV1.addExtensionUsed(outputGltf, 
            BinaryGltfV1.getBinaryGltfExtensionName());
        Buffer binaryGltfBuffer = new Buffer();
        binaryGltfBuffer.setType("arraybuffer");
        binaryGltfBuffer.setUri(
            BinaryGltfV1.getBinaryGltfBufferId() + ".bin");
        binaryGltfBuffer.setByteLength(binaryGltfBufferSize);
        Map<String, Buffer> newBuffers = Collections.singletonMap(
            BinaryGltfV1.getBinaryGltfBufferId(), binaryGltfBuffer);

        // Create defensive copies of the original maps, as linked maps
        // with a fixed iteration order (!). If the input maps are null,
        // then these will be empty maps. This has to be considered when
        // the new maps are created and put into the glTF!
        Map<String, Buffer> oldBuffers = copy(outputGltf.getBuffers());
        Map<String, Image> oldImages = copy(outputGltf.getImages());
        Map<String, Shader> oldShaders = copy(outputGltf.getShaders());

        // TODO This is not solved very elegantly, due to the 
        // transition of glTF 1.0 to glTF 2.0 - refactor this!
        
        // Create mappings from the IDs to the corresponding model elements.
        // This assumes that they are in the same order.
        Map<String, BufferModel> bufferIdToBuffer = GltfUtilsV1.createMap(
            oldBuffers, gltfModel.getBufferModels());
        Map<String, ImageModel> imageIdToImage = GltfUtilsV1.createMap(
            oldImages, gltfModel.getImageModels());
        Map<String, ShaderModel> shaderIdToShader = GltfUtilsV1.createMap(
            oldShaders, gltfModel.getShaderModels());
        
        // Place the data from buffers, images and shaders into the
        // new binary glTF buffer. The mappings from IDs to offsets 
        // inside the resulting buffer will be used to compute the
        // offsets for the buffer views
        Map<String, Integer> bufferOffsets = concatBuffers(
            oldBuffers.keySet(), 
            id -> bufferIdToBuffer.get(id).getBufferData(), 
            binaryGltfByteBuffer);
        Map<String, Integer> imageOffsets = concatBuffers(
            oldImages.keySet(), 
            id -> imageIdToImage.get(id).getImageData(), 
            binaryGltfByteBuffer);
        Map<String, Integer> shaderOffsets = concatBuffers(
            oldShaders.keySet(), 
            id -> shaderIdToShader.get(id).getShaderData(), 
            binaryGltfByteBuffer);
        binaryGltfByteBuffer.position(0);

        // For all existing BufferViews, create new ones that are updated to 
        // refer to the new binary glTF buffer, with the appropriate offset
        Map<String, BufferView> oldBufferViews = 
            copy(outputGltf.getBufferViews());
        Map<String, BufferView> newBufferViews = 
            new LinkedHashMap<String, BufferView>();
        for (Entry<String, BufferView> oldEntry : oldBufferViews.entrySet())
        {
            String id = oldEntry.getKey();
            BufferView oldBufferView = oldEntry.getValue();
            BufferView newBufferView = GltfUtilsV1.copy(oldBufferView);

            newBufferView.setBuffer(BinaryGltfV1.getBinaryGltfBufferId());
            String oldBufferId = oldBufferView.getBuffer();
            int oldByteOffset = oldBufferView.getByteOffset();
            int bufferOffset = bufferOffsets.get(oldBufferId);
            int newByteOffset = oldByteOffset + bufferOffset;
            newBufferView.setByteOffset(newByteOffset);

            newBufferViews.put(id, newBufferView);
        }

        // For all existing Images, create new ones that are updated to 
        // refer to the new binary glTF buffer, using a bufferView ID
        // (in the binary_glTF extension object) that refers to a newly
        // created BufferView
        Map<String, Image> newImages = 
            new LinkedHashMap<String, Image>();
        for (Entry<String, Image> oldEntry : oldImages.entrySet())
        {
            String id = oldEntry.getKey();
            Image oldImage = oldEntry.getValue();
            Image newImage = GltfUtilsV1.copy(oldImage);

            // Create the BufferView for the image
            ByteBuffer imageData = 
                imageIdToImage.get(id).getImageData();
            int byteLength = imageData.capacity();
            int byteOffset = imageOffsets.get(id);
            BufferView imageBufferView = new BufferView();
            imageBufferView.setBuffer(BinaryGltfV1.getBinaryGltfBufferId());
            imageBufferView.setByteOffset(byteOffset);
            imageBufferView.setByteLength(byteLength);

            // Store the BufferView under a newly generated ID
            String generatedBufferViewId = 
                GltfIds.generateId("bufferView_for_image_" + id, 
                    oldBufferViews.keySet());
            newBufferViews.put(generatedBufferViewId, imageBufferView);

            // Let the image refer to the BufferView via its extension object
            BinaryGltfV1.setBinaryGltfBufferViewId(
                newImage, generatedBufferViewId);

            // Set the width, height and mimeType properties for the
            // extension object
            BinaryGltfV1.setBinaryGltfImageProperties(newImage, imageData);

            newImages.put(id, newImage);
        }

        // For all existing Shaders, create new ones that are updated to 
        // refer to the new binary glTF buffer using a bufferView ID
        // (in the binary_glTF extension object) that refers to a newly
        // created BufferView
        Map<String, Shader> newShaders = 
            new LinkedHashMap<String, Shader>();
        for (Entry<String, Shader> oldEntry : oldShaders.entrySet())
        {
            String id = oldEntry.getKey();
            Shader oldShader = oldEntry.getValue();
            Shader newShader = GltfUtilsV1.copy(oldShader);

            // Create the BufferView for the shader
            ByteBuffer shaderData = 
                shaderIdToShader.get(id).getShaderData();
            int byteLength = shaderData.capacity();
            int byteOffset = shaderOffsets.get(id);
            BufferView shaderBufferView = new BufferView();
            shaderBufferView.setBuffer(BinaryGltfV1.getBinaryGltfBufferId());
            shaderBufferView.setByteOffset(byteOffset);
            shaderBufferView.setByteLength(byteLength);

            // Store the BufferView under a newly generated ID
            String generatedBufferViewId =
                GltfIds.generateId("bufferView_for_shader_" + id, 
                    oldBufferViews.keySet());
            newBufferViews.put(generatedBufferViewId, shaderBufferView);

            // Let the shader refer to the BufferView via its extension object
            BinaryGltfV1.setBinaryGltfBufferViewId(
                newShader, generatedBufferViewId);

            newShaders.put(id, newShader);
        }

        // Place the newly created mappings into the output glTF,
        // if there have been non-null mappings for them in the input
        if (!newBuffers.isEmpty())
        {
            outputGltf.setBuffers(newBuffers);
        }
        if (!newImages.isEmpty())
        {
            outputGltf.setImages(newImages);
        }
        if (!newShaders.isEmpty())
        {
            outputGltf.setShaders(newShaders);
        }
        if (!newBufferViews.isEmpty())
        {
            outputGltf.setBufferViews(newBufferViews);
        }
        return new GltfAssetV1(outputGltf, binaryGltfByteBuffer);
    }


    /**
     * Compute the total size that is required for the binary glTF buffer
     * for the given {@link GltfModel}, which is the sum of all buffer
     * sizes of all buffers, images and shaders.
     * 
     * @param gltfModel The {@link GltfModel}
     * @return The total size for the binary glTF buffer
     */
    private static int computeBinaryGltfBufferSize(GltfModelV1 gltfModel)
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
        for (ShaderModel shaderModel : gltfModel.getShaderModels())
        {
            ByteBuffer shaderData = shaderModel.getShaderData();
            binaryGltfBufferSize += shaderData.capacity();
        }
        return binaryGltfBufferSize;
    }
    
    
    /**
     * Put the contents of all byte buffers that are associated with the
     * given keys into the given target buffer. This method assumes
     * that the target buffer has a sufficient capacity to hold all 
     * buffers.
     * 
     * @param <K> The key type
     * 
     * @param keys The mapping keys
     * @param keyToByteBuffer The function that provides the byte buffer 
     * based on the key of the element
     * @param targetBuffer The target buffer
     * @return A mapping from each key to the offset inside the target buffer
     */
    private static <K> Map<K, Integer> concatBuffers(
        Iterable<K> keys, 
        Function<? super K, ? extends ByteBuffer> keyToByteBuffer,
        ByteBuffer targetBuffer)
    {
        Map<K, Integer> offsets = new LinkedHashMap<K, Integer>();
        for (K key : keys)
        {
            ByteBuffer oldByteBuffer = keyToByteBuffer.apply(key);
            int offset = targetBuffer.position();
            offsets.put(key, offset);
            targetBuffer.put(oldByteBuffer.slice());
        }
        return offsets;
    }

    /**
     * Creates a copy of the given map, as a linked hash map. If the given
     * map is <code>null</code>, then an unmodifiable empty map will be
     * returned
     * 
     * @param map The input map
     * @return The copy
     */
    private static <K, V> Map<K, V> copy(Map<K, V> map)
    {
        if (map == null)
        {
            return Collections.emptyMap();
        }
        return new LinkedHashMap<K, V>(map);
    }
    

}
