package me.semx11.autotip.api.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.api.SessionKey;
import me.semx11.autotip.api.reply.AbstractReply;
import me.semx11.autotip.api.request.AbstractRequest;
import org.apache.commons.io.IOUtils;

public class RequestHandler {

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(SessionKey.class, new SessionKey.JsonAdapter())
            .create();

    public static Optional<AbstractReply> getReply(AbstractRequest request, URI uri) {
        String json = null;
        try {
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestProperty("User-Agent", "Autotip v" + Autotip.VERSION);

            InputStream input;
            if (conn.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                input = conn.getInputStream();
            } else {
                input = conn.getErrorStream();
            }
            json = IOUtils.toString(input, StandardCharsets.UTF_8);
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
