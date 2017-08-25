package me.semx11.autotip.api.util;

import me.semx11.autotip.api.reply.KeepAliveReply;
import me.semx11.autotip.api.reply.LoginReply;
import me.semx11.autotip.api.reply.LogoutReply;
import me.semx11.autotip.api.reply.TipReply;

public enum RequestType {
    LOGIN("login", LoginReply.class),
    KEEP_ALIVE("keepalive", KeepAliveReply.class),
    TIP("tip", TipReply.class),
    LOGOUT("logout", LogoutReply.class);

    private final String endpoint;
    private final Class<?> replyClass;

    RequestType(String endpoint, Class<?> replyClass) {
        this.endpoint = endpoint;
        this.replyClass = replyClass;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public Class<?> getReplyClass() {
        return replyClass;
    }

}
