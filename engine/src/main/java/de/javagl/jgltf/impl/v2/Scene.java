/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016-2021 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v2;

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
     * The indices of each root node. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Minimum: 0 (inclusive) 
     * 
     */
    private List<Integer> nodes;

    /**
     * The indices of each root node. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Minimum: 0 (inclusive) 
     * 
     * @param nodes The nodes to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setNodes(List<Integer> nodes) {
        if (nodes == null) {
            this.nodes = nodes;
            return ;
        }
        if (nodes.size()< 1) {
            throw new IllegalArgumentException("Number of nodes elements is < 1");
        }
        for (Integer nodesElement: nodes) {
            if (nodesElement< 0) {
                throw new IllegalArgumentException("nodesElement < 0");
            }
        }
        this.nodes = nodes;
    }

    /**
     * The indices of each root node. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional)<br> 
     * &nbsp;&nbsp;Minimum: 0 (inclusive) 
     * 
     * @return The nodes
     * 
     */
    public List<Integer> getNodes() {
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
    public void addNodes(Integer element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Integer> oldList = this.nodes;
        List<Integer> newList = new ArrayList<Integer>();
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
    public void removeNodes(Integer element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Integer> oldList = this.nodes;
        List<Integer> newList = new ArrayList<Integer>();
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

}
