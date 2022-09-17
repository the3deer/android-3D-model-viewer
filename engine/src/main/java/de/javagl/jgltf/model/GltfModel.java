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
 * Interface for a model that was created from a glTF asset
 */
public interface GltfModel extends ModelElement
{
    /**
     * Returns an unmodifiable view on the list of {@link AccessorModel} 
     * instances that have been created for the glTF.
     * 
     * @return The {@link AccessorModel} instances
     */
    List<AccessorModel> getAccessorModels();
    
    /**
     * Returns an unmodifiable view on the list of {@link AnimationModel} 
     * instances that have been created for the glTF.
     * 
     * @return The {@link AnimationModel} instances
     */
    List<AnimationModel> getAnimationModels();

    /**
     * Returns an unmodifiable view on the list of {@link BufferModel} 
     * instances that have been created for the glTF.
     * 
     * @return The {@link BufferModel} instances
     */
    List<BufferModel> getBufferModels();

    /**
     * Returns an unmodifiable view on the list of {@link BufferViewModel} 
     * instances that have been created for the glTF.
     * 
     * @return The {@link BufferViewModel} instances
     */
    List<BufferViewModel> getBufferViewModels();
    
    /**
     * Returns an unmodifiable view on the list of {@link CameraModel} 
     * instances that have been created for the glTF.
     * 
     * @return The {@link CameraModel} instances
     */
    List<CameraModel> getCameraModels();

    /**
     * Returns an unmodifiable view on the list of {@link ImageModel} 
     * instances that have been created for the glTF.
     * 
     * @return The {@link ImageModel} instances
     */
    List<ImageModel> getImageModels();
    
    /**
     * Returns an unmodifiable view on the list of {@link MaterialModel} 
     * instances that have been created for the glTF.
     * 
     * @return The {@link MaterialModel} instances
     */
    List<MaterialModel> getMaterialModels();

    /**
     * Returns an unmodifiable view on the list of {@link MeshModel} 
     * instances that have been created for the glTF.
     * 
     * @return The {@link MeshModel} instances
     */
    List<MeshModel> getMeshModels();
    
    /**
     * Returns an unmodifiable view on the list of {@link NodeModel} 
     * instances that have been created for the glTF.
     * 
     * @return The {@link NodeModel} instances
     */
    List<NodeModel> getNodeModels();

    /**
     * Returns an unmodifiable view on the list of {@link SceneModel} 
     * instances that have been created for the glTF.
     * 
     * @return The {@link SceneModel} instances
     */
    List<SceneModel> getSceneModels();

    /**
     * Returns an unmodifiable view on the list of {@link SkinModel} 
     * instances that have been created for the glTF.
     * 
     * @return The {@link SkinModel} instances
     */
    List<SkinModel> getSkinModels();

    /**
     * Returns an unmodifiable view on the list of {@link TextureModel} 
     * instances that have been created for the glTF.
     * 
     * @return The {@link TextureModel} instances
     */
    List<TextureModel> getTextureModels();
    
}

