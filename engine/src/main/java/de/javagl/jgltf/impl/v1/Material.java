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
 * The material appearance of a primitive. 
 * 
 * Auto-generated for material.schema.json 
 * 
 */
public class Material
    extends GlTFChildOfRootProperty
{

    /**
     * The ID of the technique. (optional) 
     * 
     */
    private String technique;
    /**
     * A dictionary object of parameter values. (optional)<br> 
     * Default: {} 
     * 
     */
    private Map<String, Object> values;

    /**
     * The ID of the technique. (optional) 
     * 
     * @param technique The technique to set
     * 
     */
    public void setTechnique(String technique) {
        if (technique == null) {
            this.technique = technique;
            return ;
        }
        this.technique = technique;
    }

    /**
     * The ID of the technique. (optional) 
     * 
     * @return The technique
     * 
     */
    public String getTechnique() {
        return this.technique;
    }

    /**
     * A dictionary object of parameter values. (optional)<br> 
     * Default: {} 
     * 
     * @param values The values to set
     * 
     */
    public void setValues(Map<String, Object> values) {
        if (values == null) {
            this.values = values;
            return ;
        }
        this.values = values;
    }

    /**
     * A dictionary object of parameter values. (optional)<br> 
     * Default: {} 
     * 
     * @return The values
     * 
     */
    public Map<String, Object> getValues() {
        return this.values;
    }

    /**
     * Add the given values. The values of this instance will be replaced 
     * with a map that contains all previous mappings, and additionally the 
     * new mapping. 
     * 
     * @param key The key
     * @param value The value
     * @throws NullPointerException If the given key or value is <code>null</code>
     * 
     */
    public void addValues(String key, Object value) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        if (value == null) {
            throw new NullPointerException("The value may not be null");
        }
        Map<String, Object> oldMap = this.values;
        Map<String, Object> newMap = new LinkedHashMap<String, Object>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.put(key, value);
        this.values = newMap;
    }

    /**
     * Remove the given values. The values of this instance will be replaced 
     * with a map that contains all previous mappings, except for the one 
     * with the given key.<br> 
     * If this new map would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param key The key
     * @throws NullPointerException If the given key is <code>null</code>
     * 
     */
    public void removeValues(String key) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        Map<String, Object> oldMap = this.values;
        Map<String, Object> newMap = new LinkedHashMap<String, Object>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.remove(key);
        if (newMap.isEmpty()) {
            this.values = null;
        } else {
            this.values = newMap;
        }
    }

    /**
     * Returns the default value of the values<br> 
     * @see #getValues 
     * 
     * @return The default values
     * 
     */
    public Map<String, Object> defaultValues() {
        return new LinkedHashMap<String, Object>();
    }

}
