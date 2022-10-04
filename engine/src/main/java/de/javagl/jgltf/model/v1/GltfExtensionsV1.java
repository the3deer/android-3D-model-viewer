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
package de.javagl.jgltf.model.v1;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.javagl.jgltf.impl.v1.GlTF;
import de.javagl.jgltf.impl.v1.GlTFProperty;
import de.javagl.jgltf.model.io.JacksonUtils;

/**
 * Utility methods related to glTF extensions for glTF 1.0
 */
public class GltfExtensionsV1
{
    /**
     * If the {@link GlTF#getExtensionsUsed() used extensions} of the given 
     * {@link GlTF} is <code>null</code> or does not contain the given
     * extension name, then it will be set to a new list that contains all
     * previous extension names, and the given one.
     * 
     * @param gltf The {@link GlTF}
     * @param extensionName The name of the extension to add
     */
    public static void addExtensionUsed(GlTF gltf, String extensionName)
    {
        List<String> oldExtensionsUsed = gltf.getExtensionsUsed();
        if (oldExtensionsUsed == null || 
            !oldExtensionsUsed.contains(extensionName))
        {
            gltf.addExtensionsUsed(extensionName);
        }
    }
    
    /**
     * If the {@link GlTF#getExtensionsUsed() used extensions} of the given 
     * {@link GlTF} contains the given extension name, then it will be set 
     * to a new list that contains all previous extension names, except
     * for the given one.
     * 
     * @param gltf The {@link GlTF}
     * @param extensionName The name of the extension to remove
     */
    public static void removeExtensionUsed(GlTF gltf, String extensionName)
    {
        List<String> oldExtensionsUsed = gltf.getExtensionsUsed();
        if (oldExtensionsUsed != null && 
            oldExtensionsUsed.contains(extensionName))
        {
            gltf.removeExtensionsUsed(extensionName);
        }
    }
    
    /**
     * Return the key-value mapping that is stored as the 
     * {@link GlTFProperty#getExtensions() extension} with the given name 
     * in the given {@link GlTFProperty}, or <code>null</code> if no such 
     * entry can be found.
     * 
     * @param gltfProperty The {@link GlTFProperty}
     * @param extensionName The extension name
     * @return The extension property mapping, or <code>null</code>
     */
    private static Map<String, Object> getExtensionMap(
        GlTFProperty gltfProperty, String extensionName)
    {
        Map<String, Object> extensions = gltfProperty.getExtensions();
        if (extensions == null)
        {
            return null;
        }
        Object value = extensions.get(extensionName);
        if (value == null)
        {
            return null;
        }
        if (value instanceof Map<?, ?>)
        {
            Map<?, ?> map = (Map<?, ?>)value;
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>)map;
            return result; 
        }
        return null;
    }
    
    
    /**
     * Returns whether a non-<code>null</code> key-value mapping is stored 
     * as the {@link GlTFProperty#getExtensions() extension} with the
     * given name in the given glTF property.

     * @param gltfProperty The {@link GlTFProperty}
     * @param extensionName The extension name
     * @return Whether the specified extension mapping exists
     */
    static boolean hasExtension(
        GlTFProperty gltfProperty, String extensionName)
    {
        return getExtensionMap(gltfProperty, extensionName) != null;
    }
    
    /**
     * Returns the value of the specified property in the key-value mapping 
     * that is stored as the {@link GlTFProperty#getExtensions() extension} 
     * with the given name in the given glTF property. If the specified
     * extension does not exist, or does not have the specified property,
     * then <code>null</code> is returned.
     * 
     * @param gltfProperty The {@link GlTFProperty}
     * @param extensionName The extension name
     * @param propertyName The property name
     * @return The value, as a string.
     */
    static String getExtensionPropertyValueAsString(
        GlTFProperty gltfProperty, String extensionName, String propertyName)
    {
        Map<String, Object> extensionMap = 
            getExtensionMap(gltfProperty, extensionName);
        if (extensionMap == null)
        {
            return null;
        }
        Object value = extensionMap.get(propertyName);
        if (value == null)
        {
            return null;
        }
        return String.valueOf(value);
    }
    
    /**
     * Stores the given property value under the given property key in the
     * key-value mapping that is stored under the given extension name in
     * the {@link GlTFProperty#getExtensions() extensions} of the given 
     * {@link GlTFProperty}. If the mapping for the given extension name
     * did not exist, it will be created. If it already existed but was
     * no key-value mapping, then its old value will be overwritten.
     * 
     * @param gltfProperty The {@link GlTFProperty}
     * @param extensionName The extension name
     * @param propertyName The property name
     * @param propertyValue The value
     */
    static void setExtensionPropertyValue(
        GlTFProperty gltfProperty, String extensionName, 
        String propertyName, Object propertyValue)
    {
        Map<String, Object> extensionMap = 
            getExtensionMap(gltfProperty, extensionName);
        if (extensionMap == null)
        {
            extensionMap = new LinkedHashMap<String, Object>();
            gltfProperty.addExtensions(extensionName, extensionMap);
        }
        extensionMap.put(propertyName, propertyValue);
    }
    
    
    /**
     * Fetch the value of the specified extension object from the given
     * {@link GlTFProperty}, converted into the given target type. 
     * Returns <code>null</code> if the given {@link GlTFProperty} does
     * not have the specified extension.
     * 
     * @param gltfProperty The {@link GlTFProperty}
     * @param extensionName The extension name
     * @param type The type to convert the object to
     * @return The object
     * @throws IllegalArgumentException If the specified extension object
     * cannot be converted to the desired target type
     */
    static <T> T fetchExtensionObject(
        GlTFProperty gltfProperty, String extensionName, Class<T> type)
    {
        Map<String, Object> extensions = gltfProperty.getExtensions();
        if (extensions == null)
        {
            return null;
        }
        Object extensionObject = extensions.get(extensionName);
        if (extensionObject == null)
        {
            return null;
        }
        ObjectMapper objectMapper = 
            JacksonUtils.createObjectMapper();
        T extension = objectMapper.convertValue(extensionObject, type);
        return extension;
    }
    
    
    /**
     * Private constructor to prevent instantiation
     */
    private GltfExtensionsV1()
    {
        // Private constructor to prevent instantiation
    }

}
