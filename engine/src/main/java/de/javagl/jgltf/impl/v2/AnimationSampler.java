/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016-2021 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v2;



/**
 * An animation sampler combines timestamps with a sequence of output 
 * values and defines an interpolation algorithm. 
 * 
 * Auto-generated for animation.sampler.schema.json 
 * 
 */
public class AnimationSampler
    extends GlTFProperty
{

    /**
     * The index of an accessor containing keyframe timestamps. (required) 
     * 
     */
    private Integer input;
    /**
     * Interpolation algorithm. (optional)<br> 
     * Default: "LINEAR"<br> 
     * Valid values: [LINEAR, STEP, CUBICSPLINE] 
     * 
     */
    private String interpolation;
    /**
     * The index of an accessor, containing keyframe output values. 
     * (required) 
     * 
     */
    private Integer output;

    /**
     * The index of an accessor containing keyframe timestamps. (required) 
     * 
     * @param input The input to set
     * @throws NullPointerException If the given value is <code>null</code>
     * 
     */
    public void setInput(Integer input) {
        if (input == null) {
            throw new NullPointerException((("Invalid value for input: "+ input)+", may not be null"));
        }
        this.input = input;
    }

    /**
     * The index of an accessor containing keyframe timestamps. (required) 
     * 
     * @return The input
     * 
     */
    public Integer getInput() {
        return this.input;
    }

    /**
     * Interpolation algorithm. (optional)<br> 
     * Default: "LINEAR"<br> 
     * Valid values: [LINEAR, STEP, CUBICSPLINE] 
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
        if (((!"LINEAR".equals(interpolation))&&(!"STEP".equals(interpolation)))&&(!"CUBICSPLINE".equals(interpolation))) {
            throw new IllegalArgumentException((("Invalid value for interpolation: "+ interpolation)+", valid: [LINEAR, STEP, CUBICSPLINE]"));
        }
        this.interpolation = interpolation;
    }

    /**
     * Interpolation algorithm. (optional)<br> 
     * Default: "LINEAR"<br> 
     * Valid values: [LINEAR, STEP, CUBICSPLINE] 
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
     * The index of an accessor, containing keyframe output values. 
     * (required) 
     * 
     * @param output The output to set
     * @throws NullPointerException If the given value is <code>null</code>
     * 
     */
    public void setOutput(Integer output) {
        if (output == null) {
            throw new NullPointerException((("Invalid value for output: "+ output)+", may not be null"));
        }
        this.output = output;
    }

    /**
     * The index of an accessor, containing keyframe output values. 
     * (required) 
     * 
     * @return The output
     * 
     */
    public Integer getOutput() {
        return this.output;
    }

}
