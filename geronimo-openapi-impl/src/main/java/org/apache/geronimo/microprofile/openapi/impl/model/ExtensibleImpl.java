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

import org.eclipse.microprofile.openapi.models.Extensible;

@Vetoed
public class ExtensibleImpl implements Extensible {

    private Map<String, Object> _extensions;

    @Override
    public Map<String, Object> getExtensions() {
        return _extensions;
    }

    @Override
    public void setExtensions(final Map<String, Object> _extensions) {
        this._extensions = _extensions;
    }

    @Override
    public void addExtension(final String key, final Object _extensions) {
        (this._extensions = this._extensions == null ? new LinkedHashMap<>() : this._extensions)
                .put((key.startsWith("x-") ? key : ("x-" + key)), _extensions);
    }
}
