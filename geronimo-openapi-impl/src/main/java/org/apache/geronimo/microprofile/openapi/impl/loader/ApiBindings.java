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
package org.apache.geronimo.microprofile.openapi.impl.loader;

import java.util.HashMap;
import java.util.Map;

import org.apache.geronimo.microprofile.openapi.impl.model.APIResponseImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.APIResponsesImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.CallbackImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ComponentsImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ContactImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ContentImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.DiscriminatorImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.EncodingImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ExampleImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ExtensibleImpl;
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
import org.apache.geronimo.microprofile.openapi.impl.model.ReferenceImpl;
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
import org.eclipse.microprofile.openapi.models.Extensible;
import org.eclipse.microprofile.openapi.models.ExternalDocumentation;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.PathItem;
import org.eclipse.microprofile.openapi.models.Paths;
import org.eclipse.microprofile.openapi.models.Reference;
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

public class ApiBindings {
    private ApiBindings() {
        // no-op
    }

    public static Map<Class<?>, Class<?>> get() {
        final Map<Class<?>, Class<?>> mapping = new HashMap<>(33);
        mapping.put(APIResponse.class, APIResponseImpl.class);
        mapping.put(APIResponses.class, APIResponsesImpl.class);
        mapping.put(Callback.class, CallbackImpl.class);
        mapping.put(Components.class, ComponentsImpl.class);
        mapping.put(Contact.class, ContactImpl.class);
        mapping.put(Content.class, ContentImpl.class);
        mapping.put(Discriminator.class, DiscriminatorImpl.class);
        mapping.put(Encoding.class, EncodingImpl.class);
        mapping.put(Example.class, ExampleImpl.class);
        mapping.put(Extensible.class, ExtensibleImpl.class);
        mapping.put(ExternalDocumentation.class, ExternalDocumentationImpl.class);
        mapping.put(Header.class, HeaderImpl.class);
        mapping.put(Info.class, InfoImpl.class);
        mapping.put(License.class, LicenseImpl.class);
        mapping.put(Link.class, LinkImpl.class);
        mapping.put(MediaType.class, MediaTypeImpl.class);
        mapping.put(OAuthFlow.class, OAuthFlowImpl.class);
        mapping.put(OAuthFlows.class, OAuthFlowsImpl.class);
        mapping.put(OpenAPI.class, OpenAPIImpl.class);
        mapping.put(Operation.class, OperationImpl.class);
        mapping.put(Parameter.class, ParameterImpl.class);
        mapping.put(PathItem.class, PathItemImpl.class);
        mapping.put(Paths.class, PathsImpl.class);
        mapping.put(Reference.class, ReferenceImpl.class);
        mapping.put(RequestBody.class, RequestBodyImpl.class);
        mapping.put(Schema.class, SchemaImpl.class);
        mapping.put(Scopes.class, ScopesImpl.class);
        mapping.put(SecurityRequirement.class, SecurityRequirementImpl.class);
        mapping.put(SecurityScheme.class, SecuritySchemeImpl.class);
        mapping.put(Server.class, ServerImpl.class);
        mapping.put(ServerVariable.class, ServerVariableImpl.class);
        mapping.put(ServerVariables.class, ServerVariablesImpl.class);
        mapping.put(Tag.class, TagImpl.class);
        mapping.put(XML.class, XMLImpl.class);
        return mapping;
    }
}
