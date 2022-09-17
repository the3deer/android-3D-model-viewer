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
package de.javagl.jgltf.model.v1;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageReader;

import de.javagl.jgltf.impl.v1.Buffer;
import de.javagl.jgltf.impl.v1.BufferView;
import de.javagl.jgltf.impl.v1.GlTFProperty;
import de.javagl.jgltf.impl.v1.Image;
import de.javagl.jgltf.impl.v1.Shader;
import de.javagl.jgltf.model.GltfException;
import de.javagl.jgltf.model.image.ImageReaders;

/**
 * Utility methods related to the binary glTF extension of glTF 1.0
 */
public class BinaryGltfV1
{
    /**
     * The name of the KHR_binary_glTF extension
     */
    private static final String KHRONOS_BINARY_GLTF_EXTENSION_NAME = 
        "KHR_binary_glTF";
    
    /**
     * The unique, universal buffer ID for the binary data that is stored
     * in a binary glTF file
     */
    private static final String BINARY_GLTF_BUFFER_ID = "binary_glTF";

    /**
     * Returns whether the given {@link GlTFProperty} has the 
     * <code>"KHR_binary_glTF"</code> extension. This is applicable
     * to {@link Image} and {@link Shader} objects that may contain
     * an extension object with a {@link BufferView} ID instead of 
     * a URI. The {@link BufferView} ID may be obtained with 
     * {@link BinaryGltfV1#getBinaryGltfBufferViewId(GlTFProperty)}.
     * 
     * @param gltfProperty The {@link GlTFProperty}
     * @return Whether the extension exists
     */
    public static boolean hasBinaryGltfExtension(GlTFProperty gltfProperty)
    {
        return GltfExtensionsV1.hasExtension(gltfProperty, 
            KHRONOS_BINARY_GLTF_EXTENSION_NAME);
    }

    /**
     * Returns the value of the <code>"bufferView"</code> property in 
     * the <code>"KHR_binary_glTF"</code> extension object of the
     * given {@link GlTFProperty}, or <code>null</code> if either the
     * extension object or the property does not exist. 
     * 
     * @param gltfProperty The {@link GlTFProperty}
     * @return The property value
     */
    static String getBinaryGltfBufferViewId(GlTFProperty gltfProperty)
    {
        return GltfExtensionsV1.getExtensionPropertyValueAsString(
            gltfProperty, KHRONOS_BINARY_GLTF_EXTENSION_NAME, "bufferView");
    }

    /**
     * Set the value of the <code>"bufferView"</code> property in 
     * the <code>"KHR_binary_glTF"</code> extension object of the
     * given {@link GlTFProperty}. If no (or an invalid) extension
     * object already existed, it will be created or overwritten,
     * respectively. 
     * 
     * @param gltfProperty The {@link GlTFProperty}
     * @param bufferViewId The {@link BufferView} ID
     */
    public static void setBinaryGltfBufferViewId(
        GlTFProperty gltfProperty, String bufferViewId)
    {
        GltfExtensionsV1.setExtensionPropertyValue(
            gltfProperty, KHRONOS_BINARY_GLTF_EXTENSION_NAME, 
            "bufferView", bufferViewId);
    }
    
    /**
     * Set the properties for the given {@link Image} that is stored 
     * as a <code>"KHR_binary_glTF"</code> image. These properties
     * are
     * <ul> 
     *   <li>The image <code>width</code></li>
     *   <li>The image <code>height</code></li>
     *   <li>The image <code>mimeType</code></li>
     * </ul>
     * (Note that the buffer view ID is set explicitly with 
     * {@link #setBinaryGltfBufferViewId(GlTFProperty, String)})
     * 
     * @param image The image
     * @param imageData The raw image data
     * @throws GltfException If the image data cannot be analyzed to derive
     * the required information
     */
    public static void setBinaryGltfImageProperties(
        Image image, ByteBuffer imageData)
    {
        ImageReader imageReader = null;
        try
        {
            imageReader = ImageReaders.findImageReader(imageData);
            int width = imageReader.getWidth(0);
            int height = imageReader.getHeight(0);
            String mimeType = "image/" + imageReader.getFormatName();

            GltfExtensionsV1.setExtensionPropertyValue(image,
                KHRONOS_BINARY_GLTF_EXTENSION_NAME, "width", width);
            GltfExtensionsV1.setExtensionPropertyValue(image,
                KHRONOS_BINARY_GLTF_EXTENSION_NAME, "height", height);
            GltfExtensionsV1.setExtensionPropertyValue(image,
                KHRONOS_BINARY_GLTF_EXTENSION_NAME, "mimeType", mimeType);
        }
        catch (IOException e)
        {
            throw new GltfException(
                "Could not derive image properties for binary glTF", e);
        }
        finally
        {
            if (imageReader != null)
            {
                imageReader.dispose();
            }
        }
    }
    

    /**
     * Returns the name of the Khronos binary glTF extension
     * (<code>"KHR_binary_glTF"</code>)
     * 
     * @return The name of the Khronos binary glTF extension
     */
    public static String getBinaryGltfExtensionName()
    {
        return KHRONOS_BINARY_GLTF_EXTENSION_NAME;
    }

    /**
     * Returns unique ID of the binary glTF {@link Buffer} 
     * (<code>"binary_glTF"</code>)
     * 
     * @return The binary glTF {@link Buffer} ID
     */
    public static String getBinaryGltfBufferId()
    {
        return BINARY_GLTF_BUFFER_ID;
    }

    /**
     * Returns whether the given ID is the unique ID of the binary glTF
     * {@link Buffer} (<code>"binary_glTF"</code>)
     * 
     * @param id The ID
     * @return Whether the given ID is the binary glTF {@link Buffer} ID
     */
    public static boolean isBinaryGltfBufferId(String id)
    {
        return BINARY_GLTF_BUFFER_ID.equals(id);
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private BinaryGltfV1()
    {
        // Private constructor to prevent instantiation
    }
    
}
