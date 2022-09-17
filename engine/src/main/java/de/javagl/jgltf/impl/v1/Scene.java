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
 * The root nodes of a scene. 
 * 
 * Auto-generated for scene.schema.json 
 * 
 */
public class Scene
    extends GlTFChildOfRootProperty
{

    /**
     * The IDs of each root node. (optional)<br> 
     * Default: []<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     */
    private List<String> nodes;

    /**
     * The IDs of each root node. (optional)<br> 
     * Default: []<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @param nodes The nodes to set
     * 
     */
    public void setNodes(List<String> nodes) {
        if (nodes == null) {
            this.nodes = nodes;
            return ;
        }
        this.nodes = nodes;
    }

    /**
     * The IDs of each root node. (optional)<br> 
     * Default: []<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @return The nodes
     * 
     */
    public List<String> getNodes() {
        return this.nodes;
    }

    /**
     * Add the given nodes. The nodes of this instance will be replaced with 
     * a list that contains all previous elements, and additionally the new 
     * element. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void addNodes(String element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<String> oldList = this.nodes;
        List<String> newList = new ArrayList<String>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.add(element);
        this.nodes = newList;
    }

    /**
     * Remove the given nodes. The nodes of this instance will be replaced 
     * with a list that contains all previous elements, except for the 
     * removed one.<br> 
     * If this new list would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void removeNodes(String element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<String> oldList = this.nodes;
        List<String> newList = new ArrayList<String>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.remove(element);
        if (newList.isEmpty()) {
            this.nodes = null;
        } else {
            this.nodes = newList;
        }
    }

    /**
     * Returns the default value of the nodes<br> 
     * @see #getNodes 
     * 
     * @return The default nodes
     * 
     */
    public List<String> defaultNodes() {
        return new ArrayList<String>();
    }

}
