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

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Utility methods related to {@link GltfAsset} instances.<br>
 * <br>
 * The methods in this class may be used to check the "nature" of a 
 * {@link GltfAsset}. A <i>default</i> asset may contain references with 
 * URIs that refer to a file. An <i>embedded</i> asset may contain 
 * references with URIs that are data URIs. A <i>binary</i> asset may
 * contain a binary data blob. Mixed forms of assets are possible:
 * It is possible to create an asset where one URI refers to a file,
 * and another URI is a data URI. The methods in this class allow
 * checking whether an asset is "purely" of one specific type.  
 * <br>
 * This class should not be considered as part of the public API. It may
 * change or be omitted in the future.
 */
public class GltfAssets
{
    /**
     * Returns whether the given {@link GltfAsset} is a <i>default</i>
     * asset. This means that it does <b>not</b> contain a (non-empty) 
     * {@link GltfAsset#getBinaryData() binary data} blob, and it
     * does <b>not</b> contain references with (embedded) data URIs.
     *  
     * @param gltfAsset The {@link GltfAsset}
     * @return Whether the asset is a default asset
     */
    public static boolean isDefault(GltfAsset gltfAsset)
    {
        ByteBuffer binaryData = gltfAsset.getBinaryData();
        if (binaryData != null && binaryData.capacity() > 0)
        {
            return false;
        }
        if (containsDataUriReferences(gltfAsset))
        {
            return false;
        }
        return true;
    }
    
    /**
     * Returns whether the given {@link GltfAsset} is an <i>embedded</i>
     * asset. This means that it does <b>not</b> contain a (non-empty) 
     * {@link GltfAsset#getBinaryData() binary data} blob, and it
     * does <b>not</b> contain references that refer to files.
     *  
     * @param gltfAsset The {@link GltfAsset}
     * @return Whether the asset is an embedded asset
     */
    public static boolean isEmbedded(GltfAsset gltfAsset)
    {
        ByteBuffer binaryData = gltfAsset.getBinaryData();
        if (binaryData != null && binaryData.capacity() > 0)
        {
            return false;
        }
        if (containsFileUriReferences(gltfAsset))
        {
            return false;
        }
        return true;
    }
    
    /**
     * Returns whether the given {@link GltfAsset} is <i>binary</i>
     * asset. This means that it does does <b>not</b> contain references 
     * that refer to files or have (embedded) data URIs. (Note that
     * this method returns <code>true</code> in this case even if 
     * the asset does not have a {@link GltfAsset#getBinaryData() binary data}
     * blob) 
     *  
     * @param gltfAsset The {@link GltfAsset}
     * @return Whether the asset is a binary asset
     */
    public static boolean isBinary(GltfAsset gltfAsset)
    {
        if (containsFileUriReferences(gltfAsset))
        {
            return false;
        }
        if (containsDataUriReferences(gltfAsset))
        {
            return false;
        }
        return true;
    }
    
    /**
     * Returns whether the given {@link GltfAsset} contains any 
     * {@link GltfReference} that has a URI that is <b>not</b> a data URI.<br>
     * <br>
     * If the asset does not contain any references, then 
     * <code>false</code> is returned.</br>
     * 
     * @param gltfAsset The {@link GltfAsset}
     * @return Whether the asset contains (non-data) URI references
     */
    private static boolean containsFileUriReferences(GltfAsset gltfAsset)
    {
        List<GltfReference> references = gltfAsset.getReferences();
        for (GltfReference reference : references)
        {
            String uriString = reference.getUri();
            if (!IO.isDataUriString(uriString))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns whether the given {@link GltfAsset} contains any 
     * {@link GltfReference} that has a URI that is a data URI. <br>
     * <br>
     * If the asset does not contain any references, then 
     * <code>false</code> is returned.</br>
     * 
     * @param gltfAsset The {@link GltfAsset}
     * @return Whether the asset contains data URI references
     */
    private static boolean containsDataUriReferences(GltfAsset gltfAsset)
    {
        List<GltfReference> references = gltfAsset.getReferences();
        for (GltfReference reference : references)
        {
            String uriString = reference.getUri();
            if (!IO.isDataUriString(uriString))
            {
                return true;
            }
        }
        return false;
    }
    
    
    /**
     * Private constructor to prevent instantiation
     */
    private GltfAssets()
    {
        // Private constructor to prevent instantiation
    }
}
