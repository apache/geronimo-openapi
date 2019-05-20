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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Vetoed;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.annotation.JsonbTypeAdapter;

import org.apache.geronimo.microprofile.openapi.impl.model.codec.Serializers;
import org.eclipse.microprofile.openapi.models.Extensible;
import org.eclipse.microprofile.openapi.models.ExternalDocumentation;
import org.eclipse.microprofile.openapi.models.media.Discriminator;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.media.XML;

@Vetoed
public class SchemaImpl implements Schema {

    private Extensible _extensible = new ExtensibleImpl();

    private Object _additionalProperties;

    private List<Schema> _allOf;

    private List<Schema> _anyOf;

    private Object _defaultValue;

    private Boolean _deprecated;

    private String _description;

    private Discriminator _discriminator;

    private List<Object> _enumeration;

    private Object _example;

    private Boolean _exclusiveMaximum;

    private Boolean _exclusiveMinimum;

    private ExternalDocumentation _externalDocs;

    private String _format;

    private Schema _items;

    private Integer _maxItems;

    private Integer _maxLength;

    private Integer _maxProperties;

    private Integer _minItems;

    private Integer _minLength;

    private Integer _minProperties;

    private java.math.BigDecimal _maximum;

    private java.math.BigDecimal _minimum;

    private java.math.BigDecimal _multipleOf;

    private Schema _not;

    private Boolean _nullable;

    private List<Schema> _oneOf;

    private String _pattern;

    private Map<String, Schema> _properties;

    private Boolean _readOnly;

    private String _ref;

    private List<String> _required;

    private String _title;

    private SchemaType _type;

    private Boolean _uniqueItems;

    private Boolean _writeOnly;

    private XML _xml;

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
    public Schema addExtension(final String name, final Object value) {
        _extensible.addExtension(name, value);
        return this;
    }

    @Override
    public void removeExtension(final String name) {
        _extensible.removeExtension(name);
    }

    @Override
    public Object getAdditionalProperties() {
        return _additionalProperties;
    }

    @Override
    @JsonbTransient
    public Schema getAdditionalPropertiesSchema() {
        return Schema.class.isInstance(_additionalProperties) ? Schema.class.cast(_additionalProperties) : null;
    }

    @Override
    @JsonbTransient
    public Boolean getAdditionalPropertiesBoolean() {
        return Boolean.class.isInstance(_additionalProperties) ? Boolean.class.cast(_additionalProperties) : null;
    }

    public void setAdditionalProperties(final Object additionalProperties) {
        _additionalProperties = additionalProperties;
    }

    @Override
    @JsonbTransient
    public void setAdditionalProperties(final Boolean additionalProperties) {
        _additionalProperties = additionalProperties;
    }

    @Override
    @JsonbTransient
    public void setAdditionalPropertiesBoolean(final Boolean additionalProperties) {
        _additionalProperties = additionalProperties;
    }

    @Override
    @JsonbTransient
    public void setAdditionalProperties(final Schema additionalProperties) {
        this._additionalProperties = additionalProperties;
    }

    @Override
    @JsonbTransient
    public void setAdditionalPropertiesSchema(final Schema additionalProperties) {
        this._additionalProperties = additionalProperties;
    }

    @Override
    public Schema additionalProperties(final Schema additionalProperties) {
        _additionalProperties = additionalProperties;
        return this;
    }

    @Override
    public Schema additionalProperties(final Boolean additionalProperties) {
        setAdditionalProperties(additionalProperties);
        return this;
    }

    @Override
    public List<Schema> getAllOf() {
        return _allOf;
    }

    @Override
    public void setAllOf(final List<Schema> _allOf) {
        this._allOf = _allOf;
    }

    @Override
    public Schema allOf(final List<Schema> _allOf) {
        setAllOf(_allOf);
        return this;
    }

    @Override
    public Schema addAllOf(final Schema allOf) {
        if (allOf != null) {
            (_allOf = _allOf == null ? new ArrayList<>() : _allOf).add(allOf);
        }
        return this;
    }

