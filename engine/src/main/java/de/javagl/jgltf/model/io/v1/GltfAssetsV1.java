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

import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.v1.GltfModelV1;

/**
 * Utility methods related to {@link GltfAssetV1} instances.<br>
 * <br>
 * This class should not be considered as part of the public API. It may
 * change or be omitted in the future.
 */
public class GltfAssetsV1
{
    /**
     * Create a new default {@link GltfAssetV1} from the given 
     * {@link GltfModel}
     * 
     * @param gltfModel The {@link GltfModel}
     * @return The {@link GltfAssetV1}
     */
    public static GltfAssetV1 createDefault(GltfModelV1 gltfModel)
    {
        DefaultAssetCreatorV1 assetCreator = new DefaultAssetCreatorV1();
        GltfAssetV1 gltfAsset = assetCreator.create(gltfModel);
        return gltfAsset;
    }
    
    /**
     * Create a new binary {@link GltfAssetV1} from the given 
     * {@link GltfModel}
     * 
     * @param gltfModel The {@link GltfModel}
     * @return The {@link GltfAssetV1}
     */
    public static GltfAssetV1 createBinary(GltfModelV1 gltfModel)
    {
        BinaryAssetCreatorV1 assetCreator = new BinaryAssetCreatorV1();
        GltfAssetV1 gltfAsset = assetCreator.create(gltfModel);
        return gltfAsset;
    }
    
    /**
     * Create a new embedded {@link GltfAssetV1} from the given 
     * {@link GltfModel}
     * 
     * @param gltfModel The {@link GltfModel}
     * @return The {@link GltfAssetV1}
     */
    public static GltfAssetV1 createEmbedded(GltfModelV1 gltfModel)
    {
        EmbeddedAssetCreatorV1 assetCreator = new EmbeddedAssetCreatorV1();
        GltfAssetV1 gltfAsset = assetCreator.create(gltfModel);
        return gltfAsset;
    }
    
    
    
    /**
     * Private constructor to prevent instantiation
     */
    private GltfAssetsV1()
    {
        // Private constructor to prevent instantiation
    }


}
