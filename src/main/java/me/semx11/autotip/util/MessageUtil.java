package me.semx11.autotip.util;

import com.google.common.collect.Queues;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.universal.UniversalUtil;
import net.minecraft.client.Minecraft;

public class MessageUtil {

    private static final Pattern FORMAT_PATTERN = Pattern.compile("(?im)&([0-9A-FK-OR])");
    private static final Pattern PARAM_PATTERN = Pattern.compile("\\{}");

    private static final String PREFIX = "&6A&eT &8> &7";

    private final Queue<String> chatQueue = Queues.newConcurrentLinkedQueue();
    private final Queue<String> cmdQueue = Queues.newConcurrentLinkedQueue();

    public MessageUtil() {
    }

    public void send(String msg, Object... params) {
        sendRaw(PREFIX + msg, params);
    }

    public void send(String msg, String url, String hoverText, Object... params) {
        UniversalUtil.addChatMessage(format(params(PREFIX + msg, params)), url, format(hoverText));
    }

    public void separator() {
        sendRaw("&6&m&l----------------------------------");
    }

    public void sendRaw(String msg, Object... params) {
        msg = format(params(msg, params));
        if (isPlayerLoaded()) {
            flushQueues();
            UniversalUtil.addChatMessage(msg);
        } else {
            chatQueue.add(msg);
            Autotip.LOGGER.info("Queued chat message: " + msg);
        }
    }

    public void sendCommand(String command) {
        if (isPlayerLoaded()) {
            flushQueues();
            Autotip.getInstance().getMinecraft().thePlayer.sendChatMessage(command);
        } else {
            cmdQueue.add(command);
            Autotip.LOGGER.info("Queued command: " + command);
        }
    }

    public String params(String input, Object... params) {
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

    public void flushQueues() {
        if (isPlayerLoaded()) {
            while (!chatQueue.isEmpty()) {
                sendRaw(chatQueue.poll());
            }
            while (!cmdQueue.isEmpty()) {
                sendCommand(cmdQueue.poll());
            }
        }
    }

    public void clearQueues() {
        chatQueue.clear();
        cmdQueue.clear();
    }

    private String format(String msg) {
        return msg.contains("&") ? FORMAT_PATTERN.matcher(msg).replaceAll("\u00a7$1") : msg;
    }

    private boolean isPlayerLoaded() {
        Minecraft minecraft = Autotip.getInstance().getMinecraft();
        return minecraft != null && minecraft.thePlayer != null;
    }

}
