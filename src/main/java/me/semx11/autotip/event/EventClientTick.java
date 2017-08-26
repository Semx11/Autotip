package me.semx11.autotip.event;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.api.reply.TipReply.Tip;
import me.semx11.autotip.misc.FetchBoosters;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class EventClientTick {

    public static final Queue<Tip> TIP_QUEUE = new ConcurrentLinkedQueue<>();

    public static int waveCounter = 910;
    public static int waveLength = 915;

    private int tipDelay = 4;
    private long time;

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        if (Autotip.onHypixel && Autotip.toggle && (time != System.currentTimeMillis() / 1000L)) {
            if (waveCounter == waveLength) {
                Autotip.THREAD_POOL.submit(new FetchBoosters());
                waveCounter = 0;
            }

            if (!TIP_QUEUE.isEmpty()) {
                tipDelay++;
            } else {
                tipDelay = 4;
            }

            if (!TIP_QUEUE.isEmpty() && (tipDelay % 5 == 0)) {
                Autotip.LOGGER.info("Attempting to tip: {}", TIP_QUEUE.peek());
                Autotip.MC.thePlayer.sendChatMessage("/tip " + TIP_QUEUE.poll());
                tipDelay = 0;
            }
            waveCounter++;
        }
        time = System.currentTimeMillis() / 1000L;
    }
}