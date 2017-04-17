/*****************************************************************************
 * LittleEndianConverter.java
 * Java Source
 *
 * This source is licensed under the GNU LGPL v2.1.
 * Please read http://www.gnu.org/copyleft/lgpl.html for more information.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 *
 * Copyright (c) 2001, 2002 Dipl. Ing. P. Szawlowski
 * University of Vienna, Dept. of Medical Computer Sciences
 ****************************************************************************/

package org.andresoviedo.app.model3D.services.stl;

import java.io.*;

/**
 * Utility to convert little endain data to big endian data.
 * <p>
 * TODO: Extend to convert big endian to little endain data and write to
 * <code>OutputStream</code>
 *
 * @author  Dipl. Ing. Paul Szawlowski -
 *          University of Vienna, Dept. of Medical Computer Sciences
 * @version $Revision: 1.3 $
 */
public class LittleEndianConverter
{
    /**
     * Converts little endian data in <code>srcBuffer</code> to big endian
     * signed short (2 bytes long) data.
     * @param srcBuffer Data in little endian format which shall be converted.
     *      The size of the array must be at least 2.
     * @param destBuffer Buffer to store the converted data. The size of the
     *      array must be at least <code>destOffset</code> +
     *      <code>destLength</code>.
     * @param srcLength Number of bytes of <code>srcBuffer</code> which shall
     *      be processed. Must be <= length of <code>srcBuffer</code>.
     * @param destOffset Offset for writing converted data in
     *      <code>destBuffer</code>.
     * @param destLength Max. number of data to be written in
     *      <code>destBuffer</code>
     * @return (even) number of processed bytes of srcBuffer
     */
    static public int convertToBigEndian
    (
            final byte[ ]   srcBuffer,
            final short[ ]  destBuffer,
            final int       srcLength,
            final int       destOffset,
            final int       destLength
    )
    {
        return convertToBigEndian
                (
                        srcBuffer,
                        destBuffer,
                        srcLength,
                        destOffset,
                        destLength,
                        ( short )0xFF
                );
    }

    /**
     * Converts little endian data in <code>srcBuffer</code> to big endian
     * short (2 bytes long) data. Significant bits can be masked, e. g. to
     * get unsigned 7 bit values use <code>0x7F</code> as mask.
     * @param srcBuffer Data in little endian format which shall be converted.
     *      The size of the array must be at least 2.
     * @param destBuffer Buffer to store the converted data. The size of the
     *      array must be at least <code>destOffset</code> +
     *      <code>destLength</code>.
     * @param srcLength Number of bytes of <code>srcBuffer</code> which shall
     *      be processed. Must be <= length of <code>srcBuffer</code>.
     * @param destOffset Offset for writing converted data in
     *      <code>destBuffer</code>.
     * @param destLength Max. number of data to be written in
     *      <code>destBuffer</code>
     * @param mask Mask for significant bits. Set significant bits to 1.
     * @return (even) number of processed bytes of srcBuffer
     */
    static public int convertToBigEndian
    (
            final byte[ ]   srcBuffer,
            final short[ ]  destBuffer,
            final int       srcLength,
            final int       destOffset,
            final int       destLength,
            final short     mask
    )
    {
        final int length = Math.min( destLength * 2, ( srcLength / 2 ) * 2 );
        for( int i = 0; i < length; i += 2 )
        {
            final int tmp =
                    ( srcBuffer[ i ] & 0xFF | ( srcBuffer[ i + 1 ] << 8 ) ) & mask;
            destBuffer[ ( i / 2 ) + destOffset ] = ( short )tmp;
        }
        return length;
    }

    /**
     * Converts little endian data in <code>srcBuffer</code> to big endian
     * signed integer (4 bytes long) data.
     * @param srcBuffer Data in little endian format which shall be converted.
     *      The size of the array must be at least 4.
     * @param destBuffer Buffer to store the converted data. The size of the
     *      array must be at least <code>destOffset</code> +
     *      <code>destLength</code>.
     * @param srcLength Number of bytes of <code>srcBuffer</code> which shall
     *      be processed. Must be <= length of <code>srcBuffer</code>.
     * @param destOffset Offset for writing converted data in
     *      <code>destBuffer</code>.
     * @param destLength Maximum number of data to be written in
     *      <code>destBuffer</code>
     * @return number of processed bytes of srcBuffer (multiple of 4 )
     */
    public static int convertToBigEndian
    (
            final byte[ ]   srcBuffer,
            final int[ ]    destBuffer,
            final int       srcLength,
            final int       destOffset,
            final int       destLength
    )
    {
        return convertToBigEndian
                (
                        srcBuffer,
                        destBuffer,
                        srcLength,
                        destOffset,
                        destLength,
                        0xFFFFFFFF
                );
    }

