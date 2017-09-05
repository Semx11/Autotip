package me.semx11.autotip.api.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.api.SessionKey;
import me.semx11.autotip.api.reply.AbstractReply;
import me.semx11.autotip.api.request.AbstractRequest;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class RequestHandler {

    private static final HttpClient HTTP_CLIENT = HttpClientBuilder.create().build();
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(SessionKey.class, new SessionKey.JsonAdapter())
            .create();

    public static Optional<AbstractReply> getReply(AbstractRequest request, HttpUriRequest uri) {
        String json = null;
        try {
            HttpEntity entity = HTTP_CLIENT.execute(uri).getEntity();
            json = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            Autotip.LOGGER.info(request.getType() + " JSON: " + json);

            AbstractReply reply = GSON.fromJson(json, (Type) request.getType().getReplyClass());

            return Optional.of(reply);
        } catch (IOException | JsonParseException e) {
            e.printStackTrace();
            Autotip.LOGGER.info(request.getType() + " JSON: " + json);
            return Optional.empty();
        }
    }

}
