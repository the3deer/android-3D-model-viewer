/*
 *
 * Copyright 2015-2016 Marco Hutter - http://www.javagl.de
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

import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Methods to create instances of the accessor data utility classes
 * that allow a <i>typed</i> access to the data that is contained in the
 * buffer view that the accessor refers to.<br>
 * <br>
 * Unless otherwise noted, none of the arguments to these methods may 
 * be <code>null</code>.
 */
public class AccessorDatas
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(AccessorDatas.class.getName());
    
    
    /**
     * Create the {@link AccessorData} for the given {@link AccessorModel}
     * 
     * @param accessorModel The {@link AccessorModel}
     * @return The {@link AccessorData}
     */
    public static AccessorData create(AccessorModel accessorModel)
    {
        BufferViewModel bufferViewModel = accessorModel.getBufferViewModel();
        ByteBuffer bufferViewData = bufferViewModel.getBufferViewData();
        return create(accessorModel, bufferViewData);
    }

    /**
     * Create the {@link AccessorData} for the given {@link AccessorModel}
     * that refers to the data from the given buffer.
     * 
     * @param accessorModel The {@link AccessorModel}
     * @param byteBuffer The byte buffer containing the data
     * @return The {@link AccessorData}
     */
    public static AccessorData create(
        AccessorModel accessorModel, ByteBuffer byteBuffer)
    {
        if (accessorModel.getComponentDataType() == byte.class)
        {
            return createByte(accessorModel, byteBuffer);
        }
        if (accessorModel.getComponentDataType() == short.class)
        {
            return createShort(accessorModel, byteBuffer);
        }
        if (accessorModel.getComponentDataType() == int.class)
        {
            return createInt(accessorModel, byteBuffer);
        }
        if (accessorModel.getComponentDataType() == float.class)
        {
            return createFloat(accessorModel, byteBuffer);
        }
        // Should never happen
        logger.severe("Invalid component data type: "
            + accessorModel.getComponentDataType());
        return null;
    }
    
    /**
     * Create an {@link AccessorData} depending on the given component type.
     * This will return an {@link AccessorByteData}, {@link AccessorShortData},
     * {@link AccessorIntData} or {@link AccessorFloatData}
     * 
     * @param componentType The component type, as a GL constant (for example,
     * <code>GL_UNSIGNED_SHORT</code> or <code>GL_FLOAT</code>)
     * @param bufferViewData The buffer view data that the accessor refers to
     * @param byteOffset The byte offset for the accessor
     * @param count The count (number of elements) for the accessor
     * @param elementType The {@link ElementType}
     * For example, if the accessor type is <code>"VEC3"</code>, then this
     * will be 3
     * @param byteStride The optional byte stride for the accessor data
     * @return The {@link AccessorData}
     * @throws IllegalArgumentException If the given component type is
     * not a valid GL constant
     */
    public static AccessorData create(
        int componentType, ByteBuffer bufferViewData, int byteOffset, 
        int count, ElementType elementType, Integer byteStride)
    {
        if (isByteType(componentType))
        {
            return new AccessorByteData(
                componentType, bufferViewData, byteOffset, count, 
                elementType, byteStride);
        }
        if (isShortType(componentType))
        {
            return new AccessorShortData(
                componentType, bufferViewData, byteOffset, count, 
                elementType, byteStride);
        }
        if (isIntType(componentType))
        {
            return new AccessorIntData(
                componentType, bufferViewData, byteOffset, count, 
                elementType, byteStride);
        }
        if (isFloatType(componentType))
        {
            return new AccessorFloatData(
                componentType, bufferViewData, byteOffset, count, 
                elementType, byteStride);
        }
        throw new IllegalArgumentException(
            "Not a valid component type: " + componentType);
    }
    
    
    
    /**
     * Returns whether the given constant is <code>GL_BYTE</code> or
     * <code>GL_UNSIGNED_BYTE</code>. 
     * 
     * @param type The type constant
     * @return Whether the type is a <code>byte</code> type
     */
    public static boolean isByteType(int type)
    {
        return 
            type == GltfConstants.GL_BYTE ||
            type == GltfConstants.GL_UNSIGNED_BYTE;
    }
    
    /**
     * Returns whether the given constant is <code>GL_SHORT</code> or
     * <code>GL_UNSIGNED_SHORT</code>. 
     * 
     * @param type The type constant
     * @return Whether the type is a <code>short</code> type
     */
    public static boolean isShortType(int type)
    {
        return 
            type == GltfConstants.GL_SHORT ||
            type == GltfConstants.GL_UNSIGNED_SHORT;
    }

    /**
     * Returns whether the given constant is <code>GL_INT</code> or
     * <code>GL_UNSIGNED_INT</code>. 
     * 
     * @param type The type constant
     * @return Whether the type is an <code>int</code> type
     */
    public static boolean isIntType(int type)
    {
        return 
            type == GltfConstants.GL_INT ||
            type == GltfConstants.GL_UNSIGNED_INT;
    }

    /**
     * Returns whether the given constant is <code>GL_FLOAT</code>.
     * 
     * @param type The type constant
     * @return Whether the type is a <code>float</code> type
     */
    public static boolean isFloatType(int type)
    {
        return type == GltfConstants.GL_FLOAT;
    }

    /**
     * Returns whether the given constant is <code>GL_UNSIGNED_BYTE</code>,
     * <code>GL_UNSIGNED_SHORT</code> or <code>GL_UNSIGNED_INT</code>.
     * 
     * @param type The type constant
     * @return Whether the type is an unsigned type
     */
    static boolean isUnsignedType(int type)
    {
        return 
            type == GltfConstants.GL_UNSIGNED_BYTE ||
            type == GltfConstants.GL_UNSIGNED_SHORT ||
            type == GltfConstants.GL_UNSIGNED_INT;
    }
    
    
    /**
     * Make sure that the given type is <code>GL_BYTE</code> or 
     * <code>GL_UNSIGNED_BYTE</code>, and throw an 
     * <code>IllegalArgumentException</code> if this is not the case.
     * 
     * @param type The type constant
     * @throws IllegalArgumentException If the given type is not 
     * <code>GL_BYTE</code> or <code>GL_UNSIGNED_BYTE</code>
     */
    static void validateByteType(int type)
    {
        if (!isByteType(type))
        {
            throw new IllegalArgumentException(
                "The type is not GL_BYTE or GL_UNSIGNED_BYTE, but " + 
                GltfConstants.stringFor(type));
        }
    }

    /**
     * Make sure that the given type is <code>GL_SHORT</code> or 
     * <code>GL_UNSIGNED_SHORT</code>, and throw an 
     * <code>IllegalArgumentException</code> if this is not the case.
     * 
     * @param type The type constant
     * @throws IllegalArgumentException If the given type is not 
     * <code>GL_SHORT</code> or <code>GL_UNSIGNED_BYTE</code>
     */
    static void validateShortType(int type)
    {
        if (!isShortType(type))
        {
            throw new IllegalArgumentException(
                "The type is not GL_SHORT or GL_UNSIGNED_SHORT, but " + 
                GltfConstants.stringFor(type));
        }
    }
    
    /**
     * Make sure that the given type is <code>GL_INT</code> or 
     * <code>GL_UNSIGNED_INT</code>, and throw an 
     * <code>IllegalArgumentException</code> if this is not the case.
     * 
     * @param type The type constant
     * @throws IllegalArgumentException If the given type is not 
     * <code>GL_INT</code> or <code>GL_UNSIGNED_INT</code>
     */
    static void validateIntType(int type)
    {
        if (!isIntType(type))
        {
            throw new IllegalArgumentException(
                "The type is not GL_INT or GL_UNSIGNED_INT, but " + 
                GltfConstants.stringFor(type));
        }
    }

    /**
     * Make sure that the given type is <code>GL_FLOAT</code>, and throw an 
     * <code>IllegalArgumentException</code> if this is not the case.
     * 
     * @param type The type constant
     * @throws IllegalArgumentException If the given type is not 
     * <code>GL_FLOAT</code>
     */
    static void validateFloatType(int type)
    {
        if (!isFloatType(type))
        {
            throw new IllegalArgumentException(
                "The type is not GL_FLOAT, but " + 
                GltfConstants.stringFor(type));
        }
    }
    
    
    
    /**
     * Creates an {@link AccessorByteData} for the given {@link AccessorModel}
     * 
     * @param accessorModel The {@link AccessorModel}
     * @return The {@link AccessorByteData}
     * @throws IllegalArgumentException If the 
     * {@link AccessorModel#getComponentType() component type} of the given
     * accessor is not <code>GL_BYTE</code> or <code>GL_UNSIGNED_BYTE</code>
     */
    static AccessorByteData createByte(AccessorModel accessorModel)
    {
        BufferViewModel bufferViewModel = accessorModel.getBufferViewModel();
        return createByte(accessorModel, bufferViewModel.getBufferViewData());
    }
    
    /**
     * Creates an {@link AccessorByteData} for the given {@link AccessorModel}
     * 
     * @param accessorModel The {@link AccessorModel}
     * @param bufferViewByteBuffer The byte buffer of the 
     * {@link BufferViewModel} referenced by the {@link AccessorModel}
     * @return The {@link AccessorByteData}
     * @throws NullPointerException If any argument is <code>null</code>
     * @throws IllegalArgumentException If the 
     * {@link AccessorModel#getComponentType() component type} of the given
     * accessorModel is not <code>GL_BYTE</code> or 
     * <code>GL_UNSIGNED_BYTE</code>
     */
    private static AccessorByteData createByte(
        AccessorModel accessorModel, ByteBuffer bufferViewByteBuffer)
    {
        return new AccessorByteData(accessorModel.getComponentType(), 
            bufferViewByteBuffer,
            accessorModel.getByteOffset(),
            accessorModel.getCount(),
            accessorModel.getElementType(),
            accessorModel.getByteStride());
    }
    
    /**
     * Creates an {@link AccessorShortData} for the given {@link AccessorModel}
     * 
     * @param accessorModel The {@link AccessorModel}
     * @return The {@link AccessorShortData}
     * @throws IllegalArgumentException If the 
     * {@link AccessorModel#getComponentType() component type} of the given
     * accessorModel is not <code>GL_SHORT</code> or 
     * <code>GL_UNSIGNED_SHORT</code> 
     */
    static AccessorShortData createShort(AccessorModel accessorModel)
    {
        BufferViewModel bufferViewModel = accessorModel.getBufferViewModel();
        return createShort(accessorModel, bufferViewModel.getBufferViewData());
    }
    
    /**
     * Creates an {@link AccessorShortData} for the given {@link AccessorModel}
     * 
     * @param accessorModel The {@link AccessorModel}
     * @param bufferViewByteBuffer The byte buffer of the 
     * {@link BufferViewModel} referenced by the {@link AccessorModel}
     * @return The {@link AccessorShortData}
     * @throws NullPointerException If any argument is <code>null</code>
     * @throws IllegalArgumentException If the 
     * {@link AccessorModel#getComponentType() component type} of the given
     * accessorModel is not <code>GL_SHORT</code> or 
     * <code>GL_UNSIGNED_SHORT</code>
     */
    private static AccessorShortData createShort(
        AccessorModel accessorModel, ByteBuffer bufferViewByteBuffer)
    {
        return new AccessorShortData(accessorModel.getComponentType(), 
            bufferViewByteBuffer,
            accessorModel.getByteOffset(),
            accessorModel.getCount(),
            accessorModel.getElementType(),
            accessorModel.getByteStride());
    }
    

    /**
     * Creates an {@link AccessorIntData} for the given {@link AccessorModel}
     * 
     * @param accessorModel The {@link AccessorModel}
     * @return The {@link AccessorIntData}
     * @throws IllegalArgumentException If the 
     * {@link AccessorModel#getComponentType() component type} of the given
     * accessorModel is not <code>GL_INT</code> or <code>GL_UNSIGNED_INT</code> 
     */
    static AccessorIntData createInt(AccessorModel accessorModel)
    {
        BufferViewModel bufferViewModel = accessorModel.getBufferViewModel();
        return createInt(accessorModel, bufferViewModel.getBufferViewData());
    }
    
    /**
     * Creates an {@link AccessorIntData} for the given {@link AccessorModel}
     * 
     * @param accessorModel The {@link AccessorModel}
     * @param bufferViewByteBuffer The byte buffer of the 
     * {@link BufferViewModel} referenced by the {@link AccessorModel}
     * @return The {@link AccessorIntData}
     * @throws NullPointerException If any argument is <code>null</code>
     * @throws IllegalArgumentException If the 
     * {@link AccessorModel#getComponentType() component type} of the given
     * accessorModel is not <code>GL_INT</code> or <code>GL_UNSIGNED_INT</code>
     */
    private static AccessorIntData createInt(
        AccessorModel accessorModel, ByteBuffer bufferViewByteBuffer)
    {
        return new AccessorIntData(accessorModel.getComponentType(), 
            bufferViewByteBuffer,
            accessorModel.getByteOffset(),
            accessorModel.getCount(),
            accessorModel.getElementType(),
            accessorModel.getByteStride());
    }
    
    /**
     * Creates an {@link AccessorFloatData} for the given {@link AccessorModel}
     * 
     * @param accessorModel The {@link AccessorModel}
     * @return The {@link AccessorFloatData}
     * @throws IllegalArgumentException If the 
     * {@link AccessorModel#getComponentType() component type} of the given
     * accessorModel is not <code>GL_FLOAT</code>
     */
    public static AccessorFloatData createFloat(AccessorModel accessorModel)
    {
        BufferViewModel bufferViewModel = accessorModel.getBufferViewModel();
        return createFloat(accessorModel, bufferViewModel.getBufferViewData());
    }
    
    /**
     * Creates an {@link AccessorFloatData} for the given {@link AccessorModel}
     * 
     * @param accessorModel The {@link AccessorModel}
     * @param bufferViewByteBuffer The byte buffer of the 
     * {@link BufferViewModel} referenced by the {@link AccessorModel}
     * @return The {@link AccessorFloatData}
     * @throws NullPointerException If any argument is <code>null</code>
     * @throws IllegalArgumentException If the 
     */
    private static AccessorFloatData createFloat(
        AccessorModel accessorModel, ByteBuffer bufferViewByteBuffer)
    {
        return new AccessorFloatData(accessorModel.getComponentType(), 
            bufferViewByteBuffer,
            accessorModel.getByteOffset(),
            accessorModel.getCount(),
            accessorModel.getElementType(),
            accessorModel.getByteStride());
    }

    /**
     * Validate that the given {@link AccessorModel} parameters are valid for
     * accessing a buffer with the given capacity
     * 
     * @param byteOffset The byte offset 
     * @param numElements The number of elements
     * @param numBytesPerElement The number of bytes per element
     * @param byteStridePerElement The byte stride
     * @param bufferCapacity The buffer capacity
     * @throws IllegalArgumentException If the given byte buffer does not
     * have a sufficient capacity
     */
    static void validateCapacity(int byteOffset, int numElements,
        int numBytesPerElement, int byteStridePerElement, int bufferCapacity)
    {
        int expectedCapacity = 
            (numElements - 1) * byteStridePerElement + numBytesPerElement;
        if (expectedCapacity > bufferCapacity)
        {
            throw new IllegalArgumentException(
                "The accessorModel has an offset of " + byteOffset + " and " + 
                numElements + " elements with a byte stride of " + 
                byteStridePerElement + " and a size of " + numBytesPerElement + 
                ", requiring " + expectedCapacity + 
                " bytes, but the buffer view has only " + 
                bufferCapacity + " bytes");
        }
    }
    
    
    /**
     * Compute the the minimum component values of the given 
     * {@link AccessorData}
     * 
     * @param accessorData The {@link AccessorData}
     * @return The minimum values
     * @throws IllegalArgumentException If the given model has an unknown type
     */
    public static Number[] computeMin(AccessorData accessorData)
    {
        if (accessorData instanceof AccessorByteData) 
        {
            AccessorByteData accessorByteData = 
                (AccessorByteData) accessorData;
            return NumberArrays.asNumbers(
                accessorByteData.computeMinInt());
        }
        if (accessorData instanceof AccessorShortData) 
        {
            AccessorShortData accessorShortData = 
                (AccessorShortData) accessorData;
            return NumberArrays.asNumbers(
                accessorShortData.computeMinInt());
        }
        if (accessorData instanceof AccessorIntData) 
        {
            AccessorIntData accessorIntData = 
                (AccessorIntData) accessorData;
            return NumberArrays.asNumbers(
                accessorIntData.computeMinLong());
        }
        if (accessorData instanceof AccessorFloatData) 
        {
            AccessorFloatData accessorFloatData = 
                (AccessorFloatData) accessorData;
            return NumberArrays.asNumbers(
                accessorFloatData.computeMin());
        }
        throw new IllegalArgumentException(
            "Invalid data type: " + accessorData);
    }
    
    /**
     * Compute the the maximum component values of the given 
     * {@link AccessorData}
     * 
     * @param accessorData The {@link AccessorData}
     * @return The maximum values
     * @throws IllegalArgumentException If the given model has an unknown type
     */
    public static Number[] computeMax(AccessorData accessorData)
    {
        if (accessorData instanceof AccessorByteData) 
        {
            AccessorByteData accessorByteData = 
                (AccessorByteData) accessorData;
            return NumberArrays.asNumbers(
                accessorByteData.computeMaxInt());
        }
        if (accessorData instanceof AccessorShortData) 
        {
            AccessorShortData accessorShortData = 
                (AccessorShortData) accessorData;
            return NumberArrays.asNumbers(
                accessorShortData.computeMaxInt());
        }
        if (accessorData instanceof AccessorIntData) 
        {
            AccessorIntData accessorIntData = 
                (AccessorIntData) accessorData;
            return NumberArrays.asNumbers(
                accessorIntData.computeMaxLong());
        }
        if (accessorData instanceof AccessorFloatData) 
        {
            AccessorFloatData accessorFloatData = 
                (AccessorFloatData) accessorData;
            return NumberArrays.asNumbers(
                accessorFloatData.computeMax());
        }
        throw new IllegalArgumentException(
            "Invalid data type: " + accessorData);
    }
    
    /**
     * Creates a (possibly large!) string representation of the given
     * {@link AccessorData}, by calling 
     * {@link AccessorByteData#createString(Locale, String, int)},
     * {@link AccessorShortData#createString(Locale, String, int)},
     * {@link AccessorIntData#createString(Locale, String, int)} or
     * {@link AccessorFloatData#createString(Locale, String, int)},
     * depending on the type of the given data, with an unspecified
     * format string.
     * 
     * @param accessorData The {@link AccessorData}
     * @param elementsPerRow The number of elements per row
     * @return The string
     */
    public static String createString(
        AccessorData accessorData, int elementsPerRow)
    {
        if (accessorData instanceof AccessorByteData) 
        {
            AccessorByteData accessorByteData = 
                (AccessorByteData) accessorData;
            String accessorDataString = 
                accessorByteData.createString(
                    Locale.ENGLISH, "%4d", elementsPerRow);
            return accessorDataString;
        }
        if (accessorData instanceof AccessorShortData) 
        {
            AccessorShortData accessorShortData = 
                (AccessorShortData) accessorData;
            String accessorDataString = 
                accessorShortData.createString(
                    Locale.ENGLISH, "%6d", elementsPerRow);
            return accessorDataString;
        }
        if (accessorData instanceof AccessorIntData) 
        {
            AccessorIntData accessorIntData = 
                (AccessorIntData) accessorData;
            String accessorDataString = 
                accessorIntData.createString(
                    Locale.ENGLISH, "%11d", elementsPerRow);
            return accessorDataString;
        }
        if (accessorData instanceof AccessorFloatData) 
        {
            AccessorFloatData accessorFloatData = 
                (AccessorFloatData) accessorData;
            String accessorDataString = 
                accessorFloatData.createString(
                    Locale.ENGLISH, "%10.5f", elementsPerRow);
            return accessorDataString;
        }
        return "Unknown accessor data type: " + accessorData;
    }
    
    
    /**
     * Private constructor to prevent instantiation
     */
    private AccessorDatas()
    {
        // Private constructor to prevent instantiation
    }
    

}
