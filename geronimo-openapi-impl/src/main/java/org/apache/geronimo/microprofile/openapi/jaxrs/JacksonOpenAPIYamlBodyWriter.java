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

import static javax.ws.rs.RuntimeType.SERVER;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Vetoed;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.apache.geronimo.microprofile.openapi.impl.loader.yaml.Yaml;

@Provider
@Dependent
@ConstrainedTo(SERVER)
@Produces({
        "text/vnd.yaml", "text/yaml", "text/x-yaml",
        "application/vnd.yaml", "application/yaml", "application/x-yaml"})
public class JacksonOpenAPIYamlBodyWriter<T> extends BaseOpenAPIYamlBodyWriter<T> {
    @Override
    public void writeTo(final T entity, final Class<?> type, final Type genericType,
                        final Annotation[] annotations, final MediaType mediaType,
                        final MultivaluedMap<String, Object> httpHeaders, final OutputStream entityStream)
            throws IOException, WebApplicationException {
        Mapper.get().writeValue(entityStream, entity);
    }

    @Vetoed
    private static class Mapper {
        private static final AtomicReference<com.fasterxml.jackson.databind.ObjectMapper> REF = new AtomicReference<>();

        private Mapper() {
            // no-op
        }

        public static com.fasterxml.jackson.databind.ObjectMapper get() {
            // for now we only support jackson, ok while this is actually only tck code ;)
            com.fasterxml.jackson.databind.ObjectMapper mapper = REF.get();
            if (mapper == null) {
                mapper = Yaml.getObjectMapper();
                if (!REF.compareAndSet(null, mapper)) {
                    mapper = REF.get();
                }
            }
            return mapper;
        }
    }
}
