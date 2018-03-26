package me.semx11.autotip.api.reply.impl;

import me.semx11.autotip.api.reply.Reply;
import me.semx11.autotip.api.RequestType;

public class LogoutReply extends Reply {

    public LogoutReply() {
    }

    public LogoutReply(boolean success) {
        super(success);
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.LOGOUT;
    }

}
