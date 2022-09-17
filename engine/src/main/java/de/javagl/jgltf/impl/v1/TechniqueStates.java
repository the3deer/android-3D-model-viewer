/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v1;

import java.util.ArrayList;
import java.util.List;


/**
 * Fixed-function rendering states. 
 * 
 * Auto-generated for technique.states.schema.json 
 * 
 */
public class TechniqueStates
    extends GlTFProperty
{

    /**
     * WebGL states to enable. (optional)<br> 
     * Default: []<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Valid values: [3042, 2884, 2929, 32823, 32926, 3089] 
     * 
     */
    private List<Integer> enable;
    /**
     * Arguments for fixed-function rendering state functions other than 
     * `enable()`/`disable()`. (optional) 
     * 
     */
    private TechniqueStatesFunctions functions;

    /**
     * WebGL states to enable. (optional)<br> 
     * Default: []<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Valid values: [3042, 2884, 2929, 32823, 32926, 3089] 
     * 
     * @param enable The enable to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setEnable(List<Integer> enable) {
        if (enable == null) {
            this.enable = enable;
            return ;
        }
        for (Integer enableElement: enable) {
            if ((((((enableElement!= 3042)&&(enableElement!= 2884))&&(enableElement!= 2929))&&(enableElement!= 32823))&&(enableElement!= 32926))&&(enableElement!= 3089)) {
                throw new IllegalArgumentException((("Invalid value for enableElement: "+ enableElement)+", valid: [3042, 2884, 2929, 32823, 32926, 3089]"));
            }
        }
        this.enable = enable;
    }

    /**
     * WebGL states to enable. (optional)<br> 
     * Default: []<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Valid values: [3042, 2884, 2929, 32823, 32926, 3089] 
     * 
     * @return The enable
     * 
     */
    public List<Integer> getEnable() {
        return this.enable;
    }

    /**
     * Add the given enable. The enable of this instance will be replaced 
     * with a list that contains all previous elements, and additionally the 
     * new element. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void addEnable(Integer element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Integer> oldList = this.enable;
        List<Integer> newList = new ArrayList<Integer>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.add(element);
        this.enable = newList;
    }

    /**
     * Remove the given enable. The enable of this instance will be replaced 
     * with a list that contains all previous elements, except for the 
     * removed one.<br> 
     * If this new list would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void removeEnable(Integer element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Integer> oldList = this.enable;
        List<Integer> newList = new ArrayList<Integer>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.remove(element);
        if (newList.isEmpty()) {
            this.enable = null;
        } else {
            this.enable = newList;
        }
    }

    /**
     * Returns the default value of the enable<br> 
     * @see #getEnable 
     * 
     * @return The default enable
     * 
     */
    public List<Integer> defaultEnable() {
        return new ArrayList<Integer>();
    }

    /**
     * Arguments for fixed-function rendering state functions other than 
     * `enable()`/`disable()`. (optional) 
     * 
     * @param functions The functions to set
     * 
     */
    public void setFunctions(TechniqueStatesFunctions functions) {
        if (functions == null) {
            this.functions = functions;
            return ;
        }
        this.functions = functions;
    }

    /**
     * Arguments for fixed-function rendering state functions other than 
     * `enable()`/`disable()`. (optional) 
     * 
     * @return The functions
     * 
     */
    public TechniqueStatesFunctions getFunctions() {
        return this.functions;
    }

}
