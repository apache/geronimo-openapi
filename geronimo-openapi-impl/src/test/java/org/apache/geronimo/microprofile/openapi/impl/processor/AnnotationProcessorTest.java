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

import org.apache.geronimo.microprofile.openapi.config.GeronimoOpenAPIConfig;
import org.apache.geronimo.microprofile.openapi.impl.model.OpenAPIImpl;
import org.apache.geronimo.microprofile.openapi.impl.processor.reflect.ClassElement;
import org.apache.geronimo.microprofile.openapi.impl.processor.reflect.MethodElement;
import org.apache.geronimo.microprofile.openapi.impl.processor.spi.NamingStrategy;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.PathItem;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.eclipse.microprofile.openapi.models.responses.APIResponses;
import org.testng.annotations.Test;

import javax.json.JsonPatch;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class AnnotationProcessorTest {

    @Test
    public void ensureParametersAreMapped() {
        AnnotationProcessor annotationProcessor = new AnnotationProcessor(GeronimoOpenAPIConfig.create(), new NamingStrategy.Default(), null);
        OpenAPI openAPI = new OpenAPIImpl();
        annotationProcessor.processClass("", openAPI, new ClassElement(TestResource.class),
                Stream.of(TestResource.class.getMethods()).map(MethodElement::new));
        PathItem pathItem = openAPI.getPaths().get("/test/{a}");
        assertNotNull(pathItem);
        List<Parameter> parameters = pathItem.getGET().getParameters();
        assertEquals(Parameter.In.PATH, parameters.get(0).getIn());
        assertEquals("a", parameters.get(0).getName());
        // TODO add more assertions
    }
    
    @Test
    public void ensureParameterAnnotationsAreMerged() {
        AnnotationProcessor annotationProcessor = new AnnotationProcessor(GeronimoOpenAPIConfig.create(), new NamingStrategy.Default(), null);
        OpenAPI openAPI = new OpenAPIImpl();
        annotationProcessor.processClass("", openAPI, new ClassElement(TestResource.class),
                Stream.of(TestResource.class.getMethods()).map(MethodElement::new));
        PathItem pathItem = openAPI.getPaths().get("/test/bye");
        assertNotNull(pathItem);
        List<Parameter> parameters = pathItem.getGET().getParameters();
        assertEquals(Parameter.In.QUERY, parameters.get(0).getIn());
        assertEquals("b", parameters.get(0).getName());
    }
    
    @Test
    public void ensureResponsesMediaTypeIsSetForDefaultResponses() {
        AnnotationProcessor annotationProcessor = new AnnotationProcessor(GeronimoOpenAPIConfig.create(), new NamingStrategy.Default(), null);
        OpenAPI openAPI = new OpenAPIImpl();
        annotationProcessor.processClass("", openAPI, new ClassElement(TestResource.class),
                Stream.of(TestResource.class.getMethods()).map(MethodElement::new));
        PathItem pathItem = openAPI.getPaths().get("/test/bye");
        assertNotNull(pathItem);
        APIResponses responses = pathItem.getGET().getResponses();
        assertEquals(responses.size(), 2);
        assertNotNull(responses.get("default"));
        assertNotNull(responses.get("default").getContent().get("text/plain"));
        assertNotNull(responses.get("204"));
        assertNotNull(responses.get("204").getContent().get("text/plain"));
    }
    
    @Test
    public void ensureResponsesMediaTypeIsSetForAllResponses() {
        AnnotationProcessor annotationProcessor = new AnnotationProcessor(GeronimoOpenAPIConfig.create(), new NamingStrategy.Default(), null);
        OpenAPI openAPI = new OpenAPIImpl();
        annotationProcessor.processClass("", openAPI, new ClassElement(TestResource.class),
                Stream.of(TestResource.class.getMethods()).map(MethodElement::new));
        PathItem pathItem = openAPI.getPaths().get("/test/bye");
        assertNotNull(pathItem);
        APIResponses responses = pathItem.getPATCH().getResponses();
        assertEquals(responses.size(), 2);
        assertNotNull(responses.get("404"));
        assertNotNull(responses.get("404").getContent().get("application/json"));
        assertNotNull(responses.get("204"));
        assertNotNull(responses.get("204").getContent().get("application/json"));
    }
    
    @Test
    public void ensureResponsesDefaultMediaTypeIsSet() {
        AnnotationProcessor annotationProcessor = new AnnotationProcessor(GeronimoOpenAPIConfig.create(), new NamingStrategy.Default(), null);
        OpenAPI openAPI = new OpenAPIImpl();
        annotationProcessor.processClass("", openAPI, new ClassElement(TestResource.class),
                Stream.of(TestResource.class.getMethods()).map(MethodElement::new));
        PathItem pathItem = openAPI.getPaths().get("/test/bye");
        assertNotNull(pathItem);
        APIResponses responses = pathItem.getDELETE().getResponses();
        assertEquals(responses.size(), 1);
        assertNotNull(responses.get("204"));
        assertNotNull(responses.get("204").getContent().get("*/*"));
    }

    @Test
    public void namingStrategy() {
        final Map<NamingStrategy, String> expectations = new HashMap<>();
        expectations.put(new NamingStrategy.Default(), "hello");
        expectations.put(new NamingStrategy.Qualified(), "org.apache.geronimo.microprofile.openapi.impl.processor.AnnotationProcessorTest$TestResource.hello");
        expectations.put(new NamingStrategy.SimpleQualified(), "TestResource.hello");
        expectations.put(new NamingStrategy.SimpleQualifiedCamelCase(), "TestResourceHello");
        expectations.put(new NamingStrategy.Http(), "GET:/test/{a}");

        final GeronimoOpenAPIConfig config = (value, def) -> null;
        expectations.forEach((strategy, operationName) -> {
            final AnnotationProcessor annotationProcessor = new AnnotationProcessor(config, strategy, null);
            final OpenAPI openAPI = new OpenAPIImpl();
            annotationProcessor.processClass("", openAPI, new ClassElement(TestResource.class),
                    Stream.of(TestResource.class.getMethods()).map(MethodElement::new));
            assertEquals(openAPI.getPaths().get("/test/{a}").getGET().getOperationId(), operationName);
        });
    }

    @Test
    public void rootPath() {
        final AnnotationProcessor annotationProcessor = new AnnotationProcessor((value, def) -> null, new NamingStrategy.Default(), null);
        final OpenAPI openAPI = new OpenAPIImpl();
        annotationProcessor.processClass("", openAPI, new ClassElement(RootPath.class),
                Stream.of(RootPath.class.getMethods()).map(MethodElement::new));
        assertNotNull(openAPI.getPaths().get("/{a}").getGET().getOperationId()); // we didn't get an index exception
    }

    @Test
    public void patch() {
        final AnnotationProcessor annotationProcessor = new AnnotationProcessor((value, def) -> null, new NamingStrategy.Default(), null);
        final OpenAPI openAPI = new OpenAPIImpl();
        annotationProcessor.processClass("", openAPI, new ClassElement(Patched.class),
                Stream.of(Patched.class.getMethods()).map(MethodElement::new));
        assertNotNull(openAPI.getPaths().get("/{a}").getPATCH().getOperationId()); // we didn't get an index exception
    }

    @Path("/")
    public class Patched {

        @PATCH
        @Path("/{a}")
        @Produces(MediaType.TEXT_PLAIN)
        public String hello(final String foo) {
            return null;
        }
    }

    @Path("/")
    public class RootPath {

        @GET
        @Path("/{a}")
        @Produces(MediaType.TEXT_PLAIN)
        public String hello() {
            return null;
        }
    }

    @Path("/test")
    public class TestResource {

        @GET
        @Path("/{a}")
        @Produces(MediaType.TEXT_PLAIN)
        public String hello(@PathParam("a") String a) {
            return "hello";
        }
        
        @GET
        @Path("/bye")
        @Produces(MediaType.TEXT_PLAIN)
        public void bye(@org.eclipse.microprofile.openapi.annotations.parameters.Parameter(required = true) @QueryParam("b") String b) {
        }
        
        @DELETE
        @Path("/bye")
        @APIResponse(responseCode = "204")
        public void bye() {
        }
        
        @PATCH
        @Path("/bye")
        @Produces(MediaType.APPLICATION_JSON)
        @APIResponse(responseCode = "204")
        @APIResponse(responseCode = "404")
        public Response bye(JsonPatch patch) {
            return Response.ok().build();
        }
    }
}
