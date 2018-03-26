package me.semx11.autotip.api.request.impl;

import com.mojang.authlib.GameProfile;
import java.util.Optional;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.api.reply.Reply;
import me.semx11.autotip.api.reply.impl.LoginReply;
import me.semx11.autotip.api.request.Request;
import me.semx11.autotip.api.GetBuilder;
import me.semx11.autotip.api.RequestHandler;
import me.semx11.autotip.api.RequestType;
import org.apache.http.client.methods.HttpUriRequest;

public class LoginRequest implements Request<LoginReply> {

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
        Autotip autotip = Autotip.getInstance();
        HttpUriRequest request = GetBuilder.of(this)
                .addParameter("username", this.profile.getName())
                .addParameter("uuid", this.profile.getId().toString().replace("-", ""))
                .addParameter("tips", this.tips)
                .addParameter("v", autotip.getVersion())
                .addParameter("mc", autotip.getMcVersion())
                .addParameter("os", System.getProperty("os.name"))
                .addParameter("hash", this.hash)
                .build();

        Optional<Reply> optional = RequestHandler.getReply(this, request.getURI());
        return optional
                .map(reply -> (LoginReply) reply)
                .orElseGet(() -> new LoginReply(false));
    }

    @Override
    public RequestType getType() {
        return RequestType.LOGIN;
    }

}
