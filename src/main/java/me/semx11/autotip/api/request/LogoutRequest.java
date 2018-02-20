package me.semx11.autotip.api.request;

import java.util.Optional;
import me.semx11.autotip.api.SessionKey;
import me.semx11.autotip.api.reply.AbstractReply;
import me.semx11.autotip.api.reply.LogoutReply;
import me.semx11.autotip.api.util.GetBuilder;
import me.semx11.autotip.api.util.RequestHandler;
import me.semx11.autotip.api.util.RequestType;
import org.apache.http.client.methods.HttpUriRequest;

public class LogoutRequest extends AbstractRequest<LogoutReply> {

    private final SessionKey sessionKey;

    private LogoutRequest(SessionKey sessionKey) {
        this.sessionKey = sessionKey;
    }

    public static LogoutRequest of(SessionKey sessionKey) {
        return new LogoutRequest(sessionKey);
    }

    @Override
    public LogoutReply execute() {
        HttpUriRequest request = GetBuilder.of(this)
                .addParameter("key", this.sessionKey)
                .build();

        Optional<AbstractReply<AbstractReply>> optional = RequestHandler.getReply(this, request.getURI());
        return optional
                .map(reply -> (LogoutReply) reply)
                .orElseGet(() -> new LogoutReply(false));
    }

    @Override
    public RequestType getType() {
        return RequestType.LOGOUT;
    }

}
