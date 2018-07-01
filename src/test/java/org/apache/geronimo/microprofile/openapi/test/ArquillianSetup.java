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

import static java.lang.String.format;

import java.lang.reflect.Field;

import org.apache.meecrowave.Meecrowave;
import org.apache.meecrowave.arquillian.MeecrowaveContainer;
import org.jboss.arquillian.container.spi.event.container.AfterStart;
import org.jboss.arquillian.container.test.impl.client.protocol.local.LocalProtocol;
import org.jboss.arquillian.container.test.spi.client.deployment.DeploymentPackager;
import org.jboss.arquillian.container.test.spi.client.protocol.Protocol;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public class ArquillianSetup implements LoadableExtension {

    @Override
    public void register(final ExtensionBuilder extensionBuilder) {
        extensionBuilder.observer(Setup.class).override(Protocol.class, LocalProtocol.class, CdiProtocol.class);
    }

    public static class Setup {

        public void setTestUrl(@Observes final AfterStart afterStart) throws Exception {
            final Field container = MeecrowaveContainer.class.getDeclaredField("container");
            container.setAccessible(true);
            final int port = Meecrowave.class.cast(container.get(afterStart.getDeployableContainer())).getConfiguration()
                    .getHttpPort();
            System.setProperty("test.url", format("http://localhost:%d", port));
        }
    }

    public static class CdiProtocol extends LocalProtocol {

        @Override
        public DeploymentPackager getPackager() {
            return (deployment, collection) -> ShrinkWrap.create(WebArchive.class, "ROOT.war")
                    .merge(deployment.getApplicationArchive()).addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        }
    }

}
