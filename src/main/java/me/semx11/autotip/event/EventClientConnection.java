package me.semx11.autotip.event;

import java.lang.reflect.Field;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.api.reply.LogoutReply;
import me.semx11.autotip.api.request.LogoutRequest;
import me.semx11.autotip.misc.StartLogin;
import me.semx11.autotip.util.MessageUtil;
import me.semx11.autotip.util.ReflectionUtil;
import me.semx11.autotip.util.UniversalUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class EventClientConnection {

    // TODO: Don't hard-code this.
    private static final String HYPIXEL_HEADER = "You are playing on MC.HYPIXEL.NET";
    private static final Field HEADER_FIELD = ReflectionUtil
            .findField(GuiPlayerTabOverlay.class, "field_175256_i", "header");

    public static String lastIp;

    @SubscribeEvent
    public void playerLoggedIn(ClientConnectedToServerEvent event) {
        MessageUtil.clearQueues();

        // TODO: Remove this probably.
        lastIp = UniversalUtil.getRemoteAddress(event).toString().toLowerCase();

        // Thanks, Forge, for not having a proper 'packet received' event.
        Autotip.THREAD_POOL.submit(() -> {
            Object header;
            int attempts = 0;
            while ((header = getHeader()) == null) {
                if (attempts > 15) {
                    return;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                attempts++;
            }

            if (UniversalUtil.getUnformattedText(header).equals(HYPIXEL_HEADER)) {
                Autotip.onHypixel = true;
                EventClientTick.waveCounter = 910;
                Autotip.THREAD_POOL.submit(new StartLogin());
            } else {
                Autotip.onHypixel = false;
            }

        });
    }

    @SubscribeEvent
    public void playerLoggedOut(ClientDisconnectionFromServerEvent event) {
        Autotip.onHypixel = false;

        Autotip.THREAD_POOL.submit(() -> {
            LogoutReply reply = LogoutRequest.of(Autotip.getSessionKey()).execute();
            if (!reply.isSuccess()) {
                return;
            }
            Autotip.ACTIVE_TASKS.removeIf(future -> future.cancel(true));
            Autotip.setSessionKey(null);

            EventClientTick.TIP_QUEUE.clear();
        });
    }

    public static Object getHeader() {
        try {
            return HEADER_FIELD.get(Minecraft.getMinecraft().ingameGUI.getTabList());
        } catch (IllegalAccessException | NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

}