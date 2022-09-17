/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v1;



/**
 * Targets an animation's sampler at a node's property. 
 * 
 * Auto-generated for animation.channel.schema.json 
 * 
 */
public class AnimationChannel
    extends GlTFProperty
{

    /**
     * The ID of a sampler in this animation used to compute the value for 
     * the target. (required) 
     * 
     */
    private String sampler;
    /**
     * The ID of the node and TRS property to target. (required) 
     * 
     */
    private AnimationChannelTarget target;

    /**
     * The ID of a sampler in this animation used to compute the value for 
     * the target. (required) 
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
     * The ID of a sampler in this animation used to compute the value for 
     * the target. (required) 
     * 
     * @return The sampler
     * 
     */
    public String getSampler() {
        return this.sampler;
    }

    /**
     * The ID of the node and TRS property to target. (required) 
     * 
     * @param target The target to set
     * @throws NullPointerException If the given value is <code>null</code>
     * 
     */
    public void setTarget(AnimationChannelTarget target) {
        if (target == null) {
            throw new NullPointerException((("Invalid value for target: "+ target)+", may not be null"));
        }
        this.target = target;
    }

    /**
     * The ID of the node and TRS property to target. (required) 
     * 
     * @return The target
     * 
     */
    public AnimationChannelTarget getTarget() {
        return this.target;
    }

}
