/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016-2021 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v2;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
     * A plain JSON object, where each key corresponds to a mesh attribute 
     * semantic and each value is the index of the accessor containing 
     * attribute's data. (required) 
     * 
     */
    private Map<String, Integer> attributes;
    /**
     * The index of the accessor that contains the vertex indices. (optional) 
     * 
     */
    private Integer indices;
    /**
     * The index of the material to apply to this primitive when rendering. 
     * (optional) 
     * 
     */
    private Integer material;
    /**
     * The topology type of primitives to render. (optional)<br> 
     * Default: 4<br> 
     * Valid values: [0, 1, 2, 3, 4, 5, 6] 
     * 
     */
    private Integer mode;
    /**
     * An array of morph targets. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;A plain JSON object specifying attributes displacements in 
     * a morph target, where each key corresponds to one of the three 
     * supported attribute semantic (`POSITION`, `NORMAL`, or `TANGENT`) and 
     * each value is the index of the accessor containing the attribute 
     * displacements' data. (optional) 
     * 
     */
    private List<Map<String, Integer>> targets;

    /**
     * A plain JSON object, where each key corresponds to a mesh attribute 
     * semantic and each value is the index of the accessor containing 
     * attribute's data. (required) 
     * 
     * @param attributes The attributes to set
     * @throws NullPointerException If the given value is <code>null</code>
     * 
     */
    public void setAttributes(Map<String, Integer> attributes) {
        if (attributes == null) {
            throw new NullPointerException((("Invalid value for attributes: "+ attributes)+", may not be null"));
        }
        this.attributes = attributes;
    }

    /**
     * A plain JSON object, where each key corresponds to a mesh attribute 
     * semantic and each value is the index of the accessor containing 
     * attribute's data. (required) 
     * 
     * @return The attributes
     * 
     */
    public Map<String, Integer> getAttributes() {
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
    public void addAttributes(String key, Integer value) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        if (value == null) {
            throw new NullPointerException("The value may not be null");
        }
        Map<String, Integer> oldMap = this.attributes;
        Map<String, Integer> newMap = new LinkedHashMap<String, Integer>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.put(key, value);
        this.attributes = newMap;
    }

    /**
     * Remove the given attributes. The attributes of this instance will be 
     * replaced with a map that contains all previous mappings, except for 
     * the one with the given key. 
     * 
     * @param key The key
     * @throws NullPointerException If the given key is <code>null</code>
     * 
     */
    public void removeAttributes(String key) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        Map<String, Integer> oldMap = this.attributes;
        Map<String, Integer> newMap = new LinkedHashMap<String, Integer>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.remove(key);
        this.attributes = newMap;
    }

    /**
     * The index of the accessor that contains the vertex indices. (optional) 
     * 
     * @param indices The indices to set
     * 
     */
    public void setIndices(Integer indices) {
        if (indices == null) {
            this.indices = indices;
            return ;
        }
        this.indices = indices;
    }

    /**
     * The index of the accessor that contains the vertex indices. (optional) 
     * 
     * @return The indices
     * 
     */
    public Integer getIndices() {
        return this.indices;
    }

    /**
     * The index of the material to apply to this primitive when rendering. 
     * (optional) 
     * 
     * @param material The material to set
     * 
     */
    public void setMaterial(Integer material) {
        if (material == null) {
            this.material = material;
            return ;
        }
        this.material = material;
    }

    /**
     * The index of the material to apply to this primitive when rendering. 
     * (optional) 
     * 
     * @return The material
     * 
     */
    public Integer getMaterial() {
        return this.material;
    }

    /**
     * The topology type of primitives to render. (optional)<br> 
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
     * The topology type of primitives to render. (optional)<br> 
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

    /**
     * An array of morph targets. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;A plain JSON object specifying attributes displacements in 
     * a morph target, where each key corresponds to one of the three 
     * supported attribute semantic (`POSITION`, `NORMAL`, or `TANGENT`) and 
     * each value is the index of the accessor containing the attribute 
     * displacements' data. (optional) 
     * 
     * @param targets The targets to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setTargets(List<Map<String, Integer>> targets) {
        if (targets == null) {
            this.targets = targets;
            return ;
        }
        if (targets.size()< 1) {
            throw new IllegalArgumentException("Number of targets elements is < 1");
        }
        this.targets = targets;
    }

    /**
     * An array of morph targets. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;A plain JSON object specifying attributes displacements in 
     * a morph target, where each key corresponds to one of the three 
     * supported attribute semantic (`POSITION`, `NORMAL`, or `TANGENT`) and 
     * each value is the index of the accessor containing the attribute 
     * displacements' data. (optional) 
     * 
     * @return The targets
     * 
     */
    public List<Map<String, Integer>> getTargets() {
        return this.targets;
    }

    /**
     * Add the given targets. The targets of this instance will be replaced 
     * with a list that contains all previous elements, and additionally the 
     * new element. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void addTargets(Map<String, Integer> element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Map<String, Integer>> oldList = this.targets;
        List<Map<String, Integer>> newList = new ArrayList<Map<String, Integer>>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.add(element);
        this.targets = newList;
    }

    /**
     * Remove the given targets. The targets of this instance will be 
     * replaced with a list that contains all previous elements, except for 
     * the removed one.<br> 
     * If this new list would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void removeTargets(Map<String, Integer> element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Map<String, Integer>> oldList = this.targets;
        List<Map<String, Integer>> newList = new ArrayList<Map<String, Integer>>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.remove(element);
        if (newList.isEmpty()) {
            this.targets = null;
        } else {
            this.targets = newList;
        }
    }

}
