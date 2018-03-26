package me.semx11.autotip.api.request;

import me.semx11.autotip.api.reply.Reply;
import me.semx11.autotip.api.RequestType;

public interface Request<T extends Reply> {

    T execute();

    RequestType getType();

}
