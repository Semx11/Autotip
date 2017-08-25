package me.semx11.autotip.api.request;

import java.util.Optional;
import me.semx11.autotip.api.reply.AbstractReply;
import me.semx11.autotip.api.reply.KeepAliveReply;
import me.semx11.autotip.api.util.GetBuilder;
import me.semx11.autotip.api.util.RequestHandler;
import me.semx11.autotip.api.util.RequestType;
import org.apache.http.client.methods.HttpUriRequest;

public class KeepAliveRequest extends AbstractRequest {

    private KeepAliveRequest() {
    }

    public static KeepAliveReply doRequest(String sessionKey) {
        KeepAliveRequest request = new KeepAliveRequest();

        HttpUriRequest uri = GetBuilder.of(request)
                .addParameter("key", sessionKey)
                .build();

        Optional<AbstractReply> optional = RequestHandler.getReply(request, uri);
        return optional
                .map(reply -> (KeepAliveReply) reply)
                .orElseGet(() -> new KeepAliveReply(false));
    }

    @Override
    public RequestType getType() {
        return RequestType.KEEP_ALIVE;
    }

}
