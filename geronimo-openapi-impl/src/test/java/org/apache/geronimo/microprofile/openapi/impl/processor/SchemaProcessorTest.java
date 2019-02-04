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
import static java.util.stream.Collectors.toSet;
import static org.testng.Assert.assertEquals;

import java.util.List;
import java.util.stream.Stream;

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

    @Test
    public void cyclicRef() {
        final Schema schema = new SchemaProcessor().mapSchemaFromClass(new ComponentsImpl(), SomeClass.class);
        assertEquals(3, schema.getProperties().size());
        assertEquals(Schema.SchemaType.STRING, schema.getProperties().get("simple").getType());
        assertSomeClass(schema.getProperties().get("child"));
        final Schema children = schema.getProperties().get("children");
        assertEquals(Schema.SchemaType.ARRAY, children.getType());
        assertSomeClass(children.getItems());
    }

    private void assertSomeClass(final Schema schema) {
        assertEquals(Schema.SchemaType.OBJECT, schema.getType());
        assertEquals(Stream.of("simple", "child", "children").collect(toSet()), schema.getProperties().keySet());
    }

    public enum AnEnum {
        A, B
    }

    public static class SomeClass {
        private String simple;
        private SomeClass child;
        private List<SomeClass> children;
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
