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
package de.javagl.jgltf.model.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import de.javagl.jgltf.model.ModelElement;

/**
 * Abstract base implementation of the {@link ModelElement} interface.
 */
public class AbstractModelElement implements ModelElement
{
    /**
     * The extensions
     */
    private Map<String, Object> extensions;
    
    /**
     * The extras
     */
    private Object extras;
    
    /**
     * Set the extensions to be a reference to the given map.
     * 
     * @param extensions The extensions
     */
    public void setExtensions(Map<String, Object> extensions)
    {
        this.extensions = extensions;
    }
    
    /**
     * Add the given extension to this object.
     * 
     * This will add the given key-value pair to the extensions map,
     * creating it if it was <code>null</code>.
     *  
     * @param name The name of the extension
     * @param extension The extension object
     */
    public void addExtension(String name, Object extension)
    {
        Objects.requireNonNull(name, "The name may not be null");
        if (this.extensions == null)
        {
            this.extensions = new LinkedHashMap<String, Object>();
        }
        this.extensions.put(name, extension);
    }

    /**
     * Remove the specified extension from this object.
     * 
     * If the extension map is empty after this call, then it will be
     * set to <code>null</code>.
     * 
     * @param name The name of the extension
     */
    public void removeExtension(String name)
    {
        if (this.extensions != null)
        {
            this.extensions.remove(name);
            if (this.extensions.isEmpty())
            {
                this.extensions = null;
            }
        }
    }
    

    /**
     * Set the extras
     * 
     * @param extras The extras
     */
    public void setExtras(Object extras)
    {
        this.extras = extras;
    }
    
    @Override
    public Map<String, Object> getExtensions()
    {
        return extensions;
    }
    
    @Override
    public Object getExtras()
    {
        return extras;
    }
}
