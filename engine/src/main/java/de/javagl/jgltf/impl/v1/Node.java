/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v1;

import java.util.ArrayList;
import java.util.List;


/**
 * A node in the node hierarchy. A node can have either the `camera`, 
 * `meshes`, or `skeletons`/`skin`/`meshes` properties defined. In the 
 * later case, all `primitives` in the referenced `meshes` contain 
 * `JOINT` and `WEIGHT` attributes and the referenced 
 * `material`/`technique` from each `primitive` has parameters with 
 * `JOINT` and `WEIGHT` semantics. A node can have either a `matrix` or 
 * any combination of `translation`/`rotation`/`scale` (TRS) properties. 
 * TRS properties are converted to matrices and postmultiplied in the `T 
 * * R * S` order to compose the transformation matrix; first the scale 
 * is applied to the vertices, then the rotation, and then the 
 * translation. If none are provided, the transform is the identity. When 
 * a node is targeted for animation (referenced by an 
 * animation.channel.target), only TRS properties may be present; 
 * `matrix` will not be present. 
 * 
 * Auto-generated for node.schema.json 
 * 
 */
public class Node
    extends GlTFChildOfRootProperty
{

    /**
     * The ID of the camera referenced by this node. (optional) 
     * 
     */
    private String camera;
    /**
     * The IDs of this node's children. (optional)<br> 
     * Default: []<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     */
    private List<String> children;
    /**
     * The ID of skeleton nodes. (optional)<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     */
    private List<String> skeletons;
    /**
     * The ID of the skin referenced by this node. (optional) 
     * 
     */
    private String skin;
    /**
     * Name used when this node is a joint in a skin. (optional) 
     * 
     */
    private String jointName;
    /**
     * A floating-point 4x4 transformation matrix stored in column-major 
     * order. (optional)<br> 
     * Default: 
     * [1.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,1.0]<br> 
     * Number of items: 16<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     */
    private float[] matrix;
    /**
     * The IDs of the meshes in this node. (optional)<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     */
    private List<String> meshes;
    /**
     * The node's unit quaternion rotation in the order (x, y, z, w), where w 
     * is the scalar. (optional)<br> 
     * Default: [0.0,0.0,0.0,1.0]<br> 
     * Number of items: 4<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     */
    private float[] rotation;
    /**
     * The node's non-uniform scale. (optional)<br> 
     * Default: [1.0,1.0,1.0]<br> 
     * Number of items: 3<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     */
    private float[] scale;
    /**
     * The node's translation. (optional)<br> 
     * Default: [0.0,0.0,0.0]<br> 
     * Number of items: 3<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     */
    private float[] translation;

    /**
     * The ID of the camera referenced by this node. (optional) 
     * 
     * @param camera The camera to set
     * 
     */
    public void setCamera(String camera) {
        if (camera == null) {
            this.camera = camera;
            return ;
        }
        this.camera = camera;
    }

    /**
     * The ID of the camera referenced by this node. (optional) 
     * 
     * @return The camera
     * 
     */
    public String getCamera() {
        return this.camera;
    }

    /**
     * The IDs of this node's children. (optional)<br> 
     * Default: []<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @param children The children to set
     * 
     */
    public void setChildren(List<String> children) {
        if (children == null) {
            this.children = children;
            return ;
        }
        this.children = children;
    }

    /**
     * The IDs of this node's children. (optional)<br> 
     * Default: []<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @return The children
     * 
     */
    public List<String> getChildren() {
        return this.children;
    }

    /**
     * Add the given children. The children of this instance will be replaced 
     * with a list that contains all previous elements, and additionally the 
     * new element. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void addChildren(String element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<String> oldList = this.children;
        List<String> newList = new ArrayList<String>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.add(element);
        this.children = newList;
    }

    /**
     * Remove the given children. The children of this instance will be 
     * replaced with a list that contains all previous elements, except for 
     * the removed one.<br> 
     * If this new list would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void removeChildren(String element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<String> oldList = this.children;
        List<String> newList = new ArrayList<String>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.remove(element);
        if (newList.isEmpty()) {
            this.children = null;
        } else {
            this.children = newList;
        }
    }

    /**
     * Returns the default value of the children<br> 
     * @see #getChildren 
     * 
     * @return The default children
     * 
     */
    public List<String> defaultChildren() {
        return new ArrayList<String>();
    }

    /**
     * The ID of skeleton nodes. (optional)<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @param skeletons The skeletons to set
     * 
     */
    public void setSkeletons(List<String> skeletons) {
        if (skeletons == null) {
            this.skeletons = skeletons;
            return ;
        }
        this.skeletons = skeletons;
    }

    /**
     * The ID of skeleton nodes. (optional)<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @return The skeletons
     * 
     */
    public List<String> getSkeletons() {
        return this.skeletons;
    }

    /**
     * Add the given skeletons. The skeletons of this instance will be 
     * replaced with a list that contains all previous elements, and 
     * additionally the new element. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void addSkeletons(String element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<String> oldList = this.skeletons;
        List<String> newList = new ArrayList<String>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.add(element);
        this.skeletons = newList;
    }

    /**
     * Remove the given skeletons. The skeletons of this instance will be 
     * replaced with a list that contains all previous elements, except for 
     * the removed one.<br> 
     * If this new list would be empty, then it will be set to 
     * <code>null</code>. 
     * 
     * @param element The element
     * @throws NullPointerException If the given element is <code>null</code>
     * 
     */
    public void removeSkeletons(String element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<String> oldList = this.skeletons;
        List<String> newList = new ArrayList<String>();
        if (oldList!= null) {
            newList.addAll(oldList);
        }
        newList.remove(element);
        if (newList.isEmpty()) {
            this.skeletons = null;
        } else {
            this.skeletons = newList;
        }
    }

    /**
     * The ID of the skin referenced by this node. (optional) 
     * 
     * @param skin The skin to set
     * 
     */
    public void setSkin(String skin) {
        if (skin == null) {
            this.skin = skin;
            return ;
        }
        this.skin = skin;
    }

    /**
     * The ID of the skin referenced by this node. (optional) 
     * 
     * @return The skin
     * 
     */
    public String getSkin() {
        return this.skin;
    }

    /**
     * Name used when this node is a joint in a skin. (optional) 
     * 
     * @param jointName The jointName to set
     * 
     */
    public void setJointName(String jointName) {
        if (jointName == null) {
            this.jointName = jointName;
            return ;
        }
        this.jointName = jointName;
    }

    /**
     * Name used when this node is a joint in a skin. (optional) 
     * 
     * @return The jointName
     * 
     */
    public String getJointName() {
        return this.jointName;
    }

    /**
     * A floating-point 4x4 transformation matrix stored in column-major 
     * order. (optional)<br> 
     * Default: 
     * [1.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,1.0]<br> 
     * Number of items: 16<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @param matrix The matrix to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setMatrix(float[] matrix) {
        if (matrix == null) {
            this.matrix = matrix;
            return ;
        }
        if (matrix.length< 16) {
            throw new IllegalArgumentException("Number of matrix elements is < 16");
        }
        if (matrix.length > 16) {
            throw new IllegalArgumentException("Number of matrix elements is > 16");
        }
        this.matrix = matrix;
    }

    /**
     * A floating-point 4x4 transformation matrix stored in column-major 
     * order. (optional)<br> 
     * Default: 
     * [1.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,1.0]<br> 
     * Number of items: 16<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @return The matrix
     * 
     */
    public float[] getMatrix() {
        return this.matrix;
    }

    /**
     * Returns the default value of the matrix<br> 
     * @see #getMatrix 
     * 
     * @return The default matrix
     * 
     */
    public float[] defaultMatrix() {
        return new float[] { 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F };
    }

    /**
     * The IDs of the meshes in this node. (optional)<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @param meshes The meshes to set
     * 
     */
    public void setMeshes(List<String> meshes) {
        if (meshes == null) {
            this.meshes = meshes;
            return ;
        }
        this.meshes = meshes;
    }

    /**
     * The IDs of the meshes in this node. (optional)<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @return The meshes
     * 
     */
    public List<String> getMeshes() {
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
    public void addMeshes(String element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<String> oldList = this.meshes;
        List<String> newList = new ArrayList<String>();
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
    public void removeMeshes(String element) {
        if (element == null) {
            throw new NullPointerException("The element may not be null");
        }
        List<String> oldList = this.meshes;
        List<String> newList = new ArrayList<String>();
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
     * The node's unit quaternion rotation in the order (x, y, z, w), where w 
     * is the scalar. (optional)<br> 
     * Default: [0.0,0.0,0.0,1.0]<br> 
     * Number of items: 4<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @param rotation The rotation to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setRotation(float[] rotation) {
        if (rotation == null) {
            this.rotation = rotation;
            return ;
        }
        if (rotation.length< 4) {
            throw new IllegalArgumentException("Number of rotation elements is < 4");
        }
        if (rotation.length > 4) {
            throw new IllegalArgumentException("Number of rotation elements is > 4");
        }
        this.rotation = rotation;
    }

    /**
     * The node's unit quaternion rotation in the order (x, y, z, w), where w 
     * is the scalar. (optional)<br> 
     * Default: [0.0,0.0,0.0,1.0]<br> 
     * Number of items: 4<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @return The rotation
     * 
     */
    public float[] getRotation() {
        return this.rotation;
    }

    /**
     * Returns the default value of the rotation<br> 
     * @see #getRotation 
     * 
     * @return The default rotation
     * 
     */
    public float[] defaultRotation() {
        return new float[] { 0.0F, 0.0F, 0.0F, 1.0F };
    }

    /**
     * The node's non-uniform scale. (optional)<br> 
     * Default: [1.0,1.0,1.0]<br> 
     * Number of items: 3<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @param scale The scale to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setScale(float[] scale) {
        if (scale == null) {
            this.scale = scale;
            return ;
        }
        if (scale.length< 3) {
            throw new IllegalArgumentException("Number of scale elements is < 3");
        }
        if (scale.length > 3) {
            throw new IllegalArgumentException("Number of scale elements is > 3");
        }
        this.scale = scale;
    }

    /**
     * The node's non-uniform scale. (optional)<br> 
     * Default: [1.0,1.0,1.0]<br> 
     * Number of items: 3<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @return The scale
     * 
     */
    public float[] getScale() {
        return this.scale;
    }

    /**
     * Returns the default value of the scale<br> 
     * @see #getScale 
     * 
     * @return The default scale
     * 
     */
    public float[] defaultScale() {
        return new float[] { 1.0F, 1.0F, 1.0F };
    }

    /**
     * The node's translation. (optional)<br> 
     * Default: [0.0,0.0,0.0]<br> 
     * Number of items: 3<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @param translation The translation to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setTranslation(float[] translation) {
        if (translation == null) {
            this.translation = translation;
            return ;
        }
        if (translation.length< 3) {
            throw new IllegalArgumentException("Number of translation elements is < 3");
        }
        if (translation.length > 3) {
            throw new IllegalArgumentException("Number of translation elements is > 3");
        }
        this.translation = translation;
    }

    /**
     * The node's translation. (optional)<br> 
     * Default: [0.0,0.0,0.0]<br> 
     * Number of items: 3<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @return The translation
     * 
     */
    public float[] getTranslation() {
        return this.translation;
    }

    /**
     * Returns the default value of the translation<br> 
     * @see #getTranslation 
     * 
     * @return The default translation
     * 
     */
    public float[] defaultTranslation() {
        return new float[] { 0.0F, 0.0F, 0.0F };
    }

}
