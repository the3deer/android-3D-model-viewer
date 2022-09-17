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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * IO utility methods
 */
public class IO
{
    /**
     * Convert the given URI string into an absolute URI, resolving it
     * against the given base URI if necessary
     * 
     * @param baseUri The base URI
     * @param uriString The URI string
     * @return The absolute URI
     * @throws IOException If the URI string is not valid
     */
    public static URI makeAbsolute(URI baseUri, String uriString) 
        throws IOException
    {
        try
        {
            String escapedUriString = uriString.replaceAll(" ", "%20");
            URI uri = new URI(escapedUriString);
            if (uri.isAbsolute())
            {
                return uri;
            }
            return baseUri.resolve(escapedUriString);
        }
        catch (URISyntaxException e)
        {
            throw new IOException("Invalid URI string: " + uriString, e);
        }
    }
    
    /**
     * Convert the given URI string into an absolute path, resolving it
     * against the given base path if necessary
     *
     * @param basePath The base path
     * @param uriString The URI string
     * @return The absolute path
     * @throws IOException If the URI string is not valid
     */
    public static Path makeAbsolute(Path basePath, String uriString)
            throws IOException
    {
        try
        {
            String escapedUriString = uriString.replaceAll(" ", "%20");

            URI uri = new URI(escapedUriString);
            if (uri.isAbsolute())
            {
                return Paths.get(uri).toAbsolutePath();
            }
            return basePath.resolve(escapedUriString).toAbsolutePath();
        }
        catch (URISyntaxException e)
        {
            throw new IOException("Invalid URI string: " + uriString, e);
        }
    }

    /**
     * Returns the URI describing the parent of the given URI. If the 
     * given URI describes a file, this will return the URI of the 
     * directory. If the given URI describes a directory, this will
     * return the URI of the parent directory
     *  
     * @param uri The URI
     * @return The parent URI
     */
    public static URI getParent(URI uri)
    {
        if (uri.getPath().endsWith("/"))
        {
            return uri.resolve("..");        
        }
        return uri.resolve(".");
    }

    /**
     * Returns the path describing the parent of the given path. If the
     * given path describes a file, this will return the path of the
     * directory. If the given path describes a directory, this will
     * return the path of the parent directory
     *
     * @param path The path
     * @return The parent path
     */
    public static Path getParent(Path path)
    {
        return path.getParent();
    }

    /**
     * Returns whether the given URI is a data URI. 
     * 
     * @param uri The URI
     * @return Whether the string is a data URI
     */
    public static boolean isDataUri(URI uri)
    {
        return "data".equalsIgnoreCase(uri.getScheme());
    }
    
    /**
     * Returns whether the given string is a data URI. If the given string
     * is <code>null</code>, then <code>false</code> will be returned. 
     * 
     * @param uriString The URI string
     * @return Whether the string is a data URI
     */
    public static boolean isDataUriString(String uriString)
    {
        if (uriString == null)
        {
            return false;
        }
        try
        {
            URI uri = new URI(uriString);
            return isDataUri(uri);
        } 
        catch (URISyntaxException e)
        {
            return false;
        }
    }
    
    /**
     * Tries to extract the "file name" that is referred to with the 
     * given URI. This is the part behind the last <code>"/"</code> slash
     * that appears in the string representation of the given URI. If no 
     * file name can be extracted, then the string representation of the 
     * URI is returned.
     * 
     * @param uri The URI
     * @return The file name
     */
    public static String extractFileName(URI uri)
    {
        String s = uri.toString();
        int lastSlashIndex = s.lastIndexOf('/');
        if (lastSlashIndex != -1)
        {
            return s.substring(lastSlashIndex + 1);
        }
        return s;
    }
    
    /**
     * Returns whether the resource that is described with the given URI
     * exists. If an IO exception occurs during this check, this method
     * will simply return <code>false</code>. 
     * 
     * @param uri The URI
     * @return Whether the resource exists
     */
    public static boolean existsUnchecked(URI uri)
    {
        try
        {
            return exists(uri);
        } 
        catch (IOException e)
        {
            return false;
        }
    }
   
    /**
     * Returns whether the resource that is described with the given URI
     * exists
     * 
     * @param uri The URI
     * @return Whether the resource exists
     * @throws IOException If an IO error occurs. This usually implies that
     * the return value would be <code>false</code>...
     */
    private static boolean exists(URI uri) throws IOException
    {
        URL url = uri.toURL();
        URLConnection connection = url.openConnection();
        if (connection instanceof HttpURLConnection)
        {
            HttpURLConnection httpConnection = (HttpURLConnection)connection;
            httpConnection.setRequestMethod("HEAD");
            int responseCode = httpConnection.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_OK;
        }
        String path = uri.getPath();
        return new File(path).exists();
    }
    
    
    /**
     * Try to obtain the content length from the given URI. Returns -1
     * if the content length can not be determined.
     * 
     * @param uri The URI
     * @return The content length
     */
    public static long getContentLength(URI uri)
    {
        try
        {
            URLConnection connection = uri.toURL().openConnection();
            return connection.getContentLengthLong();
        }
        catch (IOException e)
        {
            return -1;
        }
    }
    
