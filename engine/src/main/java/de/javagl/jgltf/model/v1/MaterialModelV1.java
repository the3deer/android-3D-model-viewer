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
package de.javagl.jgltf.model.v1;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import de.javagl.jgltf.impl.v1.Material;
import de.javagl.jgltf.model.MaterialModel;
import de.javagl.jgltf.model.TextureModel;
import de.javagl.jgltf.model.gl.TechniqueModel;
import de.javagl.jgltf.model.impl.AbstractNamedModelElement;

/**
 * Implementation of a {@link MaterialModel} for glTF 1.0.<br>
 * <br> 
 * Note: This class is actually no longer specific for glTF 1.0. It might
 * be renamed to "TechniqueBasedMaterialModel" and moved to a different
 * package in the future.
 */
public final class MaterialModelV1 extends AbstractNamedModelElement
    implements MaterialModel
{
    /**
     * The {@link TechniqueModel}
     */
    private TechniqueModel techniqueModel;
    
    /**
     * The material parameter values
     */
    private Map<String, Object> values;

    /**
     * Creates a new instance
     */
    public MaterialModelV1()
    {
        this.values = Collections.emptyMap();
    }
    
    /**
     * Set the material parameter values to be an unmodifiable shallow
     * copy of the given map (or the empty map if the given map is
     * <code>null</code>)
     * 
     * @param values The material parameter values
     */
    public void setValues(Map<String, Object> values)
    {
        if (values == null)
        {
            this.values = Collections.emptyMap();
        }
        else
        {
            this.values = Collections.unmodifiableMap(
                new LinkedHashMap<String, Object>(values));
        }
    }
    
    /**
     * Set the {@link TechniqueModel} 
     * 
     * @param techniqueModel The {@link TechniqueModel}
     */
    public void setTechniqueModel(TechniqueModel techniqueModel)
    {
        this.techniqueModel = techniqueModel;
    }

    /**
     * Returns the {@link TechniqueModel}
     * 
     * @return The {@link TechniqueModel}
     */
    public TechniqueModel getTechniqueModel()
    {
        return techniqueModel;
    }

    /**
     * Returns the parameter values of this material. Note that if any 
     * parameter value of the original {@link Material} is the texture ID 
     * for a parameter of type GL_SAMPLER2D, then the respective value 
     * will be the appropriate {@link TextureModel} instance.
     * 
     * @return The values
     */
    public Map<String, Object> getValues()
    {
        return values;
    }
    
}