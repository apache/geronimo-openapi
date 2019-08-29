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
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.Locale.ROOT;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.enterprise.inject.Vetoed;
import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonValue;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.geronimo.microprofile.openapi.config.GeronimoOpenAPIConfig;
import org.apache.geronimo.microprofile.openapi.impl.model.APIResponseImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.APIResponsesImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.CallbackImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ComponentsImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ContactImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ContentImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.EncodingImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ExampleImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ExternalDocumentationImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.HeaderImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.InfoImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.LicenseImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.LinkImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.MediaTypeImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.OAuthFlowImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.OAuthFlowsImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.OperationImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ParameterImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.PathItemImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.PathsImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.RequestBodyImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ScopesImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.SecurityRequirementImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.SecuritySchemeImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ServerImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ServerVariableImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ServerVariablesImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.TagImpl;
import org.apache.geronimo.microprofile.openapi.impl.processor.spi.NamingStrategy;
import org.eclipse.microprofile.openapi.OASConfig;
import org.eclipse.microprofile.openapi.annotations.Components;
import org.eclipse.microprofile.openapi.annotations.ExternalDocumentation;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.callbacks.Callback;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterStyle;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeIn;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.extensions.Extension;
import org.eclipse.microprofile.openapi.annotations.headers.Header;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;
import org.eclipse.microprofile.openapi.annotations.links.Link;
import org.eclipse.microprofile.openapi.annotations.links.LinkParameter;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameters;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlow;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlows;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.eclipse.microprofile.openapi.annotations.servers.ServerVariable;
import org.eclipse.microprofile.openapi.annotations.servers.Servers;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.openapi.annotations.tags.Tags;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.PathItem;
import org.eclipse.microprofile.openapi.models.examples.Example;
import org.eclipse.microprofile.openapi.models.media.Encoding;
import org.eclipse.microprofile.openapi.models.media.MediaType;
import org.eclipse.microprofile.openapi.models.responses.APIResponses;
import org.eclipse.microprofile.openapi.models.security.Scopes;

@Vetoed
public class AnnotationProcessor {
    private final GeronimoOpenAPIConfig config;
    private final SchemaProcessor schemaProcessor;
    private final NamingStrategy operationNamingStrategy;
    private final JsonReaderFactory jsonReaderFactory;
    private final Collection<String> operationId = new HashSet<>();

    public AnnotationProcessor(final GeronimoOpenAPIConfig config, final NamingStrategy strategy,
                               final JsonReaderFactory factory) {
        this.config = config;
        this.schemaProcessor = new SchemaProcessor();
        this.operationNamingStrategy = strategy;
        this.jsonReaderFactory = factory != null ? factory : Json.createReaderFactory(emptyMap());
    }

    public void processClass(final String basePath, final OpenAPI api, final AnnotatedElement annotatedType,
                             final Stream<AnnotatedMethodElement> methods) {
        final Path path = annotatedType.getAnnotation(Path.class);
        if (api.getPaths() == null) {
            api.paths(new PathsImpl());
        }

        Stream.of(annotatedType.getAnnotationsByType(Tag.class))
                .map(t -> of(t.ref()).filter(it -> !it.isEmpty())
                        .flatMap(ref -> api.getTags().stream().filter(it -> it.getName().equals(ref)).findFirst())
                        .orElseGet(() -> mapTag(t))).forEach(api::addTag);

        Stream.of(annotatedType.getAnnotationsByType(SecurityScheme.class))
                .forEach(s -> {
                    if (api.getComponents() == null) {
                        api.setComponents(new ComponentsImpl());
                    }
                    api.getComponents().addSecurityScheme(s.securitySchemeName(), mapSecurityScheme(s));
                });
        methods.filter(
                m -> Stream.of(m.getAnnotations()).anyMatch(it -> it.annotationType().getName().startsWith("javax.ws.rs.")))
                .forEach(m -> {
                    final Path nestedPath = m.getAnnotation(Path.class);
                    final String completePath = buildPath(basePath, path, nestedPath);
                    if (m.isAnnotationPresent(GET.class)) {
                        getPathItem(api, completePath).setGET(buildOperation(api, m, annotatedType, "GET", completePath));
                    } else if (m.isAnnotationPresent(PUT.class)) {
                        getPathItem(api, completePath).setPUT(buildOperation(api, m, annotatedType, "PUT", completePath));
                    } else if (m.isAnnotationPresent(POST.class)) {
                        getPathItem(api, completePath).setPOST(buildOperation(api, m, annotatedType, "POST", completePath));
                    } else if (m.isAnnotationPresent(HEAD.class)) {
                        getPathItem(api, completePath).setHEAD(buildOperation(api, m, annotatedType, "HEAD", completePath));
                    } else if (m.isAnnotationPresent(OPTIONS.class)) {
                        getPathItem(api, completePath).setOPTIONS(buildOperation(api, m, annotatedType, "OPTIONS", completePath));
                    } else if (m.isAnnotationPresent(DELETE.class)) {
                        getPathItem(api, completePath).setDELETE(buildOperation(api, m, annotatedType, "DELETE", completePath));
                    } else {
                        Stream.of(m.getAnnotations()).filter(it -> it.annotationType().isAnnotationPresent(HttpMethod.class))
                                .findFirst().ifPresent(http -> {
                            final String mtd = http.annotationType().getAnnotation(HttpMethod.class).value();
                            if ("TRACE".equals(mtd)) {
                                getPathItem(api, completePath).setTRACE(buildOperation(api, m, annotatedType, mtd, completePath));
                            } else if ("PATCH".equals(mtd)) {
                                getPathItem(api, completePath).setPATCH(buildOperation(api, m, annotatedType, mtd, completePath));
                            } // else: how to map it
                        });
                    }
                });
    }