    /**
     * Converts little endian data in <code>srcBuffer</code> to big endian
     * integer (4 bytes long) data. Significant bits can be masked, e. g. to
     * get unsigned 31 bit values use <code>0x7FFFFFFF</code> as mask.
     * @param srcBuffer Data in little endian format which shall be converted.
     *      The size of the array must be at least 4.
     * @param destBuffer Buffer to store the converted data. The size of the
     *      array must be at least <code>destOffset</code> +
     *      <code>destLength</code>.
     * @param srcLength Number of bytes of <code>srcBuffer</code> which shall
     *      be processed. Must be <= length of <code>srcBuffer</code>.
     * @param destOffset Offset for writing converted data in
     *      <code>destBuffer</code>.
     * @param destLength Maximum number of data to be written in
     *      <code>destBuffer</code>
     * @param mask Mask for significant bits. Set significant bits to 1.
     * @return number of processed bytes of srcBuffer (multiple of 4 )
     */
    public static int convertToBigEndian
    (
            final byte[ ]   srcBuffer,
            final int[ ]    destBuffer,
            final int       srcLength,
            final int       destOffset,
            final int       destLength,
            final int       mask
    )
    {
        final int length = Math.min( destLength * 4, ( srcLength / 4 ) * 4 );
        for( int i = 0; i < length; i += 4 )
        {
            destBuffer[ ( i / 4 ) + destOffset ] = ( srcBuffer[ i ] & 0xFF
                    | ( srcBuffer[ i + 1 ] << 8 ) & 0xFF00
                    | ( srcBuffer[ i + 2 ] << 16 ) & 0xFF0000
                    | ( srcBuffer[ i + 3 ] << 24 ) ) & mask;
        }
        return length;
    }

    /**
     * Converts little endian data in <code>srcBuffer</code> to big endian
     * signed integer data with a user defined block size of 2, 3, or 4 bytes.
     * <p>
     * @param srcBuffer Data in little endian format which shall be converted.
     *      The size of the array must be at least <code>blockSize</code>.
     * @param destBuffer Buffer to store the converted data. The size of the
     *      array must be at least <code>destOffset</code> +
     *      <code>destLength</code>.
     * @param srcLength Number of bytes of <code>srcBuffer</code> which shall
     *      be processed. Must be <= length of <code>srcBuffer</code>.
     * @param destOffset Offset for writing converted data in
     *      <code>destBuffer</code>.
     * @param destLength Maximum number of data to be written in
     *      <code>destBuffer</code>
     * @param blockSize May be 2, 3 or 4.
     * @return number of processed bytes of srcBuffer (multiple of
     *      <code>blockSize</code>)
     */
    public static int convertToBigEndian
    (
            final int       blockSize,
            final byte[ ]   srcBuffer,
            final int[ ]    destBuffer,
            final int       srcLength,
            final int       destOffset,
            final int       destLength
    )
    {
        return convertToBigEndian
                (
                        blockSize,
                        srcBuffer,
                        destBuffer,
                        srcLength,
                        destOffset,
                        destLength,
                        0xFFFFFFFF
                );
    }

