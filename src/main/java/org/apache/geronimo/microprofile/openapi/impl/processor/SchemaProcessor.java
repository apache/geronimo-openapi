/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.geronimo.microprofile.openapi.impl.processor;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.geronimo.microprofile.openapi.impl.model.DiscriminatorImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.SchemaImpl;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.DiscriminatorMapping;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.models.Components;

public class SchemaProcessor {
    private final Map<Type, org.eclipse.microprofile.openapi.models.media.Schema> cache = new HashMap<>();

    public org.eclipse.microprofile.openapi.models.media.Schema mapSchemaFromClass(
            final org.eclipse.microprofile.openapi.models.Components components, final Type model) {
        final boolean cached = cache.containsKey(model);
        final org.eclipse.microprofile.openapi.models.media.Schema schema = cached ?
                cache.get(model) :
                of(new SchemaImpl()).map(s -> {
                    fillSchema(components, model, s);
                    return s;
                }).get();
        if (!cached && Class.class.isInstance(model)) {
            cache.put(Class.class.cast(model), schema);
        }
        return schema;
    }

    public void fillSchema(final org.eclipse.microprofile.openapi.models.Components components, final Type model,
            final org.eclipse.microprofile.openapi.models.media.Schema schema) {
        schema.items(new SchemaImpl());
        if (Class.class.isInstance(model)) {
            if (boolean.class == model) {
                schema.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.BOOLEAN);
            } else if (String.class == model) {
                schema.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.STRING);
            } else if (double.class == model || float.class == model) {
                schema.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.NUMBER);
            } else if (int.class == model || short.class == model || byte.class == model) {
                schema.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.INTEGER);
            } else {
                // todo cache schema in components and just set the ref here - jsonb mapping based
                final Class from = Class.class.cast(model);
                ofNullable(from.getAnnotation(Schema.class)).ifPresent(s -> sets(components, Schema.class.cast(s), schema));
                schema.properties(new HashMap<>());
                Class<?> current = from;
                while (current != null && current != Object.class) { // todo: simplify with a Pair model?
                    schema.getProperties().putAll(Stream.of(current.getDeclaredFields())
                            .filter(f -> f.isAnnotationPresent(Schema.class) && !f.getAnnotation(Schema.class).hidden())
                            .peek(f -> {
                                if (f.getAnnotation(Schema.class).required()) {
                                    if (schema.getRequired() == null) {
                                        schema.required(new ArrayList<>());
                                    }
                                    final String name = findFieldName(f);
                                    if (!schema.getRequired().contains(name)) {
                                        schema.getRequired().add(name);
                                    }
                                }
                            })
                            .collect(toMap(this::findFieldName,
                                    f -> ofNullable(mapSchema(components, f.getAnnotation(Schema.class))).orElseGet(() -> {
                                        final org.eclipse.microprofile.openapi.models.media.Schema schemaFromClass = mapSchemaFromClass(
                                                components, f.getGenericType());
                                        mergeSchema(components, schemaFromClass, f.getAnnotation(Schema.class));
                                        return schemaFromClass;
                                    }))));
                    current = current.getSuperclass();
                }
            }
        } else {
            schema.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.ARRAY);
            if (ParameterizedType.class.isInstance(model)) {
                final ParameterizedType pt = ParameterizedType.class.cast(model);
                if (pt.getActualTypeArguments().length == 1 && Class.class.isInstance(pt.getActualTypeArguments()[0])) {
                    schema.items(new SchemaImpl());
                    fillSchema(components, Class.class.cast(pt.getActualTypeArguments()[0]), schema.getItems());
                }
            }
        }
    }

    private String findFieldName(final Field f) {
        return of(f.getAnnotation(Schema.class).name()).filter(it -> !it.isEmpty()).orElseGet(f::getName);
    }

    private void mergeSchema(final Components components, final org.eclipse.microprofile.openapi.models.media.Schema impl,
            final Schema schema) {
        impl.deprecated(schema.deprecated());
        if (schema.type() != SchemaType.DEFAULT) {
            impl.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.valueOf(schema.type().name()));
        }
        if (!schema.title().isEmpty()) {
            impl.title(schema.title());
        }
        if (!schema.description().isEmpty()) {
            impl.description(schema.description());
        }
        if (!schema.format().isEmpty()) {
            impl.format(schema.format());
        }
        if (!schema.ref().isEmpty()) {
            impl.ref(schema.ref());
        }
        if (!schema.example().isEmpty()) {
            impl.example(schema.example());
        }
        of(schema.not()).filter(it -> it != Void.class).ifPresent(t -> impl.not(mapSchemaFromClass(components, t)));
        final List<org.eclipse.microprofile.openapi.models.media.Schema> oneOf = Stream.of(schema.oneOf())
                .map(it -> mapSchemaFromClass(components, it)).collect(toList());
        if (!oneOf.isEmpty()) {
            impl.oneOf(oneOf);
        }
        final List<org.eclipse.microprofile.openapi.models.media.Schema> anyOf = Stream.of(schema.anyOf())
                .map(it -> mapSchemaFromClass(components, it)).collect(toList());
        if (!anyOf.isEmpty()) {
            impl.anyOf(anyOf);
        }
        final List<org.eclipse.microprofile.openapi.models.media.Schema> allOf = Stream.of(schema.allOf())
                .map(it -> mapSchemaFromClass(components, it)).collect(toList());
        if (!allOf.isEmpty()) {
            impl.allOf(allOf);
        }
        if (schema.multipleOf() != 0) {
            impl.multipleOf(BigDecimal.valueOf(schema.multipleOf()));
        }
        impl.minimum(toBigDecimal(schema.minimum()));
        impl.maximum(toBigDecimal(schema.maximum()));
        impl.exclusiveMinimum(schema.exclusiveMinimum());
        impl.exclusiveMaximum(schema.exclusiveMaximum());
        if (schema.minLength() >= 0) {
            impl.minLength(schema.minLength());
        }
        if (schema.maxLength() >= 0) {
            impl.maxLength(schema.maxLength());
        }
        if (!schema.pattern().isEmpty()) {
            impl.pattern(schema.pattern());
        }
        impl.nullable(schema.nullable());
        if (schema.minProperties() > 0) {
            impl.minProperties(schema.minProperties());
        }
        if (schema.minProperties() > 0) {
            impl.maxProperties(schema.maxProperties());
        }
        if (schema.minItems() > Integer.MIN_VALUE) {
            impl.minItems(schema.minItems());
        }
        if (schema.minItems() < Integer.MAX_VALUE) {
            impl.maxItems(schema.maxItems());
        }
        impl.uniqueItems(schema.uniqueItems());
        impl.readOnly(schema.readOnly());
        impl.writeOnly(schema.writeOnly());
        impl.defaultValue(toType(schema.defaultValue(), impl.getType()));
        final List<String> required = Stream.of(schema.requiredProperties()).collect(toList());
        if (!required.isEmpty()) {
            impl.required(required);
        }
        if (schema.discriminatorMapping().length > 0) {
            impl.discriminator(new DiscriminatorImpl().mapping(Stream.of(schema.discriminatorMapping())
                    .collect(toMap(DiscriminatorMapping::value, it -> it.schema().getName()))));
        }
        final List<Object> enums = Stream.of(schema.enumeration()).collect(toList());
        if (!enums.isEmpty()) {
            impl.setEnumeration(enums);
        }
    }

    public org.eclipse.microprofile.openapi.models.media.Schema mapSchema(
            final org.eclipse.microprofile.openapi.models.Components components, final Schema schema) {
        if (schema.hidden() || (schema.implementation() == Void.class && schema.name().isEmpty() && schema.ref().isEmpty())) {
            if (schema.type() != SchemaType.DEFAULT) {
                return new SchemaImpl()
                        .type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.valueOf(schema.type().name()));
            }
            return null;
        }

        final SchemaImpl impl = new SchemaImpl();
        sets(components, schema, impl);
        return impl;
    }

    private void sets(final Components components, final Schema schema,
            final org.eclipse.microprofile.openapi.models.media.Schema impl) {
        if (!schema.ref().isEmpty()) {
            impl.ref(resolveSchemaRef(components, schema.ref()));
        } else {
            if (schema.implementation() != Void.class) {
                fillSchema(components, schema.implementation(), impl);
            }
            mergeSchema(components, impl, schema);
        }
    }

    private String resolveSchemaRef(final org.eclipse.microprofile.openapi.models.Components components, final String ref) {
        if (ref.startsWith("#")) {
            return ref;
        }
        return "#/components/schemas/" + ref;
    }

    private BigDecimal toBigDecimal(final String minimum) {
        return minimum.isEmpty() ? null : new BigDecimal(minimum);
    }

    private Object toType(final String s, final org.eclipse.microprofile.openapi.models.media.Schema.SchemaType type) {
        if (type == null || s.isEmpty()) {
            return null;
        }
        switch (type) {
        case STRING:
            return s;
        case INTEGER:
            return Integer.valueOf(s);
        case NUMBER:
            return Double.valueOf(s);
        case BOOLEAN:
            return Boolean.parseBoolean(s);
        case OBJECT:
        case ARRAY:
            // todo using jsonb
        default:
            return null;
        }
    }
}