    public void processApplication(final OpenAPI api, final AnnotatedElement type) {
        ofNullable(type.getAnnotation(OpenAPIDefinition.class)).ifPresent(def -> processDefinition(api, def));

        final Optional<String> servers = ofNullable(config.read(OASConfig.SERVERS, null));
        if (servers.isPresent()) {
            api.servers(mapConfiguredServers(servers.get()));
        } else {
            Stream.of(type.getAnnotationsByType(Server.class)).forEach(server -> api.addServer(mapServer(server)));
        }

        Stream.of(type.getAnnotationsByType(Extension.class)).forEach(ext -> api.addExtension(ext.name(), ext.value()));
        Stream.of(type.getAnnotationsByType(ExternalDocumentation.class))
                .forEach(doc -> api.setExternalDocs(mapExternalDocumentation(doc)));
        Stream.of(type.getAnnotationsByType(Tag.class)).forEach(tag -> api.addTag(mapTag(tag)));
        Stream.of(type.getAnnotationsByType(SecurityRequirement.class))
              .forEach(security -> {
                  if (api.getSecurity() == null) {
                      api.setSecurity(new ArrayList<>(1));
                  }
                  api.addSecurityRequirement(mapSecurity(security));
              });
        Stream.of(type.getAnnotationsByType(SecurityScheme.class))
                .forEach(scheme -> {
                    if (api.getComponents().getSecuritySchemes() == null) {
                        api.getComponents().setSecuritySchemes(new HashMap<>());
                    }
                    api.getComponents().addSecurityScheme(scheme.securitySchemeName(), mapSecurityScheme(scheme));
                });
    }

    private List<org.eclipse.microprofile.openapi.models.servers.Server> mapConfiguredServers(final String servers) {
        return Stream.of(servers.split(","))
                .map(String::trim)
                .filter(v -> !v.isEmpty())
                .map(server -> new ServerImpl().url(server))
                .collect(toList());
    }

