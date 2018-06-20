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
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.stream.Stream;

import javax.enterprise.inject.Vetoed;
import javax.enterprise.inject.spi.AnnotatedType;

import org.apache.geronimo.microprofile.openapi.impl.model.ContactImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ExternalDocumentationImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.InfoImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.LicenseImpl;
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

@Vetoed
public class AnnotationProcessor {

    // TODO completely
    public void processClass(final OpenAPI api, final AnnotatedType<?> annotatedType) {
        // TODO
    }

    // TODO completely
    private void processComponents(final OpenAPI api, final Components components) {
        final Callback[] callbacks = components.callbacks();
        final ExampleObject[] examples = components.examples();
        final Header[] headers = components.headers();
        final Link[] links = components.links();
        final Parameter[] parameters = components.parameters();
        final RequestBody[] requestBodies = components.requestBodies();
        final APIResponse[] responses = components.responses();
        final Schema[] schema = components.schemas();
        final SecurityScheme[] securitySchemes = components.securitySchemes();
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

    private org.eclipse.microprofile.openapi.models.security.SecurityRequirement mapSecurity(final SecurityRequirement securityRequirement) {
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

    private org.eclipse.microprofile.openapi.models.ExternalDocumentation mapExternalDocumentation(final ExternalDocumentation externalDocumentation) {
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
