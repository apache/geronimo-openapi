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
import org.eclipse.microprofile.openapi.models.examples.Example;
import org.eclipse.microprofile.openapi.models.media.Encoding;
import org.eclipse.microprofile.openapi.models.media.MediaType;
import org.eclipse.microprofile.openapi.models.media.Schema;

@Vetoed
public class MediaTypeImpl implements MediaType {

    private Extensible _extensible = new ExtensibleImpl();

    private Map<String, Encoding> _encoding;

    private Object _example;

    private Map<String, Example> _examples;

    private Schema _schema;

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
    public MediaType addExtension(final String name, final Object value) {
        _extensible.addExtension(name, value);
        return this;
    }

    @Override
    public void removeExtension(final String name) {
        _extensible.removeExtension(name);
    }

    @Override
    public Map<String, Encoding> getEncoding() {
        return _encoding;
    }

    @Override
    @JsonbTypeDeserializer(Deserializers.MapEncodingsDeserializer.class)
    public void setEncoding(final Map<String, Encoding> _encoding) {
        this._encoding = _encoding;
    }

    @Override
    public MediaType encoding(final Map<String, Encoding> _encoding) {
        setEncoding(_encoding);
        return this;
    }

    @Override
    public MediaType addEncoding(final String key, final Encoding _encoding) {
        if (_encoding != null) {
            (this._encoding = this._encoding == null ? new LinkedHashMap<>() : this._encoding).put(key, _encoding);
        }
        return this;
    }

    @Override
    public void removeEncoding(final String key) {
        _encoding.remove(key);
    }

    @Override
    public Object getExample() {
        return _example;
    }

    @Override
    @JsonbTypeDeserializer(Deserializers.MapExamplesDeserializer.class)
    public void setExample(final Object _example) {
        this._example = _example;
    }

    @Override
    public MediaType example(final Object _example) {
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
    public MediaType examples(final Map<String, Example> _examples) {
        setExamples(_examples);
        return this;
    }

    @Override
    public MediaType addExample(final String key, final Example _examples) {
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
    public Schema getSchema() {
        return _schema != null ? _schema : (_schema = new SchemaImpl());
    }

    @Override
    public void setSchema(final Schema _schema) {
        this._schema = _schema;
    }

    @Override
    public MediaType schema(final Schema _schema) {
        setSchema(_schema);
        return this;
    }
}
