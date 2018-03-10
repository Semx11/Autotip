package me.semx11.autotip.command.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.command.CommandAbstract;
import me.semx11.autotip.config.Config;
import me.semx11.autotip.core.SessionManager;
import me.semx11.autotip.core.TaskManager;
import me.semx11.autotip.core.TaskManager.TaskType;
import me.semx11.autotip.event.impl.EventClientConnection;
import me.semx11.autotip.misc.Stats;
import me.semx11.autotip.misc.TipTracker;
import me.semx11.autotip.universal.UniversalUtil;
import me.semx11.autotip.util.LegacyFileUtil;
import me.semx11.autotip.util.MessageUtil;
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
        return "/autotip <stats, info, messages, toggle, time>";
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

        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "m":
                case "messages":
                    config.nextMessageOption().save();
                    messageUtil.send("Tip Messages: " + config.getMessageOption());
                    break;
                case "?":
                case "info":
                    messageUtil.separator();
                    messageUtil.send("&6&lAutotip v" + autotip.getVersion() + " &7by &bSemx11");
                    messageUtil.send("Brought to you by the &6Autotip Team &7(hover)",
                            null,
                            "&6The Autotip Team\n"
                                    + "&b[MVP&4+&b] Semx11 &r&7- Lead Mod developer, API, PR\n"
                                    + "&b[MVP&3+&b] 2Pi &r&7- Lead API developer, Founder, Creator, PR\n"
                                    + "&6[YT] Sk1er &r&7- Host, not the creator, PR");
                    messageUtil.send("Autotipper: " + (config.isEnabled() ? "&aEn" : "&cDis") +
                            "abled");
                    messageUtil.send("Tip Messages: " + config.getMessageOption());
                    messageUtil.send("Tips sent today: &6" + TipTracker.tipsSent);
                    messageUtil.send("Tips received today: &6" + TipTracker.tipsReceived);
                    messageUtil.send("Lifetime tips sent: &6" + "a lotta tips");
                    messageUtil.send("&6Type /autotip stats to see what has been earned.");
                    messageUtil.separator();
                    break;
                case "s":
                case "stats":
                    LocalDate now = LocalDate.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

                    if (args.length > 1) {
                        switch (args[1].toLowerCase()) {
                            case "day":
                            case "daily":
                            case "today":
                                Stats.printStats(LegacyFileUtil.getDate());
                                break;
                            case "yesterday":
                                Stats.printStats(now.minusDays(1).format(formatter));
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
                                messageUtil.send("&cUsage: /autotip stats <day, week, month, year,"
                                        + " lifetime>");
                                break;
                        }
                    } else {
                        Stats.printStats(LegacyFileUtil.getDate());
                    }
                    break;
                case "t":
                case "toggle":
                    config.toggleEnabled().save();
                    messageUtil.send("Autotip: {}abled", config.isEnabled() ? "&aEn" : "&cDis");
                    if (config.isEnabled()) {
                        if (manager.isOnHypixel() && !manager.isLoggedIn()) {
                            taskManager.executeTask(TaskType.LOGIN, manager::login);
                        }
                    } else {
                        if (manager.isLoggedIn()) {
                            taskManager.executeTask(TaskType.LOGOUT, manager::logout);
                        }
                    }
                    break;
                case "wave":
                case "time":
                    if (config.isEnabled()) {
                        if (manager.isOnHypixel()) {
                            if (manager.getNextTipWave() == 0) {
                                messageUtil.send("The first tip wave hasn't occurred yet.");
                                return;
                            }
                            messageUtil.separator();
                            long time = System.currentTimeMillis();
                            messageUtil.send("Last wave: &6" + LocalTime.MIN
                                    .plusSeconds((time - manager.getLastTipWave()) / 1000)
                                    .format(WAVE_FORMAT));
                            messageUtil.send("Next wave: &6" + LocalTime.MIN
                                    .plusSeconds((manager.getNextTipWave() - time) / 1000 + 1)
                                    .format(WAVE_FORMAT));
                            messageUtil.separator();
                        } else {
                            messageUtil.send("Autotip is disabled as you are not playing "
                                    + "on Hypixel.");
                        }
                    } else {
                        messageUtil.send("Autotip is disabled. Use &6/autotip toggle&7 to "
                                + "enable it.");
                    }
                    break;
                case "whatsnew":
                    messageUtil.separator();
                    messageUtil.send("&6What's new in Autotip v" + autotip.getVersion()
                            + ":");
                    Versions.getInstance().getInfoByVersion(autotip.getVersion()).getChangelog()
                            .forEach(
                                    s -> messageUtil.send("&8- &7" + s));
                    messageUtil.separator();
                    break;
                case "debug":
                    EventClientConnection event = autotip.getEvent(EventClientConnection.class);
                    messageUtil.separator();
                    messageUtil.send("Last IP joined: {}", event.getServerIp());
                    messageUtil.send("Detected MC version: {}", autotip.getMcVersion());
                    Object header = event.getHeader();
                    messageUtil.send("Tab Header: {}", (header == null ? "None"
                            : UniversalUtil.getUnformattedText(header)));
                    messageUtil.separator();
                    break;
                // TODO: REMOVE THIS AT RELEASE
                case "msg":
                    //old
                    messageUtil.sendRaw("");
                    messageUtil.sendRaw("&9+50 experience (Gave a player a /tip)");
                    messageUtil.sendRaw("&aYou tipped Semx11 in Classic Games");
                    messageUtil
                            .sendRaw("&a+15 coins for you in Classic Games for being generous :)");
                    messageUtil.sendRaw("");
                    messageUtil.sendRaw("&9+50 experience (Gave a player a /tip)");
                    messageUtil.sendRaw("&aYou tipped 2Pi in Arcade Games");
                    messageUtil
                            .sendRaw("&a+38 coins for you in Arcade Games for being generous :)");
                    messageUtil.sendRaw("");
                    //new
                    messageUtil.sendRaw("&aYou tipped Semx11 in Classic Games!");
                    messageUtil.sendRaw("&3+50 Hypixel Experience");
                    messageUtil.sendRaw("&6+15 VampireZ Coins");
                    messageUtil.sendRaw("&6+15 Quakecraft Coins");
                    messageUtil.sendRaw("&6+15 Paintball Coins");
                    messageUtil.sendRaw("&6+15 Turbo Kart Racers Coins");
                    messageUtil.sendRaw("&6+15 Arena Brawl Coins");
                    messageUtil.sendRaw("&6+15 Walls Coins");
                    messageUtil.sendRaw("");
                    messageUtil.sendRaw("&aYou tipped 2Pi in Arcade Games!");
                    messageUtil.sendRaw("&3+50 Hypixel Experience");
                    messageUtil.sendRaw("&6+38 Arcade Games Coins");
                    messageUtil.sendRaw("");
                    //old
                    messageUtil.sendRaw(
                            "&aYou earned &e600 coins &aand &9360 experience &afrom SkyWars tips in the last minute!");
                    messageUtil.sendRaw("");
                    messageUtil.sendRaw(
                            "&aYou earned &e100 coins &aand &90 experience &afrom VampireZ tips in the last minute!");
                    messageUtil.sendRaw(
                            "&aYou earned &e100 coins &aand &90 experience &afrom Quakecraft tips in the last minute!");
                    messageUtil.sendRaw(
                            "&aYou earned &e100 coins &aand &90 experience &afrom Paintball tips in the last minute!");
                    messageUtil.sendRaw(
                            "&aYou earned &e100 coins &aand &90 experience &afrom Turbo Kart Racers tips in the last minute!");
                    messageUtil.sendRaw(
                            "&aYou earned &e100 coins &aand &90 experience &afrom Arena Brawl tips in the last minute!");
                    messageUtil.sendRaw(
                            "&aYou earned &e100 coins &aand &960 experience &afrom Walls tips in the last minute!");
                    messageUtil.sendRaw("");
                    //new
                    messageUtil.sendRaw("&aYou were tipped by 6 players in the last minute!");
                    messageUtil.sendRaw("&3+360 Hypixel Experience");
                    messageUtil.sendRaw("&6+600 SkyWars Coins");
                    messageUtil.sendRaw("");
                    messageUtil.sendRaw("&aYou were tipped by 1 player in the last minute!");
                    messageUtil.sendRaw("&3+60 Hypixel Experience");
                    messageUtil.sendRaw("&6+100 VampireZ Coins");
                    messageUtil.sendRaw("&6+100 Quakecraft Coins");
                    messageUtil.sendRaw("&6+100 Paintball Coins");
                    messageUtil.sendRaw("&6+100 Turbo Kart Racers Coins");
                    messageUtil.sendRaw("&6+100 Arena Brawl Coins");
                    messageUtil.sendRaw("&6+100 Walls Coins");
                    messageUtil.sendRaw("");
                    //old
                    messageUtil.sendRaw("&aYou tipped 17 players! You got the following rewards:");
                    messageUtil.sendRaw("&3+600 Hypixel Experience");
                    messageUtil.sendRaw("&6+15 Speed UHC Coins");
                    messageUtil.sendRaw("&6+15 UHC Champions Coins");
                    messageUtil.sendRaw("&6+15 Arena Brawl Coins");
                    messageUtil.sendRaw("&6+15 The Walls Coins");
                    messageUtil.sendRaw("&6+15 Blitz SG Coins");
                    messageUtil.sendRaw("&6+15 Warlords Coins");
                    messageUtil.sendRaw("&6+15 Turbo Kart Racers Coins");
                    messageUtil.sendRaw("&6+15 VampireZ Coins");
                    messageUtil.sendRaw("&6+15 The TNT Games Coins");
                    messageUtil.sendRaw("&6+15 Cops and Crims Coins");
                    messageUtil.sendRaw("&6+15 Paintball Warfare Coins");
                    messageUtil.sendRaw("&6+38 Arcade Games Coins");
                    messageUtil.sendRaw("&6+15 Mega Walls Coins");
                    messageUtil.sendRaw("&6+15 SkyClash Coins");
                    messageUtil.sendRaw("&6+15 Crazy Walls Coins");
                    messageUtil.sendRaw("&6+15 SkyWars Coins");
                    messageUtil.sendRaw("&6+15 Quakecraft Coins");
                    messageUtil.sendRaw("");
                    //new
                    messageUtil.sendRaw("&aYou tipped 17 players in 12 different games!");
                    messageUtil.sendRaw("&3+600 Hypixel Experience");
                    messageUtil.sendRaw("&6+15 Speed UHC Coins");
                    messageUtil.sendRaw("&6+15 UHC Champions Coins");
                    messageUtil.sendRaw("&6+15 Arena Brawl Coins");
                    messageUtil.sendRaw("&6+15 The Walls Coins");
                    messageUtil.sendRaw("&6+15 Blitz SG Coins");
                    messageUtil.sendRaw("&6+15 Warlords Coins");
                    messageUtil.sendRaw("&6+15 Turbo Kart Racers Coins");
                    messageUtil.sendRaw("&6+15 VampireZ Coins");
                    messageUtil.sendRaw("&6+15 The TNT Games Coins");
                    messageUtil.sendRaw("&6+15 Cops and Crims Coins");
                    messageUtil.sendRaw("&6+15 Paintball Warfare Coins");
                    messageUtil.sendRaw("&6+38 Arcade Games Coins");
                    messageUtil.sendRaw("&6+15 Mega Walls Coins");
                    messageUtil.sendRaw("&6+15 SkyClash Coins");
                    messageUtil.sendRaw("&6+15 Crazy Walls Coins");
                    messageUtil.sendRaw("&6+15 SkyWars Coins");
                    messageUtil.sendRaw("&6+15 Quakecraft Coins");
                    messageUtil.sendRaw("");
                    break;
                default:
                    messageUtil.send("&cUsage: " + getCommandUsage(sender));
                    break;
            }
        } else {
            messageUtil.send("&cUsage: " + getCommandUsage(sender));
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
