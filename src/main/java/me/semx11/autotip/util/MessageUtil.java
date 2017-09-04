package me.semx11.autotip.util;

import com.google.common.collect.Queues;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.semx11.autotip.Autotip;

public class MessageUtil {

    private static final Pattern FORMAT_PATTERN = Pattern.compile("(?im)&([0-9A-FK-OR])");
    private static final Pattern PARAM_PATTERN = Pattern.compile("\\{}");

    private static final Queue<String> CHAT_QUEUE = Queues.newConcurrentLinkedQueue();
    private static final Queue<String> CMD_QUEUE = Queues.newConcurrentLinkedQueue();

    private static final String PREFIX = "&6A&eT &8> &7";

    public static void send(String msg, Object... params) {
        sendRaw(PREFIX + msg, params);
    }

    public static void send(String msg, String url, String hoverText, Object... params) {
        UniversalUtil.addChatMessage(format(params(PREFIX + msg, params)), url, format(hoverText));
    }

    public static void separator() {
        sendRaw("&6&l----------------------------------");
    }

    public static void sendRaw(String msg, Object... params) {
        msg = format(params(msg, params));
        if (isPlayerLoaded()) {
            flushQueues();
            UniversalUtil.addChatMessage(msg);
        } else {
            CHAT_QUEUE.add(msg);
            Autotip.LOGGER.info("Queued chat message: " + msg);
        }
    }

    public static void sendCommand(String command) {
        if (isPlayerLoaded()) {
            flushQueues();
            Autotip.MC.thePlayer.sendChatMessage(command);
        } else {
            CMD_QUEUE.add(command);
            Autotip.LOGGER.info("Queued command: " + command);
        }
    }

    public static String params(String input, Object... params) {
        if (params == null) {
            return input;
        }
        for (Object o : params) {
            if (o != null) {
                input = PARAM_PATTERN.matcher(input)
                        .replaceFirst(Matcher.quoteReplacement(o.toString()));
            }
        }
        return input;
    }

    public static void flushQueues() {
        if (isPlayerLoaded()) {
            while (!CHAT_QUEUE.isEmpty()) {
                sendRaw(CHAT_QUEUE.poll());
            }
            while (!CMD_QUEUE.isEmpty()) {
                sendCommand(CMD_QUEUE.poll());
            }
        }
    }

    public static void clearQueues() {
        CHAT_QUEUE.clear();
        CMD_QUEUE.clear();
    }

    private static String format(String msg) {
        return msg.contains("&") ? FORMAT_PATTERN.matcher(msg).replaceAll("\u00a7$1") : msg;
    }

    private static boolean isPlayerLoaded() {
        return Autotip.MC != null && Autotip.MC.thePlayer != null;
    }

}
