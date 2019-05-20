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
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.annotation.JsonbTypeAdapter;

import org.apache.geronimo.microprofile.openapi.impl.model.codec.Serializers;
import org.eclipse.microprofile.openapi.models.Extensible;
import org.eclipse.microprofile.openapi.models.examples.Example;
import org.eclipse.microprofile.openapi.models.media.Content;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;

@Vetoed
public class ParameterImpl implements Parameter {

    private Extensible _extensible = new ExtensibleImpl();

    private Boolean _allowEmptyValue;

    private Boolean _allowReserved;

    private Content _content;

    private Boolean _deprecated;

    private String _description;

    private Object _example;

    private Map<String, Example> _examples;

    private Boolean _explode;

    private In _in;

    private String _name;

    private String _ref;

    private Boolean _required;

    private Schema _schema;

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
    public Parameter addExtension(final String name, final Object value) {
        _extensible.addExtension(name, value);
        return this;
    }

    @Override
    public void removeExtension(final String name) {
        _extensible.removeExtension(name);
    }

    @Override
    public Boolean getAllowEmptyValue() {
        return _allowEmptyValue;
    }

    @Override
    public void setAllowEmptyValue(final Boolean _allowEmptyValue) {
        this._allowEmptyValue = _allowEmptyValue;
    }

    @Override
    public Parameter allowEmptyValue(final Boolean _allowEmptyValue) {
        setAllowEmptyValue(_allowEmptyValue);
        return this;
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
    public Parameter allowReserved(final Boolean _allowReserved) {
        setAllowReserved(_allowReserved);
        return this;
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
    public Parameter content(final Content _content) {
        setContent(_content);
        return this;
    }

    @Override
    public Boolean getDeprecated() {
        return _deprecated;
    }

    @Override
    public void setDeprecated(final Boolean _deprecated) {
        this._deprecated = _deprecated;
    }

    @Override
    public Parameter deprecated(final Boolean _deprecated) {
        setDeprecated(_deprecated);
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
    public Parameter description(final String _description) {
        setDescription(_description);
        return this;
    }

    @Override
    public Object getExample() {
        return _example;
    }

    @Override
    public void setExample(final Object _example) {
        this._example = _example;
    }

    @Override
    public Parameter example(final Object _example) {
        setExample(_example);
        return this;
    }

    @Override
    public Map<String, Example> getExamples() {
        return _examples;
    }

    @Override
    public void setExamples(final Map<String, Example> _examples) {
        this._examples = _examples;
    }

    @Override
    public Parameter examples(final Map<String, Example> _examples) {
        setExamples(_examples);
        return this;
    }

    @Override
    public Parameter addExample(final String key, final Example _examples) {
        if (_examples != null) {
            (this._examples = this._examples == null ? new LinkedHashMap<>() : this._examples).put(key, _examples);
        }
        return this;
    }

    @Override
    public void removeExample(final String key) {
        _examples.remove(key);
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
    public Parameter explode(final Boolean _explode) {
        setExplode(_explode);
        return this;
    }

    @Override
    @JsonbTypeAdapter(Serializers.InSerializer.class)
    public In getIn() {
        return _in;
    }

    @Override
    @JsonbTypeAdapter(Serializers.InSerializer.class)
    public void setIn(final In _in) {
        this._in = _in;
    }

    @Override
    public Parameter in(final In _in) {
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
    public Parameter name(final String _name) {
        setName(_name);
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
        this._ref = _ref.startsWith("#") ? _ref : ("#/components/parameters/" + _ref);
    }

    @Override
    public Parameter ref(final String _ref) {
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
    public Parameter required(final Boolean _required) {
        setRequired(_required);
        return this;
    }

    @Override
    public Schema getSchema() {
        return _schema;
    }

    @Override
    public void setSchema(final Schema _schema) {
        this._schema = _schema;
    }

    @Override
    public Parameter schema(final Schema _schema) {
        setSchema(_schema);
        return this;
    }

    @Override
    @JsonbTypeAdapter(Serializers.ParameterStyleSerializer.class)
    public Style getStyle() {
        return _style;
    }

    @Override
    @JsonbTypeAdapter(Serializers.ParameterStyleSerializer.class)
    public void setStyle(final Style _style) {
        this._style = _style;
    }

    @Override
    public Parameter style(final Style _style) {
        setStyle(_style);
        return this;
    }
}
