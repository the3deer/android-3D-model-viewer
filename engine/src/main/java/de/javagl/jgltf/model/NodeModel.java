/*
 * www.javagl.de - JglTF
 *
 * Copyright 2015-2017 Marco Hutter - http://www.javagl.de
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package de.javagl.jgltf.model;

import java.util.List;
import java.util.function.Supplier;

/**
 * Interface for a node that is part of a scene hierarchy
 */
public interface NodeModel extends NamedModelElement
{
    /**
     * Returns the parent of this node, or <code>null</code> if this is
     * a root node
     * 
     * @return The parent
     */
    NodeModel getParent();

    /**
     * Returns an unmodifiable view on the list of children of this node
     * 
     * @return The children
     */
    List<NodeModel> getChildren();
    
    /**
     * Returns an unmodifiable view on the list of {@link MeshModel} instances
     * that are attached to this node.
     * 
     * @return The {@link MeshModel} list
     */
    List<MeshModel> getMeshModels();

    /**
     * Returns the {@link SkinModel} for this node, or <code>null</code> if
     * this node is not associated with a skin
     *  
     * @return The {@link SkinModel}
     */
    SkinModel getSkinModel();
    
    /**
     * Returns the {@link CameraModel} for this node, or <code>null</code> if
     * this node is not associated with a camera
     *  
     * @return The {@link CameraModel}
     */
    CameraModel getCameraModel();
    
    /**
     * Set the matrix of this node to be a <b>reference</b> to the given
     * array. <br>
     * <br>
     * The matrix is assumed to be a 16-element array containing the matrix 
     * in column-major order. If the given matrix is <code>null</code>, then 
     * the {@link #getTranslation() translation},
     * {@link #getRotation() rotation}, and
     * {@link #getScale() scale} properties will be used for determining the 
     * local transform.
     * 
     * @param matrix The matrix
     * @throws IllegalArgumentException If the given array does not have
     * a length of 16
     */
    void setMatrix(float matrix[]);
    
    /**
     * Returns a <b>reference</b> to the array storing the matrix of this node.
     * This is a 16-element array containing the matrix in column-major order, 
     * or <code>null</code> if no matrix was set.
     * 
     * @return The matrix
     */
    float[] getMatrix();

    /**
     * Set the translation of this node to be a <b>reference</b> to the given
     * array. 
     * 
     * @param translation The translation
     * @throws IllegalArgumentException If the given array does not have
     * a length of 3
     */
    void setTranslation(float translation[]);
    
    /**
     * Returns a <b>reference</b> to the array storing the translation of this 
     * node, or <code>null</code> if no translation was set.
     * 
     * @return The translation
     */
    float[] getTranslation();

    /**
     * Set the rotation of this node to be a <b>reference</b> to the given
     * array. The array is assumed to be a quaternion, consisting of 4
     * float elements.
     * 
     * @param rotation The rotation
     * @throws IllegalArgumentException If the given array does not have
     * a length of 4
     */
    void setRotation(float rotation[]);
    
    /**
     * Returns a <b>reference</b> to the array storing the rotation of this 
     * node, or <code>null</code> if no rotation was set
     * 
     * @return The rotation
     */
    float[] getRotation();

    /**
     * Set the scale of this node to be a <b>reference</b> to the given
     * array.
     * 
     * @param scale The scale
     * @throws IllegalArgumentException If the given array does not have
     * a length of 3
     */
    void setScale(float scale[]);

    /**
     * Returns a <b>reference</b> to the array storing the scale of this 
     * node, or <code>null</code> if no scale was set
     * 
     * @return The scale
     */
    float[] getScale();
    
    /**
     * Set the morph target weights to be a <b>reference</b> to the given
     * array. 
     * 
     * @param weights The weights
     */
    void setWeights(float weights[]);
    
    /**
     * Returns a <b>reference</b> to the morph target weights, 
     * or <code>null</code> if no morph target weights have been defined
     * 
     * @return The morph target weights
     */
    float[] getWeights();
    
    /**
     * Computes the local transform of this node.<br>
     * <br>
     * The result will be written to the given array, as a 4x4 matrix in 
     * column major order. If the given array is <code>null</code> or does
     * not have a length of 16, then a new array with length 16 will be 
     * created and returned. 
     * 
     * @param result The result array
     * @return The result array
     */
    float[] computeLocalTransform(float result[]);

    /**
     * Computes the global transform of this node.<br>
     * <br>
     * The result will be written to the given array, as a 4x4 matrix in 
     * column major order. If the given array is <code>null</code> or does
     * not have a length of 16, then a new array with length 16 will be 
     * created and returned. 
     * 
     * @param result The result array
     * @return The result array
     */
    float[] computeGlobalTransform(float result[]);

    /**
     * Creates a supplier for the global transform matrix of this node 
     * model.<br>
     * <br> 
     * The matrix will be provided as a float array with 16 elements, 
     * storing the matrix entries in column-major order.<br>
     * <br>
     * Note: The supplier MAY always return the same array instance.
     * Callers MUST NOT store or modify the returned array. 
     * 
     * @return The supplier
     */
    Supplier<float[]> createGlobalTransformSupplier();

    /**
     * Creates a supplier for the local transform matrix of this node model.<br>
     * <br> 
     * The matrix will be provided as a float array with 16 elements, 
     * storing the matrix entries in column-major order.<br>
     * <br>
     * Note: The supplier MAY always return the same array instance.
     * Callers MUST NOT store or modify the returned array. 
     * 
     * @return The supplier
     */
    Supplier<float[]> createLocalTransformSupplier();

}