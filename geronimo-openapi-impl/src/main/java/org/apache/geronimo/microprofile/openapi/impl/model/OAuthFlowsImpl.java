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
import org.eclipse.microprofile.openapi.models.security.OAuthFlow;
import org.eclipse.microprofile.openapi.models.security.OAuthFlows;

@Vetoed
public class OAuthFlowsImpl implements OAuthFlows {

    private Extensible _extensible = new ExtensibleImpl();

    private OAuthFlow _authorizationCode;

    private OAuthFlow _clientCredentials;

    private OAuthFlow _implicit;

    private OAuthFlow _password;

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
    public OAuthFlows addExtension(final String name, final Object value) {
        _extensible.addExtension(name, value);
        return this;
    }

    @Override
    public void removeExtension(final String name) {
        _extensible.removeExtension(name);
    }

    @Override
    public OAuthFlow getAuthorizationCode() {
        return _authorizationCode;
    }

    @Override
    public void setAuthorizationCode(final OAuthFlow _authorizationCode) {
        this._authorizationCode = _authorizationCode;
    }

    @Override
    public OAuthFlows authorizationCode(final OAuthFlow _authorizationCode) {
        setAuthorizationCode(_authorizationCode);
        return this;
    }

    @Override
    public OAuthFlow getClientCredentials() {
        return _clientCredentials;
    }

    @Override
    public void setClientCredentials(final OAuthFlow _clientCredentials) {
        this._clientCredentials = _clientCredentials;
    }

    @Override
    public OAuthFlows clientCredentials(final OAuthFlow _clientCredentials) {
        setClientCredentials(_clientCredentials);
        return this;
    }

    @Override
    public OAuthFlow getImplicit() {
        return _implicit;
    }

    @Override
    public void setImplicit(final OAuthFlow _implicit) {
        this._implicit = _implicit;
    }

    @Override
    public OAuthFlows implicit(final OAuthFlow _implicit) {
        setImplicit(_implicit);
        return this;
    }

    @Override
    public OAuthFlow getPassword() {
        return _password;
    }

    @Override
    public void setPassword(final OAuthFlow _password) {
        this._password = _password;
    }

    @Override
    public OAuthFlows password(final OAuthFlow _password) {
        setPassword(_password);
        return this;
    }
}
