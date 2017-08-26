package me.semx11.autotip.event;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.api.reply.TipReply;
import me.semx11.autotip.api.reply.TipReply.Tip;
import me.semx11.autotip.misc.FetchBoosters;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class EventClientTick {

    public static int waveCounter = 910;
    public static int waveLength = 915;
    public static List<String> tipQueue = new ArrayList<>();
    public static Queue<Tip> newTipQueue = new ConcurrentLinkedQueue<>();

    private static int tipDelay = 4;

    private long unixTime;

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        if (Autotip.onHypixel && Autotip.toggle && (unixTime
                != System.currentTimeMillis() / 1000L)) {
            if (waveCounter == waveLength) {
                Autotip.THREAD_POOL.submit(new FetchBoosters());
                waveCounter = 0;
            }

            if (!newTipQueue.isEmpty()) {
                tipDelay++;
            } else {
                tipDelay = 4;
            }

            if (!newTipQueue.isEmpty() && (tipDelay % 5 == 0)) {
                Autotip.LOGGER.info("Attempting to tip: {}", newTipQueue.peek());
                Autotip.MC.thePlayer.sendChatMessage("/tip " + newTipQueue.poll());
                tipDelay = 0;
            }
            waveCounter++;
        }
        unixTime = System.currentTimeMillis() / 1000L;
    }
}