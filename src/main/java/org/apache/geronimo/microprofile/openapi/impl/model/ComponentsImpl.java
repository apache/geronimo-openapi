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
package org.apache.geronimo.microprofile.openapi.impl.model;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.enterprise.inject.Vetoed;

import org.eclipse.microprofile.openapi.models.Components;
import org.eclipse.microprofile.openapi.models.Extensible;
import org.eclipse.microprofile.openapi.models.callbacks.Callback;
import org.eclipse.microprofile.openapi.models.examples.Example;
import org.eclipse.microprofile.openapi.models.headers.Header;
import org.eclipse.microprofile.openapi.models.links.Link;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.eclipse.microprofile.openapi.models.parameters.RequestBody;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;
import org.eclipse.microprofile.openapi.models.security.SecurityScheme;

@Vetoed
public class ComponentsImpl implements Components {

    private Extensible _extensible = new ExtensibleImpl();

    private Map<String, Callback> _callbacks;

    private Map<String, Example> _examples;

    private Map<String, Header> _headers;

    private Map<String, Link> _links;

    private Map<String, Parameter> _parameters;

    private Map<String, RequestBody> _requestBodies;

    private Map<String, APIResponse> _responses;

    private Map<String, Schema> _schemas;

    private Map<String, SecurityScheme> _securitySchemes;

    @Override
    public Map<String, Object> getExtensions() {
        return _extensible.getExtensions();
    }

    @Override
    public void setExtensions(final Map<String, Object> extensions) {
        _extensible.setExtensions(extensions);
    }

    @Override
    public void addExtension(final String name, final Object value) {
        _extensible.addExtension(name, value);
    }

    @Override
    public Map<String, Callback> getCallbacks() {
        return _callbacks;
    }

    @Override
    public void setCallbacks(final Map<String, Callback> _callbacks) {
        this._callbacks = _callbacks;
    }

    @Override
    public Components callbacks(final Map<String, Callback> _callbacks) {
        setCallbacks(_callbacks);
        return this;
    }

    @Override
    public Components addCallback(final String key, final Callback _callbacks) {
        (this._callbacks = this._callbacks == null ? new LinkedHashMap<>() : this._callbacks).put(key, _callbacks);
        return this;
    }

    @Override
    public Map<String, Example> getExamples() {
        return _examples;
    }

    @Override
    public void setExamples(final Map<String, Example> _examples) {
        this._examples = _examples;
    }

    @Override
    public Components examples(final Map<String, Example> _examples) {
        setExamples(_examples);
        return this;
    }

    @Override
    public Components addExample(final String key, final Example _examples) {
        (this._examples = this._examples == null ? new LinkedHashMap<>() : this._examples).put(key, _examples);
        return this;
    }

    @Override
    public Map<String, Header> getHeaders() {
        return _headers;
    }

    @Override
    public void setHeaders(final Map<String, Header> _headers) {
        this._headers = _headers;
    }

    @Override
    public Components headers(final Map<String, Header> _headers) {
        setHeaders(_headers);
        return this;
    }

    @Override
    public Components addHeader(final String key, final Header _headers) {
        (this._headers = this._headers == null ? new LinkedHashMap<>() : this._headers).put(key, _headers);
        return this;
    }

    @Override
    public Map<String, Link> getLinks() {
        return _links;
    }

    @Override
    public void setLinks(final Map<String, Link> _links) {
        this._links = _links;
    }

    @Override
    public Components links(final Map<String, Link> _links) {
        setLinks(_links);
        return this;
    }

    @Override
    public Components addLink(final String key, final Link _links) {
        (this._links = this._links == null ? new LinkedHashMap<>() : this._links).put(key, _links);
        return this;
    }

    @Override
    public Map<String, Parameter> getParameters() {
        return _parameters;
    }

    @Override
    public void setParameters(final Map<String, Parameter> _parameters) {
        this._parameters = _parameters;
    }

    @Override
    public Components parameters(final Map<String, Parameter> _parameters) {
        setParameters(_parameters);
        return this;
    }

    @Override
    public Components addParameter(final String key, final Parameter _parameters) {
        (this._parameters = this._parameters == null ? new LinkedHashMap<>() : this._parameters).put(key, _parameters);
        return this;
    }

    @Override
    public Map<String, RequestBody> getRequestBodies() {
        return _requestBodies;
    }

    @Override
    public void setRequestBodies(final Map<String, RequestBody> _requestBodies) {
        this._requestBodies = _requestBodies;
    }

    @Override
    public Components requestBodies(final Map<String, RequestBody> _requestBodies) {
        setRequestBodies(_requestBodies);
        return this;
    }

    @Override
    public Components addRequestBody(final String key, final RequestBody _requestBodies) {
        (this._requestBodies = this._requestBodies == null ? new LinkedHashMap<>() : this._requestBodies).put(key,
                _requestBodies);
        return this;
    }

    @Override
    public Map<String, APIResponse> getResponses() {
        return _responses;
    }

    @Override
    public void setResponses(final Map<String, APIResponse> _responses) {
        this._responses = _responses;
    }

    @Override
    public Components responses(final Map<String, APIResponse> _responses) {
        setResponses(_responses);
        return this;
    }

    @Override
    public Components addResponse(final String key, final APIResponse _responses) {
        (this._responses = this._responses == null ? new LinkedHashMap<>() : this._responses).put(key, _responses);
        return this;
    }

    @Override
    public Map<String, Schema> getSchemas() {
        return _schemas;
    }

    @Override
    public void setSchemas(final Map<String, Schema> _schemas) {
        this._schemas = _schemas;
    }

    @Override
    public Components schemas(final Map<String, Schema> _schemas) {
        setSchemas(_schemas);
        return this;
    }

    @Override
    public Components addSchema(final String key, final Schema _schemas) {
        (this._schemas = this._schemas == null ? new LinkedHashMap<>() : this._schemas).put(key, _schemas);
        return this;
    }

    @Override
    public Map<String, SecurityScheme> getSecuritySchemes() {
        return _securitySchemes;
    }

    @Override
    public void setSecuritySchemes(final Map<String, SecurityScheme> _securitySchemes) {
        this._securitySchemes = _securitySchemes;
    }

    @Override
    public Components securitySchemes(final Map<String, SecurityScheme> _securitySchemes) {
        setSecuritySchemes(_securitySchemes);
        return this;
    }

    @Override
    public Components addSecurityScheme(final String key, final SecurityScheme _securitySchemes) {
        (this._securitySchemes = this._securitySchemes == null ? new LinkedHashMap<>() : this._securitySchemes).put(key,
                _securitySchemes);
        return this;
    }
}
