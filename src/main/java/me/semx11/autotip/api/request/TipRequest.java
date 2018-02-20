package me.semx11.autotip.api.request;

import java.util.Optional;
import me.semx11.autotip.api.SessionKey;
import me.semx11.autotip.api.reply.AbstractReply;
import me.semx11.autotip.api.reply.TipReply;
import me.semx11.autotip.api.util.GetBuilder;
import me.semx11.autotip.api.util.RequestHandler;
import me.semx11.autotip.api.util.RequestType;
import org.apache.http.client.methods.HttpUriRequest;

public class TipRequest extends AbstractRequest<TipReply> {

    private final SessionKey sessionKey;

    private TipRequest(SessionKey sessionKey) {
        this.sessionKey = sessionKey;
    }

    public static TipRequest of(SessionKey sessionKey) {
        return new TipRequest(sessionKey);
    }

    @Override
    public TipReply execute() {
        HttpUriRequest request = GetBuilder.of(this)
                .addParameter("key", this.sessionKey)
                .build();

        Optional<AbstractReply<AbstractReply>> optional = RequestHandler.getReply(this, request.getURI());
        return optional
                .map(reply -> (TipReply) reply)
                .orElseGet(TipReply::getDefault);
    }

    @Override
    public RequestType getType() {
        return RequestType.TIP;
    }

}
