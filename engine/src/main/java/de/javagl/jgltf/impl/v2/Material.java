/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016-2021 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v2;



/**
 * The material appearance of a primitive. 
 * 
 * Auto-generated for material.schema.json 
 * 
 */
public class Material
    extends GlTFChildOfRootProperty
{

    /**
     * A set of parameter values that are used to define the 
     * metallic-roughness material model from Physically Based Rendering 
     * (PBR) methodology. When undefined, all the default values of 
     * `pbrMetallicRoughness` **MUST** apply. (optional) 
     * 
     */
    private MaterialPbrMetallicRoughness pbrMetallicRoughness;
    /**
     * The tangent space normal texture. (optional) 
     * 
     */
    private MaterialNormalTextureInfo normalTexture;
    /**
     * The occlusion texture. (optional) 
     * 
     */
    private MaterialOcclusionTextureInfo occlusionTexture;
    /**
     * The emissive texture. (optional) 
     * 
     */
    private TextureInfo emissiveTexture;
    /**
     * The factors for the emissive color of the material. (optional)<br> 
     * Default: [0.0,0.0,0.0]<br> 
     * Number of items: 3<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Minimum: 0.0 (inclusive)<br> 
     * &nbsp;&nbsp;Maximum: 1.0 (inclusive) 
     * 
     */
    private float[] emissiveFactor;
    /**
     * The alpha rendering mode of the material. (optional)<br> 
     * Default: "OPAQUE"<br> 
     * Valid values: [OPAQUE, MASK, BLEND] 
     * 
     */
    private String alphaMode;
    /**
     * The alpha cutoff value of the material. (optional)<br> 
     * Default: 0.5<br> 
     * Minimum: 0.0 (inclusive) 
     * 
     */
    private Float alphaCutoff;
    /**
     * Specifies whether the material is double sided. (optional)<br> 
     * Default: false 
     * 
     */
    private Boolean doubleSided;

    /**
     * A set of parameter values that are used to define the 
     * metallic-roughness material model from Physically Based Rendering 
     * (PBR) methodology. When undefined, all the default values of 
     * `pbrMetallicRoughness` **MUST** apply. (optional) 
     * 
     * @param pbrMetallicRoughness The pbrMetallicRoughness to set
     * 
     */
    public void setPbrMetallicRoughness(MaterialPbrMetallicRoughness pbrMetallicRoughness) {
        if (pbrMetallicRoughness == null) {
            this.pbrMetallicRoughness = pbrMetallicRoughness;
            return ;
        }
        this.pbrMetallicRoughness = pbrMetallicRoughness;
    }

    /**
     * A set of parameter values that are used to define the 
     * metallic-roughness material model from Physically Based Rendering 
     * (PBR) methodology. When undefined, all the default values of 
     * `pbrMetallicRoughness` **MUST** apply. (optional) 
     * 
     * @return The pbrMetallicRoughness
     * 
     */
    public MaterialPbrMetallicRoughness getPbrMetallicRoughness() {
        return this.pbrMetallicRoughness;
    }

    /**
     * The tangent space normal texture. (optional) 
     * 
     * @param normalTexture The normalTexture to set
     * 
     */
    public void setNormalTexture(MaterialNormalTextureInfo normalTexture) {
        if (normalTexture == null) {
            this.normalTexture = normalTexture;
            return ;
        }
        this.normalTexture = normalTexture;
    }

    /**
     * The tangent space normal texture. (optional) 
     * 
     * @return The normalTexture
     * 
     */
    public MaterialNormalTextureInfo getNormalTexture() {
        return this.normalTexture;
    }

    /**
     * The occlusion texture. (optional) 
     * 
     * @param occlusionTexture The occlusionTexture to set
     * 
     */
    public void setOcclusionTexture(MaterialOcclusionTextureInfo occlusionTexture) {
        if (occlusionTexture == null) {
            this.occlusionTexture = occlusionTexture;
            return ;
        }
        this.occlusionTexture = occlusionTexture;
    }

    /**
     * The occlusion texture. (optional) 
     * 
     * @return The occlusionTexture
     * 
     */
    public MaterialOcclusionTextureInfo getOcclusionTexture() {
        return this.occlusionTexture;
    }

    /**
     * The emissive texture. (optional) 
     * 
     * @param emissiveTexture The emissiveTexture to set
     * 
     */
    public void setEmissiveTexture(TextureInfo emissiveTexture) {
        if (emissiveTexture == null) {
            this.emissiveTexture = emissiveTexture;
            return ;
        }
        this.emissiveTexture = emissiveTexture;
    }

    /**
     * The emissive texture. (optional) 
     * 
     * @return The emissiveTexture
     * 
     */
    public TextureInfo getEmissiveTexture() {
        return this.emissiveTexture;
    }

    /**
     * The factors for the emissive color of the material. (optional)<br> 
     * Default: [0.0,0.0,0.0]<br> 
     * Number of items: 3<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Minimum: 0.0 (inclusive)<br> 
     * &nbsp;&nbsp;Maximum: 1.0 (inclusive) 
     * 
     * @param emissiveFactor The emissiveFactor to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setEmissiveFactor(float[] emissiveFactor) {
        if (emissiveFactor == null) {
            this.emissiveFactor = emissiveFactor;
            return ;
        }
        if (emissiveFactor.length< 3) {
            throw new IllegalArgumentException("Number of emissiveFactor elements is < 3");
        }
        if (emissiveFactor.length > 3) {
            throw new IllegalArgumentException("Number of emissiveFactor elements is > 3");
        }
        for (float emissiveFactorElement: emissiveFactor) {
            if (emissiveFactorElement > 1.0D) {
                throw new IllegalArgumentException("emissiveFactorElement > 1.0");
            }
            if (emissiveFactorElement< 0.0D) {
                throw new IllegalArgumentException("emissiveFactorElement < 0.0");
            }
        }
        this.emissiveFactor = emissiveFactor;
    }

    /**
     * The factors for the emissive color of the material. (optional)<br> 
     * Default: [0.0,0.0,0.0]<br> 
     * Number of items: 3<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Minimum: 0.0 (inclusive)<br> 
     * &nbsp;&nbsp;Maximum: 1.0 (inclusive) 
     * 
     * @return The emissiveFactor
     * 
     */
    public float[] getEmissiveFactor() {
        return this.emissiveFactor;
    }

    /**
     * Returns the default value of the emissiveFactor<br> 
     * @see #getEmissiveFactor 
     * 
     * @return The default emissiveFactor
     * 
     */
    public float[] defaultEmissiveFactor() {
        return new float[] { 0.0F, 0.0F, 0.0F };
    }

    /**
     * The alpha rendering mode of the material. (optional)<br> 
     * Default: "OPAQUE"<br> 
     * Valid values: [OPAQUE, MASK, BLEND] 
     * 
     * @param alphaMode The alphaMode to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setAlphaMode(String alphaMode) {
        if (alphaMode == null) {
            this.alphaMode = alphaMode;
            return ;
        }
        if (((!"OPAQUE".equals(alphaMode))&&(!"MASK".equals(alphaMode)))&&(!"BLEND".equals(alphaMode))) {
            throw new IllegalArgumentException((("Invalid value for alphaMode: "+ alphaMode)+", valid: [OPAQUE, MASK, BLEND]"));
        }
        this.alphaMode = alphaMode;
    }

    /**
     * The alpha rendering mode of the material. (optional)<br> 
     * Default: "OPAQUE"<br> 
     * Valid values: [OPAQUE, MASK, BLEND] 
     * 
     * @return The alphaMode
     * 
     */
    public String getAlphaMode() {
        return this.alphaMode;
    }

    /**
     * Returns the default value of the alphaMode<br> 
     * @see #getAlphaMode 
     * 
     * @return The default alphaMode
     * 
     */
    public String defaultAlphaMode() {
        return "OPAQUE";
    }

    /**
     * The alpha cutoff value of the material. (optional)<br> 
     * Default: 0.5<br> 
     * Minimum: 0.0 (inclusive) 
     * 
     * @param alphaCutoff The alphaCutoff to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setAlphaCutoff(Float alphaCutoff) {
        if (alphaCutoff == null) {
            this.alphaCutoff = alphaCutoff;
            return ;
        }
        if (alphaCutoff< 0.0D) {
            throw new IllegalArgumentException("alphaCutoff < 0.0");
        }
        this.alphaCutoff = alphaCutoff;
    }

    /**
     * The alpha cutoff value of the material. (optional)<br> 
     * Default: 0.5<br> 
     * Minimum: 0.0 (inclusive) 
     * 
     * @return The alphaCutoff
     * 
     */
    public Float getAlphaCutoff() {
        return this.alphaCutoff;
    }

    /**
     * Returns the default value of the alphaCutoff<br> 
     * @see #getAlphaCutoff 
     * 
     * @return The default alphaCutoff
     * 
     */
    public Float defaultAlphaCutoff() {
        return  0.5F;
    }

    /**
     * Specifies whether the material is double sided. (optional)<br> 
     * Default: false 
     * 
     * @param doubleSided The doubleSided to set
     * 
     */
    public void setDoubleSided(Boolean doubleSided) {
        if (doubleSided == null) {
            this.doubleSided = doubleSided;
            return ;
        }
        this.doubleSided = doubleSided;
    }

    /**
     * Specifies whether the material is double sided. (optional)<br> 
     * Default: false 
     * 
     * @return The doubleSided
     * 
     */
    public Boolean isDoubleSided() {
        return this.doubleSided;
    }

    /**
     * Returns the default value of the doubleSided<br> 
     * @see #isDoubleSided 
     * 
     * @return The default doubleSided
     * 
     */
    public Boolean defaultDoubleSided() {
        return false;
    }

}
