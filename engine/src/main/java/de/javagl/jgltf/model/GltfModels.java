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
package de.javagl.jgltf.model;

import de.javagl.jgltf.model.io.GltfAsset;
import de.javagl.jgltf.model.io.v1.GltfAssetV1;
import de.javagl.jgltf.model.io.v2.GltfAssetV2;
import de.javagl.jgltf.model.v1.GltfModelV1;
import de.javagl.jgltf.model.v2.GltfModelCreatorV2;

/**
 * Methods to create {@link GltfModel} instances from a {@link GltfAsset}
 */
public class GltfModels
{
    /**
     * Creates a {@link GltfModel} instance from the given {@link GltfAsset}
     * 
     * @param gltfAsset The {@link GltfAsset}
     * @return The {@link GltfModel}
     * @throws IllegalArgumentException If the given asset has an 
     * unknown version
     */
    public static GltfModel create(GltfAsset gltfAsset)
    {
        if (gltfAsset instanceof GltfAssetV1)
        {
            GltfAssetV1 gltfAssetV1 = (GltfAssetV1)gltfAsset;
            return new GltfModelV1(gltfAssetV1);
        }
        if (gltfAsset instanceof GltfAssetV2)
        {
            GltfAssetV2 gltfAssetV2 = (GltfAssetV2)gltfAsset;
            return GltfModelCreatorV2.create(gltfAssetV2);
        }
        throw new IllegalArgumentException(
            "The glTF asset has an unknown version: " + gltfAsset);
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private GltfModels()
    {
        // Private constructor to prevent instantiation
    }
}
