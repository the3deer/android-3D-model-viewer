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
package de.javagl.jgltf.model;

/**
 * Utility methods related to accessor properties.<br>
 * <br>
 * Unless otherwise noted, none of the arguments to these methods may 
 * be <code>null</code>.
 */
public class Accessors
{
    /**
     * Returns the number of components that one element has for the given 
     * accessor type. Valid parameters are
     * <pre><code>
     * "SCALAR" :  1
     * "VEC2"   :  2 
     * "VEC3"   :  3 
     * "VEC4"   :  4 
     * "MAT2"   :  4 
     * "MAT3"   :  9 
     * "MAT4"   : 16
     * </code></pre>
     * 
     * @param accessorType The accessor type. 
     * @return The number of components
     * @throws IllegalArgumentException If the given type is none of the
     * valid parameters
     */
    public static int getNumComponentsForAccessorType(String accessorType)
    {
        switch (accessorType)
        {
            case "SCALAR": return 1;
            case "VEC2": return 2;
            case "VEC3": return 3;
            case "VEC4": return 4;
            case "MAT2": return 4;
            case "MAT3": return 9;
            case "MAT4": return 16;
            default:
                break;
        }
        throw new IllegalArgumentException(
            "Invalid accessor type: "+accessorType);
    }

    /**
     * Returns the number of bytes that one component with the given 
     * accessor component type consists of.
     * Valid parameters are
     * <pre><code>
     * GL_BYTE           : 1
     * GL_UNSIGNED_BYTE  : 1 
     * GL_SHORT          : 2 
     * GL_UNSIGNED_SHORT : 2 
     * GL_INT            : 4 
     * GL_UNSIGNED_INT   : 4 
     * GL_FLOAT          : 4
     * </code></pre>
     *  
     * @param componentType The component type
     * @return The number of bytes
     * @throws IllegalArgumentException If the given type is none of the
     * valid parameters
     */
    public static int getNumBytesForAccessorComponentType(int componentType)
    {
        switch (componentType)
        {
            case GltfConstants.GL_BYTE: return 1;
            case GltfConstants.GL_UNSIGNED_BYTE: return 1;
            case GltfConstants.GL_SHORT: return 2;
            case GltfConstants.GL_UNSIGNED_SHORT: return 2;
            case GltfConstants.GL_INT: return 4;
            case GltfConstants.GL_UNSIGNED_INT: return 4;
            case GltfConstants.GL_FLOAT: return 4;
            default:
                break;
        }
        throw new IllegalArgumentException(
            "Invalid accessor component type: "+componentType);
    }

    /**
     * Returns the data type for the given accessor component type.
     * Valid parameters and their return values are
     * <pre><code>
     * GL_BYTE           : byte.class
     * GL_UNSIGNED_BYTE  : byte.class
     * GL_SHORT          : short.class 
     * GL_UNSIGNED_SHORT : short.class
     * GL_INT            : int.class 
     * GL_UNSIGNED_INT   : int.class
     * GL_FLOAT          : float.class
     * </code></pre>
     *  
     * @param componentType The component type
     * @return The data type
     * @throws IllegalArgumentException If the given type is none of the
     * valid parameters
     */
    public static Class<?> getDataTypeForAccessorComponentType(
        int componentType)
    {
        switch (componentType)
        {
            case GltfConstants.GL_BYTE: return byte.class;
            case GltfConstants.GL_UNSIGNED_BYTE: return byte.class;
            case GltfConstants.GL_SHORT: return short.class;
            case GltfConstants.GL_UNSIGNED_SHORT: return short.class;
            case GltfConstants.GL_INT: return int.class;
            case GltfConstants.GL_UNSIGNED_INT: return int.class;
            case GltfConstants.GL_FLOAT: return float.class;
            default:
                break;
        }
        throw new IllegalArgumentException(
            "Invalid accessor component type: "+componentType);
    }

    /**
     * Private constructor to prevent instantiation
     */
    private Accessors()
    {
        // Private constructor to prevent instantiation
    }
    
}