    private Operation buildOperation(final OpenAPI api, final AnnotatedMethodElement m, final AnnotatedElement declaring,
                                     final String httpVerb, final String path) {
        final Optional<org.eclipse.microprofile.openapi.annotations.Operation> opOpt = ofNullable(m.getAnnotation(org.eclipse.microprofile.openapi.annotations.Operation.class));
        if (opOpt.map(org.eclipse.microprofile.openapi.annotations.Operation::hidden).orElse(false)) {
            return null;
        }

        final Optional<List<String>> produces = findProduces(m);

        final OperationImpl operation = new OperationImpl();

        if (opOpt.isPresent()) {
            final org.eclipse.microprofile.openapi.annotations.Operation op = opOpt.get();
            if (!op.operationId().isEmpty()) {
                operation.operationId(op.operationId());
            } else {
                operation.operationId(createOperationId(m, httpVerb, path));
            }
            if (!op.summary().isEmpty()) {
                operation.summary(op.summary());
            }
            operation.deprecated(op.deprecated());
            if (!op.description().isEmpty()) {
                operation.description(op.description());
            }
        } else {
            operation.operationId(createOperationId(m, httpVerb, path));
        }

        final Optional<String> servers = ofNullable(config.read(OASConfig.SERVERS_OPERATION_PREFIX + operation.getOperationId(), null));
        if (servers.isPresent()) {
            servers.ifPresent(s -> operation.servers(mapConfiguredServers(s)));
        } else {
            ofNullable(ofNullable(findServers(m)).orElseGet(() -> findServers(declaring)))
                    .ifPresent(it -> operation.servers(Stream.of(it).map(this::mapServer).collect(toList())));
        }

        of(m.getAnnotationsByType(Callback.class)).filter(it -> it.length > 0).ifPresent(cbs -> {
            final Map<String, org.eclipse.microprofile.openapi.models.callbacks.Callback> callbacks = Stream.of(cbs)
                    .collect(toMap(it -> of(it.name()).filter(n -> !n.isEmpty()).orElseGet(() -> it.ref()),
                            it -> mapCallback(api, it)));
            operation.callbacks(callbacks);
        });

        ofNullable(m.getAnnotation(SecurityScheme.class))
                .ifPresent(s -> {
                    org.eclipse.microprofile.openapi.models.Components components = api.getComponents();
                    if (components == null) {
                        components = new ComponentsImpl();
                        api.setComponents(components);
                    }
                    components.addSecurityScheme(s.ref(), mapSecurityScheme(s));
                });

        ofNullable(m.getAnnotationsByType(Extension.class))
                .map(this::mapExtensions)
                .ifPresent(exts -> exts.forEach(operation::addExtension));

        operation.security(of(Stream.concat(
                Stream.of(m.getAnnotationsByType(SecurityRequirement.class)),
                Stream.of(m.getDeclaringClass().getAnnotationsByType(SecurityRequirement.class)))
                .map(this::mapSecurity).collect(toList()))
                .filter(s -> !s.isEmpty())
                .orElse(null));

        ofNullable(findTags(m, declaring))
                .map(tags -> Stream.of(tags)
                     .map(it -> of(it.name())
                            .filter(v -> !v.isEmpty())
                            .map(tag -> {
                                api.addTag(mapTag(it));
                                return tag;
                            }).filter(v -> !v.isEmpty())
                            .orElseGet(() -> {
                                final String ref = it.ref();
                                return Stream.of(declaring.getAnnotationsByType(Tag.class))
                                    .filter(t -> t.name().equals(ref))
                                    .findFirst()
                                    .map(Tag::name)
                                    .filter(v -> !v.isEmpty())
                                    .orElseGet(() -> api.getTags().stream()
                                        .filter(t -> t.getName().equals(ref))
                                        .findFirst()
                                        .map(org.eclipse.microprofile.openapi.models.tags.Tag::getName)
                                        .orElse(ref));
                           }))
                     .distinct()
                     .filter(v -> !v.isEmpty())
                     .collect(toList()))
                .ifPresent(operation::tags);

        of(m.getAnnotationsByType(APIResponse.class)).filter(s -> s.length > 0).ifPresent(items -> {
            final APIResponses responses = new APIResponsesImpl();
            responses.putAll(Stream.of(items).collect(toMap(it -> of(it.responseCode()).filter(c -> !c.isEmpty()).orElse("200"),
                    it -> mapResponse(() -> getOrCreateComponents(api), it, produces.orElse(null)), (a, b) -> b)));
            responses.values().stream()
                     .filter(it -> it.getContent() == null || it.getContent().isEmpty() ||
                             it.getContent().values().stream().anyMatch(c -> c.getSchema() == null))
                     .forEach(v -> {
                         Type returnType = m.getReturnType();
                         if (returnType == void.class || returnType == Response.class) {
                             if (v.getContent() == null || v.getContent().isEmpty()) {
                                 final ContentImpl content = new ContentImpl();

                                 produces
                                     .orElseGet(() -> singletonList("*/*"))
                                     .forEach(mt -> content.addMediaType(mt, new MediaTypeImpl()));
                                 
                                 v.content(content);
                             }
                             return;
                         }

                         if (ParameterizedType.class.isInstance(returnType)) {
                             final ParameterizedType pt = ParameterizedType.class.cast(returnType);
                             if (pt.getActualTypeArguments().length > 0) {
                                 if (pt.getRawType() == CompletionStage.class) {
                                     returnType = pt.getActualTypeArguments()[0];
                                 }
                             }
                         }
                         final org.eclipse.microprofile.openapi.models.media.Schema schema =
                                 schemaProcessor.mapSchemaFromClass(
                                         () -> getOrCreateComponents(api), returnType);
                         if (v.getContent() == null || v.getContent().isEmpty()) {
                             final ContentImpl content = new ContentImpl();
                             final MediaTypeImpl mediaType = new MediaTypeImpl();
                             mediaType.setSchema(schema);
                             content.addMediaType("", mediaType);
                             v.content(content);
                         } else {
                             v.getContent().values().stream()
                                .filter(it -> it.getSchema() == null)
                                .forEach(it -> it.schema(schema));
                         }
                    });
            responses.values().stream().filter(r -> r.getContent() != null)
                     .forEach(resp -> ofNullable(resp.getContent().remove(""))
                             .ifPresent(updated -> produces.ifPresent(mt -> mt.forEach(type -> resp.getContent().addMediaType(type, updated)))));
            operation.responses(responses);
        });
        operation.parameters(Stream.of(m.getParameters())
                .filter(it -> it.isAnnotationPresent(Parameter.class) || hasJaxRsParams(it))
                .map(it -> buildParameter(it, api)
                        .orElseGet(() -> new ParameterImpl().schema(schemaProcessor.mapSchemaFromClass(
                                () -> getOrCreateComponents(api), it.getType()))))
                .filter(Objects::nonNull).collect(toList()));
        Stream.of(m.getParameters())
                .filter(it -> it.isAnnotationPresent(Parameters.class))
                .map(it -> it.getAnnotation(Parameters.class).value())
                .forEach(params -> operation.parameters(mapParameters(api, params)));
        Stream.of(m.getParameters())
                .filter(p -> p.isAnnotationPresent(RequestBody.class) ||
                        (!p.isAnnotationPresent(Suspended.class) && !p.isAnnotationPresent(Context.class) &&
                                !p.isAnnotationPresent(Parameter.class) && !hasJaxRsParams(p)))
                .findFirst()
                .ifPresent(p -> operation.requestBody(mapRequestBody(
                        produces.filter(it -> !it.isEmpty()).map(it -> it.iterator().next()).orElse(null), p,
                        () -> getOrCreateComponents(api), ofNullable(p.getAnnotation(RequestBody.class))
                    .orElseGet(() -> m.getAnnotation(RequestBody.class)))));
        // ensure parameters have all schemas
        operation.getParameters().stream()
                .filter(it -> it.getSchema() == null)
                .forEach(it -> Stream.of(m.getParameters())
                        .filter(mp -> findAnnotatedParameterByName(it, mp))
                        .findFirst()
                        .ifPresent(mp -> it.setSchema(schemaProcessor.mapSchemaFromClass(() -> getOrCreateComponents(api), mp.getType()))));
        // ensure parameter contents have all schemas
        operation.getParameters().stream()
                .filter(it -> it.getContent() != null && it.getContent().values().stream().anyMatch(c -> c.getSchema() == null || c.getSchema().getType() == null))
                .forEach(it -> Stream.of(m.getParameters())
                        .filter(mp -> findAnnotatedParameterByName(it, mp))
                        .findFirst()
                        .ifPresent(mp -> it.getContent().values().stream().filter(c -> c.getSchema() == null || c.getSchema().getType() == null).forEach(mt -> {
                            final org.eclipse.microprofile.openapi.models.media.Schema schema = schemaProcessor.mapSchemaFromClass(() -> getOrCreateComponents(api), mp.getType());
                            if (mt.getSchema() == null) {
                                mt.setSchema(schema);
                            } else {
                                mt.getSchema().type(schema.getType());
                            }
                        })));
        if (operation.getResponses() == null) {
            final APIResponsesImpl responses = new APIResponsesImpl();
            operation.responses(responses);
            final boolean normalResponse = Stream.of(m.getParameters()).noneMatch(it -> it.isAnnotationPresent(Suspended.class));
            final ContentImpl content = new ContentImpl();
            if (normalResponse) {
                final MediaType impl = new MediaTypeImpl();
                impl.setSchema(schemaProcessor.mapSchemaFromClass(() -> getOrCreateComponents(api), m.getReturnType()));
                produces.orElseGet(() -> singletonList("*/*")).forEach(key -> content.addMediaType(key, impl));
            }
            final org.eclipse.microprofile.openapi.models.responses.APIResponse defaultResponse =
                    new APIResponseImpl().description("default response").content(content);
            responses.addApiResponse(
                    m.getReturnType() == void.class || m.getReturnType() == Void.class && normalResponse ?
                            "204" : "200", defaultResponse);
            responses.defaultValue(defaultResponse);
        }
        return operation;
    }

