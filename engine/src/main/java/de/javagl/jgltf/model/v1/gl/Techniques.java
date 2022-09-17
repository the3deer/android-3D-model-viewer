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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.javagl.jgltf.impl.v1.Material;
import de.javagl.jgltf.impl.v1.Program;
import de.javagl.jgltf.impl.v1.Technique;
import de.javagl.jgltf.impl.v1.TechniqueParameters;
import de.javagl.jgltf.impl.v1.TechniqueStates;
import de.javagl.jgltf.impl.v1.TechniqueStatesFunctions;
import de.javagl.jgltf.model.GltfConstants;

/**
 * Utility methods related to {@link Technique}s
 */
public class Techniques
{
    /**
     * Create a default {@link Technique} with the given {@link Program} ID,
     * which is assumed to refer to a {@link Programs#createDefaultProgram(
     * String, String) default program}.<br>
     * <br>
     * The returned {@link Technique} is the {@link Technique} for the 
     * default {@link Material}, as described in "Appendix A" of the 
     * glTF 1.0 specification. 
     * 
     * @param programId The {@link Program} ID
     * @return The default {@link Technique}
     */
    static Technique createDefaultTechnique(String programId)
    {
        Technique technique = new Technique();
        technique.addAttributes("a_position", "position");
        technique.addParameters("modelViewMatrix", 
            createDefaultTechniqueParameters(
                "MODELVIEW", GltfConstants.GL_FLOAT_MAT4, null));
        technique.addParameters("projectionMatrix", 
            createDefaultTechniqueParameters(
                "PROJECTION", GltfConstants.GL_FLOAT_MAT4, null));
        technique.addParameters("emission", 
            createDefaultTechniqueParameters(
                null, GltfConstants.GL_FLOAT_VEC4, 
                Arrays.asList(0.5f, 0.5f, 0.5f, 1.0f)));
        technique.addParameters("position", 
            createDefaultTechniqueParameters(
                "POSITION", GltfConstants.GL_FLOAT_VEC3, null));
        technique.setStates(createDefaultTechniqueStates());
        technique.setProgram(programId);
        
        technique.addUniforms("u_modelViewMatrix", "modelViewMatrix");
        technique.addUniforms("u_projectionMatrix", "projectionMatrix");
        technique.addUniforms("u_emission", "emission");
        
        return technique;
    }
    
    /**
     * Create the default {@link TechniqueStates}
     * 
     * @return The default {@link TechniqueStates}
     */
    private static TechniqueStates createDefaultTechniqueStates()
    {
        TechniqueStates techniqueStates = new TechniqueStates();
        techniqueStates.setEnable(
            new ArrayList<Integer>(techniqueStates.defaultEnable()));
        techniqueStates.setFunctions(createDefaultTechniqueStatesFunctions());
        return techniqueStates;
    }
    
    /**
     * Create the default {@link TechniqueStatesFunctions}
     *  
     * @return The default {@link TechniqueStatesFunctions}
     */
    public static TechniqueStatesFunctions 
        createDefaultTechniqueStatesFunctions()
    {
        TechniqueStatesFunctions techniqueStatesFunctions = 
            new TechniqueStatesFunctions();
        techniqueStatesFunctions.setBlendColor(
            techniqueStatesFunctions.defaultBlendColor());
        techniqueStatesFunctions.setBlendEquationSeparate(
            techniqueStatesFunctions.defaultBlendEquationSeparate());
        techniqueStatesFunctions.setBlendFuncSeparate(
            techniqueStatesFunctions.defaultBlendFuncSeparate());
        techniqueStatesFunctions.setColorMask(
            techniqueStatesFunctions.defaultColorMask());
        techniqueStatesFunctions.setCullFace(
            techniqueStatesFunctions.defaultCullFace());
        techniqueStatesFunctions.setDepthFunc(
            techniqueStatesFunctions.defaultDepthFunc());
        techniqueStatesFunctions.setDepthMask(
            techniqueStatesFunctions.defaultDepthMask());
        techniqueStatesFunctions.setDepthRange(
            techniqueStatesFunctions.defaultDepthRange());
        techniqueStatesFunctions.setFrontFace(
            techniqueStatesFunctions.defaultFrontFace());
        techniqueStatesFunctions.setLineWidth(
            techniqueStatesFunctions.defaultLineWidth());
        techniqueStatesFunctions.setPolygonOffset(
            techniqueStatesFunctions.defaultPolygonOffset());
        techniqueStatesFunctions.setScissor(
            techniqueStatesFunctions.defaultScissor());
        return techniqueStatesFunctions;
    }

    /**
     * Create default {@link TechniqueParameters} with the given semantic,
     * type and value
     * 
     * @param semantic The semantic
     * @param type The type
     * @param value The value
     * @return The default {@link TechniqueParameters}
     */
    private static TechniqueParameters createDefaultTechniqueParameters(
        String semantic, Integer type, Object value)
    {
        TechniqueParameters techniqueParameters = new TechniqueParameters();
        techniqueParameters.setSemantic(semantic);
        techniqueParameters.setType(type);
        techniqueParameters.setValue(value);
        return techniqueParameters;
    }
    
    /**
     * Returns the set of states that should be enabled for the given 
     * {@link Technique}
     * 
     * @param technique The {@link Technique}
     * @return The enabled states
     */
    public static List<Integer> obtainEnabledStates(Technique technique)
    {
        TechniqueStates states = obtainTechniqueStates(technique);
        List<Integer> enable = states.getEnable();
        if (enable == null)
        {
            return states.defaultEnable();
        }
        return enable;
    }

    /**
     * Return the {@link TechniqueStates} from the given {@link Technique},
     * or the {@link TechniqueStates} from the 
     * {@link GltfDefaults#getDefaultTechnique() default technique} if
     * the given {@link Technique} is <code>null</code> or does not 
     * contain any {@link TechniqueStates}
     *  
     * @param technique The {@link Technique}
     * @return The {@link TechniqueStates}
     */
    private static TechniqueStates obtainTechniqueStates(Technique technique)
    {
        TechniqueStates states = technique.getStates();
        if (states == null)
        {
            return GltfDefaults.getDefaultTechnique().getStates();
        }
        return states;
    }

    /**
     * Return the {@link TechniqueStatesFunctions} from the 
     * {@link TechniqueStates} of the given {@link Technique}, or the 
     * {@link TechniqueStatesFunctions} from the 
     * {@link GltfDefaults#getDefaultTechnique() default technique} if
     * the given {@link Technique} is <code>null</code> or does not 
     * contain any {@link TechniqueStates} or {@link TechniqueStatesFunctions}
     *  
     * @param technique The {@link Technique}
     * @return The {@link TechniqueStatesFunctions}
     */
    public static TechniqueStatesFunctions obtainTechniqueStatesFunctions(
        Technique technique)
    {
        TechniqueStates states = obtainTechniqueStates(technique);
        TechniqueStatesFunctions functions = states.getFunctions();
        if (functions == null)
        {
            TechniqueStates defaultStates = 
                GltfDefaults.getDefaultTechnique().getStates();
            return defaultStates.getFunctions();
        }
        return functions;
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private Techniques()
    {
        // Private constructor to prevent instantiation
    }
    
}
