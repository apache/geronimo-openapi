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
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.json.bind.annotation.JsonbTypeDeserializer;

import org.apache.geronimo.microprofile.openapi.impl.model.codec.Deserializers;
import org.apache.geronimo.microprofile.openapi.impl.model.codec.Serializers;
import org.eclipse.microprofile.openapi.models.Extensible;
import org.eclipse.microprofile.openapi.models.headers.Header;
import org.eclipse.microprofile.openapi.models.media.Encoding;

@Vetoed
public class EncodingImpl implements Encoding {

    private Extensible _extensible = new ExtensibleImpl();

    private Boolean _allowReserved;

    private String _contentType;

    private Boolean _explode;

    private Map<String, Header> _headers;

    private Style _style;

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
    public Encoding addExtension(final String name, final Object value) {
        _extensible.addExtension(name, value);
        return this;
    }

    @Override
    public void removeExtension(final String name) {
        _extensible.removeExtension(name);
    }

    @Override
    public Boolean getAllowReserved() {
        return _allowReserved;
    }

    @Override
    public void setAllowReserved(final Boolean _allowReserved) {
        this._allowReserved = _allowReserved;
    }

    @Override
    public Encoding allowReserved(final Boolean _allowReserved) {
        setAllowReserved(_allowReserved);
        return this;
    }

    @Override
    public String getContentType() {
        return _contentType;
    }

    @Override
    public void setContentType(final String _contentType) {
        this._contentType = _contentType;
    }

    @Override
    public Encoding contentType(final String _contentType) {
        setContentType(_contentType);
        return this;
    }

    @Override
    public Boolean getExplode() {
        return _explode;
    }

    @Override
    public void setExplode(final Boolean _explode) {
        this._explode = _explode;
    }

    @Override
    public Encoding explode(final Boolean _explode) {
        setExplode(_explode);
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
    public Encoding addHeader(final String key, final Header header) {
        if (header != null) {
            _headers.put(key, header);
        }
        return this;
    }

    @Override
    public void removeHeader(final String key) {
        _headers.remove(key);
    }

    @Override
    public Encoding headers(final Map<String, Header> _headers) {
        setHeaders(_headers);
        return this;
    }

    @Override
    @JsonbTypeAdapter(Serializers.EncodingStyleSerializer.class)
    public Style getStyle() {
        return _style;
    }

    @Override
    @JsonbTypeAdapter(Serializers.EncodingStyleSerializer.class)
    public void setStyle(final Style _style) {
        this._style = _style;
    }

    @Override
    public Encoding style(final Style _style) {
        setStyle(_style);
        return this;
    }
}
