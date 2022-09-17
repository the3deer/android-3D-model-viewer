/*
 * www.javagl.de - JglTF
 *
 * Copyright 2015-2016 Marco Hutter - http://www.javagl.de
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.AnimationModel;
import de.javagl.jgltf.model.BufferModel;
import de.javagl.jgltf.model.BufferViewModel;
import de.javagl.jgltf.model.CameraModel;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.ImageModel;
import de.javagl.jgltf.model.MaterialModel;
import de.javagl.jgltf.model.MeshModel;
import de.javagl.jgltf.model.NodeModel;
import de.javagl.jgltf.model.SceneModel;
import de.javagl.jgltf.model.SkinModel;
import de.javagl.jgltf.model.TextureModel;

/**
 * Default implementation of a {@link GltfModel}.<br>
 */
public class DefaultGltfModel extends AbstractModelElement implements GltfModel
{
    /**
     * The {@link AccessorModel} instances
     */
    private final List<DefaultAccessorModel> accessorModels;

    /**
     * The {@link AnimationModel} instances
     */
    private final List<DefaultAnimationModel> animationModels;
    
    /**
     * The {@link BufferModel} instances
     */
    private final List<DefaultBufferModel> bufferModels;

    /**
     * The {@link BufferViewModel} instances
     */
    private final List<DefaultBufferViewModel> bufferViewModels;
    
    /**
     * The {@link CameraModel} instances
     */
    private final List<DefaultCameraModel> cameraModels;

    /**
     * The {@link ImageModel} instances
     */
    private final List<DefaultImageModel> imageModels;

    /**
     * The {@link MaterialModel} instances
     */
    private final List<MaterialModel> materialModels;
    
    /**
     * The {@link MeshModel} instances
     */
    private final List<DefaultMeshModel> meshModels;

    /**
     * The {@link NodeModel} instances
     */
    private final List<DefaultNodeModel> nodeModels;

    /**
     * The {@link SceneModel} instances
     */
    private final List<DefaultSceneModel> sceneModels;

    /**
     * The {@link SkinModel} instances
     */
    private final List<DefaultSkinModel> skinModels;

    /**
     * The {@link TextureModel} instances
     */
    private final List<DefaultTextureModel> textureModels;

    /**
     * Creates a new model 
     */
    public DefaultGltfModel()
    {
        this.accessorModels = new ArrayList<DefaultAccessorModel>();
        this.animationModels = new ArrayList<DefaultAnimationModel>();
        this.bufferModels = new ArrayList<DefaultBufferModel>();
        this.bufferViewModels = new ArrayList<DefaultBufferViewModel>();
        this.cameraModels = new ArrayList<DefaultCameraModel>();
        this.imageModels = new ArrayList<DefaultImageModel>();
        this.materialModels = new ArrayList<MaterialModel>();
        this.meshModels = new ArrayList<DefaultMeshModel>();
        this.nodeModels = new ArrayList<DefaultNodeModel>();
        this.sceneModels = new ArrayList<DefaultSceneModel>();
        this.skinModels = new ArrayList<DefaultSkinModel>();
        this.textureModels = new ArrayList<DefaultTextureModel>();
    }
    
    /**
     * Add the given {@link AccessorModel} to this model
     * 
     * @param accessorModel The object to add
     */
    public void addAccessorModel(DefaultAccessorModel accessorModel)
    {
        accessorModels.add(accessorModel);
    }

    /**
     * Remove the given {@link AccessorModel} from this model
     * 
     * @param accessorModel The object to remove
     */
    public void removeAccessorModel(DefaultAccessorModel accessorModel)
    {
        accessorModels.remove(accessorModel);
    }

    /**
     * Add the given {@link AccessorModel} instances to this model
     * 
     * @param accessorModels The objects to add
     */
    public void addAccessorModels(
        Collection<? extends DefaultAccessorModel> accessorModels)
    {
        for (DefaultAccessorModel accessorModel : accessorModels)
        {
            addAccessorModel(accessorModel);
        }
    }

    /**
     * Return the {@link AccessorModel} at the given index
     *
     * @param index The index
     * @return The {@link AccessorModel}
     */
    public DefaultAccessorModel getAccessorModel(int index)
    {
        return accessorModels.get(index);
    }

    /**
     * Remove all {@link AccessorModel} instances
     */
    public void clearAccessorModels()
    {
        accessorModels.clear();
    }

    @Override
    public List<AccessorModel> getAccessorModels()
    {
        return Collections.unmodifiableList(accessorModels);
    }
    
    /**
     * Add the given {@link AnimationModel} to this model
     *
     * @param animationModel The instance to add
     */
    public void addAnimationModel(DefaultAnimationModel animationModel)
    {
        animationModels.add(animationModel);
    }

