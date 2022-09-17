/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016-2021 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v2;



/**
 * An object pointing to a buffer view containing the deviating accessor 
 * values. The number of elements is equal to `accessor.sparse.count` 
 * times number of components. The elements have the same component type 
 * as the base accessor. The elements are tightly packed. Data **MUST** 
 * be aligned following the same rules as the base accessor. 
 * 
 * Auto-generated for accessor.sparse.values.schema.json 
 * 
 */
public class AccessorSparseValues
    extends GlTFProperty
{

    /**
     * The index of the bufferView with sparse values. The referenced buffer 
     * view **MUST NOT** have its `target` or `byteStride` properties 
     * defined. (required) 
     * 
     */
    private Integer bufferView;
    /**
     * The offset relative to the start of the bufferView in bytes. 
     * (optional)<br> 
     * Default: 0<br> 
     * Minimum: 0 (inclusive) 
     * 
     */
    private Integer byteOffset;

    /**
     * The index of the bufferView with sparse values. The referenced buffer 
     * view **MUST NOT** have its `target` or `byteStride` properties 
     * defined. (required) 
     * 
     * @param bufferView The bufferView to set
     * @throws NullPointerException If the given value is <code>null</code>
     * 
     */
    public void setBufferView(Integer bufferView) {
        if (bufferView == null) {
            throw new NullPointerException((("Invalid value for bufferView: "+ bufferView)+", may not be null"));
        }
        this.bufferView = bufferView;
    }

    /**
     * The index of the bufferView with sparse values. The referenced buffer 
     * view **MUST NOT** have its `target` or `byteStride` properties 
     * defined. (required) 
     * 
     * @return The bufferView
     * 
     */
    public Integer getBufferView() {
        return this.bufferView;
    }

    /**
     * The offset relative to the start of the bufferView in bytes. 
     * (optional)<br> 
     * Default: 0<br> 
     * Minimum: 0 (inclusive) 
     * 
     * @param byteOffset The byteOffset to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setByteOffset(Integer byteOffset) {
        if (byteOffset == null) {
            this.byteOffset = byteOffset;
            return ;
        }
        if (byteOffset< 0) {
            throw new IllegalArgumentException("byteOffset < 0");
        }
        this.byteOffset = byteOffset;
    }

    /**
     * The offset relative to the start of the bufferView in bytes. 
     * (optional)<br> 
     * Default: 0<br> 
     * Minimum: 0 (inclusive) 
     * 
     * @return The byteOffset
     * 
     */
    public Integer getByteOffset() {
        return this.byteOffset;
    }

    /**
     * Returns the default value of the byteOffset<br> 
     * @see #getByteOffset 
     * 
     * @return The default byteOffset
     * 
     */
    public Integer defaultByteOffset() {
        return  0;
    }

}
