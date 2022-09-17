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
 * Geometry to be rendered with the given material. 
 * 
 * Auto-generated for mesh.primitive.schema.json 
 * 
 */
public class MeshPrimitive
    extends GlTFProperty
{

    /**
     * A dictionary object of strings, where each string is the ID of the 
     * accessor containing an attribute. (optional)<br> 
     * Default: {} 
     * 
     */
    private Map<String, String> attributes;
    /**
     * The ID of the accessor that contains the indices. (optional) 
     * 
     */
    private String indices;
    /**
     * The ID of the material to apply to this primitive when rendering. 
     * (required) 
     * 
     */
    private String material;
    /**
     * The type of primitives to render. (optional)<br> 
     * Default: 4<br> 
     * Valid values: [0, 1, 2, 3, 4, 5, 6] 
     * 
     */
    private Integer mode;

    /**
     * A dictionary object of strings, where each string is the ID of the 
     * accessor containing an attribute. (optional)<br> 
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
     * A dictionary object of strings, where each string is the ID of the 
     * accessor containing an attribute. (optional)<br> 
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
     * The ID of the accessor that contains the indices. (optional) 
     * 
     * @param indices The indices to set
     * 
     */
    public void setIndices(String indices) {
        if (indices == null) {
            this.indices = indices;
            return ;
        }
        this.indices = indices;
    }

    /**
     * The ID of the accessor that contains the indices. (optional) 
     * 
     * @return The indices
     * 
     */
    public String getIndices() {
        return this.indices;
    }

    /**
     * The ID of the material to apply to this primitive when rendering. 
     * (required) 
     * 
     * @param material The material to set
     * @throws NullPointerException If the given value is <code>null</code>
     * 
     */
    public void setMaterial(String material) {
        if (material == null) {
            throw new NullPointerException((("Invalid value for material: "+ material)+", may not be null"));
        }
        this.material = material;
    }

    /**
     * The ID of the material to apply to this primitive when rendering. 
     * (required) 
     * 
     * @return The material
     * 
     */
    public String getMaterial() {
        return this.material;
    }

    /**
     * The type of primitives to render. (optional)<br> 
     * Default: 4<br> 
     * Valid values: [0, 1, 2, 3, 4, 5, 6] 
     * 
     * @param mode The mode to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setMode(Integer mode) {
        if (mode == null) {
            this.mode = mode;
            return ;
        }
        if (((((((mode!= 0)&&(mode!= 1))&&(mode!= 2))&&(mode!= 3))&&(mode!= 4))&&(mode!= 5))&&(mode!= 6)) {
            throw new IllegalArgumentException((("Invalid value for mode: "+ mode)+", valid: [0, 1, 2, 3, 4, 5, 6]"));
        }
        this.mode = mode;
    }

    /**
     * The type of primitives to render. (optional)<br> 
     * Default: 4<br> 
     * Valid values: [0, 1, 2, 3, 4, 5, 6] 
     * 
     * @return The mode
     * 
     */
    public Integer getMode() {
        return this.mode;
    }

    /**
     * Returns the default value of the mode<br> 
     * @see #getMode 
     * 
     * @return The default mode
     * 
     */
    public Integer defaultMode() {
        return  4;
    }

}
