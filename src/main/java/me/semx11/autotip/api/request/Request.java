package me.semx11.autotip.api.request;

import me.semx11.autotip.api.reply.Reply;
import me.semx11.autotip.api.util.RequestType;

public abstract class Request<T extends Reply> {

    public abstract T execute();

    public abstract RequestType getType();

}
