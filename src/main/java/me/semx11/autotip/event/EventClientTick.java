package me.semx11.autotip.event;

import me.semx11.autotip.util.MessageUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class EventClientTick {

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        MessageUtil.flushQueues();
    }

}