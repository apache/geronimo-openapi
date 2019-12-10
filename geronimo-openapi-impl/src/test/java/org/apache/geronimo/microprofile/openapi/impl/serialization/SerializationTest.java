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
package org.apache.geronimo.microprofile.openapi.impl.serialization;

import static org.junit.Assert.assertEquals;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyOrderStrategy;

import org.apache.geronimo.microprofile.openapi.impl.loader.yaml.Yaml;
import org.apache.geronimo.microprofile.openapi.impl.model.APIResponseImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.APIResponsesImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.CallbackImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.InfoImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.OpenAPIImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.OperationImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.PathItemImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.PathsImpl;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.PathItem;
import org.eclipse.microprofile.openapi.models.responses.APIResponses;
import org.testng.annotations.Test;

public class SerializationTest {
    @Test
    public void defaultResponse() throws Exception {
        final APIResponses responses = new APIResponsesImpl()
                .defaultValue(new APIResponseImpl().description("test"))
                .addAPIResponse("200", new APIResponseImpl().description("ok"));
        try (final Jsonb jsonb = JsonbBuilder.create()) {
            assertEquals("{\"default\":{\"description\":\"test\"},\"200\":{\"description\":\"ok\"}}", jsonb.toJson(responses));
        }
        assertEquals("---\ndefault:\n  description: \"test\"\n\"200\":\n  description: \"ok\"\n", Yaml.getObjectMapper().writeValueAsString(responses));
    }

    @Test
    public void serialize() throws Exception {
        final PathItem item = new PathItemImpl();
        item.setGET(new OperationImpl().addCallback("onData", new CallbackImpl()
                .addPathItem("{$request.query.callbackUrl}/data", new PathItemImpl())));

        final OpenAPI api = new OpenAPIImpl()
                .info(new InfoImpl().version("3.0.0"))
                .paths(new PathsImpl().addPathItem("/foo", item));

        // identify some jsonb issues with serializers early (exception)
        try (final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig()
                .withPropertyOrderStrategy(PropertyOrderStrategy.LEXICOGRAPHICAL))) {
            jsonb.toJson(api);
        }
    }
}
