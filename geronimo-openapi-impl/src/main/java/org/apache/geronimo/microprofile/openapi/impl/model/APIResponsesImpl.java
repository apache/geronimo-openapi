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

import javax.enterprise.inject.Vetoed;
import javax.json.bind.annotation.JsonbProperty;

import org.eclipse.microprofile.openapi.models.responses.APIResponse;
import org.eclipse.microprofile.openapi.models.responses.APIResponses;

@Vetoed
public class APIResponsesImpl extends LinkedHashMap<String, APIResponse> implements APIResponses {

    private APIResponse _default;

    public APIResponses addAPIResponse(final String name, final APIResponse item) {
        return addApiResponse(name, item);
    }

    @Override
    public APIResponses addApiResponse(final String name, final APIResponse item) {
        this.put(name, item);
        return this;
    }

    @Override
    public APIResponse getDefault() {
        return _default;
    }

    @Override
    @JsonbProperty("default")
    public void setDefaultValue(final APIResponse _defaultValue) {
        this._default = _defaultValue;
    }

    @Override
    public APIResponses defaultValue(final APIResponse _defaultValue) {
        setDefaultValue(_defaultValue);
        return this;
    }
}
