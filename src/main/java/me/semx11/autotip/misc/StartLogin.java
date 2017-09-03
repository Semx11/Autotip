package me.semx11.autotip.misc;

import java.util.List;
import java.util.concurrent.TimeUnit;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.api.reply.KeepAliveReply;
import me.semx11.autotip.api.reply.LoginReply;
import me.semx11.autotip.api.request.KeepAliveRequest;
import me.semx11.autotip.api.request.LoginRequest;
import me.semx11.autotip.util.ChatColor;
import me.semx11.autotip.util.ClientMessage;
import me.semx11.autotip.util.Host;
import me.semx11.autotip.util.Hosts;
import me.semx11.autotip.util.LoginUtil;
import me.semx11.autotip.util.VersionInfo;
import me.semx11.autotip.util.Versions;
import net.minecraft.util.Session;

public class StartLogin implements Runnable {

    // TODO: Make this more elegant because why is this a thing.

    private static void login() {
        Host loginHost = Hosts.getInstance().getHostById("update");

        if (loginHost == null || !loginHost.isEnabled()) {
            return;
        }

        Session session = Autotip.MC.getSession();

        String uuid = session.getProfile().getId().toString().replace("-", "");
        String serverHash = LoginUtil.hash(uuid + LoginUtil.getNextSalt());

        LoginUtil.joinServer(session.getToken(), uuid, serverHash);

        // TODO: Remove totalTipsSent and calculate instead
        LoginReply reply = LoginRequest.doRequest(session, serverHash, Autotip.totalTipsSent);

        if (!reply.isSuccess()) {
            return;
        }

        Autotip.setSessionKey(reply.getSessionKey());

        Autotip.EXECUTOR.scheduleAtFixedRate(() -> {
            KeepAliveReply r = KeepAliveRequest.doRequest(Autotip.getSessionKey());
            if (!r.isSuccess()) {
                Autotip.LOGGER.warn("KeepAliveRequest failed: {}", r.getCause());
            }
        }, 300, 300, TimeUnit.SECONDS);

    }

    private static void checkVersions() {
        Versions.updateVersions();

        Host downloadHost = Hosts.getInstance().getHostById("download");

        List<VersionInfo> vInfo = Versions.getInstance().getHigherVersionInfo(Autotip.VERSION);
        if (vInfo.size() > 0) {
            ClientMessage.separator();
            ClientMessage.send(
                    ChatColor.RED + "Autotip is out of date! Click here to update.",
                    "https://" + downloadHost.getUrl(),
                    ChatColor.GRAY + "Click to visit " + ChatColor.GOLD + downloadHost.getUrl()
                            + ChatColor.GRAY + "!"
            );
            ClientMessage.send("Update info:");
            vInfo.forEach(vi -> {
                ClientMessage.send(ChatColor.GOLD + "Autotip v" + vi.getVersion());
                ClientMessage.send("Update severity: " + vi.getSeverity().toColoredString());
                vi.getChangelog().forEach(
                        s -> ClientMessage.send(ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + s));
            });
            ClientMessage.separator();
        }
    }

    @Override
    public void run() {
        try {
            // Why.
            Thread.sleep(2000);
            login();
            checkVersions();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