    /**
     * Remove the given {@link AnimationModel} from this model
     *
     * @param animationModel The instance to remove
     */
    public void removeAnimationModel(DefaultAnimationModel animationModel)
    {
        animationModels.remove(animationModel);
    }

    /**
     * Add the given {@link AnimationModel} instances to this model
     *
     * @param animationModels The instances to add
     */
    public void addAnimationModels(
        Collection<? extends DefaultAnimationModel> animationModels)
    {
        for (DefaultAnimationModel animationModel : animationModels)
        {
            addAnimationModel(animationModel);
        }
    }

    /**
     * Return the {@link AnimationModel} at the given index
     *
     * @param index The index
     * @return The {@link AnimationModel}
     */
    public DefaultAnimationModel getAnimationModel(int index)
    {
        return animationModels.get(index);
    }

    /**
     * Remove all {@link AnimationModel} instances
     */
    public void clearAnimationModels()
    {
        animationModels.clear();
    }

    @Override
    public List<AnimationModel> getAnimationModels()
    {
        return Collections.unmodifiableList(animationModels);
    }
    
    /**
     * Add the given {@link BufferModel} to this model
     * 
     * @param bufferModel The instance to add
     */
    public void addBufferModel(DefaultBufferModel bufferModel)
    {
        bufferModels.add(bufferModel);
    }

    /**
     * Remove the given {@link BufferModel} from this model
     * 
     * @param bufferModel The instance to remove
     */
    public void removeBufferModel(DefaultBufferModel bufferModel)
    {
        bufferModels.remove(bufferModel);
    }

    /**
     * Add the given {@link BufferModel} instances to this model
     * 
     * @param bufferModels The instances to add
     */
    public void addBufferModels(
        Collection<? extends DefaultBufferModel> bufferModels)
    {
        for (DefaultBufferModel bufferModel : bufferModels)
        {
            addBufferModel(bufferModel);
        }
    }

    /**
     * Return the {@link BufferModel} at the given index
     *
     * @param index The index
     * @return The {@link BufferModel}
     */
    public DefaultBufferModel getBufferModel(int index)
    {
        return bufferModels.get(index);
    }

    /**
     * Remove all {@link BufferModel} instances
     */
    public void clearBufferModels()
    {
        bufferModels.clear();
    }

    @Override
    public List<BufferModel> getBufferModels()
    {
        return Collections.unmodifiableList(bufferModels);
    }
    
    /**
     * Add the given {@link BufferViewModel} to this model
     * 
     * @param bufferViewModel The instance to add
     */
    public void addBufferViewModel(DefaultBufferViewModel bufferViewModel)
    {
        bufferViewModels.add(bufferViewModel);
    }

    /**
     * Remove the given {@link BufferViewModel} from this model
     * 
     * @param bufferViewModel The instance to remove
     */
    public void removeBufferViewModel(DefaultBufferViewModel bufferViewModel)
    {
        bufferViewModels.remove(bufferViewModel);
    }

    /**
     * Add the given {@link BufferViewModel} instances to this model
     * 
     * @param bufferViewModels The instances to add
     */
    public void addBufferViewModels(
        Collection<? extends DefaultBufferViewModel> bufferViewModels)
    {
        for (DefaultBufferViewModel bufferViewModel : bufferViewModels)
        {
            addBufferViewModel(bufferViewModel);
        }
    }

    /**
     * Return the {@link BufferViewModel} at the given index
     *
     * @param index The index
     * @return The {@link BufferViewModel}
     */
    public DefaultBufferViewModel getBufferViewModel(int index)
    {
        return bufferViewModels.get(index);
    }

    /**
     * Remove all {@link BufferViewModel} instances
     */
    public void clearBufferViewModels()
    {
        bufferViewModels.clear();
    }

    @Override
    public List<BufferViewModel> getBufferViewModels()
    {
        return Collections.unmodifiableList(bufferViewModels);
    }
    
    /**
     * Add the given {@link CameraModel} to this model
     * 
     * @param cameraModel The instance to add
     */
    public void addCameraModel(DefaultCameraModel cameraModel)
    {
        cameraModels.add(cameraModel);
    }

    /**
     * Remove the given {@link CameraModel} from this model
     * 
     * @param cameraModel The instance to remove
     */
    public void removeCameraModel(DefaultCameraModel cameraModel)
    {
        cameraModels.remove(cameraModel);
    }

    /**
     * Add the given {@link CameraModel} instances to this model
     * 
     * @param cameraModels The instances to add
     */
    public void addCameraModels(
        Collection<? extends DefaultCameraModel> cameraModels)
    {
        for (DefaultCameraModel cameraModel : cameraModels)
        {
            addCameraModel(cameraModel);
        }
    }

