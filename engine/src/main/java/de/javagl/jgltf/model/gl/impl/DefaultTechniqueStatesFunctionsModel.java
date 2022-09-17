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

import de.javagl.jgltf.model.gl.TechniqueStatesFunctionsModel;

/**
 * Default implementation of a {@link TechniqueStatesFunctionsModel}.<br>
 * <br>
 * <b>Note: The method comments here are placeholders. For details, refer 
 * to the glTF 1.0 specification and an OpenGL documentation!</b><br>
 * <br>
 */
public class DefaultTechniqueStatesFunctionsModel
    implements TechniqueStatesFunctionsModel
{
    /**
     * The BlendColor
     */
    private float[] blendColor;

    /**
     * The BlendEquationSeparate
     */
    private int[] blendEquationSeparate;

    /**
     * The BlendFuncSeparate
     */
    private int[] blendFuncSeparate;

    /**
     * The ColorMask
     */
    private boolean[] colorMask;

    /**
     * The CullFace
     */
    private int[] cullFace;

    /**
     * The DepthFunc
     */
    private int[] depthFunc;

    /**
     * The DepthMask
     */
    private boolean[] depthMask;

    /**
     * The DepthRange
     */
    private float[] depthRange;

    /**
     * The FrontFace
     */
    private int[] frontFace;

    /**
     * The LineWidth
     */
    private float[] lineWidth;

    /**
     * The PolygonOffset
     */
    private float[] polygonOffset;

    /**
     * Default constructor
     */
    public DefaultTechniqueStatesFunctionsModel()
    {
        // Default constructor
    }

    @Override
    public float[] getBlendColor()
    {
        return blendColor;
    }

    /**
     * Set the BlendColor
     *
     * @param blendColor The BlendColor
     */
    public void setBlendColor(float[] blendColor)
    {
        this.blendColor = blendColor;
    }

    @Override
    public int[] getBlendEquationSeparate()
    {
        return blendEquationSeparate;
    }

    /**
     * Set the BlendEquationSeparate
     *
     * @param blendEquationSeparate The BlendEquationSeparate
     */
    public void setBlendEquationSeparate(int[] blendEquationSeparate)
    {
        this.blendEquationSeparate = blendEquationSeparate;
    }

    @Override
    public int[] getBlendFuncSeparate()
    {
        return blendFuncSeparate;
    }

    /**
     * Set the BlendFuncSeparate
     *
     * @param blendFuncSeparate The BlendFuncSeparate
     */
    public void setBlendFuncSeparate(int[] blendFuncSeparate)
    {
        this.blendFuncSeparate = blendFuncSeparate;
    }

    @Override
    public boolean[] getColorMask()
    {
        return colorMask;
    }

    /**
     * Set the ColorMask
     *
     * @param colorMask The ColorMask
     */
    public void setColorMask(boolean[] colorMask)
    {
        this.colorMask = colorMask;
    }

    @Override
    public int[] getCullFace()
    {
        return cullFace;
    }

    /**
     * Set the CullFace
     *
     * @param cullFace The CullFace
     */
    public void setCullFace(int[] cullFace)
    {
        this.cullFace = cullFace;
    }

    @Override
    public int[] getDepthFunc()
    {
        return depthFunc;
    }

    /**
     * Set the DepthFunc
     *
     * @param depthFunc The DepthFunc
     */
    public void setDepthFunc(int[] depthFunc)
    {
        this.depthFunc = depthFunc;
    }

    @Override
    public boolean[] getDepthMask()
    {
        return depthMask;
    }

    /**
     * Set the DepthMask
     *
     * @param depthMask The DepthMask
     */
    public void setDepthMask(boolean[] depthMask)
    {
        this.depthMask = depthMask;
    }

    @Override
    public float[] getDepthRange()
    {
        return depthRange;
    }

    /**
     * Set the DepthRange
     *
     * @param depthRange The DepthRange
     */
    public void setDepthRange(float[] depthRange)
    {
        this.depthRange = depthRange;
    }

    @Override
    public int[] getFrontFace()
    {
        return frontFace;
    }

    /**
     * Set the FrontFace
     *
     * @param frontFace The FrontFace
     */
    public void setFrontFace(int[] frontFace)
    {
        this.frontFace = frontFace;
    }

    @Override
    public float[] getLineWidth()
    {
        return lineWidth;
    }

    /**
     * Set the LineWidth
     *
     * @param lineWidth The LineWidth
     */
    public void setLineWidth(float[] lineWidth)
    {
        this.lineWidth = lineWidth;
    }

    @Override
    public float[] getPolygonOffset()
    {
        return polygonOffset;
    }

    /**
     * Set the PolygonOffset
     *
     * @param polygonOffset The PolygonOffset
     */
    public void setPolygonOffset(float[] polygonOffset)
    {
        this.polygonOffset = polygonOffset;
    }

}
