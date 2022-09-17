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

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * The raw data of a glTF asset, consisting of the raw JSON data and the binary
 * data. This data is still independent of the actual glTF version, and covers 
 * both standard (JSON) and binary glTF.<br>
 * <br>
 * Instances of this class are returned by the {@link RawGltfDataReader}.
 * Clients will usually not use this class directly. 
 */
public final class RawGltfData
{
    /**
     * The JSON data
     */
    private final ByteBuffer jsonData;
    
    /**
     * The optional binary data
     */
    private final ByteBuffer binaryData;
    
    /**
     * Default constructor. References to the buffers will be stored.
     * So they should have their position and limit set accordingly,
     * and may not be modified after being passed to this constructor.
     * 
     * @param jsonData The JSON data
     * @param binaryData The optional binary data
     */
    public RawGltfData(ByteBuffer jsonData, ByteBuffer binaryData)
    {
        this.jsonData = Objects.requireNonNull(
            jsonData, "The jsonData may not be null");
        this.binaryData = binaryData;
    }
    
    /**
     * Returns the JSON string from this data
     * 
     * @return The JSON string
     */
    public String getJsonString() 
    {
        byte jsonDataArray[] = new byte[jsonData.capacity()];
        jsonData.slice().get(jsonDataArray);
        String jsonString = new String(jsonDataArray, Charset.forName("UTF-8"));
        return jsonString;
    }
    
    /**
     * Returns the JSON data. <br>
     * <br>
     * The returned buffer will be a slice of the data that is stored 
     * internally. This means that changes of the data will affect this
     * instance, but changes of the position or limit of the returned
     * buffer will not affect this instance. 
     * 
     * @return The JSON data
     */
    public ByteBuffer getJsonData()
    {
        return Buffers.createSlice(jsonData);
    }
    
    /**
     * Returns the binary data. This may be <code>null</code> if the asset
     * was created from a JSON string only.<br>
     * <br>
     * The returned buffer will be a slice of the data that is stored 
     * internally. This means that changes of the data will affect this
     * instance, but changes of the position or limit of the returned
     * buffer will not affect this instance.
     * 
     * @return The binary data
     */
    public ByteBuffer getBinaryData()
    {
        return Buffers.createSlice(binaryData);
    }
}
