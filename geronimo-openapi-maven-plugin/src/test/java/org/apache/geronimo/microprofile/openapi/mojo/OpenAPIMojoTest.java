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
package org.apache.geronimo.microprofile.openapi.mojo;

import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.geronimo.microprofile.openapi.impl.loader.ApiBindings;
import org.apache.geronimo.microprofile.openapi.impl.model.OpenAPIImpl;
import org.apache.maven.project.MavenProject;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.junit.Test;

public class OpenAPIMojoTest {
    @Test
    public void run() throws Exception {
        final OpenAPIMojo mojo = new OpenAPIMojo();
        mojo.output = new File("target/OpenAPIMojoTest_run_1.json");
        mojo.endpointClasses = singleton(HelloServiceImpl1.class.getName());
        mojo.project = new MavenProject();
        mojo.project.setVersion("1.2.3");
        mojo.execute();
        final OpenAPI openAPI = readOpenAPI(mojo.output);
        assertNotNull(openAPI.getInfo());
        assertEquals("1.2.3", openAPI.getInfo().getVersion());
        final Operation get = openAPI.getPaths().get("/sayHello/{a}").getGET();
        assertNotNull(get);
        assertEquals(1, get.getParameters().size());
        assertEquals("a", get.getParameters().iterator().next().getName());
        assertEquals(Schema.SchemaType.STRING, get.getResponses().get("200").getContent().get("text/plain").getSchema().getType());
    }

    private OpenAPI readOpenAPI(final File output) throws Exception {
        try (final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().setProperty("johnzon.interfaceImplementationMapping", ApiBindings.get()));
             final InputStream stream = new FileInputStream(output)) {
            return jsonb.fromJson(stream, OpenAPIImpl.class);
        }
    }

    @Path("/sayHello")
    public interface HelloService {
        @GET
        @Path("/{a}")
        @Produces(MediaType.TEXT_PLAIN)
        String hi(@PathParam("a") String a);

    }

    public static class HelloServiceImpl1 implements HelloService {

        public String hi(String a) {
            return "";
        }

    }
}
