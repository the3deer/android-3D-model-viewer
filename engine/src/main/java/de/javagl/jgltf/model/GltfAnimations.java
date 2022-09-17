/*
 * www.javagl.de - JglTF
 *
 * Copyright 2015-2016 Marco Hutter - http://www.javagl.de
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import de.javagl.jgltf.model.AnimationModel.Channel;
import de.javagl.jgltf.model.AnimationModel.Interpolation;
import de.javagl.jgltf.model.AnimationModel.Sampler;
import de.javagl.jgltf.model.animation.Animation;
import de.javagl.jgltf.model.animation.AnimationListener;
import de.javagl.jgltf.model.animation.AnimationManager;
import de.javagl.jgltf.model.animation.AnimationManager.AnimationPolicy;
import de.javagl.jgltf.model.animation.InterpolatorType;

/**
 * Utility methods to create {@link AnimationManager} instances that
 * contain {@link Animation} instances that correspond to the 
 * {@link AnimationModel} instances of a glTF 
 */
public class GltfAnimations
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(GltfAnimations.class.getName());
    
    /**
     * Create a new {@link AnimationManager} using the given 
     * {@link AnimationPolicy}
     * 
     * @param animationPolicy The {@link AnimationPolicy}
     * @return The {@link AnimationManager}
     */
    public static AnimationManager createAnimationManager(
        AnimationPolicy animationPolicy)
    {
        AnimationManager animationManager = 
            new AnimationManager(animationPolicy);
        return animationManager;
    }
    
    /**
     * Create all model {@link Animation} instances from the given 
     * {@link AnimationModel} instances
     * 
     * @param animationModels The {@link AnimationModel} instances
     * @return The model animations
     */
    public static List<Animation> createModelAnimations(
        Iterable<? extends AnimationModel> animationModels)
    {
        Objects.requireNonNull(animationModels, 
            "The animationModels may not be null");
        List<Animation> allModelAnimations = new ArrayList<Animation>();
        for (AnimationModel animationModel : animationModels)
        {
            List<Channel> channels = animationModel.getChannels();
            List<Animation> modelAnimations = 
                createModelAnimationsForChannels(channels);
            allModelAnimations.addAll(modelAnimations);
        }
        return allModelAnimations;
    }
    
    /**
     * Create one {@link Animation} for each {@link Channel}.
     * If there is any error or inconsistency in the given data, then a 
     * warning will be printed and the respective animation will be
     * skipped.
     * 
     * @param channels The {@link Channel} list
     * @return The list of model animations
     */
    private static List<Animation> createModelAnimationsForChannels(
        Iterable<? extends Channel> channels)
    {
        List<Animation> modelAnimations = new ArrayList<Animation>();
        for (Channel channel : channels)
        {
            Animation modelAnimation = createModelAnimation(channel);
            if (modelAnimation != null)
            {
                modelAnimations.add(modelAnimation);
            }
        }
        return modelAnimations;
    }
    
    
    /**
     * Create the {@link Animation} for the given 
     * {@link Channel}. If there is any error or inconsistency
     * in the given data, then a warning will be printed and <code>null</code> 
     * will be returned.
     * 
     * @param channel The {@link Channel}
     * @return The {@link Animation}, or <code>null</code>.
     */
    private static Animation createModelAnimation(Channel channel)
    {
        Sampler sampler = channel.getSampler();
        Interpolation interpolation = sampler.getInterpolation();
        NodeModel nodeModel = channel.getNodeModel();
        String path = channel.getPath();
        
        AnimationListener animationListener = 
            createAnimationListener(nodeModel, path);
        if (animationListener == null)
        {
            return null;
        }

        InterpolatorType interpolatorType = 
            typeForInterpolation(interpolation, path);
        
        AccessorModel input = sampler.getInput();
        AccessorData inputData = input.getAccessorData();
        if (!(inputData instanceof AccessorFloatData))
        {
            logger.warning("Input data is not an AccessorFloatData, but "
                + inputData.getClass());
            return null;
        }
        AccessorFloatData inputFloatData = (AccessorFloatData)inputData;

        AccessorModel output = sampler.getOutput();
        AccessorData outputData = output.getAccessorData();
        if (!(outputData instanceof AccessorFloatData))
        {
            logger.warning("Output data is not an AccessorFloatData, but "
                + outputData.getClass());
            return null;
        }
        AccessorFloatData outputFloatData = (AccessorFloatData)outputData;
        
        Animation modelAnimation = 
            createAnimation(inputFloatData, outputFloatData, interpolatorType);
        modelAnimation.addAnimationListener(animationListener);
        return modelAnimation;
    }
    
    /**
     * Returns the {@link InterpolatorType} for the given {@link Interpolation}
     * and path
     * 
     * @param interpolation The {@link Interpolation}
     * @param path The path
     * @return The {@link InterpolatorType}
     */
    private static InterpolatorType typeForInterpolation(
        Interpolation interpolation, String path)
    {
        switch (interpolation)
        {
            case LINEAR:
            {
                if (path.equals("rotation")) 
                {
                    return InterpolatorType.SLERP;
                }
                return InterpolatorType.LINEAR;
            }
            case STEP:
            {
                return InterpolatorType.STEP;
            }
            
            case CUBICSPLINE:
            {
            }
            default:
                logger.warning("This interpolation type is not supported yet");
                break;
        }
        logger.warning(
            "Interpolation type not supported: " + interpolation);
        return InterpolatorType.LINEAR;
    }
    


    /**
     * Creates a new {@link Animation} from 
     * the given input data
     * 
     * @param timeData The (1D) {@link AccessorFloatData} containing the
     * time key frames
     * @param outputData The output data that contains the value key frames
     * @param interpolatorType The {@link InterpolatorType} that should
     * be used
     * @return The {@link Animation}
     */
    static Animation createAnimation(
        AccessorFloatData timeData,
        AccessorFloatData outputData, 
        InterpolatorType interpolatorType)
    {
        int numKeyElements = timeData.getNumElements();
        float keys[] = new float[numKeyElements];
        for (int e=0; e<numKeyElements; e++)
        {
            keys[e] = timeData.get(e);
        }
        
        // Note: The number of components per element that is used here
        // is NOT outputData.getNumComponentsPerElement() !!!
        // For morph target animations, the type of the output data will 
        // always be SCALAR. The actual number of components per element
        // has to be computed by dividing the total number of components
        // in the output data by the number of time elements. 
        // (For all animations except morph targets, the result will be 
        // equal to outputData.getNumComponentsPerElement(), though...)
        int totalNumValueComponents = 
            outputData.getTotalNumComponents();
        int numComponentsPerElement = 
            totalNumValueComponents / numKeyElements;
        float values[][] = new float[numKeyElements][numComponentsPerElement];
        for (int c = 0; c < numComponentsPerElement; c++)
        {
            for (int e = 0; e < numKeyElements; e++)
            {
                // Access the data using the global index, computed manually 
                // based on the computed number of components per element
                int globalIndex = e * numComponentsPerElement + c;
                values[e][c] = outputData.get(globalIndex);
            }
        }
        return new Animation(
            keys, values, interpolatorType);
    }

    /**
     * Creates an {@link AnimationListener} that writes the animation data
     * into the {@link NodeModel}, depending on the given path. If the given
     * path is not <code>"translation"</code>, <code>"rotation"</code>, 
     * <code>"scale"</code> or <code>"weights"</code>, then a warning 
     * will be printed and <code>null</code> will be returned.
     * 
     * @param nodeModel The {@link NodeModel}
     * @param path The path
     * @return The {@link AnimationListener}
     */
    private static AnimationListener createAnimationListener(
        NodeModel nodeModel, String path)
    {
        switch (path)
        {
            case "translation":
                return createTranslationAnimationListener(nodeModel);
                
            case "rotation":
                return createRotationAnimationListener(nodeModel);
                
            case "scale":
                return createScaleAnimationListener(nodeModel);
                
            case "weights":
                return createWeightsAnimationListener(nodeModel);
                
            default:
                break;
        }
        logger.warning("Animation channel target path must be "
            + "\"translation\", \"rotation\", \"scale\" or  \"weights\", "
            + "but is " + path);
        return null;
    }
    
    /**
     * Creates an {@link AnimationListener} that writes the animation data
     * into the {@link NodeModel#getTranslation() translation} of the 
     * {@link NodeModel}.
     * 
     * @param nodeModel The {@link NodeModel}
     * @return The {@link AnimationListener}
     */
    private static AnimationListener createTranslationAnimationListener(
        NodeModel nodeModel)
    {
        return (animation, timeS, values) ->
        {
            float translation[] = nodeModel.getTranslation();
            if (translation == null)
            {
                translation = values.clone();
                nodeModel.setTranslation(translation);
            }
            else
            {
                System.arraycopy(values, 0, translation, 0, values.length);
            }
        };
    }
    
    /**
     * Creates an {@link AnimationListener} that writes the animation data
     * into the {@link NodeModel#getRotation() rotation} of the 
     * {@link NodeModel}.
     * 
     * @param nodeModel The {@link NodeModel}
     * @return The {@link AnimationListener}
     */
    private static AnimationListener createRotationAnimationListener(
        NodeModel nodeModel)
    {
        return (animation, timeS, values) ->
        {
            float rotation[] = nodeModel.getRotation();
            if (rotation == null)
            {
                rotation = values.clone();
                nodeModel.setRotation(rotation);
            }
            else
            {
                System.arraycopy(values, 0, rotation, 0, values.length);
            }
        };
    }
    
    /**
     * Creates an {@link AnimationListener} that writes the animation data
     * into the {@link NodeModel#getScale() scale} of the 
     * {@link NodeModel}.
     * 
     * @param nodeModel The {@link NodeModel}
     * @return The {@link AnimationListener}
     */
    private static AnimationListener createScaleAnimationListener(
        NodeModel nodeModel)
    {
        return (animation, timeS, values) ->
        {
            float scale[] = nodeModel.getScale();
            if (scale == null)
            {
                scale = values.clone();
                nodeModel.setScale(scale);
            }
            else
            {
                System.arraycopy(values, 0, scale, 0, values.length);
            }
        };
    }
    
    /**
     * Creates an {@link AnimationListener} that writes the animation data
     * into the {@link NodeModel#getWeights() weights} of the 
     * {@link NodeModel}.
     * 
     * @param nodeModel The {@link NodeModel}
     * @return The {@link AnimationListener}
     */
    private static AnimationListener createWeightsAnimationListener(
        NodeModel nodeModel)
    {
        return (animation, timeS, values) ->
        {
            float weights[] = nodeModel.getWeights();
            if (weights == null)
            {
                weights = values.clone();
                nodeModel.setWeights(weights);
            }
            else
            {
                System.arraycopy(values, 0, weights, 0, values.length);
            }
        };
    }
    
    
    /**
     * Private constructor to prevent instantiation
     */
    private GltfAnimations()
    {
        // Private constructor to prevent instantiation
    }
}
