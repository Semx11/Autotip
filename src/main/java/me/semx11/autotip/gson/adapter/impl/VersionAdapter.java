package me.semx11.autotip.gson.adapter.impl;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import me.semx11.autotip.gson.adapter.TypeAdapter;
import me.semx11.autotip.util.Version;

public class VersionAdapter implements TypeAdapter<Version> {

    @Override
    public Version deserialize(JsonElement json, java.lang.reflect.Type type,
            JsonDeserializationContext ctx)
            throws JsonParseException {
        return new Version(json.getAsString());
    }

    @Override
    public JsonElement serialize(Version src, java.lang.reflect.Type type,
            JsonSerializationContext ctx) {
        return new JsonPrimitive(src.get());
    }

}
