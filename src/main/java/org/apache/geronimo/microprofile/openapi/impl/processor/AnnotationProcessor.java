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
import static java.util.Collections.singletonList;
import static java.util.Locale.ROOT;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

import javax.enterprise.inject.Vetoed;
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

import org.apache.cxf.jaxrs.ext.PATCH;
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
import org.apache.geronimo.microprofile.openapi.impl.model.OAuthFlowImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.OAuthFlowsImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.OperationImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ParameterImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.PathItemImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.PathsImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.RequestBodyImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.SchemaImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ScopesImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.SecurityRequirementImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.SecuritySchemeImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ServerImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ServerVariableImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ServerVariablesImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.TagImpl;
import org.eclipse.microprofile.openapi.annotations.Components;
import org.eclipse.microprofile.openapi.annotations.ExternalDocumentation;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.callbacks.Callback;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterStyle;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
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
import org.eclipse.microprofile.openapi.annotations.media.DiscriminatorMapping;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlow;
import org.eclipse.microprofile.openapi.annotations.security.OAuthFlows;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.servers.Server;
import org.eclipse.microprofile.openapi.annotations.servers.ServerVariable;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
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

    private void populateSchemaModel(
            final org.eclipse.microprofile.openapi.models.Components components,
            final org.eclipse.microprofile.openapi.models.media.Schema schema,
            final Class<?> implementation) {
        schema.items(new SchemaImpl()); // todo cache schema in components and just set the ref here
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

        processServers(api, annotatedType.getAnnotationsByType(Server.class));

        ofNullable(annotatedType.getAnnotation(SecurityScheme.class))
                .ifPresent(s -> {
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
                        getPathItem(api, completePath).setGET(buildOperation(api, m, annotatedType));
                    } else if (m.isAnnotationPresent(PUT.class)) {
                        getPathItem(api, completePath).setPUT(buildOperation(api, m, annotatedType));
                    } else if (m.isAnnotationPresent(POST.class)) {
                        getPathItem(api, completePath).setPOST(buildOperation(api, m, annotatedType));
                    } else if (m.isAnnotationPresent(HEAD.class)) {
                        getPathItem(api, completePath).setHEAD(buildOperation(api, m, annotatedType));
                    } else if (m.isAnnotationPresent(OPTIONS.class)) {
                        getPathItem(api, completePath).setOPTIONS(buildOperation(api, m, annotatedType));
                    } else if (m.isAnnotationPresent(PATCH.class)) {
                        getPathItem(api, completePath).setPATCH(buildOperation(api, m, annotatedType));
                    } else if (m.isAnnotationPresent(DELETE.class)) {
                        getPathItem(api, completePath).setDELETE(buildOperation(api, m, annotatedType));
                    } else {
                        Stream.of(m.getAnnotations()).filter(it -> it.annotationType().isAnnotationPresent(HttpMethod.class))
                                .findFirst().ifPresent(http -> {
                            final String mtd = http.annotationType().getAnnotation(HttpMethod.class).value();
                            if ("TRACE".equals(mtd)) {
                                getPathItem(api, completePath).setTRACE(buildOperation(api, m, annotatedType));
                            } // else: how to map it???
                        });
                    }
                });
    }

    public void processApplication(final OpenAPI api, final AnnotatedElement type) {
        ofNullable(type.getAnnotation(OpenAPIDefinition.class)).ifPresent(def -> processDefinition(api, def));
        Stream.of(type.getAnnotationsByType(Extension.class)).forEach(ext -> api.addExtension(ext.name(), ext.value()));
        Stream.of(type.getAnnotationsByType(Server.class)).forEach(server -> api.addServer(mapServer(server)));
        Stream.of(type.getAnnotationsByType(ExternalDocumentation.class))
                .forEach(doc -> api.setExternalDocs(mapExternalDocumentation(doc)));
        Stream.of(type.getAnnotationsByType(Tag.class)).forEach(tag -> api.addTag(mapTag(tag)));
        Stream.of(type.getAnnotationsByType(SecurityRequirement.class))
                .forEach(securityRequirementeme -> api.security(singletonList(mapSecurity(securityRequirementeme))));
    }

    private org.eclipse.microprofile.openapi.models.media.Schema mapSchemaFromClass(
            final org.eclipse.microprofile.openapi.models.Components components, final Type model) {
        final org.eclipse.microprofile.openapi.models.media.Schema schema = new SchemaImpl();
        if ((Class.class.isInstance(model) && Class.class.cast(model).isPrimitive()) || String.class == model) {
            if (boolean.class == model) {
                schema.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.BOOLEAN);
            } else if (String.class == model) {
                schema.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.STRING);
            } else if (double.class == model || float.class == model) {
                schema.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.NUMBER);
            } else if (double.class == model || float.class == model) {
                schema.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.INTEGER);
            }
        } else {
            schema.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.OBJECT);
        }
        if (Class.class.isInstance(model)) {
            populateSchemaModel(components, schema, Class.class.cast(model));
        }
        return schema;
    }

    private Operation buildOperation(final OpenAPI api, final AnnotatedMethodElement m, final AnnotatedElement declaring) {
        if (ofNullable(m.getAnnotation(org.eclipse.microprofile.openapi.annotations.Operation.class))
                .map(org.eclipse.microprofile.openapi.annotations.Operation::hidden).orElse(false)) {
            return null;
        }

        final OperationImpl operation = new OperationImpl();
        of(m.getAnnotationsByType(Callback.class)).filter(it -> it.length > 0).ifPresent(cbs -> {
            final Map<String, org.eclipse.microprofile.openapi.models.callbacks.Callback> callbacks = Stream.of(cbs)
                    .collect(toMap(it -> of(it.name()).filter(n -> !n.isEmpty()).orElseGet(() -> mapRefToName(it.ref())),
                            it -> mapCallback(api.getComponents(), it)));
            operation.callbacks(callbacks);
        });
        of(m.getAnnotationsByType(Server.class)).filter(it -> it.length > 0)
                .ifPresent(it -> operation.servers(Stream.of(it).map(this::mapServer).collect(toList())));
        ofNullable(m.getAnnotation(SecurityScheme.class))
                .ifPresent(s -> api.getComponents().addSecurityScheme(s.ref(), mapSecurityScheme(s)));
        of(m.getAnnotationsByType(Tag.class)).filter(s -> s.length > 0)
                .ifPresent(tags -> operation.tags(Stream.of(tags)
                        .map(it -> of(it.name()).map(tag -> {
                            api.getTags().add(mapTag(it));
                            return tag;
                        }).filter(v -> !v.isEmpty()).orElseGet(() -> {
                            final String ref = it.ref();
                            return Stream.of(declaring.getAnnotationsByType(Tag.class))
                                    .filter(t -> t.name().equals(ref))
                                    .findFirst()
                                    .map(Tag::name)
                                    .orElseGet(() -> api.getTags().stream()
                                            .filter(t -> t.getName().equals(ref))
                                            .findFirst()
                                            .map(org.eclipse.microprofile.openapi.models.tags.Tag::getName)
                                            .orElse(ref));
                        })).collect(toList())));
        of(m.getAnnotationsByType(APIResponse.class)).filter(s -> s.length > 0).ifPresent(items -> {
            final APIResponses responses = new APIResponsesImpl();
            responses.putAll(Stream.of(items).collect(toMap(it -> of(it.responseCode()).filter(c -> !c.isEmpty()).orElse("200"),
                    it -> mapResponse(api.getComponents(), it), (a, b) -> b)));
            responses.values().stream().filter(it -> it.getContent() == null).forEach(v -> {
                Type returnType = m.getReturnType();
                if (returnType == void.class || returnType == Response.class) {
                    final ContentImpl content = new ContentImpl();
                    if (Response.class == returnType || Stream.of(m.getParameters()).anyMatch(it -> it.isAnnotationPresent(Suspended.class))) {
                        content.put("200", new MediaTypeImpl());
                    } else {
                        content.put("204", new MediaTypeImpl());
                    }
                    v.content(content);
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
                final ContentImpl content = new ContentImpl();
                final MediaTypeImpl mediaType = new MediaTypeImpl();
                mediaType.setSchema(mapSchemaFromClass(api.getComponents(), returnType));
                content.put("", mediaType);
                v.content(content);
            });
            responses.values().stream().filter(r -> r.getContent() != null).forEach(resp -> {
                ofNullable(resp.getContent().remove("")).ifPresent(updated -> {
                    final Optional<Produces> produces = ofNullable(ofNullable(m.getAnnotation(Produces.class))
                            .orElseGet(() -> m.getDeclaringClass().getAnnotation(Produces.class)));
                    produces.ifPresent(mt -> Stream.of(mt.value()).forEach(type -> resp.getContent().put(type, updated)));
                });
            });
            operation.responses(responses);
        });
        ofNullable(m.getAnnotation(org.eclipse.microprofile.openapi.annotations.Operation.class)).ifPresent(op -> {
            operation.operationId(op.operationId());
            operation.summary(op.summary());
            operation.deprecated(op.deprecated());
            operation.description(op.description());
        });
        operation.parameters(Stream.of(m.getParameters())
                .filter(it -> !it.isAnnotationPresent(Context.class) && !it.isAnnotationPresent(Suspended.class))
                .map(it -> buildParameter(it, api.getComponents())
                        .orElseGet(() -> new ParameterImpl().schema(mapSchemaFromClass(api.getComponents(), it.getType()))))
                .filter(Objects::nonNull).collect(toList()));
        Stream.of(m.getParameters())
                .filter(p -> p.isAnnotationPresent(RequestBody.class))
                .findFirst()
                .ifPresent(p -> operation.requestBody(mapRequestBody(api.getComponents(), p.getAnnotation(RequestBody.class))));
        return operation;
    }

    private Optional<org.eclipse.microprofile.openapi.models.parameters.Parameter> buildParameter(
            final AnnotatedElement annotatedElement, final org.eclipse.microprofile.openapi.models.Components components) {
        return ofNullable(annotatedElement.getAnnotation(Parameter.class)).map(it -> mapParameter(annotatedElement, components, it));
    }

    private PathItem getPathItem(final OpenAPI api, final String path) {
        return api.getPaths().computeIfAbsent(path, p -> new PathItemImpl());
    }

    private String buildPath(final String base, final Path path, final Path mtdPath) {
        return Stream.concat(Stream.of(base), Stream.of(path, mtdPath).filter(Objects::nonNull).map(Path::value))
                .map(v -> v.substring(v.startsWith("/") ? 1 : 0, v.endsWith("/") ? v.length() - 1 : v.length()))
                .filter(it -> !it.isEmpty())
                .collect(joining("/", "/", ""));
    }

    private void processComponents(final OpenAPI api, final Components components) {
        final org.eclipse.microprofile.openapi.models.Components impl = new ComponentsImpl();
        processCallbacks(impl, components.callbacks());
        if (components.links().length > 0) {
            impl.links(Stream.of(components.links()).collect(
                    toMap(it -> of(it.name()).filter(n -> !n.isEmpty()).orElseGet(() -> mapRefToName(it.ref())), this::mapLink)));
        }
        if (components.securitySchemes().length > 0) {
            impl.securitySchemes(Stream.of(components.securitySchemes())
                    .collect(toMap(
                            it -> of(it.securitySchemeName()).filter(v -> !v.isEmpty()).orElseGet(() -> mapRefToName(it.ref())),
                            this::mapSecurityScheme)));
        }
        if (components.requestBodies().length > 0) {
            impl.requestBodies(Stream.of(components.requestBodies())
                    .collect(toMap(it -> of(it.name()).filter(n -> !n.isEmpty()).orElseGet(() -> mapRefToName(it.ref())),
                            it -> mapRequestBody(impl, it))));
        }
        if (components.parameters().length > 0) {
            impl.parameters(Stream.of(components.parameters())
                    .collect(toMap(it -> of(it.name()).filter(n -> !n.isEmpty()).orElseGet(() -> mapRefToName(it.ref())),
                            it -> mapParameter(null, api.getComponents(), it))));
        }
        if (components.headers().length > 0) {
            impl.headers(Stream.of(components.headers())
                    .collect(toMap(it -> of(it.name()).filter(n -> !n.isEmpty()).orElseGet(() -> mapRefToName(it.ref())),
                            it -> mapHeader(impl, it))));
        }
        if (components.examples().length > 0) {
            impl.examples(Stream.of(components.examples()).collect(toMap(
                    it -> of(it.name()).filter(n -> !n.isEmpty()).orElseGet(() -> mapRefToName(it.ref())), this::mapExample)));
        }
        if (components.schemas().length > 0) {
            impl.schemas(Stream.of(components.schemas())
                    .collect(toMap(it -> of(it.name()).filter(n -> !n.isEmpty()).orElseGet(() -> mapRefToName(it.ref())),
                            it -> mapSchema(impl, it))));
        }
        if (components.responses().length > 0) {
            final APIResponses responses = new APIResponsesImpl();
            responses.putAll(Stream.of(components.responses())
                    .collect(toMap(it -> of(it.name()).filter(c -> !c.isEmpty()).orElseGet(() -> mapRefToName(it.ref())),
                            it -> mapResponse(impl, it), (a, b) -> b)));
            impl.responses(responses);
        }
        api.components(impl);
    }

    private org.eclipse.microprofile.openapi.models.security.SecurityScheme mapSecurityScheme(
            final SecurityScheme securityScheme) {
        return updateSecurityScheme(securityScheme, new SecuritySchemeImpl());
    }

    private org.eclipse.microprofile.openapi.models.security.SecurityScheme updateSecurityScheme(
            final SecurityScheme securityScheme, final SecuritySchemeImpl scheme) {
        of(securityScheme.bearerFormat()).filter(v -> !v.isEmpty()).ifPresent(scheme::bearerFormat);
        of(securityScheme.description()).filter(v -> !v.isEmpty()).ifPresent(scheme::description);
        of(securityScheme.openIdConnectUrl()).filter(v -> !v.isEmpty()).ifPresent(scheme::openIdConnectUrl);
        of(securityScheme.ref()).filter(v -> !v.isEmpty()).ifPresent(scheme::ref);
        of(securityScheme.scheme()).filter(v -> !v.isEmpty()).ifPresent(scheme::scheme);
        of(securityScheme.securitySchemeName()).filter(v -> !v.isEmpty()).ifPresent(scheme::scheme);
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
        Stream.of(authorizationCode.scopes()).forEach(s -> scopes.put(s.name(), s.description()));
        return scopes;
    }

    private boolean isSet(final OAuthFlow oAuthFlow) {
        return !oAuthFlow.authorizationUrl().isEmpty() || !oAuthFlow.refreshUrl().isEmpty() || !oAuthFlow.tokenUrl().isEmpty();
    }

    private void processCallbacks(final org.eclipse.microprofile.openapi.models.Components impl, final Callback[] callbacks) {
        if (callbacks.length > 0) {
            impl.setCallbacks(Stream.of(callbacks)
                    .collect(toMap(it -> of(it.name()).filter(n -> !n.isEmpty()).orElseGet(() -> mapRefToName(it.ref())),
                            it -> mapCallback(impl, it))));
        }
    }

    private org.eclipse.microprofile.openapi.models.callbacks.Callback mapCallback(
            final org.eclipse.microprofile.openapi.models.Components components, final Callback callback) {
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
                    operation.parameters(mapParameters(components, co.parameters()));
                }
                operation.requestBody(mapRequestBody(components, co.requestBody()));
                if (co.security().length > 0) {
                    operation.security(Stream.of(co.security()).map(this::mapSecurity).collect(toList()));
                }
                if (co.responses().length > 0) {
                    final APIResponses responses = new APIResponsesImpl();
                    responses.putAll(Stream.of(co.responses())
                            .collect(toMap(it -> of(it.responseCode()).filter(c -> !c.isEmpty()).orElse("200"),
                                    it -> mapResponse(components, it))));
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

        impl.put(callback.callbackUrlExpression(), pathItem);
        return impl;
    }

    private org.eclipse.microprofile.openapi.models.responses.APIResponse mapResponse(
            final org.eclipse.microprofile.openapi.models.Components components, final APIResponse response) {
        final APIResponseImpl impl = new APIResponseImpl();
        impl.description(response.description());
        of(response.ref()).filter(r -> !r.isEmpty()).ifPresent(impl::ref);
        if (response.headers().length > 0) {
            impl.headers(Stream.of(response.headers())
                    .collect(toMap(it -> of(it.name()).filter(n -> !n.isEmpty()).orElseGet(() -> mapRefToName(it.ref())),
                            it -> mapHeader(components, it))));
        }
        if (response.content().length > 0) {
            final ContentImpl content = new ContentImpl();
            content.putAll(Stream.of(response.content()).collect(
                    toMap(it -> of(it.mediaType()).filter(v -> !v.isEmpty()).orElse("*/*"), it -> mapContent(components, it))));
            impl.content(content);
        }
        if (response.links().length > 0) {
            impl.links(Stream.of(response.links()).collect(
                    toMap(it -> of(it.name()).filter(n -> !n.isEmpty()).orElseGet(() -> mapRefToName(it.ref())), this::mapLink)));
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
            final org.eclipse.microprofile.openapi.models.Components components, final RequestBody requestBody) {

        final RequestBodyImpl impl = new RequestBodyImpl();
        impl.description(requestBody.description());
        impl.ref(requestBody.ref());
        impl.required(requestBody.required());
        if (requestBody.content().length > 0) {
            final ContentImpl content = new ContentImpl();
            content.putAll(Stream.of(requestBody.content()).collect(
                    toMap(it -> of(it.mediaType()).filter(v -> !v.isEmpty()).orElse("*/*"), it -> mapContent(components, it))));
            impl.content(content);
        }
        return impl;
    }

    private MediaType mapContent(final org.eclipse.microprofile.openapi.models.Components components, final Content content) {
        final MediaTypeImpl impl = new MediaTypeImpl();
        if (content.encoding().length > 0) {
            Stream.of(content.encoding()).forEach(e -> impl.addEncoding(e.name(), mapEncoding(components, e)));
        }
        impl.setSchema(mapSchema(components, content.schema()));
        if (content.examples().length > 0) {
            impl.examples(Stream.of(content.examples()).collect(toMap(
                    it -> of(it.name()).filter(n -> !n.isEmpty()).orElseGet(() -> mapRefToName(it.ref())), this::mapExample)));
        }
        return impl;
    }

    private Encoding mapEncoding(final org.eclipse.microprofile.openapi.models.Components components,
                                 final org.eclipse.microprofile.openapi.annotations.media.Encoding e) {
        final EncodingImpl impl = new EncodingImpl();
        impl.allowReserved(e.allowReserved());
        impl.explode(e.explode());
        impl.contentType(e.contentType());
        of(e.style()).filter(it -> !it.isEmpty()).map(it -> it.toUpperCase(ROOT))
                .ifPresent(v -> impl.style(Encoding.Style.valueOf(v)));
        if (e.headers().length > 0) {
            impl.headers(Stream.of(e.headers())
                    .collect(toMap(it -> of(it.name()).filter(n -> !n.isEmpty()).orElseGet(() -> mapRefToName(it.ref())),
                            it -> mapHeader(components, it))));
        }
        return impl;
    }

    private org.eclipse.microprofile.openapi.models.headers.Header mapHeader(
            final org.eclipse.microprofile.openapi.models.Components components, final Header header) {
        final HeaderImpl impl = new HeaderImpl();
        of(header.ref()).filter(s -> !s.isEmpty()).ifPresent(impl::ref);
        impl.deprecated(header.deprecated());
        impl.description(header.description());
        impl.allowEmptyValue(header.allowEmptyValue());
        impl.required(header.required());
        impl.schema(mapSchema(components, header.schema()));
        impl.style(org.eclipse.microprofile.openapi.models.headers.Header.Style.SIMPLE);
        return impl;
    }

    private org.eclipse.microprofile.openapi.models.media.Schema mapSchema(
            final org.eclipse.microprofile.openapi.models.Components components, final Schema schema) {
        if (schema.hidden()) {
            return null;
        }

        final SchemaImpl impl = new SchemaImpl();
        if (!schema.ref().isEmpty()) {
            impl.ref(resolveSchemaRef(components, schema.ref()));
        } else {
            if (schema.implementation() != Void.class) {
                populateSchemaModel(components, impl, schema.implementation());
            }
            impl.deprecated(schema.deprecated());
            if (schema.type() != SchemaType.DEFAULT) {
                impl.type(org.eclipse.microprofile.openapi.models.media.Schema.SchemaType.valueOf(schema.type().name()));
            }
            impl.title(schema.title());
            impl.description(schema.description());
            impl.format(schema.format());
            impl.ref(schema.ref());
            impl.example(schema.example());
            of(schema.not()).filter(it -> it != Void.class).ifPresent(t -> impl.not(mapSchemaFromClass(components, t)));
            impl.oneOf(Stream.of(schema.oneOf()).map(it -> mapSchemaFromClass(components, it)).collect(toList()));
            impl.anyOf(Stream.of(schema.anyOf()).map(it -> mapSchemaFromClass(components, it)).collect(toList()));
            impl.allOf(Stream.of(schema.allOf()).map(it -> mapSchemaFromClass(components, it)).collect(toList()));
            impl.multipleOf(BigDecimal.valueOf(schema.multipleOf()));
            impl.minimum(toBigDecimal(schema.minimum()));
            impl.maximum(toBigDecimal(schema.maximum()));
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
        }
        return impl;
    }

    private String resolveSchemaRef(final org.eclipse.microprofile.openapi.models.Components components, final String ref) {
        if (ref.startsWith("#")) {
            return ref;
        }
        if (components.getSchemas().containsKey(ref)) {
            return "#/components/schemas/" + ref;
        }
        return ref;
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

    private List<org.eclipse.microprofile.openapi.models.parameters.Parameter> mapParameters(
            final org.eclipse.microprofile.openapi.models.Components components, final Parameter[] parameters) {
        return Stream.of(parameters).map(it -> mapParameter(null, components, it)).collect(toList());
    }

    private org.eclipse.microprofile.openapi.models.parameters.Parameter mapParameter(
            final AnnotatedElement annotatedElement,
            final org.eclipse.microprofile.openapi.models.Components components,
            final Parameter parameter) {
        final ParameterImpl impl = new ParameterImpl();
        impl.description(parameter.description());
        impl.required(parameter.required());
        impl.name(parameter.name());
        impl.style(of(parameter.style())
                .filter(s -> s != ParameterStyle.DEFAULT).map(Enum::name)
                .map(org.eclipse.microprofile.openapi.models.parameters.Parameter.Style::valueOf)
                .orElse(null));
        impl.allowEmptyValue(parameter.allowEmptyValue());
        impl.allowReserved(parameter.allowReserved());
        impl.schema(mapSchema(components, parameter.schema()));
        of(parameter.content()).filter(it -> it.length > 0).map(Stream::of).ifPresent(c -> {
            final ContentImpl content = new ContentImpl();
            content.putAll(c.collect(
                    toMap(it -> of(it.mediaType()).filter(v -> !v.isEmpty()).orElse("*/*"), it -> mapContent(components, it))));
            impl.content(content);
        });
        of(parameter.example()).filter(v -> !v.isEmpty()).ifPresent(impl::example);
        if (parameter.examples().length > 0) {
            impl.examples(Stream.of(parameter.examples()).collect(toMap(
                    it -> of(it.name()).filter(n -> !n.isEmpty()).orElseGet(() -> mapRefToName(it.ref())), this::mapExample)));
        }
        if (annotatedElement != null) {
            if (annotatedElement.isAnnotationPresent(HeaderParam.class)) {
                impl.in(org.eclipse.microprofile.openapi.models.parameters.Parameter.In.HEADER);
            } else if (annotatedElement.isAnnotationPresent(CookieParam.class)) {
                impl.in(org.eclipse.microprofile.openapi.models.parameters.Parameter.In.COOKIE);
            } else if (annotatedElement.isAnnotationPresent(PathParam.class)) {
                impl.in(org.eclipse.microprofile.openapi.models.parameters.Parameter.In.PATH);
            } else if (annotatedElement.isAnnotationPresent(QueryParam.class)) {
                impl.in(org.eclipse.microprofile.openapi.models.parameters.Parameter.In.QUERY);
            }
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

    private org.eclipse.microprofile.openapi.models.tags.Tag mapTag(final Tag tag) {
        final TagImpl impl = new TagImpl();
        impl.name(tag.name());
        impl.description(tag.description());
        impl.externalDocs(mapExternalDocumentation(tag.externalDocs()));
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
