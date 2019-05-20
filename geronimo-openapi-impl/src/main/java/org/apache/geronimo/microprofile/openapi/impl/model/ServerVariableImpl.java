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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Vetoed;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;

import org.eclipse.microprofile.openapi.models.Extensible;
import org.eclipse.microprofile.openapi.models.servers.ServerVariable;

@Vetoed
public class ServerVariableImpl implements ServerVariable {

    private Extensible _extensible = new ExtensibleImpl();

    private String _defaultValue;

    private String _description;

    private List<String> _enumeration;

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
    public ServerVariable addExtension(final String name, final Object value) {
        _extensible.addExtension(name, value);
        return this;
    }

    @Override
    public void removeExtension(final String name) {
        _extensible.removeExtension(name);
    }

    @Override
    public void removeEnumeration(final String enumeration) {
        _enumeration.remove(enumeration);
    }

    @Override
    @JsonbProperty("default")
    public String getDefaultValue() {
        return _defaultValue;
    }

    @Override
    @JsonbProperty("default")
    public void setDefaultValue(final String _defaultValue) {
        this._defaultValue = _defaultValue;
    }

    @Override
    public ServerVariable defaultValue(final String _defaultValue) {
        setDefaultValue(_defaultValue);
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
    public ServerVariable description(final String _description) {
        setDescription(_description);
        return this;
    }

    @Override
    @JsonbProperty("enum")
    public List<String> getEnumeration() {
        return _enumeration;
    }

    @Override
    @JsonbProperty("enum")
    public void setEnumeration(final List<String> _enumeration) {
        this._enumeration = _enumeration;
    }

    @Override
    public ServerVariable enumeration(final List<String> _enumeration) {
        setEnumeration(_enumeration);
        return this;
    }

    @Override
    public ServerVariable addEnumeration(final String enumeration) {
        (_enumeration = _enumeration == null ? new ArrayList<>() : _enumeration).add(enumeration);
        return this;
    }
}