    private String createOperationId(final AnnotatedMethodElement m, final String httpVerb, final String path) {
        String name = operationNamingStrategy.name(new NamingStrategy.Context(m, httpVerb, path));
        int idx = 1;
        while (!operationId.add(name)) {
            name = name + "_" + idx;
            idx++;
        }
        return name;
    }

    private boolean findAnnotatedParameterByName(final org.eclipse.microprofile.openapi.models.parameters.Parameter it,
                                                 final AnnotatedTypeElement mp) {
        final String expected = ofNullable(it.getName()).orElse("");
        if (mp.isAnnotationPresent(PathParam.class)) {
            return expected.equals(mp.getAnnotation(PathParam.class).value());
        } else if (mp.isAnnotationPresent(HeaderParam.class)) {
            return expected.equals(mp.getAnnotation(HeaderParam.class).value());
        } else if (mp.isAnnotationPresent(CookieParam.class)) {
            return expected.equals(mp.getAnnotation(CookieParam.class).value());
        } else if (mp.isAnnotationPresent(QueryParam.class)) {
            return expected.equals(mp.getAnnotation(QueryParam.class).value());
        }
        return false;
    }

    private Server[] findServers(final AnnotatedElement annotatedElement) {
        if (annotatedElement.getAnnotation(Server.class) != null || annotatedElement.getAnnotation(Servers.class) != null) {
            return annotatedElement.getAnnotationsByType(Server.class);
        }
        return null;
    }

    private Tag[] findTags(final AnnotatedMethodElement m, final AnnotatedElement declaring) {
        return ofNullable(findTags(m)).orElseGet(() -> findTags(declaring));
    }

    private Tag[] findTags(final AnnotatedElement m) {
        final Tag mTag = m.getAnnotation(Tag.class);
        final Tags mTags = m.getAnnotation(Tags.class);
        if (mTag != null || mTags != null) {
            if (mTag == null) {
                return mapTagsAnnotationToTags(mTags).toArray(Tag[]::new);
            }
            return Stream.concat(Stream.of(mTag), ofNullable(mTags)
                    .map(t -> t.refs().length == 0 ? Stream.of(t.value()) : mapTagsAnnotationToTags(t))
                    .orElseGet(Stream::empty))
                 .filter(Objects::nonNull)
                 .toArray(Tag[]::new);
        }
        return null;
    }

    private Stream<Tag> mapTagsAnnotationToTags(Tags t) {
        return Stream.concat(Stream.of(t.value()), Stream.of(t.refs()).map(TagAnnotation::new));
    }

    private Optional<List<String>> findProduces(final AnnotatedMethodElement m) {
        return ofNullable(ofNullable(m.getAnnotation(Produces.class))
                .orElseGet(() -> m.getDeclaringClass().getAnnotation(Produces.class)))
                .map(p -> Stream.of(p.value()).collect(toList()));
    }

    private boolean hasJaxRsParams(final AnnotatedElement it) {
        return it.isAnnotationPresent(HeaderParam.class) || it.isAnnotationPresent(CookieParam.class) ||
                it.isAnnotationPresent(PathParam.class) || it.isAnnotationPresent(QueryParam.class);
    }

    private Optional<org.eclipse.microprofile.openapi.models.parameters.Parameter> buildParameter(
            final AnnotatedTypeElement annotatedElement, final OpenAPI openAPI) {
        return ofNullable(ofNullable(annotatedElement.getAnnotation(Parameter.class))
                .map(it -> mapParameter(annotatedElement, () -> getOrCreateComponents(openAPI), it))
                .orElseGet(() -> {
                    if (hasJaxRsParams(annotatedElement)) {
                        final ParameterImpl parameter = new ParameterImpl();
                        if (annotatedElement.isAnnotationPresent(HeaderParam.class)) {
                            parameter.in(org.eclipse.microprofile.openapi.models.parameters.Parameter.In.HEADER)
                                    .style(org.eclipse.microprofile.openapi.models.parameters.Parameter.Style.SIMPLE)
                                    .name(annotatedElement.getAnnotation(HeaderParam.class).value());
                        } else if (annotatedElement.isAnnotationPresent(CookieParam.class)) {
                            parameter.in(org.eclipse.microprofile.openapi.models.parameters.Parameter.In.COOKIE)
                                    .style(org.eclipse.microprofile.openapi.models.parameters.Parameter.Style.FORM)
                                    .name(annotatedElement.getAnnotation(CookieParam.class).value());
                        } else if (annotatedElement.isAnnotationPresent(PathParam.class)) {
                            parameter.required(true)
                                    .in(org.eclipse.microprofile.openapi.models.parameters.Parameter.In.PATH)
                                    .style(org.eclipse.microprofile.openapi.models.parameters.Parameter.Style.SIMPLE)
                                    .name(annotatedElement.getAnnotation(PathParam.class).value());
                        } else if (annotatedElement.isAnnotationPresent(QueryParam.class)) {
                            parameter.in(org.eclipse.microprofile.openapi.models.parameters.Parameter.In.QUERY)
                                    .style(org.eclipse.microprofile.openapi.models.parameters.Parameter.Style.FORM)
                                    .name(annotatedElement.getAnnotation(QueryParam.class).value());
                        }
                        parameter.schema(schemaProcessor.mapSchemaFromClass(
                                    () -> getOrCreateComponents(openAPI), annotatedElement.getType()));
                        return parameter;
                    }
                    return null;
                }));
    }

    private PathItem getPathItem(final OpenAPI api, final String path) {
        return api.getPaths().computeIfAbsent(path, p -> {
            final PathItemImpl item = new PathItemImpl();
            ofNullable(config.read(OASConfig.SERVERS_PATH_PREFIX + path, null))
                    .ifPresent(servers -> item.servers(mapConfiguredServers(servers)));
            return item;
        });
    }

    private String buildPath(final String base, final Path path, final Path mtdPath) {
        return Stream.concat(Stream.of(base), Stream.of(path, mtdPath).filter(Objects::nonNull).map(Path::value))
                .map(v -> v.substring(v.startsWith("/") && !"/".equalsIgnoreCase(v) ? 1 : 0, v.endsWith("/") ? v.length() - 1 : v.length()))
                .filter(it -> !it.isEmpty())
                .collect(joining("/", "/", ""));
    }

