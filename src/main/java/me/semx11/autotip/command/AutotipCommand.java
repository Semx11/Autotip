package me.semx11.autotip.command;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import me.semx11.autotip.Autotip;
import me.semx11.autotip.core.SessionManager;
import me.semx11.autotip.core.TaskManager;
import me.semx11.autotip.core.TaskManager.TaskType;
import me.semx11.autotip.event.EventClientConnection;
import me.semx11.autotip.misc.Stats;
import me.semx11.autotip.misc.TipTracker;
import me.semx11.autotip.util.FileUtil;
import me.semx11.autotip.util.MessageUtil;
import me.semx11.autotip.util.MinecraftVersion;
import me.semx11.autotip.util.UniversalUtil;
import me.semx11.autotip.util.Versions;
import net.minecraft.command.ICommandSender;

public class AutotipCommand extends AUniversalCommand {

    private static final DateTimeFormatter SESSION_FORMATTER = DateTimeFormatter
            .ofPattern("HH:mm:ss");
    private static final DateTimeFormatter WAVE_FORMATTER = DateTimeFormatter.ofPattern("mm:ss");

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
        if (!Autotip.MC_VERSION.equals(MinecraftVersion.V1_8)) {
            return Collections.singletonList("at");
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void onCommand(ICommandSender sender, String[] args) {

        SessionManager manager = Autotip.SESSION_MANAGER;

        if (args.length > 0) {
            switch (args[0].toLowerCase()) {
                case "m":
                case "messages":
                    Autotip.messageOption = Autotip.messageOption.next();
                    MessageUtil.send("Tip Messages: " + Autotip.messageOption);
                    break;
                case "?":
                case "info":
                    MessageUtil.separator();
                    MessageUtil
                            .send("&6&lAutotip v" + Autotip.VERSION + " &7by &bSemx11");
                    MessageUtil.send("Brought to you by the &6Autotip Team &7(hover)",
                            null,
                            "&6The Autotip Team\n"
                                    + "&b[MVP&4+&b] Semx11 &r&7- Lead Mod developer, API, PR\n"
                                    + "&b[MVP&3+&b] 2Pi &r&7- Lead API developer, Founder, Creator, PR\n"
                                    + "&6[YT] Sk1er &r&7- Host, not the creator, PR");
                    MessageUtil.send("Autotipper: " + (Autotip.toggle ? "&aEn" : "&cDis") +
                            "abled");
                    MessageUtil.send("Tip Messages: " + Autotip.messageOption);
                    MessageUtil.send("Tips sent today: &6" + TipTracker.tipsSent);
                    MessageUtil.send("Tips received today: &6" + TipTracker.tipsReceived);
                    MessageUtil
                            .send("Lifetime tips sent: &6" + Autotip.totalTipsSent);
                    MessageUtil.send("&6Type /autotip stats to see what has been earned.");
                    MessageUtil.separator();
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
                                Stats.printStats(FileUtil.getDate());
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
                                Stats.printBetween("25-06-2016", FileUtil.getDate());
                                break;
                            default:
                                MessageUtil.send("&cUsage: /autotip stats <day, week, month, year,"
                                        + " lifetime>");
                                break;
                        }
                    } else {
                        Stats.printStats(FileUtil.getDate());
                    }
                    break;
                case "t":
                case "toggle":
                    Autotip.toggle = !Autotip.toggle;
                    if (Autotip.toggle) {
                        if (manager.isOnHypixel() && !manager.isLoggedIn()) {
                            TaskManager.executeTask(TaskType.LOGIN, manager::login);
                        }
                    } else {
                        TaskManager.executeTask(TaskType.LOGOUT, manager::logout);
                    }
                    MessageUtil.send("Autotip: " + (Autotip.toggle ? "&aEn" : "&cDis") + "abled");
                    break;
                case "wave":
                case "time":
                    if (Autotip.toggle) {
                        if (manager.isOnHypixel()) {
                            if (manager.getNextTipWave() == 0) {
                                MessageUtil.send("The first tip wave hasn't occurred yet.");
                                return;
                            }
                            MessageUtil.separator();
                            long time = System.currentTimeMillis();
                            MessageUtil.send("Last wave: &6" + LocalTime.MIN
                                    .plusSeconds((time - manager.getLastTipWave()) / 1000)
                                    .format(WAVE_FORMATTER));
                            MessageUtil.send("Next wave: &6" + LocalTime.MIN
                                    .plusSeconds((manager.getNextTipWave() - time) / 1000 + 1)
                                    .format(WAVE_FORMATTER));
                            MessageUtil.separator();
                        } else {
                            MessageUtil.send("Autotip is disabled as you are not playing "
                                    + "on Hypixel.");
                        }
                    } else {
                        MessageUtil.send("Autotip is disabled. Use &6/autotip toggle&7 to "
                                + "enable it.");
                    }
                    break;
                case "whatsnew":
                    MessageUtil.separator();
                    MessageUtil.send("&6What's new in Autotip v" + Autotip.VERSION
                            + ":");
                    Versions.getInstance().getInfoByVersion(Autotip.VERSION).getChangelog().forEach(
                            s -> MessageUtil.send("&8- &7" + s));
                    MessageUtil.separator();
                    break;
                case "debug":
                    MessageUtil.separator();
                    MessageUtil.send("Last IP joined: " + EventClientConnection.getServerIp());
                    MessageUtil.send("Detected MC version: " + Autotip.MC_VERSION);
                    Object header = EventClientConnection.getHeader();
                    MessageUtil.send("Tablist Header: " + (header == null ? "No header."
                            : UniversalUtil.getUnformattedText(header)));
                    MessageUtil.separator();
                    break;
                default:
                    MessageUtil.send("&cUsage: " + getCommandUsage(sender));
                    break;
            }
        } else {
            MessageUtil.send("&cUsage: " + getCommandUsage(sender));
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
