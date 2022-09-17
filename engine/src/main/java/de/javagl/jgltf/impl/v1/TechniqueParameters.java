/*
 * glTF JSON model
 * 
 * Do not modify this class. It is automatically generated
 * with JsonModelGen (https://github.com/javagl/JsonModelGen)
 * Copyright (c) 2016 Marco Hutter - http://www.javagl.de
 */

package de.javagl.jgltf.impl.v1;



/**
 * An attribute or uniform input to a technique, and an optional semantic 
 * and value. 
 * 
 * Auto-generated for technique.parameters.schema.json 
 * 
 */
public class TechniqueParameters
    extends GlTFProperty
{

    /**
     * When defined, the parameter is an array of count elements of the 
     * specified type. Otherwise, the parameter is not an array. 
     * (optional)<br> 
     * Minimum: 1 (inclusive) 
     * 
     */
    private Integer count;
    /**
     * The id of the node whose transform is used as the parameter's value. 
     * (optional) 
     * 
     */
    private String node;
    /**
     * The datatype. (required)<br> 
     * Valid values: [5120, 5121, 5122, 5123, 5124, 5125, 5126, 35664, 35665, 
     *  35666, 35667, 35668, 35669, 35670, 35671, 35672, 35673, 35674, 35675, 
     *  35676, 35678] 
     * 
     */
    private Integer type;
    /**
     * Identifies a parameter with a well-known meaning. (optional) 
     * 
     */
    private String semantic;
    /**
     * The value of the parameter. (optional) 
     * 
     */
    private Object value;

    /**
     * When defined, the parameter is an array of count elements of the 
     * specified type. Otherwise, the parameter is not an array. 
     * (optional)<br> 
     * Minimum: 1 (inclusive) 
     * 
     * @param count The count to set
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setCount(Integer count) {
        if (count == null) {
            this.count = count;
            return ;
        }
        if (count< 1) {
            throw new IllegalArgumentException("count < 1");
        }
        this.count = count;
    }

    /**
     * When defined, the parameter is an array of count elements of the 
     * specified type. Otherwise, the parameter is not an array. 
     * (optional)<br> 
     * Minimum: 1 (inclusive) 
     * 
     * @return The count
     * 
     */
    public Integer getCount() {
        return this.count;
    }

    /**
     * The id of the node whose transform is used as the parameter's value. 
     * (optional) 
     * 
     * @param node The node to set
     * 
     */
    public void setNode(String node) {
        if (node == null) {
            this.node = node;
            return ;
        }
        this.node = node;
    }

    /**
     * The id of the node whose transform is used as the parameter's value. 
     * (optional) 
     * 
     * @return The node
     * 
     */
    public String getNode() {
        return this.node;
    }

    /**
     * The datatype. (required)<br> 
     * Valid values: [5120, 5121, 5122, 5123, 5124, 5125, 5126, 35664, 35665, 
     *  35666, 35667, 35668, 35669, 35670, 35671, 35672, 35673, 35674, 35675, 
     *  35676, 35678] 
     * 
     * @param type The type to set
     * @throws NullPointerException If the given value is <code>null</code>
     * @throws IllegalArgumentException If the given value does not meet
     * the given constraints
     * 
     */
    public void setType(Integer type) {
        if (type == null) {
            throw new NullPointerException((("Invalid value for type: "+ type)+", may not be null"));
        }
        if (((((((((((((((((((((type!= 5120)&&(type!= 5121))&&(type!= 5122))&&(type!= 5123))&&(type!= 5124))&&(type!= 5125))&&(type!= 5126))&&(type!= 35664))&&(type!= 35665))&&(type!= 35666))&&(type!= 35667))&&(type!= 35668))&&(type!= 35669))&&(type!= 35670))&&(type!= 35671))&&(type!= 35672))&&(type!= 35673))&&(type!= 35674))&&(type!= 35675))&&(type!= 35676))&&(type!= 35678)) {
            throw new IllegalArgumentException((("Invalid value for type: "+ type)+", valid: [5120, 5121, 5122, 5123, 5124, 5125, 5126, 35664, 35665, 35666, 35667, 35668, 35669, 35670, 35671, 35672, 35673, 35674, 35675, 35676, 35678]"));
        }
        this.type = type;
    }

    /**
     * The datatype. (required)<br> 
     * Valid values: [5120, 5121, 5122, 5123, 5124, 5125, 5126, 35664, 35665, 
     *  35666, 35667, 35668, 35669, 35670, 35671, 35672, 35673, 35674, 35675, 
     *  35676, 35678] 
     * 
     * @return The type
     * 
     */
    public Integer getType() {
        return this.type;
    }

    /**
     * Identifies a parameter with a well-known meaning. (optional) 
     * 
     * @param semantic The semantic to set
     * 
     */
    public void setSemantic(String semantic) {
        if (semantic == null) {
            this.semantic = semantic;
            return ;
        }
        this.semantic = semantic;
    }

    /**
     * Identifies a parameter with a well-known meaning. (optional) 
     * 
     * @return The semantic
     * 
     */
    public String getSemantic() {
        return this.semantic;
    }

    /**
     * The value of the parameter. (optional) 
     * 
     * @param value The value to set
     * 
     */
    public void setValue(Object value) {
        if (value == null) {
            this.value = value;
            return ;
        }
        this.value = value;
    }

    /**
     * The value of the parameter. (optional) 
     * 
     * @return The value
     * 
     */
    public Object getValue() {
        return this.value;
    }

}
