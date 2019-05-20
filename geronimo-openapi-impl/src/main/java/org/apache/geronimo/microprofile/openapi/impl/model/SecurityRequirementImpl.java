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

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Vetoed;

import org.eclipse.microprofile.openapi.models.security.SecurityRequirement;

@Vetoed
public class SecurityRequirementImpl extends APIMap<String, List<String>> implements SecurityRequirement {
    @Override
    public SecurityRequirement addScheme(final String securitySchemeName, final String scope) {
        return addScheme(securitySchemeName, scope == null ? emptyList() : singletonList(scope));
    }

    @Override
    public SecurityRequirement addScheme(final String securitySchemeName, final List<String> scopes) {
        super.put(securitySchemeName, scopes == null ? emptyList() : scopes);
        return this;
    }

    @Override
    public SecurityRequirement addScheme(final String securitySchemeName) {
        return addScheme(securitySchemeName, emptyList());
    }

    @Override
    public void removeScheme(final String securitySchemeName) {
        remove(securitySchemeName);
    }

    @Override
    public Map<String, List<String>> getSchemes() {
        return new LinkedHashMap<>(this);
    }

    @Override
    public void setSchemes(final Map<String, List<String>> items) {
        clear();
        putAll(items);
    }
}
