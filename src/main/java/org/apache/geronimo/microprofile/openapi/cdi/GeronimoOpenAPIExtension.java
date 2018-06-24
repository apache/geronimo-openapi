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
package org.apache.geronimo.microprofile.openapi.cdi;

import static java.util.Optional.ofNullable;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessBean;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

import org.apache.geronimo.microprofile.openapi.config.GeronimoOpenAPIConfig;
import org.apache.geronimo.microprofile.openapi.impl.model.OpenAPIImpl;
import org.apache.geronimo.microprofile.openapi.impl.processor.AnnotationProcessor;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.OASModelReader;
import org.eclipse.microprofile.openapi.models.OpenAPI;

public class GeronimoOpenAPIExtension implements Extension {

    private final AnnotationProcessor processor = new AnnotationProcessor();

    private final Collection<Bean<?>> endpoints = new ArrayList<>();

    private final Map<Application, OpenAPI> openapis = new HashMap<>();

    public <T> void findEndpointsAndApplication(@Observes final ProcessBean<T> event) {
        if (event.getAnnotated().isAnnotationPresent(Path.class)) {
            endpoints.add(event.getBean());
        }
    }

    public OpenAPI getOrCreateOpenAPI(final Application application) {
        if (!application.getSingletons().isEmpty() || !application.getClasses().isEmpty()) {
            return openapis.computeIfAbsent(application,
                    app -> createOpenApi(application.getClass(), Stream.concat(endpoints.stream().map(Bean::getBeanClass),
                            Stream.concat(app.getClasses().stream(), app.getSingletons().stream().map(Object::getClass)))));
        }
        return openapis.computeIfAbsent(application,
                app -> createOpenApi(application.getClass(), endpoints.stream().map(Bean::getBeanClass)));
    }

    private OpenAPI createOpenApi(final Class<?> application, final Stream<Class<?>> beans) {
        final CDI<Object> current = CDI.current();
        final GeronimoOpenAPIConfig config = GeronimoOpenAPIConfig.create();
        final OpenAPI api = ofNullable(config.read("mp.openapi.model.reader", null)).map(value -> newInstance(current, value))
                .map(it -> OASModelReader.class.cast(it).buildModel()).orElseGet(() -> {
                    final BeanManager beanManager = current.getBeanManager();
                    final OpenAPI impl = loadDefaultApi();
                    processor.processApplication(impl, new ElementImpl(beanManager.createAnnotatedType(application)));
                    beans.map(beanManager::createAnnotatedType).forEach(at -> processor.processClass(impl, new ElementImpl(at),
                            at.getMethods().stream().map(ElementImpl::new)));
                    return impl;
                });

        return ofNullable(config.read("mp.openapi.filter", null))
                .map(it -> newInstance(current, it))
                .map(i -> {
                    OASFilter.class.cast(i).filterOpenAPI(api);
                    return api;
                })
                .orElse(api);
    }

    private OpenAPI loadDefaultApi() {
        // todo: yaml handling + json mapping correctly (interface to impl)
        return Stream.of("", "/")
              .map(prefix -> prefix + "openapi.json")
              .map(it -> Thread.currentThread().getContextClassLoader().getResourceAsStream(it))
              .filter(Objects::nonNull)
              .findFirst()
              .map(r -> {
                  try (final Jsonb jsonb = JsonbBuilder.create()) {
                      return jsonb.fromJson(r, OpenAPIImpl.class);
                  } catch (final Exception e) {
                      throw new IllegalStateException(e);
                  }
              })
              .orElseGet(OpenAPIImpl::new);
    }

    private Object newInstance(final CDI<Object> current, final String value) {
        try {
            final Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(value.trim());
            try {
                return current.select(clazz);
            } catch (final RuntimeException e) {
                try {
                    return clazz.getConstructor().newInstance();
                } catch (final Exception e1) {
                    throw e;
                }
            }
        } catch (final ClassNotFoundException e) {
            throw new IllegalArgumentException("Can't load " + value, e);
        }
    }

    private static class ElementImpl implements AnnotatedElement {

        private final Annotated delegate;

        private ElementImpl(final Annotated annotated) {
            this.delegate = annotated;
        }

        @Override
        public <T extends Annotation> T getAnnotation(final Class<T> annotationClass) {
            return delegate.getAnnotation(annotationClass);
        }

        @Override
        public Annotation[] getAnnotations() {
            return delegate.getAnnotations().toArray(new Annotation[0]);
        }

        @Override
        public <T extends Annotation> T[] getAnnotationsByType(final Class<T> annotationClass) {
            return delegate.getAnnotations(annotationClass).toArray((T[]) Array.newInstance(annotationClass, 0));
        }

        @Override
        public Annotation[] getDeclaredAnnotations() {
            return getAnnotations();
        }
    }
}