    /**
     * Converts little endian data in <code>srcBuffer</code> to big endian
     * signed integer data with a user defined block size of 2, 3, or 4 bytes.
     * Significant bits can be masked, e. g. to get unsigned 16 bit values use
     * <code>0xFFFF</code> as mask.<p>
     * @param blockSize May be 2, 3 or 4.
     * @param srcBuffer Data in little endian format which shall be converted.
     *      The size of the array must be at least <code>blockSize</code>.
     * @param destBuffer Buffer to store the converted data. The size of the
     *      array must be at least <code>destOffset</code> +
     *      <code>destLength</code>.
     * @param srcLength Number of bytes of <code>srcBuffer</code> which shall
     *      be processed. Must be <= length of <code>srcBuffer</code>.
     * @param destOffset Offset for writing converted data in
     *      <code>destBuffer</code>.
     * @param destLength Maximum number of data to be written in
     *      <code>destBuffer</code>
     * @param mask Mask for significant bits. Set significant bits to 1.
     * @return number of processed bytes of srcBuffer (multiple of
     *      <code>blockSize</code>)
     */
    public static int convertToBigEndian
    (
            final int       blockSize,
            final byte[ ]   srcBuffer,
            final int[ ]    destBuffer,
            final int       srcLength,
            final int       destOffset,
            final int       destLength,
            final int       mask
    )
    {
        final int length = Math.min
                (
                        destLength * blockSize,
                        ( srcLength / blockSize ) * blockSize
                );
        if( blockSize == 2 )
        {
            for( int i = 0; i < length; i += 2 )
            {
                destBuffer[ ( i / 2 ) + destOffset ] =
                        ( srcBuffer[ i ] & 0xFF | ( srcBuffer[ i + 1 ] << 8 ) )
                                & mask;
            }
            return length;
        }
        else if( blockSize == 3 )
        {
            for( int i = 0; i < length; i += 3 )
            {
                destBuffer[ ( i / 3 ) + destOffset ] = ( srcBuffer[ i ] & 0xFF
                        | ( srcBuffer[ i + 1 ]  << 8 ) & 0xFF00
                        | ( srcBuffer[ i + 2 ]  << 24 ) )  & mask;
            }
            return length;
        }
        else if( blockSize == 4 )
        {
            return convertToBigEndian
                    (
                            srcBuffer,
                            destBuffer,
                            srcLength,
                            destOffset,
                            destLength,
                            mask
                    );
        }
        else
        {
            return 0;
        }
    }

    /**
     * Reads little endian data from an <code>InputStream</code> and converts
     * it to big endian signed short (2 bytes long) data.
     * @param readBuffer Auxilary Buffer to be used to read from
     *      <code>stream</code>. Choose an appropriate size (multiple of 2)
     *      depending on the size of the stream. The size of the array must be
     *      at least 2.
     * @param destBuffer Buffer to store the converted data. The size of the
     *      array must be at least <code>destOffset</code> +
     *      <code>destLength</code>.
     * @param destOffset Offset for writing converted data in
     *      <code>destBuffer</code>.
     * @param destLength Max. number of data to be written in
     *      <code>destBuffer</code>
     * @param stream <code>InputStream</code> to read from.
     * @return number of data elements written in <code>destBuffer</code>
     *      (will be <= destLength).
     */
    public static int read
    (
            final byte[ ]       readBuffer,
            final short[ ]      destBuffer,
            final int           destOffset,
            final int           destLength,
            final InputStream   stream
    )
            throws IOException
    {
        return read
                (
                        readBuffer,
                        destBuffer,
                        destOffset,
                        destLength,
                        stream,
                        ( short )0xFF
                );
    }
    /**
     * Reads little endian data from an <code>InputStream</code> and converts
     * it to big endian short (2 bytes long) data. Significant bits can be
     * masked, e. g. to get unsigned 7 bit values use <code>0x7F</code> as mask.
     * @param readBuffer Auxilary Buffer to be used to read from
     *      <code>stream</code>. Choose an appropriate size (multiple of 2)
     *      depending on the size of the stream. The size of the array must be
     *      at least 2.
     * @param destBuffer Buffer to store the converted data. The size of the
     *      array must be at least <code>destOffset</code> +
     *      <code>destLength</code>.
     * @param destOffset Offset for writing converted data in
     *      <code>destBuffer</code>.
     * @param destLength Max. number of data to be written in
     *      <code>destBuffer</code>
     * @param stream <code>InputStream</code> to read from.
     * @param mask Mask for significant bits. Set significant bits to 1.
     * @return number of data elements written in <code>destBuffer</code>
     *      (will be <= destLength).
     */
    public static int read
    (
            final byte[ ]       readBuffer,
            final short[ ]      destBuffer,
            final int           destOffset,
            final int           destLength,
            final InputStream   stream,
            final short         mask
    )
            throws IOException
    {
        int numOfBytesRead = 0;
        int numOfData = 0;
        int offset = 0;
        final int length = ( readBuffer.length / 2 ) * 2;
        while( ( numOfBytesRead >= 0 ) && ( numOfData < destLength ) )
        {
            // calculate how many more bytes can be read so that destBuffer
            // does not overflow; enables to continue reading from same stream
            // without data loss
            final int maxBytesToRead =
                    Math.min( ( destLength - numOfData ) * 2, length );
            numOfBytesRead =
                    stream.read( readBuffer, offset, maxBytesToRead - offset );
            int numOfProcessedBytes = convertToBigEndian
                    (
                            readBuffer,
                            destBuffer,
                            numOfBytesRead + offset,
                            destOffset + numOfData,
                            destLength - numOfData,
                            mask
                    );
            // if an uneven number of bytes was read from stream
            if( numOfBytesRead - numOfProcessedBytes == 1 )
            {
                offset = 1;
                readBuffer[ 0 ] = readBuffer[ numOfProcessedBytes ];
            }
            else
            {
                offset = 0;
            }
            numOfData += ( numOfProcessedBytes / 2 );
        }
        return numOfData;
    }

