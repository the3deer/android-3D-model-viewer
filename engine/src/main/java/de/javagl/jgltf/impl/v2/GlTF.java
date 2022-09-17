/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016-2021 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v2;

import java.util.ArrayList;
import java.util.List;


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
     * Names of glTF extensions used in this asset. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     */
    private List<String> extensionsUsed;
    /**
     * Names of glTF extensions required to properly load this asset. 
     * (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     */
    private List<String> extensionsRequired;
    /**
     * An array of accessors. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;A typed view into a buffer view that contains raw binary 
     * data. (optional) 
     * 
     */
    private List<Accessor> accessors;
    /**
     * An array of keyframe animations. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;A keyframe animation. (optional) 
     * 
     */
    private List<Animation> animations;
    /**
     * Metadata about the glTF asset. (required) 
     * 
     */
    private Asset asset;
    /**
     * An array of buffers. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;A buffer points to binary geometry, animation, or skins. 
     * (optional) 
     * 
     */
    private List<Buffer> buffers;
    /**
     * An array of bufferViews. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;A view into a buffer generally representing a subset of 
     * the buffer. (optional) 
     * 
     */
    private List<BufferView> bufferViews;
    /**
     * An array of cameras. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;A camera's projection. A node **MAY** reference a camera 
     * to apply a transform to place the camera in the scene. (optional) 
     * 
     */
    private List<Camera> cameras;
    /**
     * An array of images. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;Image data used to create a texture. Image **MAY** be 
     * referenced by an URI (or IRI) or a buffer view index. (optional) 
     * 
     */
    private List<Image> images;
    /**
     * An array of materials. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The material appearance of a primitive. (optional) 
     * 
     */
    private List<Material> materials;
    /**
     * An array of meshes. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;A set of primitives to be rendered. Its global transform 
     * is defined by a node that references it. (optional) 
     * 
     */
    private List<Mesh> meshes;
    /**
     * An array of nodes. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;A node in the node hierarchy. When the node contains 
     * `skin`, all `mesh.primitives` **MUST** contain `JOINTS_0` and 
     * `WEIGHTS_0` attributes. A node **MAY** have either a `matrix` or any 
     * combination of `translation`/`rotation`/`scale` (TRS) properties. TRS 
     * properties are converted to matrices and postmultiplied in the `T * R 
     * * S` order to compose the transformation matrix; first the scale is 
     * applied to the vertices, then the rotation, and then the translation. 
     * If none are provided, the transform is the identity. When a node is 
     * targeted for animation (referenced by an animation.channel.target), 
     * `matrix` **MUST NOT** be present. (optional) 
     * 
     */
    private List<Node> nodes;
    /**
     * An array of samplers. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;Texture sampler properties for filtering and wrapping 
     * modes. (optional) 
     * 
     */
    private List<Sampler> samplers;
    /**
     * The index of the default scene. (optional) 
     * 
     */
    private Integer scene;
    /**
     * An array of scenes. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The root nodes of a scene. (optional) 
     * 
     */
    private List<Scene> scenes;
    /**
     * An array of skins. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;Joints and matrices defining a skin. (optional) 
     * 
     */
    private List<Skin> skins;
    /**
     * An array of textures. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;A texture and its sampler. (optional) 
     * 
     */
    private List<Texture> textures;

    /**
     * Names of glTF extensions used in this asset. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @param extensionsUsed The extensionsUsed to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setExtensionsUsed(List<String> extensionsUsed) {
        if (extensionsUsed == null) {
            this.extensionsUsed = extensionsUsed;
            return ;
        }
        if (extensionsUsed.size()< 1) {
            throw new IllegalArgumentException("Number of extensionsUsed elements is < 1");
        }
        this.extensionsUsed = extensionsUsed;
    }

    /**
     * Names of glTF extensions used in this asset. (optional)<br> 
     * Minimum number of items: 1<br> 
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
     * Names of glTF extensions required to properly load this asset. 
     * (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @param extensionsRequired The extensionsRequired to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setExtensionsRequired(List<String> extensionsRequired) {
        if (extensionsRequired == null) {
            this.extensionsRequired = extensionsRequired;
            return ;
        }
        if (extensionsRequired.size()< 1) {
            throw new IllegalArgumentException("Number of extensionsRequired elements is < 1");
        }
        this.extensionsRequired = extensionsRequired;
    }

    /**
     * Names of glTF extensions required to properly load this asset. 
     * (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @return The extensionsRequired
     * 
     */
    public List<String> getExtensionsRequired() {
        return this.extensionsRequired;
    }

    /**
     * Add the given extensionsRequired. The extensionsRequired of this 
     * instance will be replaced with a list that contains all previous 
     * elements, and additionally the new element. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void addExtensionsRequired(String element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<String> oldList = this.extensionsRequired;
        List<String> newList = new ArrayList<String>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.add(element);
        this.extensionsRequired = newList;
    }

    /**
     * Remove the given extensionsRequired. The extensionsRequired of this 
     * instance will be replaced with a list that contains all previous 
     * elements, except for the removed one.<br> 
     * If this new list would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void removeExtensionsRequired(String element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<String> oldList = this.extensionsRequired;
        List<String> newList = new ArrayList<String>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.remove(element);
        if (newList.isEmpty()) {
            this.extensionsRequired = null;
        } else {
            this.extensionsRequired = newList;
        }
    }

    /**
     * An array of accessors. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;A typed view into a buffer view that contains raw binary 
     * data. (optional) 
     * 
     * @param accessors The accessors to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setAccessors(List<Accessor> accessors) {
        if (accessors == null) {
            this.accessors = accessors;
            return ;
        }
        if (accessors.size()< 1) {
            throw new IllegalArgumentException("Number of accessors elements is < 1");
        }
        this.accessors = accessors;
    }

    /**
     * An array of accessors. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;A typed view into a buffer view that contains raw binary 
     * data. (optional) 
     * 
     * @return The accessors
     * 
     */
    public List<Accessor> getAccessors() {
        return this.accessors;
    }

    /**
     * Add the given accessors. The accessors of this instance will be 
     * replaced with a list that contains all previous elements, and 
     * additionally the new element. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void addAccessors(Accessor element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Accessor> oldList = this.accessors;
        List<Accessor> newList = new ArrayList<Accessor>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.add(element);
        this.accessors = newList;
    }

    /**
     * Remove the given accessors. The accessors of this instance will be 
     * replaced with a list that contains all previous elements, except for 
     * the removed one.<br> 
     * If this new list would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void removeAccessors(Accessor element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Accessor> oldList = this.accessors;
        List<Accessor> newList = new ArrayList<Accessor>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.remove(element);
        if (newList.isEmpty()) {
            this.accessors = null;
        } else {
            this.accessors = newList;
        }
    }

    /**
     * An array of keyframe animations. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;A keyframe animation. (optional) 
     * 
     * @param animations The animations to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setAnimations(List<Animation> animations) {
        if (animations == null) {
            this.animations = animations;
            return ;
        }
        if (animations.size()< 1) {
            throw new IllegalArgumentException("Number of animations elements is < 1");
        }
        this.animations = animations;
    }

    /**
     * An array of keyframe animations. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;A keyframe animation. (optional) 
     * 
     * @return The animations
     * 
     */
    public List<Animation> getAnimations() {
        return this.animations;
    }

    /**
     * Add the given animations. The animations of this instance will be 
     * replaced with a list that contains all previous elements, and 
     * additionally the new element. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void addAnimations(Animation element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Animation> oldList = this.animations;
        List<Animation> newList = new ArrayList<Animation>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.add(element);
        this.animations = newList;
    }

    /**
     * Remove the given animations. The animations of this instance will be 
     * replaced with a list that contains all previous elements, except for 
     * the removed one.<br> 
     * If this new list would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void removeAnimations(Animation element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Animation> oldList = this.animations;
        List<Animation> newList = new ArrayList<Animation>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.remove(element);
        if (newList.isEmpty()) {
            this.animations = null;
        } else {
            this.animations = newList;
        }
    }

    /**
     * Metadata about the glTF asset. (required) 
     * 
     * @param asset The asset to set
     * @throws NullPointerException If the given value is <code>null</code>
     * 
     */
    public void setAsset(Asset asset) {
        if (asset == null) {
            throw new NullPointerException((("Invalid value for asset: "+ asset)+", may not be null"));
        }
        this.asset = asset;
    }

    /**
     * Metadata about the glTF asset. (required) 
     * 
     * @return The asset
     * 
     */
    public Asset getAsset() {
        return this.asset;
    }

    /**
     * An array of buffers. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;A buffer points to binary geometry, animation, or skins. 
     * (optional) 
     * 
     * @param buffers The buffers to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setBuffers(List<Buffer> buffers) {
        if (buffers == null) {
            this.buffers = buffers;
            return ;
        }
        if (buffers.size()< 1) {
            throw new IllegalArgumentException("Number of buffers elements is < 1");
        }
        this.buffers = buffers;
    }

    /**
     * An array of buffers. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;A buffer points to binary geometry, animation, or skins. 
     * (optional) 
     * 
     * @return The buffers
     * 
     */
    public List<Buffer> getBuffers() {
        return this.buffers;
    }

    /**
     * Add the given buffers. The buffers of this instance will be replaced 
     * with a list that contains all previous elements, and additionally the 
     * new element. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void addBuffers(Buffer element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Buffer> oldList = this.buffers;
        List<Buffer> newList = new ArrayList<Buffer>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.add(element);
        this.buffers = newList;
    }

    /**
     * Remove the given buffers. The buffers of this instance will be 
     * replaced with a list that contains all previous elements, except for 
     * the removed one.<br> 
     * If this new list would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void removeBuffers(Buffer element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Buffer> oldList = this.buffers;
        List<Buffer> newList = new ArrayList<Buffer>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.remove(element);
        if (newList.isEmpty()) {
            this.buffers = null;
        } else {
            this.buffers = newList;
        }
    }

    /**
     * An array of bufferViews. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;A view into a buffer generally representing a subset of 
     * the buffer. (optional) 
     * 
     * @param bufferViews The bufferViews to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setBufferViews(List<BufferView> bufferViews) {
        if (bufferViews == null) {
            this.bufferViews = bufferViews;
            return ;
        }
        if (bufferViews.size()< 1) {
            throw new IllegalArgumentException("Number of bufferViews elements is < 1");
        }
        this.bufferViews = bufferViews;
    }

    /**
     * An array of bufferViews. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;A view into a buffer generally representing a subset of 
     * the buffer. (optional) 
     * 
     * @return The bufferViews
     * 
     */
    public List<BufferView> getBufferViews() {
        return this.bufferViews;
    }

    /**
     * Add the given bufferViews. The bufferViews of this instance will be 
     * replaced with a list that contains all previous elements, and 
     * additionally the new element. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void addBufferViews(BufferView element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<BufferView> oldList = this.bufferViews;
        List<BufferView> newList = new ArrayList<BufferView>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.add(element);
        this.bufferViews = newList;
    }

    /**
     * Remove the given bufferViews. The bufferViews of this instance will be 
     * replaced with a list that contains all previous elements, except for 
     * the removed one.<br> 
     * If this new list would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void removeBufferViews(BufferView element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<BufferView> oldList = this.bufferViews;
        List<BufferView> newList = new ArrayList<BufferView>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.remove(element);
        if (newList.isEmpty()) {
            this.bufferViews = null;
        } else {
            this.bufferViews = newList;
        }
    }

    /**
     * An array of cameras. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;A camera's projection. A node **MAY** reference a camera 
     * to apply a transform to place the camera in the scene. (optional) 
     * 
     * @param cameras The cameras to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setCameras(List<Camera> cameras) {
        if (cameras == null) {
            this.cameras = cameras;
            return ;
        }
        if (cameras.size()< 1) {
            throw new IllegalArgumentException("Number of cameras elements is < 1");
        }
        this.cameras = cameras;
    }

    /**
     * An array of cameras. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;A camera's projection. A node **MAY** reference a camera 
     * to apply a transform to place the camera in the scene. (optional) 
     * 
     * @return The cameras
     * 
     */
    public List<Camera> getCameras() {
        return this.cameras;
    }

    /**
     * Add the given cameras. The cameras of this instance will be replaced 
     * with a list that contains all previous elements, and additionally the 
     * new element. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void addCameras(Camera element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Camera> oldList = this.cameras;
        List<Camera> newList = new ArrayList<Camera>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.add(element);
        this.cameras = newList;
    }

    /**
     * Remove the given cameras. The cameras of this instance will be 
     * replaced with a list that contains all previous elements, except for 
     * the removed one.<br> 
     * If this new list would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void removeCameras(Camera element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Camera> oldList = this.cameras;
        List<Camera> newList = new ArrayList<Camera>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.remove(element);
        if (newList.isEmpty()) {
            this.cameras = null;
        } else {
            this.cameras = newList;
        }
    }

    /**
     * An array of images. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;Image data used to create a texture. Image **MAY** be 
     * referenced by an URI (or IRI) or a buffer view index. (optional) 
     * 
     * @param images The images to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setImages(List<Image> images) {
        if (images == null) {
            this.images = images;
            return ;
        }
        if (images.size()< 1) {
            throw new IllegalArgumentException("Number of images elements is < 1");
        }
        this.images = images;
    }

    /**
     * An array of images. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;Image data used to create a texture. Image **MAY** be 
     * referenced by an URI (or IRI) or a buffer view index. (optional) 
     * 
     * @return The images
     * 
     */
    public List<Image> getImages() {
        return this.images;
    }

    /**
     * Add the given images. The images of this instance will be replaced 
     * with a list that contains all previous elements, and additionally the 
     * new element. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void addImages(Image element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Image> oldList = this.images;
        List<Image> newList = new ArrayList<Image>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.add(element);
        this.images = newList;
    }

    /**
     * Remove the given images. The images of this instance will be replaced 
     * with a list that contains all previous elements, except for the 
     * removed one.<br> 
     * If this new list would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void removeImages(Image element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Image> oldList = this.images;
        List<Image> newList = new ArrayList<Image>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.remove(element);
        if (newList.isEmpty()) {
            this.images = null;
        } else {
            this.images = newList;
        }
    }

    /**
     * An array of materials. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The material appearance of a primitive. (optional) 
     * 
     * @param materials The materials to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setMaterials(List<Material> materials) {
        if (materials == null) {
            this.materials = materials;
            return ;
        }
        if (materials.size()< 1) {
            throw new IllegalArgumentException("Number of materials elements is < 1");
        }
        this.materials = materials;
    }

    /**
     * An array of materials. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The material appearance of a primitive. (optional) 
     * 
     * @return The materials
     * 
     */
    public List<Material> getMaterials() {
        return this.materials;
    }

    /**
     * Add the given materials. The materials of this instance will be 
     * replaced with a list that contains all previous elements, and 
     * additionally the new element. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void addMaterials(Material element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Material> oldList = this.materials;
        List<Material> newList = new ArrayList<Material>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.add(element);
        this.materials = newList;
    }

    /**
     * Remove the given materials. The materials of this instance will be 
     * replaced with a list that contains all previous elements, except for 
     * the removed one.<br> 
     * If this new list would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void removeMaterials(Material element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Material> oldList = this.materials;
        List<Material> newList = new ArrayList<Material>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.remove(element);
        if (newList.isEmpty()) {
            this.materials = null;
        } else {
            this.materials = newList;
        }
    }

    /**
     * An array of meshes. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;A set of primitives to be rendered. Its global transform 
     * is defined by a node that references it. (optional) 
     * 
     * @param meshes The meshes to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setMeshes(List<Mesh> meshes) {
        if (meshes == null) {
            this.meshes = meshes;
            return ;
        }
        if (meshes.size()< 1) {
            throw new IllegalArgumentException("Number of meshes elements is < 1");
        }
        this.meshes = meshes;
    }

    /**
     * An array of meshes. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;A set of primitives to be rendered. Its global transform 
     * is defined by a node that references it. (optional) 
     * 
     * @return The meshes
     * 
     */
    public List<Mesh> getMeshes() {
        return this.meshes;
    }

    /**
     * Add the given meshes. The meshes of this instance will be replaced 
     * with a list that contains all previous elements, and additionally the 
     * new element. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void addMeshes(Mesh element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Mesh> oldList = this.meshes;
        List<Mesh> newList = new ArrayList<Mesh>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.add(element);
        this.meshes = newList;
    }

    /**
     * Remove the given meshes. The meshes of this instance will be replaced 
     * with a list that contains all previous elements, except for the 
     * removed one.<br> 
     * If this new list would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void removeMeshes(Mesh element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Mesh> oldList = this.meshes;
        List<Mesh> newList = new ArrayList<Mesh>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.remove(element);
        if (newList.isEmpty()) {
            this.meshes = null;
        } else {
            this.meshes = newList;
        }
    }

    /**
     * An array of nodes. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;A node in the node hierarchy. When the node contains 
     * `skin`, all `mesh.primitives` **MUST** contain `JOINTS_0` and 
     * `WEIGHTS_0` attributes. A node **MAY** have either a `matrix` or any 
     * combination of `translation`/`rotation`/`scale` (TRS) properties. TRS 
     * properties are converted to matrices and postmultiplied in the `T * R 
     * * S` order to compose the transformation matrix; first the scale is 
     * applied to the vertices, then the rotation, and then the translation. 
     * If none are provided, the transform is the identity. When a node is 
     * targeted for animation (referenced by an animation.channel.target), 
     * `matrix` **MUST NOT** be present. (optional) 
     * 
     * @param nodes The nodes to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setNodes(List<Node> nodes) {
        if (nodes == null) {
            this.nodes = nodes;
            return ;
        }
        if (nodes.size()< 1) {
            throw new IllegalArgumentException("Number of nodes elements is < 1");
        }
        this.nodes = nodes;
    }

    /**
     * An array of nodes. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;A node in the node hierarchy. When the node contains 
     * `skin`, all `mesh.primitives` **MUST** contain `JOINTS_0` and 
     * `WEIGHTS_0` attributes. A node **MAY** have either a `matrix` or any 
     * combination of `translation`/`rotation`/`scale` (TRS) properties. TRS 
     * properties are converted to matrices and postmultiplied in the `T * R 
     * * S` order to compose the transformation matrix; first the scale is 
     * applied to the vertices, then the rotation, and then the translation. 
     * If none are provided, the transform is the identity. When a node is 
     * targeted for animation (referenced by an animation.channel.target), 
     * `matrix` **MUST NOT** be present. (optional) 
     * 
     * @return The nodes
     * 
     */
    public List<Node> getNodes() {
        return this.nodes;
    }

    /**
     * Add the given nodes. The nodes of this instance will be replaced with 
     * a list that contains all previous elements, and additionally the new 
     * element. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void addNodes(Node element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Node> oldList = this.nodes;
        List<Node> newList = new ArrayList<Node>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.add(element);
        this.nodes = newList;
    }

    /**
     * Remove the given nodes. The nodes of this instance will be replaced 
     * with a list that contains all previous elements, except for the 
     * removed one.<br> 
     * If this new list would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void removeNodes(Node element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Node> oldList = this.nodes;
        List<Node> newList = new ArrayList<Node>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.remove(element);
        if (newList.isEmpty()) {
            this.nodes = null;
        } else {
            this.nodes = newList;
        }
    }

    /**
     * An array of samplers. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;Texture sampler properties for filtering and wrapping 
     * modes. (optional) 
     * 
     * @param samplers The samplers to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setSamplers(List<Sampler> samplers) {
        if (samplers == null) {
            this.samplers = samplers;
            return ;
        }
        if (samplers.size()< 1) {
            throw new IllegalArgumentException("Number of samplers elements is < 1");
        }
        this.samplers = samplers;
    }

    /**
     * An array of samplers. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;Texture sampler properties for filtering and wrapping 
     * modes. (optional) 
     * 
     * @return The samplers
     * 
     */
    public List<Sampler> getSamplers() {
        return this.samplers;
    }

    /**
     * Add the given samplers. The samplers of this instance will be replaced 
     * with a list that contains all previous elements, and additionally the 
     * new element. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void addSamplers(Sampler element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Sampler> oldList = this.samplers;
        List<Sampler> newList = new ArrayList<Sampler>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.add(element);
        this.samplers = newList;
    }

    /**
     * Remove the given samplers. The samplers of this instance will be 
     * replaced with a list that contains all previous elements, except for 
     * the removed one.<br> 
     * If this new list would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void removeSamplers(Sampler element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Sampler> oldList = this.samplers;
        List<Sampler> newList = new ArrayList<Sampler>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.remove(element);
        if (newList.isEmpty()) {
            this.samplers = null;
        } else {
            this.samplers = newList;
        }
    }

    /**
     * The index of the default scene. (optional) 
     * 
     * @param scene The scene to set
     * 
     */
    public void setScene(Integer scene) {
        if (scene == null) {
            this.scene = scene;
            return ;
        }
        this.scene = scene;
    }

    /**
     * The index of the default scene. (optional) 
     * 
     * @return The scene
     * 
     */
    public Integer getScene() {
        return this.scene;
    }

    /**
     * An array of scenes. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The root nodes of a scene. (optional) 
     * 
     * @param scenes The scenes to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setScenes(List<Scene> scenes) {
        if (scenes == null) {
            this.scenes = scenes;
            return ;
        }
        if (scenes.size()< 1) {
            throw new IllegalArgumentException("Number of scenes elements is < 1");
        }
        this.scenes = scenes;
    }

    /**
     * An array of scenes. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The root nodes of a scene. (optional) 
     * 
     * @return The scenes
     * 
     */
    public List<Scene> getScenes() {
        return this.scenes;
    }

    /**
     * Add the given scenes. The scenes of this instance will be replaced 
     * with a list that contains all previous elements, and additionally the 
     * new element. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void addScenes(Scene element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Scene> oldList = this.scenes;
        List<Scene> newList = new ArrayList<Scene>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.add(element);
        this.scenes = newList;
    }

    /**
     * Remove the given scenes. The scenes of this instance will be replaced 
     * with a list that contains all previous elements, except for the 
     * removed one.<br> 
     * If this new list would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void removeScenes(Scene element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Scene> oldList = this.scenes;
        List<Scene> newList = new ArrayList<Scene>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.remove(element);
        if (newList.isEmpty()) {
            this.scenes = null;
        } else {
            this.scenes = newList;
        }
    }

    /**
     * An array of skins. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;Joints and matrices defining a skin. (optional) 
     * 
     * @param skins The skins to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setSkins(List<Skin> skins) {
        if (skins == null) {
            this.skins = skins;
            return ;
        }
        if (skins.size()< 1) {
            throw new IllegalArgumentException("Number of skins elements is < 1");
        }
        this.skins = skins;
    }

    /**
     * An array of skins. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;Joints and matrices defining a skin. (optional) 
     * 
     * @return The skins
     * 
     */
    public List<Skin> getSkins() {
        return this.skins;
    }

    /**
     * Add the given skins. The skins of this instance will be replaced with 
     * a list that contains all previous elements, and additionally the new 
     * element. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void addSkins(Skin element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Skin> oldList = this.skins;
        List<Skin> newList = new ArrayList<Skin>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.add(element);
        this.skins = newList;
    }

    /**
     * Remove the given skins. The skins of this instance will be replaced 
     * with a list that contains all previous elements, except for the 
     * removed one.<br> 
     * If this new list would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void removeSkins(Skin element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Skin> oldList = this.skins;
        List<Skin> newList = new ArrayList<Skin>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.remove(element);
        if (newList.isEmpty()) {
            this.skins = null;
        } else {
            this.skins = newList;
        }
    }

    /**
     * An array of textures. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;A texture and its sampler. (optional) 
     * 
     * @param textures The textures to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setTextures(List<Texture> textures) {
        if (textures == null) {
            this.textures = textures;
            return ;
        }
        if (textures.size()< 1) {
            throw new IllegalArgumentException("Number of textures elements is < 1");
        }
        this.textures = textures;
    }

    /**
     * An array of textures. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;A texture and its sampler. (optional) 
     * 
     * @return The textures
     * 
     */
    public List<Texture> getTextures() {
        return this.textures;
    }

    /**
     * Add the given textures. The textures of this instance will be replaced 
     * with a list that contains all previous elements, and additionally the 
     * new element. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void addTextures(Texture element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Texture> oldList = this.textures;
        List<Texture> newList = new ArrayList<Texture>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.add(element);
        this.textures = newList;
    }

    /**
     * Remove the given textures. The textures of this instance will be 
     * replaced with a list that contains all previous elements, except for 
     * the removed one.<br> 
     * If this new list would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void removeTextures(Texture element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<Texture> oldList = this.textures;
        List<Texture> newList = new ArrayList<Texture>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.remove(element);
        if (newList.isEmpty()) {
            this.textures = null;
        } else {
            this.textures = newList;
        }
    }

}
