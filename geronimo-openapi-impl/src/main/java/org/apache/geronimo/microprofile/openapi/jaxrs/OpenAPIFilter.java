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
package org.apache.geronimo.microprofile.openapi.jaxrs;

import static javax.ws.rs.Priorities.USER;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.MediaType.WILDCARD_TYPE;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import org.apache.geronimo.microprofile.openapi.cdi.GeronimoOpenAPIExtension;
import org.eclipse.microprofile.openapi.models.OpenAPI;

// theorically a jaxrs endpoint to benefit from jaxrs tooling and filters - but forbidden by TCK :(
// @Path("openapi") + @GET
@Provider
@Dependent
@PreMatching
@Priority(USER)
public class OpenAPIFilter implements ContainerRequestFilter {

    private static final Annotation[] NO_ANNOTATION = new Annotation[0];

    @Inject
    private GeronimoOpenAPIExtension extension;

    @Context
    private Providers providers;

    private OpenAPI openApi;
    private MediaType defaultMediaType;

    @PostConstruct
    private void init() {
        defaultMediaType = defaultMediaType == null ? extension.getDefaultMediaType() : defaultMediaType;
    }

    @Override
    public void filter(final ContainerRequestContext rc) {
        if (!HttpMethod.GET.equals(rc.getRequest().getMethod())) {
            return;
        }
        final String path = rc.getUriInfo().getPath();
        if ("openapi".equals(path)) {
            final List<MediaType> mediaTypes = rc.getAcceptableMediaTypes();
            rc.abortWith(Response.ok(openApi).type(selectType(mediaTypes)).build());
        }
        if ("openapi.json".equals(path)) {
            rc.abortWith(Response.ok(openApi).type(APPLICATION_JSON_TYPE).build());
        }
        if ("openapi.yml".equals(path) || "openapi.yaml".equals(path)) {
            rc.abortWith(Response.ok(openApi).type("text/vnd.yaml").build());
        }
    }

    private MediaType selectType(final List<MediaType> mediaTypes) {
        if (mediaTypes.contains(APPLICATION_JSON_TYPE)) {
            return APPLICATION_JSON_TYPE;
        }
        if (mediaTypes.isEmpty()) {
            return defaultMediaType;
        }
        return mediaTypes.stream()
                .filter(it -> !WILDCARD_TYPE.equals(it))
                .findFirst()
                .filter(it -> providers.getMessageBodyReader(OpenAPI.class, OpenAPI.class, NO_ANNOTATION, it) != null)
                .orElse(defaultMediaType);
    }

    @Context
    public void setApplication(final Application application) {
        this.openApi = extension.getOrCreateOpenAPI(application);
    }

    public void setDefaultMediaType(final MediaType defaultMediaType) {
        this.defaultMediaType = defaultMediaType;
    }
}
