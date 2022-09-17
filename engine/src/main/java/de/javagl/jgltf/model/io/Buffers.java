/*
 * www.javagl.de - JglTF
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
package de.javagl.jgltf.model.io;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Collection;

/**
 * Utility methods related to buffers
 */
public class Buffers
{
    /**
     * Returns the contents of the given byte buffer as a string, using
     * the platform's default charset, or <code>null</code> if the given 
     * buffer is <code>null</code>. The position and limit of the given
     * buffer will be unaffected by this call.
     * 
     * @param byteBuffer The byte buffer
     * @return The data as a string
     */
    public static String readAsString(ByteBuffer byteBuffer)
    {
        if (byteBuffer == null)
        {
            return null;
        }
        byte array[] = new byte[byteBuffer.capacity()];
        byteBuffer.slice().get(array);
        return new String(array);
    }

    /**
     * Create a slice of the given byte buffer, using its current position
     * and limit. The returned slice will have the same byte order as the
     * given buffer. If the given buffer is <code>null</code>, then
     * <code>null</code> will be returned.
     * 
     * @param byteBuffer The byte buffer
     * @return The slice
     */
    public static ByteBuffer createSlice(ByteBuffer byteBuffer)
    {
        if (byteBuffer == null)
        {
            return null;
        }
        return byteBuffer.slice().order(byteBuffer.order());
    }
    
    /**
     * Create a slice of the given byte buffer, in the specified range.
     * The returned buffer will have the same byte order as the given
     * buffer. If the given buffer is <code>null</code>, then
     * <code>null</code> will be returned.
     * 
     * @param byteBuffer The byte buffer
     * @param position The position where the slice should start
     * @param length The length of the slice
     * @return The slice
     * @throws IllegalArgumentException If the range that is specified
     * by the position and length are not valid for the given buffer
     */
    public static ByteBuffer createSlice(
        ByteBuffer byteBuffer, int position, int length)
    {
        if (byteBuffer == null)
        {
            return null;
        }
        int oldPosition = byteBuffer.position();
        int oldLimit = byteBuffer.limit();
        try
        {
            int newLimit = position + length;
            if (newLimit > byteBuffer.capacity())
            {
                throw new IllegalArgumentException(
                    "The new limit is " + newLimit + ", but the capacity is "
                    + byteBuffer.capacity());
            }
            byteBuffer.limit(newLimit);
            byteBuffer.position(position);
            ByteBuffer slice = byteBuffer.slice();
            slice.order(byteBuffer.order());
            return slice;
        }
        finally
        {
            byteBuffer.limit(oldLimit);
            byteBuffer.position(oldPosition);
        }
    }
    
    /**
     * Creates a new, direct byte buffer that contains the given data,
     * with little-endian byte order
     *  
     * @param data The data
     * @return The byte buffer
     */
    public static ByteBuffer create(byte data[])
    {
        return create(data, 0, data.length);
    }
    
    /**
     * Creates a new, direct byte buffer that contains the specified range
     * of the given data, with little-endian byte order
     *  
     * @param data The data
     * @param offset The offset in the data array
     * @param length The length of the range
     * @return The byte buffer
     */
    public static ByteBuffer create(byte data[], int offset, int length)
    {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(length);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(data, offset, length);
        byteBuffer.position(0);
        return byteBuffer;
    }
    
    /**
     * Create a new direct byte buffer with the given size, and little-endian
     * byte order.
     * 
     * @param size The size of the buffer
     * @return The byte buffer
     * @throws IllegalArgumentException If the given size is negative
     */
    public static ByteBuffer create(int size)
    {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(size);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        return byteBuffer;
    }
    
    /**
     * Create an input stream from the given byte buffer, starting at its
     * current position, up to its current limit. Reading the returned
     * stream will advance the position of the buffer. If this is not 
     * desired, a slice of the buffer may be passed to this method.
     * 
     * @param byteBuffer The buffer
     * @return The input stream
     */
    public static InputStream createByteBufferInputStream(ByteBuffer byteBuffer)
    {
        return new ByteBufferInputStream(byteBuffer);
    }
    
    
    /**
     * Create a direct byte buffer with native byte order whose contents is
     * a concatenation of the given byte buffers. If the given collection
     * is <code>null</code> or empty, then a 0-byte buffer will be created.
     * The given collection may not contain <code>null</code> elements.
     * 
     * @param byteBuffers The input byte buffers
     * @return The concatenated byte buffer
     */
    public static ByteBuffer concat(
        Collection<? extends ByteBuffer> byteBuffers)
    {
        if (byteBuffers == null || byteBuffers.isEmpty())
        {
            return ByteBuffer.allocateDirect(0).order(ByteOrder.nativeOrder());
        }
        int resultCapacity = byteBuffers.stream()
            .mapToInt(ByteBuffer::capacity)
            .reduce(0, (a, b) -> a + b);
        ByteBuffer newByteBuffer = ByteBuffer
            .allocateDirect(resultCapacity)
            .order(ByteOrder.nativeOrder());
        for (ByteBuffer byteBuffer : byteBuffers)
        {
            newByteBuffer.put(byteBuffer.slice());
        }
        newByteBuffer.position(0);
        return newByteBuffer;
    }
    
    
    /**
     * Create a new direct byte buffer with native byte order that has the
     * same contents as the given float buffer.
     *  
     * @param buffer The input buffer
     * @return The new byte buffer
     */
    public static ByteBuffer createByteBufferFrom(FloatBuffer buffer)
    {
        ByteBuffer byteBuffer = 
            ByteBuffer.allocateDirect(buffer.capacity() * Float.BYTES);
        FloatBuffer floatBuffer = 
            byteBuffer.order(ByteOrder.nativeOrder()).asFloatBuffer();
        floatBuffer.put(buffer.slice());
        return byteBuffer;
    }

