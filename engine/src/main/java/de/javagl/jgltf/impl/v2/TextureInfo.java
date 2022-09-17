/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016-2021 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v2;



/**
 * Reference to a texture. 
 * 
 * Auto-generated for textureInfo.schema.json 
 * 
 */
public class TextureInfo
    extends GlTFProperty
{

    /**
     * The index of the texture. (required) 
     * 
     */
    private Integer index;
    /**
     * The set index of texture's TEXCOORD attribute used for texture 
     * coordinate mapping. (optional)<br> 
     * Default: 0<br> 
     * Minimum: 0 (inclusive) 
     * 
     */
    private Integer texCoord;

    /**
     * The index of the texture. (required) 
     * 
     * @param index The index to set
     * @throws NullPointerException If the given value is <code>null</code>
     * 
     */
    public void setIndex(Integer index) {
        if (index == null) {
            throw new NullPointerException((("Invalid value for index: "+ index)+", may not be null"));
        }
        this.index = index;
    }

    /**
     * The index of the texture. (required) 
     * 
     * @return The index
     * 
     */
    public Integer getIndex() {
        return this.index;
    }

    /**
     * The set index of texture's TEXCOORD attribute used for texture 
     * coordinate mapping. (optional)<br> 
     * Default: 0<br> 
     * Minimum: 0 (inclusive) 
     * 
     * @param texCoord The texCoord to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setTexCoord(Integer texCoord) {
        if (texCoord == null) {
            this.texCoord = texCoord;
            return ;
        }
        if (texCoord< 0) {
            throw new IllegalArgumentException("texCoord < 0");
        }
        this.texCoord = texCoord;
    }

    /**
     * The set index of texture's TEXCOORD attribute used for texture 
     * coordinate mapping. (optional)<br> 
     * Default: 0<br> 
     * Minimum: 0 (inclusive) 
     * 
     * @return The texCoord
     * 
     */
    public Integer getTexCoord() {
        return this.texCoord;
    }

    /**
     * Returns the default value of the texCoord<br> 
     * @see #getTexCoord 
     * 
     * @return The default texCoord
     * 
     */
    public Integer defaultTexCoord() {
        return  0;
    }

}
