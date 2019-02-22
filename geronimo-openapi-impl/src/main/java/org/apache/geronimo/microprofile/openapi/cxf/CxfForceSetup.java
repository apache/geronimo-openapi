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
package org.apache.geronimo.microprofile.openapi.cxf;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

import java.util.logging.Logger;

import javax.enterprise.inject.Vetoed;
import javax.enterprise.inject.spi.CDI;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.ext.JAXRSServerFactoryCustomizationExtension;
import org.apache.geronimo.microprofile.openapi.cdi.GeronimoOpenAPIExtension;
import org.apache.geronimo.microprofile.openapi.jaxrs.JacksonOpenAPIYamlBodyWriter;
import org.apache.geronimo.microprofile.openapi.jaxrs.OpenAPIFilter;

@Vetoed
public class CxfForceSetup implements JAXRSServerFactoryCustomizationExtension {
    @Override
    public void customize(final JAXRSServerFactoryBean bean) {
        if (bean.getProviders().stream().anyMatch(OpenAPIFilter.class::isInstance)) { // default app, nothing to do
            return;
        }
        final CDI<Object> current = CDI.current();
        bean.setProvider(current.select(OpenAPIFilter.class).get());
        try {
            if (current.select(GeronimoOpenAPIExtension.class).get().getDefaultMediaType().equals(APPLICATION_JSON_TYPE)) {
                return;
            }
            bean.setProvider(current.select(JacksonOpenAPIYamlBodyWriter.class).get());
        } catch (final NoClassDefFoundError | RuntimeException cne) {
            Logger.getLogger(CxfForceSetup.class.getName()).warning(cne.getMessage());
        }
    }
}
