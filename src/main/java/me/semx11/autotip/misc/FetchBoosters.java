package me.semx11.autotip.misc;

import me.semx11.autotip.Autotip;
import me.semx11.autotip.api.reply.TipReply;
import me.semx11.autotip.api.request.TipRequest;
import me.semx11.autotip.event.EventClientTick;
import me.semx11.autotip.util.Host;
import me.semx11.autotip.util.Hosts;
import org.apache.commons.lang3.StringUtils;

public class FetchBoosters implements Runnable {

    // TODO: Probably throw away this class because what the fuck.

    @Override
    public void run() {
        Host tipHost = Hosts.getInstance().getHostById("totip");

        // TODO: Probably do something else regarding no session key.
        if (!tipHost.isEnabled() || !Autotip.hasSessionKey()) {
            EventClientTick.newTipQueue.addAll(TipReply.getDefault().getTips());
            return;
        }

        TipReply reply = TipRequest.doRequest(Autotip.getSessionKey());

        // TODO: I hate copying and pasting.
        if (!reply.isSuccess()) {
            EventClientTick.newTipQueue.addAll(TipReply.getDefault().getTips());
            return;
        }

        EventClientTick.newTipQueue.addAll(reply.getTips());
        Autotip.LOGGER.info("Fetched Boosters: " +
                StringUtils.join(EventClientTick.newTipQueue.iterator(), ", "));
    }
}
