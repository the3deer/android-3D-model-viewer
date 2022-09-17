/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v1;



/**
 * The ID of the node and TRS property that an animation channel targets. 
 * 
 * Auto-generated for animation.channel.target.schema.json 
 * 
 */
public class AnimationChannelTarget
    extends GlTFProperty
{

    /**
     * The ID of the node to target. (required) 
     * 
     */
    private String id;
    /**
     * The name of the node's TRS property to modify. (required)<br> 
     * Valid values: ["translation", "rotation", "scale"] 
     * 
     */
    private String path;

    /**
     * The ID of the node to target. (required) 
     * 
     * @param id The id to set
     * @throws NullPointerException If the given value is <code>null</code>
     * 
     */
    public void setId(String id) {
        if (id == null) {
            throw new NullPointerException((("Invalid value for id: "+ id)+", may not be null"));
        }
        this.id = id;
    }

    /**
     * The ID of the node to target. (required) 
     * 
     * @return The id
     * 
     */
    public String getId() {
        return this.id;
    }

    /**
     * The name of the node's TRS property to modify. (required)<br> 
     * Valid values: ["translation", "rotation", "scale"] 
     * 
     * @param path The path to set
     * @throws NullPointerException If the given value is <code>null</code>
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setPath(String path) {
        if (path == null) {
            throw new NullPointerException((("Invalid value for path: "+ path)+", may not be null"));
        }
        if (((!"translation".equals(path))&&(!"rotation".equals(path)))&&(!"scale".equals(path))) {
            throw new IllegalArgumentException((("Invalid value for path: "+ path)+", valid: [\"translation\", \"rotation\", \"scale\"]"));
        }
        this.path = path;
    }

    /**
     * The name of the node's TRS property to modify. (required)<br> 
     * Valid values: ["translation", "rotation", "scale"] 
     * 
     * @return The path
     * 
     */
    public String getPath() {
        return this.path;
    }

}
