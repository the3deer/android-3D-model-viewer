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
package de.javagl.jgltf.model.v1;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.logging.Logger;

import de.javagl.jgltf.impl.v1.Accessor;
import de.javagl.jgltf.impl.v1.Animation;
import de.javagl.jgltf.impl.v1.AnimationChannel;
import de.javagl.jgltf.impl.v1.AnimationChannelTarget;
import de.javagl.jgltf.impl.v1.AnimationSampler;
import de.javagl.jgltf.impl.v1.Buffer;
import de.javagl.jgltf.impl.v1.BufferView;
import de.javagl.jgltf.impl.v1.Camera;
import de.javagl.jgltf.impl.v1.CameraOrthographic;
import de.javagl.jgltf.impl.v1.CameraPerspective;
import de.javagl.jgltf.impl.v1.GlTF;
import de.javagl.jgltf.impl.v1.GlTFChildOfRootProperty;
import de.javagl.jgltf.impl.v1.GlTFProperty;
import de.javagl.jgltf.impl.v1.Image;
import de.javagl.jgltf.impl.v1.Material;
import de.javagl.jgltf.impl.v1.Mesh;
import de.javagl.jgltf.impl.v1.MeshPrimitive;
import de.javagl.jgltf.impl.v1.Node;
import de.javagl.jgltf.impl.v1.Program;
import de.javagl.jgltf.impl.v1.Sampler;
import de.javagl.jgltf.impl.v1.Scene;
import de.javagl.jgltf.impl.v1.Shader;
import de.javagl.jgltf.impl.v1.Skin;
import de.javagl.jgltf.impl.v1.Technique;
import de.javagl.jgltf.impl.v1.TechniqueParameters;
import de.javagl.jgltf.impl.v1.TechniqueStates;
import de.javagl.jgltf.impl.v1.TechniqueStatesFunctions;
import de.javagl.jgltf.impl.v1.Texture;
import de.javagl.jgltf.model.AccessorDatas;
import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.Accessors;
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
import de.javagl.jgltf.model.gl.ProgramModel;
import de.javagl.jgltf.model.gl.ShaderModel;
import de.javagl.jgltf.model.gl.ShaderModel.ShaderType;
import de.javagl.jgltf.model.gl.TechniqueModel;
import de.javagl.jgltf.model.gl.TechniqueParametersModel;
import de.javagl.jgltf.model.gl.TechniqueStatesModel;
import de.javagl.jgltf.model.gl.impl.DefaultProgramModel;
import de.javagl.jgltf.model.gl.impl.DefaultShaderModel;
import de.javagl.jgltf.model.gl.impl.DefaultTechniqueModel;
import de.javagl.jgltf.model.gl.impl.DefaultTechniqueParametersModel;
import de.javagl.jgltf.model.gl.impl.DefaultTechniqueStatesFunctionsModel;
import de.javagl.jgltf.model.gl.impl.DefaultTechniqueStatesModel;
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
import de.javagl.jgltf.model.io.v1.GltfAssetV1;
import de.javagl.jgltf.model.v1.gl.DefaultModels;
import de.javagl.jgltf.model.v1.gl.GltfDefaults;
import de.javagl.jgltf.model.v1.gl.TechniqueStatesFunctionsModels;

/**
 * A class that is responsible for filling a {@link DefaultGltfModel} with
 * the model instances that are created from a {@link GltfAssetV1}
 */
