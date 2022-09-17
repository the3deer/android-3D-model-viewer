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
package de.javagl.jgltf.model.gl;

/**
 * Enumeration of the {@link TechniqueParametersModel#getSemantic() technique 
 * parameters semantics}
 */
public enum Semantic
{
    /**
     * The LOCAL semantic
     */
    LOCAL,
    
    /**
     * The MODEL semantic
     */
    MODEL,
    
    /**
     * The VIEW semantic
     */
    VIEW,
    
    /**
     * The PROJECTION semantic
     */
    PROJECTION,
    
    /**
     * The MODELVIEW semantic
     */
    MODELVIEW,
    
    /**
     * The MODELVIEWPROJECTION semantic
     */
    MODELVIEWPROJECTION,
    
    /**
     * The MODELINVERSE semantic
     */
    MODELINVERSE,
    
    /**
     * The VIEWINVERSE semantic
     */
    VIEWINVERSE,
    
    /**
     * The MODELVIEWINVERSE semantic
     */
    MODELVIEWINVERSE,
    
    /**
     * The PROJECTIONINVERSE semantic
     */
    PROJECTIONINVERSE,
    
    /**
     * The MODELVIEWPROJECTIONINVERSE semantic
     */
    MODELVIEWPROJECTIONINVERSE,
    
    /**
     * The MODELINVERSETRANSPOSE semantic
     */
    MODELINVERSETRANSPOSE,
    
    /**
     * The MODELVIEWINVERSETRANSPOSE semantic
     */
    MODELVIEWINVERSETRANSPOSE,
    
    /**
     * The VIEWPORT semantic
     */
    VIEWPORT,
    
    /**
     * The JOINTMATRIX semantic
     */
    JOINTMATRIX;

    /**
     * Returns whether the given string is a valid semantic name, and may be
     * passed to <code>Semantic.valueOf</code> without causing an exception.
     * 
     * @param s The string
     * @return Whether the given string is a valid semantic
     */
    public static boolean contains(String s)
    {
        for (Semantic semantic : values())
        {
            if (semantic.name().equals(s))
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Returns the semantic for the given string. If the string is 
     * <code>null</code> or does not describe a valid semantic,
     * then <code>null</code> is returned
     * 
     * @param string The string
     * @return The semantic
     */
    public static Semantic forString(String string)
    {
        if (string == null)
        {
            return null;
        }
        if (!contains(string))
        {
            return null;
        }
        return Semantic.valueOf(string);
    }
    
}
