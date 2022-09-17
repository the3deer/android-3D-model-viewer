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
package de.javagl.jgltf.model.v2.gl;

import de.javagl.jgltf.impl.v2.Material;
import de.javagl.jgltf.impl.v2.MaterialPbrMetallicRoughness;

/**
 * Methods to create instances of classes related to a {@link Material}
 */
public class Materials
{
    /**
     * Create a {@link Material} with all default values
     * 
     * @return The {@link Material}
     */
    public static Material createDefaultMaterial()
    {
        Material material = new Material();
        material.setPbrMetallicRoughness(
            createDefaultMaterialPbrMetallicRoughness());
        material.setNormalTexture(null);
        material.setOcclusionTexture(null);
        material.setEmissiveTexture(null);
        material.setEmissiveFactor(material.defaultEmissiveFactor());
        material.setAlphaMode(material.defaultAlphaMode());
        material.setAlphaCutoff(material.defaultAlphaCutoff());
        material.setDoubleSided(material.defaultDoubleSided());
        return material;
    }
    
    /**
     * Create a {@link MaterialPbrMetallicRoughness} with all default values
     * 
     * @return The {@link MaterialPbrMetallicRoughness}
     */
    public static MaterialPbrMetallicRoughness 
        createDefaultMaterialPbrMetallicRoughness()
    {
        MaterialPbrMetallicRoughness result = 
            new MaterialPbrMetallicRoughness();
        result.setBaseColorFactor(result.defaultBaseColorFactor());
        result.setMetallicFactor(result.defaultMetallicFactor());
        result.setRoughnessFactor(result.defaultRoughnessFactor());
        return result;
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private Materials()
    {
        // Private constructor to prevent instantiation
    }
}
