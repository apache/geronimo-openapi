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
import org.eclipse.microprofile.openapi.models.info.Contact;
import org.eclipse.microprofile.openapi.models.info.Info;
import org.eclipse.microprofile.openapi.models.info.License;

@Vetoed
public class InfoImpl implements Info {

    private Extensible _extensible = new ExtensibleImpl();

    private Contact _contact;

    private String _description;

    private License _license;

    private String _termsOfService;

    private String _title;

    private String _version;

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
    public Info addExtension(final String name, final Object value) {
        _extensible.addExtension(name, value);
        return this;
    }

    @Override
    public void removeExtension(final String name) {
        _extensible.removeExtension(name);
    }

    @Override
    public Contact getContact() {
        return _contact;
    }

    @Override
    public void setContact(final Contact _contact) {
        this._contact = _contact;
    }

    @Override
    public Info contact(final Contact _contact) {
        setContact(_contact);
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
    public Info description(final String _description) {
        setDescription(_description);
        return this;
    }

    @Override
    public License getLicense() {
        return _license;
    }

    @Override
    public void setLicense(final License _license) {
        this._license = _license;
    }

    @Override
    public Info license(final License _license) {
        setLicense(_license);
        return this;
    }

    @Override
    public String getTermsOfService() {
        return _termsOfService;
    }

    @Override
    public void setTermsOfService(final String _termsOfService) {
        this._termsOfService = _termsOfService;
    }

    @Override
    public Info termsOfService(final String _termsOfService) {
        setTermsOfService(_termsOfService);
        return this;
    }

    @Override
    public String getTitle() {
        return _title;
    }

    @Override
    public void setTitle(final String _title) {
        this._title = _title;
    }

    @Override
    public Info title(final String _title) {
        setTitle(_title);
        return this;
    }

    @Override
    public String getVersion() {
        return _version;
    }

    @Override
    public void setVersion(final String _version) {
        this._version = _version;
    }

    @Override
    public Info version(final String _version) {
        setVersion(_version);
        return this;
    }
}
