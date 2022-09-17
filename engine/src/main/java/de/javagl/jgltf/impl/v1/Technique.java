/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v1;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * A template for material appearances. 
 * 
 * Auto-generated for technique.schema.json 
 * 
 */
public class Technique
    extends GlTFChildOfRootProperty
{

    /**
     * A dictionary object of technique.parameters objects. (optional)<br> 
     * Default: {} 
     * 
     */
    private Map<String, TechniqueParameters> parameters;
    /**
     * A dictionary object of strings that maps GLSL attribute names to 
     * technique parameter IDs. (optional)<br> 
     * Default: {} 
     * 
     */
    private Map<String, String> attributes;
    /**
     * The ID of the program. (required) 
     * 
     */
    private String program;
    /**
     * A dictionary object of strings that maps GLSL uniform names to 
     * technique parameter IDs. (optional)<br> 
     * Default: {} 
     * 
     */
    private Map<String, String> uniforms;
    /**
     * Fixed-function rendering states. (optional)<br> 
     * Default: {} 
     * 
     */
    private TechniqueStates states;

    /**
     * A dictionary object of technique.parameters objects. (optional)<br> 
     * Default: {} 
     * 
     * @param parameters The parameters to set
     * 
     */
    public void setParameters(Map<String, TechniqueParameters> parameters) {
        if (parameters == null) {
            this.parameters = parameters;
            return ;
        }
        this.parameters = parameters;
    }

    /**
     * A dictionary object of technique.parameters objects. (optional)<br> 
     * Default: {} 
     * 
     * @return The parameters
     * 
     */
    public Map<String, TechniqueParameters> getParameters() {
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
    public void addParameters(String key, TechniqueParameters value) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        if (value == null) {
            throw new NullPointerException("The value may not be null");
        }
        Map<String, TechniqueParameters> oldMap = this.parameters;
        Map<String, TechniqueParameters> newMap = new LinkedHashMap<String, TechniqueParameters>();
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
        Map<String, TechniqueParameters> oldMap = this.parameters;
        Map<String, TechniqueParameters> newMap = new LinkedHashMap<String, TechniqueParameters>();
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
    public Map<String, TechniqueParameters> defaultParameters() {
        return new LinkedHashMap<String, TechniqueParameters>();
    }

    /**
     * A dictionary object of strings that maps GLSL attribute names to 
     * technique parameter IDs. (optional)<br> 
     * Default: {} 
     * 
     * @param attributes The attributes to set
     * 
     */
    public void setAttributes(Map<String, String> attributes) {
        if (attributes == null) {
            this.attributes = attributes;
            return ;
        }
        this.attributes = attributes;
    }

    /**
     * A dictionary object of strings that maps GLSL attribute names to 
     * technique parameter IDs. (optional)<br> 
     * Default: {} 
     * 
     * @return The attributes
     * 
     */
    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    /**
     * Add the given attributes. The attributes of this instance will be 
     * replaced with a map that contains all previous mappings, and 
     * additionally the new mapping. 
     * 
     * @param key The key
     * @param value The value
     * @throws NullPointerException If the given key or value is <code>null</code>
     * 
     */
    public void addAttributes(String key, String value) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        if (value == null) {
            throw new NullPointerException("The value may not be null");
        }
        Map<String, String> oldMap = this.attributes;
        Map<String, String> newMap = new LinkedHashMap<String, String>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.put(key, value);
        this.attributes = newMap;
    }

    /**
     * Remove the given attributes. The attributes of this instance will be 
     * replaced with a map that contains all previous mappings, except for 
     * the one with the given key.<br> 
     * If this new map would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param key The key
     * @throws NullPointerException If the given key is <code>null</code>
     * 
     */
    public void removeAttributes(String key) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        Map<String, String> oldMap = this.attributes;
        Map<String, String> newMap = new LinkedHashMap<String, String>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.remove(key);
        if (newMap.isEmpty()) {
            this.attributes = null;
        } else {
            this.attributes = newMap;
        }
    }

    /**
     * Returns the default value of the attributes<br> 
     * @see #getAttributes 
     * 
     * @return The default attributes
     * 
     */
    public Map<String, String> defaultAttributes() {
        return new LinkedHashMap<String, String>();
    }

    /**
     * The ID of the program. (required) 
     * 
     * @param program The program to set
     * @throws NullPointerException If the given value is <code>null</code>
     * 
     */
    public void setProgram(String program) {
        if (program == null) {
            throw new NullPointerException((("Invalid value for program: "+ program)+", may not be null"));
        }
        this.program = program;
    }

    /**
     * The ID of the program. (required) 
     * 
     * @return The program
     * 
     */
    public String getProgram() {
        return this.program;
    }

    /**
     * A dictionary object of strings that maps GLSL uniform names to 
     * technique parameter IDs. (optional)<br> 
     * Default: {} 
     * 
     * @param uniforms The uniforms to set
     * 
     */
    public void setUniforms(Map<String, String> uniforms) {
        if (uniforms == null) {
            this.uniforms = uniforms;
            return ;
        }
        this.uniforms = uniforms;
    }

    /**
     * A dictionary object of strings that maps GLSL uniform names to 
     * technique parameter IDs. (optional)<br> 
     * Default: {} 
     * 
     * @return The uniforms
     * 
     */
    public Map<String, String> getUniforms() {
        return this.uniforms;
    }

    /**
     * Add the given uniforms. The uniforms of this instance will be replaced 
     * with a map that contains all previous mappings, and additionally the 
     * new mapping. 
     * 
     * @param key The key
     * @param value The value
     * @throws NullPointerException If the given key or value is <code>null</code>
     * 
     */
    public void addUniforms(String key, String value) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        if (value == null) {
            throw new NullPointerException("The value may not be null");
        }
        Map<String, String> oldMap = this.uniforms;
        Map<String, String> newMap = new LinkedHashMap<String, String>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.put(key, value);
        this.uniforms = newMap;
    }

    /**
     * Remove the given uniforms. The uniforms of this instance will be 
     * replaced with a map that contains all previous mappings, except for 
     * the one with the given key.<br> 
     * If this new map would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param key The key
     * @throws NullPointerException If the given key is <code>null</code>
     * 
     */
    public void removeUniforms(String key) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        Map<String, String> oldMap = this.uniforms;
        Map<String, String> newMap = new LinkedHashMap<String, String>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.remove(key);
        if (newMap.isEmpty()) {
            this.uniforms = null;
        } else {
            this.uniforms = newMap;
        }
    }

    /**
     * Returns the default value of the uniforms<br> 
     * @see #getUniforms 
     * 
     * @return The default uniforms
     * 
     */
    public Map<String, String> defaultUniforms() {
        return new LinkedHashMap<String, String>();
    }

    /**
     * Fixed-function rendering states. (optional)<br> 
     * Default: {} 
     * 
     * @param states The states to set
     * 
     */
    public void setStates(TechniqueStates states) {
        if (states == null) {
            this.states = states;
            return ;
        }
        this.states = states;
    }

    /**
     * Fixed-function rendering states. (optional)<br> 
     * Default: {} 
     * 
     * @return The states
     * 
     */
    public TechniqueStates getStates() {
        return this.states;
    }

    /**
     * Returns the default value of the states<br> 
     * @see #getStates 
     * 
     * @return The default states
     * 
     */
    public TechniqueStates defaultStates() {
        return new TechniqueStates();
    }

}
