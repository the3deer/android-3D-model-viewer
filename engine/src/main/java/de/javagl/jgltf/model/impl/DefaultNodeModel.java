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
import java.util.function.Supplier;

import de.javagl.jgltf.model.CameraModel;
import de.javagl.jgltf.model.MathUtils;
import de.javagl.jgltf.model.MeshModel;
import de.javagl.jgltf.model.NodeModel;
import de.javagl.jgltf.model.SkinModel;
import de.javagl.jgltf.model.Suppliers;
import de.javagl.jgltf.model.Utils;

/**
 * Implementation of a {@link NodeModel} 
 */
public class DefaultNodeModel extends AbstractNamedModelElement
    implements NodeModel
{
    /**
     * A thread-local, temporary 16-element matrix
     */
    private static final ThreadLocal<float[]> TEMP_MATRIX_4x4_IN_LOCAL =
        ThreadLocal.withInitial(() -> new float[16]);
    
    /**
     * A thread-local, temporary 16-element matrix
     */
    private static final ThreadLocal<float[]> TEMP_MATRIX_4x4_IN_GLOBAL =
        ThreadLocal.withInitial(() -> new float[16]);
    
    /**
     * The parent of this node. This is <code>null</code> for the root node.
     */
    private NodeModel parent;
    
    /**
     * The children of this node
     */
    private final List<NodeModel> children;
    
    /**
     * The {@link MeshModel} objects that are attached to this node
     */
    private final List<MeshModel> meshModels;

    /**
     * The {@link SkinModel}
     */
    private SkinModel skinModel;
    
    /**
     * The {@link CameraModel}
     */
    private CameraModel cameraModel;

    /**
     * The local transform matrix
     */
    private float matrix[];
    
    /**
     * The translation
     */
    private float translation[];
    
    /**
     * The rotation
     */
    private float rotation[];
    
    /**
     * The scale
     */
    private float scale[];

    /**
     * The weights
     */
    private float weights[];
    
    /**
     * Creates a new instance 
     */
    public DefaultNodeModel()
    {
        this.children = new ArrayList<NodeModel>();
        this.meshModels = new ArrayList<MeshModel>();
    }
    
    /**
     * Copy constructor that creates a shallow copy with <i>references</i>
     * to the elements of the given model, except for the children and
     * {@link MeshModel} instances (which will be empty in the copy).
     * 
     * @param other The other {@link NodeModel}
     */
    public DefaultNodeModel(NodeModel other)
    {
        this.cameraModel = other.getCameraModel();
        this.children = new ArrayList<NodeModel>();
        this.matrix = other.getMatrix();
        this.meshModels = new ArrayList<MeshModel>();
        this.parent = other.getParent();
        this.rotation = other.getRotation();
        this.scale = other.getScale();
        this.skinModel = other.getSkinModel();
        this.translation = other.getTranslation();
        this.weights = other.getWeights();
    }
    
    /**
     * Set the parent of this node
     * 
     * @param parent The parent node
     */
    public void setParent(DefaultNodeModel parent)
    {
        this.parent = parent;
    }
    
    /**
     * Add the given child node
     * 
     * @param child The child node
     */
    public void addChild(DefaultNodeModel child)
    {
        Objects.requireNonNull(child, "The child may not be null");
        children.add(child);
        child.setParent(this);
    }
    
    /**
     * Add the given {@link MeshModel} 
     * 
     * @param meshModel The {@link MeshModel}
     */
    public void addMeshModel(MeshModel meshModel)
    {
        Objects.requireNonNull(meshModel, "The meshModel may not be null");
        meshModels.add(meshModel);
    }
    
    /**
     * Set the {@link SkinModel} 
     * 
     * @param skinModel The {@link SkinModel}
     */
    public void setSkinModel(SkinModel skinModel)
    {
        this.skinModel = skinModel;
    }
    
    /**
     * Set the {@link CameraModel} 
     * 
     * @param cameraModel The {@link CameraModel}
     */
    public void setCameraModel(CameraModel cameraModel)
    {
        this.cameraModel = cameraModel;
    }
    
    @Override
    public NodeModel getParent()
    {
        return parent;
    }
    
    @Override
    public List<NodeModel> getChildren()
    {
        return Collections.unmodifiableList(children);
    }
    
    @Override
    public List<MeshModel> getMeshModels()
    {
        return Collections.unmodifiableList(meshModels);
    }
    
    @Override
    public SkinModel getSkinModel()
    {
        return skinModel;
    }
    
    @Override
    public CameraModel getCameraModel()
    {
        return cameraModel;
    }
    
    @Override
    public void setMatrix(float[] matrix)
    {
        this.matrix = check(matrix, 16);
    }
    
    @Override
    public float[] getMatrix()
    {
        return matrix;
    }

    @Override
    public void setTranslation(float[] translation)
    {
        this.translation = check(translation, 3);
    }

    @Override
    public float[] getTranslation()
    {
        return translation;
    }

    @Override
    public void setRotation(float[] rotation)
    {
        this.rotation = check(rotation, 4);
    }

    @Override
    public float[] getRotation()
    {
        return rotation;
    }

    @Override
    public void setScale(float[] scale)
    {
        this.scale = check(scale, 3);
    }

    @Override
    public float[] getScale()
    {
        return scale;
    }

    @Override
    public void setWeights(float[] weights)
    {
        this.weights = weights;
    }

    @Override
    public float[] getWeights()
    {
        return weights;
    }
    
    
    @Override
    public float[] computeLocalTransform(float result[])
    {
        return computeLocalTransform(this, result);
    }

    @Override
    public float[] computeGlobalTransform(float result[])
    {
        return computeGlobalTransform(this, result);
    }
    
    @Override
    public Supplier<float[]> createGlobalTransformSupplier()
    {
        return Suppliers.createTransformSupplier(this, 
            NodeModel::computeGlobalTransform);
    }
    
    @Override
    public Supplier<float[]> createLocalTransformSupplier()
    {
        return Suppliers.createTransformSupplier(this, 
            NodeModel::computeLocalTransform);
    }

    /**
     * Compute the local transform of the given node. The transform
     * is either taken from the {@link #getMatrix()} (if it is not
     * <code>null</code>), or computed from the {@link #getTranslation()}, 
     * {@link #getRotation()} and {@link #getScale()}, if they
     * are not <code>null</code>, respectively.<br>
     * <br>
     * The result will be written to the given array, as a 4x4 matrix in 
     * column major order. If the given array is <code>null</code> or does
     * not have a length of 16, then a new array with length 16 will be 
     * created and returned. 
     * 
     * @param nodeModel The node. May not be <code>null</code>.
     * @param result The result array
     * @return The result array
     */
    public static float[] computeLocalTransform(
        NodeModel nodeModel, float result[])
    {
        float localResult[] = Utils.validate(result, 16);
        if (nodeModel.getMatrix() != null)
        {
            float m[] = nodeModel.getMatrix();
            System.arraycopy(m, 0, localResult, 0, m.length);
            return localResult;
        }
        
        MathUtils.setIdentity4x4(localResult);
        if (nodeModel.getTranslation() != null)
        {
            float t[] = nodeModel.getTranslation();
            localResult[12] = t[0]; 
            localResult[13] = t[1]; 
            localResult[14] = t[2]; 
        }
        if (nodeModel.getRotation() != null)
        {
            float q[] = nodeModel.getRotation();
            float m[] = TEMP_MATRIX_4x4_IN_LOCAL.get();
            MathUtils.quaternionToMatrix4x4(q, m);
            MathUtils.mul4x4(localResult, m, localResult);
        }
        if (nodeModel.getScale() != null)
        {
            float s[] = nodeModel.getScale();
            float m[] = TEMP_MATRIX_4x4_IN_LOCAL.get();
            MathUtils.setIdentity4x4(m);
            m[ 0] = s[0];
            m[ 5] = s[1];
            m[10] = s[2];
            m[15] = 1.0f;
            MathUtils.mul4x4(localResult, m, localResult);
        }
        return localResult;
    }
    
    /**
     * Compute the global transform for the given {@link NodeModel},
     * and store it in the given result. If the given result is 
     * <code>null</code> or does not have a length of 16, then 
     * a new array will be created and returned.
     * 
     * @param nodeModel The {@link NodeModel}
     * @param result The result
     * @return The result
     */
    private static float[] computeGlobalTransform(
        NodeModel nodeModel, float result[])
    {
        float localResult[] = Utils.validate(result, 16);
        float tempLocalTransform[] = TEMP_MATRIX_4x4_IN_GLOBAL.get();
        NodeModel currentNode = nodeModel;
        MathUtils.setIdentity4x4(localResult);
        while (currentNode != null)
        {
            currentNode.computeLocalTransform(tempLocalTransform);
            MathUtils.mul4x4(
                tempLocalTransform, localResult, localResult);
            currentNode = currentNode.getParent();
        }
        return localResult;
    }
    
    /**
     * Check whether the given array has the expected length, and return
     * the given array. If the given source array is <code>null</code>, then 
     * <code>null</code> will be returned. If the given source array does not 
     * have the expected length, then an <code>IllegalArgumentException</code>
     * will be thrown. 
     * 
     * @param array The array
     * @param expectedLength The expected length
     * @return The array
     * @throws IllegalArgumentException If the given array does not have
     * the expected length
     */
    private static float[] check(float array[], int expectedLength)
    {
        if (array == null)
        {
            return null;
        }
        if (array.length != expectedLength)
        {
            throw new IllegalArgumentException("Expected " + expectedLength
                + " array elements, but found " + array.length);
        }
        return array;
    }
    
    
}
