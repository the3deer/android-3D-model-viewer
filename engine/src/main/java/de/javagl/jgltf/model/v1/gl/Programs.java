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

import de.javagl.jgltf.impl.v1.Material;
import de.javagl.jgltf.impl.v1.Program;
import de.javagl.jgltf.impl.v1.Shader;

/**
 * Utility methods for {@link Program}s
 */
class Programs
{
    /**
     * Creates a default {@link Program} with the given vertex- and 
     * fragment {@link Shader} IDs, which are assumed to refer to
     * the {@link Shaders#createDefaultVertexShader() default vertex shader}
     * and {@link Shaders#createDefaultFragmentShader() default fragment 
     * shader}.<br>
     * <br>
     * The returned {@link Program} is the {@link Program} for the default 
     * {@link Material}, as described in "Appendix A" of the 
     * glTF 1.0 specification. 
     * 
     * @param vertexShaderId The vertex {@link Shader} ID
     * @param fragmentShaderId The fragment {@link Shader} ID
     * @return The default {@link Program}
     */
    static Program createDefaultProgram(
        String vertexShaderId, String fragmentShaderId)
    {
        Program program = new Program();
        program.setVertexShader(vertexShaderId);
        program.setFragmentShader(fragmentShaderId);
        program.addAttributes("a_position");
        return program;
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private Programs()
    {
        // Private constructor to prevent instantiation
    }

}
