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

import de.javagl.jgltf.model.CameraPerspectiveModel;

/**
 * Default implementation of a {@link CameraPerspectiveModel}
 */
public class DefaultCameraPerspectiveModel implements CameraPerspectiveModel
{
    /**
     * The aspect ratio
     */
    private Float aspectRatio;

    /**
     * The FOV
     */
    private Float yfov;

    /**
     * The clipping plane distance
     */
    private Float zfar;

    /**
     * The near clipping plane distance
     */
    private Float znear;

    /**
     * Set the aspect ratio
     * 
     * @param aspectRatio The aspect ratio
     */
    public void setAspectRatio(Float aspectRatio)
    {
        this.aspectRatio = aspectRatio;
    }

    /**
     * Set the FOV
     * 
     * @param yfov The FOV
     */
    public void setYfov(Float yfov)
    {
        this.yfov = yfov;
    }

    /**
     * Set the far clipping plane distance
     * 
     * @param zfar The distance
     */
    public void setZfar(Float zfar)
    {
        this.zfar = zfar;
    }

    /**
     * Set the near clipping plane distance
     * 
     * @param znear The distance
     */
    public void setZnear(Float znear)
    {
        this.znear = znear;
    }
    
    @Override
    public Float getAspectRatio()
    {
        return aspectRatio;
    }
    
    @Override
    public Float getYfov()
    {
        return yfov;
    }

    @Override
    public Float getZfar()
    {
        return zfar;
    }

    @Override
    public Float getZnear()
    {
        return znear;
    }

}

