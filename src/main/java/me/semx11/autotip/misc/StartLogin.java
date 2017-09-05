package me.semx11.autotip.misc;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.api.reply.KeepAliveReply;
import me.semx11.autotip.api.reply.LoginReply;
import me.semx11.autotip.api.request.KeepAliveRequest;
import me.semx11.autotip.api.request.LoginRequest;
import me.semx11.autotip.util.ChatColor;
import me.semx11.autotip.util.Host;
import me.semx11.autotip.util.Hosts;
import me.semx11.autotip.util.LoginUtil;
import me.semx11.autotip.util.MessageUtil;
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
        LoginReply reply = LoginRequest.of(session, serverHash, Autotip.totalTipsSent).execute();

        if (!reply.isSuccess()) {
            MessageUtil.send("&cError during login: {}", reply.getCause());
            return;
        }

        Autotip.setSessionKey(reply.getSessionKey());

        ScheduledFuture future = Autotip.EXECUTOR.scheduleAtFixedRate(() -> {
            Autotip.LOGGER.info("KeepAlive");
            KeepAliveReply r = KeepAliveRequest.of(Autotip.getSessionKey()).execute();
            if (!r.isSuccess()) {
                Autotip.LOGGER.warn("KeepAliveRequest failed: {}", r.getCause());
            }
        }, 300, 300, TimeUnit.SECONDS);

        Autotip.ACTIVE_TASKS.add(future);
    }

    private static void checkVersions() {
        Versions.updateVersions();

        Host downloadHost = Hosts.getInstance().getHostById("download");

        List<VersionInfo> vInfo = Versions.getInstance().getHigherVersionInfo(Autotip.VERSION);
        if (vInfo.size() > 0) {
            MessageUtil.separator();
            MessageUtil.send(
                    ChatColor.RED + "Autotip is out of date! Click here to update.",
                    "https://" + downloadHost.getUrl(),
                    ChatColor.GRAY + "Click to visit " + ChatColor.GOLD + downloadHost.getUrl()
                            + ChatColor.GRAY + "!"
            );
            MessageUtil.send("Update info:");
            vInfo.forEach(vi -> {
                MessageUtil.send(ChatColor.GOLD + "Autotip v" + vi.getVersion());
                MessageUtil.send("Update severity: " + vi.getSeverity().toColoredString());
                vi.getChangelog().forEach(
                        s -> MessageUtil.send(ChatColor.DARK_GRAY + "- " + ChatColor.GRAY + s));
            });
            MessageUtil.separator();
        }
    }

    @Override
    public void run() {
        try {
            // Why.
            Thread.sleep(3000);
            login();
            checkVersions();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
