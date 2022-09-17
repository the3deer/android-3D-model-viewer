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
package de.javagl.jgltf.model.io;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * A class for resolving the external data of {@link GltfReference} objects
 * that are obtained from a {@link GltfAsset}
 */
public class GltfReferenceResolver
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(GltfReferenceResolver.class.getName());

    /**
     * Calls {@link #resolve(GltfReference, Function)} with each 
     * {@link GltfReference} of the given list, resolving the
     * URIs of the references against the given base URI
     * 
     * @param references The {@link GltfReference} objects
     * @param baseUri The base URI that references will be resolved against
     */
    public static void resolveAll(
        Iterable<? extends GltfReference> references, URI baseUri)
    {
        Objects.requireNonNull(references, "The references may not be null");
        Objects.requireNonNull(baseUri, "The baseUri may not be null");
        Function<String, ByteBuffer> uriResolver = 
            UriResolvers.createBaseUriResolver(baseUri);
        resolveAll(references, uriResolver);
    }

    /**
     * Calls {@link #resolve(GltfReference, Function)} with each
     * {@link GltfReference} of the given list, resolving the
     * Paths of the references against the given base Path
     *
     * @param references The {@link GltfReference} objects
     * @param basePath The base Path that references will be resolved against
     */
    public static void resolveAll(
        Iterable<? extends GltfReference> references, Path basePath)
    {
        Objects.requireNonNull(references, "The references may not be null");
        Objects.requireNonNull(basePath, "The basePath may not be null");
        Function<String, ByteBuffer> uriResolver =
            UriResolvers.createBasePathResolver(basePath);
        resolveAll(references, uriResolver);
    }

    /**
     * Calls {@link #resolve(GltfReference, Function)} with each 
     * {@link GltfReference} of the given list
     * 
     * @param references The {@link GltfReference} objects
     * @param uriResolver The function for resolving a URI string
     * into a byte buffer
     */
    public static void resolveAll(
        Iterable<? extends GltfReference> references, 
        Function<? super String, ? extends ByteBuffer> uriResolver)
    {
        Objects.requireNonNull(references, "The references may not be null");
        Objects.requireNonNull(uriResolver, "The uriResolver may not be null");

        for (GltfReference reference : references) 
        {
            resolve(reference, uriResolver);
        }
    }
    
    /**
     * Pass the {@link GltfReference#getUri() URI} of the given 
     * {@link GltfReference} to the given resolver function, 
     * and and pass the resulting byte buffer to the 
     * {@link GltfReference#getTarget() target} of the reference. If
     * a URI cannot be resolved, a warning will be printed.
     * 
     * @param reference The {@link GltfReference}
     * @param uriResolver The function for resolving a URI string
     * into an byte buffer
     */
    public static void resolve(GltfReference reference, 
        Function<? super String, ? extends ByteBuffer> uriResolver)
    {
        Objects.requireNonNull(reference, "The reference may not be null");
        Objects.requireNonNull(uriResolver, "The uriResolver may not be null");

        String uri = reference.getUri();
        ByteBuffer byteBuffer = uriResolver.apply(uri);
        if (byteBuffer == null)
        {
            logger.warning("Could not resolve URI " + uri);
        }
        Consumer<ByteBuffer> target = reference.getTarget();
        target.accept(byteBuffer);
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private GltfReferenceResolver()
    {
        // Private constructor to prevent instantiation
    }
    
}
