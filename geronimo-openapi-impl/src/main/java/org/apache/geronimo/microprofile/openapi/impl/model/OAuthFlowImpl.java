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
import org.eclipse.microprofile.openapi.models.security.Scopes;

@Vetoed
public class OAuthFlowImpl implements OAuthFlow {

    private Extensible _extensible = new ExtensibleImpl();

    private String _authorizationUrl;

    private String _refreshUrl;

    private Scopes _scopes;

    private String _tokenUrl;

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
    public OAuthFlow addExtension(final String name, final Object value) {
        _extensible.addExtension(name, value);
        return this;
    }

    @Override
    public void removeExtension(final String name) {
        _extensible.removeExtension(name);
    }

    @Override
    public String getAuthorizationUrl() {
        return _authorizationUrl;
    }

    @Override
    public void setAuthorizationUrl(final String _authorizationUrl) {
        this._authorizationUrl = _authorizationUrl;
    }

    @Override
    public OAuthFlow authorizationUrl(final String _authorizationUrl) {
        setAuthorizationUrl(_authorizationUrl);
        return this;
    }

    @Override
    public String getRefreshUrl() {
        return _refreshUrl;
    }

    @Override
    public void setRefreshUrl(final String _refreshUrl) {
        this._refreshUrl = _refreshUrl;
    }

    @Override
    public OAuthFlow refreshUrl(final String _refreshUrl) {
        setRefreshUrl(_refreshUrl);
        return this;
    }

    @Override
    public Scopes getScopes() {
        return _scopes;
    }

    @Override
    public void setScopes(final Scopes _scopes) {
        this._scopes = _scopes;
    }

    @Override
    public OAuthFlow scopes(final Scopes _scopes) {
        setScopes(_scopes);
        return this;
    }

    @Override
    public String getTokenUrl() {
        return _tokenUrl;
    }

    @Override
    public void setTokenUrl(final String _tokenUrl) {
        this._tokenUrl = _tokenUrl;
    }

    @Override
    public OAuthFlow tokenUrl(final String _tokenUrl) {
        setTokenUrl(_tokenUrl);
        return this;
    }
}
