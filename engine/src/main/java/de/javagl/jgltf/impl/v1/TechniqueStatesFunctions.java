/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v1;



/**
 * Arguments for fixed-function rendering state functions other than 
 * `enable()`/`disable()`. 
 * 
 * Auto-generated for technique.states.functions.schema.json 
 * 
 */
public class TechniqueStatesFunctions
    extends GlTFProperty
{

    /**
     * Floating-point values passed to `blendColor()`. [red, green, blue, 
     * alpha] (optional)<br> 
     * Default: [0.0,0.0,0.0,0.0]<br> 
     * Number of items: 4<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     */
    private float[] blendColor;
    /**
     * Integer values passed to `blendEquationSeparate()`. (optional)<br> 
     * Default: [32774,32774]<br> 
     * Number of items: 2<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Valid values: [32774, 32778, 32779] 
     * 
     */
    private int[] blendEquationSeparate;
    /**
     * Integer values passed to `blendFuncSeparate()`. (optional)<br> 
     * Default: [1,0,1,0]<br> 
     * Number of items: 4<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Valid values: [0, 1, 768, 769, 774, 775, 770, 771, 772, 
     *  773, 32769, 32770, 32771, 32772, 776] 
     * 
     */
    private int[] blendFuncSeparate;
    /**
     * Boolean values passed to `colorMask()`. [red, green, blue, alpha]. 
     * (optional)<br> 
     * Default: [true,true,true,true]<br> 
     * Number of items: 4<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     */
    private boolean[] colorMask;
    /**
     * Integer value passed to `cullFace()`. (optional)<br> 
     * Default: [1029]<br> 
     * Number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Valid values: [1028, 1029, 1032] 
     * 
     */
    private int[] cullFace;
    /**
     * Integer values passed to `depthFunc()`. (optional)<br> 
     * Default: [513]<br> 
     * Number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Valid values: [512, 513, 515, 514, 516, 517, 518, 519] 
     * 
     */
    private int[] depthFunc;
    /**
     * Boolean value passed to `depthMask()`. (optional)<br> 
     * Default: [true]<br> 
     * Number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     */
    private boolean[] depthMask;
    /**
     * Floating-point values passed to `depthRange()`. [zNear, zFar] 
     * (optional)<br> 
     * Default: [0.0,1.0]<br> 
     * Number of items: 2<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     */
    private float[] depthRange;
    /**
     * Integer value passed to `frontFace()`. (optional)<br> 
     * Default: [2305]<br> 
     * Number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Valid values: [2304, 2305] 
     * 
     */
    private int[] frontFace;
    /**
     * Floating-point value passed to `lineWidth()`. (optional)<br> 
     * Default: [1.0]<br> 
     * Number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Minimum: 0.0 (exclusive) 
     * 
     */
    private float[] lineWidth;
    /**
     * Floating-point value passed to `polygonOffset()`. [factor, units] 
     * (optional)<br> 
     * Default: [0.0,0.0]<br> 
     * Number of items: 2<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     */
    private float[] polygonOffset;
    /**
     * Floating-point value passed to `scissor()`. [x, y, width, height]. 
     * (optional)<br> 
     * Default: [0.0,0.0,0.0,0.0]<br> 
     * Number of items: 4<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     */
    private float[] scissor;

    /**
     * Floating-point values passed to `blendColor()`. [red, green, blue, 
     * alpha] (optional)<br> 
     * Default: [0.0,0.0,0.0,0.0]<br> 
     * Number of items: 4<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @param blendColor The blendColor to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setBlendColor(float[] blendColor) {
        if (blendColor == null) {
            this.blendColor = blendColor;
            return ;
        }
        if (blendColor.length< 4) {
            throw new IllegalArgumentException("Number of blendColor elements is < 4");
        }
        if (blendColor.length > 4) {
            throw new IllegalArgumentException("Number of blendColor elements is > 4");
        }
        this.blendColor = blendColor;
    }

    /**
     * Floating-point values passed to `blendColor()`. [red, green, blue, 
     * alpha] (optional)<br> 
     * Default: [0.0,0.0,0.0,0.0]<br> 
     * Number of items: 4<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @return The blendColor
     * 
     */
    public float[] getBlendColor() {
        return this.blendColor;
    }

    /**
     * Returns the default value of the blendColor<br> 
     * @see #getBlendColor 
     * 
     * @return The default blendColor
     * 
     */
    public float[] defaultBlendColor() {
        return new float[] { 0.0F, 0.0F, 0.0F, 0.0F };
    }

    /**
     * Integer values passed to `blendEquationSeparate()`. (optional)<br> 
     * Default: [32774,32774]<br> 
     * Number of items: 2<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Valid values: [32774, 32778, 32779] 
     * 
     * @param blendEquationSeparate The blendEquationSeparate to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setBlendEquationSeparate(int[] blendEquationSeparate) {
        if (blendEquationSeparate == null) {
            this.blendEquationSeparate = blendEquationSeparate;
            return ;
        }
        if (blendEquationSeparate.length< 2) {
            throw new IllegalArgumentException("Number of blendEquationSeparate elements is < 2");
        }
        if (blendEquationSeparate.length > 2) {
            throw new IllegalArgumentException("Number of blendEquationSeparate elements is > 2");
        }
        for (int blendEquationSeparateElement: blendEquationSeparate) {
            if (((blendEquationSeparateElement!= 32774)&&(blendEquationSeparateElement!= 32778))&&(blendEquationSeparateElement!= 32779)) {
                throw new IllegalArgumentException((("Invalid value for blendEquationSeparateElement: "+ blendEquationSeparateElement)+", valid: [32774, 32778, 32779]"));
            }
        }
        this.blendEquationSeparate = blendEquationSeparate;
    }

    /**
     * Integer values passed to `blendEquationSeparate()`. (optional)<br> 
     * Default: [32774,32774]<br> 
     * Number of items: 2<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Valid values: [32774, 32778, 32779] 
     * 
     * @return The blendEquationSeparate
     * 
     */
    public int[] getBlendEquationSeparate() {
        return this.blendEquationSeparate;
    }

    /**
     * Returns the default value of the blendEquationSeparate<br> 
     * @see #getBlendEquationSeparate 
     * 
     * @return The default blendEquationSeparate
     * 
     */
    public int[] defaultBlendEquationSeparate() {
        return new int[] { 32774, 32774 };
    }

    /**
     * Integer values passed to `blendFuncSeparate()`. (optional)<br> 
     * Default: [1,0,1,0]<br> 
     * Number of items: 4<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Valid values: [0, 1, 768, 769, 774, 775, 770, 771, 772, 
     *  773, 32769, 32770, 32771, 32772, 776] 
     * 
     * @param blendFuncSeparate The blendFuncSeparate to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setBlendFuncSeparate(int[] blendFuncSeparate) {
        if (blendFuncSeparate == null) {
            this.blendFuncSeparate = blendFuncSeparate;
            return ;
        }
        if (blendFuncSeparate.length< 4) {
            throw new IllegalArgumentException("Number of blendFuncSeparate elements is < 4");
        }
        if (blendFuncSeparate.length > 4) {
            throw new IllegalArgumentException("Number of blendFuncSeparate elements is > 4");
        }
        for (int blendFuncSeparateElement: blendFuncSeparate) {
            if (((((((((((((((blendFuncSeparateElement!= 0)&&(blendFuncSeparateElement!= 1))&&(blendFuncSeparateElement!= 768))&&(blendFuncSeparateElement!= 769))&&(blendFuncSeparateElement!= 774))&&(blendFuncSeparateElement!= 775))&&(blendFuncSeparateElement!= 770))&&(blendFuncSeparateElement!= 771))&&(blendFuncSeparateElement!= 772))&&(blendFuncSeparateElement!= 773))&&(blendFuncSeparateElement!= 32769))&&(blendFuncSeparateElement!= 32770))&&(blendFuncSeparateElement!= 32771))&&(blendFuncSeparateElement!= 32772))&&(blendFuncSeparateElement!= 776)) {
                throw new IllegalArgumentException((("Invalid value for blendFuncSeparateElement: "+ blendFuncSeparateElement)+", valid: [0, 1, 768, 769, 774, 775, 770, 771, 772, 773, 32769, 32770, 32771, 32772, 776]"));
            }
        }
        this.blendFuncSeparate = blendFuncSeparate;
    }

    /**
     * Integer values passed to `blendFuncSeparate()`. (optional)<br> 
     * Default: [1,0,1,0]<br> 
     * Number of items: 4<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Valid values: [0, 1, 768, 769, 774, 775, 770, 771, 772, 
     *  773, 32769, 32770, 32771, 32772, 776] 
     * 
     * @return The blendFuncSeparate
     * 
     */
    public int[] getBlendFuncSeparate() {
        return this.blendFuncSeparate;
    }

    /**
     * Returns the default value of the blendFuncSeparate<br> 
     * @see #getBlendFuncSeparate 
     * 
     * @return The default blendFuncSeparate
     * 
     */
    public int[] defaultBlendFuncSeparate() {
        return new int[] { 1, 0, 1, 0 };
    }

    /**
     * Boolean values passed to `colorMask()`. [red, green, blue, alpha]. 
     * (optional)<br> 
     * Default: [true,true,true,true]<br> 
     * Number of items: 4<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @param colorMask The colorMask to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setColorMask(boolean[] colorMask) {
        if (colorMask == null) {
            this.colorMask = colorMask;
            return ;
        }
        if (colorMask.length< 4) {
            throw new IllegalArgumentException("Number of colorMask elements is < 4");
        }
        if (colorMask.length > 4) {
            throw new IllegalArgumentException("Number of colorMask elements is > 4");
        }
        this.colorMask = colorMask;
    }

    /**
     * Boolean values passed to `colorMask()`. [red, green, blue, alpha]. 
     * (optional)<br> 
     * Default: [true,true,true,true]<br> 
     * Number of items: 4<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @return The colorMask
     * 
     */
    public boolean[] getColorMask() {
        return this.colorMask;
    }

    /**
     * Returns the default value of the colorMask<br> 
     * @see #getColorMask 
     * 
     * @return The default colorMask
     * 
     */
    public boolean[] defaultColorMask() {
        return new boolean[] {true, true, true, true };
    }

    /**
     * Integer value passed to `cullFace()`. (optional)<br> 
     * Default: [1029]<br> 
     * Number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Valid values: [1028, 1029, 1032] 
     * 
     * @param cullFace The cullFace to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setCullFace(int[] cullFace) {
        if (cullFace == null) {
            this.cullFace = cullFace;
            return ;
        }
        if (cullFace.length< 1) {
            throw new IllegalArgumentException("Number of cullFace elements is < 1");
        }
        if (cullFace.length > 1) {
            throw new IllegalArgumentException("Number of cullFace elements is > 1");
        }
        for (int cullFaceElement: cullFace) {
            if (((cullFaceElement!= 1028)&&(cullFaceElement!= 1029))&&(cullFaceElement!= 1032)) {
                throw new IllegalArgumentException((("Invalid value for cullFaceElement: "+ cullFaceElement)+", valid: [1028, 1029, 1032]"));
            }
        }
        this.cullFace = cullFace;
    }

    /**
     * Integer value passed to `cullFace()`. (optional)<br> 
     * Default: [1029]<br> 
     * Number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Valid values: [1028, 1029, 1032] 
     * 
     * @return The cullFace
     * 
     */
    public int[] getCullFace() {
        return this.cullFace;
    }

    /**
     * Returns the default value of the cullFace<br> 
     * @see #getCullFace 
     * 
     * @return The default cullFace
     * 
     */
    public int[] defaultCullFace() {
        return new int[] { 1029 };
    }

    /**
     * Integer values passed to `depthFunc()`. (optional)<br> 
     * Default: [513]<br> 
     * Number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Valid values: [512, 513, 515, 514, 516, 517, 518, 519] 
     * 
     * @param depthFunc The depthFunc to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setDepthFunc(int[] depthFunc) {
        if (depthFunc == null) {
            this.depthFunc = depthFunc;
            return ;
        }
        if (depthFunc.length< 1) {
            throw new IllegalArgumentException("Number of depthFunc elements is < 1");
        }
        if (depthFunc.length > 1) {
            throw new IllegalArgumentException("Number of depthFunc elements is > 1");
        }
        for (int depthFuncElement: depthFunc) {
            if ((((((((depthFuncElement!= 512)&&(depthFuncElement!= 513))&&(depthFuncElement!= 515))&&(depthFuncElement!= 514))&&(depthFuncElement!= 516))&&(depthFuncElement!= 517))&&(depthFuncElement!= 518))&&(depthFuncElement!= 519)) {
                throw new IllegalArgumentException((("Invalid value for depthFuncElement: "+ depthFuncElement)+", valid: [512, 513, 515, 514, 516, 517, 518, 519]"));
            }
        }
        this.depthFunc = depthFunc;
    }

    /**
     * Integer values passed to `depthFunc()`. (optional)<br> 
     * Default: [513]<br> 
     * Number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Valid values: [512, 513, 515, 514, 516, 517, 518, 519] 
     * 
     * @return The depthFunc
     * 
     */
    public int[] getDepthFunc() {
        return this.depthFunc;
    }

    /**
     * Returns the default value of the depthFunc<br> 
     * @see #getDepthFunc 
     * 
     * @return The default depthFunc
     * 
     */
    public int[] defaultDepthFunc() {
        return new int[] { 513 };
    }

    /**
     * Boolean value passed to `depthMask()`. (optional)<br> 
     * Default: [true]<br> 
     * Number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @param depthMask The depthMask to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setDepthMask(boolean[] depthMask) {
        if (depthMask == null) {
            this.depthMask = depthMask;
            return ;
        }
        if (depthMask.length< 1) {
            throw new IllegalArgumentException("Number of depthMask elements is < 1");
        }
        if (depthMask.length > 1) {
            throw new IllegalArgumentException("Number of depthMask elements is > 1");
        }
        this.depthMask = depthMask;
    }

    /**
     * Boolean value passed to `depthMask()`. (optional)<br> 
     * Default: [true]<br> 
     * Number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @return The depthMask
     * 
     */
    public boolean[] getDepthMask() {
        return this.depthMask;
    }

    /**
     * Returns the default value of the depthMask<br> 
     * @see #getDepthMask 
     * 
     * @return The default depthMask
     * 
     */
    public boolean[] defaultDepthMask() {
        return new boolean[] {true };
    }

    /**
     * Floating-point values passed to `depthRange()`. [zNear, zFar] 
     * (optional)<br> 
     * Default: [0.0,1.0]<br> 
     * Number of items: 2<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @param depthRange The depthRange to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setDepthRange(float[] depthRange) {
        if (depthRange == null) {
            this.depthRange = depthRange;
            return ;
        }
        if (depthRange.length< 2) {
            throw new IllegalArgumentException("Number of depthRange elements is < 2");
        }
        if (depthRange.length > 2) {
            throw new IllegalArgumentException("Number of depthRange elements is > 2");
        }
        this.depthRange = depthRange;
    }

    /**
     * Floating-point values passed to `depthRange()`. [zNear, zFar] 
     * (optional)<br> 
     * Default: [0.0,1.0]<br> 
     * Number of items: 2<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @return The depthRange
     * 
     */
    public float[] getDepthRange() {
        return this.depthRange;
    }

    /**
     * Returns the default value of the depthRange<br> 
     * @see #getDepthRange 
     * 
     * @return The default depthRange
     * 
     */
    public float[] defaultDepthRange() {
        return new float[] { 0.0F, 1.0F };
    }

    /**
     * Integer value passed to `frontFace()`. (optional)<br> 
     * Default: [2305]<br> 
     * Number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Valid values: [2304, 2305] 
     * 
     * @param frontFace The frontFace to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setFrontFace(int[] frontFace) {
        if (frontFace == null) {
            this.frontFace = frontFace;
            return ;
        }
        if (frontFace.length< 1) {
            throw new IllegalArgumentException("Number of frontFace elements is < 1");
        }
        if (frontFace.length > 1) {
            throw new IllegalArgumentException("Number of frontFace elements is > 1");
        }
        for (int frontFaceElement: frontFace) {
            if ((frontFaceElement!= 2304)&&(frontFaceElement!= 2305)) {
                throw new IllegalArgumentException((("Invalid value for frontFaceElement: "+ frontFaceElement)+", valid: [2304, 2305]"));
            }
        }
        this.frontFace = frontFace;
    }

    /**
     * Integer value passed to `frontFace()`. (optional)<br> 
     * Default: [2305]<br> 
     * Number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Valid values: [2304, 2305] 
     * 
     * @return The frontFace
     * 
     */
    public int[] getFrontFace() {
        return this.frontFace;
    }

    /**
     * Returns the default value of the frontFace<br> 
     * @see #getFrontFace 
     * 
     * @return The default frontFace
     * 
     */
    public int[] defaultFrontFace() {
        return new int[] { 2305 };
    }

    /**
     * Floating-point value passed to `lineWidth()`. (optional)<br> 
     * Default: [1.0]<br> 
     * Number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Minimum: 0.0 (exclusive) 
     * 
     * @param lineWidth The lineWidth to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setLineWidth(float[] lineWidth) {
        if (lineWidth == null) {
            this.lineWidth = lineWidth;
            return ;
        }
        if (lineWidth.length< 1) {
            throw new IllegalArgumentException("Number of lineWidth elements is < 1");
        }
        if (lineWidth.length > 1) {
            throw new IllegalArgumentException("Number of lineWidth elements is > 1");
        }
        for (float lineWidthElement: lineWidth) {
            if (lineWidthElement<= 0.0D) {
                throw new IllegalArgumentException("lineWidthElement <= 0.0");
            }
        }
        this.lineWidth = lineWidth;
    }

    /**
     * Floating-point value passed to `lineWidth()`. (optional)<br> 
     * Default: [1.0]<br> 
     * Number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Minimum: 0.0 (exclusive) 
     * 
     * @return The lineWidth
     * 
     */
    public float[] getLineWidth() {
        return this.lineWidth;
    }

    /**
     * Returns the default value of the lineWidth<br> 
     * @see #getLineWidth 
     * 
     * @return The default lineWidth
     * 
     */
    public float[] defaultLineWidth() {
        return new float[] { 1.0F };
    }

    /**
     * Floating-point value passed to `polygonOffset()`. [factor, units] 
     * (optional)<br> 
     * Default: [0.0,0.0]<br> 
     * Number of items: 2<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @param polygonOffset The polygonOffset to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setPolygonOffset(float[] polygonOffset) {
        if (polygonOffset == null) {
            this.polygonOffset = polygonOffset;
            return ;
        }
        if (polygonOffset.length< 2) {
            throw new IllegalArgumentException("Number of polygonOffset elements is < 2");
        }
        if (polygonOffset.length > 2) {
            throw new IllegalArgumentException("Number of polygonOffset elements is > 2");
        }
        this.polygonOffset = polygonOffset;
    }

    /**
     * Floating-point value passed to `polygonOffset()`. [factor, units] 
     * (optional)<br> 
     * Default: [0.0,0.0]<br> 
     * Number of items: 2<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @return The polygonOffset
     * 
     */
    public float[] getPolygonOffset() {
        return this.polygonOffset;
    }

    /**
     * Returns the default value of the polygonOffset<br> 
     * @see #getPolygonOffset 
     * 
     * @return The default polygonOffset
     * 
     */
    public float[] defaultPolygonOffset() {
        return new float[] { 0.0F, 0.0F };
    }

    /**
     * Floating-point value passed to `scissor()`. [x, y, width, height]. 
     * (optional)<br> 
     * Default: [0.0,0.0,0.0,0.0]<br> 
     * Number of items: 4<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @param scissor The scissor to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setScissor(float[] scissor) {
        if (scissor == null) {
            this.scissor = scissor;
            return ;
        }
        if (scissor.length< 4) {
            throw new IllegalArgumentException("Number of scissor elements is < 4");
        }
        if (scissor.length > 4) {
            throw new IllegalArgumentException("Number of scissor elements is > 4");
        }
        this.scissor = scissor;
    }

    /**
     * Floating-point value passed to `scissor()`. [x, y, width, height]. 
     * (optional)<br> 
     * Default: [0.0,0.0,0.0,0.0]<br> 
     * Number of items: 4<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @return The scissor
     * 
     */
    public float[] getScissor() {
        return this.scissor;
    }

    /**
     * Returns the default value of the scissor<br> 
     * @see #getScissor 
     * 
     * @return The default scissor
     * 
     */
    public float[] defaultScissor() {
        return new float[] { 0.0F, 0.0F, 0.0F, 0.0F };
    }

}
