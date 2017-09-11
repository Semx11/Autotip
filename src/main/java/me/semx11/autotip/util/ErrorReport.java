package me.semx11.autotip.util;

import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import me.semx11.autotip.Autotip;
import net.minecraftforge.common.ForgeVersion;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class ErrorReport {

    public static void reportException(Throwable t) {
        Autotip.LOGGER.error(t.getMessage(), t);
        try {
            URL url = new URL("https://api.autotip.pro/error_report.php");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            JsonObject obj = JsonObjectBuilder.newBuilder()
                    .addString("username", Autotip.MC.getSession().getProfile().getName())
                    .addString("uuid", Autotip.MC.getSession().getProfile().getId())
                    .addString("v", Autotip.VERSION)
                    .addString("mc", Autotip.MC_VERSION)
                    .addString("os", System.getProperty("os.name"))
                    .addString("forge", ForgeVersion.getVersion())
                    .addString("sessionKey", Autotip.SESSION_MANAGER.getKey())
                    .addString("stackTrace", ExceptionUtils.getStackTrace(t))
                    .addNumber("time", System.currentTimeMillis())
                    .build();

            byte[] jsonBytes = obj.toString().getBytes(StandardCharsets.UTF_8);

            conn.setFixedLengthStreamingMode(jsonBytes.length);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.connect();

            try (OutputStream out = conn.getOutputStream()) {
                out.write(jsonBytes);
            }

            InputStream input;
            if (conn.getResponseCode() < HttpURLConnection.HTTP_BAD_REQUEST) {
                input = conn.getInputStream();
            } else {
                input = conn.getErrorStream();
            }
            String json = IOUtils.toString(input, StandardCharsets.UTF_8);
            Autotip.LOGGER.info("Error JSON: " + json);

        } catch (IOException e) {
            // Hmm... what would happen if I were to report this one?
            e.printStackTrace();
        }
    }

    private static class JsonObjectBuilder {

        private final JsonObject obj;

        private JsonObjectBuilder(JsonObject obj) {
            this.obj = obj;
        }

        public static JsonObjectBuilder newBuilder() {
            return new JsonObjectBuilder(new JsonObject());
        }

        public JsonObjectBuilder addString(String property, Object value) {
            obj.addProperty(property, value != null ? value.toString() : "null");
            return this;
        }

        public JsonObjectBuilder addNumber(String property, Number value) {
            obj.addProperty(property, value);
            return this;
        }

        public JsonObject build() {
            return obj;
        }

    }

}
