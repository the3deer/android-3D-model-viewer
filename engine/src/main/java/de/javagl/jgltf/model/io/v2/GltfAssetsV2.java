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

import de.javagl.jgltf.model.GltfModel;

/**
 * Utility methods related to {@link GltfAssetV2} instances.<br>
 * <br>
 * This class should not be considered as part of the public API. It may
 * change or be omitted in the future.
 */
public class GltfAssetsV2
{
    /**
     * Create a new default {@link GltfAssetV2} from the given 
     * {@link GltfModel}
     * 
     * @param gltfModel The {@link GltfModel}
     * @return The {@link GltfAssetV2}
     */
    public static GltfAssetV2 createDefault(GltfModel gltfModel)
    {
        DefaultAssetCreatorV2 assetCreator = new DefaultAssetCreatorV2();
        GltfAssetV2 gltfAsset = assetCreator.create(gltfModel);
        return gltfAsset;
    }
    
    /**
     * Create a new binary {@link GltfAssetV2} from the given 
     * {@link GltfModel}
     * 
     * @param gltfModel The {@link GltfModel}
     * @return The {@link GltfAssetV2}
     */
    public static GltfAssetV2 createBinary(GltfModel gltfModel)
    {
        BinaryAssetCreatorV2 assetCreator = new BinaryAssetCreatorV2();
        GltfAssetV2 gltfAsset = assetCreator.create(gltfModel);
        return gltfAsset;
    }
    
    /**
     * Create a new embedded {@link GltfAssetV2} from the given 
     * {@link GltfModel}
     * 
     * @param gltfModel The {@link GltfModel}
     * @return The {@link GltfAssetV2}
     */
    public static GltfAssetV2 createEmbedded(GltfModel gltfModel)
    {
        EmbeddedAssetCreatorV2 assetCreator = new EmbeddedAssetCreatorV2();
        GltfAssetV2 gltfAsset = assetCreator.create(gltfModel);
        return gltfAsset;
    }
    
    
    
    /**
     * Private constructor to prevent instantiation
     */
    private GltfAssetsV2()
    {
        // Private constructor to prevent instantiation
    }


}
