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
package de.javagl.jgltf.model.v1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import de.javagl.jgltf.impl.v1.Accessor;
import de.javagl.jgltf.impl.v1.Animation;
import de.javagl.jgltf.impl.v1.AnimationChannel;
import de.javagl.jgltf.impl.v1.AnimationChannelTarget;
import de.javagl.jgltf.impl.v1.AnimationSampler;
import de.javagl.jgltf.impl.v1.Asset;
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
import de.javagl.jgltf.impl.v1.Scene;
import de.javagl.jgltf.impl.v1.Shader;
import de.javagl.jgltf.impl.v1.Skin;
import de.javagl.jgltf.impl.v1.Technique;
import de.javagl.jgltf.impl.v1.TechniqueParameters;
import de.javagl.jgltf.impl.v1.TechniqueStates;
import de.javagl.jgltf.impl.v1.TechniqueStatesFunctions;
import de.javagl.jgltf.impl.v1.Texture;
import de.javagl.jgltf.model.AccessorData;
import de.javagl.jgltf.model.AccessorDatas;
import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.AnimationModel;
import de.javagl.jgltf.model.AnimationModel.Channel;
import de.javagl.jgltf.model.AnimationModel.Sampler;
import de.javagl.jgltf.model.BufferModel;
import de.javagl.jgltf.model.BufferViewModel;
import de.javagl.jgltf.model.CameraModel;
import de.javagl.jgltf.model.CameraOrthographicModel;
import de.javagl.jgltf.model.CameraPerspectiveModel;
import de.javagl.jgltf.model.GltfConstants;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.ImageModel;
import de.javagl.jgltf.model.MaterialModel;
import de.javagl.jgltf.model.MeshModel;
import de.javagl.jgltf.model.MeshPrimitiveModel;
import de.javagl.jgltf.model.ModelElement;
import de.javagl.jgltf.model.NamedModelElement;
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
import de.javagl.jgltf.model.gl.TechniqueStatesFunctionsModel;
import de.javagl.jgltf.model.gl.TechniqueStatesModel;

/**
 * A class for creating the {@link GlTF version 1.0 glTF} from a 
 * {@link GltfModel}.<br>
 * <br>
 * TODO: Not all features that could be supported are supported yet. 
 */