    private void processComponents(final OpenAPI api, final Components components) {
        final org.eclipse.microprofile.openapi.models.Components impl = new ComponentsImpl();
        api.components(impl);
        processCallbacks(api, components.callbacks());
        if (components.schemas().length > 0) {
            Map<String, org.eclipse.microprofile.openapi.models.media.Schema> schemas = Stream.of(components.schemas())
                .map(it -> {
                    final String ref = of(it.name()).filter(n -> !n.isEmpty()).orElseGet(() -> it.ref());
                    return new SchemaWithRef(ref, mapSchema(api, it, ref));
                })
                .collect(toMap(it -> it.ref, it -> it.schema));

            schemas.forEach((key, value) -> impl.getSchemas().putIfAbsent(key,value));
        }
        if (components.links().length > 0) {
            impl.links(Stream.of(components.links()).collect(
                    toMap(it -> of(it.name()).filter(n -> !n.isEmpty()).orElseGet(() -> it.ref()), this::mapLink)));
        }
        if (components.securitySchemes().length > 0) {
            impl.securitySchemes(Stream.of(components.securitySchemes())
                    .collect(toMap(
                            it -> of(it.securitySchemeName()).filter(v -> !v.isEmpty()).orElseGet(() -> it.ref()),
                            this::mapSecurityScheme)));
        }
        if (components.requestBodies().length > 0) {
            impl.requestBodies(Stream.of(components.requestBodies())
                    .collect(toMap(it -> of(it.name()).filter(n -> !n.isEmpty()).orElseGet(() -> it.ref()),
                            it -> mapRequestBody(null, null, () -> impl, it))));
        }
        if (components.parameters().length > 0) {
            impl.parameters(Stream.of(components.parameters())
                    .collect(toMap(it -> of(it.name()).filter(n -> !n.isEmpty()).orElseGet(() -> it.ref()),
                            it -> mapParameter(null, () -> impl, it))));
        }
        if (components.headers().length > 0) {
            impl.headers(Stream.of(components.headers())
                    .collect(toMap(it -> of(it.name()).filter(n -> !n.isEmpty()).orElseGet(() -> it.ref()),
                            it -> mapHeader(() -> impl, it))));
        }
        if (components.examples().length > 0) {
            impl.examples(Stream.of(components.examples()).collect(toMap(
                    it -> of(it.name()).filter(n -> !n.isEmpty()).orElseGet(() -> it.ref()), this::mapExample)));
        }
        if (components.responses().length > 0) {
            final APIResponses responses = new APIResponsesImpl();
            responses.putAll(Stream.of(components.responses())
                    .collect(toMap(it -> of(it.name()).filter(c -> !c.isEmpty()).orElseGet(() -> it.ref()),
                            it -> mapResponse(() -> impl, it, null), (a, b) -> b)));
            impl.responses(responses);
        }
    }

    private org.eclipse.microprofile.openapi.models.media.Schema mapSchema(
            final OpenAPI api, final Schema schema, final String ref) {
        return ofNullable(schemaProcessor.mapSchema(() -> getOrCreateComponents(api), schema, ref))
                .map(s -> s.externalDocs(mapExternalDocumentation(schema.externalDocs())))
                .orElse(null);
    }

    private org.eclipse.microprofile.openapi.models.security.SecurityScheme mapSecurityScheme(
            final SecurityScheme securityScheme) {
        return updateSecurityScheme(securityScheme, new SecuritySchemeImpl());
    }

    private org.eclipse.microprofile.openapi.models.security.SecurityScheme updateSecurityScheme(
            final SecurityScheme securityScheme, final SecuritySchemeImpl scheme) {
        of(securityScheme.apiKeyName()).filter(v -> !v.isEmpty()).ifPresent(scheme::setName);
        of(securityScheme.bearerFormat()).filter(v -> !v.isEmpty()).ifPresent(scheme::bearerFormat);
        of(securityScheme.description()).filter(v -> !v.isEmpty()).ifPresent(scheme::description);
        of(securityScheme.openIdConnectUrl()).filter(v -> !v.isEmpty()).ifPresent(scheme::openIdConnectUrl);
        of(securityScheme.ref()).filter(v -> !v.isEmpty()).ifPresent(scheme::ref);
        of(securityScheme.scheme()).filter(v -> !v.isEmpty()).ifPresent(scheme::scheme);
        of(securityScheme.type()).filter(it -> it != SecuritySchemeType.DEFAULT)
                .map(it -> org.eclipse.microprofile.openapi.models.security.SecurityScheme.Type.valueOf(it.name()))
                .ifPresent(scheme::type);
        of(securityScheme.in()).filter(it -> it != SecuritySchemeIn.DEFAULT)
                .map(it -> org.eclipse.microprofile.openapi.models.security.SecurityScheme.In.valueOf(it.name()))
                .ifPresent(scheme::in);
        of(securityScheme.flows()).map(this::mapFlows).ifPresent(scheme::flows);
        return scheme;
    }

    private org.eclipse.microprofile.openapi.models.security.OAuthFlows mapFlows(final OAuthFlows oAuthFlows) {
        final OAuthFlowsImpl flows = new OAuthFlowsImpl();
        boolean empty = true;
        final OAuthFlow authorizationCode = oAuthFlows.authorizationCode();
        if (isSet(authorizationCode)) {
            empty = false;
            flows.authorizationCode(mapFlow(authorizationCode));
        }
        final OAuthFlow clientCredentials = oAuthFlows.clientCredentials();
        if (isSet(clientCredentials)) {
            empty = false;
            flows.clientCredentials(mapFlow(clientCredentials));
        }
        final OAuthFlow password = oAuthFlows.password();
        if (isSet(password)) {
            empty = false;
            flows.password(mapFlow(password));
        }
        final OAuthFlow implicit = oAuthFlows.implicit();
        if (isSet(implicit)) {
            empty = false;
            flows.implicit(mapFlow(implicit));
        }
        return empty ? null : flows;
    }

