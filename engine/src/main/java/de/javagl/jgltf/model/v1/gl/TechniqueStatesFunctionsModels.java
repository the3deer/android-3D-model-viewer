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

import de.javagl.jgltf.impl.v1.TechniqueStatesFunctions;
import de.javagl.jgltf.model.Optionals;
import de.javagl.jgltf.model.gl.TechniqueStatesFunctionsModel;
import de.javagl.jgltf.model.gl.impl.DefaultTechniqueStatesFunctionsModel;

/**
 * Methods to create {@link TechniqueStatesFunctionsModel} instances from
 * glTF 1.0 {@link TechniqueStatesFunctions} objects
 */
public class TechniqueStatesFunctionsModels
{
    /**
     * Creates a new {@link TechniqueStatesFunctionsModel} object from the
     * given {@link TechniqueStatesFunctions}
     * 
     * @param techniqueStatesFunctions The {@link TechniqueStatesFunctions}
     * @return The {@link TechniqueStatesFunctionsModel}
     */
    public static DefaultTechniqueStatesFunctionsModel create(
        TechniqueStatesFunctions techniqueStatesFunctions)
    {
        DefaultTechniqueStatesFunctionsModel techniqueStatesFunctionsModel = 
            new DefaultTechniqueStatesFunctionsModel();
        techniqueStatesFunctionsModel.setBlendColor(Optionals.clone(
            techniqueStatesFunctions.getBlendColor()));
        techniqueStatesFunctionsModel.setBlendEquationSeparate(Optionals.clone(
            techniqueStatesFunctions.getBlendEquationSeparate()));
        techniqueStatesFunctionsModel.setBlendFuncSeparate(Optionals.clone(
            techniqueStatesFunctions.getBlendFuncSeparate()));
        techniqueStatesFunctionsModel.setColorMask(Optionals.clone(
            techniqueStatesFunctions.getColorMask()));
        techniqueStatesFunctionsModel.setCullFace(Optionals.clone(
            techniqueStatesFunctions.getCullFace()));
        techniqueStatesFunctionsModel.setDepthFunc(Optionals.clone(
            techniqueStatesFunctions.getDepthFunc()));
        techniqueStatesFunctionsModel.setDepthMask(Optionals.clone(
            techniqueStatesFunctions.getDepthMask()));
        techniqueStatesFunctionsModel.setDepthRange(Optionals.clone(
            techniqueStatesFunctions.getDepthRange()));
        techniqueStatesFunctionsModel.setFrontFace(Optionals.clone(
            techniqueStatesFunctions.getFrontFace()));
        techniqueStatesFunctionsModel.setLineWidth(Optionals.clone(
            techniqueStatesFunctions.getLineWidth()));
        techniqueStatesFunctionsModel.setPolygonOffset(Optionals.clone(
            techniqueStatesFunctions.getPolygonOffset()));
     
        return techniqueStatesFunctionsModel;
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private TechniqueStatesFunctionsModels()
    {
        // Private constructor to prevent instantiation
    }
}
