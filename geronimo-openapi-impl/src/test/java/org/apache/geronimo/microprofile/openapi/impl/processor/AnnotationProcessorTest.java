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
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.PathItem;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.testng.annotations.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.stream.Stream;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class AnnotationProcessorTest {

    @Test
    public void ensureParametersAreMapped() {
        AnnotationProcessor annotationProcessor = new AnnotationProcessor(GeronimoOpenAPIConfig.create());
        OpenAPI openAPI = new OpenAPIImpl();
        annotationProcessor.processClass("", openAPI, new ClassElement(TestResource.class),
                Stream.of(TestResource.class.getMethods()).map(MethodElement::new));
        PathItem pathItem = openAPI.getPaths().get("/test/{a}");
        assertNotNull(pathItem);
        List<Parameter> parameters = pathItem.getGET().getParameters();
        assertEquals(Parameter.In.PATH, parameters.get(0).getIn());
        // TODO add more assertions
    }

    @Path("/test")
    public class TestResource {

        @GET
        @Path("/{a}")
        @Produces(MediaType.TEXT_PLAIN)
        public String hello(@PathParam("a") String a) {
            return "hello";
        }
    }
}
