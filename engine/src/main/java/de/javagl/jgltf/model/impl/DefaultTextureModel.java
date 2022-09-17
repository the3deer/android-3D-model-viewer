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

import de.javagl.jgltf.model.ImageModel;
import de.javagl.jgltf.model.TextureModel;

/**
 * Implementation of a {@link TextureModel}
 */
public class DefaultTextureModel extends AbstractNamedModelElement
    implements TextureModel
{
    /**
     * The magnification filter constant
     */
    private Integer magFilter;

    /**
     * The minification filter constant
     */
    private Integer minFilter;
    
    /**
     * The wrapping constant for the S-direction
     */
    private Integer wrapS;
    
    /**
     * The wrapping constant for the T-direction
     */
    private Integer wrapT;
    
    /**
     * The {@link ImageModel}
     */
    private ImageModel imageModel;
    
    /**
     * Creates a new instance
     */
    public DefaultTextureModel()
    {
        // Default constructor
    }
    
    /**
     * Set the {@link ImageModel} that backs this texture
     * 
     * @param imageModel The {@link ImageModel}
     */
    public void setImageModel(ImageModel imageModel)
    {
        this.imageModel = imageModel;
    }
    
    @Override
    public Integer getMagFilter()
    {
        return magFilter;
    }
    
    /**
     * Set the magnification filter
     * 
     * @param magFilter The filter
     */
    public void setMagFilter(Integer magFilter)
    {
        this.magFilter = magFilter;
    }

    @Override
    public Integer getMinFilter()
    {
        return minFilter;
    }
    
    /**
     * Set the minification filter
     * 
     * @param minFilter The filter
     */
    public void setMinFilter(Integer minFilter)
    {
        this.minFilter = minFilter;
    }

    @Override
    public Integer getWrapS()
    {
        return wrapS;
    }
    
    /**
     * Set the wrapping behavior in S-direction
     * 
     * @param wrapS The wrapping mode
     */
    public void setWrapS(Integer wrapS)
    {
        this.wrapS = wrapS;
    }

    @Override
    public Integer getWrapT()
    {
        return wrapT;
    }

    /**
     * Set the wrapping behavior in T-direction
     * 
     * @param wrapT The wrapping mode
     */
    public void setWrapT(Integer wrapT)
    {
        this.wrapT = wrapT;
    }
    
    @Override
    public ImageModel getImageModel()
    {
        return imageModel;
    }
}