    /**
     * Create a new direct byte buffer with native byte order that has the
     * same contents as the given int buffer.
     *  
     * @param buffer The input buffer
     * @return The new byte buffer
     */
    public static ByteBuffer createByteBufferFrom(IntBuffer buffer)
    {
        ByteBuffer byteBuffer = 
            ByteBuffer.allocateDirect(buffer.capacity() * Integer.BYTES);
        IntBuffer intBuffer = 
            byteBuffer.order(ByteOrder.nativeOrder()).asIntBuffer();
        intBuffer.put(buffer.slice());
        return byteBuffer;
    }

    /**
     * Create a new direct byte buffer with native byte order that has the
     * same contents as the given short buffer.
     *  
     * @param buffer The input buffer
     * @return The new byte buffer
     */
    public static ByteBuffer createByteBufferFrom(ShortBuffer buffer)
    {
        ByteBuffer byteBuffer = 
            ByteBuffer.allocateDirect(buffer.capacity() * Short.BYTES);
        ShortBuffer shortBuffer = 
            byteBuffer.order(ByteOrder.nativeOrder()).asShortBuffer();
        shortBuffer.put(buffer.slice());
        return byteBuffer;
    }
    
    /**
     * Convert the given input buffer into a direct byte buffer with native
     * byte order, by casting all elements to <code>byte</code>.
     * 
     * @param buffer The input buffer
     * @return The byte buffer
     */
    public static ByteBuffer castToByteBuffer(IntBuffer buffer)
    {
        ByteBuffer byteBuffer = 
            ByteBuffer.allocateDirect(buffer.capacity())
            .order(ByteOrder.nativeOrder());
        for (int i = 0; i < buffer.capacity(); i++)
        {
            byteBuffer.put(i, (byte) buffer.get(i));
        }
        return byteBuffer;
    }    
    
    /**
     * Convert the given input buffer into a direct byte buffer with native
     * byte order that contains the elements of the given input buffer,
     * casted to <code>short</code>.
     * 
     * @param buffer The input buffer
     * @return The short buffer
     */
    public static ByteBuffer castToShortByteBuffer(IntBuffer buffer)
    {
        ByteBuffer byteBuffer = 
            ByteBuffer.allocateDirect(buffer.capacity() * Short.BYTES);
        ShortBuffer shortBuffer = 
            byteBuffer.order(ByteOrder.nativeOrder()).asShortBuffer();
        for (int i = 0; i < buffer.capacity(); i++)
        {
            shortBuffer.put(i, (short) buffer.get(i));
        }
        return byteBuffer;
    }
    

    /**
     * Creates a copy of the given buffer, as a direct buffer with the 
     * same byte order, and the given capacity. If the given capacity
     * is smaller than that of the given buffer, the copy will be 
     * truncated. If it is larger, the additional bytes will be 
     * initialized to zero.
     *  
     * @param buffer The input buffer
     * @param newCapacity The new capacity
     * @return The copy
     */
    public static ByteBuffer copyOf(ByteBuffer buffer, int newCapacity)
    {
        ByteBuffer copy = ByteBuffer.allocateDirect(newCapacity);
        copy.order(buffer.order());
        if (newCapacity < buffer.capacity())
        {
            copy.slice().put(createSlice(buffer, 0, newCapacity));
        }
        else
        {
            copy.slice().put(createSlice(buffer));
        }
        return copy;
    }
    
    /**
     * Perform a copy of the specified buffer range, analogously to 
     * <code>System#arraycopy</code>.
     * 
     * @param src The source buffer
     * @param srcPos The source position
     * @param dst The destination buffer
     * @param dstPos The destination position
     * @param length The length
     * @throws IndexOutOfBoundsException If the indices are invalid.
     */
    public static void bufferCopy(
        ByteBuffer src, int srcPos,
        ByteBuffer dst, int dstPos,
        int length)
    {
        // This could be optimized for large lengths, by using bulk operations
        // on slices of the buffers
        for (int i = 0; i < length; i++)
        {
            byte b = src.get(srcPos + i);
            dst.put(dstPos + i, b);
        }
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private Buffers()
    {
        // Private constructor to prevent instantiation
    }

}
