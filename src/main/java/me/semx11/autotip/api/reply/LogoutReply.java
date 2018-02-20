package me.semx11.autotip.api.reply;

import me.semx11.autotip.api.util.RequestType;

public class LogoutReply extends AbstractReply<AbstractReply> {

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
