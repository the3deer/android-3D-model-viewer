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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import javax.imageio.ImageReader;

import de.javagl.jgltf.model.image.ImageReaders;

/**
 * Utility methods to related to the MIME type from data URLs or image data
 */
public class MimeTypes
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(MimeTypes.class.getName());
    
    /**
     * Tries to detect the format of the image data from the given URI, and 
     * return the corresponding MIME type string.<br>
     * <br>
     * This may,  for example, be <code>"image/png"</code> or 
     * <code>"image/gif"</code> or <code>"image/jpeg"</code> (<b>not</b> 
     * <code>"image/jpg"</code>!).<br> 
     * <br>
     *  
     * @param uriString The image data
     * @return The image format string, or <code>null</code> if it can not
     * be detected.
     */
    private static String guessImageMimeTypeString(String uriString)
    {
        try
        {
            URI uri = new URI(uriString);
            if ("data".equalsIgnoreCase(uri.getScheme()))
            {
                String raw = uri.getRawSchemeSpecificPart().toLowerCase();
                String type = getStringBetween(raw, "image/", ";base64");
                return "image/" + type.toLowerCase();
            }
        } 
        catch (URISyntaxException e)
        {
            return null;
        }
        int lastDotIndex = uriString.lastIndexOf('.');
        if (lastDotIndex == -1)
        {
            return null;
        }
        String end = uriString.substring(lastDotIndex + 1).toLowerCase();
        if (end.equalsIgnoreCase("jpg") || end.equalsIgnoreCase("jpeg"))
        {
            return "image/jpeg";
        }
        return "image/" + end.toLowerCase();
    }
    
    /**
     * Returns the part of the input string between the given "before" and
     * "after" part, or <code>null</code> if either of the given parts are
     * not contained in the input string, or the "after" part appears 
     * before the "before" part.
     * 
     * @param input The input string
     * @param before The "before" part
     * @param after The "after" part
     * @return The string between "before" and "after"
     */
    private static String getStringBetween(
        String input, String before, String after)
    {
        int beforeIndex = input.indexOf(before);
        if (beforeIndex < 0)
        {
            return null;
        }
        int afterIndex = input.indexOf(after);
        if (afterIndex < beforeIndex)
        {
            return null;
        }
        return input.substring(beforeIndex + before.length(), afterIndex);
    }
    
    
    /**
     * Tries to detect the format of the given image data, and return the
     * corresponding MIME type string.<br>
     * <br>
     * This may,  for example, be <code>"image/png"</code> or 
     * <code>"image/gif"</code> or <code>"image/jpeg"</code> (<b>not</b> 
     * <code>"image/jpg"</code>!).<br> 
     * <br>
     *  
     * @param imageData The image data
     * @return The image format string
     * @throws IOException If the image format can not be detected
     */
    private static String guessImageMimeTypeString(ByteBuffer imageData) 
        throws IOException
    {
        ImageReader imageReader = null;
        try
        {
            imageReader = ImageReaders.findImageReader(imageData);
            return "image/" + imageReader.getFormatName().toLowerCase();
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
     * Tries to detect the format of the given image data, and return the
     * corresponding MIME type string.<br>
     * <br>
     * This may,  for example, be <code>"image/png"</code> or 
     * <code>"image/gif"</code> or <code>"image/jpeg"</code> (<b>not</b> 
     * <code>"image/jpg"</code>!).<br> 
     * <br>   
     * @param imageData The image data
     * @return The image format string
     */
    public static String guessImageMimeTypeStringUnchecked(ByteBuffer imageData) 
    {
        try
        {
            return guessImageMimeTypeString(imageData);
        }
        catch (IOException e)
        {
            return null;
        }
    }
    
    /**
     * Tries to detect the format of the given image URI and its data and 
     * return the corresponding MIME type string.<br> 
     * <br>
     * This may,  for example, be <code>"image/png"</code> or 
     * <code>"image/gif"</code> or <code>"image/jpeg"</code> (<b>not</b> 
     * <code>"image/jpg"</code>!).<br> 
     * <br>
     * This method will do an (unspecified) best-effort approach to detect 
     * the mime type, either from the image or from the image data (which
     * are both optional). If the type can not be determined, then 
     * <code>null</code> will be returned. 
     *  
     * @param uriString The URI string
     * @param imageData The image data
     * @return The image format string, or <code>null</code> if it can not
     * be detected.
     */
    public static String guessImageMimeTypeString(
        String uriString, ByteBuffer imageData)
    {
        if (uriString != null)
        {
            String imageMimeTypeString = 
                MimeTypes.guessImageMimeTypeString(uriString);
            if (imageMimeTypeString != null)
            {
                return imageMimeTypeString;
            }
        }
        if (imageData != null)
        {
            return guessImageMimeTypeStringUnchecked(imageData);
        }
        return null;
    }

    /**
     * Obtain the file name extension (without the <code>"."</code> dot) for
     * an image file with the given MIME type. Valid input and return values
     * are
     * <ul>
     *   <li><code>"image/jpeg"</code> : <code>"jpg"</code></li>
     *   <li><code>"image/png"</code> : <code>"png"</code></li>
     *   <li><code>"image/gif"</code> : <code>"gif"</code></li>
     * </ul>
     * For other inputs, a warning will be printed, and <code>null</code> will
     * be returned.
     *  
     * @param mimeTypeString The MIME type string
     * @return The file extension
     */
    public static String imageFileNameExtensionForMimeTypeString(
        String mimeTypeString)
    {
        if ("image/jpeg".equals(mimeTypeString))
        {
            return "jpg";
        }
        if ("image/png".equals(mimeTypeString))
        {
            return "png";
        }
        if ("image/gif".equals(mimeTypeString))
        {
            return "gif";
        }
        logger.warning("Invalid MIME type string: " + mimeTypeString);
        return null;
    }
    

    /**
     * Private constructor to prevent instantiation
     */
    private MimeTypes()
    {
        // Private constructor to prevent instantiation
    }
}
