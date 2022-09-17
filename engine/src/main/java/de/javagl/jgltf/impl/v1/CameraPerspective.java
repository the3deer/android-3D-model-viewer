/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v1;



/**
 * A perspective camera containing properties to create a perspective 
 * projection matrix. 
 * 
 * Auto-generated for camera.perspective.schema.json 
 * 
 */
public class CameraPerspective
    extends GlTFProperty
{

    /**
     * The floating-point aspect ratio of the field of view. (optional)<br> 
     * Minimum: 0.0 (inclusive) 
     * 
     */
    private Float aspectRatio;
    /**
     * The floating-point vertical field of view in radians. (required)<br> 
     * Minimum: 0.0 (inclusive) 
     * 
     */
    private Float yfov;
    /**
     * The floating-point distance to the far clipping plane. (required)<br> 
     * Minimum: 0.0 (exclusive) 
     * 
     */
    private Float zfar;
    /**
     * The floating-point distance to the near clipping plane. (required)<br> 
     * Minimum: 0.0 (exclusive) 
     * 
     */
    private Float znear;

    /**
     * The floating-point aspect ratio of the field of view. (optional)<br> 
     * Minimum: 0.0 (inclusive) 
     * 
     * @param aspectRatio The aspectRatio to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setAspectRatio(Float aspectRatio) {
        if (aspectRatio == null) {
            this.aspectRatio = aspectRatio;
            return ;
        }
        if (aspectRatio< 0.0D) {
            throw new IllegalArgumentException("aspectRatio < 0.0");
        }
        this.aspectRatio = aspectRatio;
    }

    /**
     * The floating-point aspect ratio of the field of view. (optional)<br> 
     * Minimum: 0.0 (inclusive) 
     * 
     * @return The aspectRatio
     * 
     */
    public Float getAspectRatio() {
        return this.aspectRatio;
    }

    /**
     * The floating-point vertical field of view in radians. (required)<br> 
     * Minimum: 0.0 (inclusive) 
     * 
     * @param yfov The yfov to set
     * @throws NullPointerException If the given value is <code>null</code>
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setYfov(Float yfov) {
        if (yfov == null) {
            throw new NullPointerException((("Invalid value for yfov: "+ yfov)+", may not be null"));
        }
        if (yfov< 0.0D) {
            throw new IllegalArgumentException("yfov < 0.0");
        }
        this.yfov = yfov;
    }

    /**
     * The floating-point vertical field of view in radians. (required)<br> 
     * Minimum: 0.0 (inclusive) 
     * 
     * @return The yfov
     * 
     */
    public Float getYfov() {
        return this.yfov;
    }

    /**
     * The floating-point distance to the far clipping plane. (required)<br> 
     * Minimum: 0.0 (exclusive) 
     * 
     * @param zfar The zfar to set
     * @throws NullPointerException If the given value is <code>null</code>
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setZfar(Float zfar) {
        if (zfar == null) {
            throw new NullPointerException((("Invalid value for zfar: "+ zfar)+", may not be null"));
        }
        if (zfar<= 0.0D) {
            throw new IllegalArgumentException("zfar <= 0.0");
        }
        this.zfar = zfar;
    }

    /**
     * The floating-point distance to the far clipping plane. (required)<br> 
     * Minimum: 0.0 (exclusive) 
     * 
     * @return The zfar
     * 
     */
    public Float getZfar() {
        return this.zfar;
    }

    /**
     * The floating-point distance to the near clipping plane. (required)<br> 
     * Minimum: 0.0 (exclusive) 
     * 
     * @param znear The znear to set
     * @throws NullPointerException If the given value is <code>null</code>
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setZnear(Float znear) {
        if (znear == null) {
            throw new NullPointerException((("Invalid value for znear: "+ znear)+", may not be null"));
        }
        if (znear<= 0.0D) {
            throw new IllegalArgumentException("znear <= 0.0");
        }
        this.znear = znear;
    }

    /**
     * The floating-point distance to the near clipping plane. (required)<br> 
     * Minimum: 0.0 (exclusive) 
     * 
     * @return The znear
     * 
     */
    public Float getZnear() {
        return this.znear;
    }

}
