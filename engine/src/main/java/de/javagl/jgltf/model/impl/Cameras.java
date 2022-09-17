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

import java.util.logging.Logger;

import de.javagl.jgltf.model.CameraModel;
import de.javagl.jgltf.model.CameraOrthographicModel;
import de.javagl.jgltf.model.CameraPerspectiveModel;
import de.javagl.jgltf.model.MathUtils;
import de.javagl.jgltf.model.Utils;

/**
 * Utility methods related to cameras
 */
class Cameras
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(Cameras.class.getName());
    
    /**
     * Compute the projection matrix for the given {@link CameraModel}. If the 
     * camera is neither perspective orthographic, then an error message will 
     * be printed, and the result will be the identity matrix.<br>
     * <br>
     * The result will be written to the given array, as a 4x4 matrix in 
     * column major order. If the given array is <code>null</code> or does
     * not have a length of 16, then a new array with length 16 will be 
     * created and returned. 
     * 
     * @param cameraModel The {@link CameraModel}
     * @param aspectRatio An optional aspect ratio to use. If this is 
     * <code>null</code>, then the aspect ratio of the camera will be used.
     * @param result The array storing the result
     * @return The result array
     */
    static float[] computeProjectionMatrix(
        CameraModel cameraModel, Float aspectRatio, float result[])
    {
        float localResult[] = Utils.validate(result, 16);
        
        CameraPerspectiveModel cameraPerspective = 
            cameraModel.getCameraPerspectiveModel();

        CameraOrthographicModel cameraOrthographic = 
            cameraModel.getCameraOrthographicModel();
        
        if (cameraPerspective != null)
        {
            float fovRad = cameraPerspective.getYfov();
            float fovDeg = (float)Math.toDegrees(fovRad);
            float localAspectRatio = 1.0f;
            if (aspectRatio != null)
            {
                localAspectRatio = aspectRatio;
            }
            else if (cameraPerspective.getAspectRatio() != null)
            {
                localAspectRatio = cameraPerspective.getAspectRatio();
            }
            float zNear = cameraPerspective.getZnear();
            Float zFar = cameraPerspective.getZfar();
            if (zFar == null)
            {
                MathUtils.infinitePerspective4x4(
                    fovDeg, localAspectRatio, zNear, localResult);
            }
            else
            {
                MathUtils.perspective4x4(
                    fovDeg, localAspectRatio, zNear, zFar, localResult);
            }
        }
        else if (cameraOrthographic != null)
        {
            float xMag = cameraOrthographic.getXmag();
            float yMag = cameraOrthographic.getYmag();
            float zNear = cameraOrthographic.getZnear();
            float zFar = cameraOrthographic.getZfar();
            MathUtils.setIdentity4x4(localResult);
            localResult[0] = 1.0f / xMag;
            localResult[5] = 1.0f / yMag;
            localResult[10] = 2.0f / (zNear - zFar);
            localResult[14] = (zFar + zNear) / (zNear - zFar);
        }
        else
        {
            logger.severe("Invalid camera type: " + cameraModel);
            MathUtils.setIdentity4x4(localResult);
        }
        return localResult;
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private Cameras()
    {
        // Private constructor to prevent instantiation
    }
}