    @Override
    public void removeAllOf(final Schema allOf) {
        _allOf.remove(allOf);
    }

    @Override
    public List<Schema> getAnyOf() {
        return _anyOf;
    }

    @Override
    public void setAnyOf(final List<Schema> _anyOf) {
        this._anyOf = _anyOf;
    }

    @Override
    public Schema anyOf(final List<Schema> _anyOf) {
        setAnyOf(_anyOf);
        return this;
    }

    @Override
    public Schema addAnyOf(final Schema anyOf) {
        if (anyOf != null) {
            (_anyOf = _anyOf == null ? new ArrayList<>() : _anyOf).add(anyOf);
        }
        return this;
    }

    @Override
    public void removeAnyOf(final Schema anyOf) {
        _anyOf.remove(anyOf);
    }

    @Override
    @JsonbProperty("default")
    public Object getDefaultValue() {
        return _defaultValue;
    }

    @Override
    @JsonbProperty("default")
    public void setDefaultValue(final Object _defaultValue) {
        this._defaultValue = _defaultValue;
    }

    @Override
    public Schema defaultValue(final Object _defaultValue) {
        setDefaultValue(_defaultValue);
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
    public Schema deprecated(final Boolean _deprecated) {
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
    public Schema description(final String _description) {
        setDescription(_description);
        return this;
    }

    @Override
    public Discriminator getDiscriminator() {
        return _discriminator;
    }

    @Override
    public void setDiscriminator(final Discriminator _discriminator) {
        this._discriminator = _discriminator;
    }

    @Override
    public Schema discriminator(final Discriminator _discriminator) {
        setDiscriminator(_discriminator);
        return this;
    }

    @Override
    @JsonbProperty("enum")
    public List<Object> getEnumeration() {
        return _enumeration;
    }

    @Override
    @JsonbProperty("enum")
    public void setEnumeration(final List<Object> _enumeration) {
        this._enumeration = _enumeration;
    }

    @Override
    public Schema enumeration(final List<Object> _enumeration) {
        setEnumeration(_enumeration);
        return this;
    }

    @Override
    public Schema addEnumeration(final Object enumeration) {
        if (enumeration != null) {
            (_enumeration = _enumeration == null ? new ArrayList<>() : _enumeration).add(enumeration);
        }
        return this;
    }

    @Override
    public void removeEnumeration(final Object enumeration) {
        _enumeration.remove(enumeration);
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
    public Schema example(final Object _example) {
        setExample(_example);
        return this;
    }

    @Override
    public Boolean getExclusiveMaximum() {
        return _exclusiveMaximum;
    }

    @Override
    public void setExclusiveMaximum(final Boolean _exclusiveMaximum) {
        this._exclusiveMaximum = _exclusiveMaximum;
    }

    @Override
    public Schema exclusiveMaximum(final Boolean _exclusiveMaximum) {
        setExclusiveMaximum(_exclusiveMaximum);
        return this;
    }

    @Override
    public Boolean getExclusiveMinimum() {
        return _exclusiveMinimum;
    }

    @Override
    public void setExclusiveMinimum(final Boolean _exclusiveMinimum) {
        this._exclusiveMinimum = _exclusiveMinimum;
    }

    @Override
    public Schema exclusiveMinimum(final Boolean _exclusiveMinimum) {
        setExclusiveMinimum(_exclusiveMinimum);
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
    public Schema externalDocs(final ExternalDocumentation _externalDocs) {
        setExternalDocs(_externalDocs);
        return this;
    }

    @Override
    public String getFormat() {
        return _format;
    }

    @Override
    public void setFormat(final String _format) {
        this._format = _format;
    }

    @Override
    public Schema format(final String _format) {
        setFormat(_format);
        return this;
    }

    @Override
    public Schema getItems() {
        return _items;
    }

    @Override
    public void setItems(final Schema _items) {
        this._items = _items;
    }

    @Override
    public Schema items(final Schema _items) {
        setItems(_items);
        return this;
    }

    @Override
    public Integer getMaxItems() {
        return _maxItems;
    }

    @Override
    public void setMaxItems(final Integer _maxItems) {
        this._maxItems = _maxItems;
    }

    @Override
    public Schema maxItems(final Integer _maxItems) {
        setMaxItems(_maxItems);
        return this;
    }

    @Override
    public Integer getMaxLength() {
        return _maxLength;
    }

    @Override
    public void setMaxLength(final Integer _maxLength) {
        this._maxLength = _maxLength;
    }

    @Override
    public Schema maxLength(final Integer _maxLength) {
        setMaxLength(_maxLength);
        return this;
    }

    @Override
    public Integer getMaxProperties() {
        return _maxProperties;
    }

    @Override
    public void setMaxProperties(final Integer _maxProperties) {
        this._maxProperties = _maxProperties;
    }

    @Override
    public Schema maxProperties(final Integer _maxProperties) {
        setMaxProperties(_maxProperties);
        return this;
    }

    @Override
    @JsonbTypeAdapter(Serializers.BigDecimalSerializer.class)
    public java.math.BigDecimal getMaximum() {
        return _maximum;
    }

    @Override
    public void setMaximum(final java.math.BigDecimal _maximum) {
        this._maximum = _maximum;
    }

    @Override
    public Schema maximum(final java.math.BigDecimal _maximum) {
        setMaximum(_maximum);
        return this;
    }

    @Override
    public Integer getMinItems() {
        return _minItems;
    }

    @Override
    public void setMinItems(final Integer _minItems) {
        this._minItems = _minItems;
    }

    @Override
    public Schema minItems(final Integer _minItems) {
        setMinItems(_minItems);
        return this;
    }

    @Override
    public Integer getMinLength() {
        return _minLength;
    }

    @Override
    public void setMinLength(final Integer _minLength) {
        this._minLength = _minLength;
    }

    @Override
    public Schema minLength(final Integer _minLength) {
        setMinLength(_minLength);
        return this;
    }

    @Override
    public Integer getMinProperties() {
        return _minProperties;
    }

    @Override
    public void setMinProperties(final Integer _minProperties) {
        this._minProperties = _minProperties;
    }

    @Override
    public Schema minProperties(final Integer _minProperties) {
        setMinProperties(_minProperties);
        return this;
    }

    @Override
    @JsonbTypeAdapter(Serializers.BigDecimalSerializer.class)
    public java.math.BigDecimal getMinimum() {
        return _minimum;
    }

    @Override
    public void setMinimum(final java.math.BigDecimal _minimum) {
        this._minimum = _minimum;
    }

    @Override
    public Schema minimum(final java.math.BigDecimal _minimum) {
        setMinimum(_minimum);
        return this;
    }

    @Override
    @JsonbTypeAdapter(Serializers.BigDecimalSerializer.class)
    public java.math.BigDecimal getMultipleOf() {
        return _multipleOf;
    }

    @Override
    public void setMultipleOf(final java.math.BigDecimal _multipleOf) {
        this._multipleOf = _multipleOf;
    }

    @Override
    public Schema multipleOf(final java.math.BigDecimal _multipleOf) {
        setMultipleOf(_multipleOf);
        return this;
    }

    @Override
    public Schema getNot() {
        return _not;
    }

    @Override
    public void setNot(final Schema _not) {
        this._not = _not;
    }

    @Override
    public Schema not(final Schema _not) {
        setNot(_not);
        return this;
    }

    @Override
    public Boolean getNullable() {
        return _nullable;
    }

    @Override
    public void setNullable(final Boolean _nullable) {
        this._nullable = _nullable;
    }

    @Override
    public Schema nullable(final Boolean _nullable) {
        setNullable(_nullable);
        return this;
    }

    @Override
    public List<Schema> getOneOf() {
        return _oneOf;
    }

    @Override
    public void setOneOf(final List<Schema> _oneOf) {
        this._oneOf = _oneOf;
    }

    @Override
    public Schema oneOf(final List<Schema> _oneOf) {
        setOneOf(_oneOf);
        return this;
    }

    @Override
    public Schema addOneOf(final Schema oneOf) {
        if (oneOf != null) {
            (_oneOf = _oneOf == null ? new ArrayList<>() : _oneOf).add(oneOf);
        }
        return this;
    }

    @Override
    public void removeOneOf(final Schema oneOf) {
        _oneOf.remove(oneOf);
    }

    @Override
    public String getPattern() {
        return _pattern;
    }

    @Override
    public void setPattern(final String _pattern) {
        this._pattern = _pattern;
    }

    @Override
    public Schema pattern(final String _pattern) {
        setPattern(_pattern);
        return this;
    }

    @Override
    public Map<String, Schema> getProperties() {
        return _properties;
    }

    @Override
    public void setProperties(final Map<String, Schema> _properties) {
        this._properties = _properties;
    }

    @Override
    public Schema properties(final Map<String, Schema> _properties) {
        setProperties(_properties);
        return this;
    }

    @Override
    public Schema addProperty(final String key, final Schema _properties) {
        if (_properties != null) {
            (this._properties = this._properties == null ? new LinkedHashMap<>() : this._properties).put(key, _properties);
        }
        return this;
    }

    @Override
    public void removeProperty(final String key) {
        _properties.remove(key);
    }

    @Override
    public Boolean getReadOnly() {
        return _readOnly;
    }

    @Override
    public void setReadOnly(final Boolean _readOnly) {
        this._readOnly = _readOnly;
    }

    @Override
    public Schema readOnly(final Boolean _readOnly) {
        setReadOnly(_readOnly);
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
        this._ref = _ref.startsWith("#") ? _ref : ("#/components/schemas/" + _ref);
    }

    @Override
    public Schema ref(final String _ref) {
        setRef(_ref);
        return this;
    }

    @Override
    public List<String> getRequired() {
        return _required;
    }

    @Override
    public void setRequired(final List<String> _required) {
        this._required = _required;
    }

    @Override
    public Schema required(final List<String> _required) {
        setRequired(_required);
        return this;
    }

    @Override
    public Schema addRequired(final String required) {
        if (required != null) {
            (_required = _required == null ? new ArrayList<>() : _required).add(required);
        }
        return this;
    }

    @Override
    public void removeRequired(final String required) {
        _required.remove(required);
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
    public Schema title(final String _title) {
        setTitle(_title);
        return this;
    }

    @Override
    @JsonbTypeAdapter(Serializers.SchemaTypeSerializer.class)
    public SchemaType getType() {
        return _type;
    }

    @Override
    @JsonbTypeAdapter(Serializers.SchemaTypeSerializer.class)
    public void setType(final SchemaType _type) {
        this._type = _type;
    }

    @Override
    public Schema type(final SchemaType _type) {
        setType(_type);
        return this;
    }

    @Override
    public Boolean getUniqueItems() {
        return _uniqueItems;
    }

    @Override
    public void setUniqueItems(final Boolean _uniqueItems) {
        this._uniqueItems = _uniqueItems;
    }

    @Override
    public Schema uniqueItems(final Boolean _uniqueItems) {
        setUniqueItems(_uniqueItems);
        return this;
    }

    @Override
    public Boolean getWriteOnly() {
        return _writeOnly;
    }

    @Override
    public void setWriteOnly(final Boolean _writeOnly) {
        this._writeOnly = _writeOnly;
    }

    @Override
    public Schema writeOnly(final Boolean _writeOnly) {
        setWriteOnly(_writeOnly);
        return this;
    }

    @Override
    public XML getXml() {
        return _xml;
    }

    @Override
    public void setXml(final XML _xml) {
        this._xml = _xml;
    }

    @Override
    public Schema xml(final XML _xml) {
        setXml(_xml);
        return this;
    }

    @Override
    public String toString() {
        return "SchemaImpl{properties=" + _properties + ", type=" + _type + '}';
    }
}
