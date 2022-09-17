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

import java.lang.reflect.Field;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;

/**
 * An implementation of a Jackson PropertyNamingStrategy that handles 
 * properties that collide with reserved words. 
 * 
 * It assumes that the classes that are generated for a schema avoid
 * using property names that collide with reserved words, by appending
 * the suffix <code>Property</code> to the actual property name. 
 * 
 * For example, it assumes that a JSON object like
 * <pre><code>
 * {
 *     "class": "Example",
 *     "int": 123
 * }
 * </code></pre>
 * is represented with a class like
 * <pre><code>
 * class Example
 * {
 *     String classProperty;
 *     int intProperty;
 * }
 * </code></pre>
 * 
 * When this strategy is assigned to an ObjectMapper, it will resolve
 * the actual underlying names.
 */
class KeywordPropertyNamingStrategy extends PropertyNamingStrategy
{
    // TODO The exact renaming process could or should be configurable.
    // There could be two functions, s -> s.with(suffix)
    // and r -> r.without(suffix) to describe the conversion
    
    /**
     * Serial UID
     */
    private static final long serialVersionUID = 1L;

    @Override
    public String nameForField(MapperConfig<?> config,
        AnnotatedField field, String defaultName)
    {
        return field.getName();
    }

    @Override
    public String nameForGetterMethod(MapperConfig<?> config,
        AnnotatedMethod method, String defaultName)
    {
        return handleKeywordNames(method.getDeclaringClass(), defaultName);
    }

    @Override
    public String nameForSetterMethod(MapperConfig<?> config,
        AnnotatedMethod method, String defaultName)
    {
        return handleKeywordNames(method.getDeclaringClass(), defaultName);
    }

    /**
     * Returns the name of the JSON property that is described with the 
     * given Java field name.
     * 
     * If the name ends with <code>"Property"</code>, for example, the name
     * <code>"intProperty"</code>, then this method will see whether
     * there is a field called <code>"intProperty"</code>, and if there
     * is, return the name of the property that is described with this 
     * field - in this case, the actual, underling JSON property name 
     * would be <code>"int"</code>.
     * 
     * @param c The class
     * @param defaultName The default name
     * @return The JSON property name
     */
    private String handleKeywordNames(Class<?> c, String defaultName)
    {
        if (!defaultName.endsWith("Property"))
        {
            return defaultName;
        }
        String baseName =
            defaultName.substring(0, 
                defaultName.length() - "Property".length());
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields)
        {
            if (field.getName().equalsIgnoreCase(defaultName))
            {
                return baseName;
            }
        }
        return defaultName;
    }
}