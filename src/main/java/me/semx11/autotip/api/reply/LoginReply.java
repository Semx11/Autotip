package me.semx11.autotip.api.reply;

import me.semx11.autotip.api.util.RequestType;

public class LoginReply extends AbstractReply {

    private String sessionKey;

    public LoginReply() {
    }

    public LoginReply(boolean success) {
        super(success);
    }

    public String getSessionKey() {
        return sessionKey;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.LOGIN;
    }

}
