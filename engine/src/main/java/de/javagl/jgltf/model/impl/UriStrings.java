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
package de.javagl.jgltf.model.impl;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.logging.Logger;

import de.javagl.jgltf.model.ImageModel;
import de.javagl.jgltf.model.gl.ShaderModel;
import de.javagl.jgltf.model.gl.ShaderModel.ShaderType;
import de.javagl.jgltf.model.io.MimeTypes;

/**
 * Utility methods to generate URI strings for buffers, images and shaders.<br>
 * <br>
 * This class is only intended for internal use!
 */
public class UriStrings
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(UriStrings.class.getName());
    
    /**
     * Create an unspecified, new URI string that is not yet used as 
     * a URI string.
     * 
     * @param existingUriStrings The existing URI strings
     * @return The new URI string
     */
    public static String createBufferUriString(
        Collection<? extends String> existingUriStrings)
    {
        int counter = 0;
        while (true) 
        {
            String uri = "buffer" + counter + "." + "bin";
            if (!existingUriStrings.contains(uri))
            {
                return uri;
            }
            counter++;
        }
    }
    
    /**
     * Create an unspecified, new URI string that is not yet used as 
     * a URI string
     * 
     * @param imageModel The {@link ImageModel} to create the string for
     * @param existingUriStrings The existing URI strings
     * @return The new URI string
     */
    public static String createImageUriString(ImageModel imageModel,
        Collection<? extends String> existingUriStrings)
    {
        String extensionWithoutDot = 
            determineImageFileNameExtension(imageModel);
        int counter = 0;
        while (true) 
        {
            String uri = "image" + counter + "." + extensionWithoutDot;
            if (!existingUriStrings.contains(uri))
            {
                return uri;
            }
            counter++;
        }
    }

    /**
     * Determine the extension for an image file name (without the 
     * <code>"."</code> dot), for the given {@link ImageModel}
     *  
     * @param imageModel The {@link ImageModel}
     * @return The file extension
     */
    private static String determineImageFileNameExtension(
        ImageModel imageModel)
    {
        // Try to figure out the MIME type
        String mimeTypeString = imageModel.getMimeType();
        if (mimeTypeString == null)
        {
            ByteBuffer imageData = imageModel.getImageData();
            mimeTypeString = 
                MimeTypes.guessImageMimeTypeStringUnchecked(imageData);
        }
        
        // Try to figure out the extension based on the MIME type
        if (mimeTypeString != null)
        {
            String extensionWithoutDot = 
                MimeTypes.imageFileNameExtensionForMimeTypeString(
                    mimeTypeString);
            if (extensionWithoutDot != null)
            {
                return extensionWithoutDot;
            }
        }
        logger.warning("Could not determine file extension for image URI");
        return "";
    }
    
    /**
     * Create an unspecified, new URI string that is not yet used as 
     * a URI string
     * 
     * @param shaderModel The {@link ShaderModel} to create the string for
     * @param existingUriStrings The existing URI strings
     * @return The new URI string
     */
    public static String createShaderUriString(ShaderModel shaderModel,
        Collection<? extends String> existingUriStrings)
    {
        String extensionWithoutDot = 
            determineShaderFileNameExtension(shaderModel);
        int counter = 0;
        while (true) 
        {
            String uri = "shader" + counter + "." + extensionWithoutDot;
            if (!existingUriStrings.contains(uri))
            {
                return uri;
            }
            counter++;
        }
    }
    
    /**
     * Determine the extension for a shader file name, (without the 
     * <code>"."</code> dot), for the given {@link ShaderModel}
     *  
     * @param shaderModel The {@link ShaderModel}
     * @return The file extension
     */
    private static String determineShaderFileNameExtension(
        ShaderModel shaderModel)
    {
        if (shaderModel.getShaderType() == ShaderType.VERTEX_SHADER)
        {
            return "vert";
        }
        return "frag";
    }

    /**
     * Private constructor to prevent instantiation
     */
    private UriStrings()
    {
        // Private constructor to prevent instantiation
    }

}
