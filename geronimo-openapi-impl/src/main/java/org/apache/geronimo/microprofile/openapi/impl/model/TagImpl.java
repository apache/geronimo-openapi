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
import java.util.Objects;

import javax.enterprise.inject.Vetoed;
import javax.json.bind.annotation.JsonbTransient;

import org.eclipse.microprofile.openapi.models.Extensible;
import org.eclipse.microprofile.openapi.models.ExternalDocumentation;
import org.eclipse.microprofile.openapi.models.tags.Tag;

@Vetoed
public class TagImpl implements Tag {

    private Extensible _extensible = new ExtensibleImpl();

    private String _description;

    private ExternalDocumentation _externalDocs;

    private String _name;

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
    public Tag addExtension(final String name, final Object value) {
        _extensible.addExtension(name, value);
        return this;
    }

    @Override
    public void removeExtension(final String name) {
        _extensible.removeExtension(name);
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
    public Tag description(final String _description) {
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
    public Tag externalDocs(final ExternalDocumentation _externalDocs) {
        setExternalDocs(_externalDocs);
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
    public Tag name(final String _name) {
        setName(_name);
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final TagImpl tag = TagImpl.class.cast(o);
        return Objects.equals(_extensible, tag._extensible) && Objects.equals(_description,
                tag._description) && Objects.equals(_externalDocs, tag._externalDocs) && Objects.equals(_name,
                tag._name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_extensible, _description, _externalDocs, _name);
    }
}
