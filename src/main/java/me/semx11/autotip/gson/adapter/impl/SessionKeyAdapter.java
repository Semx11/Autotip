package me.semx11.autotip.gson.adapter.impl;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import me.semx11.autotip.api.SessionKey;
import me.semx11.autotip.gson.adapter.TypeAdapter;

public class SessionKeyAdapter implements TypeAdapter<SessionKey> {

    @Override
    public SessionKey deserialize(JsonElement json, java.lang.reflect.Type t,
            JsonDeserializationContext ctx)
            throws JsonParseException {
        return new SessionKey(json.getAsString());
    }

    @Override
    public JsonElement serialize(SessionKey src, java.lang.reflect.Type t,
            JsonSerializationContext ctx) {
        return new JsonPrimitive(src.getKey());
    }

}
