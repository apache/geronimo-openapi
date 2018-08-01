package org.apache.geronimo.microprofile.openapi.mojo;

import static java.util.Optional.ofNullable;
import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_CLASSES;
import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE_PLUS_RUNTIME;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Stream;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

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
    private boolean skip;

    @Parameter(property = "geronimo-openapi.prettify", defaultValue = "true")
    private boolean prettify;

    @Parameter(property = "geronimo-openapi.output", defaultValue = "${project.build.outputDirectory}/META-INF/classes/openapi.json")
    private File output;

    @Parameter
    private String application;

    @Parameter
    private Collection<String> endpointClasses;

    @Parameter
    private Map<String, String> configuration;

    @Parameter(defaultValue = "${project.build.outputDirectory}")
    private File classes;

    @Parameter(defaultValue = "${project}", readonly = true)
    private MavenProject project;

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
                    Stream.concat(Stream.of(classes), project.getArtifacts().stream().map(Artifact::getFile))
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

    private static class MethodElement implements AnnotatedMethodElement {
        private final Method delegate;

        private MethodElement(final Method method) {
            this.delegate = method;
        }

        @Override
        public String getName() {
            return delegate.getName();
        }

        @Override
        public Type getReturnType() {
            return delegate.getGenericReturnType();
        }

        @Override
        public Class<?> getDeclaringClass() {
            return delegate.getDeclaringClass();
        }

        @Override
        public AnnotatedTypeElement[] getParameters() {
            return Stream.of(delegate.getParameters())
                    .map(p -> new AnnotatedTypeElement() {
                        @Override
                        public Type getType() {
                            return p.getParameterizedType();
                        }

                        @Override
                        public <T extends Annotation> T getAnnotation(final Class<T> annotationClass) {
                            return p.getAnnotation(annotationClass);
                        }

                        @Override
                        public Annotation[] getAnnotations() {
                            return p.getAnnotations();
                        }

                        @Override
                        public Annotation[] getDeclaredAnnotations() {
                            return p.getDeclaredAnnotations();
                        }
                    })
                    .toArray(AnnotatedTypeElement[]::new);
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
            return getAnnotations();
        }
    }

    private static class ClassElement implements AnnotatedTypeElement {
        private final Class<?> delegate;

        private ClassElement(final Class<?> delegate) {
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
            return delegate;
        }
    }
}
