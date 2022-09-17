/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016-2021 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v2;



/**
 * Image data used to create a texture. Image **MAY** be referenced by an 
 * URI (or IRI) or a buffer view index. 
 * 
 * Auto-generated for image.schema.json 
 * 
 */
public class Image
    extends GlTFChildOfRootProperty
{

    /**
     * The URI (or IRI) of the image. (optional) 
     * 
     */
    private String uri;
    /**
     * The image's media type. This field **MUST** be defined when 
     * `bufferView` is defined. (optional)<br> 
     * Valid values: [image/jpeg, image/png] 
     * 
     */
    private String mimeType;
    /**
     * The index of the bufferView that contains the image. This field **MUST 
     * NOT** be defined when `uri` is defined. (optional) 
     * 
     */
    private Integer bufferView;

    /**
     * The URI (or IRI) of the image. (optional) 
     * 
     * @param uri The uri to set
     * 
     */
    public void setUri(String uri) {
        if (uri == null) {
            this.uri = uri;
            return ;
        }
        this.uri = uri;
    }

    /**
     * The URI (or IRI) of the image. (optional) 
     * 
     * @return The uri
     * 
     */
    public String getUri() {
        return this.uri;
    }

    /**
     * The image's media type. This field **MUST** be defined when 
     * `bufferView` is defined. (optional)<br> 
     * Valid values: [image/jpeg, image/png] 
     * 
     * @param mimeType The mimeType to set
     * 
     */
    public void setMimeType(String mimeType) {
        if (mimeType == null) {
            this.mimeType = mimeType;
            return ;
        }
        this.mimeType = mimeType;
    }

    /**
     * The image's media type. This field **MUST** be defined when 
     * `bufferView` is defined. (optional)<br> 
     * Valid values: [image/jpeg, image/png] 
     * 
     * @return The mimeType
     * 
     */
    public String getMimeType() {
        return this.mimeType;
    }

    /**
     * The index of the bufferView that contains the image. This field **MUST 
     * NOT** be defined when `uri` is defined. (optional) 
     * 
     * @param bufferView The bufferView to set
     * 
     */
    public void setBufferView(Integer bufferView) {
        if (bufferView == null) {
            this.bufferView = bufferView;
            return ;
        }
        this.bufferView = bufferView;
    }

    /**
     * The index of the bufferView that contains the image. This field **MUST 
     * NOT** be defined when `uri` is defined. (optional) 
     * 
     * @return The bufferView
     * 
     */
    public Integer getBufferView() {
        return this.bufferView;
    }

}
