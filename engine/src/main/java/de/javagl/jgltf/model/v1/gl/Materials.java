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
package de.javagl.jgltf.model.v1.gl;

import java.util.Arrays;

import de.javagl.jgltf.impl.v1.Material;
import de.javagl.jgltf.impl.v1.Technique;

/**
 * Utility methods for {@link Material}s
 */
class Materials
{
    /**
     * Create a default {@link Material} with the given {@link Technique} ID,
     * that is assumed to refer to a {@link Techniques#createDefaultTechnique(
     * String) default technique}.<br>
     * <br>
     * The returned {@link Material} is the default {@link Material}, as 
     * described in "Appendix A" of the glTF 1.0 specification. 
     * 
     * @param techniqueId The {@link Technique} ID
     * @return The default {@link Material}
     */
    static Material createDefaultMaterial(String techniqueId)
    {
        Material material = new Material();
        material.addValues("emission", Arrays.asList(0.5, 0.5, 0.5, 1.0));
        material.setTechnique(techniqueId);
        return material;
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private Materials()
    {
        // Private constructor to prevent instantiation
    }

}