public class GltfCreatorV1
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(GltfCreatorV1.class.getName());
    
    /**
     * Creates a {@link GlTF} from the given {@link GltfModel}
     * 
     * @param gltfModel The {@link GltfModel}
     * @return The {@link GlTF}
     */
    public static GlTF create(GltfModel gltfModel)
    {
        GltfCreatorV1 creator = new GltfCreatorV1(gltfModel);
        return creator.create();
    }
    
    /**
     * Inner class containing the information that is necessary to define
     * a glTF {@link de.javagl.jgltf.impl.v1.Sampler}
     */
    @SuppressWarnings("javadoc")
    private static class SamplerInfo
    {
        final Integer magFilter;
        final Integer minFilter;
        final Integer wrapS;
        final Integer wrapT;
        
        SamplerInfo(TextureModel textureModel)
        {
            this.magFilter = textureModel.getMagFilter();
            this.minFilter = textureModel.getMinFilter();
            this.wrapS = textureModel.getWrapS();
            this.wrapT = textureModel.getWrapT();
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(magFilter, minFilter, wrapS, wrapT);
        }

        @Override
        public boolean equals(Object object)
        {
            if (this == object)
            {
                return true;
            }
            if (object == null)
            {
                return false;
            }
            if (getClass() != object.getClass())
            {
                return false;
            }
            SamplerInfo other = (SamplerInfo) object;
            if (!Objects.equals(magFilter, other.magFilter))
            {
                return false;
            }
            if (!Objects.equals(minFilter, other.minFilter))
            {
                return false;
            }
            if (!Objects.equals(wrapS, other.wrapS))
            {
                return false;
            }
            if (!Objects.equals(wrapT, other.wrapT))
            {
                return false;
            }
            return true;
        }
    }
    
    /**
     * The {@link GltfModel} that this instance operates on
     */
    private final GltfModel gltfModel;
    
    /**
     * A map from {@link AccessorModel} objects to their IDs
     */
    private final Map<AccessorModel, String> accessorIds;

    /**
     * A map from {@link BufferModel} objects to their IDs
     */
    private final Map<BufferModel, String> bufferIds;

    /**
     * A map from {@link BufferViewModel} objects to their IDs
     */
    private final Map<BufferViewModel, String> bufferViewIds;

    /**
     * A map from {@link CameraModel} objects to their IDs
     */
    private final Map<CameraModel, String> cameraIds;

    /**
     * A map from {@link ImageModel} objects to their IDs
     */
    private final Map<ImageModel, String> imageIds;

    /**
     * A map from {@link MaterialModel} objects to their IDs
     */
    private final Map<MaterialModel, String> materialIds;

    /**
     * A map from {@link MeshModel} objects to their IDs
     */
    private final Map<MeshModel, String> meshIds;

    /**
     * A map from {@link NodeModel} objects to their IDs
     */
    private final Map<NodeModel, String> nodeIds;

    /**
     * A map from {@link ProgramModel} objects to their IDs
     */
    private final Map<ProgramModel, String> programIds;

    /**
     * A map from {@link ShaderModel} objects to their IDs
     */
    private final Map<ShaderModel, String> shaderIds;
    
    /**
     * A map from {@link SkinModel} objects to their IDs
     */
    private final Map<SkinModel, String> skinIds;

    /**
     * A map from {@link TechniqueModel} objects to their IDs
     */
    private final Map<TechniqueModel, String> techniqueIds;
    
    /**
     * A map from {@link TextureModel} objects to their IDs
     */
    private final Map<TextureModel, String> textureIds;
    
    /**
     * A map from {@link SamplerInfo} objects to their IDs
     */
    private final Map<SamplerInfo, String> samplerIds;
    
    /**
     * Creates a new instance with the given {@link GltfModel}
     * 
     * @param gltfModel The {@link GltfModel}
     */
    private GltfCreatorV1(GltfModel gltfModel)
    {
        this.gltfModel = Objects.requireNonNull(
            gltfModel, "The gltfModel may not be null");
        
        accessorIds = computeIdMap(
            "accessor", gltfModel.getAccessorModels());
        bufferIds = computeIdMap(
            "buffer", gltfModel.getBufferModels());
        bufferViewIds = computeIdMap(
            "bufferView", gltfModel.getBufferViewModels());
        cameraIds = computeIdMap(
            "camera", gltfModel.getCameraModels());
        imageIds = computeIdMap(
            "image", gltfModel.getImageModels());
        materialIds = computeIdMap(
            "material", gltfModel.getMaterialModels());
        meshIds = computeIdMap(
            "mesh", gltfModel.getMeshModels());
        nodeIds = computeIdMap(
            "node", gltfModel.getNodeModels());
        skinIds = computeIdMap(
            "skin", gltfModel.getSkinModels());
        textureIds = computeIdMap(
            "texture", gltfModel.getTextureModels());
        
        if (gltfModel instanceof GltfModelV1)
        {
            GltfModelV1 gltfModelV1 = (GltfModelV1)gltfModel;
            programIds = computeIdMap(
                "program", gltfModelV1.getProgramModels());
            shaderIds = computeIdMap(
                "shader", gltfModelV1.getShaderModels());
            techniqueIds = computeIdMap(
                "technique", gltfModelV1.getTechniqueModels());
        }
        else
        {
            programIds = Collections.emptyMap();
            shaderIds = Collections.emptyMap();
            techniqueIds = Collections.emptyMap();
        }
        
        samplerIds = createSamplerIds(gltfModel.getTextureModels());
    }
    
    /**
     * Create the {@link GlTF} instance from the {@link GltfModel}
     * 
     * @return The {@link GlTF} instance
     */
    public GlTF create()
    {
        GlTF gltf = new GlTF();
        transferGltfPropertyElements(gltfModel, gltf);
        
        gltf.setAccessors(map("accessor",
            gltfModel.getAccessorModels(), 
            this::createAccessor));
        gltf.setAnimations(map("animation",
            gltfModel.getAnimationModels(), 
            this::createAnimation));
        gltf.setBuffers(map("buffer",
            gltfModel.getBufferModels(), 
            GltfCreatorV1::createBuffer));
        gltf.setBufferViews(map("bufferView",
            gltfModel.getBufferViewModels(), 
            this::createBufferView));
        gltf.setCameras(map("camera", 
            gltfModel.getCameraModels(), 
            this::createCamera));
        gltf.setImages(map("image",
            gltfModel.getImageModels(), 
            this::createImage));
        gltf.setMaterials(map("material", 
            gltfModel.getMaterialModels(), 
            this::createMaterial));
        gltf.setMeshes(map("mesh", 
            gltfModel.getMeshModels(), 
            this::createMesh));
        gltf.setNodes(map("node",
            gltfModel.getNodeModels(), 
            this::createNode));
        gltf.setScenes(map("scene",
            gltfModel.getSceneModels(), 
            this::createScene));
        gltf.setSkins(map("skin", 
            gltfModel.getSkinModels(), 
            this::createSkin));
        
        gltf.setSamplers(createSamplers());
        
        gltf.setTextures(map("texture",
            gltfModel.getTextureModels(), 
            this::createTexture));

        
        if (gltfModel instanceof GltfModelV1)
        {
            GltfModelV1 gltfModelV1 = (GltfModelV1)gltfModel;
            gltf.setPrograms(map("program", 
                gltfModelV1.getProgramModels(), 
                this::createProgram));
            gltf.setShaders(map("shader", 
                gltfModelV1.getShaderModels(), 
                this::createShader));
            gltf.setTechniques(map("technique", 
                gltfModelV1.getTechniqueModels(), 
                this::createTechnique));
        }
        
        if (gltf.getScenes() != null && !gltf.getScenes().isEmpty())
        {
            gltf.setScene(gltf.getScenes().keySet().iterator().next());
        }
        
        Asset asset = new Asset();
        asset.setVersion("1.0");
        asset.setGenerator("JglTF from https://github.com/javagl/JglTF");
        gltf.setAsset(asset);
        
        return gltf;
    }
    
    /**
     * Create the {@link Accessor} for the given {@link AccessorModel}
     * 
     * @param accessorModel The {@link AccessorModel}
     * @return The {@link Accessor}
     */
    private Accessor createAccessor(AccessorModel accessorModel)
    {
        String bufferViewId = 
            bufferViewIds.get(accessorModel.getBufferViewModel());
        return createAccessor(accessorModel, bufferViewId);
    }
    
    /**
     * Create the {@link Accessor} for the given {@link AccessorModel}
     * 
     * @param accessorModel The {@link AccessorModel}
     * @param bufferViewId The ID of the {@link BufferViewModel}
     * that the {@link AccessorModel} refers to
     * @return The {@link Accessor}
     */
    public static Accessor createAccessor(
        AccessorModel accessorModel, String bufferViewId)
    {
        Accessor accessor = new Accessor();
        transferGltfChildOfRootPropertyElements(accessorModel, accessor);
        
        accessor.setBufferView(bufferViewId);
        
        accessor.setByteOffset(accessorModel.getByteOffset());
        accessor.setComponentType(accessorModel.getComponentType());
        accessor.setCount(accessorModel.getCount());
        accessor.setType(accessorModel.getElementType().toString());
        accessor.setByteStride(accessorModel.getByteStride());
        
        AccessorData accessorData = accessorModel.getAccessorData();
        accessor.setMax(AccessorDatas.computeMax(accessorData));
        accessor.setMin(AccessorDatas.computeMin(accessorData));
        
        return accessor;
    }
    
    /**
     * Create the {@link Animation} for the given {@link AnimationModel}
     * 
     * @param animationModel The {@link AnimationModel}
     * @return The {@link Animation}
     */
    private Animation createAnimation(AnimationModel animationModel)
    {
        Animation animation = new Animation();
        transferGltfChildOfRootPropertyElements(animationModel, animation);
        
        Map<Sampler, String> samplers = new LinkedHashMap<Sampler, String>();
        List<Channel> channels = animationModel.getChannels();
        int counter = 0;
        for (Channel channel : channels)
        {
            String id = "sampler_" + counter;
            samplers.put(channel.getSampler(), id);
            counter++;
        }
        
        List<AnimationChannel> animationChannels = 
            new ArrayList<AnimationChannel>();
        for (Channel channel : channels)
        {
            AnimationChannel animationChannel = new AnimationChannel();
            
            AnimationChannelTarget target = new AnimationChannelTarget();
            NodeModel nodeModel = channel.getNodeModel();
            target.setId(nodeIds.get(nodeModel));
            target.setPath(channel.getPath());
            animationChannel.setTarget(target);
            
            Sampler sampler = channel.getSampler();
            animationChannel.setSampler(samplers.get(sampler));
            
            animationChannels.add(animationChannel);
        }
        animation.setChannels(animationChannels);
        
        Map<String, AnimationSampler> animationSamplers = 
            new LinkedHashMap<String, AnimationSampler>();
        for (Sampler sampler : samplers.keySet())
        {
            AnimationSampler animationSampler = new AnimationSampler();
            animationSampler.setInput(
                accessorIds.get(sampler.getInput()));
            animationSampler.setInterpolation(
                sampler.getInterpolation().name());
            animationSampler.setOutput(
                accessorIds.get(sampler.getOutput()));
            String key = samplers.get(sampler);
            animationSamplers.put(key, animationSampler);
        }
        animation.setSamplers(animationSamplers);
        
        return animation;
    }
    
    
    /**
     * Create the {@link Buffer} for the given {@link BufferModel}
     * 
     * @param bufferModel The {@link BufferModel}
     * @return The {@link Buffer}
     */
    public static Buffer createBuffer(BufferModel bufferModel)
    {
        Buffer buffer = new Buffer();
        transferGltfChildOfRootPropertyElements(bufferModel, buffer);

        buffer.setUri(bufferModel.getUri());
        buffer.setByteLength(bufferModel.getByteLength());
        return buffer;
    }
    
    /**
     * Create the {@link BufferView} for the given {@link BufferViewModel}
     * 
     * @param bufferViewModel The {@link BufferViewModel}
     * @return The {@link BufferView}
     */
    private BufferView createBufferView(BufferViewModel bufferViewModel)
    {
        String bufferId = 
            bufferIds.get(bufferViewModel.getBufferModel());
        return createBufferView(bufferViewModel, bufferId);
    }
    
    /**
     * Create the {@link BufferView} for the given {@link BufferViewModel}
     * 
     * @param bufferViewModel The {@link BufferViewModel}
     * @param bufferId The ID of the {@link BufferModel} that the
     * {@link BufferViewModel} refers to
     * @return The {@link BufferView}
     */
    public static BufferView createBufferView(
        BufferViewModel bufferViewModel, String bufferId)
    {
        BufferView bufferView = new BufferView();
        transferGltfChildOfRootPropertyElements(bufferViewModel, bufferView);

        bufferView.setBuffer(bufferId);
        bufferView.setByteOffset(bufferViewModel.getByteOffset());
        bufferView.setByteLength(bufferViewModel.getByteLength());
        bufferView.setTarget(bufferViewModel.getTarget());
        
        return bufferView;
    }
    

    /**
     * Create the {@link Camera} for the given {@link CameraModel}
     * 
     * @param cameraModel The {@link CameraModel}
     * @return The {@link Camera}
     */
    private Camera createCamera(CameraModel cameraModel)
    {
        Camera camera = new Camera();
        transferGltfChildOfRootPropertyElements(cameraModel, camera);
        
        CameraPerspectiveModel cameraPerspectiveModel = 
            cameraModel.getCameraPerspectiveModel();
        CameraOrthographicModel cameraOrthographicModel = 
            cameraModel.getCameraOrthographicModel();
        if (cameraPerspectiveModel != null)
        {
            CameraPerspective cameraPerspective = new CameraPerspective();
            cameraPerspective.setAspectRatio(
                cameraPerspectiveModel.getAspectRatio());
            cameraPerspective.setYfov(
                cameraPerspectiveModel.getYfov());
            cameraPerspective.setZfar(
                cameraPerspectiveModel.getZfar());
            cameraPerspective.setZnear(
                cameraPerspectiveModel.getZnear());
            camera.setPerspective(cameraPerspective);
            camera.setType("perspective");
        }
        else if (cameraOrthographicModel != null)
        {
            CameraOrthographic cameraOrthographic = new CameraOrthographic();
            cameraOrthographic.setXmag(
                cameraOrthographicModel.getXmag());
            cameraOrthographic.setYmag(
                cameraOrthographicModel.getYmag());
            cameraOrthographic.setZfar(
                cameraOrthographicModel.getZfar());
            cameraOrthographic.setZnear(
                cameraOrthographicModel.getZnear());
            camera.setOrthographic(cameraOrthographic);
            camera.setType("orthographic");
        }
        else
        {
            logger.severe("Camera is neither perspective nor orthographic");
        }
        return camera;
    }
    
    /**
     * Create the {@link Image} for the given {@link ImageModel}
     * 
     * @param imageModel The {@link ImageModel}
     * @return The {@link Image}
     */
    private Image createImage(ImageModel imageModel)
    {
        Image image = new Image();
        transferGltfChildOfRootPropertyElements(imageModel, image);
        
        String bufferView = 
            bufferViewIds.get(imageModel.getBufferViewModel());
        if (bufferView != null)
        {
            logger.severe(
                "Images with BufferView are not supported in glTF 1.0");
        }
        image.setUri(imageModel.getUri());
        
        return image;
    }
    
    /**
     * Create the {@link Material} for the given {@link MaterialModel}.
     * If the given {@link MaterialModel} is not a {@link MaterialModelV1},
     * then a warning is printed and <code>null</code> is returned.
     * 
     * @param materialModel The {@link MaterialModel}
     * @return The {@link Material}
     */
    private Material createMaterial(MaterialModel materialModel)
    {
        if (materialModel instanceof MaterialModelV1)
        {
            MaterialModelV1 materialModelV1 = (MaterialModelV1)materialModel;
            return createMaterialV1(materialModelV1);
        }
        // TODO It should be possible to use a glTF 2.0 material model here
        logger.severe("Cannot store glTF 2.0 material in glTF 1.0");
        return null;
    }
    
    /**
     * Create the {@link Material} for the given {@link MaterialModelV1}
     * 
     * @param materialModel The {@link MaterialModelV1}
     * @return The {@link Material}
     */
    private Material createMaterialV1(MaterialModelV1 materialModel)
    {
        Material material = new Material();
        transferGltfChildOfRootPropertyElements(materialModel, material);
        
        TechniqueModel techniqueModel = materialModel.getTechniqueModel();
        material.setTechnique(techniqueIds.get(techniqueModel));
        
        Map<String, Object> modelValues = materialModel.getValues();
        Map<String, Object> values = new LinkedHashMap<String, Object>();
        for (Entry<String, Object> valueEntry : modelValues.entrySet())
        {
            String parameterName = valueEntry.getKey();
            Object value = valueEntry.getValue();
            if (value instanceof TextureModel)
            {
                TextureModel textureModel = (TextureModel)value;
                String textureId = textureIds.get(textureModel);
                values.put(parameterName, textureId);
            }
            else
            {
                values.put(parameterName, value);
            }
        }
        material.setValues(values);
        return material;
    }
    
    /**
     * Create the {@link Program} for the given {@link ProgramModel}
     * 
     * @param programModel The {@link ProgramModel}
     * @return The {@link Program}
     */
    private Program createProgram(ProgramModel programModel)
    {
        Program program = new Program();
        transferGltfChildOfRootPropertyElements(programModel, program);
        
        ShaderModel vertexShaderModel = 
            programModel.getVertexShaderModel();
        program.setVertexShader(shaderIds.get(vertexShaderModel));
        
        ShaderModel fragmentShaderModel = 
            programModel.getFragmentShaderModel();
        program.setFragmentShader(shaderIds.get(fragmentShaderModel));
        
        List<String> modelAttributes = programModel.getAttributes();
        if (!modelAttributes.isEmpty())
        {
            List<String> attributes = new ArrayList<String>(modelAttributes);
            program.setAttributes(attributes);
        }
        return program;
    }
    
    /**
     * Create the {@link Shader} for the given {@link ShaderModel}
     * 
     * @param shaderModel The {@link ShaderModel}
     * @return The {@link Shader}
     */
    private Shader createShader(ShaderModel shaderModel)
    {
        Shader shader = new Shader();
        transferGltfChildOfRootPropertyElements(shaderModel, shader);
        
        ShaderType shaderType = shaderModel.getShaderType();
        if (shaderType == ShaderType.VERTEX_SHADER)
        {
            shader.setType(GltfConstants.GL_VERTEX_SHADER);
        }
        else if (shaderType == ShaderType.FRAGMENT_SHADER)
        {
            shader.setType(GltfConstants.GL_FRAGMENT_SHADER);
        }
        else
        {
            logger.severe("Invalid shader type: " + shaderType);
        }
        shader.setUri(shaderModel.getUri());
        return shader;
    }
    
    /**
     * Create the {@link Technique} for the given {@link TechniqueModel}
     * 
     * @param techniqueModel The {@link TechniqueModel}
     * @return The {@link Technique}
     */
    private Technique createTechnique(TechniqueModel techniqueModel)
    {
        Technique technique = new Technique();
        transferGltfChildOfRootPropertyElements(techniqueModel, technique);
        
        ProgramModel programModel = techniqueModel.getProgramModel();
        technique.setProgram(programIds.get(programModel));

        Map<String, String> uniforms = techniqueModel.getUniforms();
        technique.setUniforms(new LinkedHashMap<String, String>(uniforms));
        
        Map<String, String> attributes = techniqueModel.getAttributes();
        technique.setAttributes(new LinkedHashMap<String, String>(attributes));
        
        Map<String, TechniqueParametersModel> parametersModel = 
            techniqueModel.getParameters();
        if (!parametersModel.isEmpty())
        {
            Map<String, TechniqueParameters> parameters = 
                new LinkedHashMap<String, TechniqueParameters>();
            
            for (Entry<String, TechniqueParametersModel> entry : 
                parametersModel.entrySet())
            {
                String key = entry.getKey();
                TechniqueParametersModel techniqueParametersModel = 
                    entry.getValue();
                
                TechniqueParameters techniqueParameters =
                    createTechniqueParameters(techniqueParametersModel);
                
                parameters.put(key, techniqueParameters);
            }
            technique.setParameters(parameters);
        }
        technique.setStates(createTechniqueStates(
            techniqueModel.getTechniqueStatesModel()));
        
        return technique;
    }
    
    /**
     * Returns the {@link TechniqueParameters} object for the given
     * {@link TechniqueParametersModel}
     * 
     * @param techniqueParametersModel The {@link TechniqueParametersModel}
     * @return The {@link TechniqueParameters}
     */
    private TechniqueParameters createTechniqueParameters(
        TechniqueParametersModel techniqueParametersModel)
    {
        TechniqueParameters techniqueParameters = new TechniqueParameters();
        
        techniqueParameters.setSemantic(techniqueParametersModel.getSemantic());
        techniqueParameters.setType(techniqueParametersModel.getType());
        techniqueParameters.setCount(techniqueParametersModel.getCount());
        techniqueParameters.setValue(techniqueParametersModel.getValue());
        
        NodeModel nodeModel = techniqueParametersModel.getNodeModel();
        techniqueParameters.setNode(nodeIds.get(nodeModel));
        
        return techniqueParameters;
    }
    
    /**
     * Returns the {@link TechniqueStates} object for the given
     * {@link TechniqueStatesModel}
     * 
     * @param techniqueStatesModel The {@link TechniqueStatesModel}
     * @return The {@link TechniqueStates}
     */
    private TechniqueStates createTechniqueStates(
        TechniqueStatesModel techniqueStatesModel)
    {
        if (techniqueStatesModel == null)
        {
            return null;
        }
        TechniqueStates techniqueStates = new TechniqueStates();
        
        List<Integer> enable = techniqueStatesModel.getEnable();
        if (enable != null)
        {
            techniqueStates.setEnable(new ArrayList<Integer>(enable));
        }
        
        techniqueStates.setFunctions(createTechniqueStatesFunctions(
            techniqueStatesModel.getTechniqueStatesFunctionsModel()));
        
        return techniqueStates;
    }
    
    /**
     * Returns the {@link TechniqueStatesFunctions} object for the given
     * {@link TechniqueStatesFunctionsModel}
     * 
     * @param techniqueStatesFunctionsModel The 
     * {@link TechniqueStatesFunctionsModel}
     * @return The {@link TechniqueStatesFunctions}
     */
    private TechniqueStatesFunctions createTechniqueStatesFunctions(
        TechniqueStatesFunctionsModel techniqueStatesFunctionsModel)
    {
        if (techniqueStatesFunctionsModel == null)
        {
            return null;
        }
        TechniqueStatesFunctions techniqueStatesFunctions = 
            new TechniqueStatesFunctions();
        
        techniqueStatesFunctions.setBlendColor(Optionals.clone(
            techniqueStatesFunctionsModel.getBlendColor()));
        techniqueStatesFunctions.setBlendEquationSeparate(Optionals.clone(
            techniqueStatesFunctionsModel.getBlendEquationSeparate()));
        techniqueStatesFunctions.setBlendFuncSeparate(Optionals.clone(
            techniqueStatesFunctionsModel.getBlendFuncSeparate()));
        techniqueStatesFunctions.setColorMask(Optionals.clone(
            techniqueStatesFunctionsModel.getColorMask()));
        techniqueStatesFunctions.setCullFace(Optionals.clone(
            techniqueStatesFunctionsModel.getCullFace()));
        techniqueStatesFunctions.setDepthFunc(Optionals.clone(
            techniqueStatesFunctionsModel.getDepthFunc()));
        techniqueStatesFunctions.setDepthMask(Optionals.clone(
            techniqueStatesFunctionsModel.getDepthMask()));
        techniqueStatesFunctions.setDepthRange(Optionals.clone(
            techniqueStatesFunctionsModel.getDepthRange()));
        techniqueStatesFunctions.setFrontFace(Optionals.clone(
            techniqueStatesFunctionsModel.getFrontFace()));
        techniqueStatesFunctions.setLineWidth(Optionals.clone(
            techniqueStatesFunctionsModel.getLineWidth()));
        techniqueStatesFunctions.setPolygonOffset(Optionals.clone(
            techniqueStatesFunctionsModel.getPolygonOffset()));
        
        return techniqueStatesFunctions;
    }
    
    
    /**
     * Create the {@link Mesh} for the given {@link MeshModel}
     * 
     * @param meshModel The {@link MeshModel}
     * @return The {@link Mesh}
     */
    private Mesh createMesh(MeshModel meshModel)
    {
        Mesh mesh = new Mesh();
        transferGltfChildOfRootPropertyElements(meshModel, mesh);
        
        List<MeshPrimitive> meshPrimitives = new ArrayList<MeshPrimitive>();
        List<MeshPrimitiveModel> meshPrimitiveModels = 
            meshModel.getMeshPrimitiveModels();
        for (MeshPrimitiveModel meshPrimitiveModel : meshPrimitiveModels)
        {
            MeshPrimitive meshPrimitive = 
                createMeshPrimitive(meshPrimitiveModel);
            meshPrimitives.add(meshPrimitive);
        }
        mesh.setPrimitives(meshPrimitives);
        
        if (meshModel.getWeights() != null)
        {
            logger.severe("Morph target weights are not supported in glTF 1.0");
        }
        return mesh;
    }
    
    /**
     * Create the {@link MeshPrimitive} for the given {@link MeshPrimitiveModel}
     * 
     * @param meshPrimitiveModel The {@link MeshPrimitiveModel}
     * @return The {@link MeshPrimitive}
     */
    private MeshPrimitive createMeshPrimitive(
        MeshPrimitiveModel meshPrimitiveModel)
    {
        MeshPrimitive meshPrimitive = new MeshPrimitive();
        transferGltfPropertyElements(meshPrimitiveModel, meshPrimitive);

        meshPrimitive.setMode(meshPrimitiveModel.getMode());
        
        Map<String, String> attributes = resolveIds(
            meshPrimitiveModel.getAttributes(), 
            accessorIds::get);
        meshPrimitive.setAttributes(attributes);

        AccessorModel Ids = meshPrimitiveModel.getIndices();
        meshPrimitive.setIndices(accessorIds.get(Ids));
        
        List<Map<String, AccessorModel>> modelTargetsList = 
            meshPrimitiveModel.getTargets();
        if (!modelTargetsList.isEmpty())
        {
            logger.severe("Morph targets are not supported in glTF 1.0");
        }
        
        String material = materialIds.get(
            meshPrimitiveModel.getMaterialModel());
        meshPrimitive.setMaterial(material);
        
        return meshPrimitive;
    }

    /**
     * Create the {@link Node} for the given {@link NodeModel}
     * 
     * @param nodeModel The {@link NodeModel}
     * @return The {@link Node}
     */
    private Node createNode(NodeModel nodeModel)
    {
        Node node = new Node();
        transferGltfChildOfRootPropertyElements(nodeModel, node);
        
        if (!nodeModel.getChildren().isEmpty())
        {
            node.setChildren(map(
                nodeModel.getChildren(), nodeIds::get));
        }

        node.setTranslation(Optionals.clone(nodeModel.getTranslation()));
        node.setRotation(Optionals.clone(nodeModel.getRotation()));
        node.setScale(Optionals.clone(nodeModel.getScale()));
        node.setMatrix(Optionals.clone(nodeModel.getMatrix()));
        
        String camera = cameraIds.get(nodeModel.getCameraModel());
        node.setCamera(camera);
        
        String skin = skinIds.get(nodeModel.getSkinModel());
        node.setSkin(skin);
        
        if (nodeModel.getWeights() != null)
        {
            logger.severe("Morph target weights are not supported in glTF 1.0");
        }
        
        List<MeshModel> nodeMeshModels = nodeModel.getMeshModels();
        if (!nodeMeshModels.isEmpty())
        {
            List<String> meshes = new ArrayList<String>();
            for (MeshModel meshModel : nodeMeshModels)
            {
                String id = meshIds.get(meshModel);
                meshes.add(id);
            }
            node.setMeshes(meshes);
        }
        return node;
    }
    
    /**
     * Create the {@link Scene} for the given {@link SceneModel}
     * 
     * @param sceneModel The {@link SceneModel}
     * @return The {@link Scene}
     */
    private Scene createScene(SceneModel sceneModel)
    {
        Scene scene = new Scene();
        transferGltfChildOfRootPropertyElements(sceneModel, scene);
        
        scene.setNodes(map(
            sceneModel.getNodeModels(), nodeIds::get));
        return scene;
    }
    
    /**
     * Create the {@link Skin} for the given {@link SkinModel}
     * 
     * @param skinModel The {@link SkinModel}
     * @return The {@link Skin}
     */
    private Skin createSkin(SkinModel skinModel)
    {
        Skin skin = new Skin();
        transferGltfChildOfRootPropertyElements(skinModel, skin);
        
        String inverseBindMatrices = 
            accessorIds.get(skinModel.getInverseBindMatrices());
        skin.setInverseBindMatrices(inverseBindMatrices);
        
        // TODO Not implemented yet
        logger.severe("Skins are not yet fully supported");
        
        return skin;
    }
    
    /**
     * Create a mapping from {@link SamplerInfo} objects to IDs,
     * based on the {@link SamplerInfo} objects that are 
     * created from the given {@link TextureModel} objects
     * 
     * @param textureModels The {@link TextureModel} objects
     * @return The IDs
     */
    private Map<SamplerInfo, String> createSamplerIds(
        List<TextureModel> textureModels)
    {
        Map<SamplerInfo, String> samplerIndices = 
            new LinkedHashMap<SamplerInfo, String>();
        for (TextureModel textureModel : textureModels)
        {
            SamplerInfo samplerInfo = new SamplerInfo(textureModel);
            if (!samplerIndices.containsKey(samplerInfo))
            {
                samplerIndices.put(samplerInfo, 
                    "sampler_" + samplerIndices.size());
            }
        }
        return samplerIndices;
    }
    
    /**
     * Create the {@link de.javagl.jgltf.impl.v1.Sampler} objects for
     * the current glTF model, returning <code>null</code> if there
     * are no samplers.
     * 
     * @return The samplers
     */
    private Map<String, de.javagl.jgltf.impl.v1.Sampler> createSamplers()
    {
        if (samplerIds.isEmpty())
        {
            return null;
        }
        Map<String, de.javagl.jgltf.impl.v1.Sampler> samplers = 
            new LinkedHashMap<String, de.javagl.jgltf.impl.v1.Sampler>();
        for (SamplerInfo samplerInfo : samplerIds.keySet())
        {
            de.javagl.jgltf.impl.v1.Sampler sampler = 
                createSampler(samplerInfo);
            String key = samplerIds.get(samplerInfo);
            samplers.put(key, sampler);
        }
        return samplers;
    }
    
    /**
     * Create a {@link de.javagl.jgltf.impl.v1.Sampler} from the given 
     * {@link SamplerInfo}
     * 
     * @param samplerInfo The {@link SamplerInfo}
     * @return The {@link de.javagl.jgltf.impl.v1.Sampler}
     */
    private static de.javagl.jgltf.impl.v1.Sampler createSampler(
        SamplerInfo samplerInfo)
    {
        de.javagl.jgltf.impl.v1.Sampler sampler = 
            new de.javagl.jgltf.impl.v1.Sampler();
        sampler.setMagFilter(samplerInfo.magFilter);
        sampler.setMinFilter(samplerInfo.minFilter);
        sampler.setWrapS(samplerInfo.wrapS);
        sampler.setWrapT(samplerInfo.wrapT);
        return sampler;
    }
    

    /**
     * Creates a texture for the given {@link TextureModel}
     * 
     * @param textureModel The {@link TextureModel}
     * @return The {@link Texture}
     */
    private Texture createTexture(TextureModel textureModel)
    {
        Texture texture = new Texture();
        transferGltfChildOfRootPropertyElements(textureModel, texture);
        
        SamplerInfo samplerInfo = new SamplerInfo(textureModel);
        String id = samplerIds.get(samplerInfo);
        texture.setSampler(id);
        
        texture.setSource(imageIds.get(textureModel.getImageModel()));
        
        return texture;
    }

    /**
     * Transfer the extensions and extras from the given model element to
     * the given property
     * 
     * @param modelElement The model element
     * @param property The property
     */
    private static void transferGltfPropertyElements(
        ModelElement modelElement, GlTFProperty property)
    {
        property.setExtensions(modelElement.getExtensions());
        property.setExtras(modelElement.getExtras());
    }
    
    /**
     * Transfer the name and extensions and extras from the given model
     * element to the given property
     * 
     * @param modelElement The model element
     * @param property The property
     */
    private static void transferGltfChildOfRootPropertyElements(
        NamedModelElement modelElement,
        GlTFChildOfRootProperty property)
    {
        property.setName(modelElement.getName());
        transferGltfPropertyElements(modelElement, property);
    }
    
    /**
     * Creates a map that maps (unspecified) strings starting with the 
     * given prefix to the results of applying the given mapper to the
     * given elements, or <code>null</code> if the given collection is 
     * empty
     * 
     * @param prefix The prefix
     * @param elements The elements
     * @param mapper The mapper
     * @return The map
     */
    private static <T, U> Map<String, U> map(
        String prefix, 
        Collection<? extends T> elements,
        Function<? super T, ? extends U> mapper)
    {
        return map(prefix, map(elements, mapper));
    }

    /**
     * Creates a map that maps (unspecified) strings starting with the 
     * given prefix to the given elements, or <code>null</code> if the 
     * given collection is <code>null</code> or empty 
     * 
     * @param prefix The prefix
     * @param elements The elements
     * @return The map
     */
    private static <T> Map<String, T> map(
        String prefix, Collection<? extends T> elements)
    {
        if (elements == null || elements.isEmpty())
        {
            return null;
        }
        Map<String, T> map = new LinkedHashMap<String, T>();
        int index = 0;
        for (T element : elements)
        {
            map.put(prefix + "_" + index, element);
            index++;
        }
        return map;
    }
    
    /**
     * Returns a list containing the result of mapping the given elements with
     * the given function, or <code>null</code> if the given collection is 
     * empty
     *  
     * @param collection The collection
     * @param mapper The mapper
     * @return The list
     */
    private static <T, U> List<U> map(
        Collection<? extends T> collection, 
        Function<? super T, ? extends U> mapper)
    {
        if (collection.isEmpty())
        {
            return null;
        }
        return collection.stream().map(mapper).collect(Collectors.toList());
    }

    /**
     * Creates a map that has the same keys as the given map, mapped to 
     * the IDs that are looked up for the respective values, using 
     * the given function
     * 
     * @param map The map
     * @param idLookup The index lookup
     * @return The index map
     */
    private static <K, T> Map<K, String> resolveIds(
        Map<K, ? extends T> map, 
        Function<? super T, String> idLookup)
    {
        Map<K, String> result = new LinkedHashMap<K, String>();
        for (Entry<K, ? extends T> entry : map.entrySet())
        {
            K key = entry.getKey();
            T value = entry.getValue();
            String id = idLookup.apply(value);
            result.put(key, id);
        }
        return result;
    }
    
    /**
     * Create an ordered map that contains a mapping of the given elements
     * to IDs that start with the given prefix
     * 
     * @param prefix The prefix
     * @param elements The elements
     * @return The ID map
     */
    private static <T> Map<T, String> computeIdMap(
        String prefix, Collection<? extends T> elements)
    {
        Map<T, String> ids = new LinkedHashMap<T, String>();
        int index = 0;
        for (T element : elements)
        {
            ids.put(element, prefix + "_" + index);
            index++;
        }
        return ids;
    }
}
