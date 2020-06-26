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

import static java.beans.Introspector.decapitalize;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.io.StringReader;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.bind.annotation.JsonbProperty;
import javax.ws.rs.core.Response;

import org.apache.geronimo.microprofile.openapi.impl.model.DiscriminatorImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.SchemaImpl;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.DiscriminatorMapping;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.models.Components;

public class SchemaProcessor {
    private final Map<Type, org.eclipse.microprofile.openapi.models.media.Schema> cache = new HashMap<>();
    private final Map<Class<?>, String> providedRefs = new HashMap<>();
    private final Class<?> persistenceCapable;
    private final JsonReaderFactory jsonReaderFactory;

    public SchemaProcessor() {
        Class<?> pc = null;
        try {
            pc = Thread.currentThread().getContextClassLoader()
                    .loadClass("org.apache.openjpa.enhance.PersistenceCapable");
        } catch (final NoClassDefFoundError | ClassNotFoundException e) {
            // no-op
        }
        jsonReaderFactory = Json.createReaderFactory(emptyMap());
        persistenceCapable = pc;
    }

    public org.eclipse.microprofile.openapi.models.media.Schema mapSchemaFromClass(
            final Supplier<Components> components, final Type model) {
        final org.eclipse.microprofile.openapi.models.media.Schema cached = cache.get(model);
        if (cached != null) {
            return new SchemaImpl().type(cached.getType()).ref(toRef(Class.class.cast(model), null));
        }
        final SchemaImpl schema = new SchemaImpl();
        fillSchema(components, model, schema, null);
        return schema;
    }

    private String toRef(final Class<?> model, final String providedRef) {
        return "#/components/schemas/" + toRefName(model, providedRef);
    }

    // todo: introduce naming strategy? simplename can conflict so this is safer but ugly
    private String toRefName(final Class<?> model, final String providedRef) {
        return providedRefs.computeIfAbsent(
                model, k -> ofNullable(providedRef)
                        .orElseGet(() -> k.getName().replace('.', '_').replace('$', '_')));
    }

