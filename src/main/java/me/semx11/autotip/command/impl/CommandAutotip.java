package me.semx11.autotip.command.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.chat.MessageOption;
import me.semx11.autotip.chat.MessageUtil;
import me.semx11.autotip.command.CommandAbstract;
import me.semx11.autotip.config.Config;
import me.semx11.autotip.config.GlobalSettings;
import me.semx11.autotip.core.SessionManager;
import me.semx11.autotip.core.StatsManager;
import me.semx11.autotip.core.TaskManager;
import me.semx11.autotip.core.TaskManager.TaskType;
import me.semx11.autotip.event.impl.EventClientConnection;
import me.semx11.autotip.legacy.LegacyFileUtil;
import me.semx11.autotip.legacy.Stats;
import me.semx11.autotip.stats.StatsDaily;
import me.semx11.autotip.universal.UniversalUtil;
import me.semx11.autotip.util.MinecraftVersion;
import me.semx11.autotip.util.Versions;
import net.minecraft.command.ICommandSender;

public class CommandAutotip extends CommandAbstract {

    private static final DateTimeFormatter SESSION_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter WAVE_FORMAT = DateTimeFormatter.ofPattern("mm:ss");

    public CommandAutotip(Autotip autotip) {
        super(autotip);
    }

    @Override
    public String getCommandName() {
        return "autotip";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return autotip.getLocaleHolder().getKey("command.usage");
    }

