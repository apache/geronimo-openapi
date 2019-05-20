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
import java.util.Objects;
import java.util.stream.Stream;

import javax.enterprise.inject.Vetoed;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.annotation.JsonbTypeSerializer;

import org.apache.geronimo.microprofile.openapi.impl.model.codec.Serializers;
import org.eclipse.microprofile.openapi.models.Extensible;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.PathItem;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.eclipse.microprofile.openapi.models.servers.Server;

@Vetoed
public class PathItemImpl implements PathItem {

    private Extensible _extensible = new ExtensibleImpl();

    private String _description;

    private Operation _dELETE;

    private Operation _gET;

    private Operation _hEAD;

    private Operation _oPTIONS;

    private Operation _pATCH;

    private Operation _pOST;

    private Operation _pUT;

    private Operation _tRACE;

    private List<Parameter> _parameters;

    private String _ref;

    private List<Server> _servers;

    private String _summary;

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
    public PathItem addExtension(final String name, final Object value) {
        _extensible.addExtension(name, value);
        return this;
    }

    @Override
    public void removeExtension(final String name) {
        _extensible.removeExtension(name);
    }

    @Override
    @JsonbProperty("delete")
    @JsonbTypeSerializer(Serializers.ExtensionSerializer.class)
    public Operation getDELETE() {
        return _dELETE;
    }

    @Override
    @JsonbProperty("delete")
    public void setDELETE(final Operation _dELETE) {
        this._dELETE = _dELETE;
    }

    @Override
    public PathItem DELETE(final Operation delete) {
        setDELETE(delete);
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
    public PathItem description(final String _description) {
        setDescription(_description);
        return this;
    }

    @Override
    @JsonbProperty("get")
    @JsonbTypeSerializer(Serializers.ExtensionSerializer.class)
    public Operation getGET() {
        return _gET;
    }

    @Override
    @JsonbProperty("get")
    public void setGET(final Operation _gET) {
        this._gET = _gET;
    }

    @Override
    public PathItem GET(final Operation get) {
        setGET(get);
        return this;
    }

    @Override
    @JsonbProperty("head")
    @JsonbTypeSerializer(Serializers.ExtensionSerializer.class)
    public Operation getHEAD() {
        return _hEAD;
    }

    @Override
    @JsonbProperty("head")
    public void setHEAD(final Operation _hEAD) {
        this._hEAD = _hEAD;
    }

    @Override
    public PathItem HEAD(final Operation head) {
        setHEAD(head);
        return this;
    }

    @Override
    @JsonbProperty("options")
    @JsonbTypeSerializer(Serializers.ExtensionSerializer.class)
    public Operation getOPTIONS() {
        return _oPTIONS;
    }

    @Override
    @JsonbProperty("options")
    public void setOPTIONS(final Operation _oPTIONS) {
        this._oPTIONS = _oPTIONS;
    }

    @Override
    public PathItem OPTIONS(final Operation options) {
        setOPTIONS(options);
        return this;
    }

    @Override
    @JsonbProperty("patch")
    @JsonbTypeSerializer(Serializers.ExtensionSerializer.class)
    public Operation getPATCH() {
        return _pATCH;
    }

    @Override
    @JsonbProperty("patch")
    public void setPATCH(final Operation _pATCH) {
        this._pATCH = _pATCH;
    }

    @Override
    public PathItem PATCH(final Operation patch) {
        setPATCH(patch);
        return this;
    }

    @Override
    @JsonbProperty("post")
    @JsonbTypeSerializer(Serializers.ExtensionSerializer.class)
    public Operation getPOST() {
        return _pOST;
    }

    @Override
    @JsonbProperty("post")
    public void setPOST(final Operation _pOST) {
        this._pOST = _pOST;
    }

    @Override
    public PathItem POST(final Operation post) {
        setPOST(post);
        return this;
    }

    @Override
    @JsonbProperty("put")
    @JsonbTypeSerializer(Serializers.ExtensionSerializer.class)
    public Operation getPUT() {
        return _pUT;
    }

    @Override
    @JsonbProperty("put")
    public void setPUT(final Operation _pUT) {
        this._pUT = _pUT;
    }

    @Override
    public PathItem PUT(final Operation put) {
        setPUT(put);
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
    public PathItem parameters(final List<Parameter> _parameters) {
        setParameters(_parameters);
        return this;
    }

    @Override
    public PathItem addParameter(final Parameter _parameters) {
        if (_parameters != null) {
            (this._parameters = this._parameters == null ? new ArrayList<>() : this._parameters).add(_parameters);
        }
        return this;
    }

    @Override
    public void removeParameter(final Parameter parameter) {
        _parameters.remove(parameter);
    }

    @Override
    @JsonbProperty("$ref")
    public String getRef() {
        return _ref;
    }

    @Override
    @JsonbProperty("$ref")
    public void setRef(final String _ref) {
        this._ref = _ref;
    }

    @Override
    public PathItem ref(final String _ref) {
        setRef(_ref);
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
    public PathItem servers(final List<Server> _servers) {
        setServers(_servers);
        return this;
    }

    @Override
    public PathItem addServer(final Server _servers) {
        if (_servers != null) {
            (this._servers = this._servers == null ? new ArrayList<>() : this._servers).add(_servers);
        }
        return this;
    }

    @Override
    public void removeServer(final Server server) {
        _servers.remove(server);
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
    public PathItem summary(final String _summary) {
        setSummary(_summary);
        return this;
    }

    @Override
    @JsonbProperty("trace")
    @JsonbTypeSerializer(Serializers.ExtensionSerializer.class)
    public Operation getTRACE() {
        return _tRACE;
    }

    @Override
    @JsonbProperty("trace")
    public void setTRACE(final Operation _tRACE) {
        this._tRACE = _tRACE;
    }

    @Override
    public PathItem TRACE(final Operation trace) {
        setTRACE(trace);
        return this;
    }

    @Override
    public List<Operation> readOperations() {
        return Stream.of(_dELETE, _gET, _hEAD, _oPTIONS, _pATCH, _pOST, _pUT, _tRACE).filter(Objects::nonNull).collect(toList());
    }

    @Override
    @JsonbTransient
    public Map<HttpMethod, Operation> getOperations() {
        final Map<HttpMethod, Operation> map = new HashMap<>();
        map.put(HttpMethod.DELETE, _dELETE);
        map.put(HttpMethod.GET, _gET);
        map.put(HttpMethod.HEAD, _hEAD);
        map.put(HttpMethod.OPTIONS, _oPTIONS);
        map.put(HttpMethod.PATCH, _pATCH);
        map.put(HttpMethod.POST, _pOST);
        map.put(HttpMethod.PUT, _pUT);
        map.put(HttpMethod.TRACE, _tRACE);
        return map;
    }

    @Override
    public Map<HttpMethod, Operation> readOperationsMap() {
        return getOperations();
    }
}
