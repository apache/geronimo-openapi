/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"; } you may not use this file except in compliance with
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
package org.apache.geronimo.microprofile.openapi.impl.resolver;

import static java.util.Objects.requireNonNull;

import org.apache.geronimo.microprofile.openapi.impl.model.APIResponseImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.APIResponsesImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.CallbackImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ComponentsImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ContactImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ContentImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.DiscriminatorImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.EncodingImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ExampleImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ExternalDocumentationImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.HeaderImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.InfoImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.LicenseImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.LinkImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.MediaTypeImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.OAuthFlowImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.OAuthFlowsImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.OpenAPIImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.OperationImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ParameterImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.PathItemImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.PathsImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.RequestBodyImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.SchemaImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ScopesImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.SecurityRequirementImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.SecuritySchemeImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ServerImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ServerVariableImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ServerVariablesImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.TagImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.XMLImpl;
import org.eclipse.microprofile.openapi.models.Components;
import org.eclipse.microprofile.openapi.models.Constructible;
import org.eclipse.microprofile.openapi.models.ExternalDocumentation;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.PathItem;
import org.eclipse.microprofile.openapi.models.Paths;
import org.eclipse.microprofile.openapi.models.callbacks.Callback;
import org.eclipse.microprofile.openapi.models.examples.Example;
import org.eclipse.microprofile.openapi.models.headers.Header;
import org.eclipse.microprofile.openapi.models.info.Contact;
import org.eclipse.microprofile.openapi.models.info.Info;
import org.eclipse.microprofile.openapi.models.info.License;
import org.eclipse.microprofile.openapi.models.links.Link;
import org.eclipse.microprofile.openapi.models.media.Content;
import org.eclipse.microprofile.openapi.models.media.Discriminator;
import org.eclipse.microprofile.openapi.models.media.Encoding;
import org.eclipse.microprofile.openapi.models.media.MediaType;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.media.XML;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.eclipse.microprofile.openapi.models.parameters.RequestBody;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;
import org.eclipse.microprofile.openapi.models.responses.APIResponses;
import org.eclipse.microprofile.openapi.models.security.OAuthFlow;
import org.eclipse.microprofile.openapi.models.security.OAuthFlows;
import org.eclipse.microprofile.openapi.models.security.Scopes;
import org.eclipse.microprofile.openapi.models.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.models.security.SecurityScheme;
import org.eclipse.microprofile.openapi.models.servers.Server;
import org.eclipse.microprofile.openapi.models.servers.ServerVariable;
import org.eclipse.microprofile.openapi.models.servers.ServerVariables;
import org.eclipse.microprofile.openapi.models.tags.Tag;
import org.eclipse.microprofile.openapi.spi.OASFactoryResolver;

public class OASFactoryResolverImpl extends OASFactoryResolver {

    @Override
    public <T extends Constructible> T createObject(final Class<T> clazz) {
        requireNonNull(clazz);

        if (APIResponse.class == clazz) {
            return clazz.cast(new APIResponseImpl());
        }
        if (APIResponses.class == clazz) {
            return clazz.cast(new APIResponsesImpl());
        }
        if (Callback.class == clazz) {
            return clazz.cast(new CallbackImpl());
        }
        if (Components.class == clazz) {
            return clazz.cast(new ComponentsImpl());
        }
        if (Contact.class == clazz) {
            return clazz.cast(new ContactImpl());
        }
        if (Content.class == clazz) {
            return clazz.cast(new ContentImpl());
        }
        if (Discriminator.class == clazz) {
            return clazz.cast(new DiscriminatorImpl());
        }
        if (Encoding.class == clazz) {
            return clazz.cast(new EncodingImpl());
        }
        if (Example.class == clazz) {
            return clazz.cast(new ExampleImpl());
        }
        if (ExternalDocumentation.class == clazz) {
            return clazz.cast(new ExternalDocumentationImpl());
        }
        if (Header.class == clazz) {
            return clazz.cast(new HeaderImpl());
        }
        if (Info.class == clazz) {
            return clazz.cast(new InfoImpl());
        }
        if (License.class == clazz) {
            return clazz.cast(new LicenseImpl());
        }
        if (Link.class == clazz) {
            return clazz.cast(new LinkImpl());
        }
        if (MediaType.class == clazz) {
            return clazz.cast(new MediaTypeImpl());
        }
        if (OAuthFlow.class == clazz) {
            return clazz.cast(new OAuthFlowImpl());
        }
        if (OAuthFlows.class == clazz) {
            return clazz.cast(new OAuthFlowsImpl());
        }
        if (OpenAPI.class == clazz) {
            return clazz.cast(new OpenAPIImpl());
        }
        if (Operation.class == clazz) {
            return clazz.cast(new OperationImpl());
        }
        if (Parameter.class == clazz) {
            return clazz.cast(new ParameterImpl());
        }
        if (PathItem.class == clazz) {
            return clazz.cast(new PathItemImpl());
        }
        if (Paths.class == clazz) {
            return clazz.cast(new PathsImpl());
        }
        if (RequestBody.class == clazz) {
            return clazz.cast(new RequestBodyImpl());
        }
        if (Schema.class == clazz) {
            return clazz.cast(new SchemaImpl());
        }
        if (Scopes.class == clazz) {
            return clazz.cast(new ScopesImpl());
        }
        if (SecurityRequirement.class == clazz) {
            return clazz.cast(new SecurityRequirementImpl());
        }
        if (SecurityScheme.class == clazz) {
            return clazz.cast(new SecuritySchemeImpl());
        }
        if (Server.class == clazz) {
            return clazz.cast(new ServerImpl());
        }
        if (ServerVariable.class == clazz) {
            return clazz.cast(new ServerVariableImpl());
        }
        if (ServerVariables.class == clazz) {
            return clazz.cast(new ServerVariablesImpl());
        }
        if (Tag.class == clazz) {
            return clazz.cast(new TagImpl());
        }
        if (XML.class == clazz) {
            return clazz.cast(new XMLImpl());
        }
        throw new IllegalArgumentException("Unsupported: " + clazz);
    }
}
