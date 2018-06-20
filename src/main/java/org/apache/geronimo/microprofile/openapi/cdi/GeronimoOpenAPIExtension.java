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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessBean;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

import org.apache.geronimo.microprofile.openapi.impl.model.OpenAPIImpl;
import org.apache.geronimo.microprofile.openapi.impl.processor.AnnotationProcessor;
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
            return openapis.computeIfAbsent(application, app -> createOpenApi(application.getClass(),
                    Stream.concat(app.getClasses().stream(), app.getSingletons().stream().map(Object::getClass))));
        }
        return openapis.computeIfAbsent(application,
                app -> createOpenApi(application.getClass(), endpoints.stream().map(Bean::getBeanClass)));
    }

    private OpenAPI createOpenApi(final Class<?> application, final Stream<Class<?>> beans) {
        final BeanManager beanManager = CDI.current().getBeanManager();
        final OpenAPIImpl api = new OpenAPIImpl();
        processor.processApplication(api, beanManager.createAnnotatedType(application));
        beans.map(beanManager::createAnnotatedType).forEach(at -> processor.processClass(api, at));
        return api;
    }
}
