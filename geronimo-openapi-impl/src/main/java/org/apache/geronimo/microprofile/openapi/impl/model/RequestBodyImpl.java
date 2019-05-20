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
import org.eclipse.microprofile.openapi.models.media.Content;
import org.eclipse.microprofile.openapi.models.parameters.RequestBody;

@Vetoed
public class RequestBodyImpl implements RequestBody {

    private Extensible _extensible = new ExtensibleImpl();

    private Content _content;

    private String _description;

    private String _ref;

    private Boolean _required;

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
    public RequestBody addExtension(final String name, final Object value) {
        _extensible.addExtension(name, value);
        return this;
    }

    @Override
    public void removeExtension(final String name) {
        _extensible.removeExtension(name);
    }

    @Override
    public Content getContent() {
        return _content;
    }

    @Override
    public void setContent(final Content _content) {
        this._content = _content;
    }

    @Override
    public RequestBody content(final Content _content) {
        setContent(_content);
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
    public RequestBody description(final String _description) {
        setDescription(_description);
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
        this._ref = _ref.startsWith("#") ? _ref : ("#/components/requestBodies/" + _ref);
    }

    @Override
    public RequestBody ref(final String _ref) {
        setRef(_ref);
        return this;
    }

    @Override
    public Boolean getRequired() {
        return _required;
    }

    @Override
    public void setRequired(final Boolean _required) {
        this._required = _required;
    }

    @Override
    public RequestBody required(final Boolean _required) {
        setRequired(_required);
        return this;
    }
}
