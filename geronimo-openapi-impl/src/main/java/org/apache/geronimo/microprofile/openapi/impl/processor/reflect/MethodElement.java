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

import org.apache.geronimo.microprofile.openapi.impl.processor.AnnotatedMethodElement;
import org.apache.geronimo.microprofile.openapi.impl.processor.AnnotatedTypeElement;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class MethodElement implements AnnotatedMethodElement {
    private static final Method[] NO_METHOD = new Method[0];

    private final Method[] delegates;
    private Annotation[] annotations;

    public MethodElement(final Method method) {
        final Collection<Method> methods = new LinkedList<>();
        methods.add(method);
        Stream.of(method.getDeclaringClass().getInterfaces())
              .map(it -> {
                  try {
                      return it.getMethod(method.getName(), method.getParameterTypes());
                  } catch (final NoSuchMethodException e) {
                      return null;
                  }
              })
              .filter(Objects::nonNull).forEach(methods::add);
        this.delegates = methods.toArray(NO_METHOD);
    }

    @Override
    public String getName() {
        return delegates[0].getName();
    }

    @Override
    public Type getReturnType() {
        return delegates[0].getGenericReturnType();
    }

    @Override
    public Class<?> getDeclaringClass() {
        return delegates[0].getDeclaringClass();
    }

    @Override
    public AnnotatedTypeElement[] getParameters() {
        final java.lang.reflect.Parameter[] parameters = delegates[0].getParameters();
        return IntStream.range(0, parameters.length)
                    .mapToObj(p -> new AnnotatedTypeElement() {
                        private Annotation[] annotations;

                        @Override
                        public Type getType() {
                            return parameters[p].getParameterizedType();
                        }

                        @Override // todo:
                        public <T extends Annotation> T getAnnotation(final Class<T> annotationClass) {
                            return Stream.of(delegates)
                                    .map(m -> m.getParameters()[p])
                                    .filter(it -> it.isAnnotationPresent(annotationClass))
                                    .map(it -> it.getAnnotation(annotationClass))
                                    .findFirst()
                                    .orElse(null);
                        }

                        @Override
                        public Annotation[] getAnnotations() {
                            return annotations == null ? annotations = mergeAnnotations(delegates) : annotations;
                        }

                        @Override
                        public Annotation[] getDeclaredAnnotations() {
                            return getAnnotations();
                        }
                    }).toArray(AnnotatedTypeElement[]::new);
    }

    @Override
    public <T extends Annotation> T getAnnotation(final Class<T> annotationClass) {
        return Stream.of(delegates)
                     .filter(d -> d.isAnnotationPresent(annotationClass))
                     .map(d -> d.getAnnotation(annotationClass))
                     .findFirst().orElse(null);
    }

    @Override
    public Annotation[] getAnnotations() {
        return annotations == null ? annotations = mergeAnnotations(delegates) : annotations;
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return getAnnotations();
    }


    private static Annotation[] mergeAnnotations(final AnnotatedElement... element) {
        return Stream.of(element)
                .flatMap(i -> Stream.of(i.getAnnotations()))
                .collect(toMap(Annotation::annotationType, identity(), (a, b) -> a))
                .values()
                .toArray(new Annotation[0]);
    }
}
