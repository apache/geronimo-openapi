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

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Vetoed;
import javax.json.bind.annotation.JsonbTransient;

import org.eclipse.microprofile.openapi.models.Extensible;
import org.eclipse.microprofile.openapi.models.ExternalDocumentation;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.callbacks.Callback;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.eclipse.microprofile.openapi.models.parameters.RequestBody;
import org.eclipse.microprofile.openapi.models.responses.APIResponses;
import org.eclipse.microprofile.openapi.models.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.models.servers.Server;

@Vetoed
public class OperationImpl implements Operation {

    private Extensible _extensible = new ExtensibleImpl();

    private Map<String, Callback> _callbacks;

    private Boolean _deprecated;

    private String _description;

    private ExternalDocumentation _externalDocs;

    private String _operationId;

    private List<Parameter> _parameters;

    private RequestBody _requestBody;

    private APIResponses _responses;

    private List<SecurityRequirement> _security;

    private List<Server> _servers;

    private String _summary;

    private List<String> _tags;

    @Override
    @JsonbTransient
    public Map<String, Object> getExtensions() {
        return _extensible.getExtensions();
    }

    @Override
    public void setExtensions(final Map<String, Object> extensions) {
        _extensible.setExtensions(extensions);
    }

    @Override
    public Operation addExtension(final String name, final Object value) {
        _extensible.addExtension(name, value);
        return this;
    }

    @Override
    public void removeExtension(final String name) {
        _extensible.removeExtension(name);
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
    public Operation callbacks(final Map<String, Callback> _callbacks) {
        setCallbacks(_callbacks);
        return this;
    }

    @Override
    public Boolean getDeprecated() {
        return _deprecated;
    }

    @Override
    public void setDeprecated(final Boolean _deprecated) {
        this._deprecated = _deprecated;
    }

    @Override
    public Operation deprecated(final Boolean _deprecated) {
        setDeprecated(_deprecated);
        return this;
    }

    @Override
    public String getDescription() {
        return _description;
    }

    @Override
    public void setDescription(final String _description) {
        this._description = _description;
    }

    @Override
    public Operation description(final String _description) {
        setDescription(_description);
        return this;
    }

    @Override
    public ExternalDocumentation getExternalDocs() {
        return _externalDocs;
    }

    @Override
    public void setExternalDocs(final ExternalDocumentation _externalDocs) {
        this._externalDocs = _externalDocs;
    }

    @Override
    public Operation externalDocs(final ExternalDocumentation _externalDocs) {
        setExternalDocs(_externalDocs);
        return this;
    }

    @Override
    public String getOperationId() {
        return _operationId;
    }

    @Override
    public void setOperationId(final String _operationId) {
        this._operationId = _operationId;
    }

    @Override
    public Operation operationId(final String _operationId) {
        setOperationId(_operationId);
        return this;
    }

    @Override
    public List<Parameter> getParameters() {
        return _parameters;
    }

    @Override
    public void setParameters(final List<Parameter> _parameters) {
        this._parameters = _parameters;
    }

    @Override
    public Operation parameters(final List<Parameter> _parameters) {
        setParameters(_parameters);
        return this;
    }

    @Override
    public Operation addParameter(final Parameter _parameters) {
        if (_parameters != null) {
            (this._parameters = this._parameters == null ? new ArrayList<>() : this._parameters).add(_parameters);
        }
        return this;
    }

    @Override
    public RequestBody getRequestBody() {
        return _requestBody;
    }

    @Override
    public void setRequestBody(final RequestBody _requestBody) {
        this._requestBody = _requestBody;
    }

    @Override
    public Operation requestBody(final RequestBody _requestBody) {
        setRequestBody(_requestBody);
        return this;
    }

    @Override
    public APIResponses getResponses() {
        return _responses;
    }

    @Override
    public void setResponses(final APIResponses _responses) {
        this._responses = _responses;
    }

    @Override
    public Operation responses(final APIResponses _responses) {
        setResponses(_responses);
        return this;
    }

    @Override
    public List<SecurityRequirement> getSecurity() {
        return _security;
    }

    @Override
    public void setSecurity(final List<SecurityRequirement> _security) {
        this._security = _security;
    }

    @Override
    public Operation security(final List<SecurityRequirement> _security) {
        setSecurity(_security);
        return this;
    }

    @Override
    public Operation addSecurityRequirement(final SecurityRequirement _security) {
        if (_security != null) {
            (this._security = this._security == null ? new ArrayList<>() : this._security).add(_security);
        }
        return this;
    }

    @Override
    public List<Server> getServers() {
        return _servers;
    }

    @Override
    public void setServers(final List<Server> _servers) {
        this._servers = _servers;
    }

    @Override
    public Operation servers(final List<Server> _servers) {
        setServers(_servers);
        return this;
    }

    @Override
    public Operation addServer(final Server _servers) {
        (this._servers = this._servers == null ? new ArrayList<>() : this._servers).add(_servers);
        return this;
    }

    @Override
    public String getSummary() {
        return _summary;
    }

    @Override
    public void setSummary(final String _summary) {
        this._summary = _summary;
    }

    @Override
    public Operation summary(final String _summary) {
        setSummary(_summary);
        return this;
    }

    @Override
    public List<String> getTags() {
        return _tags;
    }

    @Override
    public void setTags(final List<String> _tags) {
        this._tags = _tags;
    }

    @Override
    public Operation tags(final List<String> _tags) {
        setTags(_tags == null ? null : _tags.stream().distinct().collect(toList()));
        return this;
    }

    @Override
    public Operation addTag(final String tag) {
        final List<String> tags = _tags = _tags == null ? new ArrayList<>() : _tags;
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
        return this;
    }

    @Override
    public void removeTag(final String tag) {
        _tags.remove(tag);
    }

    @Override
    public void removeParameter(final Parameter parameter) {
        _parameters.remove(parameter);
    }

    @Override
    public Operation addCallback(final String key, final Callback callback) {
        if (callback != null) {
            if (_callbacks == null) {
                _callbacks = new HashMap<>();
            }
            _callbacks.put(key, callback);
        }
        return this;
    }

    @Override
    public void removeCallback(final String key) {
        _callbacks.remove(key);
    }

    @Override
    public void removeSecurityRequirement(final SecurityRequirement securityRequirement) {
        _security.remove(securityRequirement);
    }

    @Override
    public void removeServer(final Server server) {
        _servers.remove(server);
    }
}
