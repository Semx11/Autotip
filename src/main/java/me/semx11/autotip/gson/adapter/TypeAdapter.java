package me.semx11.autotip.gson.adapter;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

public interface TypeAdapter<T> extends JsonSerializer<T>, JsonDeserializer<T> {

}
