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
package org.apache.geronimo.microprofile.openapi.impl.model.codec;

import static java.util.Locale.ROOT;

import java.math.BigDecimal;
import java.util.stream.Stream;

import javax.enterprise.inject.Vetoed;
import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.serializer.JsonbSerializer;
import javax.json.bind.serializer.SerializationContext;
import javax.json.stream.JsonGenerator;

import org.eclipse.microprofile.openapi.models.Extensible;
import org.eclipse.microprofile.openapi.models.headers.Header;
import org.eclipse.microprofile.openapi.models.media.Encoding;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.eclipse.microprofile.openapi.models.security.SecurityScheme;

@Vetoed
public final class Serializers {

    private Serializers() {
        // no-op
    }

    @Vetoed // truncate longs/integers at serialization time
    public static class BigDecimalSerializer implements JsonbAdapter<BigDecimal, Number> {
        @Override
        public Number adaptToJson(final BigDecimal obj) {
            if (obj.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0) {
                return obj.longValueExact();
            }
            return obj;
        }

        @Override
        public BigDecimal adaptFromJson(final Number obj) {
            return BigDecimal.class.isInstance(obj) ? BigDecimal.class.cast(obj) : BigDecimal.valueOf(obj.doubleValue());
        }
    }

    @Vetoed
    private static abstract class EnumSerializer<E extends Enum<E>> implements JsonbAdapter<E, String> {

        private final Class<E> type;

        protected EnumSerializer(final Class<E> type) {
            this.type = type;
        }

        @Override
        public String adaptToJson(final E obj) {
            return obj.toString();
        }

        @Override
        public E adaptFromJson(final String obj) {
            try {
                return Enum.valueOf(type, obj.toUpperCase(ROOT));
            } catch (final IllegalArgumentException iae) {
                return Stream.of(type.getEnumConstants())
                             .filter(it -> it.toString().equals(obj)).findFirst()
                             .orElseThrow(() -> iae);
            }
        }
    }

    @Vetoed
    public static class EncodingStyleSerializer extends EnumSerializer<Encoding.Style> implements JsonbAdapter<Encoding.Style, String> {

        public EncodingStyleSerializer() {
            super(Encoding.Style.class);
        }
    }

    @Vetoed
    public static class HeaderStyleSerializer extends EnumSerializer<Header.Style> implements JsonbAdapter<Header.Style, String> {

        public HeaderStyleSerializer() {
            super(Header.Style.class);
        }
    }

    @Vetoed
    public static class ParameterStyleSerializer extends EnumSerializer<Parameter.Style> implements JsonbAdapter<Parameter.Style, String> {

        public ParameterStyleSerializer() {
            super(Parameter.Style.class);
        }
    }

    @Vetoed
    public static class SecuritySchemeTypeSerializer extends EnumSerializer<SecurityScheme.Type> implements JsonbAdapter<SecurityScheme.Type, String> {

        public SecuritySchemeTypeSerializer() {
            super(SecurityScheme.Type.class);
        }
    }

    @Vetoed
    public static class SecuritySchemeInSerializer extends EnumSerializer<SecurityScheme.In> implements JsonbAdapter<SecurityScheme.In, String> {

        public SecuritySchemeInSerializer() {
            super(SecurityScheme.In.class);
        }
    }

    @Vetoed
    public static class InSerializer extends EnumSerializer<Parameter.In> implements JsonbAdapter<Parameter.In, String> {

        public InSerializer() {
            super(Parameter.In.class);
        }
    }

    @Vetoed
    public static class SchemaTypeSerializer extends EnumSerializer<Schema.SchemaType>
            implements JsonbAdapter<Schema.SchemaType, String> {

        public SchemaTypeSerializer() {
            super(Schema.SchemaType.class);
        }
    }

    @Vetoed
    public static class ExtensionSerializer<T extends Extensible<T>> implements JsonbSerializer<T> {
        @Override
        public void serialize(final T t, final JsonGenerator jsonGenerator, final SerializationContext serializationContext) {
            serializationContext.serialize(t, jsonGenerator);
            if (t.getExtensions() != null) {
                t.getExtensions().forEach((k, v) -> serializationContext.serialize(k, v, jsonGenerator));
            }
        }
    }
}
