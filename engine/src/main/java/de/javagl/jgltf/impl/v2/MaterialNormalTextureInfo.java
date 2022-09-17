/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016-2021 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v2;



/**
 * Auto-generated for material.normalTextureInfo.schema.json 
 * 
 */
public class MaterialNormalTextureInfo
    extends TextureInfo
{

    /**
     * The scalar parameter applied to each normal vector of the normal 
     * texture. (optional)<br> 
     * Default: 1.0 
     * 
     */
    private Float scale;

    /**
     * The scalar parameter applied to each normal vector of the normal 
     * texture. (optional)<br> 
     * Default: 1.0 
     * 
     * @param scale The scale to set
     * 
     */
    public void setScale(Float scale) {
        if (scale == null) {
            this.scale = scale;
            return ;
        }
        this.scale = scale;
    }

    /**
     * The scalar parameter applied to each normal vector of the normal 
     * texture. (optional)<br> 
     * Default: 1.0 
     * 
     * @return The scale
     * 
     */
    public Float getScale() {
        return this.scale;
    }

    /**
     * Returns the default value of the scale<br> 
     * @see #getScale 
     * 
     * @return The default scale
     * 
     */
    public Float defaultScale() {
        return  1.0F;
    }

}
