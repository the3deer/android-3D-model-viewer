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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.MaterialModel;
import de.javagl.jgltf.model.MeshPrimitiveModel;

/**
 * Implementation of a {@link MeshPrimitiveModel}
 */
public final class DefaultMeshPrimitiveModel extends AbstractModelElement 
    implements MeshPrimitiveModel
{
    /**
     * The attributes of this mesh primitive model
     */
    private final Map<String, AccessorModel> attributes;
    
    /**
     * The {@link AccessorModel} for the indices data
     */
    private AccessorModel indices;
    
    /**
     * The {@link MaterialModel} that should be used for rendering
     */
    private MaterialModel materialModel;
    
    /**
     * The rendering mode
     */
    private final int mode;
    
    /**
     * The morph targets
     */
    private final List<Map<String, AccessorModel>> targets;
    
    /**
     * Creates a new instance
     * 
     * @param mode The rendering mode
     */
    public DefaultMeshPrimitiveModel(int mode)
    {
        this.mode = mode;
        this.attributes = new LinkedHashMap<String, AccessorModel>();
        this.targets = new ArrayList<Map<String,AccessorModel>>();
    }
    
    
    /**
     * Put the given {@link AccessorModel} into the attributes, under the
     * given name
     * 
     * @param name The name
     * @param accessorModel The {@link AccessorModel}
     * @return The old value that was stored under the given name
     */
    public AccessorModel putAttribute(String name, AccessorModel accessorModel)
    {
        Objects.requireNonNull(
            accessorModel, "The accessorModel may not be null");
        return attributes.put(name, accessorModel);
    }
    
    /**
     * Set the {@link AccessorModel} for the indices 
     * 
     * @param indices The indices
     */
    public void setIndices(AccessorModel indices)
    {
        this.indices = indices;
    }
    
    /**
     * Set the {@link MaterialModel}
     * 
     * @param materialModel The {@link MaterialModel}
     */
    public void setMaterialModel(MaterialModel materialModel)
    {
        this.materialModel = Objects.requireNonNull(
            materialModel, "The materialModel may not be null");
    }
    
    /**
     * Add the given morph target. A reference to the given map will be stored.
     * 
     * @param target The target
     */
    public void addTarget(Map<String, AccessorModel> target)
    {
        Objects.requireNonNull(target, "The target may not be null");
        this.targets.add(target);
    }
    

    @Override
    public Map<String, AccessorModel> getAttributes()
    {
        return Collections.unmodifiableMap(attributes);
    }

    @Override
    public AccessorModel getIndices()
    {
        return indices;
    }

    @Override
    public int getMode()
    {
        return mode;
    }

    @Override
    public MaterialModel getMaterialModel()
    {
        return materialModel;
    }
    
    @Override
    public List<Map<String, AccessorModel>> getTargets()
    {
        return Collections.unmodifiableList(targets);
    }


}
