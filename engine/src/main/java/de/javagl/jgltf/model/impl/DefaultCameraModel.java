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

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import de.javagl.jgltf.model.CameraModel;
import de.javagl.jgltf.model.CameraOrthographicModel;
import de.javagl.jgltf.model.CameraPerspectiveModel;
import de.javagl.jgltf.model.Suppliers;

/**
 * Implementation of a {@link CameraModel}  
 */
public final class DefaultCameraModel extends AbstractNamedModelElement
    implements CameraModel
{
    /**
     * The {@link CameraOrthographicModel}
     */
    private CameraOrthographicModel cameraOrthographicModel;
    
    /**
     * The {@link CameraPerspectiveModel}
     */
    private CameraPerspectiveModel cameraPerspectiveModel;
    
    /**
     * Creates a new instance
     */
    public DefaultCameraModel()
    {
        // Default constructor
    }
    
    /**
     * Set the {@link CameraOrthographicModel}
     * 
     * @param cameraOrthographicModel The {@link CameraOrthographicModel}
     */
    public void setCameraOrthographicModel(
        CameraOrthographicModel cameraOrthographicModel)
    {
        this.cameraOrthographicModel = cameraOrthographicModel;
    }
    
    @Override
    public CameraOrthographicModel getCameraOrthographicModel()
    {
        return cameraOrthographicModel;
    }
    
    /**
     * Set the {@link CameraPerspectiveModel}
     * 
     * @param cameraPerspectiveModel The {@link CameraPerspectiveModel}
     */
    public void setCameraPerspectiveModel(
        CameraPerspectiveModel cameraPerspectiveModel)
    {
        this.cameraPerspectiveModel = cameraPerspectiveModel;
    }
    
    @Override
    public CameraPerspectiveModel getCameraPerspectiveModel()
    {
        return cameraPerspectiveModel;
    }

    @Override
    public float[] computeProjectionMatrix(float result[], Float aspectRatio)
    {
        return Cameras.computeProjectionMatrix(this, aspectRatio, result);
    }
    
    @Override
    public Supplier<float[]> createProjectionMatrixSupplier(
        DoubleSupplier aspectRatioSupplier)
    {
        return Suppliers.createTransformSupplier(this, (c, t) -> 
        {
            Float aspectRatio = null;
            if (aspectRatioSupplier != null)
            {
                aspectRatio = (float)aspectRatioSupplier.getAsDouble();
            }
            computeProjectionMatrix(t, aspectRatio);
        });
    }
    
    
}
