/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016-2021 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v2;



/**
 * The descriptor of the animated property. 
 * 
 * Auto-generated for animation.channel.target.schema.json 
 * 
 */
public class AnimationChannelTarget
    extends GlTFProperty
{

    /**
     * The index of the node to animate. When undefined, the animated object 
     * **MAY** be defined by an extension. (optional) 
     * 
     */
    private Integer node;
    /**
     * The name of the node's TRS property to animate, or the `"weights"` of 
     * the Morph Targets it instantiates. For the `"translation"` property, 
     * the values that are provided by the sampler are the translation along 
     * the X, Y, and Z axes. For the `"rotation"` property, the values are a 
     * quaternion in the order (x, y, z, w), where w is the scalar. For the 
     * `"scale"` property, the values are the scaling factors along the X, Y, 
     * and Z axes. (required)<br> 
     * Valid values: [translation, rotation, scale, weights] 
     * 
     */
    private String path;

    /**
     * The index of the node to animate. When undefined, the animated object 
     * **MAY** be defined by an extension. (optional) 
     * 
     * @param node The node to set
     * 
     */
    public void setNode(Integer node) {
        if (node == null) {
            this.node = node;
            return ;
        }
        this.node = node;
    }

    /**
     * The index of the node to animate. When undefined, the animated object 
     * **MAY** be defined by an extension. (optional) 
     * 
     * @return The node
     * 
     */
    public Integer getNode() {
        return this.node;
    }

    /**
     * The name of the node's TRS property to animate, or the `"weights"` of 
     * the Morph Targets it instantiates. For the `"translation"` property, 
     * the values that are provided by the sampler are the translation along 
     * the X, Y, and Z axes. For the `"rotation"` property, the values are a 
     * quaternion in the order (x, y, z, w), where w is the scalar. For the 
     * `"scale"` property, the values are the scaling factors along the X, Y, 
     * and Z axes. (required)<br> 
     * Valid values: [translation, rotation, scale, weights] 
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
        if ((((!"translation".equals(path))&&(!"rotation".equals(path)))&&(!"scale".equals(path)))&&(!"weights".equals(path))) {
            throw new IllegalArgumentException((("Invalid value for path: "+ path)+", valid: [translation, rotation, scale, weights]"));
        }
        this.path = path;
    }

    /**
     * The name of the node's TRS property to animate, or the `"weights"` of 
     * the Morph Targets it instantiates. For the `"translation"` property, 
     * the values that are provided by the sampler are the translation along 
     * the X, Y, and Z axes. For the `"rotation"` property, the values are a 
     * quaternion in the order (x, y, z, w), where w is the scalar. For the 
     * `"scale"` property, the values are the scaling factors along the X, Y, 
     * and Z axes. (required)<br> 
     * Valid values: [translation, rotation, scale, weights] 
     * 
     * @return The path
     * 
     */
    public String getPath() {
        return this.path;
    }

}
