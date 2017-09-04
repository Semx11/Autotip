package me.semx11.autotip.api.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.api.SessionKey;
import me.semx11.autotip.api.reply.AbstractReply;
import me.semx11.autotip.api.request.AbstractRequest;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

public class RequestHandler {

    private static final String BASE_URL = "https://api.autotip.pro/";

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private static final HttpClient HTTP_CLIENT = HttpClientBuilder.create().build();
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(SessionKey.class, new SessionKey.JsonAdapter())
            .create();

    public static Optional<AbstractReply> getReply(AbstractRequest request, HttpUriRequest uri) {
        String json = null;
        try {
            HttpEntity entity = execute(uri).get(10, TimeUnit.SECONDS).getEntity();
            json = EntityUtils.toString(entity, StandardCharsets.UTF_8);

            AbstractReply reply = GSON.fromJson(json, (Type) request.getType().getReplyClass());

            return Optional.of(reply);
        } catch (InterruptedException | ExecutionException | IOException | JsonParseException | TimeoutException e) {
            e.printStackTrace();
            Autotip.LOGGER.info("JSON: " + json);
            return Optional.empty();
        }
    }

    private static Future<HttpResponse> execute(HttpUriRequest request) {
        return EXECUTOR.submit(() -> HTTP_CLIENT.execute(request));
    }

}
