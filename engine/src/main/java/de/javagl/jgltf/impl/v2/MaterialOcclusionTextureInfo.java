/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016-2021 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v2;



/**
 * Auto-generated for material.occlusionTextureInfo.schema.json 
 * 
 */
public class MaterialOcclusionTextureInfo
    extends TextureInfo
{

    /**
     * A scalar multiplier controlling the amount of occlusion applied. 
     * (optional)<br> 
     * Default: 1.0<br> 
     * Minimum: 0.0 (inclusive)<br> 
     * Maximum: 1.0 (inclusive) 
     * 
     */
    private Float strength;

    /**
     * A scalar multiplier controlling the amount of occlusion applied. 
     * (optional)<br> 
     * Default: 1.0<br> 
     * Minimum: 0.0 (inclusive)<br> 
     * Maximum: 1.0 (inclusive) 
     * 
     * @param strength The strength to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setStrength(Float strength) {
        if (strength == null) {
            this.strength = strength;
            return ;
        }
        if (strength > 1.0D) {
            throw new IllegalArgumentException("strength > 1.0");
        }
        if (strength< 0.0D) {
            throw new IllegalArgumentException("strength < 0.0");
        }
        this.strength = strength;
    }

    /**
     * A scalar multiplier controlling the amount of occlusion applied. 
     * (optional)<br> 
     * Default: 1.0<br> 
     * Minimum: 0.0 (inclusive)<br> 
     * Maximum: 1.0 (inclusive) 
     * 
     * @return The strength
     * 
     */
    public Float getStrength() {
        return this.strength;
    }

    /**
     * Returns the default value of the strength<br> 
     * @see #getStrength 
     * 
     * @return The default strength
     * 
     */
    public Float defaultStrength() {
        return  1.0F;
    }

}