    /**
     * Return the {@link CameraModel} at the given index
     *
     * @param index The index
     * @return The {@link CameraModel}
     */
    public DefaultCameraModel getCameraModel(int index)
    {
        return cameraModels.get(index);
    }

    /**
     * Remove all {@link CameraModel} instances
     */
    public void clearCameraModels()
    {
        cameraModels.clear();
    }

    @Override
    public List<CameraModel> getCameraModels()
    {
        return Collections.unmodifiableList(cameraModels);
    }
    
    /**
     * Add the given {@link ImageModel} to this model
     * 
     * @param imageModel The instance to add
     */
    public void addImageModel(DefaultImageModel imageModel)
    {
        imageModels.add(imageModel);
    }

    /**
     * Remove the given {@link ImageModel} from this model
     * 
     * @param imageModel The instance to remove
     */
    public void removeImageModel(DefaultImageModel imageModel)
    {
        imageModels.remove(imageModel);
    }

    /**
     * Add the given {@link ImageModel} instances to this model
     * 
     * @param imageModels The instances to add
     */
    public void addImageModels(
        Collection<? extends DefaultImageModel> imageModels)
    {
        for (DefaultImageModel imageModel : imageModels)
        {
            addImageModel(imageModel);
        }
    }

    /**
     * Return the {@link ImageModel} at the given index
     *
     * @param index The index
     * @return The {@link ImageModel}
     */
    public DefaultImageModel getImageModel(int index)
    {
        return imageModels.get(index);
    }

    /**
     * Remove all {@link ImageModel} instances
     */
    public void clearImageModels()
    {
        imageModels.clear();
    }

    @Override
    public List<ImageModel> getImageModels()
    {
        return Collections.unmodifiableList(imageModels);
    }
    
    /**
     * Add the given {@link MaterialModel} to this model
     * 
     * @param materialModel The instance to add
     */
    public void addMaterialModel(MaterialModel materialModel)
    {
        materialModels.add(materialModel);
    }

    /**
     * Remove the given {@link MaterialModel} from this model
     * 
     * @param materialModel The instance to remove
     */
    public void removeMaterialModel(MaterialModel materialModel)
    {
        materialModels.remove(materialModel);
    }

    /**
     * Add the given {@link MaterialModel} instances to this model
     * 
     * @param materialModels The instances to add
     */
    public void addMaterialModels(
        Collection<? extends MaterialModel> materialModels)
    {
        for (MaterialModel materialModel : materialModels)
        {
            addMaterialModel(materialModel);
        }
    }

    /**
     * Return the {@link MaterialModel} at the given index
     *
     * @param index The index
     * @return The {@link MaterialModel}
     */
    public MaterialModel getMaterialModel(int index)
    {
        return materialModels.get(index);
    }

    /**
     * Remove all {@link MaterialModel} instances
     */
    public void clearMaterialModels()
    {
        materialModels.clear();
    }

    @Override
    public List<MaterialModel> getMaterialModels()
    {
        return Collections.unmodifiableList(materialModels);
    }

    /**
     * Add the given {@link MeshModel} to this model
     * 
     * @param meshModel The instance to add
     */
    public void addMeshModel(DefaultMeshModel meshModel)
    {
        meshModels.add(meshModel);
    }

    /**
     * Remove the given {@link MeshModel} from this model
     * 
     * @param meshModel The instance to remove
     */
    public void removeMeshModel(DefaultMeshModel meshModel)
    {
        meshModels.remove(meshModel);
    }

    /**
     * Add the given {@link MeshModel} instances to this model
     * 
     * @param meshModels The instances to add
     */
    public void addMeshModels(
        Collection<? extends DefaultMeshModel> meshModels)
    {
        for (DefaultMeshModel meshModel : meshModels)
        {
            addMeshModel(meshModel);
        }
    }

    /**
     * Return the {@link MeshModel} at the given index
     *
     * @param index The index
     * @return The {@link MeshModel}
     */
    public DefaultMeshModel getMeshModel(int index)
    {
        return meshModels.get(index);
    }

    /**
     * Remove all {@link MeshModel} instances
     */
    public void clearMeshModels()
    {
        meshModels.clear();
    }

    @Override
    public List<MeshModel> getMeshModels()
    {
        return Collections.unmodifiableList(meshModels);
    }
    
    /**
     * Add the given {@link NodeModel} to this model
     * 
     * @param nodeModel The instance to add
     */
    public void addNodeModel(DefaultNodeModel nodeModel)
    {
        nodeModels.add(nodeModel);
    }

    /**
     * Remove the given {@link NodeModel} from this model
     * 
     * @param nodeModel The instance to remove
     */
    public void removeNodeModel(DefaultNodeModel nodeModel)
    {
        nodeModels.remove(nodeModel);
    }