    public void fillSchema(
            final Supplier<org.eclipse.microprofile.openapi.models.Components>components,
            final Type rawModel,
            final org.eclipse.microprofile.openapi.models.media.Schema schema,
            final String providedRef) {
        final Type model = unwrapType(rawModel);
        if (Class.class.isInstance(model)) {
            if (boolean.class == model) {
                schema.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.BOOLEAN);
            } else if (Boolean.class == model) {
                schema.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.BOOLEAN).nullable(true);
            } else if (String.class == model || JsonString.class == model) {
                schema.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.STRING);
            } else if (double.class == model || float.class == model) {
                schema.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.NUMBER);
            } else if (Double.class == model || Float.class == model || JsonNumber.class == model) {
                schema.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.NUMBER).nullable(true);
            } else if (int.class == model || short.class == model || byte.class == model || long.class == model) {
                schema.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.INTEGER);
            } else if (Integer.class == model || Short.class == model || Byte.class == model || Long.class == model) {
                schema.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.INTEGER).nullable(true);
            } else if (Response.class == model || JsonObject.class == model || JsonValue.class == model) {
                schema.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.OBJECT)
                        .nullable(true)
                        .properties(new HashMap<>());
            } else if (JsonArray.class == model) {
                schema.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.ARRAY)
                        .nullable(true)
                        .items(new SchemaImpl()
                                .type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.OBJECT)
                                .properties(new HashMap<>()));
            } else if (isStringable(model)) {
                schema.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.STRING).nullable(true);
            } else {
                final Class<?> from = Class.class.cast(model);
                if (from.isEnum()) {
                    schema.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.STRING)
                          .enumeration(asList(from.getEnumConstants()))
                          .nullable(true);
                } else if (from.isArray()) {
                    schema.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.ARRAY);
                    final SchemaImpl items = new SchemaImpl();
                    fillSchema(components, from.getComponentType(), items, null);
                    schema.items(items);
                } else if (Collection.class.isAssignableFrom(from)) {
                    schema.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.ARRAY);
                    final SchemaImpl items = new SchemaImpl();
                    fillSchema(components, Object.class, items, null);
                    schema.items(items);
                } else {
                    //if providedRef is null and the Schema name is not Empty, we set it as providedRef
                    final String ref = ofNullable(from.getAnnotation(Schema.class))
                            .filter(a -> !a.name().isEmpty())
                            .map(s -> {
                                final String sRef = s.name();
                                sets(components, s, schema, sRef);
                                return sRef;
                            })
                            .orElse(providedRef);

                    schema.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.OBJECT);

                    final org.eclipse.microprofile.openapi.models.media.Schema objectSchema = getOrCreateReusableObjectComponent(components, from, ref);
                    if (schema != objectSchema) {
                        schema.ref(toRef(from, ref));
                    }
                }
            }
        } else {
            schema.items(new SchemaImpl());
            if (ParameterizedType.class.isInstance(model)) {
                final ParameterizedType pt = ParameterizedType.class.cast(model);
                if (Class.class.isInstance(pt.getRawType()) && Map.class.isAssignableFrom(Class.class.cast(pt.getRawType()))) {
                    schema.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.OBJECT);
                } else if (pt.getActualTypeArguments().length == 1 && Class.class.isInstance(pt.getActualTypeArguments()[0])) {
                    schema.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.ARRAY);
                    final SchemaImpl items = new SchemaImpl();
                    fillSchema(components, Class.class.cast(pt.getActualTypeArguments()[0]), items, null);
                    schema.items(items);
                } else {
                    schema.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.ARRAY);
                }
            } else { // todo?
                schema.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.ARRAY);
            }
        }
    }

    /**
     * Creates or get from the cache the reusable object component
     *
     * @param components  components, where the schema will be added, if new created
     * @param from        the class for which we want create the schema
     * @param providedRef providedRef
     * @return the schema (from the cache or new created) for the given class
     */
    private org.eclipse.microprofile.openapi.models.media.Schema getOrCreateReusableObjectComponent(
        final Supplier<org.eclipse.microprofile.openapi.models.Components> components,
        final Class from,
        final String providedRef) {

        final org.eclipse.microprofile.openapi.models.media.Schema existingSchema = cache.get(from);
        if (existingSchema != null) {
            return existingSchema;
        }

        final SchemaImpl schema = new SchemaImpl();
        ofNullable(from.getAnnotation(Schema.class)).ifPresent(
            s -> sets(components, Schema.class.cast(s), schema, null));

        schema.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.OBJECT);

        if (cache.putIfAbsent(from, schema) == null) {
            components.get().addSchema(toRefName(from, providedRef), schema);
        }

        final Predicate<String> ignored = createIgnorePredicate(from);

        schema.properties(new HashMap<>());
        Class<?> current = from;
        while (current != null && current != Object.class) {
            Stream.of(current.getDeclaredFields())
                .filter(it -> isVisible(it, it.getModifiers()))
                .peek(f -> handleRequired(schema, f, () -> findFieldName(f)))
                .forEach(f -> {
                    final String fieldName = findFieldName(f);
                    if (!ignored.test(fieldName)) {
                        schema.getProperties().put(fieldName, mapField(components, f, f.getGenericType()));
                    }
                });
            Stream.of(current.getDeclaredMethods())
                .filter(it -> isVisible(it, it.getModifiers()))
                .filter(it -> (it.getName().startsWith("get") || it.getName().startsWith("is")) && it.getName().length() > 2)
                .peek(m -> handleRequired(schema, m, () -> findMethodName(m)))
                .forEach(m -> {
                    final String key = findMethodName(m);
                    if (!ignored.test(key) && !schema.getProperties().containsKey(key)) {
                        schema.getProperties().put(key, mapField(components, m, m.getGenericReturnType()));
                    }
                });
            current = current.getSuperclass();
        }
        return schema;
    }

    private Predicate<String> createIgnorePredicate(final Class from) {
        return persistenceCapable != null && persistenceCapable.isAssignableFrom(from) ?
                v -> v.startsWith("pc") : v -> false;
    }

    private boolean isVisible(final AnnotatedElement elt, final int modifiers) {
        final boolean explicit = elt.isAnnotationPresent(Schema.class);
        if (!explicit || !elt.getAnnotation(Schema.class).hidden()) {
            return (!Modifier.isPrivate(modifiers) || elt.isAnnotationPresent(Schema.class)) && !Modifier.isStatic(modifiers);
        }
        return false;
    }

    private Type unwrapType(final Type rawModel) {
        if (ParameterizedType.class.isInstance(rawModel) &&
                Stream.of(ParameterizedType.class.cast(rawModel).getActualTypeArguments()).allMatch(WildcardType.class::isInstance)) {
            return ParameterizedType.class.cast(rawModel).getRawType();
        }
        return rawModel;
    }

    private boolean isStringable(final Type model) {
        return Date.class == model || model.getTypeName().startsWith("java.time.") || Class.class == model || Type.class == model;
    }

    private void handleRequired(final org.eclipse.microprofile.openapi.models.media.Schema schema,
                                final AnnotatedElement element, final Supplier<String> nameSupplier) {
        if (!element.isAnnotationPresent(Schema.class) || !element.getAnnotation(Schema.class).required()) {
            return;
        }
        if (schema.getRequired() == null) {
            schema.required(new ArrayList<>());
        }
        final String name = nameSupplier.get();
        if (!schema.getRequired().contains(name)) {
            schema.getRequired().add(name);
        }
    }

    private org.eclipse.microprofile.openapi.models.media.Schema mapField(final Supplier<Components> components,
                                                                          final AnnotatedElement element,
                                                                          final Type type) {
        final Schema annotation = element.getAnnotation(Schema.class);
        return ofNullable(annotation).map(s -> mapSchema(components, s, null)).orElseGet(() -> {
            final org.eclipse.microprofile.openapi.models.media.Schema schemaFromClass = mapSchemaFromClass(
                    components, type);
            if (annotation != null) {
                mergeSchema(components, schemaFromClass, annotation, type);
            }
            return schemaFromClass;
        });
    }

    private String findFieldName(final Field f) {
        return findSchemaName(f)
                .orElseGet(() -> {
                    if (f.isAnnotationPresent(JsonbProperty.class)) {
                        return f.getAnnotation(JsonbProperty.class).value();
                    }
                    // getter
                    final String fName = f.getName();
                    final String subName = Character.toUpperCase(fName.charAt(0))
                            + (fName.length() > 1 ? fName.substring(1) : "");
                    try {
                        final Method getter = f.getDeclaringClass().getMethod("get" + subName);
                        if (getter.isAnnotationPresent(JsonbProperty.class)) {
                            return getter.getAnnotation(JsonbProperty.class).value();
                        }
                    } catch (final NoSuchMethodException e) {
                        if (boolean.class == f.getType()) {
                            try {
                                final Method isser = f.getDeclaringClass().getMethod("is" + subName);
                                if (isser.isAnnotationPresent(JsonbProperty.class)) {
                                    return isser.getAnnotation(JsonbProperty.class).value();
                                }
                            } catch (final NoSuchMethodException e2) {
                                // no-op
                            }
                        }
                    }
                    return fName;
                });
    }

    private String findMethodName(final Method m) {
        return findSchemaName(m)
                .orElseGet(() -> {
                    if (m.isAnnotationPresent(JsonbProperty.class)) {
                        return m.getAnnotation(JsonbProperty.class).value();
                    }
                    final String name = m.getName();
                    if (name.startsWith("get")) {
                        return decapitalize(name.substring("get".length()));
                    }
                    if (name.startsWith("is")) {
                        return decapitalize(name.substring("is".length()));
                    }
                    return decapitalize(name);
                });
    }

    private Optional<String> findSchemaName(final AnnotatedElement m) {
        return ofNullable(m.getAnnotation(Schema.class))
                .map(Schema::name)
                .filter(it -> !it.isEmpty());
    }

    private void mergeSchema(final Supplier<Components> components,
                             final org.eclipse.microprofile.openapi.models.media.Schema impl,
                             final Schema schema, final Type type) {
        if (schema.deprecated()) {
            impl.deprecated(schema.deprecated());
        }
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
        final String example = schema.example();
        if (!example.isEmpty()) {
            if (type != null) {
                if (type == double.class || type == Double.class ||
                        type == float.class || type == Float.class) {
                    impl.example(Double.parseDouble(example));
                } else if (type == long.class || type == Long.class ||
                        type == int.class || type == Integer.class ||
                        type == short.class || type == Short.class ||
                        type == byte.class || type == Byte.class) {
                    impl.example(Integer.parseInt(example));
                } else if (type == boolean.class || type == Boolean.class) {
                    impl.example(Boolean.parseBoolean(example));
                } else if (type == String.class) {
                    impl.example(example);
                } else if ((example.startsWith("{") && example.endsWith("}")) ||
                        (example.startsWith("[") && example.endsWith("}"))) {
                    try (final JsonReader reader = jsonReaderFactory.createReader(new StringReader(example))) {
                        impl.example(reader.readValue());
                    } catch (final Exception e) {
                        impl.example(example);
                    }
                } else {
                    impl.example(example);
                }
            } else {
                impl.example(example);
            }
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
        if (schema.exclusiveMinimum()) {
            impl.exclusiveMinimum(schema.exclusiveMinimum());
        }
        if (schema.exclusiveMaximum()) {
            impl.exclusiveMaximum(schema.exclusiveMaximum());
        }
        if (schema.minLength() >= 0 && schema.minLength() != 0) {
            impl.minLength(schema.minLength());
        }
        if (schema.maxLength() >= 0 && schema.maxLength() != Integer.MAX_VALUE) {
            impl.maxLength(schema.maxLength());
        }
        if (!schema.pattern().isEmpty()) {
            impl.pattern(schema.pattern());
        }
        if (schema.nullable()) {
            impl.nullable(schema.nullable());
        }
        if (schema.minProperties() > 0) {
            impl.minProperties(schema.minProperties());
        }
        if (schema.minProperties() > 0) {
            impl.maxProperties(schema.maxProperties());
        }
        if (schema.minItems() < Integer.MAX_VALUE) {
            impl.minItems(schema.minItems());
        }
        if (schema.maxItems() > Integer.MIN_VALUE) {
            impl.maxItems(schema.maxItems());
        }
        if (schema.uniqueItems()) {
            impl.uniqueItems(schema.uniqueItems());
        }
        if (schema.readOnly()) {
            impl.readOnly(schema.readOnly());
        }
        if (schema.writeOnly()) {
            impl.writeOnly(schema.writeOnly());
        }
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
            final Supplier<org.eclipse.microprofile.openapi.models.Components>components, final Schema schema,
            final String providedRefMapping) {
        if (schema.hidden() || (schema.implementation() == Void.class && schema.name().isEmpty() && schema.ref().isEmpty())) {
            if (schema.type() != SchemaType.DEFAULT) {
                return new SchemaImpl()
                        .type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.valueOf(schema.type().name()));
            }
            return null;
        }

        final SchemaImpl impl = new SchemaImpl();
        sets(components, schema, impl, providedRefMapping);
        return impl;
    }

    private void sets(final Supplier<Components> components, final Schema schema,
            final org.eclipse.microprofile.openapi.models.media.Schema impl,
            final String providedRef) {
        if (!schema.ref().isEmpty()) {
            impl.ref(resolveSchemaRef(schema.ref()));
        } else {
            if (schema.implementation() != Void.class) {
                final boolean array = schema.type() == SchemaType.ARRAY;
                if (array) {
                    final SchemaImpl itemSchema = new SchemaImpl();
                    fillSchema(components, schema.implementation(), itemSchema, providedRef);
                    impl.items(itemSchema);
                } else {
                    fillSchema(components, schema.implementation(), impl, providedRef);
                }
            }
            mergeSchema(components, impl, schema, null);
        }
    }

    private String resolveSchemaRef(final String ref) {
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
