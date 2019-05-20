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
import org.eclipse.microprofile.openapi.models.examples.Example;

@Vetoed
public class ExampleImpl implements Example {

    private Extensible _extensible = new ExtensibleImpl();

    private String _description;

    private String _externalValue;

    private String _ref;

    private String _summary;

    private Object _value;

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
    public Example addExtension(final String name, final Object value) {
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
    public Example description(final String _description) {
        setDescription(_description);
        return this;
    }

    @Override
    public String getExternalValue() {
        return _externalValue;
    }

    @Override
    public void setExternalValue(final String _externalValue) {
        this._externalValue = _externalValue;
    }

    @Override
    public Example externalValue(final String _externalValue) {
        setExternalValue(_externalValue);
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
        this._ref = _ref.startsWith("#") ? _ref : ("#/components/examples/" + _ref);
    }

    @Override
    public Example ref(final String _ref) {
        setRef(_ref);
        return this;
    }

    @Override
    public String getSummary() {
        return _summary;
    }

    @Override
    public void setSummary(final String _summary) {
        this._summary = _summary;
    }

    @Override
    public Example summary(final String _summary) {
        setSummary(_summary);
        return this;
    }

    @Override
    public Object getValue() {
        return _value;
    }

    @Override
    public void setValue(final Object _value) {
        this._value = _value;
    }

    @Override
    public Example value(final Object _value) {
        setValue(_value);
        return this;
    }
}
