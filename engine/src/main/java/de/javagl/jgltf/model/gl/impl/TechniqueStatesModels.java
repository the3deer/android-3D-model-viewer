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
package de.javagl.jgltf.model.gl.impl;

import java.util.Arrays;
import java.util.List;

import de.javagl.jgltf.impl.v1.TechniqueStatesFunctions;
import de.javagl.jgltf.model.GltfConstants;
import de.javagl.jgltf.model.gl.TechniqueStatesFunctionsModel;
import de.javagl.jgltf.model.gl.TechniqueStatesModel;
import de.javagl.jgltf.model.v1.gl.TechniqueStatesFunctionsModels;

/**
 * Methods to create {@link TechniqueStatesModel} instances
 */
public class TechniqueStatesModels
{
    /**
     * Create a default {@link TechniqueStatesModel}
     * 
     * @return The {@link TechniqueStatesModel}
     */
    public static TechniqueStatesModel createDefault()
    {
        TechniqueStatesModel techniqueStatesModel = 
            new DefaultTechniqueStatesModel(
                createDefaultTechniqueStatesEnable(), 
                createDefaultTechniqueStatesFunctions());
        return techniqueStatesModel;
    }
    
    /**
     * Create the default {@link TechniqueStatesFunctionsModel}
     * @return The {@link TechniqueStatesFunctionsModel}
     */
    public static TechniqueStatesFunctionsModel 
        createDefaultTechniqueStatesFunctions()
    {
        TechniqueStatesFunctions functions = 
            de.javagl.jgltf.model.v1.gl.Techniques
                .createDefaultTechniqueStatesFunctions();
        TechniqueStatesFunctionsModel techniqueStatesFunctionsModel =
            TechniqueStatesFunctionsModels.create(functions);
        return techniqueStatesFunctionsModel;
    }
    
    /**
     * Returns the default {@link TechniqueStatesModel#getEnable() enable}
     * states 
     * 
     * @return The default enable states
     */
    public static List<Integer> createDefaultTechniqueStatesEnable()
    {
        List<Integer> enable = Arrays.asList(
            GltfConstants.GL_DEPTH_TEST, 
            GltfConstants.GL_CULL_FACE
        );
        return enable;
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private TechniqueStatesModels()
    {
        // Private constructor to prevent instantiation
    }
}
