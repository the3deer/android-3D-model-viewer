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
package de.javagl.jgltf.model.io;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

/**
 * Interface for a low-level representation of a glTF asset, consisting of 
 * the (version-specific) JSON part, optional binary data, and references 
 * to external data.
 */
public interface GltfAsset
{
    /**
     * Returns the version-specific glTF object. This may be a 
     * {@link de.javagl.jgltf.impl.v1.GlTF version 1.0 glTF} or
     * or a {@link de.javagl.jgltf.impl.v2.GlTF version 2.0 glTF}
     * 
     * @return The glTF
     */
    Object getGltf();
    
    /**
     * Returns the binary data of this asset, or <code>null</code> if this
     * asset does not have associated binary data.<br>
     * <br>
     * The returned buffer will be a slice of the data that is stored 
     * internally. So changes of the contents of the buffer will affect
     * this asset, but changes of the limit or position of the buffer
     * will not affect this asset.
     *  
     * @return the optional binary data
     */
    ByteBuffer getBinaryData();
    
    /**
     * Return a list of all {@link GltfReference} objects that refer to
     * external resources for this asset
     * 
     * @return The {@link GltfReference} objects
     */
    List<GltfReference> getReferences();

    /**
     * Returns the byte buffer containing the data of the external resource
     * with the given (relative!) URI, or <code>null</code> if there is
     * no such data.<br>
     * <br>
     * The returned buffer will be a slice of the data that is stored 
     * internally. So changes of the contents of the buffer will affect
     * this asset, but changes of the limit or position of the buffer
     * will not affect this asset.
     * 
     * @param uriString The URI string
     * @return The byte buffer
     */
    ByteBuffer getReferenceData(String uriString);
    
    /**
     * Returns an unmodifiable view on the mapping from relative URI strings
     * to the byte buffers containing the data of the external resources.<br>
     * <br> 
     * <b>Callers may not modify the values of this map. That is, the 
     * positions or limits of the returned buffers may not be modified!</b>
     * 
     * @return The reference data mapping
     */
    Map<String, ByteBuffer> getReferenceDatas();
    
}
