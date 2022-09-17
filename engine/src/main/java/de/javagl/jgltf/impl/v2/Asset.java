/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016-2021 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v2;



/**
 * Metadata about the glTF asset. 
 * 
 * Auto-generated for asset.schema.json 
 * 
 */
public class Asset
    extends GlTFProperty
{

    /**
     * A copyright message suitable for display to credit the content 
     * creator. (optional) 
     * 
     */
    private String copyright;
    /**
     * Tool that generated this glTF model. Useful for debugging. (optional) 
     * 
     */
    private String generator;
    /**
     * The glTF version in the form of `&lt;major&gt;.&lt;minor&gt;` that 
     * this asset targets. (required) 
     * 
     */
    private String version;
    /**
     * The minimum glTF version in the form of `&lt;major&gt;.&lt;minor&gt;` 
     * that this asset targets. This property **MUST NOT** be greater than 
     * the asset version. (optional) 
     * 
     */
    private String minVersion;

    /**
     * A copyright message suitable for display to credit the content 
     * creator. (optional) 
     * 
     * @param copyright The copyright to set
     * 
     */
    public void setCopyright(String copyright) {
        if (copyright == null) {
            this.copyright = copyright;
            return ;
        }
        this.copyright = copyright;
    }

    /**
     * A copyright message suitable for display to credit the content 
     * creator. (optional) 
     * 
     * @return The copyright
     * 
     */
    public String getCopyright() {
        return this.copyright;
    }

    /**
     * Tool that generated this glTF model. Useful for debugging. (optional) 
     * 
     * @param generator The generator to set
     * 
     */
    public void setGenerator(String generator) {
        if (generator == null) {
            this.generator = generator;
            return ;
        }
        this.generator = generator;
    }

    /**
     * Tool that generated this glTF model. Useful for debugging. (optional) 
     * 
     * @return The generator
     * 
     */
    public String getGenerator() {
        return this.generator;
    }

    /**
     * The glTF version in the form of `&lt;major&gt;.&lt;minor&gt;` that 
     * this asset targets. (required) 
     * 
     * @param version The version to set
     * @throws NullPointerException If the given value is <code>null</code>
     * 
     */
    public void setVersion(String version) {
        if (version == null) {
            throw new NullPointerException((("Invalid value for version: "+ version)+", may not be null"));
        }
        this.version = version;
    }

    /**
     * The glTF version in the form of `&lt;major&gt;.&lt;minor&gt;` that 
     * this asset targets. (required) 
     * 
     * @return The version
     * 
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * The minimum glTF version in the form of `&lt;major&gt;.&lt;minor&gt;` 
     * that this asset targets. This property **MUST NOT** be greater than 
     * the asset version. (optional) 
     * 
     * @param minVersion The minVersion to set
     * 
     */
    public void setMinVersion(String minVersion) {
        if (minVersion == null) {
            this.minVersion = minVersion;
            return ;
        }
        this.minVersion = minVersion;
    }

    /**
     * The minimum glTF version in the form of `&lt;major&gt;.&lt;minor&gt;` 
     * that this asset targets. This property **MUST NOT** be greater than 
     * the asset version. (optional) 
     * 
     * @return The minVersion
     * 
     */
    public String getMinVersion() {
        return this.minVersion;
    }

}
