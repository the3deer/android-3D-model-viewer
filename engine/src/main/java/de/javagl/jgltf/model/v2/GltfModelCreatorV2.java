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
package de.javagl.jgltf.model.v2;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Logger;

import de.javagl.jgltf.impl.v2.Accessor;
import de.javagl.jgltf.impl.v2.AccessorSparse;
import de.javagl.jgltf.impl.v2.AccessorSparseIndices;
import de.javagl.jgltf.impl.v2.AccessorSparseValues;
import de.javagl.jgltf.impl.v2.Animation;
import de.javagl.jgltf.impl.v2.AnimationChannel;
import de.javagl.jgltf.impl.v2.AnimationChannelTarget;
import de.javagl.jgltf.impl.v2.AnimationSampler;
import de.javagl.jgltf.impl.v2.Buffer;
import de.javagl.jgltf.impl.v2.BufferView;
import de.javagl.jgltf.impl.v2.Camera;
import de.javagl.jgltf.impl.v2.CameraOrthographic;
import de.javagl.jgltf.impl.v2.CameraPerspective;
import de.javagl.jgltf.impl.v2.GlTF;
import de.javagl.jgltf.impl.v2.GlTFChildOfRootProperty;
import de.javagl.jgltf.impl.v2.GlTFProperty;
import de.javagl.jgltf.impl.v2.Image;
import de.javagl.jgltf.impl.v2.Material;
import de.javagl.jgltf.impl.v2.MaterialNormalTextureInfo;
import de.javagl.jgltf.impl.v2.MaterialOcclusionTextureInfo;
import de.javagl.jgltf.impl.v2.MaterialPbrMetallicRoughness;
import de.javagl.jgltf.impl.v2.Mesh;
import de.javagl.jgltf.impl.v2.MeshPrimitive;
import de.javagl.jgltf.impl.v2.Node;
import de.javagl.jgltf.impl.v2.Sampler;
import de.javagl.jgltf.impl.v2.Scene;
import de.javagl.jgltf.impl.v2.Skin;
import de.javagl.jgltf.impl.v2.Texture;
import de.javagl.jgltf.impl.v2.TextureInfo;
import de.javagl.jgltf.model.AccessorData;
import de.javagl.jgltf.model.AccessorDatas;
import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.AnimationModel;
import de.javagl.jgltf.model.AnimationModel.Channel;
import de.javagl.jgltf.model.AnimationModel.Interpolation;
import de.javagl.jgltf.model.BufferModel;
import de.javagl.jgltf.model.BufferViewModel;
import de.javagl.jgltf.model.CameraModel;
import de.javagl.jgltf.model.ElementType;
import de.javagl.jgltf.model.GltfConstants;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.ImageModel;
import de.javagl.jgltf.model.MaterialModel;
import de.javagl.jgltf.model.MeshModel;
import de.javagl.jgltf.model.MeshPrimitiveModel;
import de.javagl.jgltf.model.NodeModel;
import de.javagl.jgltf.model.Optionals;
import de.javagl.jgltf.model.SceneModel;
import de.javagl.jgltf.model.SkinModel;
import de.javagl.jgltf.model.TextureModel;
import de.javagl.jgltf.model.impl.AbstractModelElement;
import de.javagl.jgltf.model.impl.AbstractNamedModelElement;
import de.javagl.jgltf.model.impl.DefaultAccessorModel;
import de.javagl.jgltf.model.impl.DefaultAnimationModel;
import de.javagl.jgltf.model.impl.DefaultAnimationModel.DefaultChannel;
import de.javagl.jgltf.model.impl.DefaultAnimationModel.DefaultSampler;
import de.javagl.jgltf.model.impl.DefaultBufferModel;
import de.javagl.jgltf.model.impl.DefaultBufferViewModel;
import de.javagl.jgltf.model.impl.DefaultCameraModel;
import de.javagl.jgltf.model.impl.DefaultCameraOrthographicModel;
import de.javagl.jgltf.model.impl.DefaultCameraPerspectiveModel;
import de.javagl.jgltf.model.impl.DefaultGltfModel;
import de.javagl.jgltf.model.impl.DefaultImageModel;
import de.javagl.jgltf.model.impl.DefaultMeshModel;
import de.javagl.jgltf.model.impl.DefaultMeshPrimitiveModel;
import de.javagl.jgltf.model.impl.DefaultNodeModel;
import de.javagl.jgltf.model.impl.DefaultSceneModel;
import de.javagl.jgltf.model.impl.DefaultSkinModel;
import de.javagl.jgltf.model.impl.DefaultTextureModel;
import de.javagl.jgltf.model.io.Buffers;
import de.javagl.jgltf.model.io.GltfAsset;
import de.javagl.jgltf.model.io.IO;
import de.javagl.jgltf.model.io.v2.GltfAssetV2;
import de.javagl.jgltf.model.v2.MaterialModelV2.AlphaMode;
import de.javagl.jgltf.model.v2.gl.Materials;

/**
 * A class that is responsible for filling a {@link DefaultGltfModel} with
 * the model instances that are created from a {@link GltfAssetV2}
 */