    /**
     * Add the given {@link NodeModel} instances to this model
     * 
     * @param nodeModels The instances to add
     */
    public void addNodeModels(
        Collection<? extends DefaultNodeModel> nodeModels)
    {
        for (DefaultNodeModel nodeModel : nodeModels)
        {
            addNodeModel(nodeModel);
        }
    }

    /**
     * Return the {@link NodeModel} at the given index
     *
     * @param index The index
     * @return The {@link NodeModel}
     */
    public DefaultNodeModel getNodeModel(int index)
    {
        return nodeModels.get(index);
    }

    /**
     * Remove all {@link NodeModel} instances
     */
    public void clearNodeModels()
    {
        nodeModels.clear();
    }

    @Override
    public List<NodeModel> getNodeModels()
    {
        return Collections.unmodifiableList(nodeModels);
    }
    
    /**
     * Add the given {@link SceneModel} to this model
     * 
     * @param sceneModel The instance to add
     */
    public void addSceneModel(DefaultSceneModel sceneModel)
    {
        sceneModels.add(sceneModel);
    }

    /**
     * Remove the given {@link SceneModel} from this model
     * 
     * @param sceneModel The instance to remove
     */
    public void removeSceneModel(DefaultSceneModel sceneModel)
    {
        sceneModels.remove(sceneModel);
    }

    /**
     * Add the given {@link SceneModel} instances to this model
     * 
     * @param sceneModels The instances to add
     */
    public void addSceneModels(
        Collection<? extends DefaultSceneModel> sceneModels)
    {
        for (DefaultSceneModel sceneModel : sceneModels)
        {
            addSceneModel(sceneModel);
        }
    }

    /**
     * Return the {@link SceneModel} at the given index
     *
     * @param index The index
     * @return The {@link SceneModel}
     */
    public DefaultSceneModel getSceneModel(int index)
    {
        return sceneModels.get(index);
    }

    /**
     * Remove all {@link SceneModel} instances
     */
    public void clearSceneModels()
    {
        sceneModels.clear();
    }

    @Override
    public List<SceneModel> getSceneModels()
    {
        return Collections.unmodifiableList(sceneModels);
    }

    /**
     * Add the given {@link SkinModel} to this model
     * 
     * @param skinModel The instance to add
     */
    public void addSkinModel(DefaultSkinModel skinModel)
    {
        skinModels.add(skinModel);
    }

    /**
     * Remove the given {@link SkinModel} from this model
     * 
     * @param skinModel The instance to remove
     */
    public void removeSkinModel(DefaultSkinModel skinModel)
    {
        skinModels.remove(skinModel);
    }

    /**
     * Add the given {@link SkinModel} instances to this model
     * 
     * @param skinModels The instances to add
     */
    public void addSkinModels(
        Collection<? extends DefaultSkinModel> skinModels)
    {
        for (DefaultSkinModel skinModel : skinModels)
        {
            addSkinModel(skinModel);
        }
    }

    /**
     * Return the {@link SkinModel} at the given index
     *
     * @param index The index
     * @return The {@link SkinModel}
     */
    public DefaultSkinModel getSkinModel(int index)
    {
        return skinModels.get(index);
    }

    /**
     * Remove all {@link SkinModel} instances
     */
    public void clearSkinModels()
    {
        skinModels.clear();
    }

    @Override
    public List<SkinModel> getSkinModels()
    {
        return Collections.unmodifiableList(skinModels);
    }
    
    /**
     * Add the given {@link TextureModel} to this model
     * 
     * @param textureModel The instance to add
     */
    public void addTextureModel(DefaultTextureModel textureModel)
    {
        textureModels.add(textureModel);
    }

    /**
     * Remove the given {@link TextureModel} from this model
     * 
     * @param textureModel The instance to remove
     */
    public void removeTextureModel(DefaultTextureModel textureModel)
    {
        textureModels.remove(textureModel);
    }

    /**
     * Add the given {@link TextureModel} instances to this model
     * 
     * @param textureModels The instances to add
     */
    public void addTextureModels(
        Collection<? extends DefaultTextureModel> textureModels)
    {
        for (DefaultTextureModel textureModel : textureModels)
        {
            addTextureModel(textureModel);
        }
    }

    /**
     * Return the {@link TextureModel} at the given index
     *
     * @param index The index
     * @return The {@link TextureModel}
     */
    public DefaultTextureModel getTextureModel(int index)
    {
        return textureModels.get(index);
    }

    /**
     * Remove all {@link TextureModel} instances
     */
    public void clearTextureModels()
    {
        textureModels.clear();
    }

    @Override
    public List<TextureModel> getTextureModels()
    {
        return Collections.unmodifiableList(textureModels);
    }
}