class GltfModelCreatorV1
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(GltfModelCreatorV1.class.getName());
    
    /**
     * The {@link IndexMappingSet}
     */
    private final IndexMappingSet indexMappingSet;
    
    /**
     * The {@link GltfAsset} of this model
     */
    private final GltfAsset gltfAsset;
    
    /**
     * The {@link GlTF} of this model
     */
    private final GlTF gltf;
    
    /**
     * The {@link GltfModel} that is built
     */
    private final GltfModelV1 gltfModel;
    
    /**
     * Creates a new model for the given glTF
     * 
     * @param gltfAsset The {@link GltfAssetV1}
     * @param gltfModel The {@link GltfModel}
     */
    GltfModelCreatorV1(
        GltfAssetV1 gltfAsset, GltfModelV1 gltfModel)
    {
        this.gltfAsset = Objects.requireNonNull(gltfAsset, 
            "The gltfAsset may not be null");
        this.gltf = gltfAsset.getGltf();
        this.gltfModel = Objects.requireNonNull(gltfModel, 
            "The gltfModel may not be null");
        this.indexMappingSet = IndexMappingSets.create(gltfAsset.getGltf());
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
        createShaderModels();
        createProgramModels();
        createTechniqueModels();
        
        initBufferModels();
        initBufferViewModels();
        
        initAccessorModels();
        
        assignBufferViewByteStrides();
        
        initAnimationModels();
        initImageModels();
        initTechniqueModels();
        initMaterialModels();
        initMeshModels();
        initNodeModels();
        initSceneModels();
        initSkinModels();
        initTextureModels();
        initShaderModels();
        initProgramModels();
    }
    
    /**
     * Create the {@link AccessorModel} instances
     */
    private void createAccessorModels()
    {
        Map<String, Accessor> accessors = Optionals.of(gltf.getAccessors());
        for (Accessor accessor : accessors.values())
        {
            DefaultAccessorModel accessorModel = createAccessorModel(accessor);
            gltfModel.addAccessorModel(accessorModel);
        }
    }

    /**
     * Create a {@link DefaultAccessorModel} for the given {@link Accessor}
     * 
     * @param accessor The {@link Accessor}
     * @return The {@link AccessorModel}
     */
    private static DefaultAccessorModel createAccessorModel(Accessor accessor)
    {
        Integer componentType = accessor.getComponentType();
        Integer byteOffset = accessor.getByteOffset();
        Integer count = accessor.getCount();
        ElementType elementType = ElementType.forString(accessor.getType());
        Integer byteStride = accessor.getByteStride();
        if (byteStride == null)
        {
            byteStride = elementType.getNumComponents() *
                Accessors.getNumBytesForAccessorComponentType(
                    componentType);
        }
        DefaultAccessorModel accessorModel = new DefaultAccessorModel(
            componentType, count, elementType);
        accessorModel.setByteOffset(byteOffset);
        accessorModel.setByteStride(byteStride);
        return accessorModel;
    }

    /**
     * Create the {@link AnimationModel} instances
     */
    private void createAnimationModels()
    {
        Map<String, Animation> animations = Optionals.of(gltf.getAnimations());
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
        Map<String, Buffer> buffers = Optionals.of(gltf.getBuffers());
        for (Buffer buffer : buffers.values())
        {
            DefaultBufferModel bufferModel = new DefaultBufferModel();
            bufferModel.setUri(buffer.getUri());
            gltfModel.addBufferModel(bufferModel);
        }
    }
    
    /**
     * Create the {@link BufferViewModel} instances
     */
    private void createBufferViewModels()
    {
        Map<String, BufferView> bufferViews = 
            Optionals.of(gltf.getBufferViews());
        for (BufferView bufferView : bufferViews.values())
        {
            DefaultBufferViewModel bufferViewModel = 
                createBufferViewModel(bufferView);
            gltfModel.addBufferViewModel(bufferViewModel);
        }
    }
    
    /**
     * Create the {@link CameraModel} instances
     */
    private void createCameraModels()
    {
        Map<String, Camera> cameras = 
            Optionals.of(gltf.getCameras());
        for (Camera camera : cameras.values())
        {
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
     * Create a {@link DefaultBufferViewModel} for the given {@link BufferView}
     * 
     * @param bufferView The {@link BufferView}
     * @return The {@link BufferViewModel}
     */
    private static DefaultBufferViewModel createBufferViewModel(
        BufferView bufferView)
    {
        int byteOffset = bufferView.getByteOffset();
        Integer byteLength = bufferView.getByteLength();
        if (byteLength == null)
        {
            logger.warning("No byteLength found in BufferView");
            byteLength = 0;
        }
        Integer target = bufferView.getTarget();
        DefaultBufferViewModel bufferViewModel = 
            new DefaultBufferViewModel(target);
        bufferViewModel.setByteOffset(byteOffset);
        bufferViewModel.setByteLength(byteLength);
        return bufferViewModel;
    }
    
    /**
     * Create the {@link ImageModel} instances
     */
    private void createImageModels()
    {
        Map<String, Image> images = 
            Optionals.of(gltf.getImages());
        for (Image image : images.values())
        {
            DefaultImageModel imageModel = 
                new DefaultImageModel();
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
        Map<String, Material> materials = Optionals.of(gltf.getMaterials());
        for (int i = 0; i < materials.size(); i++)
        {
            gltfModel.addMaterialModel(new MaterialModelV1());
        }
    }
    
    /**
     * Create the {@link MeshModel} instances
     */
    private void createMeshModels()
    {
        Map<String, Mesh> meshes = Optionals.of(gltf.getMeshes());
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
        Map<String, Node> nodes = Optionals.of(gltf.getNodes());
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
        Map<String, Scene> scenes = Optionals.of(gltf.getScenes());
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
        Map<String, Skin> skins = Optionals.of(gltf.getSkins());
        for (Entry<String, Skin> entry : skins.entrySet())
        {
            Skin skin = entry.getValue();
            float[] bindShapeMatrix = skin.getBindShapeMatrix();
            DefaultSkinModel skinModel = new DefaultSkinModel();
            skinModel.setBindShapeMatrix(bindShapeMatrix);
            gltfModel.addSkinModel(skinModel);
        }
    }

    /**
     * Create the {@link TextureModel} instances
     */
    private void createTextureModels()
    {
        Map<String, Texture> textures = Optionals.of(gltf.getTextures());
        Map<String, Sampler> samplers = Optionals.of(gltf.getSamplers());
        for (Entry<String, Texture> entry : textures.entrySet())
        {
            Texture texture = entry.getValue();
            String samplerId = texture.getSampler();
            Sampler sampler = samplers.get(samplerId);
            
            int magFilter = Optionals.of(
                sampler.getMagFilter(), sampler.defaultMagFilter());
            int minFilter = Optionals.of(
                sampler.getMinFilter(), sampler.defaultMinFilter());
            int wrapS = Optionals.of(
                sampler.getWrapS(), sampler.defaultWrapS());
            int wrapT = Optionals.of(
                sampler.getWrapT(), sampler.defaultWrapT());
            
            DefaultTextureModel textureModel = new DefaultTextureModel();
            textureModel.setMagFilter(magFilter);
            textureModel.setMinFilter(minFilter);
            textureModel.setWrapS(wrapS);
            textureModel.setWrapT(wrapT);
            gltfModel.addTextureModel(textureModel);
        }
    }
    
    /**
     * Create the {@link ShaderModel} instances
     */
    private void createShaderModels()
    {
        Map<String, Shader> shaders = Optionals.of(gltf.getShaders());
        for (Entry<String, Shader> entry : shaders.entrySet())
        {
            Shader shader = entry.getValue();
            Integer type = shader.getType();
            ShaderType shaderType = null;
            if (type == GltfConstants.GL_VERTEX_SHADER)
            {
                shaderType = ShaderType.VERTEX_SHADER;
            }
            else 
            {
                shaderType = ShaderType.FRAGMENT_SHADER;
            }
            DefaultShaderModel shaderModel =
                new DefaultShaderModel(shader.getUri(), shaderType);
            gltfModel.addShaderModel(shaderModel);
        }
    }

    /**
     * Create the {@link ProgramModel} instances
     */
    private void createProgramModels()
    {
        Map<String, Program> programs = Optionals.of(gltf.getPrograms());
        for (int i = 0; i < programs.size(); i++)
        {
            gltfModel.addProgramModel(new DefaultProgramModel());
        }
    }
    
    /**
     * Create the {@link TechniqueModel} instances
     */
    private void createTechniqueModels()
    {
        Map<String, Technique> techniques = Optionals.of(gltf.getTechniques());
        for (int i = 0; i < techniques.size(); i++)
        {
            gltfModel.addTechniqueModel(new DefaultTechniqueModel());
        }
    }
    

    /**
     * Initialize the {@link AccessorModel} instances
     */
    private void initAccessorModels()
    {
        Map<String, Accessor> accessors = Optionals.of(gltf.getAccessors());
        for (Entry<String, Accessor> entry : accessors.entrySet())
        {
            String accessorId = entry.getKey();
            Accessor accessor = entry.getValue();
            String bufferViewId = accessor.getBufferView();
            BufferViewModel bufferViewModel = 
                get("bufferViews", bufferViewId, 
                    gltfModel::getBufferViewModel);
            DefaultAccessorModel accessorModel =
                get("accessors", accessorId, 
                    gltfModel::getAccessorModel);
            
            transferGltfChildOfRootPropertyElements(accessor, accessorModel);
            accessorModel.setBufferViewModel(bufferViewModel);
            accessorModel.setAccessorData(AccessorDatas.create(accessorModel));
        }
    }

    /**
     * Initialize the {@link AnimationModel} instances
     */
    private void initAnimationModels()
    {
        Map<String, Animation> animations = Optionals.of(gltf.getAnimations());
        for (Entry<String, Animation> entry : animations.entrySet())
        {
            String animationId = entry.getKey();
            Animation animation = entry.getValue();
            DefaultAnimationModel animationModel =
                get("animations", animationId, 
                    gltfModel::getAnimationModel);
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
     * Initialize the {@link ImageModel} instances
     */
    private void initImageModels()
    {
        Map<String, Image> images = Optionals.of(gltf.getImages());
        for (Entry<String, Image> entry : images.entrySet())
        {
            String imageId = entry.getKey();
            Image image = entry.getValue();
            DefaultImageModel imageModel =
                get("images", imageId, gltfModel::getImageModel);
            transferGltfChildOfRootPropertyElements(image, imageModel);
            
            if (BinaryGltfV1.hasBinaryGltfExtension(image))
            {
                String bufferViewId = 
                    BinaryGltfV1.getBinaryGltfBufferViewId(image);
                BufferViewModel bufferViewModel =
                    get("bufferViews", bufferViewId, 
                        gltfModel::getBufferViewModel);
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
        Map<String, String> parameters = 
            Optionals.of(animation.getParameters());
        Map<String, AnimationSampler> samplers = 
            Optionals.of(animation.getSamplers());

        String samplerId = animationChannel.getSampler();
        AnimationSampler animationSampler = samplers.get(samplerId);
        
        String inputParameterId = animationSampler.getInput();
        String inputAccessorId = parameters.get(inputParameterId);
        if (inputAccessorId == null)
        {
            // This was valid for a short time, when glTF 2.0 was still 
            // called glTF 1.1. The check here is not perfectly reliable, 
            // but there should be a decreasing number of glTF 1.0 models 
            // out there, and even fewer glTF 1.1 ones.
            logger.warning(
                "Assuming " + inputParameterId + " to be an accessor ID");
            inputAccessorId = inputParameterId;
        }
        AccessorModel inputAccessorModel = 
            get("accessors", inputAccessorId, gltfModel::getAccessorModel);
        
        String outputParameterId = animationSampler.getOutput();
        String outputAccessorId = parameters.get(outputParameterId);
        if (outputAccessorId == null)
        {
            // This was valid for a short time, when glTF 2.0 was still 
            // called glTF 1.1. The check here is not perfectly reliable, 
            // but there should be a decreasing number of glTF 1.0 models 
            // out there, and even fewer glTF 1.1 ones.
            logger.warning(
                "Assuming " + outputParameterId + " to be an accessor ID");
            outputAccessorId = outputParameterId;
        }
        AccessorModel outputAccessorModel = 
            get("accessors", outputAccessorId, gltfModel::getAccessorModel);
        
        String interpolationString = 
            animationSampler.getInterpolation();
        Interpolation interpolation = 
            interpolationString == null ? Interpolation.LINEAR :
            Interpolation.valueOf(interpolationString);
        
        AnimationModel.Sampler sampler = new DefaultSampler(
            inputAccessorModel, interpolation, outputAccessorModel);
        
        AnimationChannelTarget animationChannelTarget = 
            animationChannel.getTarget();
        String nodeId = animationChannelTarget.getId();
        String path = animationChannelTarget.getPath();
        
        NodeModel nodeModel = get("nodes", nodeId, gltfModel::getNodeModel);
        
        Channel channel =
            new DefaultChannel(sampler, nodeModel, path);
        return channel;
    }

    /**
     * Initialize the {@link BufferModel} instances
     */
    private void initBufferModels()
    {
        ByteBuffer binaryData = null;
        ByteBuffer b = gltfAsset.getBinaryData();
        if (b != null && b.capacity() > 0)
        {
            binaryData = b;
        }
        
        Map<String, Buffer> buffers = Optionals.of(gltf.getBuffers());
        for (Entry<String, Buffer> entry : buffers.entrySet())
        {
            String bufferId = entry.getKey();
            Buffer buffer = entry.getValue();
            DefaultBufferModel bufferModel = 
                get("buffers", bufferId, gltfModel::getBufferModel);
            transferGltfChildOfRootPropertyElements(buffer, bufferModel);
            
            if (BinaryGltfV1.isBinaryGltfBufferId(bufferId))
            {
                if (binaryData == null)
                {
                    logger.severe("The glTF contains a buffer with the binary"
                        + " buffer ID, but no binary data has been given");
                    continue;
                }
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
                    ByteBuffer bufferData = gltfAsset.getReferenceData(uri);
                    bufferModel.setBufferData(bufferData);
                }
            }
        }
    }
    
    
    /**
     * Initialize the {@link BufferViewModel} instances
     */
    private void initBufferViewModels()
    {
        Map<String, BufferView> bufferViews = 
            Optionals.of(gltf.getBufferViews());
        for (Entry<String, BufferView> entry : bufferViews.entrySet())
        {
            String bufferViewId = entry.getKey();
            BufferView bufferView = entry.getValue();
            
            String bufferId = bufferView.getBuffer();
            BufferModel bufferModel = 
                get("buffers", bufferId, gltfModel::getBufferModel);
            DefaultBufferViewModel bufferViewModel = 
                get("bufferViews", bufferViewId, gltfModel::getBufferViewModel);
            transferGltfChildOfRootPropertyElements(
                bufferView, bufferViewModel);
            bufferViewModel.setBufferModel(bufferModel);
        }
    }
    
    /**
     * Compute all {@link AccessorModel} instances that refer to the
     * given {@link BufferViewModel}
     * 
     * @param bufferViewModel The {@link BufferViewModel}
     * @return The list of {@link AccessorModel} instances
     */
    private List<DefaultAccessorModel> computeAccessorModelsOf(
        BufferViewModel bufferViewModel)
    {
        List<DefaultAccessorModel> result = 
            new ArrayList<DefaultAccessorModel>();
        int n = gltfModel.getAccessorModels().size();
        for (int i = 0; i < n; i++)
        {
            DefaultAccessorModel accessorModel = gltfModel.getAccessorModel(i);
            BufferViewModel b = accessorModel.getBufferViewModel();
            if (bufferViewModel.equals(b))
            {
                result.add(accessorModel);
            }
        }
        return result;
    }
    
    /**
     * Computes the {@link AccessorModel#getByteStride() byte stride} of
     * the given {@link AccessorModel} instances. If the given instances
     * do not have the same byte stride, then a warning will be printed.
     * 
     * @param accessorModels The {@link AccessorModel} instances
     * @return The common byte stride
     */
    private static int computeCommonByteStride(
        Iterable<? extends AccessorModel> accessorModels)
    {
        int commonByteStride = -1;
        for (AccessorModel accessorModel : accessorModels)
        {
            int byteStride = accessorModel.getByteStride();
            if (commonByteStride == -1)
            {
                commonByteStride = byteStride;
            }
            else
            {
                if (commonByteStride != byteStride)
                {
                    logger.warning("The accessor models do not have the "
                        + "same byte stride: " + commonByteStride 
                        + " and " + byteStride);
                }
            }
        }
        return commonByteStride;
    }
    

    /**
     * Set the {@link BufferViewModel#getByteStride() byte strides} of all
     * {@link BufferViewModel} instances, depending on the 
     * {@link AccessorModel} instances that refer to them
     */
    private void assignBufferViewByteStrides()
    {
        int n = gltfModel.getBufferModels().size();
        for (int i = 0; i < n; i++)
        {
            DefaultBufferViewModel bufferViewModel = 
                gltfModel.getBufferViewModel(i);
            List<DefaultAccessorModel> accessorModelsOfBufferView = 
                computeAccessorModelsOf(bufferViewModel);
            if (accessorModelsOfBufferView.size() > 1)
            {
                int byteStride = 
                    computeCommonByteStride(accessorModelsOfBufferView);
                bufferViewModel.setByteStride(byteStride);
            }
        }
    }
    
    /**
     * Initialize the {@link MeshModel} instances
     */
    private void initMeshModels()
    {
        Map<String, Mesh> meshes = 
            Optionals.of(gltf.getMeshes());
        for (Entry<String, Mesh> entry : meshes.entrySet())
        {
            String meshId = entry.getKey();
            Mesh mesh = entry.getValue();
            List<MeshPrimitive> primitives = 
                Optionals.of(mesh.getPrimitives());
            DefaultMeshModel meshModel = 
                get("meshes", meshId, gltfModel::getMeshModel);
            transferGltfChildOfRootPropertyElements(mesh, meshModel);

            for (MeshPrimitive meshPrimitive : primitives)
            {
                MeshPrimitiveModel meshPrimitiveModel = 
                    createMeshPrimitiveModel(meshPrimitive);
                meshModel.addMeshPrimitiveModel(meshPrimitiveModel);
            }
        }
    }
    
    /**
     * Create a {@link MeshPrimitiveModel} for the given {@link MeshPrimitive}
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
        
        String indicesId = meshPrimitive.getIndices();
        if (indicesId != null)
        {
            AccessorModel indices = 
                get("accessors", indicesId, gltfModel::getAccessorModel);
            meshPrimitiveModel.setIndices(indices);
        }
        Map<String, String> attributes = 
            Optionals.of(meshPrimitive.getAttributes());
        for (Entry<String, String> entry : attributes.entrySet())
        {
            String attributeName = entry.getKey();
            String attributeId = entry.getValue();
            
            AccessorModel attribute = 
                get("accessors", attributeId, gltfModel::getAccessorModel);
            meshPrimitiveModel.putAttribute(attributeName, attribute);
        }
        
        String materialId = meshPrimitive.getMaterial();
        if (materialId == null ||
            GltfDefaults.isDefaultMaterialId(materialId))
        {
            meshPrimitiveModel.setMaterialModel(
                DefaultModels.getDefaultMaterialModel());
        }
        else
        {
            MaterialModel materialModel = 
                get("materials", materialId, gltfModel::getMaterialModel);
            meshPrimitiveModel.setMaterialModel(materialModel);
        }
        
        return meshPrimitiveModel;
    }

    /**
     * Initialize the {@link NodeModel} instances
     */
    private void initNodeModels()
    {
        Map<String, Node> nodes = Optionals.of(gltf.getNodes());
        for (Entry<String, Node> entry : nodes.entrySet())
        {
            String nodeId = entry.getKey();
            Node node = entry.getValue();
            
            DefaultNodeModel nodeModel = 
                get("nodes", nodeId, gltfModel::getNodeModel);
            transferGltfChildOfRootPropertyElements(node, nodeModel);
            
            List<String> childIds = Optionals.of(node.getChildren());
            for (String childId : childIds)
            {
                DefaultNodeModel child = 
                    get("nodes", childId, gltfModel::getNodeModel);
                nodeModel.addChild(child);
            }
            List<String> meshIds = Optionals.of(node.getMeshes());
            for (String meshId : meshIds)
            {
                MeshModel meshModel = 
                    get("meshes", meshId, gltfModel::getMeshModel);
                nodeModel.addMeshModel(meshModel);
            }
            String skinId = node.getSkin();
            if (skinId != null)
            {
                SkinModel skinModel = 
                    get("skins", skinId, gltfModel::getSkinModel);
                nodeModel.setSkinModel(skinModel);
            }
            String cameraId = node.getCamera();
            if (cameraId != null)
            {
                CameraModel cameraModel = 
                    get("cameras", cameraId, gltfModel::getCameraModel);
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
        }
    }
    
    /**
     * Initialize the {@link SceneModel} instances
     */
    private void initSceneModels()
    {
        Map<String, Scene> scenes = Optionals.of(gltf.getScenes());
        for (Entry<String, Scene> entry : scenes.entrySet())
        {
            String sceneId = entry.getKey();
            Scene scene = entry.getValue();

            DefaultSceneModel sceneModel =
                get("scenes", sceneId, gltfModel::getSceneModel);
            transferGltfChildOfRootPropertyElements(scene, sceneModel);
            
            List<String> nodes = Optionals.of(scene.getNodes());
            for (String nodeId : nodes)
            {
                NodeModel nodeModel = 
                    get("nodes", nodeId, gltfModel::getNodeModel);
                sceneModel.addNode(nodeModel);
            }
        }
    }
    
    /**
     * Compute the mapping from joint names to the ID of the {@link Node} with
     * the respective {@link Node#getJointName() joint name}
     * 
     * @param gltf The {@link GlTF}
     * @return The mapping
     */
    private static Map<String, String> computeJointNameToNodeIdMap(GlTF gltf)
    {
        Map<String, String> map = new LinkedHashMap<String, String>();
        Map<String, Node> nodes = Optionals.of(gltf.getNodes());
        for (Entry<String, Node> entry : nodes.entrySet())
        {
            String nodeId = entry.getKey();
            Node node = entry.getValue();
            if (node.getJointName() != null)
            {
                String oldNodeId = map.put(node.getJointName(), nodeId);
                if (oldNodeId != null)
                {
                    logger.warning("Joint name " + node.getJointName()
                        + " is mapped to nodes with IDs " + nodeId + " and "
                        + oldNodeId);
                }
            }
        }
        return map;
    }

    /**
     * Initialize the {@link SkinModel} instances
     */
    private void initSkinModels()
    {
        Map<String, String> jointNameToNodeIdMap = 
            computeJointNameToNodeIdMap(gltf);
        Map<String, Skin> skins = Optionals.of(gltf.getSkins());
        for (Entry<String, Skin> entry : skins.entrySet())
        {
            String skinId = entry.getKey();
            Skin skin = entry.getValue();
            DefaultSkinModel skinModel = 
                get("skins", skinId, gltfModel::getSkinModel);
            transferGltfChildOfRootPropertyElements(skin, skinModel);
            
            List<String> jointNames = skin.getJointNames();
            for (String jointName : jointNames)
            {
                String nodeId = jointNameToNodeIdMap.get(jointName);
                NodeModel nodeModel = 
                    get("nodes", nodeId, gltfModel::getNodeModel);
                skinModel.addJoint(nodeModel);
            }
            
            String inverseBindMatricesId = skin.getInverseBindMatrices();
            AccessorModel inverseBindMatrices =
                get("accessors", inverseBindMatricesId, 
                    gltfModel::getAccessorModel);
            skinModel.setInverseBindMatrices(inverseBindMatrices);
        }
    }
    
    /**
     * Initialize the {@link TextureModel} instances
     */
    private void initTextureModels()
    {
        Map<String, Texture> textures = Optionals.of(gltf.getTextures());
        for (Entry<String, Texture> entry : textures.entrySet())
        {
            String textureId = entry.getKey();
            Texture texture = entry.getValue();
            DefaultTextureModel textureModel = 
                get("textures", textureId, gltfModel::getTextureModel);
            transferGltfChildOfRootPropertyElements(texture, textureModel);
            
            String imageId = texture.getSource();
            DefaultImageModel imageModel = 
                get("images", imageId, gltfModel::getImageModel);
            textureModel.setImageModel(imageModel);
        }
    }
    
    /**
     * Initialize the {@link ShaderModel} instances
     */
    private void initShaderModels()
    {
        Map<String, Shader> shaders = Optionals.of(gltf.getShaders());
        for (Entry<String, Shader> entry : shaders.entrySet())
        {
            String shaderId = entry.getKey();
            Shader shader = entry.getValue();
            DefaultShaderModel shaderModel = 
                get("shaders", shaderId, gltfModel::getShaderModel);
            transferGltfChildOfRootPropertyElements(shader, shaderModel);
            
            if (BinaryGltfV1.hasBinaryGltfExtension(shader))
            {
                String bufferViewId = 
                    BinaryGltfV1.getBinaryGltfBufferViewId(shader);
                BufferViewModel bufferViewModel =
                    get("bufferViews", bufferViewId, 
                        gltfModel::getBufferViewModel);
                
                shaderModel.setShaderData(bufferViewModel.getBufferViewData());
            }
            else
            {
                String uri = shader.getUri();
                if (IO.isDataUriString(uri))
                {
                    byte data[] = IO.readDataUri(uri);
                    ByteBuffer shaderData = Buffers.create(data);
                    shaderModel.setShaderData(shaderData);
                }
                else
                {
                    ByteBuffer shaderData = gltfAsset.getReferenceData(uri);
                    shaderModel.setShaderData(shaderData);
                }
            }
        }
    }
    
    /**
     * Initialize the {@link ProgramModel} instances
     */
    void initProgramModels()
    {
        Map<String, Program> programs = Optionals.of(gltf.getPrograms());
        for (Entry<String, Program> entry : programs.entrySet())
        {
            String programId = entry.getKey();
            Program program = entry.getValue();
            DefaultProgramModel programModel = 
                get("programs", programId, gltfModel::getProgramModel);
            transferGltfChildOfRootPropertyElements(program, programModel);
            
            String vertexShaderId = program.getVertexShader();
            DefaultShaderModel vertexShaderModel =
                get("shaders", vertexShaderId, gltfModel::getShaderModel);
            programModel.setVertexShaderModel(vertexShaderModel);
            
            String fragmentShaderId = program.getFragmentShader();
            DefaultShaderModel fragmentShaderModel =
                get("shaders", fragmentShaderId, gltfModel::getShaderModel);
            programModel.setFragmentShaderModel(fragmentShaderModel);
            
            List<String> attributes = Optionals.of(program.getAttributes());
            for (String attribute : attributes)
            {
                programModel.addAttribute(attribute);
            }
        }
    }

    
    /**
     * Add all {@link TechniqueParametersModel} instances for the 
     * attributes of the given {@link Technique} to the given
     * {@link TechniqueModel}
     * 
     * @param technique The {@link Technique}
     * @param techniqueModel The {@link TechniqueModel}
     * @param nodeLookup The function for looking up the {@link NodeModel}
     * for a given node ID. This may be <code>null</code>, but if its is 
     * <code>null</code> and there is a non-<code>null</code> node ID 
     * in the technique parameters, then an error message will be 
     * printed. 
     */
    private static void addParameters(Technique technique,
        DefaultTechniqueModel techniqueModel, 
        Function<? super String, ? extends NodeModel> nodeLookup)
    {
        Map<String, TechniqueParameters> parameters = 
            Optionals.of(technique.getParameters());
        for (Entry<String, TechniqueParameters> entry : parameters.entrySet())
        {
            String parameterName = entry.getKey();
            TechniqueParameters parameter = entry.getValue();
            
            int type = parameter.getType();
            int count = Optionals.of(parameter.getCount(), 1);
            String semantic = parameter.getSemantic();
            Object value = parameter.getValue();
            String nodeId = parameter.getNode();
            NodeModel nodeModel = null;
            if (nodeId != null)
            {
                if (nodeLookup == null)
                {
                    logger.severe("No lookup function found for the nodes");
                }
                else
                {
                    nodeModel = nodeLookup.apply(nodeId);
                }
            }
            
            TechniqueParametersModel techniqueParametersModel =
                new DefaultTechniqueParametersModel(
                    type, count, semantic, value, nodeModel);
            techniqueModel.addParameter(
                parameterName, techniqueParametersModel);
        }
    }

    /**
     * Add all attribute entries of the given {@link Technique} to the given
     * {@link TechniqueModel}
     * 
     * @param technique The {@link Technique}
     * @param techniqueModel The {@link TechniqueModel}
     */
    private static void addAttributes(Technique technique,
        DefaultTechniqueModel techniqueModel)
    {
        Map<String, String> attributes = 
            Optionals.of(technique.getAttributes());
        for (Entry<String, String> entry : attributes.entrySet())
        {
            String attributeName = entry.getKey();
            String parameterName = entry.getValue();
            techniqueModel.addAttribute(attributeName, parameterName);
        }
    }

    /**
     * Add all uniform entries of the given {@link Technique} to the given
     * {@link TechniqueModel}
     * 
     * @param technique The {@link Technique}
     * @param techniqueModel The {@link TechniqueModel}
     */
    private static void addUniforms(Technique technique,
        DefaultTechniqueModel techniqueModel)
    {
        Map<String, String> uniforms = 
            Optionals.of(technique.getUniforms());
        for (Entry<String, String> entry : uniforms.entrySet())
        {
            String uniformName = entry.getKey();
            String parameterName = entry.getValue();
            techniqueModel.addUniform(uniformName, parameterName);
        }
    }
    
    
    /**
     * Initialize the {@link TechniqueModel} instances
     */
    private void initTechniqueModels()
    {
        Map<String, Technique> techniques = Optionals.of(gltf.getTechniques());
        for (Entry<String, Technique> entry : techniques.entrySet())
        {
            String techniqueId = entry.getKey();
            Technique technique = entry.getValue();
            
            DefaultTechniqueModel techniqueModel = 
                get("techniques", techniqueId, gltfModel::getTechniqueModel);
            
            String programId = technique.getProgram();
            DefaultProgramModel programModel = 
                get("programs", programId, gltfModel::getProgramModel);
            techniqueModel.setProgramModel(programModel);

            Function<String, NodeModel> nodeLookup = nodeId -> 
                get("nodes", nodeId, gltfModel::getNodeModel);
            
            initTechniqueModel(techniqueModel, technique, nodeLookup);
            
        }
    }

    /**
     * Initialize the given {@link TechniqueModel} with the values that are
     * obtained from the given {@link Technique}
     * 
     * @param techniqueModel The {@link TechniqueModel}
     * @param technique The {@link Technique}
     * @param nodeLookup The function for looking up the {@link NodeModel}
     * for a given node ID. This may be <code>null</code>, but if its is 
     * <code>null</code> and there is a non-<code>null</code> node ID 
     * in the technique parameters, then an error message will be 
     * printed. 
     */
    public static void initTechniqueModel(
        DefaultTechniqueModel techniqueModel, Technique technique,
        Function<? super String, ? extends NodeModel> nodeLookup)
    {
        transferGltfChildOfRootPropertyElements(technique, techniqueModel);
        
        addParameters(technique, techniqueModel, nodeLookup);
        addAttributes(technique, techniqueModel);
        addUniforms(technique, techniqueModel);
        
        List<Integer> enableModel = null;
        DefaultTechniqueStatesFunctionsModel techniqueStatesFunctionsModel = 
            null;
        TechniqueStates states = technique.getStates();
        if (states != null)
        {
            List<Integer> enable = states.getEnable();
            if (enable != null)
            {
                enableModel = new ArrayList<Integer>(enable);
            }
            TechniqueStatesFunctions functions = states.getFunctions();
            if (functions != null)
            {
                techniqueStatesFunctionsModel =
                    TechniqueStatesFunctionsModels.create(functions);
            }
            
            TechniqueStatesModel techniqueStatesModel = 
                new DefaultTechniqueStatesModel(
                    enableModel, techniqueStatesFunctionsModel);
            techniqueModel.setTechniqueStatesModel(techniqueStatesModel);
        }
    }
    
    
    /**
     * Initialize the {@link MaterialModel} instances
     */
    private void initMaterialModels()
    {
        Map<String, Material> materials = Optionals.of(gltf.getMaterials());
        for (Entry<String, Material> entry : materials.entrySet())
        {
            String materialId = entry.getKey();
            Material material = entry.getValue();
            MaterialModelV1 materialModel = 
                (MaterialModelV1) get("materials", 
                    materialId, gltfModel::getMaterialModel);
            
            transferGltfChildOfRootPropertyElements(material, materialModel);
            
            String techniqueId = material.getTechnique();
            TechniqueModel techniqueModel;
            if (techniqueId == null ||
                GltfDefaults.isDefaultTechniqueId(techniqueId))
            {
                techniqueModel = DefaultModels.getDefaultTechniqueModel();
            }
            else
            {
                techniqueModel =
                    get("techniques", techniqueId, 
                        gltfModel::getTechniqueModel);
            }
            materialModel.setTechniqueModel(techniqueModel);
            
            
            Map<String, Object> modelValues = 
                new LinkedHashMap<String, Object>();
            Map<String, Object> values = Optionals.of(material.getValues());
            for (Entry<String, Object> valueEntry : values.entrySet())
            {
                String parameterName = valueEntry.getKey();
                TechniqueParametersModel techniqueParametersModel = 
                    techniqueModel.getParameters().get(parameterName);
                if (techniqueParametersModel != null &&
                    techniqueParametersModel.getType() == 
                        GltfConstants.GL_SAMPLER_2D)
                {
                    TextureModel textureModel = null;
                    Object value = valueEntry.getValue();
                    if (value != null)
                    {
                        String textureId = String.valueOf(value);
                        textureModel = get("textures", textureId, 
                            gltfModel::getTextureModel);
                    }
                    modelValues.put(parameterName, textureModel);
                }
                else
                {
                    modelValues.put(parameterName, valueEntry.getValue());
                }
            }
            materialModel.setValues(modelValues);
        }
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
    
    
    /**
     * Return the element from the given getter, based on the 
     * {@link #indexMappingSet} for the given name and ID. 
     * If the ID is <code>null</code>, then <code>null</code> is 
     * returned. If there is no proper index stored for the given
     * ID, then a warning will be printed and <code>null</code>
     * will be returned.
     * 
     * @param <T> The element type
     * 
     * @param name The name
     * @param id The ID
     * @param getter The getter
     * @return The element
     */
    private <T> T get(String name, String id, IntFunction<? extends T> getter)
    {
        Integer index = indexMappingSet.getIndex(name, id);
        if (index == null)
        {
            logger.severe("No index found for " + name + " ID " + id);
            return null;
        }
        T element = getter.apply(index);
        return element;
    }
    
}
