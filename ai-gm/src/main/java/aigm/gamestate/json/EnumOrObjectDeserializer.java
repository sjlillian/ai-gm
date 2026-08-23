package aigm.gamestate.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * Catalog types are either a named enum (JSON string) or a custom record (JSON object).
 * Temporal's default Jackson payload converter cannot construct interface fields without this.
 */
abstract class EnumOrObjectDeserializer<I, E extends Enum<E>, C extends I>
        extends JsonDeserializer<I> {

    private final Class<E> enumType;
    private final Class<C> objectType;

    protected EnumOrObjectDeserializer(Class<E> enumType, Class<C> objectType) {
        this.enumType = enumType;
        this.objectType = objectType;
    }

    @Override
    @SuppressWarnings("unchecked")
    public I deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken token = p.currentToken();
        if (token == JsonToken.VALUE_NULL) {
            return getNullValue(ctxt);
        }
        if (token == JsonToken.VALUE_STRING) {
            String name = p.getText();
            try {
                return (I) Enum.valueOf(enumType, name);
            } catch (IllegalArgumentException e) {
                return (I) ctxt.handleWeirdStringValue(enumType, name, e.getMessage());
            }
        }
        if (token == JsonToken.START_OBJECT) {
            return ctxt.readValue(p, objectType);
        }
        return (I) ctxt.handleUnexpectedToken(enumType, p);
    }
}
