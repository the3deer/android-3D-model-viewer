/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016-2021 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v2;



/**
 * Texture sampler properties for filtering and wrapping modes. 
 * 
 * Auto-generated for sampler.schema.json 
 * 
 */
public class Sampler
    extends GlTFChildOfRootProperty
{

    /**
     * Magnification filter. (optional)<br> 
     * Valid values: [9728, 9729] 
     * 
     */
    private Integer magFilter;
    /**
     * Minification filter. (optional)<br> 
     * Valid values: [9728, 9729, 9984, 9985, 9986, 9987] 
     * 
     */
    private Integer minFilter;
    /**
     * S (U) wrapping mode. (optional)<br> 
     * Default: 10497<br> 
     * Valid values: [33071, 33648, 10497] 
     * 
     */
    private Integer wrapS;
    /**
     * T (V) wrapping mode. (optional)<br> 
     * Default: 10497<br> 
     * Valid values: [33071, 33648, 10497] 
     * 
     */
    private Integer wrapT;

    /**
     * Magnification filter. (optional)<br> 
     * Valid values: [9728, 9729] 
     * 
     * @param magFilter The magFilter to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setMagFilter(Integer magFilter) {
        if (magFilter == null) {
            this.magFilter = magFilter;
            return ;
        }
        if ((magFilter!= 9728)&&(magFilter!= 9729)) {
            throw new IllegalArgumentException((("Invalid value for magFilter: "+ magFilter)+", valid: [9728, 9729]"));
        }
        this.magFilter = magFilter;
    }

    /**
     * Magnification filter. (optional)<br> 
     * Valid values: [9728, 9729] 
     * 
     * @return The magFilter
     * 
     */
    public Integer getMagFilter() {
        return this.magFilter;
    }

    /**
     * Minification filter. (optional)<br> 
     * Valid values: [9728, 9729, 9984, 9985, 9986, 9987] 
     * 
     * @param minFilter The minFilter to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setMinFilter(Integer minFilter) {
        if (minFilter == null) {
            this.minFilter = minFilter;
            return ;
        }
        if ((((((minFilter!= 9728)&&(minFilter!= 9729))&&(minFilter!= 9984))&&(minFilter!= 9985))&&(minFilter!= 9986))&&(minFilter!= 9987)) {
            throw new IllegalArgumentException((("Invalid value for minFilter: "+ minFilter)+", valid: [9728, 9729, 9984, 9985, 9986, 9987]"));
        }
        this.minFilter = minFilter;
    }

    /**
     * Minification filter. (optional)<br> 
     * Valid values: [9728, 9729, 9984, 9985, 9986, 9987] 
     * 
     * @return The minFilter
     * 
     */
    public Integer getMinFilter() {
        return this.minFilter;
    }

    /**
     * S (U) wrapping mode. (optional)<br> 
     * Default: 10497<br> 
     * Valid values: [33071, 33648, 10497] 
     * 
     * @param wrapS The wrapS to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setWrapS(Integer wrapS) {
        if (wrapS == null) {
            this.wrapS = wrapS;
            return ;
        }
        if (((wrapS!= 33071)&&(wrapS!= 33648))&&(wrapS!= 10497)) {
            throw new IllegalArgumentException((("Invalid value for wrapS: "+ wrapS)+", valid: [33071, 33648, 10497]"));
        }
        this.wrapS = wrapS;
    }

    /**
     * S (U) wrapping mode. (optional)<br> 
     * Default: 10497<br> 
     * Valid values: [33071, 33648, 10497] 
     * 
     * @return The wrapS
     * 
     */
    public Integer getWrapS() {
        return this.wrapS;
    }

    /**
     * Returns the default value of the wrapS<br> 
     * @see #getWrapS 
     * 
     * @return The default wrapS
     * 
     */
    public Integer defaultWrapS() {
        return  10497;
    }

    /**
     * T (V) wrapping mode. (optional)<br> 
     * Default: 10497<br> 
     * Valid values: [33071, 33648, 10497] 
     * 
     * @param wrapT The wrapT to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setWrapT(Integer wrapT) {
        if (wrapT == null) {
            this.wrapT = wrapT;
            return ;
        }
        if (((wrapT!= 33071)&&(wrapT!= 33648))&&(wrapT!= 10497)) {
            throw new IllegalArgumentException((("Invalid value for wrapT: "+ wrapT)+", valid: [33071, 33648, 10497]"));
        }
        this.wrapT = wrapT;
    }

    /**
     * T (V) wrapping mode. (optional)<br> 
     * Default: 10497<br> 
     * Valid values: [33071, 33648, 10497] 
     * 
     * @return The wrapT
     * 
     */
    public Integer getWrapT() {
        return this.wrapT;
    }

    /**
     * Returns the default value of the wrapT<br> 
     * @see #getWrapT 
     * 
     * @return The default wrapT
     * 
     */
    public Integer defaultWrapT() {
        return  10497;
    }

}
