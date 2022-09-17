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

import java.util.Base64;

import de.javagl.jgltf.impl.v1.Material;
import de.javagl.jgltf.impl.v1.Shader;
import de.javagl.jgltf.model.GltfConstants;

/**
 * Utility methods for {@link Shader}s.
 */
class Shaders
{
    /**
     * The source code of the default vertex shader
     */
    private static final String DEFAULT_VERTEX_SHADER_CODE = 
        "#ifdef GL_ES"  + "\n" +
        "  precision highp float;" + "\n" +
        "#endif"+ "\n" + "\n" +
        "uniform mat4 u_modelViewMatrix;" + "\n" + 
        "uniform mat4 u_projectionMatrix;" + "\n" +
        "attribute vec3 a_position;"  + "\n" +
        "void main(void)" + "\n" +
        "{" + "\n" +
        "    gl_Position = u_projectionMatrix * u_modelViewMatrix *" + "\n" +
        "        vec4(a_position,1.0);" + "\n" +
        "}" + "\n" +
        "\n";
    
    /**
     * The source code of the default fragment shader
     */
    private static final String DEFAULT_FRAGMENT_SHADER_CODE =
        "#ifdef GL_ES"  + "\n" +
        "  precision highp float;" + "\n" +
        "#endif"+ "\n" + "\n" +
        "uniform vec4 u_emission;" + "\n" +
        "void main(void)" + "\n" +
        "{" + "\n" +
        "    gl_FragColor = u_emission;" + "\n" +
        "}" + "\n" +
        "\n";
    
    
    /**
     * Creates a default vertex {@link Shader}, with an embedded 
     * representation of the source code in form of a data URI.<br>
     * <br>
     * The returned {@link Shader} is the vertex {@link Shader} for the 
     * default {@link Material}, as described in "Appendix A" of the 
     * specification. 
     * 
     * @return The default {@link Shader}
     */
    static Shader createDefaultVertexShader()
    {
        Shader shader = new Shader();
        shader.setType(GltfConstants.GL_VERTEX_SHADER);
        String encodedCode = 
            Base64.getEncoder().encodeToString(
                DEFAULT_VERTEX_SHADER_CODE.getBytes());
        String dataUriString = "data:text/plain;base64," + encodedCode;
        shader.setUri(dataUriString);
        return shader;
    }

    /**
     * Creates a default fragment {@link Shader}, with an embedded 
     * representation of the source code in form of a data URI.<br>
     * <br>
     * The returned {@link Shader} is the fragment {@link Shader} for the 
     * default {@link Material}, as described in "Appendix A" of the 
     * specification. 
     * 
     * @return The default {@link Shader}
     */
    static Shader createDefaultFragmentShader()
    {
        Shader shader = new Shader();
        shader.setType(GltfConstants.GL_FRAGMENT_SHADER);
        String encodedCode = 
            Base64.getEncoder().encodeToString(
                DEFAULT_FRAGMENT_SHADER_CODE.getBytes());
        String dataUriString = "data:text/plain;base64," + encodedCode;
        shader.setUri(dataUriString);
        return shader;
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private Shaders()
    {
        // Private constructor to prevent instantiation
    }

}
