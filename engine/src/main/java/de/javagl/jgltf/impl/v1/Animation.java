/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v1;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * A keyframe animation. 
 * 
 * Auto-generated for animation.schema.json 
 * 
 */
public class Animation
    extends GlTFChildOfRootProperty
{

    /**
     * An array of channels, each of which targets an animation's sampler at 
     * a node's property. (optional)<br> 
     * Default: []<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;Targets an animation's sampler at a node's property. 
     * (optional) 
     * 
     */
    private List<AnimationChannel> channels;
    /**
     * A dictionary object of strings whose values are IDs of accessors with 
     * keyframe data, e.g., time, translation, rotation, etc. (optional)<br> 
     * Default: {} 
     * 
     */
    private Map<String, String> parameters;
    /**
     * A dictionary object of samplers that combines input and output 
     * parameters with an interpolation algorithm to define a keyframe graph 
     * (but not its target). (optional)<br> 
     * Default: {} 
     * 
     */
    private Map<String, AnimationSampler> samplers;

    /**
     * An array of channels, each of which targets an animation's sampler at 
     * a node's property. (optional)<br> 
     * Default: []<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;Targets an animation's sampler at a node's property. 
     * (optional) 
     * 
     * @param channels The channels to set
     * 
     */
    public void setChannels(List<AnimationChannel> channels) {
        if (channels == null) {
            this.channels = channels;
            return ;
        }
        this.channels = channels;
    }

    /**
     * An array of channels, each of which targets an animation's sampler at 
     * a node's property. (optional)<br> 
     * Default: []<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;Targets an animation's sampler at a node's property. 
     * (optional) 
     * 
     * @return The channels
     * 
     */
    public List<AnimationChannel> getChannels() {
        return this.channels;
    }

    /**
     * Add the given channels. The channels of this instance will be replaced 
     * with a list that contains all previous elements, and additionally the 
     * new element. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void addChannels(AnimationChannel element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<AnimationChannel> oldList = this.channels;
        List<AnimationChannel> newList = new ArrayList<AnimationChannel>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.add(element);
        this.channels = newList;
    }

    /**
     * Remove the given channels. The channels of this instance will be 
     * replaced with a list that contains all previous elements, except for 
     * the removed one.<br> 
     * If this new list would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void removeChannels(AnimationChannel element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<AnimationChannel> oldList = this.channels;
        List<AnimationChannel> newList = new ArrayList<AnimationChannel>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.remove(element);
        if (newList.isEmpty()) {
            this.channels = null;
        } else {
            this.channels = newList;
        }
    }

    /**
     * Returns the default value of the channels<br> 
     * @see #getChannels 
     * 
     * @return The default channels
     * 
     */
    public List<AnimationChannel> defaultChannels() {
        return new ArrayList<AnimationChannel>();
    }

    /**
     * A dictionary object of strings whose values are IDs of accessors with 
     * keyframe data, e.g., time, translation, rotation, etc. (optional)<br> 
     * Default: {} 
     * 
     * @param parameters The parameters to set
     * 
     */
    public void setParameters(Map<String, String> parameters) {
        if (parameters == null) {
            this.parameters = parameters;
            return ;
        }
        this.parameters = parameters;
    }

    /**
     * A dictionary object of strings whose values are IDs of accessors with 
     * keyframe data, e.g., time, translation, rotation, etc. (optional)<br> 
     * Default: {} 
     * 
     * @return The parameters
     * 
     */
    public Map<String, String> getParameters() {
        return this.parameters;
    }

    /**
     * Add the given parameters. The parameters of this instance will be 
     * replaced with a map that contains all previous mappings, and 
     * additionally the new mapping. 
     * 
     * @param key The key
     * @param value The value
     * @throws NullPointerException If the given key or value is <code>null</code>
     * 
     */
    public void addParameters(String key, String value) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        if (value == null) {
            throw new NullPointerException("The value may not be null");
        }
        Map<String, String> oldMap = this.parameters;
        Map<String, String> newMap = new LinkedHashMap<String, String>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.put(key, value);
        this.parameters = newMap;
    }

    /**
     * Remove the given parameters. The parameters of this instance will be 
     * replaced with a map that contains all previous mappings, except for 
     * the one with the given key.<br> 
     * If this new map would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param key The key
     * @throws NullPointerException If the given key is <code>null</code>
     * 
     */
    public void removeParameters(String key) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        Map<String, String> oldMap = this.parameters;
        Map<String, String> newMap = new LinkedHashMap<String, String>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.remove(key);
        if (newMap.isEmpty()) {
            this.parameters = null;
        } else {
            this.parameters = newMap;
        }
    }

    /**
     * Returns the default value of the parameters<br> 
     * @see #getParameters 
     * 
     * @return The default parameters
     * 
     */
    public Map<String, String> defaultParameters() {
        return new LinkedHashMap<String, String>();
    }

    /**
     * A dictionary object of samplers that combines input and output 
     * parameters with an interpolation algorithm to define a keyframe graph 
     * (but not its target). (optional)<br> 
     * Default: {} 
     * 
     * @param samplers The samplers to set
     * 
     */
    public void setSamplers(Map<String, AnimationSampler> samplers) {
        if (samplers == null) {
            this.samplers = samplers;
            return ;
        }
        this.samplers = samplers;
    }

    /**
     * A dictionary object of samplers that combines input and output 
     * parameters with an interpolation algorithm to define a keyframe graph 
     * (but not its target). (optional)<br> 
     * Default: {} 
     * 
     * @return The samplers
     * 
     */
    public Map<String, AnimationSampler> getSamplers() {
        return this.samplers;
    }

    /**
     * Add the given samplers. The samplers of this instance will be replaced 
     * with a map that contains all previous mappings, and additionally the 
     * new mapping. 
     * 
     * @param key The key
     * @param value The value
     * @throws NullPointerException If the given key or value is <code>null</code>
     * 
     */
    public void addSamplers(String key, AnimationSampler value) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        if (value == null) {
            throw new NullPointerException("The value may not be null");
        }
        Map<String, AnimationSampler> oldMap = this.samplers;
        Map<String, AnimationSampler> newMap = new LinkedHashMap<String, AnimationSampler>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.put(key, value);
        this.samplers = newMap;
    }

    /**
     * Remove the given samplers. The samplers of this instance will be 
     * replaced with a map that contains all previous mappings, except for 
     * the one with the given key.<br> 
     * If this new map would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param key The key
     * @throws NullPointerException If the given key is <code>null</code>
     * 
     */
    public void removeSamplers(String key) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        Map<String, AnimationSampler> oldMap = this.samplers;
        Map<String, AnimationSampler> newMap = new LinkedHashMap<String, AnimationSampler>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.remove(key);
        if (newMap.isEmpty()) {
            this.samplers = null;
        } else {
            this.samplers = newMap;
        }
    }

    /**
     * Returns the default value of the samplers<br> 
     * @see #getSamplers 
     * 
     * @return The default samplers
     * 
     */
    public Map<String, AnimationSampler> defaultSamplers() {
        return new LinkedHashMap<String, AnimationSampler>();
    }

}
