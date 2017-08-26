package me.semx11.autotip.api.request;

import java.util.Optional;
import me.semx11.autotip.api.SessionKey;
import me.semx11.autotip.api.reply.AbstractReply;
import me.semx11.autotip.api.reply.TipReply;
import me.semx11.autotip.api.util.GetBuilder;
import me.semx11.autotip.api.util.RequestHandler;
import me.semx11.autotip.api.util.RequestType;
import org.apache.http.client.methods.HttpUriRequest;

public class TipRequest extends AbstractRequest {

    private TipRequest() {
    }

    public static TipReply doRequest(SessionKey sessionKey) {
        TipRequest request = new TipRequest();

        HttpUriRequest uri = GetBuilder.of(request)
                .addParameter("key", sessionKey)
                .build();

        Optional<AbstractReply> optional = RequestHandler.getReply(request, uri);
        return optional
                .map(reply -> (TipReply) reply)
                .orElseGet(TipReply::getDefault);
    }

    @Override
    public RequestType getType() {
        return RequestType.TIP;
    }

}
