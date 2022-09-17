/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016-2021 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v2;



/**
 * A typed view into a buffer view that contains raw binary data. 
 * 
 * Auto-generated for accessor.schema.json 
 * 
 */
public class Accessor
    extends GlTFChildOfRootProperty
{

    /**
     * The index of the bufferView. (optional) 
     * 
     */
    private Integer bufferView;
    /**
     * The offset relative to the start of the buffer view in bytes. 
     * (optional)<br> 
     * Default: 0<br> 
     * Minimum: 0 (inclusive) 
     * 
     */
    private Integer byteOffset;
    /**
     * The datatype of the accessor's components. (required)<br> 
     * Valid values: [5120, 5121, 5122, 5123, 5125, 5126] 
     * 
     */
    private Integer componentType;
    /**
     * Specifies whether integer data values are normalized before usage. 
     * (optional)<br> 
     * Default: false 
     * 
     */
    private Boolean normalized;
    /**
     * The number of elements referenced by this accessor. (required)<br> 
     * Minimum: 1 (inclusive) 
     * 
     */
    private Integer count;
    /**
     * Specifies if the accessor's elements are scalars, vectors, or 
     * matrices. (required)<br> 
     * Valid values: [SCALAR, VEC2, VEC3, VEC4, MAT2, MAT3, MAT4] 
     * 
     */
    private String type;
    /**
     * Maximum value of each component in this accessor. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Maximum number of items: 16<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     */
    private Number[] max;
    /**
     * Minimum value of each component in this accessor. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Maximum number of items: 16<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     */
    private Number[] min;
    /**
     * Sparse storage of elements that deviate from their initialization 
     * value. (optional) 
     * 
     */
    private AccessorSparse sparse;

    /**
     * The index of the bufferView. (optional) 
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
     * The index of the bufferView. (optional) 
     * 
     * @return The bufferView
     * 
     */
    public Integer getBufferView() {
        return this.bufferView;
    }

    /**
     * The offset relative to the start of the buffer view in bytes. 
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
     * The offset relative to the start of the buffer view in bytes. 
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

    /**
     * The datatype of the accessor's components. (required)<br> 
     * Valid values: [5120, 5121, 5122, 5123, 5125, 5126] 
     * 
     * @param componentType The componentType to set
     * @throws NullPointerException If the given value is <code>null</code>
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setComponentType(Integer componentType) {
        if (componentType == null) {
            throw new NullPointerException((("Invalid value for componentType: "+ componentType)+", may not be null"));
        }
        if ((((((componentType!= 5120)&&(componentType!= 5121))&&(componentType!= 5122))&&(componentType!= 5123))&&(componentType!= 5125))&&(componentType!= 5126)) {
            throw new IllegalArgumentException((("Invalid value for componentType: "+ componentType)+", valid: [5120, 5121, 5122, 5123, 5125, 5126]"));
        }
        this.componentType = componentType;
    }

    /**
     * The datatype of the accessor's components. (required)<br> 
     * Valid values: [5120, 5121, 5122, 5123, 5125, 5126] 
     * 
     * @return The componentType
     * 
     */
    public Integer getComponentType() {
        return this.componentType;
    }

    /**
     * Specifies whether integer data values are normalized before usage. 
     * (optional)<br> 
     * Default: false 
     * 
     * @param normalized The normalized to set
     * 
     */
    public void setNormalized(Boolean normalized) {
        if (normalized == null) {
            this.normalized = normalized;
            return ;
        }
        this.normalized = normalized;
    }

    /**
     * Specifies whether integer data values are normalized before usage. 
     * (optional)<br> 
     * Default: false 
     * 
     * @return The normalized
     * 
     */
    public Boolean isNormalized() {
        return this.normalized;
    }

    /**
     * Returns the default value of the normalized<br> 
     * @see #isNormalized 
     * 
     * @return The default normalized
     * 
     */
    public Boolean defaultNormalized() {
        return false;
    }

    /**
     * The number of elements referenced by this accessor. (required)<br> 
     * Minimum: 1 (inclusive) 
     * 
     * @param count The count to set
     * @throws NullPointerException If the given value is <code>null</code>
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setCount(Integer count) {
        if (count == null) {
            throw new NullPointerException((("Invalid value for count: "+ count)+", may not be null"));
        }
        if (count< 1) {
            throw new IllegalArgumentException("count < 1");
        }
        this.count = count;
    }

    /**
     * The number of elements referenced by this accessor. (required)<br> 
     * Minimum: 1 (inclusive) 
     * 
     * @return The count
     * 
     */
    public Integer getCount() {
        return this.count;
    }

    /**
     * Specifies if the accessor's elements are scalars, vectors, or 
     * matrices. (required)<br> 
     * Valid values: [SCALAR, VEC2, VEC3, VEC4, MAT2, MAT3, MAT4] 
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
        if (((((((!"SCALAR".equals(type))&&(!"VEC2".equals(type)))&&(!"VEC3".equals(type)))&&(!"VEC4".equals(type)))&&(!"MAT2".equals(type)))&&(!"MAT3".equals(type)))&&(!"MAT4".equals(type))) {
            throw new IllegalArgumentException((("Invalid value for type: "+ type)+", valid: [SCALAR, VEC2, VEC3, VEC4, MAT2, MAT3, MAT4]"));
        }
        this.type = type;
    }

    /**
     * Specifies if the accessor's elements are scalars, vectors, or 
     * matrices. (required)<br> 
     * Valid values: [SCALAR, VEC2, VEC3, VEC4, MAT2, MAT3, MAT4] 
     * 
     * @return The type
     * 
     */
    public String getType() {
        return this.type;
    }

    /**
     * Maximum value of each component in this accessor. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Maximum number of items: 16<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @param max The max to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setMax(Number[] max) {
        if (max == null) {
            this.max = max;
            return ;
        }
        if (max.length< 1) {
            throw new IllegalArgumentException("Number of max elements is < 1");
        }
        if (max.length > 16) {
            throw new IllegalArgumentException("Number of max elements is > 16");
        }
        this.max = max;
    }

    /**
     * Maximum value of each component in this accessor. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Maximum number of items: 16<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @return The max
     * 
     */
    public Number[] getMax() {
        return this.max;
    }

    /**
     * Minimum value of each component in this accessor. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Maximum number of items: 16<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @param min The min to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setMin(Number[] min) {
        if (min == null) {
            this.min = min;
            return ;
        }
        if (min.length< 1) {
            throw new IllegalArgumentException("Number of min elements is < 1");
        }
        if (min.length > 16) {
            throw new IllegalArgumentException("Number of min elements is > 16");
        }
        this.min = min;
    }

    /**
     * Minimum value of each component in this accessor. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Maximum number of items: 16<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     * @return The min
     * 
     */
    public Number[] getMin() {
        return this.min;
    }

    /**
     * Sparse storage of elements that deviate from their initialization 
     * value. (optional) 
     * 
     * @param sparse The sparse to set
     * 
     */
    public void setSparse(AccessorSparse sparse) {
        if (sparse == null) {
            this.sparse = sparse;
            return ;
        }
        this.sparse = sparse;
    }

    /**
     * Sparse storage of elements that deviate from their initialization 
     * value. (optional) 
     * 
     * @return The sparse
     * 
     */
    public AccessorSparse getSparse() {
        return this.sparse;
    }

}
