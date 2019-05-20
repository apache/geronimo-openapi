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

import static java.util.Optional.ofNullable;
import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_CLASSES;
import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE_PLUS_RUNTIME;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import org.apache.geronimo.microprofile.openapi.impl.model.InfoImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.OpenAPIImpl;
import org.apache.geronimo.microprofile.openapi.impl.processor.AnnotationProcessor;
import org.apache.geronimo.microprofile.openapi.impl.processor.reflect.ClassElement;
import org.apache.geronimo.microprofile.openapi.impl.processor.reflect.MethodElement;
import org.apache.geronimo.microprofile.openapi.impl.processor.spi.NamingStrategy;
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

    @Parameter(property = "geronimo-openapi.operationNamingStrategy", defaultValue = "org.apache.geronimo.microprofile.openapi.impl.processor.spi.NamingStrategy$Default")
    protected String operationNamingStrategy;

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
                        (value, def) -> ofNullable(configuration).orElseGet(Collections::emptyMap).getOrDefault(value, def),
                        loadNamingStrategy(), null);
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

    private NamingStrategy loadNamingStrategy() {
        return ofNullable(operationNamingStrategy)
                .map(String::trim)
                .filter(it -> !it.isEmpty())
                .map(it -> {
                    try {
                        return Thread.currentThread().getContextClassLoader().loadClass(it).getConstructor().newInstance();
                    } catch (final InstantiationException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException e) {
                        throw new IllegalArgumentException(e);
                    } catch (final InvocationTargetException ite) {
                        throw new IllegalArgumentException(ite.getTargetException());
                    }
                })
                .map(NamingStrategy.class::cast)
                .orElseGet(NamingStrategy.Default::new);
    }

    private Class<?> load(final String name) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(name.trim());
        } catch (final ClassNotFoundException e) {
            throw new IllegalArgumentException("'" + name + "' can't be loaded", e);
        }
    }

}