    /**
     * Reads little endian data from an <code>InputStream</code> and converts
     * it to big endian signed int (4 bytes long) data.
     * @param readBuffer Auxilary Buffer to be used to read from
     *      <code>stream</code>. Choose an appropriate size (multiple of 4)
     *      depending on the size of the stream. The size of the array must be
     *      at least 4.
     * @param destBuffer Buffer to store the converted data. The size of the
     *      array must be at least <code>destOffset</code> +
     *      <code>destLength</code>.
     * @param destOffset Offset for writing converted data in
     *      <code>destBuffer</code>.
     * @param destLength Max. number of data to be written in
     *      <code>destBuffer</code>
     * @param stream <code>InputStream</code> to read from.
     * @return number of data elements written in <code>destBuffer</code>
     *      (will be <= destLength).
     */
    public static int read
    (
            final byte[ ]       readBuffer,
            final int[ ]        destBuffer,
            final int           destOffset,
            final int           destLength,
            final InputStream   stream
    )
            throws IOException
    {
        return read
                (
                        readBuffer,
                        destBuffer,
                        destOffset,
                        destLength,
                        stream,
                        0xFFFFFFFF
                );
    }

    /**
     * Reads little endian data from an <code>InputStream</code> and converts
     * it to big endian int (4 bytes long) data. Significant bits can be masked,
     * e. g. to get unsigned 31 bit values use <code>0x7FFFFFFF</code> as mask.
     * @param readBuffer Auxilary Buffer to be used to read from
     *      <code>stream</code>. Choose an appropriate size (multiple of 4)
     *      depending on the size of the stream. The size of the array must be
     *      at least 4.
     * @param destBuffer Buffer to store the converted data. The size of the
     *      array must be at least <code>destOffset</code> +
     *      <code>destLength</code>.
     * @param destOffset Offset for writing converted data in
     *      <code>destBuffer</code>.
     * @param destLength Max. number of data to be written in
     *      <code>destBuffer</code>
     * @param stream <code>InputStream</code> to read from.
     * @param mask Mask for significant bits. Set significant bits to 1.
     * @return number of data elements written in <code>destBuffer</code>
     *      (will be <= destLength).
     */
    public static int read
    (
            final byte[ ]       readBuffer,
            final int[ ]        destBuffer,
            final int           destOffset,
            final int           destLength,
            final InputStream   stream,
            final int           mask
    )
            throws IOException
    {
        int numOfBytesRead = 0;
        int numOfData = 0;
        int offset = 0;
        final int length = ( readBuffer.length / 4 ) * 4;
        while( ( numOfBytesRead >= 0 ) && ( numOfData < destLength ) )
        {
            // calculate how many more bytes can be read so that destBuffer
            // does not overflow; enables to continue reading from same stream
            // without data loss
            final int maxBytesToRead =
                    Math.min( ( destLength - numOfData ) * 4, length );
            numOfBytesRead =
                    stream.read( readBuffer, offset, maxBytesToRead - offset );
            int numOfProcessedBytes = convertToBigEndian
                    (
                            readBuffer,
                            destBuffer,
                            numOfBytesRead + offset,
                            destOffset + numOfData,
                            destLength - numOfData,
                            mask
                    );
            final int diff = numOfBytesRead - numOfProcessedBytes;
            // if an number of bytes was read from stream was not a multiple
            // of 4
            offset = 0;
            if(diff == 1 )
            {
                offset = 1;
                readBuffer[ 0 ] = readBuffer[ numOfProcessedBytes ];
            }
            if( diff == 2 )
            {
                offset = 2;
                readBuffer[ 1 ] = readBuffer[ numOfProcessedBytes + 1 ];
            }
            if( diff == 3 )
            {
                offset = 3;
                readBuffer[ 2 ] = readBuffer[ numOfProcessedBytes + 2 ];
            }
            numOfData += ( numOfProcessedBytes / 4 );
        }
        return numOfData;
    }

