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
package de.javagl.jgltf.model.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.AnimationModel;
import de.javagl.jgltf.model.NodeModel;

/**
 * Implementation of an {@link AnimationModel}
 */
public class DefaultAnimationModel extends AbstractNamedModelElement
    implements AnimationModel
{
    /**
     * Default implementation of a 
     * {@link Sampler}
     */
    public static class DefaultSampler implements Sampler
    {
        /**
         * The input data
         */
        private final AccessorModel input;
        
        /**
         * The interpolation method
         */
        private final Interpolation interpolation;
        
        /**
         * The output data
         */
        private final AccessorModel output;
        
        /**
         * Default constructor
         * 
         * @param input The input
         * @param interpolation The interpolation
         * @param output The output
         */
        public DefaultSampler(
            AccessorModel input,
            Interpolation interpolation,
            AccessorModel output)
        {
            this.input = Objects.requireNonNull(
                input, "The input may not be null");
            this.interpolation = Objects.requireNonNull(
                interpolation, "The interpolation may not be null");
            this.output = Objects.requireNonNull(
                output, "The output may not be null");
        }
        
        @Override
        public AccessorModel getInput()
        {
            return input;
        }

        @Override
        public Interpolation getInterpolation()
        {
            return interpolation;
        }

        @Override
        public AccessorModel getOutput()
        {
            return output;
        }
    }
    
    /**
     * Default implementation of a 
     * {@link Channel}
     */
    public static class DefaultChannel implements Channel
    {
        /**
         * The sampler
         */
        private final Sampler sampler;
        
        /**
         * The node model
         */
        private final NodeModel nodeModel;
        
        /**
         * The path
         */
        private final String path;
        
        /**
         * Default constructor
         * 
         * @param sampler The sampler
         * @param nodeModel The node model
         * @param path The path
         */
        public DefaultChannel(
            Sampler sampler,
            NodeModel nodeModel,
            String path)
        {
            this.sampler = Objects.requireNonNull(
                sampler, "The sampler may not be null");
            this.nodeModel = nodeModel;
            this.path = Objects.requireNonNull(
                path, "The path may not be null");
            
        }
        
        @Override
        public Sampler getSampler()
        {
            return sampler;
        }

        @Override
        public NodeModel getNodeModel()
        {
            return nodeModel;
        }

        @Override
        public String getPath()
        {
            return path;
        }
        
    }
    
    /**
     * The {@link Channel} instances
     * of this animation
     */
    private final List<Channel> channels;
    
    /**
     * Creates a new instance
     */
    public DefaultAnimationModel()
    {
        this.channels = new ArrayList<Channel>();
    }
    
    /**
     * Add the given {@link Channel}
     * 
     * @param channel The {@link Channel}
     */
    public void addChannel(Channel channel)
    {
        Objects.requireNonNull(channel, "The channel may not be null");
        this.channels.add(channel);
    }
    
    @Override
    public List<Channel> getChannels()
    {
        return Collections.unmodifiableList(channels);
    }
    
}