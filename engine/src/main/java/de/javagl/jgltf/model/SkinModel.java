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

/**
 * Interface for a skin of a glTF asset
 */
public interface SkinModel extends NamedModelElement
{
    /**
     * Provides the bind shape matrix of the skin.<br>
     * <br>
     * The result will be written to the given array, as a 4x4 matrix in 
     * column major order. If the given array is <code>null</code> or does
     * not have a length of 16, then a new array with length 16 will be 
     * created and returned. 
     * 
     * @param result The result array
     * @return The result array
     */
    float[] getBindShapeMatrix(float result[]);
    
    /**
     * Returns an unmodifiable list containing the joint nodes of the skeleton
     * 
     * @return The joint nodes
     */
    List<NodeModel> getJoints();
    
    /**
     * Returns the skeleton root node. If the return value is <code>null</code>,
     * then joint transforms refer to the scene root.  
     * 
     * @return The skeleton node
     */
    NodeModel getSkeleton();
    
    /**
     * Returns the {@link AccessorModel} that provides the data for the 
     * inverse bind matrices, one for each {@link #getJoints() joint}
     * 
     * @return The inverse bind matrices accessor
     */
    AccessorModel getInverseBindMatrices();
    
    /**
     * Convenience function to obtain the inverse bind matrix for the joint
     * with the given index.<br>
     * <br>
     * The result will be written to the given array, as a 4x4 matrix in 
     * column major order. If the given array is <code>null</code> or does
     * not have a length of 16, then a new array with length 16 will be 
     * created and returned. 
     *  
     * @param index The index of the joint
     * @param result The result array
     * @return The result array
     */
    float[] getInverseBindMatrix(int index, float result[]);
}
