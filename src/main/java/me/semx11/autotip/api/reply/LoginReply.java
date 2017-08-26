package me.semx11.autotip.api.reply;

import me.semx11.autotip.api.SessionKey;
import me.semx11.autotip.api.util.RequestType;

public class LoginReply extends AbstractReply {

    private SessionKey sessionKey;

    public LoginReply() {
    }

    public LoginReply(boolean success) {
        super(success);
    }

    public SessionKey getSessionKey() {
        return sessionKey;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.LOGIN;
    }

}
