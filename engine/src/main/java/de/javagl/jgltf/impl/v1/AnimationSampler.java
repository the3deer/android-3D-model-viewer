/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v1;



/**
 * Combines input and output parameters with an interpolation algorithm 
 * to define a keyframe graph (but not its target). 
 * 
 * Auto-generated for animation.sampler.schema.json 
 * 
 */
public class AnimationSampler
    extends GlTFProperty
{

    /**
     * The ID of a parameter in this animation to use as keyframe input, 
     * e.g., time. (required) 
     * 
     */
    private String input;
    /**
     * Interpolation algorithm. (optional)<br> 
     * Default: "LINEAR"<br> 
     * Valid values: ["LINEAR", "STEP"] 
     * 
     */
    private String interpolation;
    /**
     * The ID of a parameter in this animation to use as keyframe output. 
     * (required) 
     * 
     */
    private String output;

    /**
     * The ID of a parameter in this animation to use as keyframe input, 
     * e.g., time. (required) 
     * 
     * @param input The input to set
     * @throws NullPointerException If the given value is <code>null</code>
     * 
     */
    public void setInput(String input) {
        if (input == null) {
            throw new NullPointerException((("Invalid value for input: "+ input)+", may not be null"));
        }
        this.input = input;
    }

    /**
     * The ID of a parameter in this animation to use as keyframe input, 
     * e.g., time. (required) 
     * 
     * @return The input
     * 
     */
    public String getInput() {
        return this.input;
    }

    /**
     * Interpolation algorithm. (optional)<br> 
     * Default: "LINEAR"<br> 
     * Valid values: ["LINEAR"] 
     * 
     * @param interpolation The interpolation to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setInterpolation(String interpolation) {
        if (interpolation == null) {
            this.interpolation = interpolation;
            return ;
        }
        if ((!"LINEAR".equals(interpolation))&&(!"STEP".equals(interpolation))) {
            throw new IllegalArgumentException((("Invalid value for interpolation: "+ interpolation)+", valid: [\"LINEAR\", \"STEP\"]"));
        }
        this.interpolation = interpolation;
    }

    /**
     * Interpolation algorithm. (optional)<br> 
     * Default: "LINEAR"<br> 
     * Valid values: ["LINEAR"] 
     * 
     * @return The interpolation
     * 
     */
    public String getInterpolation() {
        return this.interpolation;
    }

    /**
     * Returns the default value of the interpolation<br> 
     * @see #getInterpolation 
     * 
     * @return The default interpolation
     * 
     */
    public String defaultInterpolation() {
        return "LINEAR";
    }

    /**
     * The ID of a parameter in this animation to use as keyframe output. 
     * (required) 
     * 
     * @param output The output to set
     * @throws NullPointerException If the given value is <code>null</code>
     * 
     */
    public void setOutput(String output) {
        if (output == null) {
            throw new NullPointerException((("Invalid value for output: "+ output)+", may not be null"));
        }
        this.output = output;
    }

    /**
     * The ID of a parameter in this animation to use as keyframe output. 
     * (required) 
     * 
     * @return The output
     * 
     */
    public String getOutput() {
        return this.output;
    }

}
