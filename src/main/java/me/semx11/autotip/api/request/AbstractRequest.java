package me.semx11.autotip.api.request;

import me.semx11.autotip.api.reply.AbstractReply;
import me.semx11.autotip.api.util.RequestType;

public abstract class AbstractRequest<T extends AbstractReply<AbstractReply>> {

    public abstract T execute();

    public abstract RequestType getType();

}