    private org.eclipse.microprofile.openapi.models.security.OAuthFlow mapFlow(final OAuthFlow flow) {
        return new OAuthFlowImpl()
                .refreshUrl(valueOrNull(flow.refreshUrl()))
                .tokenUrl(valueOrNull(flow.tokenUrl()))
                .authorizationUrl(valueOrNull(flow.authorizationUrl()))
                .scopes(createScopes(flow));
    }

    private String valueOrNull(final String s) {
        return s.isEmpty() ? null : s;
    }

    private Scopes createScopes(final OAuthFlow authorizationCode) {
        final ScopesImpl scopes = new ScopesImpl();
        Stream.of(authorizationCode.scopes()).forEach(s -> scopes.addScope(s.name(), s.description()));
        return scopes;
    }

    private boolean isSet(final OAuthFlow oAuthFlow) {
        return !oAuthFlow.authorizationUrl().isEmpty() || !oAuthFlow.refreshUrl().isEmpty() || !oAuthFlow.tokenUrl().isEmpty();
    }

    private void processCallbacks(final OpenAPI api, final Callback[] callbacks) {
        if (callbacks.length > 0) {
            getOrCreateComponents(api).setCallbacks(Stream.of(callbacks)
                    .collect(toMap(it -> of(it.name()).filter(n -> !n.isEmpty()).orElseGet(() -> it.ref()),
                            it -> mapCallback(api, it))));
        }
    }

    private org.eclipse.microprofile.openapi.models.callbacks.Callback mapCallback(
            final OpenAPI api, final Callback callback) {
        final CallbackImpl impl = new CallbackImpl();
        of(callback.ref()).filter(r -> !r.isEmpty()).ifPresent(impl::ref);

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
                    operation.parameters(mapParameters(api, co.parameters()));
                }
                operation.requestBody(mapRequestBody(null, null, () -> getOrCreateComponents(api), co.requestBody()));
                if (co.security().length > 0) {
                    operation.security(Stream.of(co.security()).map(this::mapSecurity).collect(toList()));
                }
                if (co.responses().length > 0) {
                    final APIResponses responses = new APIResponsesImpl();
                    responses.putAll(Stream.of(co.responses())
                            .collect(toMap(it -> of(it.responseCode()).filter(c -> !c.isEmpty()).orElse("200"),
                                    it -> mapResponse(() -> getOrCreateComponents(api), it, null))));
                    operation.responses(responses);
                }
                switch (co.method().toUpperCase(ROOT)) {
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

        impl.addPathItem(callback.callbackUrlExpression(), pathItem);
        return impl;
    }

    private org.eclipse.microprofile.openapi.models.responses.APIResponse mapResponse(
            final Supplier<org.eclipse.microprofile.openapi.models.Components> components, final APIResponse response,
            final Collection<String> defaultMediaTypes) {
        final APIResponseImpl impl = new APIResponseImpl();
        impl.description(response.description());
        of(response.ref()).filter(r -> !r.isEmpty()).ifPresent(impl::ref);
        if (response.headers().length > 0) {
            impl.headers(Stream.of(response.headers())
                    .collect(toMap(it -> of(it.name()).filter(n -> !n.isEmpty())
                                    .orElseGet(() -> it.ref().replace("#/components/headers/", "")),
                            it -> mapHeader(components, it))));
        }
        if (response.content().length > 0) {
            final ContentImpl content = new ContentImpl();
            content.putAll(Stream.of(response.content()).collect(
                    toMap(it -> of(it.mediaType()).filter(v -> !v.isEmpty()).orElse(""), it -> mapContent(components, it))));
            ofNullable(content.remove(""))
                    .ifPresent(c -> (defaultMediaTypes == null ? singletonList("*/*") : defaultMediaTypes)
                            .forEach(it -> content.addMediaType(it, c)));
            impl.content(content);
        }
        if (response.links().length > 0) {
            impl.links(Stream.of(response.links()).collect(
                    toMap(it -> of(it.name()).filter(n -> !n.isEmpty()).orElseGet(() -> it.ref()), this::mapLink)));
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
            impl.parameters(Stream.of(link.parameters()).collect(toMap(LinkParameter::name, LinkParameter::expression)));
        }
        return impl;
    }

    private org.eclipse.microprofile.openapi.models.parameters.RequestBody mapRequestBody(
            final String defaultContentType, final AnnotatedTypeElement param,
            final Supplier<org.eclipse.microprofile.openapi.models.Components> components,
            final RequestBody requestBody) {

        final org.eclipse.microprofile.openapi.models.parameters.RequestBody impl = new RequestBodyImpl()
                .content(new ContentImpl());
        if (requestBody != null) {
            if (!requestBody.description().isEmpty()) {
                impl.description(requestBody.description());
            }
            if (!requestBody.ref().isEmpty()) {
                impl.ref(requestBody.ref());
            }
            impl.required(requestBody.required());
            impl.getContent().putAll(Stream.of(requestBody.content()).collect(toMap(
                    it -> of(it.mediaType()).filter(v -> !v.isEmpty()).orElse("*/*"),
                    it -> mapContent(components, it))));
        } else if (param != null && defaultContentType != null) {
            impl.required(true);
        }
        if (impl.getContent().isEmpty() && param != null && defaultContentType != null) {
            impl.getContent().addMediaType(defaultContentType, new MediaTypeImpl()
                    .schema(schemaProcessor.mapSchemaFromClass(components, param.getType())));
        }
        return impl;
    }

    private MediaType mapContent(final Supplier<org.eclipse.microprofile.openapi.models.Components> components, final Content content) {
        final MediaTypeImpl impl = new MediaTypeImpl();
        if (content.encoding().length > 0) {
            Stream.of(content.encoding()).forEach(e -> impl.addEncoding(e.name(), mapEncoding(components, e)));
        }
        impl.setSchema(schemaProcessor.mapSchema(components, content.schema(), null));
        if (content.examples().length > 0) {
            impl.examples(Stream.of(content.examples()).collect(toMap(
                    it -> of(it.name()).filter(n -> !n.isEmpty()).orElseGet(() -> it.ref()), this::mapExample)));
        }
        if (!content.example().isEmpty()) {
            impl.example(content.example());
        }
        return impl;
    }

