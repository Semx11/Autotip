package me.semx11.autotip.event;

import java.util.regex.Pattern;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.misc.StartLogin;
import me.semx11.autotip.util.UniversalUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class HypixelListener {

    private static final Pattern IP_PATTERN = Pattern
            .compile("(^([\\w-]+[.\\u2024])?hypixel[.\\u2024]net|209\\.222\\.115\\.\\d{1,3})");

    public static String lastIp;

    @SubscribeEvent
    public void playerLoggedIn(ClientConnectedToServerEvent event) {
        lastIp = UniversalUtil.getRemoteAddress(event).toString().toLowerCase();
        if (IP_PATTERN.matcher(lastIp).matches()) {
            Autotip.onHypixel = true;
            Tipper.waveCounter = 910;
            Autotip.THREAD_POOL.submit(new StartLogin());
        } else {
            Autotip.onHypixel = false;
        }
    }

    @SubscribeEvent
    public void playerLoggedOut(ClientDisconnectionFromServerEvent event) {
        Autotip.onHypixel = false;
    }

}