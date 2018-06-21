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

import static java.util.Arrays.asList;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.enterprise.inject.Vetoed;
import javax.enterprise.inject.spi.AnnotatedType;

import org.apache.geronimo.microprofile.openapi.impl.model.APIResponseImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.APIResponsesImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.CallbackImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ComponentsImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ContactImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ContentImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.DiscriminatorImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.EncodingImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ExampleImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ExternalDocumentationImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.HeaderImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.InfoImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.LicenseImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.LinkImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.MediaTypeImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.OperationImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ParameterImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.PathItemImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.RequestBodyImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.SchemaImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.SecurityRequirementImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ServerImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ServerVariableImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ServerVariablesImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.TagImpl;
import org.eclipse.microprofile.openapi.annotations.Components;
import org.eclipse.microprofile.openapi.annotations.ExternalDocumentation;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.callbacks.Callback;
import org.eclipse.microprofile.openapi.annotations.extensions.Extension;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.links.Link;
import org.eclipse.microprofile.openapi.annotations.links.LinkParameter;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.DiscriminatorMapping;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.eclipse.microprofile.openapi.annotations.servers.ServerVariable;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.examples.Example;
import org.eclipse.microprofile.openapi.models.media.Encoding;
import org.eclipse.microprofile.openapi.models.media.MediaType;
import org.eclipse.microprofile.openapi.models.responses.APIResponses;

@Vetoed
public class AnnotationProcessor {

    // TODO completely
    public void processClass(final OpenAPI api, final AnnotatedType<?> annotatedType) {
        // TODO
    }

    private org.eclipse.microprofile.openapi.models.media.Schema mapSchemaFromClass(final Class<?> not) {
        return new SchemaImpl(); // TODO
    }

    // TODO completely
    private void processComponents(final OpenAPI api, final Components components) {
        final org.eclipse.microprofile.openapi.models.Components impl = new ComponentsImpl();
        processCallbacks(impl, components.callbacks());

        // TODO
        final ExampleObject[] examples = components.examples();
        final Header[] headers = components.headers();
        final Link[] links = components.links();
        final Parameter[] parameters = components.parameters();
        final RequestBody[] requestBodies = components.requestBodies();
        final APIResponse[] responses = components.responses();
        final Schema[] schema = components.schemas();
        final SecurityScheme[] securitySchemes = components.securitySchemes();

        api.components(impl);
    }

    private void processCallbacks(final org.eclipse.microprofile.openapi.models.Components impl, final Callback[] callbacks) {
        if (callbacks.length > 0) {
            impl.setCallbacks(Stream.of(callbacks).collect(toMap(
                    it -> of(it.name()).filter(n -> !n.isEmpty()).orElseGet(() -> mapRefToName(it.ref())), this::mapCallback)));
        }
    }

    private org.eclipse.microprofile.openapi.models.callbacks.Callback mapCallback(final Callback callback) {
        final CallbackImpl impl = new CallbackImpl();
        impl.ref(callback.ref());

        final PathItemImpl pathItem = new PathItemImpl();
        if (callback.operations().length > 0) {
            Stream.of(callback.operations()).forEach(co -> {
                final Operation operation = new OperationImpl();
                operation.summary(co.summary());
                operation.description(co.description());
                operation.externalDocs(mapExternalDocumentation(co.externalDocs()));
                if (co.extensions().length > 0) {
                    operation.setExtensions(mapExtensions(co.extensions()));
                }
                if (co.parameters().length > 0) {
                    operation.parameters(mapParameters(co.parameters()));
                }
                operation.requestBody(mapRequestBody(co.requestBody()));
                if (co.security().length > 0) {
                    operation.security(Stream.of(co.security()).map(this::mapSecurity).collect(toList()));
                }
                if (co.security().length > 0) {
                    operation.parameters(Stream.of(co.parameters()).map(this::mapParameter).collect(toList()));
                }
                if (co.responses().length > 0) {
                    final APIResponses responses = new APIResponsesImpl();
                    responses.putAll(Stream.of(co.responses())
                       .collect(toMap(it -> of(it.responseCode()).filter(c -> !c.isEmpty()).orElse("200"), this::mapResponse)));
                    operation.responses(responses);
                }
                switch (co.method()) {
                case "GET":
                    pathItem.setGET(operation);
                    break;
                case "PUT":
                    pathItem.setPUT(operation);
                    break;
                case "POST":
                    pathItem.setPOST(operation);
                    break;
                case "DELETE":
                    pathItem.setDELETE(operation);
                    break;
                case "OPTIONS":
                    pathItem.setOPTIONS(operation);
                    break;
                case "TRACE":
                    pathItem.setTRACE(operation);
                    break;
                case "HEAD":
                    pathItem.setHEAD(operation);
                    break;
                case "PATCH":
                    pathItem.setPATCH(operation);
                    break;
                default:
                }
            });
        }

        impl.put(callback.callbackUrlExpression(), pathItem);
        return impl;
    }

