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
import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.annotation.JsonbTypeDeserializer;

import org.apache.geronimo.microprofile.openapi.impl.model.codec.Deserializers;
import org.eclipse.microprofile.openapi.models.Extensible;
import org.eclipse.microprofile.openapi.models.headers.Header;
import org.eclipse.microprofile.openapi.models.links.Link;
import org.eclipse.microprofile.openapi.models.media.Content;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;

@Vetoed
public class APIResponseImpl implements APIResponse {

    private Extensible _extensible = new ExtensibleImpl();

    private Content _content;

    private String _description;

    private Map<String, Header> _headers;

    private Map<String, Link> _links;

    private String _ref;

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
    public APIResponse addExtension(final String name, final Object value) {
        _extensible.addExtension(name, value);
        return this;
    }

    @Override
    public void removeHeader(final String name) {
        _headers.remove(name);
    }

    @Override
    public void removeLink(final String name) {
        _links.remove(name);
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
    public APIResponse content(final Content _content) {
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
    public APIResponse description(final String _description) {
        setDescription(_description);
        return this;
    }

    @Override
    public Map<String, Header> getHeaders() {
        return _headers;
    }

    @Override
    @JsonbTypeDeserializer(Deserializers.MapHeadersDeserializer.class)
    public void setHeaders(final Map<String, Header> _headers) {
        this._headers = _headers;
    }

    @Override
    public APIResponse headers(final Map<String, Header> _headers) {
        setHeaders(_headers);
        return this;
    }

    @Override
    public APIResponse addHeader(final String key, final Header _headers) {
        if (_headers != null) {
            (this._headers = this._headers == null ? new LinkedHashMap<>() : this._headers).put(key, _headers);
        }
        return this;
    }

    @Override
    public Map<String, Link> getLinks() {
        return _links;
    }

    @Override
    @JsonbTypeDeserializer(Deserializers.MapLinksDeserializer.class)
    public void setLinks(final Map<String, Link> _links) {
        this._links = _links;
    }

    @Override
    public APIResponse links(final Map<String, Link> _links) {
        setLinks(_links);
        return this;
    }

    @Override
    public APIResponse addLink(final String key, final Link _links) {
        if (_links != null) {
            (this._links = this._links == null ? new LinkedHashMap<>() : this._links).put(key, _links);
        }
        return this;
    }

    @Override
    public String getRef() {
        return _ref;
    }

    @Override
    public void setRef(final String _ref) {
        this._ref = _ref.startsWith("#") ? _ref : ("#/components/responses/" + _ref);
    }

    @Override
    public APIResponse ref(final String _ref) {
        setRef(_ref);
        return this;
    }
}
