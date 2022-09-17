/*
 * www.javagl.de - JglTF
 *
 * Copyright 2015-2017 Marco Hutter - http://www.javagl.de
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package de.javagl.jgltf.model;

import java.util.List;

/**
 * Interface for an animation
 */
public interface AnimationModel extends NamedModelElement
{
    /**
     * Enumeration of the different interpolation methods for an animation
     */
    public enum Interpolation
    {
        /**
         * Stepwise interpolation
         */
        STEP,
        
        /**
         * Linear interpolation
         */
        LINEAR,
        
        /**
         * Cubic spline interpolation
         */
        CUBICSPLINE        
    }
    
    /**
     * Interface for an animation channel
     */
    public interface Channel 
    {
        /**
         * Returns the {@link Sampler} for this channel
         * 
         * @return The {@link Sampler}
         */
        Sampler getSampler();
        
        /**
         * Returns the optional {@link NodeModel} to which the animated
         * property (path) belongs.
         * 
         * @return The {@link NodeModel}
         */
        NodeModel getNodeModel();
        
        /**
         * Returns the path describing the animated property
         * 
         * @return The path
         */
        String getPath();
    }
    
    /**
     * Interface for an animation sampler
     */
    public interface Sampler
    {
        /**
         * Returns the {@link AccessorModel} that contains the input (time
         * key frame) data
         * 
         * @return The input data
         */
        AccessorModel getInput();
        
        /**
         * Returns the {@link Interpolation} method
         * 
         * @return The {@link Interpolation}
         */
        Interpolation getInterpolation();
        
        /**
         * Returns the {@link AccessorModel} that contains the output (value
         * key frame) data
         * 
         * @return The output data
         */
        AccessorModel getOutput();
    }
    
    /**
     * Returns an unmodifiable list containing the {@link Channel} instances
     * of the animation
     * 
     * @return The {@link Channel} instances
     */
    List<Channel> getChannels();
}
