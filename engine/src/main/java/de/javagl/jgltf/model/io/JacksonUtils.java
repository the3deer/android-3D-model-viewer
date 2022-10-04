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

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Utility methods related to Jackson JSON parsing.<br>
 * <br>
 * This class should not be considered as part of the API.
 */
public class JacksonUtils
{
    /**
     * The logger used in this class
     */
    private static final Logger logger = 
        Logger.getLogger(JacksonUtils.class.getName());
    
    /**
     * An consumer for {@link JsonError}s that prints log messages
     */
    private static final Consumer<JsonError> LOG_JSON_ERROR_CONSUMER = 
        new Consumer<JsonError>()
    {
        @Override
        public void accept(JsonError jsonError)
        {
            logger.warning("Error: " + jsonError.getMessage() + 
                ", JSON path " + jsonError.getJsonPathString());
        }
    };
    
    /**
     * Create a DeserializationProblemHandler that may be added to an
     * ObjectMapper, and will handle unknown properties by forwarding 
     * the error information to the given consumer, if it is not 
     * <code>null</code>
     * 
     * @param jsonErrorConsumer The consumer for {@link JsonError}s
     * @return The problem handler
     */
    private static DeserializationProblemHandler 
        createDeserializationProblemHandler(
            Consumer<? super JsonError> jsonErrorConsumer)
    {
        return new DeserializationProblemHandler()
        {
            @Override
            public boolean handleUnknownProperty(
                DeserializationContext ctxt, JsonParser jp, 
                JsonDeserializer<?> deserializer, Object beanOrClass, 
                String propertyName) 
                    throws IOException, JsonProcessingException
            {
                if (jsonErrorConsumer != null)
                {
                    jsonErrorConsumer.accept(new JsonError(
                        "Unknown property: " + propertyName, 
                        jp.getParsingContext(), null));
                }
                return super.handleUnknownProperty(
                    ctxt, jp, deserializer, beanOrClass, propertyName);
            }
        };
    }
    
    /**
     * Creates a BeanDeserializerModifier that replaces the 
     * SettableBeanProperties in the BeanDeserializerBuilder with
     * ErrorReportingSettableBeanProperty instances that forward
     * information about errors when setting bean properties to the
     * given consumer. (Don't ask ... )  
     * 
     * @param jsonErrorConsumer The consumer for {@link JsonError}s.
     * If this is <code>null</code>, then no errors will be reported.
     * @return The modifier
     */
    private static BeanDeserializerModifier 
        createErrorHandlingBeanDeserializerModifier(
            Consumer<? super JsonError> jsonErrorConsumer)
    {
        return new BeanDeserializerModifier()
        {
            @Override
            public BeanDeserializerBuilder updateBuilder(
                DeserializationConfig config,
                BeanDescription beanDesc,
                BeanDeserializerBuilder builder)
            {
                Iterator<SettableBeanProperty> propertiesIterator =
                    builder.getProperties();
                while (propertiesIterator.hasNext())
                {
                    SettableBeanProperty property = propertiesIterator.next();
                    SettableBeanProperty wrappedProperty =
                        new ErrorReportingSettableBeanProperty(
                            property, jsonErrorConsumer);
                    builder.addOrReplaceProperty(wrappedProperty, true);
                }
                return builder;
            }
        };    
    }
    
    /**
     * Returns a consumer for {@link JsonError}s that prints logging
     * messages for the errors.
     *  
     * @return The consumer
     */
    public static Consumer<JsonError> loggingJsonErrorConsumer()
    {
        return LOG_JSON_ERROR_CONSUMER;
    }
    
    /**
     * Create a default Jackson object mapper for this class
     * 
     * @return The object mapper
     */
    public static ObjectMapper createObjectMapper()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        configure(objectMapper, loggingJsonErrorConsumer());
        return objectMapper;
    }

    /**
     * Create a default Jackson object mapper for this class
     * 
     * @return The object mapper
     * @param jsonErrorConsumer The consumer for {@link JsonError}s. If this 
     * is <code>null</code>, then the errors will not be handled.
     */
    public static ObjectMapper createObjectMapper(
        Consumer<? super JsonError> jsonErrorConsumer)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        configure(objectMapper, jsonErrorConsumer);
        return objectMapper;
    }
    
    /**
     * Perform a default configuration of the given object mapper for
     * parsing glTF data
     * 
     * @param objectMapper The object mapper
     * @param jsonErrorConsumer The consumer for {@link JsonError}s. If this 
     * is <code>null</code>, then the errors will not be handled.
     */
    public static void configure(
        ObjectMapper objectMapper, 
        Consumer<? super JsonError> jsonErrorConsumer)
    {
        // Some glTF files have single values instead of arrays,
        // so accept this for compatibility reasons
        objectMapper.configure(
            DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        objectMapper.configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        objectMapper.addHandler(
            createDeserializationProblemHandler(jsonErrorConsumer));

        objectMapper.setSerializationInclusion(Include.NON_NULL);
        
        objectMapper.setPropertyNamingStrategy(
            new KeywordPropertyNamingStrategy());
        
        // Register the module that will initialize the setup context
        // with the error handling bean deserializer modifier
        objectMapper.registerModule(new SimpleModule()
        {
            /**
             * Serial UID
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void setupModule(SetupContext context)
            {
                super.setupModule(context);
                context.addBeanDeserializerModifier(
                    createErrorHandlingBeanDeserializerModifier(
                        jsonErrorConsumer));
            }
        });

    }
    
    /**
     * Read a Jackson JSON node from the given JSON data
     * 
     * @param jsonData The JSON data
     * @return The Jackson JSON node
     * @throws IOException If an IO error occurs
     */
    public static JsonNode readJson(ByteBuffer jsonData) throws IOException
    {
        ObjectMapper objectMapper = createObjectMapper();
        try (InputStream jsonInputStream =
            Buffers.createByteBufferInputStream(jsonData))
        {
            return objectMapper.readTree(jsonInputStream);
        }
    }
    
    /**
     * Private constructor to prevent instantiation
     */
    private JacksonUtils()
    {
        // Private constructor to prevent instantiation
    }

}
