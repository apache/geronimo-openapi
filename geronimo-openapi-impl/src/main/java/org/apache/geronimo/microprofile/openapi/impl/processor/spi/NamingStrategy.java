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
package org.apache.geronimo.microprofile.openapi.impl.processor.spi;

import org.apache.geronimo.microprofile.openapi.impl.processor.AnnotatedMethodElement;

public interface NamingStrategy {

    String name(Context ctx);

    class Context {
        private final AnnotatedMethodElement method;
        private final String httpVerb;
        private final String path;

        // @Internal
        public Context(final AnnotatedMethodElement method, final String httpVerb, final String path) {
            this.method = method;
            this.httpVerb = httpVerb;
            this.path = path;
        }

        public AnnotatedMethodElement getMethod() {
            return method;
        }

        public String getHttpVerb() {
            return httpVerb;
        }

        public String getPath() {
            return path;
        }
    }

    class Default implements NamingStrategy {
        @Override
        public String name(final Context ctx) {
            return ctx.method.getName();
        }
    }

    class SimpleQualified implements NamingStrategy {
        @Override
        public String name(final Context ctx) {
            return ctx.method.getDeclaringClass().getSimpleName() + '.' + ctx.method.getName();
        }
    }

    class SimpleQualifiedCamelCase implements NamingStrategy {
        @Override
        public String name(final Context ctx) {
            final String method = ctx.method.getName();
            return ctx.method.getDeclaringClass().getSimpleName() + Character.toUpperCase(method.charAt(0)) + method.substring(1);
        }
    }

    class Qualified implements NamingStrategy {
        @Override
        public String name(final Context ctx) {
            return ctx.method.getDeclaringClass().getName() + '.' + ctx.method.getName();
        }
    }

    class Http implements NamingStrategy {
        @Override
        public String name(final Context ctx) {
            return ctx.httpVerb + ':' + ctx.path;
        }
    }
}
