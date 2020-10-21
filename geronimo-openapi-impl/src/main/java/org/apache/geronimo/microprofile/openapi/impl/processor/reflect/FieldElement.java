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
import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class FieldElement implements AnnotatedTypeElement {
    private final Field delegate;
    private Annotation[] annotations;

    public FieldElement(final Field delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T extends Annotation> T getAnnotation(final Class<T> annotationClass) {
        return delegate.getAnnotation(annotationClass);
    }

    @Override
    public Annotation[] getAnnotations() {
        return delegate.getAnnotations();
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        return delegate.getDeclaredAnnotations();
    }

    @Override
    public Type getType() {
        return delegate.getGenericType();
    }
}
