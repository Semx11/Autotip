package me.semx11.autotip.misc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import me.semx11.autotip.Autotip;

public class TipTracker {

    public static Map<Long, String> tipsSentHistory = new TreeMap<>(Collections.reverseOrder());
    public static Map<String, Integer> tipsSentEarnings = new HashMap<>();
    public static Map<String, Integer> tipsReceivedEarnings = new HashMap<>();
    public static int tipsSent = 0;
    public static int tipsReceived = 0;
    public static int karmaCount = 0;

    public static void addTip(String username) {
        tipsSentHistory.put(System.currentTimeMillis(), username);
        tipsSent++;
        Autotip.totalTipsSent++;

        Autotip.LOGGER.info("Tipped: {}", username);
        Writer.execute();
    }

}