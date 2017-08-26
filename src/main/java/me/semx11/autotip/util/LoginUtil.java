package me.semx11.autotip.util;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

public class LoginUtil {

    private static final HttpClient HTTP_CLIENT = HttpClientBuilder.create().build();

    private static SecureRandom random = new SecureRandom();

    public static String getNextSalt() {
        return new BigInteger(130, random).toString(32);
    }

    public static String hash(String str) {
        try {
            byte[] digest = digest(str, "SHA-1");
            return new BigInteger(digest).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] digest(String str, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
        return md.digest(strBytes);
    }

    public static int joinServer(String token, String uuid, String serverHash) {
        try {
            HttpPost request = new HttpPost("https://sessionserver.mojang.com/session/"
                    + "minecraft/join");

            JsonObject obj = new JsonObject();
            obj.addProperty("accessToken", token);
            obj.addProperty("selectedProfile", uuid);
            obj.addProperty("serverId", serverHash);

            request.setEntity(new StringEntity(obj.toString()));
            request.addHeader("Content-Type", "application/json");

            HttpResponse response = HTTP_CLIENT.execute(request);

            return response.getStatusLine().getStatusCode();
        } catch (IOException e) {
            e.printStackTrace();
            return HttpStatus.SC_BAD_REQUEST;
        }
    }

}
