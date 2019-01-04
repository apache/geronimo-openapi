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
package org.apache.geronimo.microprofile.openapi.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URL;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.annotations.Test;

public class YamlAnswerTest extends Arquillian {
    @Deployment(testable = false)
    public static Archive<?> war() {
        return ShrinkWrap.create(WebArchive.class, YamlAnswerTest.class.getSimpleName() + ".war")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @ArquillianResource
    private URL base;

    @Test
    public void getWildcard() {
        assertEquals("---\nopenapi: \"3.0.1\"\n", api("*/*"));
    }

    @Test
    public void getMissing() { // some MP server will match json even if default is set to yaml cause */*+json usage
        assertTrue(api("foo/bar").contains("\"3.0.1\""));
    }

    @Test
    public void getTextHtml() {
        assertTrue(api("text/html").contains("\"3.0.1\""));
    }

    @Test
    public void getYaml() {
        assertEquals("---\nopenapi: \"3.0.1\"\n", api("text/vnd.yaml"));
    }

    @Test
    public void getJson() {
        assertEquals("{\"openapi\":\"3.0.1\"}", api("application/json"));
    }

    private String api(final String type) {
        final Client client = ClientBuilder.newClient();
        try {
            return client.target(base.toExternalForm())
                    .path("openapi")
                    .request(type)
                    .get(String.class);
        } finally {
            client.close();
        }
    }
}