    /**
     * Reads little endian data from an <code>InputStream</code> and converts
     * it to to big endian signed integer data with a user defined block size
     * of 1, 2, 3, or 4 bytes (1 is here for conveniance).<p>
     * @param blockSize May be 1, 2, 3 or 4.
     * @param readBuffer Auxilary Buffer to be used to read from
     *      <code>stream</code>. Choose an appropriate size (multiple of 4)
     *      depending on the size of the stream. The size of the array must be
     *      at least <code>blockSize</code>.
     * @param destBuffer Buffer to store the converted data. The size of the
     *      array must be at least <code>destOffset</code> +
     *      <code>destLength</code>.
     * @param destOffset Offset for writing converted data in
     *      <code>destBuffer</code>.
     * @param destLength Max. number of data to be written in
     *      <code>destBuffer</code>
     * @param stream <code>InputStream</code> to read from.
     * @return number of data elements written in <code>destBuffer</code>
     *      (will be <= destLength).
     */
    public static int read
    (
            final int           blockSize,
            final byte[ ]       readBuffer,
            final int[ ]        destBuffer,
            final int           destOffset,
            final int           destLength,
            final InputStream   stream
    )
            throws IOException
    {
        return read
                (
                        blockSize,
                        readBuffer,
                        destBuffer,
                        destOffset,
                        destLength,
                        stream,
                        0xFFFFFFFF
                );
    }

    /**
     * Reads little endian data from an <code>InputStream</code> and converts
     * it to to big endian signed integer data with a user defined block size
     * of 1, 2, 3, or 4 bytes (1 is here for conveniance). Significant bits can
     * be masked, e. g. to get unsigned 16 bit values use <code>0xFFFF</code>
     * as mask.<p>
     * @param blockSize May be 1, 2, 3 or 4.
     * @param readBuffer Auxilary Buffer to be used to read from
     *      <code>stream</code>. Choose an appropriate size (multiple of 4)
     *      depending on the size of the stream. The size of the array must be
     *      at least <code>blockSize</code>.
     * @param destBuffer Buffer to store the converted data. The size of the
     *      array must be at least <code>destOffset</code> +
     *      <code>destLength</code>.
     * @param destOffset Offset for writing converted data in
     *      <code>destBuffer</code>.
     * @param destLength Max. number of data to be written in
     *      <code>destBuffer</code>
     * @param stream <code>InputStream</code> to read from.
     * @param mask Mask for significant bits. Set significant bits to 1.
     * @return number of data elements written in <code>destBuffer</code>
     *      (will be <= destLength).
     */
    public static int read
    (
            final int           blockSize,
            final byte[ ]       readBuffer,
            final int[ ]        destBuffer,
            final int           destOffset,
            final int           destLength,
            final InputStream   stream,
            final int           mask
    )
            throws IOException
    {
        if( blockSize == 2 )
        {
            return read2ByteBlock
                    (
                            readBuffer,
                            destBuffer,
                            destOffset,
                            destLength,
                            stream,
                            mask
                    );
        }
        else if( blockSize == 3 )
        {
            return read3ByteBlock
                    (
                            readBuffer,
                            destBuffer,
                            destOffset,
                            destLength,
                            stream,
                            mask
                    );
        }
        else if( blockSize == 4 )
        {
            return read
                    (
                            readBuffer,
                            destBuffer,
                            destOffset,
                            destLength,
                            stream,
                            mask
                    );
        }
        else
        {
            return 0;
        }
    }

    /**
     * Reads 4 bytes in little endian format and converts it to a signed int.<p>
     * @throws IOException if EOF occurs and only one, 2 or 3 bytes were read or
     *      if error during reading occurs
     */
    public static int read4ByteBlock( final InputStream stream )
            throws java.io.IOException
    {
        return read( stream ) & 0xFF
                | ( read( stream ) << 8 ) & 0xFF00
                | ( read( stream ) << 16 ) & 0xFF0000
                | ( read( stream ) << 24 );
    }