    private org.eclipse.microprofile.openapi.models.responses.APIResponse mapResponse(final APIResponse response) {
        final APIResponseImpl impl = new APIResponseImpl();
        impl.description(response.description());
        impl.ref(response.ref());
        if (response.headers().length > 0) {
            impl.headers(Stream.of(response.headers())
               .collect(toMap(it -> of(it.name()).filter(n -> !n.isEmpty()).orElseGet(() -> mapRefToName(it.ref())), this::mapHeader)));
        }
        if (response.content().length > 0) {
            final ContentImpl content = new ContentImpl();
            content.putAll(Stream.of(response.content()).collect(toMap(Content::mediaType, this::mapContent)));
            impl.content(content);
        }
        if (response.links().length > 0) {
            impl.links(Stream.of(response.links())
                .collect(toMap(it -> of(it.name()).filter(n -> !n.isEmpty()).orElseGet(() -> mapRefToName(it.ref())), this::mapLink)));
        }
        return impl;
    }

    private org.eclipse.microprofile.openapi.models.links.Link mapLink(final Link link) {
        final LinkImpl impl = new LinkImpl();
        impl.description(link.description());
        impl.operationId(link.operationId());
        impl.operationRef(link.operationRef());
        impl.requestBody(link.requestBody());
        impl.server(mapServer(link.server()));
        if (link.parameters().length > 0) {
            impl.parameters(Stream.of(link.parameters())
              .collect(toMap(LinkParameter::name, LinkParameter::expression)));
        }
        return impl;
    }

    private org.eclipse.microprofile.openapi.models.parameters.RequestBody mapRequestBody(final RequestBody requestBody) {

        final RequestBodyImpl impl = new RequestBodyImpl();
        impl.description(requestBody.description());
        impl.ref(requestBody.ref());
        impl.required(requestBody.required());
        if (requestBody.content().length > 0) {
            final ContentImpl content = new ContentImpl();
            content.putAll(Stream.of(requestBody.content()).collect(toMap(Content::mediaType, this::mapContent)));
            impl.content(content);
        }
        return impl;
    }

    private MediaType mapContent(final Content content) {
        final MediaTypeImpl impl = new MediaTypeImpl();
        if (content.encoding().length > 0) {
            Stream.of(content.encoding()).forEach(e -> impl.addEncoding(e.name(), mapEncoding(e)));
        }
        return impl;
    }

    private Encoding mapEncoding(final org.eclipse.microprofile.openapi.annotations.media.Encoding e) {
        final EncodingImpl impl = new EncodingImpl();
        impl.allowReserved(e.allowReserved());
        impl.explode(e.explode());
        impl.contentType(e.contentType());
        of(e.style()).filter(it -> !it.isEmpty()).ifPresent(v -> impl.style(Encoding.Style.valueOf(v)));
        if (e.headers().length > 0) {
            impl.headers(Stream.of(e.headers()).collect(toMap(
                    it -> of(it.name()).filter(n -> !n.isEmpty()).orElseGet(() -> mapRefToName(it.ref())), this::mapHeader)));
        }
        return impl;
    }

    private org.eclipse.microprofile.openapi.models.headers.Header mapHeader(final Header header) {
        final HeaderImpl impl = new HeaderImpl();
        impl.deprecated(header.deprecated());
        impl.description(header.description());
        impl.allowEmptyValue(header.allowEmptyValue());
        impl.required(header.required());
        impl.schema(mapSchema(header.schema()));
        return impl;
    }

