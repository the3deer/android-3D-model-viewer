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

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonStreamContext;

/**
 * A class containing information about an error that happened 
 * during JSON parsing
 */
public final class JsonError
{
    /**
     * The error message
     */
    private final String message;
    
    /**
     * The JSON stream context
     */
    private final List<String> jsonPath;
    
    /**
     * An optional throwable associated with the error
     */
    private final Throwable throwable;
    
    /**
     * Default constructor
     * @param message The error message
     * @param jsonStreamContext The JSON stream context
     * @param throwable An optional throwable associated with the error
     */
    JsonError(String message, JsonStreamContext jsonStreamContext, 
        Throwable throwable)
    {
        this.message = message;
        this.jsonPath = Collections.unmodifiableList(
            createJsonPath(jsonStreamContext));
        this.throwable = throwable;
    }
    
    /**
     * Returns the error message
     * 
     * @return The error message
     */
    public String getMessage()
    {
        return message;
    }
    
    /**
     * Returns an unmodifiable list containing the tokens describing the 
     * JSON path where the error occurred
     * 
     * @return The JSON path
     */
    public List<String> getJsonPath()
    {
        return jsonPath;
    }

    /**
     * Returns a short string representation of the {@link #getJsonPath()}
     * 
     * @return The string
     */
    public String getJsonPathString()
    {
        return jsonPath.stream().collect(Collectors.joining("."));
    }
    
    /**
     * Returns the throwable that is associated with the error.
     * This may be <code>null</code>.
     * 
     * @return The throwable associated with the error
     */
    public Throwable getThrowable()
    {
        return throwable;
    }
    
    
    
    /**
     * Create a list of strings describing the JSON path for the given
     * stream context
     * 
     * @param streamContext The stream context
     * @return The string list
     */
    private static List<String> createJsonPath(JsonStreamContext streamContext)
    {
        Collection<JsonStreamContext> list = expand(streamContext);
        return list.stream()
            .map(c -> c.getCurrentName() == null ? 
                "" : c.getCurrentName())
            .collect(Collectors.toList());
    }
    
    /**
     * Create a collection consisting of stream contexts, starting at the root
     * node and ending at the given stream context
     *  
     * @param streamContext The stream context
     * @return The collection
     */
    private static Collection<JsonStreamContext> expand(
        JsonStreamContext streamContext)
    {
        Deque<JsonStreamContext> collection = 
            new LinkedList<JsonStreamContext>();
        JsonStreamContext current = streamContext;
        while (current != null)
        {
            collection.addFirst(current);
            current = current.getParent();
        }
        return collection;
    }
    
}