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
package org.apache.geronimo.microprofile.openapi.impl.filter;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.Components;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.callbacks.Callback;
import org.eclipse.microprofile.openapi.models.links.Link;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.eclipse.microprofile.openapi.models.servers.Server;
import org.eclipse.microprofile.openapi.models.tags.Tag;

public class FilterImpl {
    private final OASFilter delegate;

    public FilterImpl(final OASFilter delegate) {
        this.delegate = delegate;
    }

    // todo: likely complete it since the visitor pattern is not complete ATM
    public OpenAPI filter(final OpenAPI api) {
        ofNullable(api.getComponents()).ifPresent(this::filterComponents);
        ofNullable(api.getPaths())
                .ifPresent(paths -> paths.forEach((k, v) -> {
                    ofNullable(v.readOperationsMap())
                            .ifPresent(operations -> {
                                operations.entrySet().stream()
                                        .filter(it -> it.getValue() != null)
                                        .filter(it -> delegate.filterOperation(it.getValue()) == null)
                                        .map(Map.Entry::getKey)
                                        .collect(toList())
                                        .forEach(operations::remove);
                                operations.values().stream().filter(Objects::nonNull).forEach(this::filterOperation);
                            });

                    paths.entrySet().stream()
                            .filter(it -> delegate.filterPathItem(it.getValue()) == null)
                            .map(Map.Entry::getKey)
                            .collect(toList())
                            .forEach(paths::remove);

                    ofNullable(v.getParameters()).ifPresent(this::filterParameters);
                    ofNullable(v.getServers()).ifPresent(this::filterServers);
                }));
        ofNullable(api.getServers()).ifPresent(this::filterServers);
        ofNullable(api.getTags()).ifPresent(this::filterTags);
        delegate.filterOpenAPI(api);
        return api;
    }

    private void filterOperation(final Operation op) {
        ofNullable(op.getServers()).ifPresent(this::filterServers);
        ofNullable(op.getRequestBody()).ifPresent(it -> {
            if (delegate.filterRequestBody(it) == null) {
                op.requestBody(null);
            }
        });
        ofNullable(op.getParameters()).ifPresent(this::filterParameters);
        ofNullable(op.getCallbacks()).ifPresent(this::filterCallbacks);
        ofNullable(op.getResponses())
                .ifPresent(responses -> {
                    responses.forEach((rk, response) -> {
                        ofNullable(response.getLinks()).ifPresent(this::filterLinks);
                        ofNullable(response.getContent())
                                .ifPresent(contents -> contents.values().forEach(content -> {
                                    ofNullable(content.getSchema()).ifPresent(schema -> {
                                        if (delegate.filterSchema(schema) == null) {
                                            content.setSchema(null);
                                        }
                                    });
                                }));
                    responses.entrySet().stream()
                            .filter(it -> delegate.filterAPIResponse(it.getValue()) == null)
                            .map(Map.Entry::getKey)
                            .collect(toList())
                            .forEach(responses::remove);
                    });
                });
    }

    private void filterComponents(final  Components components) {
        ofNullable(components.getCallbacks()).ifPresent(this::filterCallbacks);
        ofNullable(components.getHeaders())
                .ifPresent(headers ->
                        headers.entrySet().stream()
                                .filter(it -> delegate.filterHeader(it.getValue()) == null)
                                .map(Map.Entry::getKey)
                                .collect(toList())
                                .forEach(headers::remove));
        ofNullable(components.getLinks()).ifPresent(this::filterLinks);
        ofNullable(components.getParameters())
                .ifPresent(parameters ->
                        parameters.entrySet().stream()
                                .filter(it -> delegate.filterParameter(it.getValue()) == null)
                                .map(Map.Entry::getKey)
                                .collect(toList())
                                .forEach(parameters::remove));
        ofNullable(components.getRequestBodies())
                .ifPresent(requestBodies ->
                        requestBodies.entrySet().stream()
                                .filter(it -> delegate.filterRequestBody(it.getValue()) == null)
                                .map(Map.Entry::getKey)
                                .collect(toList())
                                .forEach(requestBodies::remove));
        ofNullable(components.getResponses())
                .ifPresent(responses ->
                        responses.entrySet().stream()
                                .filter(it -> delegate.filterAPIResponse(it.getValue()) == null)
                                .map(Map.Entry::getKey)
                                .collect(toList())
                                .forEach(responses::remove));
        ofNullable(components.getSchemas())
                .ifPresent(schemas ->
                        schemas.entrySet().stream()
                                .filter(it -> delegate.filterSchema(it.getValue()) == null)
                                .map(Map.Entry::getKey)
                                .collect(toList())
                                .forEach(schemas::remove));
        ofNullable(components.getSecuritySchemes())
                .ifPresent(schemes ->
                        schemes.entrySet().stream()
                                .filter(it -> delegate.filterSecurityScheme(it.getValue()) == null)
                                .map(Map.Entry::getKey)
                                .collect(toList())
                                .forEach(schemes::remove));
    }

    private void filterLinks(final Map<String, Link> links) {
        links.entrySet().stream()
                .filter(it -> delegate.filterLink(it.getValue()) == null)
                .map(Map.Entry::getKey)
                .collect(toList())
                .forEach(links::remove);
    }

    private void filterCallbacks(final Map<String, Callback> callbacks) {
        callbacks.entrySet().stream()
                .filter(it -> delegate.filterCallback(it.getValue()) == null)
                .map(Map.Entry::getKey)
                .collect(toList())
                .forEach(callbacks::remove);
    }

    private void filterTags(final List<Tag> tags) {
        tags.stream()
                .filter(it -> delegate.filterTag(it) == null)
                .collect(toList())
                .forEach(tags::remove);
    }

    private void filterParameters(final List<Parameter> parameters) {
        parameters.stream()
                .filter(it -> delegate.filterParameter(it) == null)
                .collect(toList())
                .forEach(parameters::remove);
    }

    private void filterServers(final List<Server> servers) {
        servers.stream()
                .filter(it -> delegate.filterServer(it) == null)
                .collect(toList())
                .forEach(servers::remove);
    }
}