    private org.eclipse.microprofile.openapi.models.media.Schema mapSchema(final Schema schema) {
        if (schema.hidden()) {
            return null;
        }

        final SchemaImpl impl = new SchemaImpl();
        impl.deprecated(schema.deprecated());
        impl.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.valueOf(schema.type().name()));
        impl.title(schema.title());
        impl.description(schema.description());
        impl.format(schema.format());
        impl.ref(schema.ref());
        impl.example(schema.example());
        impl.not(mapSchemaFromClass(schema.not()));
        impl.oneOf(Stream.of(schema.oneOf()).map(this::mapSchemaFromClass).collect(toList()));
        impl.anyOf(Stream.of(schema.anyOf()).map(this::mapSchemaFromClass).collect(toList()));
        impl.allOf(Stream.of(schema.allOf()).map(this::mapSchemaFromClass).collect(toList()));
        impl.multipleOf(BigDecimal.valueOf(schema.multipleOf()));
        impl.minimum(new BigDecimal(schema.minimum()));
        impl.maximum(new BigDecimal(schema.maximum()));
        impl.exclusiveMinimum(schema.exclusiveMinimum());
        impl.exclusiveMaximum(schema.exclusiveMaximum());
        impl.minLength(schema.minLength());
        impl.maxLength(schema.maxLength());
        impl.pattern(schema.pattern());
        impl.nullable(schema.nullable());
        impl.minProperties(schema.minProperties());
        impl.maxProperties(schema.maxProperties());
        impl.minItems(schema.minItems());
        impl.maxItems(schema.maxItems());
        impl.uniqueItems(schema.uniqueItems());
        impl.readOnly(schema.readOnly());
        impl.writeOnly(schema.writeOnly());
        impl.externalDocs(mapExternalDocumentation(schema.externalDocs()));
        impl.defaultValue(toType(schema.defaultValue(), impl.getType()));
        if (schema.required()) {
            impl.required(Stream.of(schema.requiredProperties()).collect(toList()));
        }
        if (schema.discriminatorMapping().length > 0) {
            impl.discriminator(new DiscriminatorImpl().mapping(Stream.of(schema.discriminatorMapping())
                    .collect(toMap(DiscriminatorMapping::value, it -> it.schema().getName()))));
        }
        impl.setEnumeration(Stream.of(schema.enumeration()).collect(toList()));
        return impl;
    }

    private Object toType(final String s, final org.eclipse.microprofile.openapi.models.media.Schema.SchemaType type) {
        if (s.isEmpty()) {
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

    private List<org.eclipse.microprofile.openapi.models.parameters.Parameter> mapParameters(final Parameter[] parameters) {
        return Stream.of(parameters).map(this::mapParameter).collect(toList());
    }

    private org.eclipse.microprofile.openapi.models.parameters.Parameter mapParameter(final Parameter parameter) {
        final ParameterImpl impl = new ParameterImpl();
        of(parameter.example()).filter(v -> !v.isEmpty()).ifPresent(impl::example);
        if (parameter.examples().length > 0) {
            impl.examples(Stream.of(parameter.examples()).collect(toMap(
                    it -> of(it.name()).filter(n -> !n.isEmpty()).orElseGet(() -> mapRefToName(it.ref())), this::mapExample)));
        }
        return impl;
    }

    private Example mapExample(final ExampleObject exampleObject) {
        final ExampleImpl impl = new ExampleImpl();
        impl.description(exampleObject.description());
        impl.externalValue(exampleObject.externalValue());
        impl.value(exampleObject.value());
        impl.summary(exampleObject.summary());
        return impl;
    }

    private Map<String, Object> mapExtensions(final Extension[] extensions) {
        return Stream.of(extensions).collect(toMap(Extension::name, Extension::value));
    }

    private String mapRefToName(final String ref) {
        final String[] segments = ref.split("/");
        return segments[segments.length - 1];
    }

    public void processApplication(final OpenAPI api, final AnnotatedType<?> type) {
        ofNullable(type.getAnnotation(OpenAPIDefinition.class)).ifPresent(def -> processDefinition(api, def));
        type.getAnnotations(Extension.class).forEach(ext -> api.addExtension(ext.name(), ext.value()));
        type.getAnnotations(Server.class).forEach(server -> api.addServer(mapServer(server)));
        type.getAnnotations(ExternalDocumentation.class).forEach(doc -> api.setExternalDocs(mapExternalDocumentation(doc)));
        type.getAnnotations(Tag.class).forEach(tag -> api.addTag(mapTag(tag)));
        type.getAnnotations(SecurityScheme.class).forEach(securityRequirement -> {
            // todo
        });
        type.getAnnotations(Schema.class).forEach(scheme -> {
            // todo: append
        });
    }

    private void processDefinition(final OpenAPI api, final OpenAPIDefinition annotation) {
        processInfo(api, annotation.info());
        processTags(api, annotation.tags());
        api.externalDocs(mapExternalDocumentation(annotation.externalDocs()));
        processSecurity(api, annotation.security());
        processServers(api, annotation.servers());
        processComponents(api, annotation.components());
    }

    private void processServers(final OpenAPI api, final Server[] servers) {
        if (servers.length == 0) {
            return;
        }
        api.servers(Stream.of(servers).map(this::mapServer).collect(toList()));
    }

    private org.eclipse.microprofile.openapi.models.servers.Server mapServer(final Server server) {
        final ServerImpl impl = new ServerImpl();
        impl.url(server.url());
        impl.description(server.description());

        final ServerVariable[] variables = server.variables();
        if (variables.length != 0) {
            final ServerVariablesImpl variablesImpl = new ServerVariablesImpl();
            variablesImpl.putAll(Stream.of(variables).collect(toMap(ServerVariable::name, this::mapVariable)));
            impl.variables(variablesImpl);
        }
        return impl;
    }

    private org.eclipse.microprofile.openapi.models.servers.ServerVariable mapVariable(final ServerVariable serverVariable) {
        final ServerVariableImpl impl = new ServerVariableImpl();
        impl.defaultValue(serverVariable.defaultValue());
        impl.description(serverVariable.description());
        impl.enumeration(asList(serverVariable.enumeration()));
        return impl;
    }

    private void processSecurity(final OpenAPI api, final SecurityRequirement[] security) {
        if (security.length == 0) {
            return;
        }
        api.security(Stream.of(security).map(this::mapSecurity).collect(toList()));
    }

    private org.eclipse.microprofile.openapi.models.security.SecurityRequirement mapSecurity(
            final SecurityRequirement securityRequirement) {
        final SecurityRequirementImpl impl = new SecurityRequirementImpl();
        impl.addScheme(securityRequirement.name(), asList(securityRequirement.scopes()));
        return impl;
    }

    private void processTags(final OpenAPI api, final Tag[] tags) {
        if (tags.length == 0) {
            return;
        }
        api.tags(Stream.of(tags).map(this::mapTag).collect(toList()));
    }

    private TagImpl mapTag(final Tag tag) {
        final TagImpl impl = new TagImpl();
        impl.description(tag.description());
        impl.name(tag.name());
        impl.externalDocs(mapExternalDocumentation(tag.externalDocs()));
        // todo: ref support
        return impl;
    }

    private org.eclipse.microprofile.openapi.models.ExternalDocumentation mapExternalDocumentation(
            final ExternalDocumentation externalDocumentation) {
        final ExternalDocumentationImpl impl = new ExternalDocumentationImpl();
        impl.url(externalDocumentation.url());
        impl.description(externalDocumentation.description());
        return impl;
    }

    private void processInfo(final OpenAPI api, final Info info) {
        final Contact contact = info.contact();
        final ContactImpl contactImpl = new ContactImpl();
        contactImpl.email(contact.email());
        contactImpl.name(contact.name());
        contactImpl.url(contact.url());

        final License license = info.license();
        final org.eclipse.microprofile.openapi.models.info.License licenseImpl = new LicenseImpl();
        licenseImpl.name(license.name());
        licenseImpl.url(license.url());

        final org.eclipse.microprofile.openapi.models.info.Info impl = new InfoImpl();
        impl.description(info.description());
        impl.termsOfService(info.termsOfService());
        impl.title(info.title());
        impl.version(info.version());
        impl.contact(contactImpl);
        impl.license(licenseImpl);
        api.info(impl);
    }
}