    private Encoding mapEncoding(final Supplier<org.eclipse.microprofile.openapi.models.Components> components,
                                 final org.eclipse.microprofile.openapi.annotations.media.Encoding e) {
        final EncodingImpl impl = new EncodingImpl();
        impl.allowReserved(e.allowReserved());
        impl.explode(e.explode());
        impl.contentType(of(e.contentType()).filter(v -> !v.isEmpty()).orElse("*/*"));
        of(e.style()).filter(it -> !it.isEmpty()).map(it -> it.toUpperCase(ROOT))
                .ifPresent(v -> impl.style(Encoding.Style.valueOf(v)));
        if (e.headers().length > 0) {
            impl.headers(Stream.of(e.headers())
                    .collect(toMap(it -> of(it.name()).filter(n -> !n.isEmpty())
                                    .orElseGet(() -> it.ref().replace("#/components/headers/", "")),
                            it -> mapHeader(components, it))));
        }
        return impl;
    }

    private org.eclipse.microprofile.openapi.models.headers.Header mapHeader(
            final Supplier<org.eclipse.microprofile.openapi.models.Components> components, final Header header) {
        final String ref = header.ref();
        if (!ref.isEmpty()) {
            final org.eclipse.microprofile.openapi.models.headers.Header headerRef = findHeaderByRef(components.get(), ref);
            final HeaderImpl impl = new HeaderImpl();
            impl.deprecated(headerRef.getDeprecated());
            impl.description(headerRef.getDescription());
            impl.allowEmptyValue(headerRef.getAllowEmptyValue());
            impl.required(headerRef.getRequired());
            impl.schema(headerRef.getSchema());
            impl.style(headerRef.getStyle());
            impl.ref(ref.startsWith("#") ? ref : ("#/components/headers/" + ref));
            return impl;
        }
        final HeaderImpl impl = new HeaderImpl();
        impl.deprecated(header.deprecated());
        impl.description(header.description());
        impl.allowEmptyValue(header.allowEmptyValue());
        impl.required(header.required());
        impl.schema(schemaProcessor.mapSchema(components, header.schema(), null));
        impl.style(org.eclipse.microprofile.openapi.models.headers.Header.Style.SIMPLE);
        return impl;
    }

    private org.eclipse.microprofile.openapi.models.headers.Header findHeaderByRef(
            final org.eclipse.microprofile.openapi.models.Components components, final String ref) {
        if (ref.startsWith("#/components/headers/")) {
            return components.getHeaders().get(ref.substring("#/components/headers/".length()));
        } // else?
        return ofNullable(components.getHeaders().get(ref))
                .orElseGet(HeaderImpl::new);
    }

    private List<org.eclipse.microprofile.openapi.models.parameters.Parameter> mapParameters(
            final OpenAPI openAPI, final Parameter[] parameters) {
        return Stream.of(parameters)
                .map(it -> mapParameter(null, () -> getOrCreateComponents(openAPI), it))
                .collect(toList());
    }

    private org.eclipse.microprofile.openapi.models.Components getOrCreateComponents(final OpenAPI openAPI) {
        org.eclipse.microprofile.openapi.models.Components components = openAPI.getComponents();
        if (components == null) {
            components = new ComponentsImpl();
            openAPI.components(components);
        }
        return components;
    }

    private org.eclipse.microprofile.openapi.models.parameters.Parameter mapParameter(
            final AnnotatedTypeElement annotatedElement,
            final Supplier<org.eclipse.microprofile.openapi.models.Components> components,
            final Parameter parameter) {
        final ParameterImpl impl = new ParameterImpl();
        impl.description(parameter.description());
        impl.required(parameter.required());
        impl.name(parameter.name());
        impl.in(of(parameter.in())
                .filter(s -> s != ParameterIn.DEFAULT).map(Enum::name)
                .map(org.eclipse.microprofile.openapi.models.parameters.Parameter.In::valueOf)
                .orElse(null));
        impl.style(of(parameter.style())
                .filter(s -> s != ParameterStyle.DEFAULT).map(Enum::name)
                .map(org.eclipse.microprofile.openapi.models.parameters.Parameter.Style::valueOf)
                .orElse(null));
        impl.allowEmptyValue(parameter.allowEmptyValue());
        impl.allowReserved(parameter.allowReserved());
        impl.schema(ofNullable(schemaProcessor.mapSchema(components, parameter.schema(), null))
                .map(s -> s.externalDocs(mapExternalDocumentation(parameter.schema().externalDocs())))
                .orElseGet(() -> {
                    if (annotatedElement == null) {
                        return null;
                    }
                    return schemaProcessor.mapSchemaFromClass(components, annotatedElement.getType());
                }));
        if (impl.getSchema() != null && impl.getSchema().getType() == null && annotatedElement != null) {
            schemaProcessor.fillSchema(components, annotatedElement.getType(), impl.getSchema(), null);
        }
        of(parameter.content()).filter(it -> it.length > 0).map(Stream::of).ifPresent(c -> {
            final ContentImpl content = new ContentImpl();
            content.putAll(c.collect(
                    toMap(it -> of(it.mediaType()).filter(v -> !v.isEmpty()).orElse("*/*"), it -> {
                        final MediaType mediaType = mapContent(components, it);
                        if (mediaType.getSchema() == null && annotatedElement != null) {
                            mediaType.schema(schemaProcessor.mapSchemaFromClass(components, annotatedElement.getType()));
                        }
                        return mediaType;
                    })));
            impl.content(content);
        });
        of(parameter.example()).filter(v -> !v.isEmpty()).ifPresent(impl::example);
        if (parameter.examples().length > 0) {
            impl.examples(Stream.of(parameter.examples()).collect(toMap(
                    it -> of(it.name()).filter(n -> !n.isEmpty()).orElseGet(() -> it.ref()), this::mapExample)));
        }
        if (annotatedElement != null) {
            if (annotatedElement.isAnnotationPresent(HeaderParam.class)) {
                final HeaderParam annotation = annotatedElement.getAnnotation(HeaderParam.class);
                impl.in(org.eclipse.microprofile.openapi.models.parameters.Parameter.In.HEADER);
                mapParameterName(impl, annotation.value());
            } else if (annotatedElement.isAnnotationPresent(CookieParam.class)) {
                final CookieParam annotation = annotatedElement.getAnnotation(CookieParam.class);
                impl.in(org.eclipse.microprofile.openapi.models.parameters.Parameter.In.COOKIE);
                mapParameterName(impl, annotation.value());
            } else if (annotatedElement.isAnnotationPresent(PathParam.class)) {
                final PathParam annotation = annotatedElement.getAnnotation(PathParam.class);
                impl.in(org.eclipse.microprofile.openapi.models.parameters.Parameter.In.PATH);
                mapParameterName(impl, annotation.value());
            } else if (annotatedElement.isAnnotationPresent(QueryParam.class)) {
                final QueryParam annotation = annotatedElement.getAnnotation(QueryParam.class);
                impl.in(org.eclipse.microprofile.openapi.models.parameters.Parameter.In.QUERY);
                mapParameterName(impl, annotation.value());
            }
        }
        return impl;
    }

