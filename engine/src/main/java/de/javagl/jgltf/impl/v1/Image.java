/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v1;



/**
 * Image data used to create a texture. 
 * 
 * Auto-generated for image.schema.json 
 * 
 */
public class Image
    extends GlTFChildOfRootProperty
{

    /**
     * The uri of the image. (required) 
     * 
     */
    private String uri;

    /**
     * The uri of the image. (required) 
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
     * The uri of the image. (required) 
     * 
     * @return The uri
     * 
     */
    public String getUri() {
        return this.uri;
    }

}
