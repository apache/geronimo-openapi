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
import java.util.Objects;

import javax.enterprise.inject.Vetoed;
import javax.json.bind.annotation.JsonbTransient;

import org.eclipse.microprofile.openapi.models.Extensible;

@Vetoed
public class ExtensibleImpl<T extends Extensible<T>> implements Extensible<T> {

    private Map<String, Object> _extensions;

    @Override
    @JsonbTransient
    public Map<String, Object> getExtensions() {
        return _extensions;
    }

    @Override
    public T addExtension(final String key, final Object _extensions) {
        if (_extensions == null) {
            return (T) this;
        }
        (this._extensions = this._extensions == null ? new LinkedHashMap<>() : this._extensions)
                .put((key.startsWith("x-") ? key : ("x-" + key)), _extensions);
        return (T) this;
    }

    @Override
    public void removeExtension(final String name) {
        _extensions.remove(name);
    }

    @Override
    public void setExtensions(final Map<String, Object> _extensions) {
        this._extensions = _extensions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return Objects.equals(_extensions, ExtensibleImpl.class.cast(o)._extensions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_extensions);
    }
}
