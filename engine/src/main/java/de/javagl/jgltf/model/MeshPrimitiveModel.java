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
package de.javagl.jgltf.model;

import java.util.List;
import java.util.Map;

/**
 * Interface for a primitive that is part of a mesh
 */
public interface MeshPrimitiveModel extends ModelElement
{
    /**
     * Returns an unmodifiable view on the mapping from attribute names to
     * the {@link AccessorModel} instances for the attribute data
     * 
     * @return The attributes mapping
     */
    Map<String, AccessorModel> getAttributes();
    
    /**
     * Return an {@link AccessorModel} for the indices, or <code>null</code>
     * if this primitive describes non-indexed geometry
     * 
     * @return The indices 
     */
    AccessorModel getIndices();
    
    /**
     * Returns the rendering mode, as a (GL) constant, standing for
     * <code>GL_POINTS</code>, <code>GL_TRIANGLES</code> etc.
     *  
     * @return The rendering mode
     */
    int getMode();
    
    /**
     * Returns the {@link MaterialModel} that should be used for rendering
     * this mesh primitive
     * 
     * @return The {@link MaterialModel}
     */
    MaterialModel getMaterialModel();
    
    /**
     * Returns an unmodifiable view on the list of morph targets. Each element
     * of this list will be an unmodifiable map. Each map maps the attribute
     * name to the {@link AccessorModel} that provides the morph target data.
     * 
     * @return The morph targets
     */
    List<Map<String, AccessorModel>> getTargets();
}
