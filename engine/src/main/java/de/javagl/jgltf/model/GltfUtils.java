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

import de.javagl.jgltf.impl.v1.Asset;
import de.javagl.jgltf.impl.v1.GlTF;

/**
 * Utility methods related to {@link GlTF} instances
 */
public class GltfUtils
{
    /**
     * Returns the version string that is reported by the {@link Asset} in
     * the given {@link GlTF}. If it does not have an {@link Asset}, or
     * the version string in the asset is <code>null</code>, then
     * this method will return the string <code>"1.0.0"</code>.
     * 
     * @param gltf The {@link GlTF}
     * @return The version string
     */
    public static String getVersion(GlTF gltf)
    {
        Objects.requireNonNull(gltf, "The gltf is null");
        Asset asset = gltf.getAsset();
        if (asset == null)
        {
            return "1.0";
        }
        String version = asset.getVersion();
        if (version == null)
        {
            return "1.0";
        }
        return version;
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private GltfUtils()
    {
        // Private constructor to prevent instantiation    
    }
}