    /**
     * Reads 2 bytes in little endian format and converts it to a signed int.<p>
     * To Convert it to an unsigned int <code>&</code> the result with
     * <code>0xFFFF</code>.
     * @throws IOException if EOF occurs and only one bytes was read or
     *      if error during reading occurs
     */
    public static int read2ByteBlock( final InputStream stream )
            throws java.io.IOException
    {
        return read( stream ) & 0xFF
                | ( read( stream ) << 8 );
    }

    /**
     * Reads 3 bytes in little endian format and converts it to a signed int.<p>
     * To Convert it to an unsigned int <code>&</code> the result with
     * <code>0xFFFFFF</code>.
     * @throws IOException if EOF occurs and only one or 2 bytes were read or
     *      if error during reading occurs
     */
    public static int read3ByteBlock( final InputStream stream )
            throws java.io.IOException
    {
        return read( stream ) & 0xFF
                | ( read( stream ) << 8 ) & 0xFF00
                | ( read( stream ) << 16 );
    }

    private static int read2ByteBlock
            (
                    final byte[ ]       readBuffer,
                    final int[ ]        destBuffer,
                    final int           destOffset,
                    final int           destLength,
                    final InputStream   stream,
                    final int           mask
            )
            throws IOException
    {
        int numOfBytesRead = 0;
        int numOfData = 0;
        int offset = 0;
        final int length = ( readBuffer.length / 2 ) * 2;
        while( ( numOfBytesRead >= 0 ) && ( numOfData < destLength ) )
        {
            // calculate how many more bytes can be read so that destBuffer
            // does not overflow; enables to continue reading from same stream
            // without data loss
            final int maxBytesToRead =
                    Math.max( ( destLength - numOfData ) * 2, length );
            numOfBytesRead =
                    stream.read( readBuffer, offset, maxBytesToRead - offset );
            int numOfProcessedBytes = convertToBigEndian
                    (
                            2,
                            readBuffer,
                            destBuffer,
                            numOfBytesRead + offset,
                            destOffset + numOfData,
                            destLength - numOfData,
                            mask
                    );
            // if an uneven number of bytes was read from stream
            if( numOfBytesRead - numOfProcessedBytes == 1 )
            {
                offset = 1;
                readBuffer[ 0 ] = readBuffer[ numOfProcessedBytes ];
            }
            else
            {
                offset = 0;
            }
            numOfData += ( numOfProcessedBytes / 2 );
        }
        return numOfData;
    }

    private static int read3ByteBlock
            (
                    final byte[ ]       readBuffer,
                    final int[ ]        destBuffer,
                    final int           destOffset,
                    final int           destLength,
                    final InputStream   stream,
                    final int           mask
            )
            throws IOException
    {
        int numOfBytesRead = 0;
        int numOfData = 0;
        int offset = 0;
        final int length = ( readBuffer.length / 3 ) * 3;
        while( ( numOfBytesRead >= 0 ) && ( numOfData < destLength ) )
        {
            // calculate how many more bytes can be read so that destBuffer
            // does not overflow; enables to continue reading from same stream
            // without data loss
            final int maxBytesToRead =
                    Math.max( ( destLength - numOfData ) * 3, length );
            numOfBytesRead =
                    stream.read( readBuffer, offset, maxBytesToRead - offset );
            int numOfProcessedBytes = convertToBigEndian
                    (
                            3,
                            readBuffer,
                            destBuffer,
                            numOfBytesRead + offset,
                            destOffset + numOfData,
                            destLength - numOfData,
                            mask
                    );
            final int diff = numOfBytesRead - numOfProcessedBytes;
            // if an number of bytes was read from stream was not a multiple
            // of 3
            offset = 0;
            if(diff == 1 )
            {
                offset = 1;
                readBuffer[ 0 ] = readBuffer[ numOfProcessedBytes ];
            }
            if( diff == 2 )
            {
                offset = 2;
                readBuffer[ 1 ] = readBuffer[ numOfProcessedBytes + 1 ];
            }
            numOfData += ( numOfProcessedBytes / 3 );
        }
        return numOfData;
    }

    private static int read( final InputStream stream ) throws IOException
    {
        final int tempValue = stream.read( );
        if( tempValue == -1 )
        {
            throw new IOException( "Filesize does not match blocksize" );
        }
        return tempValue;
    }
}