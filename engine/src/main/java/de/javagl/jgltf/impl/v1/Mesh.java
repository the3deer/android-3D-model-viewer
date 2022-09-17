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
 * A set of primitives to be rendered. A node can contain one or more 
 * meshes. A node's transform places the mesh in the scene. 
 * 
 * Auto-generated for mesh.schema.json 
 * 
 */
public class Mesh
    extends GlTFChildOfRootProperty
{

    /**
     * An array of primitives, each defining geometry to be rendered with a 
     * material. (optional)<br> 
     * Default: []<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;Geometry to be rendered with the given material. 
     * (optional) 
     * 
     */
    private List<MeshPrimitive> primitives;

    /**
     * An array of primitives, each defining geometry to be rendered with a 
     * material. (optional)<br> 
     * Default: []<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;Geometry to be rendered with the given material. 
     * (optional) 
     * 
     * @param primitives The primitives to set
     * 
     */
    public void setPrimitives(List<MeshPrimitive> primitives) {
        if (primitives == null) {
            this.primitives = primitives;
            return ;
        }
        this.primitives = primitives;
    }

    /**
     * An array of primitives, each defining geometry to be rendered with a 
     * material. (optional)<br> 
     * Default: []<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;Geometry to be rendered with the given material. 
     * (optional) 
     * 
     * @return The primitives
     * 
     */
    public List<MeshPrimitive> getPrimitives() {
        return this.primitives;
    }

    /**
     * Add the given primitives. The primitives of this instance will be 
     * replaced with a list that contains all previous elements, and 
     * additionally the new element. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void addPrimitives(MeshPrimitive element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<MeshPrimitive> oldList = this.primitives;
        List<MeshPrimitive> newList = new ArrayList<MeshPrimitive>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.add(element);
        this.primitives = newList;
    }

    /**
     * Remove the given primitives. The primitives of this instance will be 
     * replaced with a list that contains all previous elements, except for 
     * the removed one.<br> 
     * If this new list would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void removePrimitives(MeshPrimitive element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<MeshPrimitive> oldList = this.primitives;
        List<MeshPrimitive> newList = new ArrayList<MeshPrimitive>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.remove(element);
        if (newList.isEmpty()) {
            this.primitives = null;
        } else {
            this.primitives = newList;
        }
    }

    /**
     * Returns the default value of the primitives<br> 
     * @see #getPrimitives 
     * 
     * @return The default primitives
     * 
     */
    public List<MeshPrimitive> defaultPrimitives() {
        return new ArrayList<MeshPrimitive>();
    }

}
