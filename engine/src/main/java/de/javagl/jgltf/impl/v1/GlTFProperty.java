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
 * Auto-generated for glTFProperty.schema.json 
 * 
 */
public class GlTFProperty {

    /**
     * Dictionary object with extension-specific objects. (optional) 
     * 
     */
    private Map<String, Object> extensions;
    /**
     * Application-specific data. (optional) 
     * 
     */
    private Object extras;

    /**
     * Dictionary object with extension-specific objects. (optional) 
     * 
     * @param extensions The extensions to set
     * 
     */
    public void setExtensions(Map<String, Object> extensions) {
        if (extensions == null) {
            this.extensions = extensions;
            return ;
        }
        this.extensions = extensions;
    }

    /**
     * Dictionary object with extension-specific objects. (optional) 
     * 
     * @return The extensions
     * 
     */
    public Map<String, Object> getExtensions() {
        return this.extensions;
    }

    /**
     * Add the given extensions. The extensions of this instance will be 
     * replaced with a map that contains all previous mappings, and 
     * additionally the new mapping. 
     * 
     * @param key The key
     * @param value The value
     * @throws NullPointerException If the given key or value is <code>null</code>
     * 
     */
    public void addExtensions(String key, Object value) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        if (value == null) {
            throw new NullPointerException("The value may not be null");
        }
        Map<String, Object> oldMap = this.extensions;
        Map<String, Object> newMap = new LinkedHashMap<String, Object>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.put(key, value);
        this.extensions = newMap;
    }

    /**
     * Remove the given extensions. The extensions of this instance will be 
     * replaced with a map that contains all previous mappings, except for 
     * the one with the given key.<br> 
     * If this new map would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param key The key
     * @throws NullPointerException If the given key is <code>null</code>
     * 
     */
    public void removeExtensions(String key) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        Map<String, Object> oldMap = this.extensions;
        Map<String, Object> newMap = new LinkedHashMap<String, Object>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.remove(key);
        if (newMap.isEmpty()) {
            this.extensions = null;
        } else {
            this.extensions = newMap;
        }
    }

    /**
     * Application-specific data. (optional) 
     * 
     * @param extras The extras to set
     * 
     */
    public void setExtras(Object extras) {
        if (extras == null) {
            this.extras = extras;
            return ;
        }
        this.extras = extras;
    }

    /**
     * Application-specific data. (optional) 
     * 
     * @return The extras
     * 
     */
    public Object getExtras() {
        return this.extras;
    }

}
