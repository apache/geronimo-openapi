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

import org.eclipse.microprofile.openapi.models.media.Content;
import org.eclipse.microprofile.openapi.models.media.MediaType;

@Vetoed
public class ContentImpl extends APIMap<String, MediaType> implements Content {

    @Override
    public Content addMediaType(final String name, final MediaType item) {
        if (item != null) {
            super.put(name, item);
        }
        return this;
    }

    @Override
    public void removeMediaType(final String name) {
        remove(name);
    }

    @Override
    public Map<String, MediaType> getMediaTypes() {
        return new LinkedHashMap<>(this);
    }

    @Override
    public void setMediaTypes(final Map<String, MediaType> mediaTypes) {
        clear();
        putAll(mediaTypes);
    }
}
