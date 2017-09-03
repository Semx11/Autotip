package me.semx11.autotip.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class SessionKey {

    private final String key;

    private SessionKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return key;
    }

    public static class JsonAdapter implements JsonSerializer<SessionKey>,
            JsonDeserializer<SessionKey> {

        @Override
        public SessionKey deserialize(JsonElement json, Type t, JsonDeserializationContext ctx)
                throws JsonParseException {
            return new SessionKey(json.getAsString());
        }

        @Override
        public JsonElement serialize(SessionKey src, Type t, JsonSerializationContext ctx) {
            return new JsonPrimitive(src.getKey());
        }

    }

}
