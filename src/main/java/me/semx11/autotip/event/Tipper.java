package me.semx11.autotip.event;

import me.semx11.autotip.Autotip;
import me.semx11.autotip.misc.FetchBoosters;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

import java.util.ArrayList;
import java.util.List;

public class Tipper {
    public static int waveCounter = 910;
    public static int waveLength = 915;
    public static List<String> tipQueue = new ArrayList<>();
    private static int tipDelay = 4;
    private long unixTime;

    @SubscribeEvent
    public void gameTick(ClientTickEvent event) {
        if (Autotip.onHypixel && Autotip.toggle && (unixTime != System.currentTimeMillis() / 1000L)) {
            if (waveCounter == waveLength) {
                Autotip.THREAD_POOL.submit(new FetchBoosters());
                waveCounter = 0;
            }

            if (!tipQueue.isEmpty()) tipDelay++;
            else tipDelay = 4;

            if (!tipQueue.isEmpty() && (tipDelay % 5 == 0)) {
                System.out.println("Attempting to tip: " + tipQueue.get(0));
                Autotip.mc.thePlayer.sendChatMessage("/tip " + tipQueue.get(0));
                tipQueue.remove(0);
                tipDelay = 0;
            }
            waveCounter++;
        }
        unixTime = System.currentTimeMillis() / 1000L;
    }
}