/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016-2021 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v2;



/**
 * A set of parameter values that are used to define the 
 * metallic-roughness material model from Physically-Based Rendering 
 * (PBR) methodology. 
 * 
 * Auto-generated for material.pbrMetallicRoughness.schema.json 
 * 
 */
public class MaterialPbrMetallicRoughness
    extends GlTFProperty
{

    /**
     * The factors for the base color of the material. (optional)<br> 
     * Default: [1.0,1.0,1.0,1.0]<br> 
     * Number of items: 4<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Minimum: 0.0 (inclusive)<br> 
     * &nbsp;&nbsp;Maximum: 1.0 (inclusive) 
     * 
     */
    private float[] baseColorFactor;
    /**
     * The base color texture. (optional) 
     * 
     */
    private TextureInfo baseColorTexture;
    /**
     * The factor for the metalness of the material. (optional)<br> 
     * Default: 1.0<br> 
     * Minimum: 0.0 (inclusive)<br> 
     * Maximum: 1.0 (inclusive) 
     * 
     */
    private Float metallicFactor;
    /**
     * The factor for the roughness of the material. (optional)<br> 
     * Default: 1.0<br> 
     * Minimum: 0.0 (inclusive)<br> 
     * Maximum: 1.0 (inclusive) 
     * 
     */
    private Float roughnessFactor;
    /**
     * The metallic-roughness texture. (optional) 
     * 
     */
    private TextureInfo metallicRoughnessTexture;

    /**
     * The factors for the base color of the material. (optional)<br> 
     * Default: [1.0,1.0,1.0,1.0]<br> 
     * Number of items: 4<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Minimum: 0.0 (inclusive)<br> 
     * &nbsp;&nbsp;Maximum: 1.0 (inclusive) 
     * 
     * @param baseColorFactor The baseColorFactor to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setBaseColorFactor(float[] baseColorFactor) {
        if (baseColorFactor == null) {
            this.baseColorFactor = baseColorFactor;
            return ;
        }
        if (baseColorFactor.length< 4) {
            throw new IllegalArgumentException("Number of baseColorFactor elements is < 4");
        }
        if (baseColorFactor.length > 4) {
            throw new IllegalArgumentException("Number of baseColorFactor elements is > 4");
        }
        for (float baseColorFactorElement: baseColorFactor) {
            if (baseColorFactorElement > 1.0D) {
                throw new IllegalArgumentException("baseColorFactorElement > 1.0");
            }
            if (baseColorFactorElement< 0.0D) {
                throw new IllegalArgumentException("baseColorFactorElement < 0.0");
            }
        }
        this.baseColorFactor = baseColorFactor;
    }

    /**
     * The factors for the base color of the material. (optional)<br> 
     * Default: [1.0,1.0,1.0,1.0]<br> 
     * Number of items: 4<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Minimum: 0.0 (inclusive)<br> 
     * &nbsp;&nbsp;Maximum: 1.0 (inclusive) 
     * 
     * @return The baseColorFactor
     * 
     */
    public float[] getBaseColorFactor() {
        return this.baseColorFactor;
    }

    /**
     * Returns the default value of the baseColorFactor<br> 
     * @see #getBaseColorFactor 
     * 
     * @return The default baseColorFactor
     * 
     */
    public float[] defaultBaseColorFactor() {
        return new float[] { 1.0F, 1.0F, 1.0F, 1.0F };
    }

    /**
     * The base color texture. (optional) 
     * 
     * @param baseColorTexture The baseColorTexture to set
     * 
     */
    public void setBaseColorTexture(TextureInfo baseColorTexture) {
        if (baseColorTexture == null) {
            this.baseColorTexture = baseColorTexture;
            return ;
        }
        this.baseColorTexture = baseColorTexture;
    }

    /**
     * The base color texture. (optional) 
     * 
     * @return The baseColorTexture
     * 
     */
    public TextureInfo getBaseColorTexture() {
        return this.baseColorTexture;
    }

    /**
     * The factor for the metalness of the material. (optional)<br> 
     * Default: 1.0<br> 
     * Minimum: 0.0 (inclusive)<br> 
     * Maximum: 1.0 (inclusive) 
     * 
     * @param metallicFactor The metallicFactor to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setMetallicFactor(Float metallicFactor) {
        if (metallicFactor == null) {
            this.metallicFactor = metallicFactor;
            return ;
        }
        if (metallicFactor > 1.0D) {
            throw new IllegalArgumentException("metallicFactor > 1.0");
        }
        if (metallicFactor< 0.0D) {
            throw new IllegalArgumentException("metallicFactor < 0.0");
        }
        this.metallicFactor = metallicFactor;
    }

    /**
     * The factor for the metalness of the material. (optional)<br> 
     * Default: 1.0<br> 
     * Minimum: 0.0 (inclusive)<br> 
     * Maximum: 1.0 (inclusive) 
     * 
     * @return The metallicFactor
     * 
     */
    public Float getMetallicFactor() {
        return this.metallicFactor;
    }

    /**
     * Returns the default value of the metallicFactor<br> 
     * @see #getMetallicFactor 
     * 
     * @return The default metallicFactor
     * 
     */
    public Float defaultMetallicFactor() {
        return  1.0F;
    }

    /**
     * The factor for the roughness of the material. (optional)<br> 
     * Default: 1.0<br> 
     * Minimum: 0.0 (inclusive)<br> 
     * Maximum: 1.0 (inclusive) 
     * 
     * @param roughnessFactor The roughnessFactor to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setRoughnessFactor(Float roughnessFactor) {
        if (roughnessFactor == null) {
            this.roughnessFactor = roughnessFactor;
            return ;
        }
        if (roughnessFactor > 1.0D) {
            throw new IllegalArgumentException("roughnessFactor > 1.0");
        }
        if (roughnessFactor< 0.0D) {
            throw new IllegalArgumentException("roughnessFactor < 0.0");
        }
        this.roughnessFactor = roughnessFactor;
    }

    /**
     * The factor for the roughness of the material. (optional)<br> 
     * Default: 1.0<br> 
     * Minimum: 0.0 (inclusive)<br> 
     * Maximum: 1.0 (inclusive) 
     * 
     * @return The roughnessFactor
     * 
     */
    public Float getRoughnessFactor() {
        return this.roughnessFactor;
    }

    /**
     * Returns the default value of the roughnessFactor<br> 
     * @see #getRoughnessFactor 
     * 
     * @return The default roughnessFactor
     * 
     */
    public Float defaultRoughnessFactor() {
        return  1.0F;
    }

    /**
     * The metallic-roughness texture. (optional) 
     * 
     * @param metallicRoughnessTexture The metallicRoughnessTexture to set
     * 
     */
    public void setMetallicRoughnessTexture(TextureInfo metallicRoughnessTexture) {
        if (metallicRoughnessTexture == null) {
            this.metallicRoughnessTexture = metallicRoughnessTexture;
            return ;
        }
        this.metallicRoughnessTexture = metallicRoughnessTexture;
    }

    /**
     * The metallic-roughness texture. (optional) 
     * 
     * @return The metallicRoughnessTexture
     * 
     */
    public TextureInfo getMetallicRoughnessTexture() {
        return this.metallicRoughnessTexture;
    }

}
