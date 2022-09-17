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

import de.javagl.jgltf.model.CameraOrthographicModel;

/**
 * Default implementation of a {@link CameraOrthographicModel}
 */
public class DefaultCameraOrthographicModel implements CameraOrthographicModel
{
    /**
     * The magnification in x-direction
     */
    private Float xmag;

    /**
     * The magnification in y-direction
     */
    private Float ymag;

    /**
     * The clipping plane distance
     */
    private Float zfar;

    /**
     * The near clipping plane distance
     */
    private Float znear;

    /**
     * Set the magnification in x-direction
     * 
     * @param xmag The magnification
     */
    public void setXmag(Float xmag)
    {
        this.xmag = xmag;
    }

    /**
     * Set the magnification in y-direction
     * 
     * @param ymag The magnification
     */
    public void setYmag(Float ymag)
    {
        this.ymag = ymag;
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
    public Float getXmag()
    {
        return xmag;
    }

    @Override
    public Float getYmag()
    {
        return ymag;
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

