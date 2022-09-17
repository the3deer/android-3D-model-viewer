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
package de.javagl.jgltf.model.io;

import java.io.IOException;
import java.io.OutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * A class for writing a glTF as JSON
 */
public final class GltfWriter
{
    /**
     * Whether the JSON output should be indented
     */
    private boolean indenting;
    
    /**
     * Creates a new glTF writer. By default, the output written by this class
     * will be indented.
     */
    public GltfWriter()
    {
        this.indenting = true;
    }
    
    /**
     * Set whether the JSON output should be indented
     * 
     * @param indenting whether the JSON output should be indented
     */
    public void setIndenting(boolean indenting)
    {
        this.indenting = indenting;
    }
    
    /**
     * Returns whether the JSON output will be indented
     * 
     * @return Whether the JSON output will be indented
     */
    public boolean isIndenting()
    {
        return indenting;
    }
    
    /**
     * Write the given glTF to the given output stream. The caller
     * is responsible for closing the stream.
     * 
     * @param gltf The glTF
     * @param outputStream The output stream
     * @throws IOException If an IO error occurred
     */
    public void write(Object gltf, OutputStream outputStream) 
        throws IOException 
    {
        ObjectMapper objectMapper = JacksonUtils.createObjectMapper();
        if (indenting)
        {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
        objectMapper.writeValue(outputStream, gltf);
    }
    
}


