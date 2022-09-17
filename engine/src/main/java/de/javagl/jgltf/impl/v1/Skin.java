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
 * Joints and matrices defining a skin. 
 * 
 * Auto-generated for skin.schema.json 
 * 
 */
public class Skin
    extends GlTFChildOfRootProperty
{

    /**
     * Floating-point 4x4 transformation matrix stored in column-major order. 
     * (optional)<br> 
     * Default: 
     * [1.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,1.0]<br> 
     * Number of items: 16<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     */
    private float[] bindShapeMatrix;
    /**
     * The ID of the accessor containing the floating-point 4x4 inverse-bind 
     * matrices. (required) 
     * 
     */
    private String inverseBindMatrices;
    /**
     * Joint names of the joints (nodes with a `jointName` property) in this 
     * skin. (required)<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     */
    private List<String> jointNames;

    /**
     * Floating-point 4x4 transformation matrix stored in column-major order. 
     * (optional)<br> 
     * Default: 
     * [1.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,1.0]<br> 
     * Number of items: 16<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @param bindShapeMatrix The bindShapeMatrix to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setBindShapeMatrix(float[] bindShapeMatrix) {
        if (bindShapeMatrix == null) {
            this.bindShapeMatrix = bindShapeMatrix;
            return ;
        }
        if (bindShapeMatrix.length< 16) {
            throw new IllegalArgumentException("Number of bindShapeMatrix elements is < 16");
        }
        if (bindShapeMatrix.length > 16) {
            throw new IllegalArgumentException("Number of bindShapeMatrix elements is > 16");
        }
        this.bindShapeMatrix = bindShapeMatrix;
    }

    /**
     * Floating-point 4x4 transformation matrix stored in column-major order. 
     * (optional)<br> 
     * Default: 
     * [1.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,1.0]<br> 
     * Number of items: 16<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @return The bindShapeMatrix
     * 
     */
    public float[] getBindShapeMatrix() {
        return this.bindShapeMatrix;
    }

    /**
     * Returns the default value of the bindShapeMatrix<br> 
     * @see #getBindShapeMatrix 
     * 
     * @return The default bindShapeMatrix
     * 
     */
    public float[] defaultBindShapeMatrix() {
        return new float[] { 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F };
    }

    /**
     * The ID of the accessor containing the floating-point 4x4 inverse-bind 
     * matrices. (required) 
     * 
     * @param inverseBindMatrices The inverseBindMatrices to set
     * @throws NullPointerException If the given value is <code>null</code>
     * 
     */
    public void setInverseBindMatrices(String inverseBindMatrices) {
        if (inverseBindMatrices == null) {
            throw new NullPointerException((("Invalid value for inverseBindMatrices: "+ inverseBindMatrices)+", may not be null"));
        }
        this.inverseBindMatrices = inverseBindMatrices;
    }

    /**
     * The ID of the accessor containing the floating-point 4x4 inverse-bind 
     * matrices. (required) 
     * 
     * @return The inverseBindMatrices
     * 
     */
    public String getInverseBindMatrices() {
        return this.inverseBindMatrices;
    }

    /**
     * Joint names of the joints (nodes with a `jointName` property) in this 
     * skin. (required)<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @param jointNames The jointNames to set
     * @throws NullPointerException If the given value is <code>null</code>
     * 
     */
    public void setJointNames(List<String> jointNames) {
        if (jointNames == null) {
            throw new NullPointerException((("Invalid value for jointNames: "+ jointNames)+", may not be null"));
        }
        this.jointNames = jointNames;
    }

    /**
     * Joint names of the joints (nodes with a `jointName` property) in this 
     * skin. (required)<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @return The jointNames
     * 
     */
    public List<String> getJointNames() {
        return this.jointNames;
    }

    /**
     * Add the given jointNames. The jointNames of this instance will be 
     * replaced with a list that contains all previous elements, and 
     * additionally the new element. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void addJointNames(String element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<String> oldList = this.jointNames;
        List<String> newList = new ArrayList<String>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.add(element);
        this.jointNames = newList;
    }

    /**
     * Remove the given jointNames. The jointNames of this instance will be 
     * replaced with a list that contains all previous elements, except for 
     * the removed one. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void removeJointNames(String element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<String> oldList = this.jointNames;
        List<String> newList = new ArrayList<String>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.remove(element);
        this.jointNames = newList;
    }

}
