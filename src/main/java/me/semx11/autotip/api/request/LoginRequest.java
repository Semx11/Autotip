package me.semx11.autotip.api.request;

import com.mojang.authlib.GameProfile;
import java.util.Optional;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.api.reply.AbstractReply;
import me.semx11.autotip.api.reply.LoginReply;
import me.semx11.autotip.api.util.GetBuilder;
import me.semx11.autotip.api.util.RequestHandler;
import me.semx11.autotip.api.util.RequestType;
import org.apache.http.client.methods.HttpUriRequest;

public class LoginRequest extends AbstractRequest<LoginReply> {

    private final GameProfile profile;
    private final String hash;
    private final int tips;

    private LoginRequest(GameProfile profile, String hash, int tips) {
        this.profile = profile;
        this.hash = hash;
        this.tips = tips;
    }

    public static LoginRequest of(GameProfile profile, String hash, int tips) {
        return new LoginRequest(profile, hash, tips);
    }

    @Override
    public LoginReply execute() {
        HttpUriRequest request = GetBuilder.of(this)
                .addParameter("username", this.profile.getName())
                .addParameter("uuid", this.profile.getId().toString().replace("-", ""))
                .addParameter("tips", this.tips)
                .addParameter("v", Autotip.VERSION)
                .addParameter("mc", Autotip.MC_VERSION)
                .addParameter("os", System.getProperty("os.name"))
                .addParameter("hash", this.hash)
                .build();

        Optional<AbstractReply> optional = RequestHandler.getReply(this, request.getURI());
        return optional
                .map(reply -> (LoginReply) reply)
                .orElseGet(() -> new LoginReply(false));
    }

    @Override
    public RequestType getType() {
        return RequestType.LOGIN;
    }

}
