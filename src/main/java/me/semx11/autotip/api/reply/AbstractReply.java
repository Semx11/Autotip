package me.semx11.autotip.api.reply;

import me.semx11.autotip.api.util.RequestType;

public abstract class AbstractReply {

    private boolean success;
    private String cause;

    AbstractReply() {
    }

    AbstractReply(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getCause() {
        return cause;
    }

    public abstract RequestType getRequestType();

}
