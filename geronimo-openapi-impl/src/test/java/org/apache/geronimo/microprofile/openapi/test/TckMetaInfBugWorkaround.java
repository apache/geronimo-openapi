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
package org.apache.geronimo.microprofile.openapi.test;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.io.File;
import java.net.MalformedURLException;

import org.apache.catalina.Context;
import org.apache.catalina.loader.ParallelWebappClassLoader;
import org.apache.geronimo.config.configsource.PropertyFileConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.ConfigSourceProvider;

// tck puts config in META-INF of wars and not of jars or WEB-INF so it can't work OOTB
public class TckMetaInfBugWorkaround implements ConfigSourceProvider {
    @Override
    public Iterable<ConfigSource> getConfigSources(final ClassLoader classLoader) {
        final ConfigSource source = create(classLoader);
        return source == null ? emptyList() : singletonList(source);
    }

    private ConfigSource create(final ClassLoader classLoader) {
        if (classLoader instanceof ParallelWebappClassLoader) {
            final Context context = ParallelWebappClassLoader.class.cast(classLoader)
                    .getResources()
                    .getContext();
            final File config = new File(new File(context.getCatalinaBase(), "webapps/" + context.getDocBase()), "META-INF/microprofile-config.properties");
            try {
                return config.exists() ? new PropertyFileConfigSource(config.toURI().toURL()) : null;
            } catch (final MalformedURLException e) {
                throw new IllegalStateException(e);
            }
        }
        return null;
    }
}
