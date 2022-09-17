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

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A class for reading the JSON for a glTF asset in a version-agnostic form.
 * It a allows determining the version of the glTF and returning it as 
 * a properly typed object - that is, as a {@link de.javagl.jgltf.impl.v1.GlTF}
 * or a {@link de.javagl.jgltf.impl.v2.GlTF}.<br>
 */
final class GltfReader
{
    /**
     * The logger used in this class
     */
    private static final Logger logger =
        Logger.getLogger(GltfReader.class.getName());

    /**
     * A consumer for {@link JsonError}s that may occur while reading
     * the glTF JSON
     */
    private Consumer<? super JsonError> jsonErrorConsumer = 
        JacksonUtils.loggingJsonErrorConsumer();
    
    /**
     * The Jackson object mapper
     */
    private final ObjectMapper objectMapper;
    
    /**
     * The root node that was read during the last call to {@link #read}
     */
    private JsonNode rootNode;
    
    /**
     * Default constructor
     */
    GltfReader()
    {
        objectMapper = 
            JacksonUtils.createObjectMapper(jsonErrorConsumer);
    }
    
    /**
     * Set the given consumer to receive {@link JsonError}s that may 
     * occur when the JSON part of the glTF is read
     * 
     * @param jsonErrorConsumer The consumer
     */
    void setJsonErrorConsumer(
        Consumer<? super JsonError> jsonErrorConsumer)
    {
        this.jsonErrorConsumer = jsonErrorConsumer;
    }
    
    /**
     * Read the JSON data from the given input stream. The caller is 
     * responsible for closing the given stream. After this method
     * has been called, the version of the glTF may be obtained with
     * {@link #getVersion()}, and the actual asset may be obtained
     * with {@link #getAsGltfV1()} or {@link #getAsGltfV2()}.
     * 
     * @param inputStream The input stream
     * @throws IOException If an IO error occurred
     */
    void read(InputStream inputStream) throws IOException
    {
        JacksonUtils.configure(objectMapper, jsonErrorConsumer);
        rootNode = objectMapper.readTree(inputStream);
    }
    
    /**
     * Returns the version of the glTF, or <code>null</code>
     * if no glTF was read yet
     * 
     * @return The version string
     */
    String getVersion()
    {
        if (rootNode == null)
        {
            return null;
        }
        return getVersion(rootNode);
    }
    
    /**
     * Returns the major version of the glTF, or 0 of no glTF was read yet.
     * 
     * @return The major version number
     */
    int getMajorVersion()
    {
        if (rootNode == null)
        {
            return 0;
        }
        int version[] = VersionUtils.computeMajorMinorPatch(getVersion());
        return version[0];
    }
    
    /**
     * Obtain the glTF as a {@link de.javagl.jgltf.impl.v1.GlTF}, 
     * or <code>null</code> if no glTF was read yet.
     * 
     * @return The glTF.
     * @throws IllegalArgumentException If the glTF that was read is not
     * a valid glTF 1.0
     */
    de.javagl.jgltf.impl.v1.GlTF getAsGltfV1()
    {
        if (rootNode == null)
        {
            return null;
        }
        return objectMapper.convertValue(rootNode, 
            de.javagl.jgltf.impl.v1.GlTF.class);
    }
    
    /**
     * Obtain the glTF as a {@link de.javagl.jgltf.impl.v2.GlTF}, 
     * or <code>null</code> if no glTF was read yet.
     * 
     * @return The glTF.
     * @throws IllegalArgumentException If the glTF that was read is not
     * a valid glTF 2.0
     */
    de.javagl.jgltf.impl.v2.GlTF getAsGltfV2()
    {
        if (rootNode == null)
        {
            return null;
        }
        return objectMapper.convertValue(rootNode, 
            de.javagl.jgltf.impl.v2.GlTF.class);
    }
    
    /**
     * Tries to obtain the <code>rootNode.asset.version</code> string. If
     * either node is <code>null</code>, then <code>"1.0"</code> will be
     * returned.
     * 
     * @param rootNode The root node
     * @return The version 
     */
    private static String getVersion(JsonNode rootNode)
    {
        JsonNode assetNode = rootNode.get("asset");
        if (assetNode == null)
        {
            return "1.0";
        }
        JsonNode versionNode = assetNode.get("version");
        if (versionNode == null)
        {
            return "1.0";
        }
        if (!versionNode.isValueNode())
        {
            logger.warning("No valid 'version' property in 'asset'. " + 
                "Assuming version 1.0");
            return "1.0";
        }
        return versionNode.asText();
    }
    
    
}