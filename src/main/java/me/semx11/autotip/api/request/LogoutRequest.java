package me.semx11.autotip.api.request;

import java.util.Optional;
import me.semx11.autotip.api.reply.AbstractReply;
import me.semx11.autotip.api.reply.LogoutReply;
import me.semx11.autotip.api.util.GetBuilder;
import me.semx11.autotip.api.util.RequestHandler;
import me.semx11.autotip.api.util.RequestType;
import org.apache.http.client.methods.HttpUriRequest;

public class LogoutRequest extends AbstractRequest {

    private LogoutRequest() {
    }

    public static LogoutReply doRequest(String sessionKey) {
        LogoutRequest request = new LogoutRequest();

        HttpUriRequest uri = GetBuilder.of(request)
                .addParameter("key", sessionKey)
                .build();

        Optional<AbstractReply> optional = RequestHandler.getReply(request, uri);
        return optional
                .map(reply -> (LogoutReply) reply)
                .orElseGet(() -> new LogoutReply(false));
    }

    @Override
    public RequestType getType() {
        return RequestType.LOGOUT;
    }

}
