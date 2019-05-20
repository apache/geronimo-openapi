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

import org.eclipse.microprofile.openapi.models.media.Discriminator;

@Vetoed
public class DiscriminatorImpl implements Discriminator {

    private Map<String, String> _mapping;

    private String _propertyName;

    @Override
    public Map<String, String> getMapping() {
        return _mapping;
    }

    @Override
    public void setMapping(final Map<String, String> _mapping) {
        this._mapping = _mapping;
    }

    @Override
    public Discriminator mapping(final Map<String, String> _mapping) {
        setMapping(_mapping);
        return this;
    }

    @Override
    public Discriminator addMapping(final String key, final String _mapping) {
        if (_mapping != null) {
            (this._mapping = this._mapping == null ? new LinkedHashMap<>() : this._mapping).put(key, _mapping);
        }
        return this;
    }

    @Override
    public void removeMapping(final String name) {
        _mapping.remove(name);
    }

    @Override
    public String getPropertyName() {
        return _propertyName;
    }

    @Override
    public void setPropertyName(final String _propertyName) {
        this._propertyName = _propertyName;
    }

    @Override
    public Discriminator propertyName(final String _propertyName) {
        setPropertyName(_propertyName);
        return this;
    }
}
