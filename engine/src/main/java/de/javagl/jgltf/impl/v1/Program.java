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
 * A shader program, including its vertex and fragment shader, and names 
 * of vertex shader attributes. 
 * 
 * Auto-generated for program.schema.json 
 * 
 */
public class Program
    extends GlTFChildOfRootProperty
{

    /**
     * Names of GLSL vertex shader attributes. (optional)<br> 
     * Default: []<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     */
    private List<String> attributes;
    /**
     * The ID of the fragment shader. (required) 
     * 
     */
    private String fragmentShader;
    /**
     * The ID of the vertex shader. (required) 
     * 
     */
    private String vertexShader;

    /**
     * Names of GLSL vertex shader attributes. (optional)<br> 
     * Default: []<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @param attributes The attributes to set
     * 
     */
    public void setAttributes(List<String> attributes) {
        if (attributes == null) {
            this.attributes = attributes;
            return ;
        }
        this.attributes = attributes;
    }

    /**
     * Names of GLSL vertex shader attributes. (optional)<br> 
     * Default: []<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @return The attributes
     * 
     */
    public List<String> getAttributes() {
        return this.attributes;
    }

    /**
     * Add the given attributes. The attributes of this instance will be 
     * replaced with a list that contains all previous elements, and 
     * additionally the new element. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void addAttributes(String element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<String> oldList = this.attributes;
        List<String> newList = new ArrayList<String>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.add(element);
        this.attributes = newList;
    }

    /**
     * Remove the given attributes. The attributes of this instance will be 
     * replaced with a list that contains all previous elements, except for 
     * the removed one.<br> 
     * If this new list would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void removeAttributes(String element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<String> oldList = this.attributes;
        List<String> newList = new ArrayList<String>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.remove(element);
        if (newList.isEmpty()) {
            this.attributes = null;
        } else {
            this.attributes = newList;
        }
    }

    /**
     * Returns the default value of the attributes<br> 
     * @see #getAttributes 
     * 
     * @return The default attributes
     * 
     */
    public List<String> defaultAttributes() {
        return new ArrayList<String>();
    }

    /**
     * The ID of the fragment shader. (required) 
     * 
     * @param fragmentShader The fragmentShader to set
     * @throws NullPointerException If the given value is <code>null</code>
     * 
     */
    public void setFragmentShader(String fragmentShader) {
        if (fragmentShader == null) {
            throw new NullPointerException((("Invalid value for fragmentShader: "+ fragmentShader)+", may not be null"));
        }
        this.fragmentShader = fragmentShader;
    }

    /**
     * The ID of the fragment shader. (required) 
     * 
     * @return The fragmentShader
     * 
     */
    public String getFragmentShader() {
        return this.fragmentShader;
    }

    /**
     * The ID of the vertex shader. (required) 
     * 
     * @param vertexShader The vertexShader to set
     * @throws NullPointerException If the given value is <code>null</code>
     * 
     */
    public void setVertexShader(String vertexShader) {
        if (vertexShader == null) {
            throw new NullPointerException((("Invalid value for vertexShader: "+ vertexShader)+", may not be null"));
        }
        this.vertexShader = vertexShader;
    }

    /**
     * The ID of the vertex shader. (required) 
     * 
     * @return The vertexShader
     * 
     */
    public String getVertexShader() {
        return this.vertexShader;
    }

}
