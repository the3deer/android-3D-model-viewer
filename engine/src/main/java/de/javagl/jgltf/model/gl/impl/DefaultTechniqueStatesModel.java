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
package de.javagl.jgltf.model.gl.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.javagl.jgltf.model.gl.TechniqueStatesFunctionsModel;
import de.javagl.jgltf.model.gl.TechniqueStatesModel;

/**
 * Implementation of a {@link TechniqueStatesModel}
 */
public class DefaultTechniqueStatesModel implements TechniqueStatesModel
{
    /**
     * The enabled states
     */
    private final List<Integer> enable;
    
    /**
     * The {@link TechniqueStatesFunctionsModel}
     */
    private final TechniqueStatesFunctionsModel techniqueStatesFunctionsModel;
    
    /**
     * Default constructor
     * 
     * @param enable The enabled states
     * @param techniqueStatesFunctionsModel 
     * The {@link TechniqueStatesFunctionsModel}
     */
    public DefaultTechniqueStatesModel(List<Integer> enable, 
        TechniqueStatesFunctionsModel techniqueStatesFunctionsModel)
    {
        this.enable = Collections.unmodifiableList(
            new ArrayList<Integer>(enable));
        this.techniqueStatesFunctionsModel = techniqueStatesFunctionsModel;
    }

    @Override
    public List<Integer> getEnable()
    {
        return enable;
    }

    @Override
    public TechniqueStatesFunctionsModel getTechniqueStatesFunctionsModel()
    {
        return techniqueStatesFunctionsModel;
    }
}
