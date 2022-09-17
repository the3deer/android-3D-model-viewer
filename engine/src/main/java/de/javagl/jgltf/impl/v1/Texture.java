/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v1;



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
     * The texture's format. (optional)<br> 
     * Default: 6408<br> 
     * Valid values: [6406, 6407, 6408, 6409, 6410] 
     * 
     */
    private Integer format;
    /**
     * The texture's internal format. (optional)<br> 
     * Default: 6408<br> 
     * Valid values: [6406, 6407, 6408, 6409, 6410] 
     * 
     */
    private Integer internalFormat;
    /**
     * The ID of the sampler used by this texture. (required) 
     * 
     */
    private String sampler;
    /**
     * The ID of the image used by this texture. (required) 
     * 
     */
    private String source;
    /**
     * The target that the WebGL texture should be bound to. (optional)<br> 
     * Default: 3553<br> 
     * Valid values: [3553] 
     * 
     */
    private Integer target;
    /**
     * Texel datatype. (optional)<br> 
     * Default: 5121<br> 
     * Valid values: [5121, 33635, 32819, 32820] 
     * 
     */
    private Integer type;

    /**
     * The texture's format. (optional)<br> 
     * Default: 6408<br> 
     * Valid values: [6406, 6407, 6408, 6409, 6410] 
     * 
     * @param format The format to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setFormat(Integer format) {
        if (format == null) {
            this.format = format;
            return ;
        }
        if (((((format!= 6406)&&(format!= 6407))&&(format!= 6408))&&(format!= 6409))&&(format!= 6410)) {
            throw new IllegalArgumentException((("Invalid value for format: "+ format)+", valid: [6406, 6407, 6408, 6409, 6410]"));
        }
        this.format = format;
    }

    /**
     * The texture's format. (optional)<br> 
     * Default: 6408<br> 
     * Valid values: [6406, 6407, 6408, 6409, 6410] 
     * 
     * @return The format
     * 
     */
    public Integer getFormat() {
        return this.format;
    }

    /**
     * Returns the default value of the format<br> 
     * @see #getFormat 
     * 
     * @return The default format
     * 
     */
    public Integer defaultFormat() {
        return  6408;
    }

    /**
     * The texture's internal format. (optional)<br> 
     * Default: 6408<br> 
     * Valid values: [6406, 6407, 6408, 6409, 6410] 
     * 
     * @param internalFormat The internalFormat to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setInternalFormat(Integer internalFormat) {
        if (internalFormat == null) {
            this.internalFormat = internalFormat;
            return ;
        }
        if (((((internalFormat!= 6406)&&(internalFormat!= 6407))&&(internalFormat!= 6408))&&(internalFormat!= 6409))&&(internalFormat!= 6410)) {
            throw new IllegalArgumentException((("Invalid value for internalFormat: "+ internalFormat)+", valid: [6406, 6407, 6408, 6409, 6410]"));
        }
        this.internalFormat = internalFormat;
    }

    /**
     * The texture's internal format. (optional)<br> 
     * Default: 6408<br> 
     * Valid values: [6406, 6407, 6408, 6409, 6410] 
     * 
     * @return The internalFormat
     * 
     */
    public Integer getInternalFormat() {
        return this.internalFormat;
    }

    /**
     * Returns the default value of the internalFormat<br> 
     * @see #getInternalFormat 
     * 
     * @return The default internalFormat
     * 
     */
    public Integer defaultInternalFormat() {
        return  6408;
    }

    /**
     * The ID of the sampler used by this texture. (required) 
     * 
     * @param sampler The sampler to set
     * @throws NullPointerException If the given value is <code>null</code>
     * 
     */
    public void setSampler(String sampler) {
        if (sampler == null) {
            throw new NullPointerException((("Invalid value for sampler: "+ sampler)+", may not be null"));
        }
        this.sampler = sampler;
    }

    /**
     * The ID of the sampler used by this texture. (required) 
     * 
     * @return The sampler
     * 
     */
    public String getSampler() {
        return this.sampler;
    }

    /**
     * The ID of the image used by this texture. (required) 
     * 
     * @param source The source to set
     * @throws NullPointerException If the given value is <code>null</code>
     * 
     */
    public void setSource(String source) {
        if (source == null) {
            throw new NullPointerException((("Invalid value for source: "+ source)+", may not be null"));
        }
        this.source = source;
    }

    /**
     * The ID of the image used by this texture. (required) 
     * 
     * @return The source
     * 
     */
    public String getSource() {
        return this.source;
    }

    /**
     * The target that the WebGL texture should be bound to. (optional)<br> 
     * Default: 3553<br> 
     * Valid values: [3553] 
     * 
     * @param target The target to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setTarget(Integer target) {
        if (target == null) {
            this.target = target;
            return ;
        }
        if (target!= 3553) {
            throw new IllegalArgumentException((("Invalid value for target: "+ target)+", valid: [3553]"));
        }
        this.target = target;
    }

    /**
     * The target that the WebGL texture should be bound to. (optional)<br> 
     * Default: 3553<br> 
     * Valid values: [3553] 
     * 
     * @return The target
     * 
     */
    public Integer getTarget() {
        return this.target;
    }

    /**
     * Returns the default value of the target<br> 
     * @see #getTarget 
     * 
     * @return The default target
     * 
     */
    public Integer defaultTarget() {
        return  3553;
    }

    /**
     * Texel datatype. (optional)<br> 
     * Default: 5121<br> 
     * Valid values: [5121, 33635, 32819, 32820] 
     * 
     * @param type The type to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setType(Integer type) {
        if (type == null) {
            this.type = type;
            return ;
        }
        if ((((type!= 5121)&&(type!= 33635))&&(type!= 32819))&&(type!= 32820)) {
            throw new IllegalArgumentException((("Invalid value for type: "+ type)+", valid: [5121, 33635, 32819, 32820]"));
        }
        this.type = type;
    }

    /**
     * Texel datatype. (optional)<br> 
     * Default: 5121<br> 
     * Valid values: [5121, 33635, 32819, 32820] 
     * 
     * @return The type
     * 
     */
    public Integer getType() {
        return this.type;
    }

    /**
     * Returns the default value of the type<br> 
     * @see #getType 
     * 
     * @return The default type
     * 
     */
    public Integer defaultType() {
        return  5121;
    }

}
