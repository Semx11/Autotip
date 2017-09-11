package me.semx11.autotip.event;

import java.lang.reflect.Field;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.core.SessionManager;
import me.semx11.autotip.core.TaskManager;
import me.semx11.autotip.util.ErrorReport;
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
        SessionManager manager = Autotip.SESSION_MANAGER;

        MessageUtil.clearQueues();

        // TODO: Remove this probably.
        lastIp = UniversalUtil.getRemoteAddress(event).toString().toLowerCase();

        TaskManager.EXECUTOR.submit(() -> {
            Object header;
            int attempts = 0;
            while ((header = getHeader()) == null) {
                if (attempts > 15) {
                    return;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    ErrorReport.reportException(e);
                }
                attempts++;
            }

            if (UniversalUtil.getUnformattedText(header).equals(HYPIXEL_HEADER)) {
                manager.setOnHypixel(true);
                manager.checkVersions();
                manager.login();
            } else {
                manager.setOnHypixel(false);
            }

        });
    }

    @SubscribeEvent
    public void playerLoggedOut(ClientDisconnectionFromServerEvent event) {
        TaskManager.EXECUTOR.submit(Autotip.SESSION_MANAGER::logout);
    }

    public static Object getHeader() {
        try {
            return HEADER_FIELD.get(Minecraft.getMinecraft().ingameGUI.getTabList());
        } catch (IllegalAccessException | NullPointerException e) {
            ErrorReport.reportException(e);
            return null;
        }
    }

}