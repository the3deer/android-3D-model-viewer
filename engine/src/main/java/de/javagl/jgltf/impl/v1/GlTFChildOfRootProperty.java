/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v1;



/**
 * Auto-generated for glTFChildOfRootProperty.schema.json 
 * 
 */
public class GlTFChildOfRootProperty
    extends GlTFProperty
{

    /**
     * The user-defined name of this object. (optional) 
     * 
     */
    private String name;

    /**
     * The user-defined name of this object. (optional) 
     * 
     * @param name The name to set
     * 
     */
    public void setName(String name) {
        if (name == null) {
            this.name = name;
            return ;
        }
        this.name = name;
    }

    /**
     * The user-defined name of this object. (optional) 
     * 
     * @return The name
     * 
     */
    public String getName() {
        return this.name;
    }

}
