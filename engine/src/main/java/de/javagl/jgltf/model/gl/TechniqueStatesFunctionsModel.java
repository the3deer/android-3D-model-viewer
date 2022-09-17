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

/**
 * Interface for technique state function parameters. <br>
 * <br>
 * <b>Note: The method comments here are placeholders. For details, refer 
 * to the glTF 1.0 specification and an OpenGL documentation!</b><br>
 * <br>
 * Note: The methods in this interface may return <i>references</i>
 * to arrays that are stored internally. Callers should not store
 * or modify the returned arrays! 
 */
public interface TechniqueStatesFunctionsModel
{
    /**
     * Returns the blend color
     * 
     * @return The blend color
     */
    float[] getBlendColor();
    
    /**
     * Returns the blend equation
     * 
     * @return the blend equation
     */
    int[] getBlendEquationSeparate();
    
    /**
     * Returns the blend function
     * 
     * @return The blend function
     */
    int[] getBlendFuncSeparate();
    
    /**
     * Returns the color mask
     * 
     * @return The color mask
     */
    boolean[] getColorMask();
    
    /**
     * Returns the cull face
     * 
     * @return The cull face
     */
    int[] getCullFace();
    
    /**
     * Returns the depth func
     * 
     * @return The depth func
     */
    int[] getDepthFunc();
    
    /**
     * Returns the depth mask
     * 
     * @return The depth mask
     */
    boolean[] getDepthMask();
    
    /**
     * Returns the depth range
     *  
     * @return The depth range
     */
    float[] getDepthRange();
    
    /**
     * Returns the front face
     * 
     * @return The front face
     */
    int[] getFrontFace();
    
    /**
     * Returns the line width
     * 
     * @return The line width
     */
    float[] getLineWidth();
    
    /**
     * Returns the polygon offset
     * 
     * @return The polygon offset
     */
    float[] getPolygonOffset();
}
