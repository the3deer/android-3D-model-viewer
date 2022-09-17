/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v1;



/**
 * A vertex or fragment shader. 
 * 
 * Auto-generated for shader.schema.json 
 * 
 */
public class Shader
    extends GlTFChildOfRootProperty
{

    /**
     * The uri of the GLSL source. (required) 
     * 
     */
    private String uri;
    /**
     * The shader stage. (required)<br> 
     * Valid values: [35632, 35633] 
     * 
     */
    private Integer type;

    /**
     * The uri of the GLSL source. (required) 
     * 
     * @param uri The uri to set
     * @throws NullPointerException If the given value is <code>null</code>
     * 
     */
    public void setUri(String uri) {
        if (uri == null) {
            throw new NullPointerException((("Invalid value for uri: "+ uri)+", may not be null"));
        }
        this.uri = uri;
    }

    /**
     * The uri of the GLSL source. (required) 
     * 
     * @return The uri
     * 
     */
    public String getUri() {
        return this.uri;
    }

    /**
     * The shader stage. (required)<br> 
     * Valid values: [35632, 35633] 
     * 
     * @param type The type to set
     * @throws NullPointerException If the given value is <code>null</code>
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setType(Integer type) {
        if (type == null) {
            throw new NullPointerException((("Invalid value for type: "+ type)+", may not be null"));
        }
        if ((type!= 35632)&&(type!= 35633)) {
            throw new IllegalArgumentException((("Invalid value for type: "+ type)+", valid: [35632, 35633]"));
        }
        this.type = type;
    }

    /**
     * The shader stage. (required)<br> 
     * Valid values: [35632, 35633] 
     * 
     * @return The type
     * 
     */
    public Integer getType() {
        return this.type;
    }

}
