/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v1;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * The root object for a glTF asset. 
 * 
 * Auto-generated for glTF.schema.json 
 * 
 */
public class GlTF
    extends GlTFProperty
{

    /**
     * Names of extensions used somewhere in this asset. (optional)<br> 
     * Default: []<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     */
    private List<String> extensionsUsed;
    /**
     * A dictionary object of accessors. (optional)<br> 
     * Default: {} 
     * 
     */
    private Map<String, Accessor> accessors;
    /**
     * A dictionary object of keyframe animations. (optional)<br> 
     * Default: {} 
     * 
     */
    private Map<String, Animation> animations;
    /**
     * Metadata about the glTF asset. (optional)<br> 
     * Default: {} 
     * 
     */
    private Asset asset;
    /**
     * A dictionary object of buffers. (optional)<br> 
     * Default: {} 
     * 
     */
    private Map<String, Buffer> buffers;
    /**
     * A dictionary object of bufferViews. (optional)<br> 
     * Default: {} 
     * 
     */
    private Map<String, BufferView> bufferViews;
    /**
     * A dictionary object of cameras. (optional)<br> 
     * Default: {} 
     * 
     */
    private Map<String, Camera> cameras;
    /**
     * A dictionary object of images. (optional)<br> 
     * Default: {} 
     * 
     */
    private Map<String, Image> images;
    /**
     * A dictionary object of materials. (optional)<br> 
     * Default: {} 
     * 
     */
    private Map<String, Material> materials;
    /**
     * A dictionary object of meshes. (optional)<br> 
     * Default: {} 
     * 
     */
    private Map<String, Mesh> meshes;
    /**
     * A dictionary object of nodes. (optional)<br> 
     * Default: {} 
     * 
     */
    private Map<String, Node> nodes;
    /**
     * A dictionary object of programs. (optional)<br> 
     * Default: {} 
     * 
     */
    private Map<String, Program> programs;
    /**
     * A dictionary object of samplers. (optional)<br> 
     * Default: {} 
     * 
     */
    private Map<String, Sampler> samplers;
    /**
     * The ID of the default scene. (optional) 
     * 
     */
    private String scene;
    /**
     * A dictionary object of scenes. (optional)<br> 
     * Default: {} 
     * 
     */
    private Map<String, Scene> scenes;
    /**
     * A dictionary object of shaders. (optional)<br> 
     * Default: {} 
     * 
     */
    private Map<String, Shader> shaders;
    /**
     * A dictionary object of skins. (optional)<br> 
     * Default: {} 
     * 
     */
    private Map<String, Skin> skins;
    /**
     * A dictionary object of techniques. (optional)<br> 
     * Default: {} 
     * 
     */
    private Map<String, Technique> techniques;
    /**
     * A dictionary object of textures. (optional)<br> 
     * Default: {} 
     * 
     */
    private Map<String, Texture> textures;

    /**
     * Names of extensions used somewhere in this asset. (optional)<br> 
     * Default: []<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @param extensionsUsed The extensionsUsed to set
     * 
     */
    public void setExtensionsUsed(List<String> extensionsUsed) {
        if (extensionsUsed == null) {
            this.extensionsUsed = extensionsUsed;
            return ;
        }
        this.extensionsUsed = extensionsUsed;
    }

    /**
     * Names of extensions used somewhere in this asset. (optional)<br> 
     * Default: []<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @return The extensionsUsed
     * 
     */
    public List<String> getExtensionsUsed() {
        return this.extensionsUsed;
    }

    /**
     * Add the given extensionsUsed. The extensionsUsed of this instance will 
     * be replaced with a list that contains all previous elements, and 
     * additionally the new element. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void addExtensionsUsed(String element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<String> oldList = this.extensionsUsed;
        List<String> newList = new ArrayList<String>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.add(element);
        this.extensionsUsed = newList;
    }

    /**
     * Remove the given extensionsUsed. The extensionsUsed of this instance 
     * will be replaced with a list that contains all previous elements, 
     * except for the removed one.<br> 
     * If this new list would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void removeExtensionsUsed(String element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<String> oldList = this.extensionsUsed;
        List<String> newList = new ArrayList<String>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.remove(element);
        if (newList.isEmpty()) {
            this.extensionsUsed = null;
        } else {
            this.extensionsUsed = newList;
        }
    }

    /**
     * Returns the default value of the extensionsUsed<br> 
     * @see #getExtensionsUsed 
     * 
     * @return The default extensionsUsed
     * 
     */
    public List<String> defaultExtensionsUsed() {
        return new ArrayList<String>();
    }

    /**
     * A dictionary object of accessors. (optional)<br> 
     * Default: {} 
     * 
     * @param accessors The accessors to set
     * 
     */
    public void setAccessors(Map<String, Accessor> accessors) {
        if (accessors == null) {
            this.accessors = accessors;
            return ;
        }
        this.accessors = accessors;
    }

    /**
     * A dictionary object of accessors. (optional)<br> 
     * Default: {} 
     * 
     * @return The accessors
     * 
     */
    public Map<String, Accessor> getAccessors() {
        return this.accessors;
    }

    /**
     * Add the given accessors. The accessors of this instance will be 
     * replaced with a map that contains all previous mappings, and 
     * additionally the new mapping. 
     * 
     * @param key The key
     * @param value The value
     * @throws NullPointerException If the given key or value is <code>null</code>
     * 
     */
    public void addAccessors(String key, Accessor value) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        if (value == null) {
            throw new NullPointerException("The value may not be null");
        }
        Map<String, Accessor> oldMap = this.accessors;
        Map<String, Accessor> newMap = new LinkedHashMap<String, Accessor>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.put(key, value);
        this.accessors = newMap;
    }

    /**
     * Remove the given accessors. The accessors of this instance will be 
     * replaced with a map that contains all previous mappings, except for 
     * the one with the given key.<br> 
     * If this new map would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param key The key
     * @throws NullPointerException If the given key is <code>null</code>
     * 
     */
    public void removeAccessors(String key) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        Map<String, Accessor> oldMap = this.accessors;
        Map<String, Accessor> newMap = new LinkedHashMap<String, Accessor>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.remove(key);
        if (newMap.isEmpty()) {
            this.accessors = null;
        } else {
            this.accessors = newMap;
        }
    }

    /**
     * Returns the default value of the accessors<br> 
     * @see #getAccessors 
     * 
     * @return The default accessors
     * 
     */
    public Map<String, Accessor> defaultAccessors() {
        return new LinkedHashMap<String, Accessor>();
    }

    /**
     * A dictionary object of keyframe animations. (optional)<br> 
     * Default: {} 
     * 
     * @param animations The animations to set
     * 
     */
    public void setAnimations(Map<String, Animation> animations) {
        if (animations == null) {
            this.animations = animations;
            return ;
        }
        this.animations = animations;
    }

    /**
     * A dictionary object of keyframe animations. (optional)<br> 
     * Default: {} 
     * 
     * @return The animations
     * 
     */
    public Map<String, Animation> getAnimations() {
        return this.animations;
    }

    /**
     * Add the given animations. The animations of this instance will be 
     * replaced with a map that contains all previous mappings, and 
     * additionally the new mapping. 
     * 
     * @param key The key
     * @param value The value
     * @throws NullPointerException If the given key or value is <code>null</code>
     * 
     */
    public void addAnimations(String key, Animation value) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        if (value == null) {
            throw new NullPointerException("The value may not be null");
        }
        Map<String, Animation> oldMap = this.animations;
        Map<String, Animation> newMap = new LinkedHashMap<String, Animation>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.put(key, value);
        this.animations = newMap;
    }

    /**
     * Remove the given animations. The animations of this instance will be 
     * replaced with a map that contains all previous mappings, except for 
     * the one with the given key.<br> 
     * If this new map would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param key The key
     * @throws NullPointerException If the given key is <code>null</code>
     * 
     */
    public void removeAnimations(String key) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        Map<String, Animation> oldMap = this.animations;
        Map<String, Animation> newMap = new LinkedHashMap<String, Animation>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.remove(key);
        if (newMap.isEmpty()) {
            this.animations = null;
        } else {
            this.animations = newMap;
        }
    }

    /**
     * Returns the default value of the animations<br> 
     * @see #getAnimations 
     * 
     * @return The default animations
     * 
     */
    public Map<String, Animation> defaultAnimations() {
        return new LinkedHashMap<String, Animation>();
    }

    /**
     * Metadata about the glTF asset. (optional)<br> 
     * Default: {} 
     * 
     * @param asset The asset to set
     * 
     */
    public void setAsset(Asset asset) {
        if (asset == null) {
            this.asset = asset;
            return ;
        }
        this.asset = asset;
    }

    /**
     * Metadata about the glTF asset. (optional)<br> 
     * Default: {} 
     * 
     * @return The asset
     * 
     */
    public Asset getAsset() {
        return this.asset;
    }

    /**
     * Returns the default value of the asset<br> 
     * @see #getAsset 
     * 
     * @return The default asset
     * 
     */
    public Asset defaultAsset() {
        return new Asset();
    }

    /**
     * A dictionary object of buffers. (optional)<br> 
     * Default: {} 
     * 
     * @param buffers The buffers to set
     * 
     */
    public void setBuffers(Map<String, Buffer> buffers) {
        if (buffers == null) {
            this.buffers = buffers;
            return ;
        }
        this.buffers = buffers;
    }

    /**
     * A dictionary object of buffers. (optional)<br> 
     * Default: {} 
     * 
     * @return The buffers
     * 
     */
    public Map<String, Buffer> getBuffers() {
        return this.buffers;
    }

    /**
     * Add the given buffers. The buffers of this instance will be replaced 
     * with a map that contains all previous mappings, and additionally the 
     * new mapping. 
     * 
     * @param key The key
     * @param value The value
     * @throws NullPointerException If the given key or value is <code>null</code>
     * 
     */
    public void addBuffers(String key, Buffer value) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        if (value == null) {
            throw new NullPointerException("The value may not be null");
        }
        Map<String, Buffer> oldMap = this.buffers;
        Map<String, Buffer> newMap = new LinkedHashMap<String, Buffer>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.put(key, value);
        this.buffers = newMap;
    }

    /**
     * Remove the given buffers. The buffers of this instance will be 
     * replaced with a map that contains all previous mappings, except for 
     * the one with the given key.<br> 
     * If this new map would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param key The key
     * @throws NullPointerException If the given key is <code>null</code>
     * 
     */
    public void removeBuffers(String key) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        Map<String, Buffer> oldMap = this.buffers;
        Map<String, Buffer> newMap = new LinkedHashMap<String, Buffer>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.remove(key);
        if (newMap.isEmpty()) {
            this.buffers = null;
        } else {
            this.buffers = newMap;
        }
    }

    /**
     * Returns the default value of the buffers<br> 
     * @see #getBuffers 
     * 
     * @return The default buffers
     * 
     */
    public Map<String, Buffer> defaultBuffers() {
        return new LinkedHashMap<String, Buffer>();
    }

    /**
     * A dictionary object of bufferViews. (optional)<br> 
     * Default: {} 
     * 
     * @param bufferViews The bufferViews to set
     * 
     */
    public void setBufferViews(Map<String, BufferView> bufferViews) {
        if (bufferViews == null) {
            this.bufferViews = bufferViews;
            return ;
        }
        this.bufferViews = bufferViews;
    }

    /**
     * A dictionary object of bufferViews. (optional)<br> 
     * Default: {} 
     * 
     * @return The bufferViews
     * 
     */
    public Map<String, BufferView> getBufferViews() {
        return this.bufferViews;
    }

    /**
     * Add the given bufferViews. The bufferViews of this instance will be 
     * replaced with a map that contains all previous mappings, and 
     * additionally the new mapping. 
     * 
     * @param key The key
     * @param value The value
     * @throws NullPointerException If the given key or value is <code>null</code>
     * 
     */
    public void addBufferViews(String key, BufferView value) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        if (value == null) {
            throw new NullPointerException("The value may not be null");
        }
        Map<String, BufferView> oldMap = this.bufferViews;
        Map<String, BufferView> newMap = new LinkedHashMap<String, BufferView>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.put(key, value);
        this.bufferViews = newMap;
    }

    /**
     * Remove the given bufferViews. The bufferViews of this instance will be 
     * replaced with a map that contains all previous mappings, except for 
     * the one with the given key.<br> 
     * If this new map would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param key The key
     * @throws NullPointerException If the given key is <code>null</code>
     * 
     */
    public void removeBufferViews(String key) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        Map<String, BufferView> oldMap = this.bufferViews;
        Map<String, BufferView> newMap = new LinkedHashMap<String, BufferView>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.remove(key);
        if (newMap.isEmpty()) {
            this.bufferViews = null;
        } else {
            this.bufferViews = newMap;
        }
    }

    /**
     * Returns the default value of the bufferViews<br> 
     * @see #getBufferViews 
     * 
     * @return The default bufferViews
     * 
     */
    public Map<String, BufferView> defaultBufferViews() {
        return new LinkedHashMap<String, BufferView>();
    }

    /**
     * A dictionary object of cameras. (optional)<br> 
     * Default: {} 
     * 
     * @param cameras The cameras to set
     * 
     */
    public void setCameras(Map<String, Camera> cameras) {
        if (cameras == null) {
            this.cameras = cameras;
            return ;
        }
        this.cameras = cameras;
    }

    /**
     * A dictionary object of cameras. (optional)<br> 
     * Default: {} 
     * 
     * @return The cameras
     * 
     */
    public Map<String, Camera> getCameras() {
        return this.cameras;
    }

    /**
     * Add the given cameras. The cameras of this instance will be replaced 
     * with a map that contains all previous mappings, and additionally the 
     * new mapping. 
     * 
     * @param key The key
     * @param value The value
     * @throws NullPointerException If the given key or value is <code>null</code>
     * 
     */
    public void addCameras(String key, Camera value) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        if (value == null) {
            throw new NullPointerException("The value may not be null");
        }
        Map<String, Camera> oldMap = this.cameras;
        Map<String, Camera> newMap = new LinkedHashMap<String, Camera>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.put(key, value);
        this.cameras = newMap;
    }

    /**
     * Remove the given cameras. The cameras of this instance will be 
     * replaced with a map that contains all previous mappings, except for 
     * the one with the given key.<br> 
     * If this new map would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param key The key
     * @throws NullPointerException If the given key is <code>null</code>
     * 
     */
    public void removeCameras(String key) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        Map<String, Camera> oldMap = this.cameras;
        Map<String, Camera> newMap = new LinkedHashMap<String, Camera>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.remove(key);
        if (newMap.isEmpty()) {
            this.cameras = null;
        } else {
            this.cameras = newMap;
        }
    }

    /**
     * Returns the default value of the cameras<br> 
     * @see #getCameras 
     * 
     * @return The default cameras
     * 
     */
    public Map<String, Camera> defaultCameras() {
        return new LinkedHashMap<String, Camera>();
    }

    /**
     * A dictionary object of images. (optional)<br> 
     * Default: {} 
     * 
     * @param images The images to set
     * 
     */
    public void setImages(Map<String, Image> images) {
        if (images == null) {
            this.images = images;
            return ;
        }
        this.images = images;
    }

    /**
     * A dictionary object of images. (optional)<br> 
     * Default: {} 
     * 
     * @return The images
     * 
     */
    public Map<String, Image> getImages() {
        return this.images;
    }

    /**
     * Add the given images. The images of this instance will be replaced 
     * with a map that contains all previous mappings, and additionally the 
     * new mapping. 
     * 
     * @param key The key
     * @param value The value
     * @throws NullPointerException If the given key or value is <code>null</code>
     * 
     */
    public void addImages(String key, Image value) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        if (value == null) {
            throw new NullPointerException("The value may not be null");
        }
        Map<String, Image> oldMap = this.images;
        Map<String, Image> newMap = new LinkedHashMap<String, Image>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.put(key, value);
        this.images = newMap;
    }

    /**
     * Remove the given images. The images of this instance will be replaced 
     * with a map that contains all previous mappings, except for the one 
     * with the given key.<br> 
     * If this new map would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param key The key
     * @throws NullPointerException If the given key is <code>null</code>
     * 
     */
    public void removeImages(String key) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        Map<String, Image> oldMap = this.images;
        Map<String, Image> newMap = new LinkedHashMap<String, Image>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.remove(key);
        if (newMap.isEmpty()) {
            this.images = null;
        } else {
            this.images = newMap;
        }
    }

    /**
     * Returns the default value of the images<br> 
     * @see #getImages 
     * 
     * @return The default images
     * 
     */
    public Map<String, Image> defaultImages() {
        return new LinkedHashMap<String, Image>();
    }

    /**
     * A dictionary object of materials. (optional)<br> 
     * Default: {} 
     * 
     * @param materials The materials to set
     * 
     */
    public void setMaterials(Map<String, Material> materials) {
        if (materials == null) {
            this.materials = materials;
            return ;
        }
        this.materials = materials;
    }

    /**
     * A dictionary object of materials. (optional)<br> 
     * Default: {} 
     * 
     * @return The materials
     * 
     */
    public Map<String, Material> getMaterials() {
        return this.materials;
    }

    /**
     * Add the given materials. The materials of this instance will be 
     * replaced with a map that contains all previous mappings, and 
     * additionally the new mapping. 
     * 
     * @param key The key
     * @param value The value
     * @throws NullPointerException If the given key or value is <code>null</code>
     * 
     */
    public void addMaterials(String key, Material value) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        if (value == null) {
            throw new NullPointerException("The value may not be null");
        }
        Map<String, Material> oldMap = this.materials;
        Map<String, Material> newMap = new LinkedHashMap<String, Material>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.put(key, value);
        this.materials = newMap;
    }

    /**
     * Remove the given materials. The materials of this instance will be 
     * replaced with a map that contains all previous mappings, except for 
     * the one with the given key.<br> 
     * If this new map would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param key The key
     * @throws NullPointerException If the given key is <code>null</code>
     * 
     */
    public void removeMaterials(String key) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        Map<String, Material> oldMap = this.materials;
        Map<String, Material> newMap = new LinkedHashMap<String, Material>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.remove(key);
        if (newMap.isEmpty()) {
            this.materials = null;
        } else {
            this.materials = newMap;
        }
    }

    /**
     * Returns the default value of the materials<br> 
     * @see #getMaterials 
     * 
     * @return The default materials
     * 
     */
    public Map<String, Material> defaultMaterials() {
        return new LinkedHashMap<String, Material>();
    }

    /**
     * A dictionary object of meshes. (optional)<br> 
     * Default: {} 
     * 
     * @param meshes The meshes to set
     * 
     */
    public void setMeshes(Map<String, Mesh> meshes) {
        if (meshes == null) {
            this.meshes = meshes;
            return ;
        }
        this.meshes = meshes;
    }

    /**
     * A dictionary object of meshes. (optional)<br> 
     * Default: {} 
     * 
     * @return The meshes
     * 
     */
    public Map<String, Mesh> getMeshes() {
        return this.meshes;
    }

    /**
     * Add the given meshes. The meshes of this instance will be replaced 
     * with a map that contains all previous mappings, and additionally the 
     * new mapping. 
     * 
     * @param key The key
     * @param value The value
     * @throws NullPointerException If the given key or value is <code>null</code>
     * 
     */
    public void addMeshes(String key, Mesh value) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        if (value == null) {
            throw new NullPointerException("The value may not be null");
        }
        Map<String, Mesh> oldMap = this.meshes;
        Map<String, Mesh> newMap = new LinkedHashMap<String, Mesh>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.put(key, value);
        this.meshes = newMap;
    }

    /**
     * Remove the given meshes. The meshes of this instance will be replaced 
     * with a map that contains all previous mappings, except for the one 
     * with the given key.<br> 
     * If this new map would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param key The key
     * @throws NullPointerException If the given key is <code>null</code>
     * 
     */
    public void removeMeshes(String key) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        Map<String, Mesh> oldMap = this.meshes;
        Map<String, Mesh> newMap = new LinkedHashMap<String, Mesh>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.remove(key);
        if (newMap.isEmpty()) {
            this.meshes = null;
        } else {
            this.meshes = newMap;
        }
    }

    /**
     * Returns the default value of the meshes<br> 
     * @see #getMeshes 
     * 
     * @return The default meshes
     * 
     */
    public Map<String, Mesh> defaultMeshes() {
        return new LinkedHashMap<String, Mesh>();
    }

    /**
     * A dictionary object of nodes. (optional)<br> 
     * Default: {} 
     * 
     * @param nodes The nodes to set
     * 
     */
    public void setNodes(Map<String, Node> nodes) {
        if (nodes == null) {
            this.nodes = nodes;
            return ;
        }
        this.nodes = nodes;
    }

    /**
     * A dictionary object of nodes. (optional)<br> 
     * Default: {} 
     * 
     * @return The nodes
     * 
     */
    public Map<String, Node> getNodes() {
        return this.nodes;
    }

    /**
     * Add the given nodes. The nodes of this instance will be replaced with 
     * a map that contains all previous mappings, and additionally the new 
     * mapping. 
     * 
     * @param key The key
     * @param value The value
     * @throws NullPointerException If the given key or value is <code>null</code>
     * 
     */
    public void addNodes(String key, Node value) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        if (value == null) {
            throw new NullPointerException("The value may not be null");
        }
        Map<String, Node> oldMap = this.nodes;
        Map<String, Node> newMap = new LinkedHashMap<String, Node>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.put(key, value);
        this.nodes = newMap;
    }

    /**
     * Remove the given nodes. The nodes of this instance will be replaced 
     * with a map that contains all previous mappings, except for the one 
     * with the given key.<br> 
     * If this new map would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param key The key
     * @throws NullPointerException If the given key is <code>null</code>
     * 
     */
    public void removeNodes(String key) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        Map<String, Node> oldMap = this.nodes;
        Map<String, Node> newMap = new LinkedHashMap<String, Node>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.remove(key);
        if (newMap.isEmpty()) {
            this.nodes = null;
        } else {
            this.nodes = newMap;
        }
    }

    /**
     * Returns the default value of the nodes<br> 
     * @see #getNodes 
     * 
     * @return The default nodes
     * 
     */
    public Map<String, Node> defaultNodes() {
        return new LinkedHashMap<String, Node>();
    }

    /**
     * A dictionary object of programs. (optional)<br> 
     * Default: {} 
     * 
     * @param programs The programs to set
     * 
     */
    public void setPrograms(Map<String, Program> programs) {
        if (programs == null) {
            this.programs = programs;
            return ;
        }
        this.programs = programs;
    }

    /**
     * A dictionary object of programs. (optional)<br> 
     * Default: {} 
     * 
     * @return The programs
     * 
     */
    public Map<String, Program> getPrograms() {
        return this.programs;
    }

    /**
     * Add the given programs. The programs of this instance will be replaced 
     * with a map that contains all previous mappings, and additionally the 
     * new mapping. 
     * 
     * @param key The key
     * @param value The value
     * @throws NullPointerException If the given key or value is <code>null</code>
     * 
     */
    public void addPrograms(String key, Program value) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        if (value == null) {
            throw new NullPointerException("The value may not be null");
        }
        Map<String, Program> oldMap = this.programs;
        Map<String, Program> newMap = new LinkedHashMap<String, Program>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.put(key, value);
        this.programs = newMap;
    }

    /**
     * Remove the given programs. The programs of this instance will be 
     * replaced with a map that contains all previous mappings, except for 
     * the one with the given key.<br> 
     * If this new map would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param key The key
     * @throws NullPointerException If the given key is <code>null</code>
     * 
     */
    public void removePrograms(String key) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        Map<String, Program> oldMap = this.programs;
        Map<String, Program> newMap = new LinkedHashMap<String, Program>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.remove(key);
        if (newMap.isEmpty()) {
            this.programs = null;
        } else {
            this.programs = newMap;
        }
    }

    /**
     * Returns the default value of the programs<br> 
     * @see #getPrograms 
     * 
     * @return The default programs
     * 
     */
    public Map<String, Program> defaultPrograms() {
        return new LinkedHashMap<String, Program>();
    }

    /**
     * A dictionary object of samplers. (optional)<br> 
     * Default: {} 
     * 
     * @param samplers The samplers to set
     * 
     */
    public void setSamplers(Map<String, Sampler> samplers) {
        if (samplers == null) {
            this.samplers = samplers;
            return ;
        }
        this.samplers = samplers;
    }

    /**
     * A dictionary object of samplers. (optional)<br> 
     * Default: {} 
     * 
     * @return The samplers
     * 
     */
    public Map<String, Sampler> getSamplers() {
        return this.samplers;
    }

    /**
     * Add the given samplers. The samplers of this instance will be replaced 
     * with a map that contains all previous mappings, and additionally the 
     * new mapping. 
     * 
     * @param key The key
     * @param value The value
     * @throws NullPointerException If the given key or value is <code>null</code>
     * 
     */
    public void addSamplers(String key, Sampler value) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        if (value == null) {
            throw new NullPointerException("The value may not be null");
        }
        Map<String, Sampler> oldMap = this.samplers;
        Map<String, Sampler> newMap = new LinkedHashMap<String, Sampler>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.put(key, value);
        this.samplers = newMap;
    }

    /**
     * Remove the given samplers. The samplers of this instance will be 
     * replaced with a map that contains all previous mappings, except for 
     * the one with the given key.<br> 
     * If this new map would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param key The key
     * @throws NullPointerException If the given key is <code>null</code>
     * 
     */
    public void removeSamplers(String key) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        Map<String, Sampler> oldMap = this.samplers;
        Map<String, Sampler> newMap = new LinkedHashMap<String, Sampler>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.remove(key);
        if (newMap.isEmpty()) {
            this.samplers = null;
        } else {
            this.samplers = newMap;
        }
    }

    /**
     * Returns the default value of the samplers<br> 
     * @see #getSamplers 
     * 
     * @return The default samplers
     * 
     */
    public Map<String, Sampler> defaultSamplers() {
        return new LinkedHashMap<String, Sampler>();
    }

    /**
     * The ID of the default scene. (optional) 
     * 
     * @param scene The scene to set
     * 
     */
    public void setScene(String scene) {
        if (scene == null) {
            this.scene = scene;
            return ;
        }
        this.scene = scene;
    }

    /**
     * The ID of the default scene. (optional) 
     * 
     * @return The scene
     * 
     */
    public String getScene() {
        return this.scene;
    }

    /**
     * A dictionary object of scenes. (optional)<br> 
     * Default: {} 
     * 
     * @param scenes The scenes to set
     * 
     */
    public void setScenes(Map<String, Scene> scenes) {
        if (scenes == null) {
            this.scenes = scenes;
            return ;
        }
        this.scenes = scenes;
    }

    /**
     * A dictionary object of scenes. (optional)<br> 
     * Default: {} 
     * 
     * @return The scenes
     * 
     */
    public Map<String, Scene> getScenes() {
        return this.scenes;
    }

    /**
     * Add the given scenes. The scenes of this instance will be replaced 
     * with a map that contains all previous mappings, and additionally the 
     * new mapping. 
     * 
     * @param key The key
     * @param value The value
     * @throws NullPointerException If the given key or value is <code>null</code>
     * 
     */
    public void addScenes(String key, Scene value) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        if (value == null) {
            throw new NullPointerException("The value may not be null");
        }
        Map<String, Scene> oldMap = this.scenes;
        Map<String, Scene> newMap = new LinkedHashMap<String, Scene>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.put(key, value);
        this.scenes = newMap;
    }

    /**
     * Remove the given scenes. The scenes of this instance will be replaced 
     * with a map that contains all previous mappings, except for the one 
     * with the given key.<br> 
     * If this new map would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param key The key
     * @throws NullPointerException If the given key is <code>null</code>
     * 
     */
    public void removeScenes(String key) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        Map<String, Scene> oldMap = this.scenes;
        Map<String, Scene> newMap = new LinkedHashMap<String, Scene>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.remove(key);
        if (newMap.isEmpty()) {
            this.scenes = null;
        } else {
            this.scenes = newMap;
        }
    }

    /**
     * Returns the default value of the scenes<br> 
     * @see #getScenes 
     * 
     * @return The default scenes
     * 
     */
    public Map<String, Scene> defaultScenes() {
        return new LinkedHashMap<String, Scene>();
    }

    /**
     * A dictionary object of shaders. (optional)<br> 
     * Default: {} 
     * 
     * @param shaders The shaders to set
     * 
     */
    public void setShaders(Map<String, Shader> shaders) {
        if (shaders == null) {
            this.shaders = shaders;
            return ;
        }
        this.shaders = shaders;
    }

    /**
     * A dictionary object of shaders. (optional)<br> 
     * Default: {} 
     * 
     * @return The shaders
     * 
     */
    public Map<String, Shader> getShaders() {
        return this.shaders;
    }

    /**
     * Add the given shaders. The shaders of this instance will be replaced 
     * with a map that contains all previous mappings, and additionally the 
     * new mapping. 
     * 
     * @param key The key
     * @param value The value
     * @throws NullPointerException If the given key or value is <code>null</code>
     * 
     */
    public void addShaders(String key, Shader value) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        if (value == null) {
            throw new NullPointerException("The value may not be null");
        }
        Map<String, Shader> oldMap = this.shaders;
        Map<String, Shader> newMap = new LinkedHashMap<String, Shader>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.put(key, value);
        this.shaders = newMap;
    }

    /**
     * Remove the given shaders. The shaders of this instance will be 
     * replaced with a map that contains all previous mappings, except for 
     * the one with the given key.<br> 
     * If this new map would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param key The key
     * @throws NullPointerException If the given key is <code>null</code>
     * 
     */
    public void removeShaders(String key) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        Map<String, Shader> oldMap = this.shaders;
        Map<String, Shader> newMap = new LinkedHashMap<String, Shader>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.remove(key);
        if (newMap.isEmpty()) {
            this.shaders = null;
        } else {
            this.shaders = newMap;
        }
    }

    /**
     * Returns the default value of the shaders<br> 
     * @see #getShaders 
     * 
     * @return The default shaders
     * 
     */
    public Map<String, Shader> defaultShaders() {
        return new LinkedHashMap<String, Shader>();
    }

    /**
     * A dictionary object of skins. (optional)<br> 
     * Default: {} 
     * 
     * @param skins The skins to set
     * 
     */
    public void setSkins(Map<String, Skin> skins) {
        if (skins == null) {
            this.skins = skins;
            return ;
        }
        this.skins = skins;
    }

    /**
     * A dictionary object of skins. (optional)<br> 
     * Default: {} 
     * 
     * @return The skins
     * 
     */
    public Map<String, Skin> getSkins() {
        return this.skins;
    }

    /**
     * Add the given skins. The skins of this instance will be replaced with 
     * a map that contains all previous mappings, and additionally the new 
     * mapping. 
     * 
     * @param key The key
     * @param value The value
     * @throws NullPointerException If the given key or value is <code>null</code>
     * 
     */
    public void addSkins(String key, Skin value) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        if (value == null) {
            throw new NullPointerException("The value may not be null");
        }
        Map<String, Skin> oldMap = this.skins;
        Map<String, Skin> newMap = new LinkedHashMap<String, Skin>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.put(key, value);
        this.skins = newMap;
    }

    /**
     * Remove the given skins. The skins of this instance will be replaced 
     * with a map that contains all previous mappings, except for the one 
     * with the given key.<br> 
     * If this new map would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param key The key
     * @throws NullPointerException If the given key is <code>null</code>
     * 
     */
    public void removeSkins(String key) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        Map<String, Skin> oldMap = this.skins;
        Map<String, Skin> newMap = new LinkedHashMap<String, Skin>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.remove(key);
        if (newMap.isEmpty()) {
            this.skins = null;
        } else {
            this.skins = newMap;
        }
    }

    /**
     * Returns the default value of the skins<br> 
     * @see #getSkins 
     * 
     * @return The default skins
     * 
     */
    public Map<String, Skin> defaultSkins() {
        return new LinkedHashMap<String, Skin>();
    }

    /**
     * A dictionary object of techniques. (optional)<br> 
     * Default: {} 
     * 
     * @param techniques The techniques to set
     * 
     */
    public void setTechniques(Map<String, Technique> techniques) {
        if (techniques == null) {
            this.techniques = techniques;
            return ;
        }
        this.techniques = techniques;
    }

    /**
     * A dictionary object of techniques. (optional)<br> 
     * Default: {} 
     * 
     * @return The techniques
     * 
     */
    public Map<String, Technique> getTechniques() {
        return this.techniques;
    }

    /**
     * Add the given techniques. The techniques of this instance will be 
     * replaced with a map that contains all previous mappings, and 
     * additionally the new mapping. 
     * 
     * @param key The key
     * @param value The value
     * @throws NullPointerException If the given key or value is <code>null</code>
     * 
     */
    public void addTechniques(String key, Technique value) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        if (value == null) {
            throw new NullPointerException("The value may not be null");
        }
        Map<String, Technique> oldMap = this.techniques;
        Map<String, Technique> newMap = new LinkedHashMap<String, Technique>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.put(key, value);
        this.techniques = newMap;
    }

    /**
     * Remove the given techniques. The techniques of this instance will be 
     * replaced with a map that contains all previous mappings, except for 
     * the one with the given key.<br> 
     * If this new map would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param key The key
     * @throws NullPointerException If the given key is <code>null</code>
     * 
     */
    public void removeTechniques(String key) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        Map<String, Technique> oldMap = this.techniques;
        Map<String, Technique> newMap = new LinkedHashMap<String, Technique>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.remove(key);
        if (newMap.isEmpty()) {
            this.techniques = null;
        } else {
            this.techniques = newMap;
        }
    }

    /**
     * Returns the default value of the techniques<br> 
     * @see #getTechniques 
     * 
     * @return The default techniques
     * 
     */
    public Map<String, Technique> defaultTechniques() {
        return new LinkedHashMap<String, Technique>();
    }

    /**
     * A dictionary object of textures. (optional)<br> 
     * Default: {} 
     * 
     * @param textures The textures to set
     * 
     */
    public void setTextures(Map<String, Texture> textures) {
        if (textures == null) {
            this.textures = textures;
            return ;
        }
        this.textures = textures;
    }

    /**
     * A dictionary object of textures. (optional)<br> 
     * Default: {} 
     * 
     * @return The textures
     * 
     */
    public Map<String, Texture> getTextures() {
        return this.textures;
    }

    /**
     * Add the given textures. The textures of this instance will be replaced 
     * with a map that contains all previous mappings, and additionally the 
     * new mapping. 
     * 
     * @param key The key
     * @param value The value
     * @throws NullPointerException If the given key or value is <code>null</code>
     * 
     */
    public void addTextures(String key, Texture value) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        if (value == null) {
            throw new NullPointerException("The value may not be null");
        }
        Map<String, Texture> oldMap = this.textures;
        Map<String, Texture> newMap = new LinkedHashMap<String, Texture>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.put(key, value);
        this.textures = newMap;
    }

    /**
     * Remove the given textures. The textures of this instance will be 
     * replaced with a map that contains all previous mappings, except for 
     * the one with the given key.<br> 
     * If this new map would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param key The key
     * @throws NullPointerException If the given key is <code>null</code>
     * 
     */
    public void removeTextures(String key) {
        if (key == null) {
            throw new NullPointerException("The key may not be null");
        }
        Map<String, Texture> oldMap = this.textures;
        Map<String, Texture> newMap = new LinkedHashMap<String, Texture>();
        if (oldMap!= null) {
            newMap.putAll(oldMap);
        }
        newMap.remove(key);
        if (newMap.isEmpty()) {
            this.textures = null;
        } else {
            this.textures = newMap;
        }
    }

    /**
     * Returns the default value of the textures<br> 
     * @see #getTextures 
     * 
     * @return The default textures
     * 
     */
    public Map<String, Texture> defaultTextures() {
        return new LinkedHashMap<String, Texture>();
    }

}
