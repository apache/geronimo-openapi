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
package org.apache.geronimo.microprofile.openapi.impl.processor.reflect;

import org.apache.geronimo.microprofile.openapi.impl.processor.AnnotatedTypeElement;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public class ClassElement implements AnnotatedTypeElement {
    private final Class<?> delegate;
    private Annotation[] annotations;

    public ClassElement(final Class<?> delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T extends Annotation> T getAnnotation(final Class<T> annotationClass) {
        return ofNullable(delegate.getAnnotation(annotationClass))
                .orElseGet(() -> findInInterfaces(annotationClass));
    }

    @Override
    public Annotation[] getAnnotations() {
        return annotations == null ? annotations = gatherAnnotations() : annotations;
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return getAnnotations();
    }

    @Override
    public Type getType() {
        return delegate;
    }

    private Annotation[] gatherAnnotations() {
        final Collection<Annotation> annotations = new LinkedList<>(asList(delegate.getAnnotations()));
        annotations.addAll(findInterfaces(delegate)
                .flatMap(i -> Stream.of(i.getAnnotations()).filter(it -> !delegate.isAnnotationPresent(it.annotationType())))
                .distinct()
                .collect(toList()));
        return annotations.toArray(new Annotation[0]);
    }

    private <T extends Annotation> T findInInterfaces(final Class<T> annotationClass) {
        return findInterfaces(delegate)
                .filter(it -> it.isAnnotationPresent(annotationClass))
                .findFirst()
                .map(it -> it.getAnnotation(annotationClass))
                .orElse(null);
    }

    private Stream<Class<?>> findInterfaces(final Class<?> delegate) {
        return Stream.of(delegate.getInterfaces());
    }
}
