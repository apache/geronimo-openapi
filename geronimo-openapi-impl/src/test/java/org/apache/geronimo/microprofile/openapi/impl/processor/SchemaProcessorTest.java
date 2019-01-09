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
import static org.testng.Assert.assertEquals;

import javax.json.bind.annotation.JsonbProperty;

import org.apache.geronimo.microprofile.openapi.impl.model.ComponentsImpl;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.testng.annotations.Test;

public class SchemaProcessorTest {
    @Test
    public void mapImplicit() {
        final Schema schema = new SchemaProcessor().mapSchemaFromClass(new ComponentsImpl(), Data.class);
        assertEquals(1, schema.getProperties().size());
        assertEquals(Schema.SchemaType.STRING, schema.getProperties().get("name").getType());
    }

    @Test
    public void mapJsonb() {
        final Schema schema = new SchemaProcessor().mapSchemaFromClass(new ComponentsImpl(), JsonbData.class);
        assertEquals(1, schema.getProperties().size());
        assertEquals(Schema.SchemaType.STRING, schema.getProperties().get("foo").getType());
    }

    @Test
    public void mapEnum() {
        final Schema schema = new SchemaProcessor().mapSchemaFromClass(new ComponentsImpl(), DataWithEnum.class);
        assertEquals(1, schema.getProperties().size());
        final Schema anEnum = schema.getProperties().get("anEnum");
        assertEquals(Schema.SchemaType.STRING, anEnum.getType());
        assertEquals(asList(AnEnum.A, AnEnum.B), anEnum.getEnumeration());
    }

    public enum AnEnum {
        A, B;
    }

    public static class DataWithEnum {
        private AnEnum anEnum;
    }

    public static class Data {
        private String name;
    }

    public static class JsonbData {
        @JsonbProperty("foo")
        private String name;
    }
}
