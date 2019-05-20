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

import java.util.Map;

import javax.enterprise.inject.Vetoed;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;

import org.eclipse.microprofile.openapi.models.Extensible;
import org.eclipse.microprofile.openapi.models.servers.Server;
import org.eclipse.microprofile.openapi.models.servers.ServerVariable;
import org.eclipse.microprofile.openapi.models.servers.ServerVariables;

@Vetoed
public class ServerImpl implements Server {

    private Extensible _extensible = new ExtensibleImpl();

    private String _description;

    private String _url;

    private ServerVariables _variables;

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
    public Server addExtension(final String name, final Object value) {
        _extensible.addExtension(name, value);
        return this;
    }

    @Override
    public void removeExtension(final String s) {
        _extensible.removeExtension(s);
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
    public Server description(final String _description) {
        setDescription(_description);
        return this;
    }

    @Override
    public String getUrl() {
        return _url;
    }

    @Override
    public void setUrl(final String _url) {
        this._url = _url;
    }

    @Override
    public Server url(final String _url) {
        setUrl(_url);
        return this;
    }

    @Override
    @JsonbProperty("variables")
    public ServerVariables getVariables() {
        return _variables;
    }

    @Override
    @JsonbProperty("variables")
    public void setVariables(final ServerVariables _variables) {
        this._variables = _variables;
    }

    @Override
    @JsonbTransient
    public void setVariables(final Map<String, ServerVariable> map) {
        if (map != null) {
            this._variables = new ServerVariablesImpl();
            map.forEach((k, v) -> this._variables.addServerVariable(k, v));
        } else {
            this._variables = null;
        }
    }

    @Override
    public Server variables(final ServerVariables _variables) {
        setVariables(_variables);
        return this;
    }
}
