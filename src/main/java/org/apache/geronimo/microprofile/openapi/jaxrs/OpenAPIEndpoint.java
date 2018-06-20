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

import static java.util.Optional.ofNullable;
import static javax.ws.rs.Priorities.USER;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.geronimo.microprofile.openapi.cdi.GeronimoOpenAPIExtension;
import org.eclipse.microprofile.openapi.models.OpenAPI;

// theorically a jaxrs endpoint to benefit from jaxrs tooling and filters - but forbidden by TCK :(
// @Path("openapi") + @GET
@Provider
@PreMatching
@Priority(USER)
@ApplicationScoped
public class OpenAPIEndpoint implements ContainerRequestFilter {

    @Inject
    private GeronimoOpenAPIExtension extension;

    private OpenAPI openApi;

    @Override
    public void filter(final ContainerRequestContext rc) {
        if (!HttpMethod.GET.equals(rc.getRequest().getMethod())) {
            return;
        }
        final String path = rc.getUriInfo().getPath();
        if ("openapi".equals(path)) {
            rc.abortWith(Response.ok(openApi).type(ofNullable(rc.getMediaType()).orElse(APPLICATION_JSON_TYPE)).build());
        }
        if ("openapi.json".equals(path)) {
            rc.abortWith(Response.ok(openApi).type(APPLICATION_JSON_TYPE).build());
        }
    }

    @Context
    public void setApplication(final Application application) {
        this.openApi = extension.getOrCreateOpenAPI(application);
    }
}
