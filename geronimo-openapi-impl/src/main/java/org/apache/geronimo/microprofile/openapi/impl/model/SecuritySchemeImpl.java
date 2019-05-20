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
import javax.json.bind.annotation.JsonbTypeAdapter;

import org.apache.geronimo.microprofile.openapi.impl.model.codec.Serializers;
import org.eclipse.microprofile.openapi.models.Extensible;
import org.eclipse.microprofile.openapi.models.security.OAuthFlows;
import org.eclipse.microprofile.openapi.models.security.SecurityScheme;

@Vetoed
public class SecuritySchemeImpl implements SecurityScheme {

    private Extensible _extensible = new ExtensibleImpl();

    private String _bearerFormat;

    private String _description;

    private OAuthFlows _flows;

    private In _in;

    private String _name;

    private String _openIdConnectUrl;

    private String _ref;

    private String _scheme;

    private Type _type;

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
    public SecurityScheme addExtension(final String name, final Object value) {
        _extensible.addExtension(name, value);
        return this;
    }

    @Override
    public void removeExtension(final String name) {
        _extensible.removeExtension(name);
    }

    @Override
    public String getBearerFormat() {
        return _bearerFormat;
    }

    @Override
    public void setBearerFormat(final String _bearerFormat) {
        this._bearerFormat = _bearerFormat;
    }

    @Override
    public SecurityScheme bearerFormat(final String _bearerFormat) {
        setBearerFormat(_bearerFormat);
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
    public SecurityScheme description(final String _description) {
        setDescription(_description);
        return this;
    }

    @Override
    public OAuthFlows getFlows() {
        return _flows;
    }

    @Override
    public void setFlows(final OAuthFlows _flows) {
        this._flows = _flows;
    }

    @Override
    public SecurityScheme flows(final OAuthFlows _flows) {
        setFlows(_flows);
        return this;
    }

    @Override
    @JsonbTypeAdapter(Serializers.SecuritySchemeInSerializer.class)
    public In getIn() {
        return _in;
    }

    @Override
    @JsonbTypeAdapter(Serializers.SecuritySchemeInSerializer.class)
    public void setIn(final In _in) {
        this._in = _in;
    }

    @Override
    public SecurityScheme in(final In _in) {
        setIn(_in);
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
    public SecurityScheme name(final String _name) {
        setName(_name);
        return this;
    }

    @Override
    public String getOpenIdConnectUrl() {
        return _openIdConnectUrl;
    }

    @Override
    public void setOpenIdConnectUrl(final String _openIdConnectUrl) {
        this._openIdConnectUrl = _openIdConnectUrl;
    }

    @Override
    public SecurityScheme openIdConnectUrl(final String _openIdConnectUrl) {
        setOpenIdConnectUrl(_openIdConnectUrl);
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
        this._ref = _ref.startsWith("#") ? _ref : ("#/components/securitySchemes/" + _ref);
    }

    @Override
    public SecurityScheme ref(final String _ref) {
        setRef(_ref);
        return this;
    }

    @Override
    public String getScheme() {
        return _scheme;
    }

    @Override
    public void setScheme(final String _scheme) {
        this._scheme = _scheme;
    }

    @Override
    public SecurityScheme scheme(final String _scheme) {
        setScheme(_scheme);
        return this;
    }

    @Override
    @JsonbTypeAdapter(Serializers.SecuritySchemeTypeSerializer.class)
    public Type getType() {
        return _type;
    }

    @Override
    @JsonbTypeAdapter(Serializers.SecuritySchemeTypeSerializer.class)
    public void setType(final Type _type) {
        this._type = _type;
    }

    @Override
    public SecurityScheme type(final Type _type) {
        setType(_type);
        return this;
    }
}
