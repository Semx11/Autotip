package me.semx11.autotip.event;

import java.lang.reflect.Field;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.core.SessionManager;
import me.semx11.autotip.core.TaskManager;
import me.semx11.autotip.core.TaskManager.TaskType;
import me.semx11.autotip.util.ErrorReport;
import me.semx11.autotip.util.ReflectionUtil;
import me.semx11.autotip.util.UniversalUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class EventClientConnection {

    private static final EventClientConnection INSTANCE = new EventClientConnection();

    // TODO: Don't hard-code this.
    private static final String HYPIXEL_HEADER = "You are playing on MC.HYPIXEL.NET";
    private static final Field HEADER_FIELD = ReflectionUtil
            .findField(GuiPlayerTabOverlay.class, "field_175256_i", "header");

    private String serverIp;
    private long lastLogin;

    private EventClientConnection() {
    }

    public static EventClientConnection getInstance() {
        return INSTANCE;
    }

    public String getServerIp() {
        return serverIp;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public Object getHeader() {
        try {
            return HEADER_FIELD.get(Minecraft.getMinecraft().ingameGUI.getTabList());
        } catch (IllegalAccessException | NullPointerException e) {
            ErrorReport.reportException(e);
            return null;
        }
    }

    private void resetHeader() {
        try {
            HEADER_FIELD.set(Minecraft.getMinecraft().ingameGUI.getTabList(), null);
        } catch (IllegalAccessException e) {
            ErrorReport.reportException(e);
        }
    }

    @SubscribeEvent
    public void playerLoggedIn(ClientConnectedToServerEvent event) {
        Autotip autotip = Autotip.getInstance();
        TaskManager taskManager = autotip.getTaskManager();
        SessionManager manager = autotip.getSessionManager();

        autotip.getMessageUtil().clearQueues();

        this.serverIp = UniversalUtil.getRemoteAddress(event).toString().toLowerCase();
        this.lastLogin = System.currentTimeMillis();

        taskManager.getExecutor().execute(() -> {
            Object header;
            int attempts = 0;
            while ((header = this.getHeader()) == null) {
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
                if (autotip.getConfig().isEnabled()) {
                    taskManager.executeTask(TaskType.LOGIN, manager::login);
                }
            } else {
                manager.setOnHypixel(false);
            }
        });
    }

    @SubscribeEvent
    public void playerLoggedOut(ClientDisconnectionFromServerEvent event) {
        Autotip autotip = Autotip.getInstance();
        TaskManager taskManager = autotip.getTaskManager();
        SessionManager manager = autotip.getSessionManager();
        manager.setOnHypixel(false);
        taskManager.executeTask(TaskType.LOGOUT, manager::logout);
        this.resetHeader();
    }

}