package me.semx11.autotip.api.request;

import java.util.Optional;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.api.reply.AbstractReply;
import me.semx11.autotip.api.reply.LoginReply;
import me.semx11.autotip.api.util.GetBuilder;
import me.semx11.autotip.api.util.RequestHandler;
import me.semx11.autotip.api.util.RequestType;
import net.minecraft.util.Session;
import org.apache.http.client.methods.HttpUriRequest;

public class LoginRequest extends AbstractRequest {

    private LoginRequest() {
    }

    public static LoginReply doRequest(Session session, String hash, int tips) {
        LoginRequest request = new LoginRequest();

        HttpUriRequest uri = GetBuilder.of(request)
                .addParameter("username", session.getProfile().getName())
                .addParameter("uuid", session.getProfile().getId())
                .addParameter("tips", tips)
                .addParameter("v", Autotip.VERSION)
                .addParameter("mc", Autotip.MC_VERSION)
                .addParameter("os", System.getProperty("os.name"))
                .addParameter("hash", hash)
                .build();

        Optional<AbstractReply> optional = RequestHandler.getReply(request, uri);
        return optional
                .map(reply -> (LoginReply) reply)
                .orElseGet(() -> new LoginReply(false));
    }

    @Override
    public RequestType getType() {
        return RequestType.LOGIN;
    }

}
