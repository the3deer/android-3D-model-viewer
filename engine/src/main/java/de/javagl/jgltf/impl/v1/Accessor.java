/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v1;



/**
 * A typed view into a bufferView. A bufferView contains raw binary data. 
 * An accessor provides a typed view into a bufferView or a subset of a 
 * bufferView similar to how WebGL's `vertexAttribPointer()` defines an 
 * attribute in a buffer. 
 * 
 * Auto-generated for accessor.schema.json 
 * 
 */
public class Accessor
    extends GlTFChildOfRootProperty
{

    /**
     * The ID of the bufferView. (required) 
     * 
     */
    private String bufferView;
    /**
     * The offset relative to the start of the bufferView in bytes. 
     * (required)<br> 
     * Minimum: 0 (inclusive) 
     * 
     */
    private Integer byteOffset;
    /**
     * The stride, in bytes, between attributes referenced by this accessor. 
     * (optional)<br> 
     * Default: 0<br> 
     * Minimum: 0 (inclusive)<br> 
     * Maximum: 255 (inclusive) 
     * 
     */
    private Integer byteStride;
    /**
     * The datatype of components in the attribute. (required)<br> 
     * Valid values: [5120, 5121, 5122, 5123, 5126] 
     * 
     */
    private Integer componentType;
    /**
     * The number of attributes referenced by this accessor. (required)<br> 
     * Minimum: 1 (inclusive) 
     * 
     */
    private Integer count;
    /**
     * Specifies if the attribute is a scalar, vector, or matrix. 
     * (required)<br> 
     * Valid values: ["SCALAR", "VEC2", "VEC3", "VEC4", "MAT2", "MAT3", 
     * "MAT4"] 
     * 
     */
    private String type;
    /**
     * Maximum value of each component in this attribute. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Maximum number of items: 16<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     */
    private Number[] max;
    /**
     * Minimum value of each component in this attribute. (optional)<br> 
     * Minimum number of items: 1<br> 
     * Maximum number of items: 16<br> 
     * Array elements:<br> 
     * &nbsp;&nbsp;The elements of this array (optional) 
     * 
     */
    private Number[] min;

    /**
     * The ID of the bufferView. (required) 
     * 
     * @param bufferView The bufferView to set
     * @throws NullPointerException If the given value is <code>null</code>
     * 
     */
    public void setBufferView(String bufferView) {
        if (bufferView == null) {
            throw new NullPointerException((("Invalid value for bufferView: "+ bufferView)+", may not be null"));
        }
        this.bufferView = bufferView;
    }

    /**
     * The ID of the bufferView. (required) 
     * 
     * @return The bufferView
     * 
     */
    public String getBufferView() {
        return this.bufferView;
    }

    /**
     * The offset relative to the start of the bufferView in bytes. 
     * (required)<br> 
     * Minimum: 0 (inclusive) 
     * 
     * @param byteOffset The byteOffset to set
     * @throws NullPointerException If the given value is <code>null</code>
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setByteOffset(Integer byteOffset) {
        if (byteOffset == null) {
            throw new NullPointerException((("Invalid value for byteOffset: "+ byteOffset)+", may not be null"));
        }
        if (byteOffset< 0) {
            throw new IllegalArgumentException("byteOffset < 0");
        }
        this.byteOffset = byteOffset;
    }

    /**
     * The offset relative to the start of the bufferView in bytes. 
     * (required)<br> 
     * Minimum: 0 (inclusive) 
     * 
     * @return The byteOffset
     * 
     */
    public Integer getByteOffset() {
        return this.byteOffset;
    }

    /**
     * The stride, in bytes, between attributes referenced by this accessor. 
     * (optional)<br> 
     * Default: 0<br> 
     * Minimum: 0 (inclusive)<br> 
     * Maximum: 255 (inclusive) 
     * 
     * @param byteStride The byteStride to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setByteStride(Integer byteStride) {
        if (byteStride == null) {
            this.byteStride = byteStride;
            return ;
        }
        if (byteStride > 255) {
            throw new IllegalArgumentException("byteStride > 255");
        }
        if (byteStride< 0) {
            throw new IllegalArgumentException("byteStride < 0");
        }
        this.byteStride = byteStride;
    }

    /**
     * The stride, in bytes, between attributes referenced by this accessor. 
     * (optional)<br> 
     * Default: 0<br> 
     * Minimum: 0 (inclusive)<br> 
     * Maximum: 255 (inclusive) 
     * 
     * @return The byteStride
     * 
     */
    public Integer getByteStride() {
        return this.byteStride;
    }

    /**
     * Returns the default value of the byteStride<br> 
     * @see #getByteStride 
     * 
     * @return The default byteStride
     * 
     */
    public Integer defaultByteStride() {
        return  0;
    }

    /**
     * The datatype of components in the attribute. (required)<br> 
     * Valid values: [5120, 5121, 5122, 5123, 5126] 
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
        if (((((componentType!= 5120)&&(componentType!= 5121))&&(componentType!= 5122))&&(componentType!= 5123))&&(componentType!= 5126)) {
            throw new IllegalArgumentException((("Invalid value for componentType: "+ componentType)+", valid: [5120, 5121, 5122, 5123, 5126]"));
        }
        this.componentType = componentType;
    }

    /**
     * The datatype of components in the attribute. (required)<br> 
     * Valid values: [5120, 5121, 5122, 5123, 5126] 
     * 
     * @return The componentType
     * 
     */
    public Integer getComponentType() {
        return this.componentType;
    }

    /**
     * The number of attributes referenced by this accessor. (required)<br> 
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
     * The number of attributes referenced by this accessor. (required)<br> 
     * Minimum: 1 (inclusive) 
     * 
     * @return The count
     * 
     */
    public Integer getCount() {
        return this.count;
    }

    /**
     * Specifies if the attribute is a scalar, vector, or matrix. 
     * (required)<br> 
     * Valid values: ["SCALAR", "VEC2", "VEC3", "VEC4", "MAT2", "MAT3", 
     * "MAT4"] 
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
            throw new IllegalArgumentException((("Invalid value for type: "+ type)+", valid: [\"SCALAR\", \"VEC2\", \"VEC3\", \"VEC4\", \"MAT2\", \"MAT3\", \"MAT4\"]"));
        }
        this.type = type;
    }

    /**
     * Specifies if the attribute is a scalar, vector, or matrix. 
     * (required)<br> 
     * Valid values: ["SCALAR", "VEC2", "VEC3", "VEC4", "MAT2", "MAT3", 
     * "MAT4"] 
     * 
     * @return The type
     * 
     */
    public String getType() {
        return this.type;
    }

    /**
     * Maximum value of each component in this attribute. (optional)<br> 
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
     * Maximum value of each component in this attribute. (optional)<br> 
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
     * Minimum value of each component in this attribute. (optional)<br> 
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
     * Minimum value of each component in this attribute. (optional)<br> 
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

}
