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

import java.nio.ByteBuffer;

import de.javagl.jgltf.model.gl.ShaderModel;
import de.javagl.jgltf.model.impl.AbstractNamedModelElement;
import de.javagl.jgltf.model.io.Buffers;

/**
 * Implementation of a {@link ShaderModel}
 */
public class DefaultShaderModel extends AbstractNamedModelElement
    implements ShaderModel
{
    /**
     * The URI 
     */
    private final String uri;
    
    /**
     * The actual raw shader data
     */
    private ByteBuffer shaderData;

    /**
     * The {@link ShaderType}
     */
    private final ShaderType shaderType;
    
    /**
     * Default constructor 
     * 
     * @param uri The URI
     * @param shaderType The 
     * {@link ShaderType}
     */
    public DefaultShaderModel(String uri, ShaderType shaderType)
    {
        this.uri = uri;
        this.shaderType = shaderType;
    }
    
    /**
     * Set the data of this shader
     * 
     * @param shaderData The shader data
     */
    public void setShaderData(ByteBuffer shaderData)
    {
        this.shaderData = shaderData;
    }

    @Override
    public String getUri()
    {
        return uri;
    }

    @Override
    public ByteBuffer getShaderData()
    {
        return Buffers.createSlice(shaderData);
    }

    @Override
    public String getShaderSource()
    {
        return Buffers.readAsString(shaderData);
    }
    
    @Override
    public ShaderType getShaderType()
    {
        return shaderType;
    }
}
