/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v1;



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
     * Specifies if the shaders were generated with premultiplied alpha. 
     * (optional)<br> 
     * Default: false 
     * 
     */
    private Boolean premultipliedAlpha;
    /**
     * The profile of this Asset (optional)<br> 
     * Default: {} 
     * 
     */
    private AssetProfile profile;
    /**
     * The glTF version. (required) 
     * 
     */
    private String version;

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
     * Specifies if the shaders were generated with premultiplied alpha. 
     * (optional)<br> 
     * Default: false 
     * 
     * @param premultipliedAlpha The premultipliedAlpha to set
     * 
     */
    public void setPremultipliedAlpha(Boolean premultipliedAlpha) {
        if (premultipliedAlpha == null) {
            this.premultipliedAlpha = premultipliedAlpha;
            return ;
        }
        this.premultipliedAlpha = premultipliedAlpha;
    }

    /**
     * Specifies if the shaders were generated with premultiplied alpha. 
     * (optional)<br> 
     * Default: false 
     * 
     * @return The premultipliedAlpha
     * 
     */
    public Boolean isPremultipliedAlpha() {
        return this.premultipliedAlpha;
    }

    /**
     * Returns the default value of the premultipliedAlpha<br> 
     * @see #isPremultipliedAlpha 
     * 
     * @return The default premultipliedAlpha
     * 
     */
    public Boolean defaultPremultipliedAlpha() {
        return false;
    }

    /**
     * The profile of this Asset (optional)<br> 
     * Default: {} 
     * 
     * @param profile The profile to set
     * 
     */
    public void setProfile(AssetProfile profile) {
        if (profile == null) {
            this.profile = profile;
            return ;
        }
        this.profile = profile;
    }

    /**
     * The profile of this Asset (optional)<br> 
     * Default: {} 
     * 
     * @return The profile
     * 
     */
    public AssetProfile getProfile() {
        return this.profile;
    }

    /**
     * Returns the default value of the profile<br> 
     * @see #getProfile 
     * 
     * @return The default profile
     * 
     */
    public AssetProfile defaultProfile() {
        return new AssetProfile();
    }

    /**
     * The glTF version. (required) 
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
     * The glTF version. (required) 
     * 
     * @return The version
     * 
     */
    public String getVersion() {
        return this.version;
    }

}
