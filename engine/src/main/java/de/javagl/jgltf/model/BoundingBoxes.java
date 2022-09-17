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
package de.javagl.jgltf.model;

import java.util.Objects;

/**
 * Utility methods for computing bounding boxes.<br>
 * <br>
 * <b>Note:</b> This class is preliminary. It may be replaced with more
 * sophisticated bounding box computation methods in the future.
 */
public class BoundingBoxes
{
    /**
     * Compute the bounding box of the given {@link GltfModel}. The result
     * will be an array <code>[minX, minY, minZ, maxX, maxY, maxZ]</code>.
     * 
     * @param gltfModel The {@link GltfModel}
     * @return The bounding box
     */
    public static float[] computeBoundingBoxMinMax(GltfModel gltfModel)
    {
        Objects.requireNonNull(gltfModel, "The gltfModel may not be null");
        
        BoundingBoxComputer boundingBoxComputer =
            new BoundingBoxComputer(gltfModel);
        BoundingBox boundingBox = boundingBoxComputer.compute();
        
        float result[] = {
            boundingBox.getMinX(),
            boundingBox.getMinY(),
            boundingBox.getMinZ(),
            boundingBox.getMaxX(),
            boundingBox.getMaxY(),
            boundingBox.getMaxZ()
        };
        return result;
        
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private BoundingBoxes()
    {
        // Private constructor to prevent instantiation
    }
}
