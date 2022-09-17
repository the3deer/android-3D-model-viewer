/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v1;



/**
 * A camera's projection. A node can reference a camera ID to apply a 
 * transform to place the camera in the scene. 
 * 
 * Auto-generated for camera.schema.json 
 * 
 */
public class Camera
    extends GlTFChildOfRootProperty
{

    /**
     * An orthographic camera containing properties to create an orthographic 
     * projection matrix. (optional) 
     * 
     */
    private CameraOrthographic orthographic;
    /**
     * A perspective camera containing properties to create a perspective 
     * projection matrix. (optional) 
     * 
     */
    private CameraPerspective perspective;
    /**
     * Specifies if the camera uses a perspective or orthographic projection. 
     * (required)<br> 
     * Valid values: ["perspective", "orthographic"] 
     * 
     */
    private String type;

    /**
     * An orthographic camera containing properties to create an orthographic 
     * projection matrix. (optional) 
     * 
     * @param orthographic The orthographic to set
     * 
     */
    public void setOrthographic(CameraOrthographic orthographic) {
        if (orthographic == null) {
            this.orthographic = orthographic;
            return ;
        }
        this.orthographic = orthographic;
    }

    /**
     * An orthographic camera containing properties to create an orthographic 
     * projection matrix. (optional) 
     * 
     * @return The orthographic
     * 
     */
    public CameraOrthographic getOrthographic() {
        return this.orthographic;
    }

    /**
     * A perspective camera containing properties to create a perspective 
     * projection matrix. (optional) 
     * 
     * @param perspective The perspective to set
     * 
     */
    public void setPerspective(CameraPerspective perspective) {
        if (perspective == null) {
            this.perspective = perspective;
            return ;
        }
        this.perspective = perspective;
    }

    /**
     * A perspective camera containing properties to create a perspective 
     * projection matrix. (optional) 
     * 
     * @return The perspective
     * 
     */
    public CameraPerspective getPerspective() {
        return this.perspective;
    }

    /**
     * Specifies if the camera uses a perspective or orthographic projection. 
     * (required)<br> 
     * Valid values: ["perspective", "orthographic"] 
     * 
     * @param type The type to set
     * @throws NullPointerException If the given value is <code>null</code>
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setType(String type) {
        if (type == null) {
            throw new NullPointerException((("Invalid value for type: "+ type)+", may not be null"));
        }
        if ((!"perspective".equals(type))&&(!"orthographic".equals(type))) {
            throw new IllegalArgumentException((("Invalid value for type: "+ type)+", valid: [\"perspective\", \"orthographic\"]"));
        }
        this.type = type;
    }

    /**
     * Specifies if the camera uses a perspective or orthographic projection. 
     * (required)<br> 
     * Valid values: ["perspective", "orthographic"] 
     * 
     * @return The type
     * 
     */
    public String getType() {
        return this.type;
    }

}
