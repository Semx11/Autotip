package me.semx11.autotip.util;

import com.google.common.collect.Queues;
import java.util.Queue;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.universal.UniversalUtil;
import net.minecraft.client.Minecraft;

public class MessageUtil {

    private static final String PREFIX = "&6A&eT &8> &7";

    private final Queue<String> chatQueue = Queues.newConcurrentLinkedQueue();
    private final Queue<String> cmdQueue = Queues.newConcurrentLinkedQueue();

    public MessageUtil() {
    }

    public void send(String msg, Object... params) {
        sendRaw(PREFIX + msg, params);
    }

    public void send(String msg, String url, String hoverText, Object... params) {
        UniversalUtil.addChatMessage(StringUtil.params(PREFIX + msg, params), url,
                StringUtil.format(hoverText));
    }

    public void separator() {
        sendRaw("&6&m&l----------------------------------");
    }

    public void sendRaw(String msg, Object... params) {
        msg = StringUtil.params(msg, params);
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

    private boolean isPlayerLoaded() {
        Minecraft minecraft = Autotip.getInstance().getMinecraft();
        return minecraft != null && minecraft.thePlayer != null;
    }

}
