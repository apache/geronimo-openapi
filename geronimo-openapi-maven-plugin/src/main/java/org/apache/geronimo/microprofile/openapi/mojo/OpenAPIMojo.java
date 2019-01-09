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
package org.apache.geronimo.microprofile.openapi.mojo;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_CLASSES;
import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE_PLUS_RUNTIME;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.apache.geronimo.microprofile.openapi.impl.model.InfoImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.OpenAPIImpl;
import org.apache.geronimo.microprofile.openapi.impl.processor.AnnotatedMethodElement;
import org.apache.geronimo.microprofile.openapi.impl.processor.AnnotatedTypeElement;
import org.apache.geronimo.microprofile.openapi.impl.processor.AnnotationProcessor;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "openapi.json", defaultPhase = PROCESS_CLASSES, requiresDependencyResolution = COMPILE_PLUS_RUNTIME, threadSafe = true)
public class OpenAPIMojo extends AbstractMojo {
    @Parameter(property = "geronimo-openapi.skip", defaultValue = "false")
    protected boolean skip;

    @Parameter(property = "geronimo-openapi.prettify", defaultValue = "true")
    protected boolean prettify;

    @Parameter(property = "geronimo-openapi.output", defaultValue = "${project.build.outputDirectory}/META-INF/classes/openapi.json")
    protected File output;

    @Parameter
    protected String application;

    @Parameter
    protected Collection<String> endpointClasses;

    @Parameter
    protected Map<String, String> configuration;

    @Parameter(defaultValue = "${project.build.outputDirectory}")
    protected File classes;

    @Parameter(defaultValue = "${project}", readonly = true)
    protected MavenProject project;

    @Parameter
    protected InfoImpl info;

    @Override
    public void execute() throws MojoExecutionException {
        if (skip) {
            getLog().warn("Execution is skipped");
            return;
        }

        final OpenAPIImpl api = new OpenAPIImpl();
        final Thread thread = Thread.currentThread();
        final ClassLoader pluginLoader = thread.getContextClassLoader();
        try {
            try (final URLClassLoader loader = new URLClassLoader(
                    Stream.concat(
                                Stream.of(classes),
                                ofNullable(project)
                                        .map(p -> p.getArtifacts().stream().map(Artifact::getFile)).orElseGet(Stream::empty))
                            .filter(Objects::nonNull)
                            .map(file -> {
                                try {
                                    return file.toURI().toURL();
                                } catch (final MalformedURLException e) {
                                    throw new IllegalStateException(e.getMessage());
                                }
                            })
                            .toArray(URL[]::new), pluginLoader) {

                {
                    thread.setContextClassLoader(this);
                }

                @Override
                public void close() throws IOException {
                    thread.setContextClassLoader(pluginLoader);
                    super.close();
                }
            }) {
                final AnnotationProcessor processor = new AnnotationProcessor(
                        (value, def) -> ofNullable(configuration).orElseGet(Collections::emptyMap).getOrDefault(value, def));
                if (application != null) {
                    processor.processApplication(api, new ClassElement(load(application)));
                    getLog().info("Processed application " + application);
                }
                if (endpointClasses != null) {
                    final String binding = application == null ? "" : processor.getApplicationBinding(load(application));
                    endpointClasses.stream().map(this::load)
                            .peek(c -> getLog().info("Processing class " + c.getName()))
                            .forEach(c -> processor.processClass(
                                binding, api, new ClassElement(c),
                                Stream.of(c.getMethods()).map(MethodElement::new)));
                } else {
                    getLog().warn("No <endpointClasses> registered, your OpenAPI will be empty.");
                }
            }
        } catch (final IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }

        // info are required
        if (info == null) {
            info = new InfoImpl();
        }
        if (info.getVersion() == null) {
            info.setVersion(project.getVersion());
        }
        if (info.getTitle() == null) {
            info.setTitle(project.getName());
        }
        if (info.getDescription() == null) {
            info.setDescription(project.getDescription());
        }
        api.info(info);

        output.getParentFile().mkdirs();
        try (final Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(prettify));
             final Writer writer = new FileWriter(output)) {
            jsonb.toJson(api, writer);
        } catch (final Exception e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        getLog().info("Wrote " + output);
    }

    private Class<?> load(final String name) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(name.trim());
        } catch (final ClassNotFoundException e) {
            throw new IllegalArgumentException("'" + name + "' can't be loaded", e);
        }
    }

    private static Annotation[] mergeAnnotations(final AnnotatedElement... element) {
        return Stream.of(element)
                     .flatMap(i -> Stream.of(i.getAnnotations()))
                     .collect(toMap(Annotation::annotationType, identity(), (a, b) -> a))
                     .values()
                     .toArray(new Annotation[0]);
    }

    private static class MethodElement implements AnnotatedMethodElement {
        private static final Method[] NO_METHOD = new Method[0];

        private final Method[] delegates;
        private Annotation[] annotations;

        private MethodElement(final Method method) {
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
    }

    private static class ClassElement implements AnnotatedTypeElement {
        private final Class<?> delegate;
        private Annotation[] annotations;

        private ClassElement(final Class<?> delegate) {
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
}
