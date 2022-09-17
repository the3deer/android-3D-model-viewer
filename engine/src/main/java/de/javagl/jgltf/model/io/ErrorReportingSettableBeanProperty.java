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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;

/**
 * A SettableBeanProperty that passes all calls to a delegate, and
 * passes error information to a consumer of {@link JsonError}s 
 * (for example, when calling a setter caused an exception).
 * This is used for error reporting in the Jackson bean deserializers.
 */
class ErrorReportingSettableBeanProperty extends SettableBeanProperty
{
    /**
     * Serial UID 
     */
    private static final long serialVersionUID = 7398743799397469737L;

    /**
     * The delegate
     */
    private final SettableBeanProperty delegate;
    
    /**
     * The consumer for {@link JsonError}s
     */
    private final Consumer<? super JsonError> jsonErrorConsumer;
    
    /**
     * Creates a new instance with the given delegate and error consumer
     *  
     * @param delegate The delegate
     * @param jsonErrorConsumer The consumer for {@link JsonError}s. If
     * this is <code>null</code>, then errors will be ignored.
     */
    ErrorReportingSettableBeanProperty(
        SettableBeanProperty delegate, 
        Consumer<? super JsonError> jsonErrorConsumer)
    {
        super(delegate);
        this.delegate = delegate;
        this.jsonErrorConsumer = jsonErrorConsumer;
    }

    @Override
    public SettableBeanProperty
        withValueDeserializer(JsonDeserializer<?> deser)
    {
        return new ErrorReportingSettableBeanProperty(
            delegate.withValueDeserializer(deser), jsonErrorConsumer);
    }
    
    @Override
    public SettableBeanProperty withName(PropertyName newName)
    {
        return new ErrorReportingSettableBeanProperty(
            delegate.withName(newName), jsonErrorConsumer);
    }
    
    @Override
    public Object setAndReturn(Object instance, Object value)
        throws IOException
    {
        return delegate.setAndReturn(instance, value);
    }
    
    @Override
    public void set(Object instance, Object value) throws IOException
    {
        delegate.set(instance, value);
    }
    
    @Override
    public AnnotatedMember getMember()
    {
        return delegate.getMember();
    }
    
    @Override
    public <A extends Annotation> A getAnnotation(Class<A> acls)
    {
        return delegate.getAnnotation(acls);
    }
    
    @Override
    public Object deserializeSetAndReturn(JsonParser p,
        DeserializationContext ctxt, Object instance) throws IOException
    {
        return delegate.deserializeSetAndReturn(p, ctxt, instance);
    }
    
    @Override
    public void deserializeAndSet(JsonParser p, DeserializationContext ctxt,
        Object instance) throws IOException
    {
        try
        {
            delegate.deserializeAndSet(p, ctxt, instance);
        }
        catch (Exception e)
        {
            if (jsonErrorConsumer != null)
            {
                jsonErrorConsumer.accept(new JsonError(
                    e.getMessage(), p.getParsingContext(), e));
            }
        }
    }
    
    @Override
    public SettableBeanProperty withNullProvider(NullValueProvider nva)
    {
        return new ErrorReportingSettableBeanProperty(
            delegate.withNullProvider(nva), jsonErrorConsumer);
    }
}