    /**
     * Creates an input stream from the given URI, which may either be
     * an actual (absolute) URI, or a data URI with base64 encoded data
     * 
     * @param uri The URI
     * @return The input stream
     * @throws IOException If the stream can not be opened
     */
    public static InputStream createInputStream(URI uri) throws IOException
    {
        if ("data".equalsIgnoreCase(uri.getScheme()))
        {
            byte data[] = readDataUri(uri.toString());
            return new ByteArrayInputStream(data);
        }
        try
        {
            return uri.toURL().openStream();
        }
        catch (MalformedURLException e)
        {
            throw new IOException(e);
        }
    }

    /**
     * Creates an input stream from the given Path, which may either be
     * an actual (absolute) Path, or a data Path with base64 encoded data
     *
     * @param path The Path
     * @return The input stream
     * @throws IOException If the stream can not be opened
     */
    public static InputStream createInputStream(Path path) throws IOException
    {
        if ("data".equalsIgnoreCase(path.toUri().getScheme()))
        {
            byte data[] = readDataUri(path.toUri().toString());
            return new ByteArrayInputStream(data);
        }
        try
        {
            return path.toUri().toURL().openStream();
        }
        catch (MalformedURLException e)
        {
            throw new IOException(e);
        }
    }

    /**
     * Read the data from the given URI as a byte array. The data may either
     * be an actual URI, or a data URI with base64 encoded data.
     * 
     * @param uri The URI
     * @return The byte array
     * @throws IOException If an IO error occurs
     */
    public static byte[] read(URI uri) 
        throws IOException
    {
        try (InputStream inputStream = createInputStream(uri))
        {
            byte data[] = readStream(inputStream);
            return data;
        }
    }


    /**
     * Read the base 64 encoded data from the given data URI string.
     * The data is assumed to start after the <code>base64,</code> part
     * of the URI string, which must have the form 
     * <code>data:...;base64,...</code>
     * 
     * @param uriString The URI string
     * @return The data
     * @throws IllegalArgumentException If the given string is not a valid
     * Base64 encoded data URI string
     */
    public static byte[] readDataUri(String uriString)
    {
        String encoding = "base64,";
        int encodingIndex = uriString.indexOf(encoding);
        if (encodingIndex < 0)
        {
            throw new IllegalArgumentException(
                "The given URI string is not a base64 encoded "
                + "data URI string: " + uriString);
        }
        int contentStartIndex = encodingIndex + encoding.length();
        byte data[] = Base64.getDecoder().decode(
            uriString.substring(contentStartIndex));
        return data;
    }
    
    /**
     * Reads the data from the given inputStream and returns it as
     * a byte array. The caller is responsible for closing the stream.
     * 
     * @param inputStream The input stream to read
     * @return The data from the inputStream
     * @throws IOException If an IO error occurs, or if the thread that
     * executes this method is interrupted.
     */
    public static byte[] readStream(InputStream inputStream) throws IOException
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte buffer[] = new byte[16384];
        while (true)
        {
            int read = inputStream.read(buffer);
            if (read == -1)
            {
                break;
            }
            baos.write(buffer, 0, read);
            if (Thread.currentThread().isInterrupted())
            {
                throw new IOException("Interrupted while reading stream",
                    new InterruptedException());
            }
        }
        baos.flush();
        return baos.toByteArray();
    }
    
    /**
     * Read the specified number of bytes from the given input stream, 
     * writing them into the given array at the given offset
     * 
     * @param inputStream The input stream
     * @param data The array to write the data to
     * @param offset The offset inside the target array
     * @param numBytesToRead The number of bytes to read 
     * @throws IOException If an IO error occurs, or the end of the input
     * stream was encountered before the requested number of bytes have
     * been read 
     * @throws IllegalArgumentException If the given offset is negative, or
     * the sum of the given offset and the number of bytes to read is larger
     * than the length of the given array
     */
    static void read(InputStream inputStream, byte data[], int offset, 
        int numBytesToRead) throws IOException
    {
        if (offset < 0)
        {
            throw new IllegalArgumentException(
                "Array offset is negative: " + offset);
        }
        if (offset + numBytesToRead > data.length)
        {
            throw new IllegalArgumentException(
                "Cannot write " + numBytesToRead
                + " bytes into an array of length " + data.length
                + " with an offset of " + offset);
        }
        int totalNumBytesRead = 0;
        while (true)
        {
            int read = inputStream.read(
                data, offset + totalNumBytesRead, 
                numBytesToRead - totalNumBytesRead);
            if (read == -1)
            {
                throw new IOException(
                    "Could not read " + numBytesToRead + " bytes");
            }
            totalNumBytesRead += read;
            if (totalNumBytesRead == numBytesToRead)
            {
                break;
            }
        }
    }
    
    /**
     * Read from the given input stream, writing into the given array,
     * until the array is filled.
     * 
     * @param inputStream The input stream
     * @param data The array to write the data to
     * @throws IOException If an IO error occurs, or the end of the input
     * stream was encountered before the requested number of bytes have
     * been read 
     */
    public static void read(InputStream inputStream, byte data[]) 
        throws IOException
    {
        read(inputStream, data, 0, data.length);
    }
    
    
    /**
     * Private constructor to prevent instantiation
     */
    private IO()
    {
        // Private constructor to prevent instantiation
    }
    
}