    private void mapParameterName(final ParameterImpl impl, final String name) {
        if (impl.getName() == null || impl.getName().isEmpty()) {
            impl.name(name);
        }
    }

    private Example mapExample(final ExampleObject exampleObject) {
        final ExampleImpl impl = new ExampleImpl();
        if (!exampleObject.description().isEmpty()) {
            impl.description(exampleObject.description());
        }
        if (!exampleObject.externalValue().isEmpty()) {
            impl.externalValue(exampleObject.externalValue());
        }
        if (!exampleObject.value().isEmpty()) { // todo: type
            impl.value(exampleObject.value());
        }
        if (!exampleObject.summary().isEmpty()) {
            impl.summary(exampleObject.summary());
        }
        return impl;
    }

    private Map<String, Object> mapExtensions(final Extension[] extensions) {
        return Stream.of(extensions)
                .collect(toMap(Extension::name, e -> {
                    if (e.parseValue()) {
                        return parse(e.value());
                    }
                    return e.value();
                }));
    }

    private Object parse(final String value) {
        try (final JsonReader reader = jsonReaderFactory.createReader(new StringReader(value))) {
            final JsonValue jsonValue = reader.readValue();
            switch (jsonValue.getValueType()) {
                case NULL:
                    return null;
                case TRUE:
                case FALSE:
                    return JsonValue.TRUE.equals(jsonValue);
                case NUMBER:
                    final JsonNumber number = JsonNumber.class.cast(jsonValue);
                    final double doubleValue = number.doubleValue();
                    if (doubleValue == number.intValue()) {
                        return number.intValue();
                    }
                    if (doubleValue == number.longValue()) {
                        return number.longValue();
                    }
                    return doubleValue;
                default:
                    return jsonValue;
            }
        }
    }

    private void processDefinition(final OpenAPI api, final OpenAPIDefinition annotation) {
        processInfo(api, annotation.info());
        processTags(api, annotation.tags());
        api.externalDocs(mapExternalDocumentation(annotation.externalDocs()));
        processSecurity(api, annotation.security());
        api.servers(mapServers(annotation.servers()));
        processComponents(api, annotation.components());
    }

    private List<org.eclipse.microprofile.openapi.models.servers.Server> mapServers(final Server[] servers) {
        return servers.length == 0 ? null : Stream.of(servers).map(this::mapServer).collect(toList());
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
        Stream.of(tags).map(this::mapTag).forEach(api::addTag);
    }

    private org.eclipse.microprofile.openapi.models.tags.Tag mapTag(final Tag tag) {
        final TagImpl impl = new TagImpl();
        impl.name(tag.name());
        impl.description(tag.description());
        impl.externalDocs(ofNullable(tag.externalDocs()).map(this::mapExternalDocumentation).orElse(null));
        return impl;
    }

    private org.eclipse.microprofile.openapi.models.ExternalDocumentation mapExternalDocumentation(
            final ExternalDocumentation externalDocumentation) {
        if (externalDocumentation.url().isEmpty() && externalDocumentation.description().isEmpty()) {
            return null;
        }
        final ExternalDocumentationImpl impl = new ExternalDocumentationImpl();
        if (!externalDocumentation.url().isEmpty()) {
            impl.url(externalDocumentation.url());
        }
        if (!externalDocumentation.description().isEmpty()) {
            impl.description(externalDocumentation.description());
        }
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

    public String getApplicationBinding(final Class<?> application) {
        // todo: use servlet to get the servlet mapping which is a valid deployment too
        return ofNullable(application.getAnnotation(ApplicationPath.class)).map(ApplicationPath::value)
                .filter(it -> !"/".equals(it))
                .map(it -> it.endsWith("*") ? it.substring(0, it.length() - 1) : it)
                .orElse("");
    }

    public void beforeProcessing() {
        operationId.clear();
    }

    private static class TagAnnotation implements Tag {
        private final String ref;

        private TagAnnotation(final String ref) {
            this.ref = ref;
        }

        @Override
        public String name() {
            return "";
        }

        @Override
        public String description() {
            return "";
        }

        @Override
        public ExternalDocumentation externalDocs() {
            return null;
        }

        @Override
        public String ref() {
            return ref;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return Tag.class;
        }
    }

    private static class SchemaWithRef {
        private final String ref;
        private final org.eclipse.microprofile.openapi.models.media.Schema schema;

        private SchemaWithRef(final String ref,
                              final org.eclipse.microprofile.openapi.models.media.Schema schema) {
            this.ref = ref;
            this.schema = schema;
        }
    }
}