    @Override
    public List<String> getCommandAliases() {
        if (!autotip.getMcVersion().equals(MinecraftVersion.V1_8)) {
            return Collections.singletonList("at");
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void onCommand(ICommandSender sender, String[] args) {
        Config config = autotip.getConfig();
        MessageUtil messageUtil = autotip.getMessageUtil();
        TaskManager taskManager = autotip.getTaskManager();
        SessionManager manager = autotip.getSessionManager();
        StatsManager stats = autotip.getStatsManager();
        GlobalSettings settings = autotip.getGlobalSettings();

        StatsDaily today = stats.get();

        if (args.length <= 0) {
            messageUtil.sendKey("command.usage");
            return;
        }

        switch (args[0].toLowerCase()) {
            case "s":
            case "stats":
                LocalDate now = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

                if (args.length <= 1) {
                    stats.get(now).print();
                    return;
                }

                switch (args[1].toLowerCase()) {
                    case "day":
                    case "daily":
                    case "today":
                        stats.get(now).print();
                        break;
                    case "yesterday":
                        stats.get(now.minusDays(1)).print();
                        break;
                    case "week":
                    case "weekly":
                        Stats.printBetween(now.with(DayOfWeek.MONDAY).format(formatter),
                                now.with(DayOfWeek.SUNDAY).format(formatter));
                        break;
                    case "month":
                    case "monthly":
                        Stats.printBetween(now.withDayOfMonth(1).format(formatter),
                                now.withDayOfMonth(now.lengthOfMonth()).format(formatter));
                        break;
                    case "year":
                    case "yearly":
                        Stats.printBetween("01-01-" + Year.now().getValue(),
                                "31-12-" + Year.now().getValue());
                        break;
                    case "all":
                    case "total":
                    case "life":
                    case "lifetime":
                        Stats.printBetween("25-06-2016", LegacyFileUtil.getDate());
                        break;
                    default:
                        messageUtil.sendKey("command.stats.usage");
                        break;

                }
                break;
            case "?":
            case "info":
                messageUtil.separator();
                messageUtil.sendKey("command.info.version", autotip.getVersion());
                messageUtil.getBuilder(messageUtil.getKey("command.info.credits"))
                        .setHover(messageUtil.getKey("command.info.creditsHover"))
                        .send();
                messageUtil.sendKey("command.info.status." + (config.isEnabled() ? "enabled"
                        : "disabled"));
                messageUtil.sendKey("command.info.messages", config.getMessageOption());
                messageUtil.sendKey("command.info.tipsSent", today.getTipsSent());
                messageUtil.sendKey("command.info.tipsReceived", today.getTipsReceived());
                messageUtil.sendKey("command.info.statsCommand");
                messageUtil.separator();
                break;
            case "m":
            case "messages":
                try {
                    if (args.length > 1) {
                        MessageOption option = MessageOption.valueOfIgnoreCase(args[1]);
                        config.setMessageOption(option).save();
                    } else {
                        config.nextMessageOption().save();
                    }
                    messageUtil.sendKey("command.messages.next", config.getMessageOption());
                } catch (IllegalArgumentException e) {
                    messageUtil.sendKey("command.messages.error", args.length > 1 ? args[1] : null);
                }
                break;
            case "t":
            case "toggle":
                config.toggleEnabled().save();
                messageUtil.sendKey("command.toggle." + (config.isEnabled() ? "enabled"
                        : "disabled"));
                if (config.isEnabled()) {
                    if (manager.isOnHypixel() && !manager.isLoggedIn()) {
                        taskManager.executeTask(TaskType.LOGIN, manager::login);
                    }
                } else if (manager.isLoggedIn()) {
                    taskManager.executeTask(TaskType.LOGOUT, manager::logout);
                }
                break;
            case "wave":
                if (!config.isEnabled()) {
                    messageUtil.send("error.disabled");
                    return;
                }
                if (!manager.isOnHypixel()) {
                    messageUtil.sendKey("error.disabledHypixel");
                    return;
                }
                if (manager.getNextTipWave() == 0) {
                    messageUtil.sendKey("command.wave.error");
                    return;
                }

                messageUtil.separator();
                long time = System.currentTimeMillis();
                messageUtil.sendKey("command.wave.lastWave", LocalTime.MIN
                        .plusSeconds((time - manager.getLastTipWave()) / 1000)
                        .format(WAVE_FORMAT));
                messageUtil.sendKey("command.wave.nextWave" + LocalTime.MIN
                        .plusSeconds((manager.getNextTipWave() - time) / 1000 + 1)
                        .format(WAVE_FORMAT));
                messageUtil.separator();
                break;
            case "changelog":
                // TODO: Fix this
                messageUtil.separator();
                messageUtil.sendKey("command.changelog.version", autotip.getVersion());
                Versions.getInstance().getInfoByVersion(autotip.getVersion()).getChangelog()
                        .forEach(s -> messageUtil.sendKey("command.changelog.entry", s));
                messageUtil.separator();
                break;
            case "debug":
                EventClientConnection event = autotip.getEvent(EventClientConnection.class);
                messageUtil.separator();
                messageUtil.send("command.debug.serverIp", event.getServerIp());
                messageUtil.send("command.debug.mcVersion", autotip.getMcVersion());
                Object header = event.getHeader();
                messageUtil.sendKey("command.debug.header." + (header == null ? "none"
                        : "present"), UniversalUtil.getUnformattedText(header));
                messageUtil.separator();
                break;
            case "msgdebug":
                config.setMessageOption(MessageOption.DEBUG).save();
                messageUtil.send("Tip Messages: " + config.getMessageOption());
                break;
            case "reload":
                try {
                    autotip.reloadGlobalSettings();
                    autotip.reloadLocale();
                    messageUtil.sendKey("command.reload.success");
                } catch (IllegalStateException e) {
                    messageUtil.sendKey("command.reload.error");
                }
                break;
            case "add":
                // TODO: Remove before release
                stats.getToday().addCoins("SkyWars", 1337, 1337);
                break;
            case "msg":
                // TODO: Remove before release
                messageUtil.getBuilder(false, "&aYou tipped 17 players in 12 different games!")
                        .setHover("&aRewards\n"
                                + "&3+600 Hypixel Experience\n"
                                + "&6+15 Speed UHC Coins\n"
                                + "&6+15 UHC Champions Coins\n"
                                + "&6+15 Arena Brawl Coins\n"
                                + "&6+15 The Walls Coins\n"
                                + "&6+15 Blitz SG Coins\n"
                                + "&6+15 Warlords Coins\n"
                                + "&6+15 Turbo Kart Racers Coins\n"
                                + "&6+15 VampireZ Coins\n"
                                + "&6+15 The TNT Games Coins\n"
                                + "&6+15 Cops and Crims Coins\n"
                                + "&6+15 Paintball Warfare Coins\n"
                                + "&6+38 Arcade Games Coins\n"
                                + "&6+15 Mega Walls Coins\n"
                                + "&6+15 SkyClash Coins\n"
                                + "&6+15 Crazy Walls Coins\n"
                                + "&6+15 SkyWars Coins\n"
                                + "&6+15 Quakecraft Coins")
                        .send();
                break;
            default:
                messageUtil.send("&cUsage: " + getCommandUsage(sender));
                break;
        }
    }

    @Override
    public List<String> onTabComplete(ICommandSender sender, String[] args) {
        switch (args.length) {
            case 1:
                return getListOfStringsMatchingLastWord(args, "stats", "info", "messages", "toggle",
                        "time");
            case 2:
                if (args[0].equalsIgnoreCase("stats") || args[0].equalsIgnoreCase("s")) {
                    return getListOfStringsMatchingLastWord(args, "day", "yesterday", "week",
                            "month", "year",
                            "lifetime");
                }
        }
        return Collections.emptyList();
    }

}
