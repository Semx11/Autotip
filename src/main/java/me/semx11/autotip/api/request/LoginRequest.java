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

public class LoginRequest extends AbstractRequest<LoginReply> {

    private final Session session;
    private final String hash;
    private final int tips;

    private LoginRequest(Session session, String hash, int tips) {
        this.session = session;
        this.hash = hash;
        this.tips = tips;
    }

    public static LoginRequest of(Session session, String hash, int tips) {
        return new LoginRequest(session, hash, tips);
    }

    @Override
    public LoginReply execute() {
        HttpUriRequest uri = GetBuilder.of(this)
                .addParameter("username", this.session.getProfile().getName())
                .addParameter("uuid", this.session.getProfile().getId())
                .addParameter("tips", this.tips)
                .addParameter("v", Autotip.VERSION)
                .addParameter("mc", Autotip.MC_VERSION)
                .addParameter("os", System.getProperty("os.name"))
                .addParameter("hash", this.hash)
                .build();

        Optional<AbstractReply> optional = RequestHandler.getReply(this, uri);
        return optional
                .map(reply -> (LoginReply) reply)
                .orElseGet(() -> new LoginReply(false));
    }

    @Override
    public RequestType getType() {
        return RequestType.LOGIN;
    }

}
