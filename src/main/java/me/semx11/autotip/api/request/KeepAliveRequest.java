package me.semx11.autotip.api.request;

import java.util.Optional;
import me.semx11.autotip.api.SessionKey;
import me.semx11.autotip.api.reply.AbstractReply;
import me.semx11.autotip.api.reply.KeepAliveReply;
import me.semx11.autotip.api.util.GetBuilder;
import me.semx11.autotip.api.util.RequestHandler;
import me.semx11.autotip.api.util.RequestType;
import org.apache.http.client.methods.HttpUriRequest;

public class KeepAliveRequest extends AbstractRequest<KeepAliveReply> {

    private final SessionKey sessionKey;

    private KeepAliveRequest(SessionKey sessionKey) {
        this.sessionKey = sessionKey;
    }

    public static KeepAliveRequest of(SessionKey sessionKey) {
        return new KeepAliveRequest(sessionKey);
    }

    @Override
    public KeepAliveReply execute() {
        HttpUriRequest uri = GetBuilder.of(this)
                .addParameter("key", this.sessionKey)
                .build();

        Optional<AbstractReply> optional = RequestHandler.getReply(this, uri);
        return optional
                .map(reply -> (KeepAliveReply) reply)
                .orElseGet(() -> new KeepAliveReply(false));
    }

    @Override
    public RequestType getType() {
        return RequestType.KEEP_ALIVE;
    }

}