public class GltfModelCreatorV2
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(GltfModelCreatorV2.class.getName());

    /**
     * Create the {@link GltfModel} for the given {@link GltfAssetV2}
     * 
     * @param gltfAsset The {@link GltfAssetV2}
     * @return The {@link GltfModel}
     */
    public static DefaultGltfModel create(GltfAssetV2 gltfAsset)
    {
        DefaultGltfModel gltfModel = new DefaultGltfModel();
        GltfModelCreatorV2 creator = 
            new GltfModelCreatorV2(gltfAsset, gltfModel);
        creator.create();
        return gltfModel;
    }
    
    /**
     * The {@link GltfAsset} of the model
     */
    private final GltfAsset gltfAsset;
    
    /**
     * The {@link GlTF} of this model
     */
    private final GlTF gltf;
    
    /**
     * The {@link GltfModel} that is built
     */
    private final DefaultGltfModel gltfModel;
    
    /**
     * Creates a new model for the given glTF
     * 
     * @param gltfAsset The {@link GltfAssetV2}
     * @param gltfModel The {@link GltfModel}
     */
    GltfModelCreatorV2(GltfAssetV2 gltfAsset, DefaultGltfModel gltfModel)
    {
        this.gltfAsset = Objects.requireNonNull(gltfAsset, 
            "The gltfAsset may not be null");
        this.gltf = gltfAsset.getGltf();
        this.gltfModel = Objects.requireNonNull(gltfModel, 
            "The gltfModel may not be null");
    }
    
    /**
     * Create and initialize all models
     */
    void create()
    {
        transferGltfPropertyElements(gltf, gltfModel);
        
        createAccessorModels();
        createAnimationModels();
        createBufferModels();
        createBufferViewModels();
        createCameraModels();
        createImageModels();
        createMaterialModels();
        createMeshModels();
        createNodeModels();
        createSceneModels();
        createSkinModels();
        createTextureModels();

        initBufferModels();
        initBufferViewModels();
        
        initAccessorModels();
        initAnimationModels();
        initImageModels();
        initMeshModels();
        initNodeModels();
        initSceneModels();
        initSkinModels();
        initTextureModels();
        initMaterialModels();
    }
    
    /**
     * Create the {@link AccessorModel} instances
     */
    private void createAccessorModels()
    {
        List<Accessor> accessors = Optionals.of(gltf.getAccessors());
        for (int i = 0; i < accessors.size(); i++)
        {
            Accessor accessor = accessors.get(i);
            Integer componentType = accessor.getComponentType();
            Integer count = accessor.getCount();
            ElementType elementType = ElementType.forString(accessor.getType());
            DefaultAccessorModel accessorModel =  new DefaultAccessorModel(
                componentType, count, elementType);
            gltfModel.addAccessorModel(accessorModel);
        }
    }

    /**
     * Create the {@link AnimationModel} instances
     */
    private void createAnimationModels()
    {
        List<Animation> animations = Optionals.of(gltf.getAnimations());
        for (int i = 0; i < animations.size(); i++)
        {
            gltfModel.addAnimationModel(new DefaultAnimationModel());
        }
    }
    
    /**
     * Create the {@link BufferModel} instances
     */
    private void createBufferModels()
    {
        List<Buffer> buffers = Optionals.of(gltf.getBuffers());
        for (int i = 0; i < buffers.size(); i++)
        {
            Buffer buffer = buffers.get(i);
            DefaultBufferModel bufferModel = new DefaultBufferModel();
            bufferModel.setUri(buffer.getUri());
            gltfModel.addBufferModel(bufferModel);
        }
    }
    
    /**
     * Create the {@link CameraModel} instances
     */
    private void createCameraModels()
    {
        List<Camera> cameras = Optionals.of(gltf.getCameras());
        for (int i = 0; i < cameras.size(); i++)
        {
            Camera camera = cameras.get(i);
            String type = camera.getType();
            if ("perspective".equals(type))
            {
                CameraPerspective cameraPerspective = camera.getPerspective();
                DefaultCameraPerspectiveModel cameraPerspectiveModel = 
                    new DefaultCameraPerspectiveModel();
                cameraPerspectiveModel.setAspectRatio(
                    cameraPerspective.getAspectRatio());
                cameraPerspectiveModel.setYfov(
                    cameraPerspective.getYfov());
                cameraPerspectiveModel.setZfar(
                    cameraPerspective.getZfar());
                cameraPerspectiveModel.setZnear(
                    cameraPerspective.getZnear());
                DefaultCameraModel cameraModel = 
                    new DefaultCameraModel();
                cameraModel.setCameraPerspectiveModel(cameraPerspectiveModel);
                gltfModel.addCameraModel(cameraModel);
            }
            else if ("orthographic".equals(type))
            {
                CameraOrthographic cameraOrthographic = 
                    camera.getOrthographic();
                DefaultCameraOrthographicModel cameraOrthographicModel = 
                    new DefaultCameraOrthographicModel();
                cameraOrthographicModel.setXmag(
                    cameraOrthographic.getXmag());
                cameraOrthographicModel.setYmag(
                    cameraOrthographic.getYmag());
                cameraOrthographicModel.setZfar(
                    cameraOrthographic.getZfar());
                cameraOrthographicModel.setZnear(
                    cameraOrthographic.getZnear());
                DefaultCameraModel cameraModel = 
                    new DefaultCameraModel();
                cameraModel.setCameraOrthographicModel(cameraOrthographicModel);
                gltfModel.addCameraModel(cameraModel);
            }
            else
            {
                logger.severe("Invalid camera type: " + type);
            }
        }
    }
    
    /**
     * Create the {@link BufferViewModel} instances
     */
    private void createBufferViewModels()
    {
        List<BufferView> bufferViews = Optionals.of(gltf.getBufferViews());
        for (int i = 0; i < bufferViews.size(); i++)
        {
            BufferView bufferView = bufferViews.get(i);
            DefaultBufferViewModel bufferViewModel = 
                createBufferViewModel(bufferView);
            gltfModel.addBufferViewModel(bufferViewModel);
        }
    }

    /**
     * Create a {@link DefaultBufferViewModel} for the given {@link BufferView}
     * 
     * @param bufferView The {@link BufferView}
     * @return The {@link BufferViewModel}
     */
    private static DefaultBufferViewModel createBufferViewModel(
        BufferView bufferView)
    {
        int byteOffset = Optionals.of(bufferView.getByteOffset(), 0);
        int byteLength = bufferView.getByteLength();
        Integer byteStride = bufferView.getByteStride();
        Integer target = bufferView.getTarget();
        DefaultBufferViewModel bufferViewModel = 
            new DefaultBufferViewModel(target);
        bufferViewModel.setByteOffset(byteOffset);
        bufferViewModel.setByteLength(byteLength);
        bufferViewModel.setByteStride(byteStride);
        return bufferViewModel;
    }
    
    /**
     * Create the {@link ImageModel} instances
     */
    private void createImageModels()
    {
        List<Image> images = Optionals.of(gltf.getImages());
        for (int i = 0; i < images.size(); i++)
        {
            Image image = images.get(i);
            String mimeType = image.getMimeType();
            DefaultImageModel imageModel = 
                new DefaultImageModel();
            imageModel.setMimeType(mimeType);
            String uri = image.getUri();
            imageModel.setUri(uri);
            gltfModel.addImageModel(imageModel);
        }
    }
    
    /**
     * Create the {@link MaterialModel} instances
     */
    private void createMaterialModels()
    {
        List<Material> materials = Optionals.of(gltf.getMaterials());
        for (int i = 0; i < materials.size(); i++)
        {
            MaterialModelV2 materialModel = new MaterialModelV2();
            gltfModel.addMaterialModel(materialModel);
        }
    }
    
    /**
     * Create the {@link MeshModel} instances
     */
    private void createMeshModels()
    {
        List<Mesh> meshes = Optionals.of(gltf.getMeshes());
        for (int i = 0; i < meshes.size(); i++)
        {
            gltfModel.addMeshModel(new DefaultMeshModel());
        }
    }

    /**
     * Create the {@link NodeModel} instances
     */
    private void createNodeModels()
    {
        List<Node> nodes = Optionals.of(gltf.getNodes());
        for (int i = 0; i < nodes.size(); i++)
        {
            gltfModel.addNodeModel(new DefaultNodeModel());
        }
    }

    /**
     * Create the {@link SceneModel} instances
     */
    private void createSceneModels()
    {
        List<Scene> scenes = Optionals.of(gltf.getScenes());
        for (int i = 0; i < scenes.size(); i++)
        {
            gltfModel.addSceneModel(new DefaultSceneModel());
        }
    }
    
    /**
     * Create the {@link SkinModel} instances
     */
    private void createSkinModels()
    {
        List<Skin> skins = Optionals.of(gltf.getSkins());
        for (int i = 0; i < skins.size(); i++)
        {
            gltfModel.addSkinModel(new DefaultSkinModel());
        }
    }
    
    /**
     * Create the {@link TextureModel} instances
     */
    private void createTextureModels()
    {
        List<Texture> textures = Optionals.of(gltf.getTextures());
        List<Sampler> samplers = Optionals.of(gltf.getSamplers());
        for (int i = 0; i < textures.size(); i++)
        {
            Texture texture = textures.get(i);
            Integer samplerIndex = texture.getSampler();
            
            Integer magFilter = GltfConstants.GL_LINEAR;
            Integer minFilter = GltfConstants.GL_LINEAR;
            Integer wrapS = GltfConstants.GL_REPEAT;
            Integer wrapT = GltfConstants.GL_REPEAT;
            
            if (samplerIndex != null)
            {
                Sampler sampler = samplers.get(samplerIndex);
                magFilter = sampler.getMagFilter();
                minFilter = sampler.getMinFilter();
                wrapS = Optionals.of(
                    sampler.getWrapS(), sampler.defaultWrapS());
                wrapT = Optionals.of(
                    sampler.getWrapT(), sampler.defaultWrapT());
            }
            
            DefaultTextureModel textureModel = new DefaultTextureModel();
            textureModel.setMagFilter(magFilter);
            textureModel.setMinFilter(minFilter);
            textureModel.setWrapS(wrapS);
            textureModel.setWrapT(wrapT);
            gltfModel.addTextureModel(textureModel);
        }
    }
    
    /**
     * Initialize the {@link AccessorModel} instances
     */
    private void initAccessorModels()
    {
        List<Accessor> accessors = Optionals.of(gltf.getAccessors());
        for (int i = 0; i < accessors.size(); i++)
        {
            Accessor accessor = accessors.get(i);
            DefaultAccessorModel accessorModel = 
                gltfModel.getAccessorModel(i);
            transferGltfChildOfRootPropertyElements(accessor, accessorModel);
            
            int byteOffset = Optionals.of(accessor.getByteOffset(), 0);
            accessorModel.setByteOffset(byteOffset);
            
            Boolean normalized = accessor.isNormalized();
            accessorModel.setNormalized(Boolean.TRUE.equals(normalized));

            AccessorSparse accessorSparse = accessor.getSparse();
            if (accessorSparse == null)
            {
                initDenseAccessorModel(i, accessor, accessorModel);
            }
            else
            {
                initSparseAccessorModel(i, accessor, accessorModel);
            }
        }
    }


    /**
     * Initialize the {@link AccessorModel} by setting its 
     * {@link AccessorModel#getBufferViewModel() buffer view model}
     * for the case that the accessor is dense (i.e. not sparse)
     * 
     * @param accessorIndex The accessor index. Only used for constructing
     * the URI string of buffers that may have to be created internally 
     * @param accessor The {@link Accessor}
     * @param accessorModel The {@link AccessorModel}
     */
    private void initDenseAccessorModel(int accessorIndex,
        Accessor accessor, DefaultAccessorModel accessorModel)
    {
        Integer bufferViewIndex = accessor.getBufferView();
        if (bufferViewIndex != null)
        {
            // When there is a BufferView referenced from the accessor, then 
            // the corresponding BufferViewModel may be assigned directly
            BufferViewModel bufferViewModel = 
                gltfModel.getBufferViewModel(bufferViewIndex);
            accessorModel.setBufferViewModel(bufferViewModel);
            Integer byteStride = bufferViewModel.getByteStride();
            if (byteStride != null)
            {
                accessorModel.setByteStride(byteStride);
            }
            accessorModel.setAccessorData(AccessorDatas.create(accessorModel));
        }
        else
        {
            // When there is no BufferView referenced from the accessor,
            // then a NEW BufferViewModel (and Buffer) have to be created
            int count = accessorModel.getCount();
            int elementSizeInBytes = accessorModel.getElementSizeInBytes();
            int byteLength = elementSizeInBytes * count;
            ByteBuffer bufferData = Buffers.create(byteLength);
            String uriString = "buffer_for_accessor" + accessorIndex + ".bin";
            BufferViewModel bufferViewModel = 
                createBufferViewModel(uriString, bufferData);
            accessorModel.setBufferViewModel(bufferViewModel);
            accessorModel.setAccessorData(AccessorDatas.create(accessorModel));
        }
    }
    
    /**
     * Initialize the given {@link AccessorModel} by setting its 
     * {@link AccessorModel#getBufferViewModel() buffer view model}
     * for the case that the accessor is sparse. 
     * 
     * @param accessorIndex The accessor index. Only used for constructing
     * the URI string of buffers that may have to be created internally 
     * @param accessor The {@link Accessor}
     * @param accessorModel The {@link AccessorModel}
     */
    private void initSparseAccessorModel(int accessorIndex,
        Accessor accessor, DefaultAccessorModel accessorModel)
    {
        // When the (sparse!) Accessor already refers to a BufferView,
        // then this BufferView has to be replaced with a new one,
        // to which the data substitution will be applied 
        int count = accessorModel.getCount();
        int elementSizeInBytes = accessorModel.getElementSizeInBytes();
        int byteLength = elementSizeInBytes * count;
        ByteBuffer bufferData = Buffers.create(byteLength);
        String uriString = "buffer_for_accessor" + accessorIndex + ".bin";
        DefaultBufferViewModel denseBufferViewModel = 
            createBufferViewModel(uriString, bufferData);
        accessorModel.setBufferViewModel(denseBufferViewModel);
        accessorModel.setByteOffset(0);
        
        Integer bufferViewIndex = accessor.getBufferView();
        if (bufferViewIndex != null)
        {
            // If the accessor refers to a BufferView, then the corresponding
            // data serves as the basis for the initialization of the values, 
            // before the sparse substitution is applied
            Consumer<ByteBuffer> sparseSubstitutionCallback = denseByteBuffer -> 
            {
                logger.fine("Substituting sparse accessor data,"
                    + " based on existing buffer view");
                
                DefaultBufferViewModel baseBufferViewModel = 
                    gltfModel.getBufferViewModel(bufferViewIndex);
                ByteBuffer baseBufferViewData = 
                    baseBufferViewModel.getBufferViewData();
                AccessorData baseAccessorData = AccessorDatas.create(
                    accessorModel, baseBufferViewData);
                AccessorData denseAccessorData = 
                    AccessorDatas.create(accessorModel, bufferData);
                substituteSparseAccessorData(accessor, accessorModel, 
                    denseAccessorData, baseAccessorData); 
            };
            denseBufferViewModel.setSparseSubstitutionCallback(
                sparseSubstitutionCallback);
        }
        else
        {
            // When the sparse accessor does not yet refer to a BufferView,
            // then a new one is created, 
            Consumer<ByteBuffer> sparseSubstitutionCallback = denseByteBuffer -> 
            {
                logger.fine("Substituting sparse accessor data, "
                    + "without an existing buffer view");
                
                AccessorData denseAccessorData = 
                    AccessorDatas.create(accessorModel, bufferData);
                substituteSparseAccessorData(accessor, accessorModel, 
                    denseAccessorData, null); 
            };
            denseBufferViewModel.setSparseSubstitutionCallback(
                sparseSubstitutionCallback);
        }
    }
    
    /**
     * Create a new {@link BufferViewModel} with an associated 
     * {@link BufferModel} that serves as the basis for a sparse accessor, or 
     * an accessor that does not refer to a {@link BufferView})
     * 
     * @param uriString The URI string that will be assigned to the 
     * {@link BufferModel} that is created internally. This string 
     * is not strictly required, but helpful for debugging, at least
     * @param bufferData The buffer data
     * @return The new {@link BufferViewModel}
     */
    private static DefaultBufferViewModel createBufferViewModel(
        String uriString, ByteBuffer bufferData)
    {
        DefaultBufferModel bufferModel = new DefaultBufferModel();
        bufferModel.setUri(uriString);
        bufferModel.setBufferData(bufferData);

        DefaultBufferViewModel bufferViewModel = 
            new DefaultBufferViewModel(null);
        bufferViewModel.setByteOffset(0);
        bufferViewModel.setByteLength(bufferData.capacity());
        bufferViewModel.setBufferModel(bufferModel);
        
        return bufferViewModel;
    }
    
    /**
     * Substitute the sparse accessor data in the given dense 
     * {@link AccessorData} for the given {@link AccessorModel}
     * based on the sparse accessor data that is defined in the given 
     * {@link Accessor}.
     * 
     * @param accessor The {@link Accessor}
     * @param accessorModel The {@link AccessorModel}
     * @param denseAccessorData The dense {@link AccessorData}
     * @param baseAccessorData The optional {@link AccessorData} that contains 
     * the base data. If this is not <code>null</code>, then it will be used 
     * to initialize the {@link AccessorData}, before the sparse data 
     * substitution takes place
     */
    private void substituteSparseAccessorData(
        Accessor accessor, AccessorModel accessorModel, 
        AccessorData denseAccessorData, AccessorData baseAccessorData)
    {
        AccessorSparse accessorSparse = accessor.getSparse();
        int count = accessorSparse.getCount();
        
        AccessorSparseIndices accessorSparseIndices = 
            accessorSparse.getIndices();
        AccessorData sparseIndicesAccessorData = 
            createSparseIndicesAccessorData(accessorSparseIndices, count);
        
        AccessorSparseValues accessorSparseValues = accessorSparse.getValues();
        ElementType elementType = accessorModel.getElementType();
        AccessorData sparseValuesAccessorData =
            createSparseValuesAccessorData(accessorSparseValues, 
                accessorModel.getComponentType(),
                elementType, count);
     
        AccessorSparseUtils.substituteAccessorData(
            denseAccessorData, 
            baseAccessorData, 
            sparseIndicesAccessorData, 
            sparseValuesAccessorData);
    }
    
    
    /**
     * Create the {@link AccessorData} for the given 
     * {@link AccessorSparseIndices}
     * 
     * @param accessorSparseIndices The {@link AccessorSparseIndices}
     * @param count The count from the {@link AccessorSparse} 
     * @return The {@link AccessorData}
     */
    private AccessorData createSparseIndicesAccessorData(
        AccessorSparseIndices accessorSparseIndices, int count)
    {
        Integer componentType = accessorSparseIndices.getComponentType();
        Integer bufferViewIndex = accessorSparseIndices.getBufferView();
        BufferViewModel bufferViewModel = 
            gltfModel.getBufferViewModel(bufferViewIndex);
        ByteBuffer bufferViewData = bufferViewModel.getBufferViewData();
        int byteOffset = Optionals.of(accessorSparseIndices.getByteOffset(), 0);
        return AccessorDatas.create(
            componentType, bufferViewData, byteOffset, 
            count, ElementType.SCALAR, null);
    }
    
    /**
     * Create the {@link AccessorData} for the given 
     * {@link AccessorSparseValues}
     * 
     * @param accessorSparseValues The {@link AccessorSparseValues}
     * @param componentType The component type of the {@link Accessor}
     * @param elementType The {@link ElementType}
     * of the {@link AccessorModel#getElementType() accessor element type}
     * @param count The count from the {@link AccessorSparse} 
     * @return The {@link AccessorData}
     */
    private AccessorData createSparseValuesAccessorData(
        AccessorSparseValues accessorSparseValues, 
        int componentType, ElementType elementType, int count)
    {
        Integer bufferViewIndex = accessorSparseValues.getBufferView();
        BufferViewModel bufferViewModel = 
            gltfModel.getBufferViewModel(bufferViewIndex);
        ByteBuffer bufferViewData = bufferViewModel.getBufferViewData();
        int byteOffset = Optionals.of(accessorSparseValues.getByteOffset(), 0);
        return AccessorDatas.create(
            componentType, bufferViewData, byteOffset, count, 
            elementType, null);
    }
    
    /**
     * Initialize the {@link AnimationModel} instances
     */
    private void initAnimationModels()
    {
        List<Animation> animations = Optionals.of(gltf.getAnimations());
        for (int i = 0; i < animations.size(); i++)
        {
            Animation animation = animations.get(i);
            DefaultAnimationModel animationModel = 
                gltfModel.getAnimationModel(i);
            transferGltfChildOfRootPropertyElements(animation, animationModel);
            
            List<AnimationChannel> channels = 
                Optionals.of(animation.getChannels());
            for (AnimationChannel animationChannel : channels)
            {
                Channel channel = createChannel(animation, animationChannel);
                animationModel.addChannel(channel);
            }
        }
    }
    
    /**
     * Create the {@link Channel} object for the given animation and animation
     * channel
     * 
     * @param animation The {@link Animation}
     * @param animationChannel The {@link AnimationChannel}
     * @return The {@link Channel}
     */
    private Channel createChannel(
        Animation animation, AnimationChannel animationChannel)
    {
        List<AnimationSampler> samplers = 
            Optionals.of(animation.getSamplers());

        int samplerIndex = animationChannel.getSampler();
        AnimationSampler animationSampler = samplers.get(samplerIndex);
        
        int inputAccessorIndex = animationSampler.getInput();
        AccessorModel inputAccessorModel = 
            gltfModel.getAccessorModel(inputAccessorIndex);
        
        int outputAccessorIndex = animationSampler.getOutput();
        AccessorModel outputAccessorModel = 
            gltfModel.getAccessorModel(outputAccessorIndex);
        
        String interpolationString = 
            animationSampler.getInterpolation();
        Interpolation interpolation = 
            interpolationString == null ? Interpolation.LINEAR :
            Interpolation.valueOf(interpolationString);
        
        AnimationModel.Sampler sampler = new DefaultSampler(
            inputAccessorModel, interpolation, outputAccessorModel);
        
        AnimationChannelTarget animationChannelTarget = 
            animationChannel.getTarget();
        
        Integer nodeIndex = animationChannelTarget.getNode();
        NodeModel nodeModel = null;
        if (nodeIndex == null)
        {
            // Should not happen yet. Targets always refer to nodes
            logger.warning("No node index given for animation channel target");
        }
        else
        {
            nodeModel = gltfModel.getNodeModel(nodeIndex);
        }
        String path = animationChannelTarget.getPath();
        
        Channel channel =
            new DefaultChannel(sampler, nodeModel, path);
        return channel;
    }

    /**
     * Initialize the {@link BufferModel} instances
     */
    private void initBufferModels()
    {
        List<Buffer> buffers = Optionals.of(gltf.getBuffers());

        ByteBuffer binaryData = null;
        ByteBuffer b = gltfAsset.getBinaryData();
        if (b != null && b.capacity() > 0)
        {
            binaryData = b;
        }
            
        if (buffers.isEmpty() && binaryData != null)
        {
            logger.warning("Binary data was given, but no buffers");
            return;
        }

        for (int i = 0; i < buffers.size(); i++)
        {
            Buffer buffer = buffers.get(i);
            DefaultBufferModel bufferModel = gltfModel.getBufferModel(i);
            transferGltfChildOfRootPropertyElements(buffer, bufferModel);
            if (i == 0 && binaryData != null)
            {
                bufferModel.setBufferData(binaryData);
            }
            else
            {
                String uri = buffer.getUri();
                if (IO.isDataUriString(uri))
                {
                    byte data[] = IO.readDataUri(uri);
                    ByteBuffer bufferData = Buffers.create(data);
                    bufferModel.setBufferData(bufferData);
                }
                else
                {
                    if (uri == null)
                    {
                        logger.warning("Buffer " + i + " does not have "
                            + "a uri. Binary chunks that are not the main GLB "
                            + "buffer are not supported.");
                    }
                    else
                    {
                        ByteBuffer bufferData = gltfAsset.getReferenceData(uri);
                        bufferModel.setBufferData(bufferData);
                    }
                }
            }
        }
    }
    
    
    /**
     * Initialize the {@link BufferViewModel} instances
     */
    private void initBufferViewModels()
    {
        List<BufferView> bufferViews = Optionals.of(gltf.getBufferViews());
        for (int i = 0; i < bufferViews.size(); i++)
        {
            BufferView bufferView = bufferViews.get(i);
            
            DefaultBufferViewModel bufferViewModel = 
                gltfModel.getBufferViewModel(i);
            transferGltfChildOfRootPropertyElements(
                bufferView, bufferViewModel);
            
            int bufferIndex = bufferView.getBuffer();
            BufferModel bufferModel = gltfModel.getBufferModel(bufferIndex);
            bufferViewModel.setBufferModel(bufferModel);
        }
    }
    

    /**
     * Initialize the {@link MeshModel} instances
     */
    private void initMeshModels()
    {
        List<Mesh> meshes = Optionals.of(gltf.getMeshes());
        for (int i = 0; i < meshes.size(); i++)
        {
            Mesh mesh = meshes.get(i);
            DefaultMeshModel meshModel = gltfModel.getMeshModel(i);
            transferGltfChildOfRootPropertyElements(mesh, meshModel);
            
            List<MeshPrimitive> primitives = 
                Optionals.of(mesh.getPrimitives());
            for (MeshPrimitive meshPrimitive : primitives)
            {
                MeshPrimitiveModel meshPrimitiveModel = 
                    createMeshPrimitiveModel(meshPrimitive);
                meshModel.addMeshPrimitiveModel(meshPrimitiveModel);
            }
        }
    }
    
    /**
     * Create a {@link MeshPrimitiveModel} for the given 
     * {@link MeshPrimitive}.<br>
     * 
     * @param meshPrimitive The {@link MeshPrimitive}
     * @return The {@link MeshPrimitiveModel}
     */
    private DefaultMeshPrimitiveModel createMeshPrimitiveModel(
        MeshPrimitive meshPrimitive)
    {
        Integer mode = Optionals.of(
            meshPrimitive.getMode(), 
            meshPrimitive.defaultMode());
        DefaultMeshPrimitiveModel meshPrimitiveModel = 
            new DefaultMeshPrimitiveModel(mode);
        transferGltfPropertyElements(meshPrimitive, meshPrimitiveModel);
        
        Integer indicesIndex = meshPrimitive.getIndices();
        if (indicesIndex != null)
        {
            AccessorModel indices = gltfModel.getAccessorModel(indicesIndex);
            meshPrimitiveModel.setIndices(indices);
        }
        Map<String, Integer> attributes = 
            Optionals.of(meshPrimitive.getAttributes());
        for (Entry<String, Integer> entry : attributes.entrySet())
        {
            String attributeName = entry.getKey();
            int attributeIndex = entry.getValue();
            AccessorModel attribute = 
                gltfModel.getAccessorModel(attributeIndex);
            meshPrimitiveModel.putAttribute(attributeName, attribute);
        }
        
        List<Map<String, Integer>> morphTargets =
            Optionals.of(meshPrimitive.getTargets());
        for (Map<String, Integer> morphTarget : morphTargets)
        {
            Map<String, AccessorModel> morphTargetModel = 
                new LinkedHashMap<String, AccessorModel>();
            for (Entry<String, Integer> entry : morphTarget.entrySet())
            {
                String attribute = entry.getKey();
                Integer accessorIndex = entry.getValue();
                AccessorModel accessorModel = 
                    gltfModel.getAccessorModel(accessorIndex);
                morphTargetModel.put(attribute, accessorModel);
            }
            meshPrimitiveModel.addTarget(
                Collections.unmodifiableMap(morphTargetModel));
        }
        
        Integer materialIndex = meshPrimitive.getMaterial();
        if (materialIndex != null)
        {
            MaterialModelV2 materialModel = 
                (MaterialModelV2) gltfModel.getMaterialModel(materialIndex);
            meshPrimitiveModel.setMaterialModel(materialModel);
        }
        
        return meshPrimitiveModel;
    }

    /**
     * Initialize the {@link NodeModel} instances
     */
    private void initNodeModels()
    {
        List<Node> nodes = Optionals.of(gltf.getNodes());
        for (int i = 0; i < nodes.size(); i++)
        {
            Node node = nodes.get(i);
            
            DefaultNodeModel nodeModel = gltfModel.getNodeModel(i);
            transferGltfChildOfRootPropertyElements(node, nodeModel);            
            
            List<Integer> childIndices = Optionals.of(node.getChildren());
            for (Integer childIndex : childIndices)
            {
                DefaultNodeModel child = gltfModel.getNodeModel(childIndex);
                nodeModel.addChild(child);
            }
            
            Integer meshIndex = node.getMesh();
            if (meshIndex != null)
            {
                MeshModel meshModel = gltfModel.getMeshModel(meshIndex);
                nodeModel.addMeshModel(meshModel);
            }
            
            Integer skinIndex = node.getSkin();
            if (skinIndex != null)
            {
                SkinModel skinModel = gltfModel.getSkinModel(skinIndex);
                nodeModel.setSkinModel(skinModel);
            }
            
            Integer cameraIndex = node.getCamera();
            if (cameraIndex != null)
            {
                CameraModel cameraModel = gltfModel.getCameraModel(cameraIndex);
                nodeModel.setCameraModel(cameraModel);
            }
            
            float matrix[] = node.getMatrix();
            float translation[] = node.getTranslation();
            float rotation[] = node.getRotation();
            float scale[] = node.getScale();
            nodeModel.setMatrix(Optionals.clone(matrix));
            nodeModel.setTranslation(Optionals.clone(translation));
            nodeModel.setRotation(Optionals.clone(rotation));
            nodeModel.setScale(Optionals.clone(scale));
            
            List<Float> weights = node.getWeights();
            if (weights != null)
            {
                float weightsArray[] = new float[weights.size()];
                for (int j = 0; j < weights.size(); j++)
                {
                    weightsArray[j] = weights.get(j);
                }
                nodeModel.setWeights(weightsArray);
            }
        }
    }
    

    /**
     * Initialize the {@link SceneModel} instances
     */
    private void initSceneModels()
    {
        List<Scene> scenes = Optionals.of(gltf.getScenes());
        for (int i = 0; i < scenes.size(); i++)
        {
            Scene scene = scenes.get(i);

            DefaultSceneModel sceneModel = gltfModel.getSceneModel(i);
            transferGltfChildOfRootPropertyElements(scene, sceneModel);            
            
            List<Integer> nodeIndices = Optionals.of(scene.getNodes());
            for (Integer nodeIndex : nodeIndices)
            {
                NodeModel nodeModel = gltfModel.getNodeModel(nodeIndex);
                sceneModel.addNode(nodeModel);
            }
        }
    }
    
    /**
     * Initialize the {@link SkinModel} instances
     */
    private void initSkinModels()
    {
        List<Skin> skins = Optionals.of(gltf.getSkins());
        for (int i = 0; i < skins.size(); i++)
        {
            Skin skin = skins.get(i);
            DefaultSkinModel skinModel = gltfModel.getSkinModel(i);
            transferGltfChildOfRootPropertyElements(skin, skinModel);
            
            List<Integer> jointIndices = skin.getJoints();
            for (Integer jointIndex : jointIndices)
            {
                NodeModel jointNodeModel = gltfModel.getNodeModel(jointIndex);
                skinModel.addJoint(jointNodeModel);
            }
            
            Integer inverseBindMatricesIndex = skin.getInverseBindMatrices();
            AccessorModel inverseBindMatrices = 
                gltfModel.getAccessorModel(inverseBindMatricesIndex);
            skinModel.setInverseBindMatrices(inverseBindMatrices);
        }
    }
    
    /**
     * Initialize the {@link TextureModel} instances
     */
    private void initTextureModels()
    {
        List<Texture> textures = Optionals.of(gltf.getTextures());
        for (int i = 0; i < textures.size(); i++)
        {
            Texture texture = textures.get(i);
            DefaultTextureModel textureModel = gltfModel.getTextureModel(i);
            transferGltfChildOfRootPropertyElements(texture, textureModel);
            
            // The source may be null when the image data is provided
            // by an extension.
            Integer imageIndex = texture.getSource();
            if (imageIndex != null)
            {
                DefaultImageModel imageModel = 
                    gltfModel.getImageModel(imageIndex);
                textureModel.setImageModel(imageModel);
            }
        }
    }
    
    /**
     * Initialize the {@link ImageModel} instances
     */
    private void initImageModels()
    {
        List<Image> images = Optionals.of(gltf.getImages());
        for (int i = 0; i < images.size(); i++)
        {
            Image image = images.get(i);
            DefaultImageModel imageModel = gltfModel.getImageModel(i);
            transferGltfChildOfRootPropertyElements(image, imageModel);
            
            Integer bufferViewIndex = image.getBufferView();
            if (bufferViewIndex != null)
            {
                BufferViewModel bufferViewModel = 
                    gltfModel.getBufferViewModel(bufferViewIndex);
                imageModel.setBufferViewModel(bufferViewModel);
            }
            else
            {
                String uri = image.getUri();
                if (IO.isDataUriString(uri))
                {
                    byte data[] = IO.readDataUri(uri);
                    ByteBuffer imageData = Buffers.create(data);
                    imageModel.setImageData(imageData);
                }
                else
                {
                    ByteBuffer imageData = gltfAsset.getReferenceData(uri);
                    imageModel.setImageData(imageData);
                }
            }
        }
    }
    
    /**
     * Initialize the {@link MaterialModel} instances
     */
    private void initMaterialModels()
    {
        List<Material> materials = Optionals.of(gltf.getMaterials());
        for (int i = 0; i < materials.size(); i++)
        {
            Material material = materials.get(i);
            MaterialModelV2 materialModel = 
                (MaterialModelV2) gltfModel.getMaterialModel(i);
            
            transferGltfChildOfRootPropertyElements(material, materialModel);            
            initMaterialModel(materialModel, material);
        }
    }
    
    /**
     * Initialize the given {@link MaterialModelV2} based on the given
     * {@link Material}
     * 
     * @param materialModel The {@link MaterialModelV2}
     * @param material The {@link Material}
     */
    private void initMaterialModel(
        MaterialModelV2 materialModel, Material material)
    {
        MaterialPbrMetallicRoughness pbrMetallicRoughness = 
            material.getPbrMetallicRoughness();
        if (pbrMetallicRoughness == null)
        {
            pbrMetallicRoughness = 
                Materials.createDefaultMaterialPbrMetallicRoughness();
        }
        
        String alphaModeString = material.getAlphaMode();
        if (alphaModeString != null)
        {
            materialModel.setAlphaMode(AlphaMode.valueOf(alphaModeString));
        }
        materialModel.setAlphaCutoff(
            Optionals.of(material.getAlphaCutoff(), 0.5f));
        
        materialModel.setDoubleSided(
            Boolean.TRUE.equals(material.isDoubleSided()));
        
        TextureInfo baseColorTextureInfo = 
            pbrMetallicRoughness.getBaseColorTexture();
        if (baseColorTextureInfo != null)
        {
            int index = baseColorTextureInfo.getIndex();
            TextureModel textureModel = gltfModel.getTextureModel(index);
            materialModel.setBaseColorTexture(textureModel);
            materialModel.setBaseColorTexcoord(
                baseColorTextureInfo.getTexCoord());
        }
        float[] baseColorFactor = Optionals.of(
            pbrMetallicRoughness.getBaseColorFactor(),
            pbrMetallicRoughness.defaultBaseColorFactor());
        materialModel.setBaseColorFactor(baseColorFactor);
        
        TextureInfo metallicRoughnessTextureInfo = 
            pbrMetallicRoughness.getMetallicRoughnessTexture();
        if (metallicRoughnessTextureInfo != null)
        {
            int index = metallicRoughnessTextureInfo.getIndex();
            TextureModel textureModel = gltfModel.getTextureModel(index);
            materialModel.setMetallicRoughnessTexture(textureModel);
            materialModel.setMetallicRoughnessTexcoord(
                metallicRoughnessTextureInfo.getTexCoord());
        }
        float metallicFactor = Optionals.of(
            pbrMetallicRoughness.getMetallicFactor(),
            pbrMetallicRoughness.defaultMetallicFactor());
        materialModel.setMetallicFactor(metallicFactor);
        
        float roughnessFactor = Optionals.of(
            pbrMetallicRoughness.getRoughnessFactor(),
            pbrMetallicRoughness.defaultRoughnessFactor());
        materialModel.setRoughnessFactor(roughnessFactor);
        
        MaterialNormalTextureInfo normalTextureInfo = 
            material.getNormalTexture();
        if (normalTextureInfo != null)
        {
            int index = normalTextureInfo.getIndex();
            TextureModel textureModel = gltfModel.getTextureModel(index);
            materialModel.setNormalTexture(textureModel);
            materialModel.setNormalTexcoord(
                normalTextureInfo.getTexCoord());
            
            float normalScale = Optionals.of(
                normalTextureInfo.getScale(),
                normalTextureInfo.defaultScale());
            materialModel.setNormalScale(normalScale);
        }

        MaterialOcclusionTextureInfo occlusionTextureInfo = 
            material.getOcclusionTexture();
        if (occlusionTextureInfo != null)
        {
            int index = occlusionTextureInfo.getIndex();
            TextureModel textureModel = gltfModel.getTextureModel(index);
            materialModel.setOcclusionTexture(textureModel);
            materialModel.setOcclusionTexcoord(
                occlusionTextureInfo.getTexCoord());
            
            float occlusionStrength = Optionals.of(
                occlusionTextureInfo.getStrength(),
                occlusionTextureInfo.defaultStrength());
            materialModel.setOcclusionStrength(occlusionStrength);
        }

        TextureInfo emissiveTextureInfo = 
            material.getEmissiveTexture();
        if (emissiveTextureInfo != null)
        {
            int index = emissiveTextureInfo.getIndex();
            TextureModel textureModel = gltfModel.getTextureModel(index);
            materialModel.setEmissiveTexture(textureModel);
            materialModel.setEmissiveTexcoord(
                emissiveTextureInfo.getTexCoord());
        }
        
        float[] emissiveFactor = Optionals.of(
            material.getEmissiveFactor(),
            material.defaultEmissiveFactor());
        materialModel.setEmissiveFactor(emissiveFactor);
    }

    /**
     * Transfer the extensions and extras from the given property to
     * the given target
     * 
     * @param property The property
     * @param modelElement The target
     */
    private static void transferGltfPropertyElements(
        GlTFProperty property, AbstractModelElement modelElement)
    {
        modelElement.setExtensions(property.getExtensions());
        modelElement.setExtras(property.getExtras());
    }
    
    /**
     * Transfer the name and extensions and extras from the given property to
     * the given target
     * 
     * @param property The property
     * @param modelElement The target
     */
    private static void transferGltfChildOfRootPropertyElements(
        GlTFChildOfRootProperty property, 
        AbstractNamedModelElement modelElement)
    {
        modelElement.setName(property.getName());
        transferGltfPropertyElements(property, modelElement);
    }
    
}
