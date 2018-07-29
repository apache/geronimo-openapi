package org.apache.geronimo.microprofile.openapi.impl.model.codec;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import javax.enterprise.inject.Vetoed;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

import org.apache.geronimo.microprofile.openapi.impl.model.APIResponseImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.CallbackImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.EncodingImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ExampleImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.HeaderImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.LinkImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.ParameterImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.RequestBodyImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.SchemaImpl;
import org.apache.geronimo.microprofile.openapi.impl.model.SecuritySchemeImpl;
import org.eclipse.microprofile.openapi.models.callbacks.Callback;
import org.eclipse.microprofile.openapi.models.examples.Example;
import org.eclipse.microprofile.openapi.models.headers.Header;
import org.eclipse.microprofile.openapi.models.links.Link;
import org.eclipse.microprofile.openapi.models.media.Encoding;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.eclipse.microprofile.openapi.models.parameters.RequestBody;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;
import org.eclipse.microprofile.openapi.models.security.SecurityScheme;

@Vetoed
public final class Deserializers {

    private Deserializers() {
        // no-op
    }

    private static Type mapType(final Class<?> value) {
        final Type[] args = new Type[] { String.class, value };
        return new ParameterizedType() {

            @Override
            public Type[] getActualTypeArguments() {
                return args;
            }

            @Override
            public Type getRawType() {
                return Map.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            };
        };
    }

    @Vetoed
    public static class MapHeadersDeserializer extends BaseDeserializer<Map<String, Header>> {
        public MapHeadersDeserializer() {
            super(mapType(HeaderImpl.class));
        }
    }

    @Vetoed
    public static class MapLinksDeserializer extends BaseDeserializer<Map<String, Link>> {
        public MapLinksDeserializer() {
            super(mapType(LinkImpl.class));
        }
    }

    @Vetoed
    public static class MapCallbacksDeserializer extends BaseDeserializer<Map<String, Callback>> {
        public MapCallbacksDeserializer() {
            super(mapType(CallbackImpl.class));
        }
    }

    @Vetoed
    public static class MapExamplesDeserializer extends BaseDeserializer<Map<String, Example>> {
        public MapExamplesDeserializer() {
            super(mapType(ExampleImpl.class));
        }
    }

    @Vetoed
    public static class MapParametersDeserializer extends BaseDeserializer<Map<String, Parameter>> {
        public MapParametersDeserializer() {
            super(mapType(ParameterImpl.class));
        }
    }

    @Vetoed
    public static class MapRequestBodiesDeserializer extends BaseDeserializer<Map<String, RequestBody>> {
        public MapRequestBodiesDeserializer() {
            super(mapType(RequestBodyImpl.class));
        }
    }

    @Vetoed
    public static class MapAPIResponsesDeserializer extends BaseDeserializer<Map<String, APIResponse>> {
        public MapAPIResponsesDeserializer() {
            super(mapType(APIResponseImpl.class));
        }
    }

    @Vetoed
    public static class MapSchemasDeserializer extends BaseDeserializer<Map<String, Schema>> {
        public MapSchemasDeserializer() {
            super(mapType(SchemaImpl.class));
        }
    }

    @Vetoed
    public static class MapSecuritySchemesDeserializer extends BaseDeserializer<Map<String, SecurityScheme>> {
        public MapSecuritySchemesDeserializer() {
            super(mapType(SecuritySchemeImpl.class));
        }
    }

    @Vetoed
    public static class MapEncodingsDeserializer extends BaseDeserializer<Map<String, Encoding>> {
        public MapEncodingsDeserializer() {
            super(mapType(EncodingImpl.class));
        }
    }

    @Vetoed
    protected static class BaseDeserializer<T> implements JsonbDeserializer<T> {

        private final Type actualType;

        protected BaseDeserializer(final Type actualType) {
            this.actualType = actualType;
        }

        @Override
        public T deserialize(final JsonParser parser, final DeserializationContext ctx, final Type rtType) {
            return ctx.deserialize(actualType, parser);
        }
    }
}
