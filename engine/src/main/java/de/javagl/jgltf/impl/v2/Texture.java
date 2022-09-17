/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016-2021 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v2;



/**
 * A texture and its sampler. 
 * 
 * Auto-generated for texture.schema.json 
 * 
 */
public class Texture
    extends GlTFChildOfRootProperty
{

    /**
     * The index of the sampler used by this texture. When undefined, a 
     * sampler with repeat wrapping and auto filtering **SHOULD** be used. 
     * (optional) 
     * 
     */
    private Integer sampler;
    /**
     * The index of the image used by this texture. When undefined, an 
     * extension or other mechanism **SHOULD** supply an alternate texture 
     * source, otherwise behavior is undefined. (optional) 
     * 
     */
    private Integer source;

    /**
     * The index of the sampler used by this texture. When undefined, a 
     * sampler with repeat wrapping and auto filtering **SHOULD** be used. 
     * (optional) 
     * 
     * @param sampler The sampler to set
     * 
     */
    public void setSampler(Integer sampler) {
        if (sampler == null) {
            this.sampler = sampler;
            return ;
        }
        this.sampler = sampler;
    }

    /**
     * The index of the sampler used by this texture. When undefined, a 
     * sampler with repeat wrapping and auto filtering **SHOULD** be used. 
     * (optional) 
     * 
     * @return The sampler
     * 
     */
    public Integer getSampler() {
        return this.sampler;
    }

    /**
     * The index of the image used by this texture. When undefined, an 
     * extension or other mechanism **SHOULD** supply an alternate texture 
     * source, otherwise behavior is undefined. (optional) 
     * 
     * @param source The source to set
     * 
     */
    public void setSource(Integer source) {
        if (source == null) {
            this.source = source;
            return ;
        }
        this.source = source;
    }

    /**
     * The index of the image used by this texture. When undefined, an 
     * extension or other mechanism **SHOULD** supply an alternate texture 
     * source, otherwise behavior is undefined. (optional) 
     * 
     * @return The source
     * 
     */
    public Integer getSource() {
        return this.source;
    }

}
