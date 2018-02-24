package me.semx11.autotip.event;

import me.semx11.autotip.Autotip;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class EventClientTick {

    private static final EventClientTick INSTANCE = new EventClientTick();

    private EventClientTick() {
    }

    public static EventClientTick getInstance() {
        return INSTANCE;
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        Autotip.getInstance().getMessageUtil().flushQueues();
    }

}