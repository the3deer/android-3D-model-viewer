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
package de.javagl.jgltf.model.io.v1;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import de.javagl.jgltf.impl.v1.BufferView;
import de.javagl.jgltf.impl.v1.GlTF;
import de.javagl.jgltf.impl.v1.Image;
import de.javagl.jgltf.impl.v1.Shader;
import de.javagl.jgltf.model.GltfException;
import de.javagl.jgltf.model.io.JacksonUtils;

/**
 * Utility methods related to {@link GlTF}s
 */
class GltfUtilsV1
{
    /**
     * Creates a deep copy of the given {@link GlTF}.<br>
     * <br>
     * Note: Some details about the copy are not specified. E.g. whether
     * values that are mapped to <code>null</code> are still contained
     * in the copy. The goal of this method is to create a copy that is,
     * as far as reasonably possible, "structurally equivalent" to the
     * given input.
     * 
     * @param gltf The input 
     * @return The copy
     * @throws GltfException If the copy can not be created
     */
    static GlTF copy(GlTF gltf)
    {
        ObjectMapper objectMapper = 
            JacksonUtils.createObjectMapper();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            objectMapper.writeValue(baos, gltf);
            return objectMapper.readValue(baos.toByteArray(), GlTF.class);
        } 
        catch (IOException e)
        {
            throw new GltfException("Could not copy glTF", e);
        }
    }

    /**
     * Creates a shallow copy of the given {@link BufferView}
     * 
     * @param bufferView The {@link BufferView}
     * @return The copy
     */
    static BufferView copy(BufferView bufferView)
    {
        BufferView copy = new BufferView();
        copy.setExtensions(bufferView.getExtensions());
        copy.setExtras(bufferView.getExtras());
        copy.setName(bufferView.getName());
        copy.setBuffer(bufferView.getBuffer());
        copy.setByteOffset(bufferView.getByteOffset());
        copy.setByteLength(bufferView.getByteLength());
        copy.setTarget(bufferView.getTarget());
        return copy;
    }
    
    
    /**
     * Creates a shallow copy of the given {@link Image}
     * 
     * @param image The {@link Image}
     * @return The copy
     */
    static Image copy(Image image)
    {
        Image copy = new Image();
        copy.setExtensions(image.getExtensions());
        copy.setExtras(image.getExtras());
        copy.setName(image.getName());
        copy.setUri(image.getUri());
        return copy;
    }
    
    /**
     * Creates a shallow copy of the given {@link Shader}
     * 
     * @param shader The {@link Shader}
     * @return The copy
     */
    static Shader copy(Shader shader)
    {
        Shader copy = new Shader();
        copy.setExtensions(shader.getExtensions());
        copy.setExtras(shader.getExtras());
        copy.setName(shader.getName());
        copy.setType(shader.getType());
        copy.setUri(shader.getUri());
        return copy;
    }
    
    /**
     * Combine the keys of the given map with the elements of the given
     * collection, in iteration order.
     * 
     * @param map The map
     * @param collection The collection
     * @return The resulting map
     * @throws IllegalArgumentException If the inputs have different sizes
     */
    static <K, V> Map<K, V> createMap(
        Map<? extends K, ?> map, 
        Collection<? extends V> collection)
    {
        if (map == null)
        {
            return Collections.emptyMap();
        }
        if (map.size() != collection.size())
        {
            throw new IllegalArgumentException(
                "The inputs must have the same size, but the sizes are "
                + map.size() + " and " + collection.size());
        }
        Iterator<? extends K> iterator0 = map.keySet().iterator();
        Iterator<? extends V> iterator1 = collection.iterator();
        Map<K, V> result = new LinkedHashMap<K, V>();
        while (iterator0.hasNext())
        {
            K k = iterator0.next();
            V v = iterator1.next();
            result.put(k, v);
        }
        return result;
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private GltfUtilsV1()
    {
        // Private constructor to prevent instantiation
    }
}
