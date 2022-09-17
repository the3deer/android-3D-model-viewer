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
package de.javagl.jgltf.model.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.javagl.jgltf.model.AccessorDatas;
import de.javagl.jgltf.model.AccessorFloatData;
import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.MathUtils;
import de.javagl.jgltf.model.NodeModel;
import de.javagl.jgltf.model.SkinModel;
import de.javagl.jgltf.model.Utils;

/**
 * Implementation of a {@link SkinModel}
 */
public final class DefaultSkinModel extends AbstractNamedModelElement
    implements SkinModel
{
    /**
     * The bind shape matrix
     */
    private float bindShapeMatrix[];
    
    /**
     * The joint nodes
     */
    private final List<NodeModel> joints;
    
    /**
     * The skeleton node
     */
    private NodeModel skeleton;
    
    /**
     * The inverse bind matrices
     */
    private AccessorModel inverseBindMatrices;
    
    /**
     * Creates a new instance
     */
    public DefaultSkinModel()
    {
        this.bindShapeMatrix = MathUtils.createIdentity4x4();
        this.joints = new ArrayList<NodeModel>();
    }
    
    /**
     * Set the bind shape matrix
     * 
     * @param bindShapeMatrix The bind shape matrix. A copy of this array
     * will be stored. If it is <code>null</code>, a new array will be 
     * created, which represents the identity matrix.
     */
    public void setBindShapeMatrix(float[] bindShapeMatrix)
    {
        if (bindShapeMatrix == null)
        {
            this.bindShapeMatrix = MathUtils.createIdentity4x4();
        }
        else
        {
            this.bindShapeMatrix = bindShapeMatrix.clone();
        }
    }
    
    /**
     * Add the given joint 
     * 
     * @param joint The joint
     */
    public void addJoint(NodeModel joint)
    {
        Objects.requireNonNull(joint, "The joint may not be null");
        joints.add(joint);
    }
    
    /**
     * Set the skeleton root node
     * 
     * @param skeleton The skeleton root node
     */
    public void setSkeleton(NodeModel skeleton)
    {
        this.skeleton = skeleton;
    }
    
    /**
     * Set the inverse bind matrices
     * 
     * @param inverseBindMatrices The inverse bind matrices
     */
    public void setInverseBindMatrices(AccessorModel inverseBindMatrices)
    {
        this.inverseBindMatrices = Objects.requireNonNull(
            inverseBindMatrices, "The inverseBindMatrices may not be null");
    }
    

    @Override
    public float[] getBindShapeMatrix(float[] result)
    {
        float localResult[] = Utils.validate(result, 16);
        System.arraycopy(bindShapeMatrix, 0, localResult, 0, 16);
        return localResult;
    }
    

    @Override
    public List<NodeModel> getJoints()
    {
        return Collections.unmodifiableList(joints);
    }

    @Override
    public NodeModel getSkeleton()
    {
        return skeleton;
    }

    @Override
    public AccessorModel getInverseBindMatrices()
    {
        return inverseBindMatrices;
    }

    @Override
    public float[] getInverseBindMatrix(int index, float[] result)
    {
        float localResult[] = Utils.validate(result, 16);
        AccessorFloatData inverseBindMatricesData = 
            AccessorDatas.createFloat(inverseBindMatrices);
        for (int j = 0; j < 16; j++)
        {
            localResult[j] = inverseBindMatricesData.get(index, j);
        }
        return localResult;
    }

}
