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
package de.javagl.jgltf.model.gl;

import java.util.Map;

import de.javagl.jgltf.model.NamedModelElement;

/**
 * Interface for a rendering technique. Such a technique may be part of a 
 * core glTF 1.0 asset, an extension of a glTF 2.0 asset, or a custom 
 * implementation that is used internally.
 */
public interface TechniqueModel extends NamedModelElement
{
    /**
     * Returns the {@link ProgramModel} that is used for implementing this
     * technique
     * 
     * @return The {@link ProgramModel}
     */
    ProgramModel getProgramModel();

    /**
     * Returns an unmodifiable map that maps parameter names to 
     * {@link TechniqueParametersModel} instances
     * 
     * @return The parameters
     */
    Map<String, TechniqueParametersModel> getParameters();
    
    /**
     * Returns an unmodifiable map that maps attribute names to 
     * parameter names
     * 
     * @return The mapping from attribute names to parameter names
     */
    Map<String, String> getAttributes();
    
    /**
     * Returns the {@link TechniqueParametersModel} for the attribute
     * with the given name, or <code>null</code> if no such parameter
     * exists. This is a convenience function and equivalent to 
     * <pre><code>
     * techniqueModel.getParameters().get(
     *     techniqueModel.getAttributes().get(attributeName));
     * </code></pre>,
     * handling <code>null</code>-cases accordingly.
     * 
     * @param attributeName The attribute name
     * @return The {@link TechniqueParametersModel}
     */
    TechniqueParametersModel getAttributeParameters(String attributeName);
    
    /**
     * Returns an unmodifiable map that maps uniform names to 
     * parameter names
     * 
     * @return The uniforms
     */
    Map<String, String> getUniforms();

    /**
     * Returns the {@link TechniqueParametersModel} for the uniform
     * with the given name, or <code>null</code> if no such parameter
     * exists. This is a convenience function and equivalent to 
     * <pre><code>
     * techniqueModel.getParameters().get(
     *     techniqueModel.getUniforms().get(uniformName));
     * </code></pre>,
     * handling <code>null</code>-cases accordingly.
     * 
     * @param uniformName The uniform name
     * @return The {@link TechniqueParametersModel}
     */
    TechniqueParametersModel getUniformParameters(String uniformName);
    
    /**
     * Returns the {@link TechniqueStatesModel}, or <code>null</code> if the
     * default technique states should be used.
     * 
     * @return The {@link TechniqueStatesModel}
     */
    TechniqueStatesModel getTechniqueStatesModel();
}

