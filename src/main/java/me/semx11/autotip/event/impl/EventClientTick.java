package me.semx11.autotip.event.impl;

import me.semx11.autotip.Autotip;
import me.semx11.autotip.event.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class EventClientTick implements Event {

    private final Autotip autotip;

    public EventClientTick(Autotip autotip) {
        this.autotip = autotip;
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        autotip.getMessageUtil().flushQueues();
        autotip.getStatsManager().saveCycle();
    }

}