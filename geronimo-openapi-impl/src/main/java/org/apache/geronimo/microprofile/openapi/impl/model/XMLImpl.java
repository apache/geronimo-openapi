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
import javax.json.bind.annotation.JsonbTransient;

import org.eclipse.microprofile.openapi.models.Extensible;
import org.eclipse.microprofile.openapi.models.media.XML;

@Vetoed
public class XMLImpl implements XML {

    private Extensible _extensible = new ExtensibleImpl();

    private Boolean _attribute;

    private String _name;

    private String _namespace;

    private String _prefix;

    private Boolean _wrapped;

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
    public XML addExtension(final String name, final Object value) {
        _extensible.addExtension(name, value);
        return this;
    }

    @Override
    public void removeExtension(final String name) {
        _extensible.removeExtension(name);
    }

    @Override
    public Boolean getAttribute() {
        return _attribute;
    }

    @Override
    public void setAttribute(final Boolean _attribute) {
        this._attribute = _attribute;
    }

    @Override
    public XML attribute(final Boolean _attribute) {
        setAttribute(_attribute);
        return this;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public void setName(final String _name) {
        this._name = _name;
    }

    @Override
    public XML name(final String _name) {
        setName(_name);
        return this;
    }

    @Override
    public String getNamespace() {
        return _namespace;
    }

    @Override
    public void setNamespace(final String _namespace) {
        this._namespace = _namespace;
    }

    @Override
    public XML namespace(final String _namespace) {
        setNamespace(_namespace);
        return this;
    }

    @Override
    public String getPrefix() {
        return _prefix;
    }

    @Override
    public void setPrefix(final String _prefix) {
        this._prefix = _prefix;
    }

    @Override
    public XML prefix(final String _prefix) {
        setPrefix(_prefix);
        return this;
    }

    @Override
    public Boolean getWrapped() {
        return _wrapped;
    }

    @Override
    public void setWrapped(final Boolean _wrapped) {
        this._wrapped = _wrapped;
    }

    @Override
    public XML wrapped(final Boolean _wrapped) {
        setWrapped(_wrapped);
        return this;
    }
}
