package me.semx11.autotip.api.reply.impl;

import me.semx11.autotip.api.reply.Reply;
import me.semx11.autotip.api.RequestType;

public class KeepAliveReply extends Reply {

    private long time;

    public KeepAliveReply() {
    }

    public KeepAliveReply(boolean success) {
        super(success);
    }

    public long getTime() {
        return time;
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.KEEP_ALIVE;
    }

}
