/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v1;



/**
 * Specifies the target rendering API and version, e.g., WebGL 1.0.3. 
 * 
 * Auto-generated for asset.profile.schema.json 
 * 
 */
public class AssetProfile
    extends GlTFProperty
{

    /**
     * Specifies the target rendering API. (optional)<br> 
     * Default: "WebGL" 
     * 
     */
    private String api;
    /**
     * The API version. (optional)<br> 
     * Default: "1.0.3" 
     * 
     */
    private String version;

    /**
     * Specifies the target rendering API. (optional)<br> 
     * Default: "WebGL" 
     * 
     * @param api The api to set
     * 
     */
    public void setApi(String api) {
        if (api == null) {
            this.api = api;
            return ;
        }
        this.api = api;
    }

    /**
     * Specifies the target rendering API. (optional)<br> 
     * Default: "WebGL" 
     * 
     * @return The api
     * 
     */
    public String getApi() {
        return this.api;
    }

    /**
     * Returns the default value of the api<br> 
     * @see #getApi 
     * 
     * @return The default api
     * 
     */
    public String defaultApi() {
        return "WebGL";
    }

    /**
     * The API version. (optional)<br> 
     * Default: "1.0.3" 
     * 
     * @param version The version to set
     * 
     */
    public void setVersion(String version) {
        if (version == null) {
            this.version = version;
            return ;
        }
        this.version = version;
    }

    /**
     * The API version. (optional)<br> 
     * Default: "1.0.3" 
     * 
     * @return The version
     * 
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Returns the default value of the version<br> 
     * @see #getVersion 
     * 
     * @return The default version
     * 
     */
    public String defaultVersion() {
        return "1.0.3";
    }

}
