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
package de.javagl.jgltf.model;

/**
 * Interface for a texture in a glTF asset
 */
public interface TextureModel extends NamedModelElement
{
    /**
     * Return the magnification filter constant
     * 
     * @return The constant
     */
    Integer getMagFilter();

    /**
     * Return the minification filter constant
     * 
     * @return The constant
     */
    Integer getMinFilter();

    /**
     * Return the wrapping constant for S-direction
     * 
     * @return The constant
     */
    Integer getWrapS();

    /**
     * Return the wrapping constant for T-direction
     * 
     * @return The constant
     */
    Integer getWrapT();
    
    /**
     * Returns the {@link ImageModel} that backs this texture
     * 
     * @return The {@link ImageModel}
     */
    ImageModel getImageModel();
}