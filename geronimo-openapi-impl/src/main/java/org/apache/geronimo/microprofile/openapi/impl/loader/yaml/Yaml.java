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
package org.apache.geronimo.microprofile.openapi.impl.loader.yaml;

import static java.util.Optional.ofNullable;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.stream.Stream;

import javax.enterprise.inject.Vetoed;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;

import org.apache.geronimo.microprofile.openapi.impl.loader.ApiBindings;
import org.apache.geronimo.microprofile.openapi.impl.model.APIResponseImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.APIResponsesImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.CallbackImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ComponentsImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ContactImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ContentImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.DiscriminatorImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.EncodingImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ExampleImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ExtensibleImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ExternalDocumentationImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.HeaderImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.InfoImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.LicenseImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.LinkImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.MediaTypeImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.OAuthFlowImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.OAuthFlowsImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.OpenAPIImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.OperationImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ParameterImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.PathItemImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.PathsImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ReferenceImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.RequestBodyImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.SchemaImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ScopesImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.SecurityRequirementImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.SecuritySchemeImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ServerImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ServerVariableImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ServerVariablesImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.TagImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.XMLImpl;
import org.eclipse.microprofile.openapi.models.Components;
import org.eclipse.microprofile.openapi.models.Extensible;
import org.eclipse.microprofile.openapi.models.ExternalDocumentation;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.PathItem;
import org.eclipse.microprofile.openapi.models.Paths;
import org.eclipse.microprofile.openapi.models.Reference;
import org.eclipse.microprofile.openapi.models.callbacks.Callback;
import org.eclipse.microprofile.openapi.models.examples.Example;
import org.eclipse.microprofile.openapi.models.headers.Header;
import org.eclipse.microprofile.openapi.models.info.Contact;
import org.eclipse.microprofile.openapi.models.info.Info;
import org.eclipse.microprofile.openapi.models.info.License;
import org.eclipse.microprofile.openapi.models.links.Link;
import org.eclipse.microprofile.openapi.models.media.Content;
import org.eclipse.microprofile.openapi.models.media.Discriminator;
import org.eclipse.microprofile.openapi.models.media.Encoding;
import org.eclipse.microprofile.openapi.models.media.MediaType;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.media.XML;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.eclipse.microprofile.openapi.models.parameters.RequestBody;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;
import org.eclipse.microprofile.openapi.models.responses.APIResponses;
import org.eclipse.microprofile.openapi.models.security.OAuthFlow;
import org.eclipse.microprofile.openapi.models.security.OAuthFlows;
import org.eclipse.microprofile.openapi.models.security.Scopes;
import org.eclipse.microprofile.openapi.models.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.models.security.SecurityScheme;
import org.eclipse.microprofile.openapi.models.servers.Server;
import org.eclipse.microprofile.openapi.models.servers.ServerVariable;
import org.eclipse.microprofile.openapi.models.servers.ServerVariables;
import org.eclipse.microprofile.openapi.models.tags.Tag;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.NumberSerializer;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

@Vetoed
public final class Yaml {
    private Yaml() {
        // no-op
    }

    public static OpenAPI loadAPI(final InputStream stream) {
        try {
            final ObjectMapper mapper = getObjectMapper();
            return mapper.readValue(stream, OpenAPI.class);
        } catch (final IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    // let be reusable in integrations
    public static ObjectMapper getObjectMapper() {
        final SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();
        ApiBindings.get().forEach((k, v) -> resolver.addMapping(Class.class.cast(k), v));

        final SimpleModule module = new SimpleModule();
        module.setAbstractTypes(resolver);
        module.addDeserializer(Parameter.In.class, new JsonDeserializer<Parameter.In>() {
            @Override
            public Parameter.In deserialize(final JsonParser p, final DeserializationContext ctxt) {
                return Stream.of(Parameter.In.values()).filter(it -> {
                    try {
                        return it.name().equalsIgnoreCase(p.getValueAsString());
                    } catch (IOException e) {
                        throw new IllegalArgumentException(e);
                    }
                }).findFirst().orElseThrow(() -> new IllegalArgumentException("No matching In value"));
            }
        });
        module.addDeserializer(Schema.SchemaType.class, new JsonDeserializer<Schema.SchemaType>() {
            @Override
            public Schema.SchemaType deserialize(final JsonParser p, final DeserializationContext ctxt) {
                return Stream.of(Schema.SchemaType.values()).filter(it -> {
                    try {
                        return it.name().equalsIgnoreCase(p.getValueAsString());
                    } catch (IOException e) {
                        throw new IllegalArgumentException(e);
                    }
                }).findFirst().orElseThrow(() -> new IllegalArgumentException("No matching SchemaType value"));
            }
        });
        module.addDeserializer(SecurityScheme.Type.class, new JsonDeserializer<SecurityScheme.Type>() {
            @Override
            public SecurityScheme.Type deserialize(final JsonParser p, final DeserializationContext ctxt) {
                return Stream.of(SecurityScheme.Type.values()).filter(it -> {
                    try {
                        return it.name().equalsIgnoreCase(p.getValueAsString());
                    } catch (IOException e) {
                        throw new IllegalArgumentException(e);
                    }
                }).findFirst().orElseThrow(() -> new IllegalArgumentException("No matching SecurityScheme.Type value"));
            }
        });
        module.addSerializer(BigDecimal.class, new NumberSerializer(BigDecimal.class) {
            @Override
            public void serialize(final Number value, final JsonGenerator g,
                                  final SerializerProvider provider) throws IOException {
                if (BigDecimal.class.isInstance(value) && value.doubleValue() == value.longValue()) {
                    super.serialize(value.longValue(), g, provider);
                } else {
                    super.serialize(value, g, provider);
                }
            }
        });
        Stream.of(SecurityScheme.Type.class, SecurityScheme.In.class,
                Schema.SchemaType.class, Header.Style.class, Encoding.Style.class,
                Parameter.Style.class, Parameter.In.class)
              .forEach(it -> toStringSerializer(module, it));

        final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.registerModule(module);
        mapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector() {
            @Override
            protected boolean _isIgnorable(final Annotated a) {
                return super._isIgnorable(a) || a.getAnnotation(JsonbTransient.class) != null;
            }

            @Override
            public PropertyName findNameForSerialization(final Annotated a) {
                return ofNullable(a.getAnnotation(JsonbProperty.class))
                        .map(JsonbProperty::value)
                        .map(PropertyName::new)
                        .orElseGet(() -> super.findNameForSerialization(a));
            }

            @Override
            public PropertyName findNameForDeserialization(final Annotated a) {
                return ofNullable(a.getAnnotation(JsonbProperty.class))
                        .map(JsonbProperty::value)
                        .map(PropertyName::new)
                        .orElseGet(() -> super.findNameForDeserialization(a));
            }
        });
        mapper.setPropertyNamingStrategy(new PropertyNamingStrategy.PropertyNamingStrategyBase() {
            @Override
            public String translate(final String propertyName) {
                return "ref".equals(propertyName) ? "$ref" : propertyName;
            }
        });
        return mapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
    }

    private static <T> void toStringSerializer(final SimpleModule module, final Class<T> it) {
        module.addSerializer(it, new JsonSerializer<T>() {
            @Override
            public void serialize(final T value,
                                  final JsonGenerator gen,
                                  final SerializerProvider serializers) throws IOException {
                gen.writeString(value.toString());
            }
        });
    }
}
