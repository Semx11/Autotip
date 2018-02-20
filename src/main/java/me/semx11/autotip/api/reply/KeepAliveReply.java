package me.semx11.autotip.api.reply;

import me.semx11.autotip.api.util.RequestType;

public class KeepAliveReply extends AbstractReply<AbstractReply> {

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
