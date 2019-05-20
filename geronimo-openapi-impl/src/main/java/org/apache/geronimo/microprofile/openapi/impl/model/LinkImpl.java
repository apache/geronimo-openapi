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
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;

import org.eclipse.microprofile.openapi.models.Extensible;
import org.eclipse.microprofile.openapi.models.links.Link;
import org.eclipse.microprofile.openapi.models.servers.Server;

@Vetoed
public class LinkImpl implements Link {

    private Extensible _extensible = new ExtensibleImpl();

    private String _description;

    private String _operationId;

    private String _operationRef;

    private Map<String, Object> _parameters;

    private String _ref;

    private Object _requestBody;

    private Server _server;

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
    public Link addExtension(final String name, final Object value) {
        _extensible.addExtension(name, value);
        return this;
    }

    @Override
    public void removeExtension(final String name) {
        _extensible.removeExtension(name);
    }

    @Override
    public void removeParameter(final String name) {
        _parameters.remove(name);
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
    public Link description(final String _description) {
        setDescription(_description);
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
    public Link operationId(final String _operationId) {
        setOperationId(_operationId);
        return this;
    }

    @Override
    public String getOperationRef() {
        return _operationRef;
    }

    @Override
    public void setOperationRef(final String _operationRef) {
        this._operationRef = _operationRef;
    }

    @Override
    public Link operationRef(final String _operationRef) {
        setOperationRef(_operationRef);
        return this;
    }

    @Override
    public Map<String, Object> getParameters() {
        return _parameters;
    }

    @Override
    public void setParameters(final Map<String, Object> _parameters) {
        this._parameters = _parameters;
    }

    @Override
    public Link parameters(final Map<String, Object> _parameters) {
        setParameters(_parameters);
        return this;
    }

    @Override
    public Link addParameter(final String key, final Object _parameters) {
        if (_parameters != null) {
            (this._parameters = this._parameters == null ? new LinkedHashMap<>() : this._parameters).put(key, _parameters);
        }
        return this;
    }

    @Override
    @JsonbProperty("$ref")
    public String getRef() {
        return _ref;
    }

    @Override
    @JsonbProperty("$ref")
    public void setRef(final String _ref) {
        this._ref = _ref.startsWith("#") ? _ref : ("#/components/links/" + _ref);
    }

    @Override
    public Link ref(final String _ref) {
        setRef(_ref);
        return this;
    }

    @Override
    public Object getRequestBody() {
        return _requestBody;
    }

    @Override
    public void setRequestBody(final Object _requestBody) {
        this._requestBody = _requestBody;
    }

    @Override
    public Link requestBody(final Object _requestBody) {
        setRequestBody(_requestBody);
        return this;
    }

    @Override
    public Server getServer() {
        return _server;
    }

    @Override
    public void setServer(final Server _server) {
        this._server = _server;
    }

    @Override
    public Link server(final Server _server) {
        setServer(_server);
        return this;
    }
}
