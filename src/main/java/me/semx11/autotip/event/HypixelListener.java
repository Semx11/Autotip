package me.semx11.autotip.event;

import me.semx11.autotip.Autotip;
import me.semx11.autotip.misc.StartLogin;
import me.semx11.autotip.util.UniversalUtil;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

public class HypixelListener {

    public static String lastIp;

    @SubscribeEvent
    public void playerLoggedIn(ClientConnectedToServerEvent event) {
        lastIp = UniversalUtil.getRemoteAddress(event).toString().toLowerCase();
        if (lastIp.contains(".hypixel.net") || lastIp.startsWith("209.222.115.")) {
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
