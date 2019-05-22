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
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Vetoed;
import javax.json.bind.annotation.JsonbTransient;

import org.eclipse.microprofile.openapi.models.Components;
import org.eclipse.microprofile.openapi.models.Extensible;
import org.eclipse.microprofile.openapi.models.ExternalDocumentation;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.PathItem;
import org.eclipse.microprofile.openapi.models.Paths;
import org.eclipse.microprofile.openapi.models.info.Info;
import org.eclipse.microprofile.openapi.models.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.models.servers.Server;
import org.eclipse.microprofile.openapi.models.tags.Tag;

@Vetoed
public class OpenAPIImpl implements OpenAPI {

    private Extensible _extensible = new ExtensibleImpl();

    private Components _components;

    private ExternalDocumentation _externalDocs;

    private Info _info;

    private String _openapi = "3.0.1";

    private Paths _paths;

    private List<SecurityRequirement> _security;

    private List<Server> _servers;

    private List<Tag> _tags;

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
    public OpenAPI addExtension(final String name, final Object value) {
        _extensible.addExtension(name, value);
        return this;
    }

    @Override
    public void removeExtension(final String name) {
        _extensible.removeExtension(name);
    }

    @Override
    public void removeServer(final Server server) {
        _servers.remove(server);
    }

    @Override
    public void removeSecurityRequirement(final SecurityRequirement securityRequirement) {
        _security.remove(securityRequirement);
    }

    @Override
    public void removeTag(final Tag tag) {
        _tags.remove(tag);
    }

    @Override
    public Components getComponents() {
        return _components;
    }

    @Override
    public void setComponents(final Components _components) {
        this._components = _components;
    }

    @Override
    public OpenAPI components(final Components _components) {
        setComponents(_components);
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
    public OpenAPI externalDocs(final ExternalDocumentation _externalDocs) {
        setExternalDocs(_externalDocs);
        return this;
    }

    @Override
    public Info getInfo() {
        return _info;
    }

    @Override
    public void setInfo(final Info _info) {
        this._info = _info;
    }

    @Override
    public OpenAPI info(final Info _info) {
        setInfo(_info);
        return this;
    }

    @Override
    public String getOpenapi() {
        return _openapi;
    }

    @Override
    public void setOpenapi(final String _openapi) {
        this._openapi = _openapi;
    }

    @Override
    public OpenAPI openapi(final String _openapi) {
        setOpenapi(_openapi);
        return this;
    }

    @Override
    public Paths getPaths() {
        return _paths;
    }

    @Override
    public void setPaths(final Paths _paths) {
        this._paths = _paths;
    }

    @Override
    public OpenAPI paths(final Paths _paths) {
        setPaths(_paths);
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
    public OpenAPI security(final List<SecurityRequirement> _security) {
        setSecurity(_security);
        return this;
    }

    @Override
    public OpenAPI addSecurityRequirement(final SecurityRequirement _security) {
        (this._security = this._security == null ? new ArrayList<>() : this._security).add(_security);
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
    public OpenAPI servers(final List<Server> _servers) {
        setServers(_servers);
        return this;
    }

    @Override
    public OpenAPI addServer(final Server _servers) {
        (this._servers = this._servers == null ? new ArrayList<>() : this._servers).add(_servers);
        return this;
    }

    @Override
    public List<Tag> getTags() {
        return _tags;
    }

    @Override
    public void setTags(final List<Tag> _tags) {
        this._tags = _tags == null ? null : _tags.stream().distinct().collect(toList());
    }

    @Override
    public OpenAPI tags(final List<Tag> _tags) {
        setTags(_tags);
        return this;
    }

    @Override
    public OpenAPI addTag(final Tag _tags) {
        if (this._tags == null) {
            this._tags = new ArrayList<>();
        }
        if (!this._tags.contains(_tags)) {
            this._tags.add(_tags);
        }
        return this;
    }

    @Override
    public OpenAPI path(final String name, final PathItem path) {
        (_paths = this._paths == null ? new PathsImpl() : this._paths).addPathItem(name, path);
        return this;
    }
}
