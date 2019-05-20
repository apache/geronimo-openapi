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

import org.eclipse.microprofile.openapi.models.Extensible;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;
import org.eclipse.microprofile.openapi.models.responses.APIResponses;

@Vetoed
public class APIResponsesImpl extends APIMap<String, APIResponse> implements APIResponses {
    private Extensible<APIResponse> _extensible = new ExtensibleImpl<>();

    public APIResponses addAPIResponse(final String name, final APIResponse item) {
        return addApiResponse(name, item);
    }

    @Override
    public void removeAPIResponse(final String name) {
        remove(name);
    }

    @Override
    public Map<String, APIResponse> getAPIResponses() {
        return new LinkedHashMap<>(this);
    }

    @Override
    public void setAPIResponses(final Map<String, APIResponse> items) {
        clear();
        putAll(items);
    }

    @Override
    public APIResponses addApiResponse(final String name, final APIResponse item) {
        if (item != null) {
            super.put(name, item);
        }
        return this;
    }

    @Override
    @JsonbTransient
    public APIResponse getDefault() {
        return get("default");
    }

    @Override
    public APIResponse getDefaultValue() {
        return getDefault();
    }

    @Override
    public void setDefaultValue(final APIResponse _defaultValue) {
        if (_defaultValue == null) {
            removeAPIResponse(APIResponses.DEFAULT);
        } else {
            addApiResponse(APIResponses.DEFAULT, _defaultValue);
        }
    }

    @Override
    public APIResponses defaultValue(final APIResponse _defaultValue) {
        setDefaultValue(_defaultValue);
        return this;
    }

    @Override
    @JsonbTransient
    public Map<String, Object> getExtensions() {
        return _extensible.getExtensions();
    }

    @Override
    public APIResponses addExtension(final String name, final Object value) {
        _extensible.addExtension(name, value);
        return this;
    }

    @Override
    public void removeExtension(final String name) {
        _extensible.removeExtension(name);
    }

    @Override
    public void setExtensions(final Map<String, Object> extensions) {
        _extensible.setExtensions(extensions);
    }
}
