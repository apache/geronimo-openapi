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
package org.apache.geronimo.microprofile.openapi.impl.loader;

import static java.util.Optional.ofNullable;

import java.io.InputStream;
import java.util.Objects;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.servlet.ServletContext;

import org.apache.geronimo.microprofile.openapi.impl.model.OpenAPIImpl;
import org.eclipse.microprofile.openapi.models.OpenAPI;

@ApplicationScoped
public class DefaultLoader {
    @Inject
    private ServletContext context;

    public OpenAPI loadDefaultApi() {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return Stream.of("", "/").map(prefix -> prefix + "META-INF/openapi.json")
                .map(it -> ofNullable(loader.getResourceAsStream(it)).orElseGet(() -> context.getResourceAsStream(it)))
                .filter(Objects::nonNull).findFirst().map(r -> {
                    try (final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig()
                            .setProperty("johnzon.interfaceImplementationMapping", ApiBindings.get()));
                         final InputStream stream = r) {
                        return jsonb.fromJson(stream, OpenAPIImpl.class);
                    } catch (final Exception e) {
                        throw new IllegalStateException(e);
                    }
                }).map(OpenAPI.class::cast)
                .orElseGet(() -> Stream.of("", "/").map(prefix -> prefix + "META-INF/openapi.")
                        .flatMap(p -> Stream.of(p + "yaml", p + "yml"))
                        .map(it -> ofNullable(loader.getResourceAsStream(it)).orElseGet(() -> context.getResourceAsStream(it)))
                        .filter(Objects::nonNull).findFirst().map(this::loadFromYaml).orElseGet(OpenAPIImpl::new));
    }

    private OpenAPI loadFromYaml(final InputStream inputStream) {
        return org.apache.geronimo.microprofile.openapi.impl.loader.yaml.Yaml.loadAPI(inputStream);
    }
}
